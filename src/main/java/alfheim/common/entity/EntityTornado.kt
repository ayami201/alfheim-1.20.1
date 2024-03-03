package alfheim.common.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.entity.IAncientWolf
import alfheim.client.render.particle.EntityTornadoFX
import alfheim.common.core.util.DamageSourceSpell
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.world.World
import vazkii.botania.common.block.ModBlocks
import kotlin.math.min

class EntityTornado(world: World): Entity(world) {

	var attacker: EntityLivingBase? = null
	var target: EntityLivingBase? = null
	
	var delay
		get() = dataWatcher.getWatchableObjectInt(2)
		set(value) = dataWatcher.updateObject(2, value)
	
	init {
		setSize(0f, 0f)
	}
	
	override fun onEntityUpdate() {
		super.onEntityUpdate()
		
		val target = target
		
		if (worldObj.isRemote) {
			repeat(16) {
				val yPos = rand.nextDouble() * 3
				val fx = EntityTornadoFX(worldObj, posX, posY + yPos, posZ, rand.nextDouble() * yPos / 2 + 0.25, motionX, motionZ, ModBlocks.elfGlass, 0, 0, min(MAX_TICKS + delay, 10))
				fx.speed * 3
				mc.effectRenderer.addEffect(fx)
			}
			
			return
		}
		
		if (target?.isEntityAlive != true) return setDead()
		
		width = 1.5f
		height = 3f
		
		getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox()).forEach {
			if (it is IAncientWolf) return@forEach
			if (!it.attackEntityFrom(DamageSourceSpell.wind(this, attacker ?: this), 7f)) return@forEach
			
			if (it.rng.nextDouble() >= it.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).attributeValue) it.knockback(attacker ?: this, 1f)
			if (it is EntityPlayerMP) it.playerNetServerHandler.sendPacket(S12PacketEntityVelocity(it))
			return setDead()
		}
		
		width = 0f
		height = 0f
		
		if (delay-- > 0) return
		if (delay < -MAX_TICKS) return setDead()
		
		val (mx, my, mz) = Vector3.fromEntity(target).sub(this).normalize().mul(0.5)
		moveEntity(mx, my, mz)
	}
	
	override fun entityInit() {
		dataWatcher.addObject(2, 0)
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		delay = nbt.getInteger(TAG_DELAY)
	}
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		nbt.setInteger(TAG_DELAY, delay)
	}
	
	companion object {
		const val TAG_DELAY = "delay"
		const val MAX_TICKS = 100
	}
}
