package alfheim.common.block.tile

import alexsocol.asjlib.*
import alfheim.common.block.AlfheimFluffBlocks
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.DamageSource
import net.minecraft.world.World

class TileChair: TileDoubleCamo() {
	
	fun mount(player: EntityPlayer): Boolean {
		if (worldObj.getEntitiesWithinAABB(EntitySit::class.java, boundingBox().offset(0, 0.5, 0)).isNotEmpty()) return false
		
		if (!worldObj.isRemote)
			EntitySit(worldObj).apply {
				setPosition(xCoord + 0.5, yCoord + 0.65, zCoord + 0.5)
				spawn()
				player.mountEntity(this)
			}
		
		return true
	}
	
	companion object {
		
		class EntitySit(world: World): Entity(world) {
			
			override fun onEntityUpdate() {
				if (riddenByEntity == null || worldObj.getBlock(this) !== AlfheimFluffBlocks.chair) setDead()
			}
			
			@SideOnly(Side.CLIENT)
			override fun setPositionAndRotation2(x: Double, y: Double, z: Double, yaw: Float, pitch: Float, nope: Int) {
				setPosition(x, y, z)
				setRotation(yaw, pitch)
				// fuck you "push out of blocks"!
			}
			
			override fun attackEntityFrom(src: DamageSource?, amount: Float) = false
			override fun entityInit() = setSize(0f, 0f)
			override fun readEntityFromNBT(nbt: NBTTagCompound?) = Unit
			override fun writeEntityToNBT(nbt: NBTTagCompound?) = Unit
		}
	}
}
