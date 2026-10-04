package alfheim.port.data

import alfheim.api.ModInfo.MODID
import alfheim.api.lib.LibOreDict
import alfheim.common.block.AlfheimBlocks
import alfheim.common.item.AlfheimItems
import alfheim.port.legacy.*
import alfheim.port.registry.*
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.*
import net.minecraft.world.item.*
import net.minecraft.world.level.block.*
import net.minecraftforge.common.Tags
import net.minecraftforge.common.data.*
import java.util.concurrent.CompletableFuture

/**
 * Инструмент добычи 1.7.10 → теги 1.20.1 (MAPPING.md, «Блоки и предметы»).
 *
 * Чем блок добывается быстро: инструмент из `setHarvestLevel` или `getHarvestTool` блока и, как у кирки и топора
 * 1.7.10, ещё и по материалу — кирка быстра на камне, железе и наковальне, топор — на дереве и растениях. Уровень
 * добычи 1, 2, 3 — теги
 * «нужен каменный, железный, алмазный инструмент», 4 и выше — незеритовый. Без нужного инструмента блок
 * ничего не роняет, только если этого требует материал (`requiresCorrectToolForDrops`, как в 1.7.10).
 */
object HarvestTags {

	fun tools(block: LegacyBlock): Set<TagKey<Block>> {
		val tools = LinkedHashSet<TagKey<Block>>()
		when (val tool = block.getHarvestTool(block.variant ?: 0)) {
			"pickaxe" -> tools += BlockTags.MINEABLE_WITH_PICKAXE
			"axe"     -> tools += BlockTags.MINEABLE_WITH_AXE
			"shovel"  -> tools += BlockTags.MINEABLE_WITH_SHOVEL
			null      -> Unit
			else      -> throw IllegalStateException("Unknown 1.7.10 harvest tool $tool")
		}
		when (block.blockMaterial) {
			Material.rock, Material.iron, Material.anvil   -> tools += BlockTags.MINEABLE_WITH_PICKAXE
			Material.wood, Material.plants, Material.vine -> tools += BlockTags.MINEABLE_WITH_AXE
		}
		return tools
	}

	fun tier(block: LegacyBlock): TagKey<Block>? {
		if (block.getHarvestTool(block.variant ?: 0) == null) return null
		return when (block.legacy.harvestLevel) {
			in Int.MIN_VALUE..0 -> null
			1                   -> BlockTags.NEEDS_STONE_TOOL
			2                   -> BlockTags.NEEDS_IRON_TOOL
			3                   -> BlockTags.NEEDS_DIAMOND_TOOL
			else                -> Tags.Blocks.NEEDS_NETHERITE_TOOL
		}
	}
}

/**
 * Теги формы, без которых блок 1.20.1 ведёт себя не так, как блок этой формы (MAPPING.md, «Блоки и предметы»): стена
 * соединяется со стенами и панелями из `minecraft:walls`, к забору из `minecraft:fences` привязывается поводок, через
 * люк из `minecraft:trapdoors` ходят мобы. Забор порта не в `minecraft:wooden_fences`: к нему не тянутся заборы
 * ванилы, как в 1.7.10 (сам он тянется к любому забору — `BlockModFence.connectsTo`)
 */
object ShapeTags {

	fun tag(block: Block): TagKey<Block>? = when (block) {
		is Wall1710     -> BlockTags.WALLS
		is Fence1710    -> BlockTags.FENCES
		is TrapDoor1710 -> BlockTags.TRAPDOORS
		else            -> null
	}
}

/**
 * Ore Dictionary 1.7.10 → теги 1.20.1 (MAPPING.md, «Ore Dictionary»). Записи даёт `regOreDict` автора; каждое имя
 * должно быть в [names], иначе генерация данных падает — новое имя добавляет КТ, в которой появилась его вещь.
 * Тег один и тот же для предмета и для блока: в 1.7.10 по Ore Dictionary искали и вещи, и блоки.
 */
object OreDictTags {

