package alfheim.common.item.block

// PORT: импорты 1.20.1; ItemBlockWithMetadata 1.7.10 — alfheim.port.legacy.ItemBlockWithMetadata
import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.common.block.colored.BlockColoredLeaves
import alfheim.port.legacy.*
import net.minecraft.network.chat.Component
import net.minecraft.world.item.*
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.Block

// PORT: ключ имени предмета-блока — ключ блока; старый ключ без номера (tile.alfheim:irisPlanks.name) переименован по
// legacy_ids.json во все ключи блоков этого имени (alfheim.port.data.LegacyIdsProvider). Поставленная игроком листва не
// опадает (getMetadata ниже) — свойство persistent: его ставит листва 1.20.1 при установке (Leaves1710)
open class ItemSubtypedBlockMod(block: Block): ItemBlockWithMetadata(block, block) {
	
	/*
	override fun getMetadata(meta: Int): Int { // used for base leaves
		if (field_150939_a is BlockLeavesMod) return meta or (field_150939_a as BlockLeavesMod).decayBit()
		return meta
	}
	
	override fun getUnlocalizedNameInefficiently(par1ItemStack: ItemStack) =
		super.getUnlocalizedNameInefficiently(par1ItemStack).replace("tile.", "tile.${ModInfo.MODID}:").replace("\\d+$".toRegex(), "")
	*/
	
	// PORT: строка подсказки 1.20.1 — Component
	fun addStringToTooltip(s: String, tooltip: MutableList<Component>?) {
		tooltip!!.add(Component.literal(s.replace("&".toRegex(), "\u00a7")))
	}
	
	// PORT: addInformation → appendHoverText
	override fun appendHoverText(stack: ItemStack?, world: World?, list: MutableList<Component>, par4: TooltipFlag) {
		if (stack?.block !is BlockColoredLeaves) return
		addStringToTooltip("&7" + StatCollector.translateToLocal("misc.${ModInfo.MODID}.color." + stack.meta) + "&r", list)
	}
}

// PORT: ключ имени — ключ блока; старый ключ с номером варианта (getUnlocalizedNameInefficiently ниже) переименован по
// legacy_ids.json (alfheim.port.data.LegacyIdsProvider). Поставленная листва не опадает — свойство persistent (Leaves1710)
@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN") // fucking reflection
class ItemUniqueSubtypedBlockMod(block: Block, val subtypes: Integer): ItemBlockWithMetadata(block, block) {

	/*
	override fun getMetadata(meta: Int): Int { // used for Alt leaves
		if (field_150939_a is BlockLeavesMod) return meta or (field_150939_a as BlockLeavesMod).decayBit()
		return meta
	}
	
	override fun getUnlocalizedNameInefficiently(stack: ItemStack) =
		super.getUnlocalizedNameInefficiently(stack).replace("tile.", "tile.${ModInfo.MODID}:") + stack.meta % subtypes.toInt()
	*/
}
