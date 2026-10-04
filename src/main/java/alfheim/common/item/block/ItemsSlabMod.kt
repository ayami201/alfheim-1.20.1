package alfheim.common.item.block

// PORT: импорты 1.20.1; ItemBlock 1.7.10 — alfheim.port.legacy.ItemBlock
import alfheim.api.ModInfo
import alfheim.port.legacy.*
import net.minecraft.network.chat.Component
import net.minecraft.world.item.*
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.Block

open class ItemColoredSlabMod(par1: Block): ItemSlabMod(par1) {
	
	// PORT: строка подсказки 1.20.1 — Component
	fun addStringToTooltip(s: String, tooltip: MutableList<Component>?) {
		tooltip!!.add(Component.literal(s.replace("&".toRegex(), "\u00a7")))
	}
	
	// PORT: addInformation → appendHoverText; field_150939_a — block
	override fun appendHoverText(stack: ItemStack?, world: World?, list: MutableList<Component>, advanced: TooltipFlag) {
		if (stack == null) return
		val meta = "\\d+$".toRegex().find(block.unlocalizedName)
		addStringToTooltip("&7" + StatCollector.translateToLocal("misc.${ModInfo.MODID}.color." + (meta?.value ?: "16")) + "&r", list)
	}
}

// PORT: ItemSlab 1.7.10 (одинарная плита на одинарную — двойная) → обычный предмет-блок: в 1.20.1 плиты складывает в
// двойную сама плита (SlabBlock). Блок предмета — свойство block 1.20.1. Ключ имени — ключ блока; старый ключ без номера
// (getUnlocalizedName ниже) переименован по legacy_ids.json (alfheim.port.data.LegacyIdsProvider)
open class ItemSlabMod(block: Block): ItemBlock(block) {
//open class ItemSlabMod(val block: Block): ItemSlab(block, (block as BlockSlabMod).getSingleBlock(), block.getFullBlock(), false) {
	
	/*
	override fun getUnlocalizedName(stack: ItemStack) =
		field_150939_a.unlocalizedName.replace("tile.".toRegex(), "tile.${ModInfo.MODID}:").replace("\\d+$".toRegex(), "")
	*/
}

/* PORT: КТ-2, партия 8б — мерцающий кварц (BlockShimmerQuartzSlab)
open class ItemShimmerSlabMod(val block: Block): ItemSlab(block, (block as BlockShimmerQuartzSlab).singleBlock, block.fullBlock, false) {
	
	override fun getUnlocalizedName(stack: ItemStack) =
		field_150939_a.unlocalizedName.replace("tile.".toRegex(), "tile.${ModInfo.MODID}:").replace("\\d+$".toRegex(), "")
}
*/

// PORT: ItemSlab 1.7.10 → обычный предмет-блок (см. ItemSlabMod). Ключ имени — ключ блока; старый ключ с номером
// варианта (getUnlocalizedName ниже) переименован по legacy_ids.json (alfheim.port.data.LegacyIdsProvider)
open class ItemMetaSlabMod(block: Block): ItemBlock(block) {
//open class ItemMetaSlabMod(val block: Block): ItemSlab(block, (block as BlockSlabMod).getSingleBlock(), block.getFullBlock(), false) {
	
	/*
	override fun getUnlocalizedName(stack: ItemStack) =
		"${field_150939_a.unlocalizedName.replace("tile.".toRegex(), "tile.${ModInfo.MODID}:")}${stack.meta and 0x8.inv()}"
	*/
}