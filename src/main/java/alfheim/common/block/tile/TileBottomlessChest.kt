package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.ASJTile
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraftforge.common.util.ForgeDirection

class TileBottomlessChest: ASJTile(), IInventory {
	
	var redstone = false
	var canEject = false
	
	override fun updateEntity() {
		canEject = canEject()
		redstone = ForgeDirection.VALID_DIRECTIONS.any { worldObj.getIndirectPowerLevelTo(xCoord + it.offsetX, yCoord + it.offsetY, zCoord + it.offsetZ, it.ordinal) > 0 }
	}
	
	fun canEject(): Boolean {
		val below = worldObj.getBlock(xCoord, yCoord - 1, zCoord)
		return below.isAir(worldObj, xCoord, yCoord - 1, zCoord) || below.getCollisionBoundingBoxFromPool(worldObj, xCoord, yCoord - 1, zCoord) == null
	}
	
	fun eject(stack: ItemStack) {
		EntityItem(worldObj, xCoord + 0.5, yCoord - 0.5, zCoord + 0.5, stack).apply {
			setMotion(0.0,0.0,0.0)
			if (redstone) age = -200
			if (!worldObj.isRemote) spawn()
		}
	}
	
	override fun setInventorySlotContents(slot: Int, stack: ItemStack?) {
		eject(stack ?: return)
	}
	
	override fun isItemValidForSlot(slot: Int, stack: ItemStack?) = canEject
	override fun getSizeInventory() = if (canEject) 1024 else 0
	override fun getStackInSlot(slot: Int) = null
	override fun decrStackSize(slot: Int, amount: Int) = null
	override fun getStackInSlotOnClosing(slot: Int) = null
	override fun getInventoryName() = "BottomlessChest"
	override fun hasCustomInventoryName() = false
	override fun getInventoryStackLimit() = if (canEject) 1024 else 0
	override fun isUseableByPlayer(player: EntityPlayer?) = false
	override fun openInventory() = Unit
	override fun closeInventory() = Unit
}
