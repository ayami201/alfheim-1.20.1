package alfheim.common.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.entity.IAncientWolf
import alfheim.common.core.util.DamageSourceSpell
import net.minecraft.entity.*
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World

class EntityFenrirSlash(world: World): Entity(world) {
	
	var attacker: EntityLivingBase? = null
	
	var yaw
		get() = dataWatcher.getWatchableObjectFloat(2)
		set(value) = dataWatcher.updateObject(2, value)
	
	var roll
		get() = dataWatcher.getWatchableObjectFloat(3)
		set(value) = dataWatcher.updateObject(3, value)
	
	init {
		setSize(2f, 4f)
	}
	
	override fun entityInit() {
		dataWatcher.addObject(2, 0f)
		dataWatcher.addObject(3, 0f)
	}
	
	override fun onEntityUpdate() {
		super.onEntityUpdate()
		
		if (ticksExisted > 100) return setDead()
		
		getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox()).forEach {
			if (it is IAncientWolf) return@forEach
			if (it.attackEntityFrom(DamageSourceSpell.windblade(this, attacker ?: this).setDamageBypassesArmor(), 5f)) setDead()
		}
		
		val (mx, my, mz) = Vector3.oZ.copy().rotateOY(yaw)
		moveEntity(mx, my, mz)
		setSize(1f, height + 0.25f)
		
		if (isCollidedHorizontally) return setDead()
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound?) = Unit
	override fun writeEntityToNBT(nbt: NBTTagCompound?) = Unit
}
