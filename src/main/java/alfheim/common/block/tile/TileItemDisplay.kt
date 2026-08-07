package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.*
import alfheim.common.core.handler.*
import net.minecraft.entity.player.*
import net.minecraft.inventory.*
import net.minecraft.item.*
import java.util.*

class TileItemDisplay: TileItemContainer(), IInventory {
	
	override fun getStackInSlot(slot: Int) = item
	
	override fun decrStackSize(slot: Int, size: Int): ItemStack? {
		if (item == null || size != 1) return null
		
		try {
			return item
		} finally {
			item = null
		}
	}
	
	override fun getStackInSlotOnClosing(slot: Int) = null
	
	override fun setInventorySlotContents(slot: Int, stack: ItemStack?) {
		if (stack != null && stack.stackSize > inventoryStackLimit)
			stack.stackSize = inventoryStackLimit
		
		item = stack
	}
	
	override fun getInventoryName() = "container.itemDisplay"
	override fun isUseableByPlayer(player: EntityPlayer) = false
	override fun hasCustomInventoryName() = false
	override fun openInventory() = Unit
	override fun closeInventory() = Unit
	override fun getInventoryStackLimit() = 1
	override fun getSizeInventory() = 1
	override fun isItemValidForSlot(slot: Int, stack: ItemStack?) = true
	
	override fun canUpdate() = ASJUtilities.isServer
	
	override fun updateEntity() {
		if (worldObj?.provider?.dimensionId == AlfheimConfigHandler.dimensionIDDomains && canUpdate())
			displaysInDomainsList.add(this)
	}
	
	override fun invalidate() {
		super.invalidate()
		
		if (worldObj?.provider?.dimensionId == AlfheimConfigHandler.dimensionIDDomains && canUpdate())
			displaysInDomainsList.remove(this)
	}
	
	override fun validate() {
		super.validate()
		
		if (worldObj?.provider?.dimensionId == AlfheimConfigHandler.dimensionIDDomains && canUpdate())
			displaysInDomainsList.add(this)
	}
	
	override fun hashCode() = Objects.hash(xCoord, yCoord, zCoord)
	
	override fun equals(other: Any?) = if (other !is TileItemDisplay) false else other.xCoord == xCoord && other.yCoord == yCoord && other.zCoord == zCoord
	
	companion object {
		val displaysInDomainsList: MutableSet<TileItemDisplay> = Collections.newSetFromMap(WeakHashMap())
	}
}