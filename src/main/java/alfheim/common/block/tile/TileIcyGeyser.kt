package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.ASJTile
import alexsocol.asjlib.math.Vector3
import alfheim.AlfheimCore
import alfheim.api.entity.INiflheimEntity
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.handler.SheerColdHandler.cold
import net.minecraft.client.particle.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.*
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.potion.Potion
import kotlin.math.pow

class TileIcyGeyser: ASJTile() {
	
	var maxCD = 0
	var cooldown = 0
	var timer = 0
	
	override fun updateEntity() {
		if (timer > 0) {
			if (worldObj.isRemote) run {
				if (Vector3.entityTileDistance(mc.renderViewEntity, this) > 64) return@run
				
				val v = Vector3()
				repeat(40) {
					if (!AlfheimCore.proxy.doParticle()) return@repeat
					
					val (x, y, z) = v.rand().sub(0.5, 0, 0.5).normalize().mul(Math.random() * 0.5 - 0.25, 1.25, Math.random() * 0.5 - 0.25)
					
					val type = worldObj.rand.nextInt(16)
					
					val particle = when (type) {
						in 0..6 -> EntityDiggingFX(worldObj, xCoord + 0.5 + Math.random() * 1.25 - 0.625, yCoord + 1.1, zCoord + 0.5 + Math.random() * 1.25 - 0.625, x, y, z, Blocks.packed_ice, 0)
						in 7..14 -> EntityBlockDustFX(worldObj, xCoord + 0.5, yCoord + 1.25, zCoord + 0.5, x, y, z, Blocks.water, 0)
						else -> EntityExplodeFX(worldObj, xCoord + 0.5, yCoord + 1.25, zCoord + 0.5, x, y, z)
					}
					
					mc.effectRenderer.addEffect(particle)
				}
			} else {
				getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox().expand(1, 6, 1).offset(0, 7, 0)).forEach {
					if (it is EntityPlayer && it.capabilities.isCreativeMode) return@forEach
					
					val (x, y, z) = Vector3.fromEntity(it).sub(Vector3.fromTileEntityCenter(this)).normalize().mul(0.1)
					
					it.motionX += x + Math.random() * 0.1 - 0.05
					it.motionY += y
					it.motionZ += z + Math.random() * 0.1 - 0.05
					
					if (it is EntityPlayerMP)
						it.playerNetServerHandler.sendPacket(S12PacketEntityVelocity(it))
					
					if (INiflheimEntity.checkProtection(it, 10)) return@forEach
					
					it.cold = 100f
					it.addPotionEffect(PotionEffectU(Potion.moveSlowdown.id, 100, 4))
					it.addPotionEffect(PotionEffectU(Potion.digSlowdown.id, 100, 4))
					it.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDIceLens, 100))
				}
			}
		}
		
		timer--
		cooldown--
		
		if (cooldown == 900 || cooldown == 450 || cooldown == 0)
			worldObj.playAuxSFX(2001, xCoord, yCoord, zCoord, Blocks.packed_ice.id)
		
		if (cooldown > 0) return
		
		if (worldObj.isRemote) return
		
		timer = ASJUtilities.randInBounds(120, 360, worldObj.rand)
		
		maxCD = ASJUtilities.randInBounds(1200, 3600, worldObj.rand)
		cooldown = maxCD + timer
		
		ASJUtilities.dispatchTEToNearbyPlayers(this)
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		maxCD = nbt.getInteger(TAG_COOLDOWN_MAX)
		cooldown = nbt.getInteger(TAG_COOLDOWN)
		timer = nbt.getInteger(TAG_TIMER)
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		nbt.setInteger(TAG_COOLDOWN_MAX, maxCD)
		nbt.setInteger(TAG_COOLDOWN, cooldown)
		nbt.setInteger(TAG_TIMER, timer)
	}
	
	override fun getRenderBoundingBox() = boundingBox(1)
	
	override fun getMaxRenderDistanceSquared() = (mc.gameSettings.renderDistanceChunks * 16.0).pow(2)
	
	companion object {
		const val TAG_COOLDOWN_MAX = "cooldownMax"
		const val TAG_COOLDOWN = "cooldown"
		const val TAG_TIMER = "timer"
	}
}
