package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.ASJTile
import alfheim.common.block.AlfheimBlocks
import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.AxisAlignedBB

class TileItemFrame: ASJTile() {
	
	val frames = arrayOfNulls<Frame>(6)
	
	fun removeFrame(i: Int, drop: Boolean): Boolean {
		val frame = frames[i]
		if (!worldObj.isRemote && drop) {
			EntityItem(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, ItemStack(AlfheimBlocks.itemFrame)).spawn()
			frame?.item?.let { EntityItem(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, it).spawn() }
		}
		
		frames[i] = null
		
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord)
		ASJUtilities.dispatchTEToNearbyPlayers(this)
		if (frames.any { it != null }) return false
		
		worldObj.setBlockToAir(xCoord, yCoord, zCoord)
		return true
	}
	
	override fun getRenderBoundingBox(): AxisAlignedBB {
		return AxisAlignedBB.getBoundingBox(xCoord.toDouble(), yCoord.toDouble(), zCoord.toDouble(), (xCoord + 1).toDouble(), (yCoord + 1).toDouble(), (zCoord + 1).toDouble())
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		for ((id, frame) in frames.withIndex()) {
			nbt.setBoolean("frame$id", frame != null)
			
			if (frame == null) continue
			
			val item = NBTTagCompound()
			frame.item?.writeToNBT(item)
			nbt.setTag("item$id", item)
			nbt.setInteger("rotation$id", frame.rotation)
		}
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		for (id in frames.indices) {
			val has = nbt.getBoolean("frame$id")
			if (!has) {
				frames[id] = null
				continue
			}
			
			frames[id] = Frame()
			
			val item = nbt.getCompoundTag("item$id")
			frames[id]!!.item = ItemStack.loadItemStackFromNBT(item)
			frames[id]!!.rotation = nbt.getInteger("rotation$id")
		}
	}
}

class Frame {
	var item: ItemStack? = null
	var rotation = 0
}