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
	
	fun mount(player: EntityPlayer, offset: Double): Boolean {
		if (!worldObj.isRemote) {
			if (worldObj.getEntitiesWithinAABB(EntitySit::class.java, boundingBox().offset(0, 0.5, 0)).isNotEmpty()) return false
			
			player.mountEntity(null)
			
			val sit = EntitySit(worldObj)
			sit.setPosition(xCoord + 0.5, yCoord + offset, zCoord + 0.5)
			player.mountEntity(sit)
			sit.spawn(worldObj)
			player.mountEntity(sit)
		}
		
		return true
	}
	
	companion object {
		
		class EntitySit(world: World): Entity(world) {
			
			init {
				setSize(0f, 0f)
			}
			
			override fun onEntityUpdate() {
				if (!worldObj.isRemote)
					if (riddenByEntity == null || worldObj.getBlock(this) !== AlfheimFluffBlocks.chair) setDead()
			}
			
			@SideOnly(Side.CLIENT)
			override fun setPositionAndRotation2(x: Double, y: Double, z: Double, yaw: Float, pitch: Float, nope: Int) {
				setPosition(x, y, z)
				setRotation(yaw, pitch)
				// fuck you "push out of blocks"!
			}
			
			override fun attackEntityFrom(src: DamageSource?, amount: Float) = false
			override fun entityInit() = Unit
			override fun readEntityFromNBT(nbt: NBTTagCompound?) = Unit
			override fun writeEntityToNBT(nbt: NBTTagCompound?) = Unit
		}
	}
}
