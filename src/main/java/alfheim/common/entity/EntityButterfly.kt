package alfheim.common.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.entity.IAlfheimMob
import alfheim.common.item.material.*
import alfheim.common.world.dim.alfheim.biome.*
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.*
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraft.world.World
import ru.vamig.worldengine.*
import vazkii.botania.client.core.handler.ClientTickHandler
import vazkii.botania.common.Botania
import vazkii.botania.common.entity.EntityFlyingCreature
import vazkii.botania.common.item.ModItems
import java.awt.Color
import kotlin.math.*

class EntityButterfly(world: World): EntityFlyingCreature(world), IAlfheimMob {
	
	/** Coordinates of where the pixie spawned.  */
	private var spawnPosition: ChunkCoordinates? = null
	
	var isGiant
		get() = getFlag(6)
		set(value) = setFlag(6, value)
	
	var resized
		get() = getFlag(7)
		set(value) = setFlag(7, value)
	
	init {
		setSize(0.25f, 0.25f)
	}
	
	override fun applyEntityAttributes() {
		super.applyEntityAttributes()
		getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = 2.0
	}
	
	override fun canBePushed() = false
	override fun collideWithEntity(entity: Entity) = Unit
	override fun collideWithNearbyEntities() = Unit
	override fun isAIEnabled(): Boolean = true
	override fun canTriggerWalking() = false
	override fun doesEntityNotTriggerPressurePlate() = true
	
	override fun getDropItem() = null
	
	override fun dropFewItems(hit: Boolean, looting: Int) {
		val count = max(1, looting) * if (isGiant) ASJUtilities.randInBounds(7, 15, rng) else 1
		
		for (i in 0 until count) {
			if (rng.nextBoolean())
				entityDropItem(ElvenFoodMetas.Nectar.stack, 0f)
			else {
				val meta = ASJUtilities.randInBounds(0, 16, rng)
				val stack = if (meta == 16) ElvenResourcesMetas.RainbowDust.stack else ItemStack(ModItems.dye, 1, meta)
				entityDropItem(stack, 0f)
			}
		}
	}
	
	private val immuneTo = arrayOf(DamageSource.inWall.damageType, DamageSource.drown.damageType, DamageSource.fall.damageType)
	
	override fun attackEntityFrom(src: DamageSource, amount: Float): Boolean {
		if (src.damageType in immuneTo) return false
		
		return super.attackEntityFrom(src, amount)
	}
	
	override fun updateEntityActionState() {
		rotationYaw = -atan2(motionX, motionZ).F * 180f / Math.PI.F
		renderYawOffset = rotationYaw
	}
	
	override fun onEntityUpdate() {
		if (worldObj.isRemote) {
			for (i in 0 until if (isGiant) 10 else 1) {
				val color = Color(Color.HSBtoRGB((ClientTickHandler.ticksInGame * entityId) % 360 / 360f, 1f, 1f))
				Botania.proxy.sparkleFX(worldObj, posX + Math.random() * width - width / 2, posY + Math.random() * height - height / 2, posZ + Math.random() * width - width / 2, color.red.F, color.green.F, color.blue.F, 0.1f + Math.random().F * 0.25f * if (isGiant) 10 else 1, 12)
			}
		} else if (!resized)  {
			(worldObj.provider as? WE_WorldProvider)?.chunkProvider?.let {
				if (WE_Biome.getBiomeAt(it, posX.mfloor(), posZ.mfloor()).isEqualTo(BiomeIslandGiantFlowers)) {
					isGiant = true
					getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = 20.0
					health = maxHealth
				}
			}
			
			resized = true
		}
		
		if (isGiant)
			setSize(2.5f, 2.5f)
		
		motionY *= 0.6
		val (x, y, z) = Vector3.fromEntity(this).mf()
		if (y - worldObj.getTopSolidOrLiquidBlock(x, z) > if (isGiant) 30 else 15) motionY -= 0.1f
		if (worldObj.rand.nextInt(600) == 0) motionY -= 5.0
		
		super.onEntityUpdate()
	}
	
	override fun updateAITasks() {
		//super.updateAITasks();
		if (spawnPosition != null && (!worldObj.isAirBlock(spawnPosition!!.posX, spawnPosition!!.posY, spawnPosition!!.posZ) || spawnPosition!!.posY < 1)) {
			spawnPosition = null
		}
		
		if (spawnPosition == null || rand.nextInt(30) == 0 || spawnPosition!!.getDistanceSquared(posX.I, posY.I, posZ.I) < 4f) {
			spawnPosition = ChunkCoordinates(posX.I + rand.nextInt(7) - rand.nextInt(7), posY.I + rand.nextInt(6) - 2, posZ.I + rand.nextInt(7) - rand.nextInt(7))
		}
		
		val d0 = spawnPosition!!.posX.D + 0.5 - posX
		val d1 = spawnPosition!!.posY.D + 0.1 - posY
		val d2 = spawnPosition!!.posZ.D + 0.5 - posZ
		motionX += (sign(d0) * 0.5 - motionX) * 0.1
		motionY += (sign(d1) * 0.7 - motionY) * 0.1
		motionZ += (sign(d2) * 0.5 - motionZ) * 0.1
		val f = (atan2(motionZ, motionX) * 180.0 / Math.PI).F - 90f
		val f1 = MathHelper.wrapAngleTo180_float(f - rotationYaw)
		moveForward = 0.5f
		rotationYaw += f1
	}
	
	override fun setDead() {
		super.setDead()
		if (worldObj.isRemote)
			for (i in 0..11)
				Botania.proxy.sparkleFX(worldObj, posX + (Math.random() - 0.5) * 0.25, posY + 0.5 + (Math.random() - 0.5) * 0.25, posZ + (Math.random() - 0.5) * 0.25, 1f, 0.25f, 0.9f, 1f + Math.random().F * 0.25f, 5)
	}
	
	override fun getCanSpawnHere(): Boolean {
		setPosition(posX, posY + 5, posZ)
		val flagTime = (worldObj.worldTime % 24000L).I in ((0..13333) + (22666..23999))
		var flagBiome = false
		
		val chunk = (worldObj.provider as? WE_WorldProvider)?.chunkProvider
		if (chunk != null) {
			val biomeAt = WE_Biome.getBiomeAt(chunk, posX.mfloor(), posZ.mfloor())
			flagBiome = biomeAt.isEqualTo(BiomeField) || biomeAt.isEqualTo(BiomeIslandGiantFlowers)
		}
		
		return flagTime && flagBiome && posY > 64 && super.getCanSpawnHere()
	}
	
	@SideOnly(Side.CLIENT)
	override fun isInRangeToRenderDist(distance: Double): Boolean {
		return super.isInRangeToRenderDist(distance / 16.0)
	}
	
	override fun getPickedResult(target: MovingObjectPosition?) = super<IAlfheimMob>.getPickedResult(target)
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		super.writeEntityToNBT(nbt)
		
		nbt.setBoolean(TAG_GIANT, isGiant)
		nbt.setBoolean(TAG_RESIZED, resized)
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		super.readEntityFromNBT(nbt)
		
		isGiant = nbt.getBoolean(TAG_GIANT)
		resized = nbt.getBoolean(TAG_RESIZED)
	}
	
	companion object {
		const val TAG_GIANT = "giant"
		const val TAG_RESIZED = "resized"
	}
}
