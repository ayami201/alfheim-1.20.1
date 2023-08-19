package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.ASJTile
import alfheim.common.item.rod.RedstoneSignalHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ChunkCoordinates
import vazkii.botania.api.wand.IWandBindable

class TileRedstoneRelay: ASJTile(), IWandBindable {
	
	var boundTo = ChunkCoordinates(0, -1, 0)
	
	override fun updateEntity() {
		val (x, y, z) = boundTo
		
		if (worldObj.isRemote || y == -1) return
		
		val power = listOf(worldObj.getBlockPowerInput(xCoord, yCoord, zCoord), worldObj.getStrongestIndirectPower(xCoord, yCoord, zCoord)).max()
		if (power <= 0) return
		
		RedstoneSignalHandler.get().addSignal(worldObj, x, y, z, 1, power)
	}
	
	override fun getBinding() = if (boundTo.posY == -1) null else boundTo
	
	override fun canSelect(player: EntityPlayer?, wand: ItemStack?, x: Int, y: Int, z: Int, side: Int) = true
	
	override fun bindTo(player: EntityPlayer?, wand: ItemStack?, x: Int, y: Int, z: Int, side: Int): Boolean {
		boundTo = ChunkCoordinates(x, y, z)
		return true
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		val (x, y, z) = boundTo
		
		nbt.setInteger(TAG_X, x)
		nbt.setInteger(TAG_Y, y)
		nbt.setInteger(TAG_Z, z)
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		boundTo = ChunkCoordinates(nbt.getInteger(TAG_X), nbt.getInteger(TAG_Y), nbt.getInteger(TAG_Z))
	}
	
	companion object {
		const val TAG_X = "bindX"
		const val TAG_Y = "bindY"
		const val TAG_Z = "bindZ"
	}
}