	val names = mapOf(
		"sand" to forge("sand"),
		"coal" to ResourceLocation("minecraft", "coals"),
		"slimeball" to forge("slimeballs"),
		LibOreDict.DRAGON_ORE to forge("ores/dragonstone"),
		LibOreDict.ELEMENTIUM_ORE to forge("ores/elementium"),
		LibOreDict.ELVEN_QUARTZ_ORE to forge("ores/quartz_elven"),
		LibOreDict.GOLD_ORE to forge("ores/gold"),
		LibOreDict.GOLD_ORE + "Alfheim" to alfheim(LibOreDict.GOLD_ORE + "Alfheim"),
		LibOreDict.IFFESAL_ORE to forge("ores/iffesal"),
		LibOreDict.LAPIS_ORE to forge("ores/lapis"),
		LibOreDict.LAPIS_ORE + "Alfheim" to alfheim(LibOreDict.LAPIS_ORE + "Alfheim"),
		LibOreDict.ELVORIUM_INGOT to forge("ingots/elvorium"),
		LibOreDict.MAUFTRIUM_INGOT to forge("ingots/mauftrium"),
		LibOreDict.MUSPELHEIM_POWER_INGOT to forge("ingots/muspelheim_power"),
		LibOreDict.NIFLHEIM_POWER_INGOT to forge("ingots/niflheim_power"),
		LibOreDict.ELVORIUM_NUGGET to forge("nuggets/elvorium"),
		LibOreDict.MAUFTRIUM_NUGGET to forge("nuggets/mauftrium"),
		LibOreDict.IFFESAL_DUST to forge("dusts/iffesal"),
		LibOreDict.MUSPELHEIM_ESSENCE to alfheim(LibOreDict.MUSPELHEIM_ESSENCE),
		LibOreDict.NIFLHEIM_ESSENCE to alfheim(LibOreDict.NIFLHEIM_ESSENCE),
		LibOreDict.FENRIR_FUR to alfheim(LibOreDict.FENRIR_FUR),
		LibOreDict.ARUNE[0] to alfheim(LibOreDict.ARUNE[0]),
		LibOreDict.ARUNE[1] to alfheim(LibOreDict.ARUNE[1]),
		LibOreDict.ARUNE[2] to alfheim(LibOreDict.ARUNE[2]),
		LibOreDict.INFUSED_DREAM_TWIG to alfheim(LibOreDict.INFUSED_DREAM_TWIG),
		LibOreDict.TWIG_THUNDERWOOD to alfheim(LibOreDict.TWIG_THUNDERWOOD),
		LibOreDict.SPLINTERS_THUNDERWOOD to alfheim(LibOreDict.SPLINTERS_THUNDERWOOD),
		LibOreDict.TWIG_NETHERWOOD to alfheim(LibOreDict.TWIG_NETHERWOOD),
		LibOreDict.SPLINTERS_NETHERWOOD to alfheim(LibOreDict.SPLINTERS_NETHERWOOD),
		LibOreDict.COAL_NETHERWOOD to alfheim(LibOreDict.COAL_NETHERWOOD),
		LibOreDict.DYES(LibOreDict.Color.Rainbow) to alfheim(LibOreDict.DYES(LibOreDict.Color.Rainbow)),
		LibOreDict.FLORAL_POWDER to alfheim(LibOreDict.FLORAL_POWDER),
		LibOreDict.RAINBOW_PETAL to alfheim(LibOreDict.RAINBOW_PETAL),
		LibOreDict.RAINBOW_QUARTZ to alfheim(LibOreDict.RAINBOW_QUARTZ),
		LibOreDict.PETAL_ANY to alfheim(LibOreDict.PETAL_ANY),
	)

	/** Общий тег Forge: `forge:ingots/elvorium` — так материалы называют и Botania 1.20.1, и другие моды */
	private fun forge(path: String) = ResourceLocation("forge", path)

	/** Имя, у которого нет общего тега, — тег Alfheim по имени автора: `essenceMuspelheim` → `alfheim:essence_muspelheim` */
	private fun alfheim(name: String) = ResourceLocation(MODID, AlfheimRegisters.snakeCase(name))

	fun entries(): List<Pair<String, ItemStack>> {
		if (OreDictionary.entries.isEmpty()) {
			AlfheimBlocks.regOreDict()
			AlfheimItems.regOreDict()
		}
		return OreDictionary.entries
	}

	fun tag(name: String) = names[name] ?: throw IllegalStateException("No 1.20.1 tag for Ore Dictionary name $name: add it to OreDictTags.names")
}

class AlfheimBlockTags(output: PackOutput, lookup: CompletableFuture<HolderLookup.Provider>, files: ExistingFileHelper): BlockTagsProvider(output, lookup, MODID, files) {

	override fun addTags(provider: HolderLookup.Provider) {
		for (block in LegacyRegistration.blocks.keys) {
			if (block !is LegacyBlock) continue
			HarvestTags.tools(block).forEach { tag(it).add(block) }
			HarvestTags.tier(block)?.let { tag(it).add(block) }
			if (block.isBeaconBase(null, 0, 0, 0, 0, 0, 0)) tag(BlockTags.BEACON_BASE_BLOCKS).add(block)
			ShapeTags.tag(block)?.let { tag(it).add(block) }
		}

		// блок — вещь-блок 1.7.10 (ItemBlock). Предмет, который ставит блок под своим именем (ItemNameBlockItem: семена,
		// лепестки Botania), в 1.7.10 был простым предметом
		for ((name, stack) in OreDictTags.entries()) {
			val item = stack.item as? BlockItem ?: continue
			if (item !is ItemNameBlockItem) tag(BlockTags.create(OreDictTags.tag(name))).add(item.block)
		}
	}
}

class AlfheimItemTags(output: PackOutput, lookup: CompletableFuture<HolderLookup.Provider>, blockTags: CompletableFuture<TagsProvider.TagLookup<Block>>, files: ExistingFileHelper): ItemTagsProvider(output, lookup, blockTags, MODID, files) {

	override fun addTags(provider: HolderLookup.Provider) {
		for ((name, stack) in OreDictTags.entries())
			tag(ItemTags.create(OreDictTags.tag(name))).add(stack.item)
	}
}
