package alfheim.common.entity

import cpw.mods.fml.relauncher.*
import net.minecraft.entity.Entity
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World

class EntityWarBanner(world: World): Entity(world) {
	
	var type
		get() = getFlag(2)
		set(value) = setFlag(2, value)
	
	override fun onUpdate() {
		if (ticksExisted > 150) setDead()
	}
	
	override fun entityInit() {
		setSize(1.5f, 3.25f)
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		type = nbt.getBoolean(TAG_TYPE)
	}
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		nbt.setBoolean(TAG_TYPE, type)
	}
	
	@SideOnly(Side.CLIENT)
	override fun setPositionAndRotation2(x: Double, y: Double, z: Double, yaw: Float, pitch: Float, nope: Int) {
		setPosition(x, y, z)
		setRotation(yaw, pitch)
		// fuck you "push out of blocks"!
	}
	
	companion object {
		const val TAG_TYPE = "type"
	}
}
