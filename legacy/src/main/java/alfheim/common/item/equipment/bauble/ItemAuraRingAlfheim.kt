package alfheim.common.item.equipment.bauble

import alfheim.common.core.util.AlfheimTab
import baubles.api.BaubleType
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import vazkii.botania.api.item.IPixieSpawner
import vazkii.botania.api.mana.*
import vazkii.botania.common.item.equipment.bauble.ItemBauble

class ItemAuraRingAlfheim(name: String, val delay: Int = 5, val pixieChance: Float = 0f): ItemBauble(name), IManaItem, IManaGivingItem, IPixieSpawner {
	
	init {
		creativeTab = AlfheimTab
	}
	
	override fun onWornTick(stack: ItemStack, player: EntityLivingBase) {
		super.onWornTick(stack, player)
		if (player is EntityPlayer && player.ticksExisted % delay == 0)
			if (!ManaItemHandler.dispatchManaExact(stack, player, 10, true))
				ManaItemHandler.dispatchMana(stack, player, 10, true)
	}
	
	override fun getBaubleType(itemstack: ItemStack) = BaubleType.RING
	
	override fun getPixieChance(stack: ItemStack?) = pixieChance
	
	override fun getMana(stack: ItemStack) = 10
	
	override fun getMaxMana(stack: ItemStack?) = 0
	
	override fun addMana(stack: ItemStack?, mana: Int) = Unit
	
	override fun canReceiveManaFromPool(stack: ItemStack?, pool: TileEntity?) = false
	
	override fun canReceiveManaFromItem(stack: ItemStack?, otherStack: ItemStack?) = false
	
	override fun canExportManaToPool(stack: ItemStack?, pool: TileEntity) = pool.worldObj.totalWorldTime % delay == 0L
	
	override fun canExportManaToItem(stack: ItemStack?, otherStack: ItemStack?) = true
	
	override fun isNoExport(stack: ItemStack?) = true
}
