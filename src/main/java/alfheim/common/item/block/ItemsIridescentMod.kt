package alfheim.common.item.block

// PORT: импорты 1.20.1; ItemBlockWithMetadata 1.7.10 — alfheim.port.legacy.ItemBlockWithMetadata
import alexsocol.asjlib.meta
import alfheim.api.ModInfo
import alfheim.port.legacy.*
import net.minecraft.network.chat.Component
import net.minecraft.world.item.*
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.Block

// PORT: ключ имени предмета-блока — ключ блока; старый ключ без номера (tile.alfheim:irisWood.name) переименован по
// legacy_ids.json во все ключи блоков этого имени (alfheim.port.data.LegacyIdsProvider). Поставленная игроком листва не
// опадает (getMetadata ниже) — свойство persistent: его ставит листва 1.20.1 при установке (Leaves1710)
open class ItemIridescentBlockMod(par2Block: Block): ItemBlockWithMetadata(par2Block, par2Block) {
	
	/*
	override fun getMetadata(meta: Int): Int { // used in bifrost leaves
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
	
	// PORT: addInformation → appendHoverText; field_150939_a — block
	override fun appendHoverText(par1ItemStack: ItemStack?, par2World: World?, par3List: MutableList<Component>, par4: TooltipFlag) {
		if (par1ItemStack == null) return
		val meta = "\\d+$".toRegex().find(block.unlocalizedName)
		addStringToTooltip("&7" + StatCollector.translateToLocal("misc.${ModInfo.MODID}.color." + (meta?.value ?: "16")) + "&r", par3List)
	}
}

class ItemIridescentWoodMod(par2Block: Block): ItemIridescentBlockMod(par2Block) {
	
	// PORT: addInformation → appendHoverText; field_150939_a — block
	override fun appendHoverText(par1ItemStack: ItemStack?, par2World: World?, par3List: MutableList<Component>, par4: TooltipFlag) {
		if (par1ItemStack == null) return
		val metaMatch = "\\d+$".toRegex().find(block.unlocalizedName)
		val meta = if (metaMatch == null) 16 else metaMatch.value.toInt() * 4 + par1ItemStack.meta
		addStringToTooltip("&7" + StatCollector.translateToLocal("misc.${ModInfo.MODID}.color.$meta") + "&r", par3List)
	}
}

class ItemIridescentLeavesMod(par2Block: Block): ItemIridescentBlockMod(par2Block) {
	
	// PORT: поставленная игроком листва не опадает — свойство persistent (Leaves1710)
	/*
	override fun getMetadata(meta: Int): Int { // used for colored leaves... only. But let the check just be there
		if (field_150939_a is BlockLeavesMod) return meta or (field_150939_a as BlockLeavesMod).decayBit()
		return meta
	}
	*/
	
	// PORT: addInformation → appendHoverText; field_150939_a — block
	override fun appendHoverText(par1ItemStack: ItemStack?, par2World: World?, par3List: MutableList<Component>, par4: TooltipFlag) {
		if (par1ItemStack == null) return
		val metaMatch = "\\d+$".toRegex().find(block.unlocalizedName)
		val meta = if (metaMatch == null) 16 else metaMatch.value.toInt() * 8 + par1ItemStack.meta % 8
		addStringToTooltip("&7" + StatCollector.translateToLocal("misc.${ModInfo.MODID}.color.$meta") + "&r", par3List)
	}
}
