package alfheim.common.entity

import alfheim.common.entity.boss.EntityFlugel
import alfheim.common.spell.wind.SpellThor
import net.minecraft.entity.*
import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World

class EntityLightningMark: Entity {
	
	var lifespan
		get() = dataWatcher.getWatchableObjectInt(2)
		set(value) = dataWatcher.updateObject(2, value)
	
	constructor(world: World): super(world) {
		setSize(1.5f, 0.0001f)
		lifespan = 25
	}
	
	constructor(caster: EntityLivingBase, x: Double, y: Double, z: Double): this(caster.worldObj) {
		setPosition(x, y, z)
		if (caster !is EntityFlugel) lifespan = SpellThor.duration
	}
	
	override fun entityInit() {
		dataWatcher.addObject(2, 0)
	}
	
	override fun onEntityUpdate() {
		if (ticksExisted <= lifespan) return
		
		if (!worldObj.isRemote)
			worldObj.addWeatherEffect(EntityLightningBolt(worldObj, posX - 0.5, posY, posZ - 0.5))
		
		setDead()
	}
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		nbt.setInteger(TAG_LIFESPAN, lifespan)
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		lifespan = nbt.getInteger(TAG_LIFESPAN)
	}
	
	companion object {
		const val TAG_LIFESPAN = "lifespan"
	}
}