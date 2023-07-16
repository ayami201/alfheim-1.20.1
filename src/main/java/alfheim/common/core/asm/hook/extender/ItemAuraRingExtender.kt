package alfheim.common.core.asm.hook.extender

import alexsocol.asjlib.ASJReflectionHelper
import gloomyfolken.hooklib.asm.*
import net.minecraft.item.*
import net.minecraft.tileentity.TileEntity
import vazkii.botania.common.item.equipment.bauble.ItemAuraRing

@Suppress("UNUSED_PARAMETER", "unused")
object ItemAuraRingExtender {
	
	val getDelay by lazy {
		val m = ASJReflectionHelper.getMethod(ItemAuraRing::class.java, "getDelay", arrayOf())
		m?.isAccessible = true
		m
	}
	
	val delays = HashMap<Int, Int>()
	
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
	fun canExportManaToPool(ring: ItemAuraRing, stack: ItemStack?, pool: TileEntity) = pool.worldObj.totalWorldTime % delays.computeIfAbsent(Item.getIdFromItem(ring)) { getDelay?.invoke(ring) as? Int ?: 10 } == 0L
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun canExportManaToItem(ring: ItemAuraRing, stack: ItemStack?, otherStack: ItemStack?) = true
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun isNoExport(ring: ItemAuraRing, stack: ItemStack?) = true
}