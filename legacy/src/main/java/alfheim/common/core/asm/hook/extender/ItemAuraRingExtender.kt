package alfheim.common.core.asm.hook.extender

import gloomyfolken.hooklib.asm.*
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import vazkii.botania.common.item.equipment.bauble.ItemAuraRing

@Suppress("unused")
object ItemAuraRingExtender {
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun getMana(ring: ItemAuraRing, stack: ItemStack) = 1
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun getMaxMana(ring: ItemAuraRing, stack: ItemStack?) = 0
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun addMana(ring: ItemAuraRing, stack: ItemStack?, mana: Int) = Unit
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun canReceiveManaFromPool(ring: ItemAuraRing, stack: ItemStack?, pool: TileEntity?) = false
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun canReceiveManaFromItem(ring: ItemAuraRing, stack: ItemStack?, otherStack: ItemStack?) = false
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun canExportManaToPool(ring: ItemAuraRing, stack: ItemStack?, pool: TileEntity) = pool.worldObj.totalWorldTime % ring.delay == 0L
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun canExportManaToItem(ring: ItemAuraRing, stack: ItemStack?, otherStack: ItemStack?) = true
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun isNoExport(ring: ItemAuraRing, stack: ItemStack?) = true
}