package alfheim.port.data

import alfheim.api.ModInfo.MODID
import alfheim.common.block.AlfheimBlocks
import alfheim.common.item.AlfheimItems
import alfheim.port.legacy.*
import alfheim.port.registry.LegacyRegistration
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
 * Чем блок добывается быстро: инструмент из `setHarvestLevel` и, как у кирки и топора 1.7.10, ещё и по материалу —
 * кирка быстра на камне, железе и наковальне, топор — на дереве и растениях. Уровень добычи 1, 2, 3 — теги
 * «нужен каменный, железный, алмазный инструмент», 4 и выше — незеритовый. Без нужного инструмента блок
 * ничего не роняет, только если этого требует материал (`requiresCorrectToolForDrops`, как в 1.7.10).
 */
object HarvestTags {

	fun tools(block: LegacyBlock): Set<TagKey<Block>> {
		val tools = LinkedHashSet<TagKey<Block>>()
		when (block.legacy.harvestTool) {
			"pickaxe" -> tools += BlockTags.MINEABLE_WITH_PICKAXE
			"axe"     -> tools += BlockTags.MINEABLE_WITH_AXE
			"shovel"  -> tools += BlockTags.MINEABLE_WITH_SHOVEL
			null      -> Unit
			else      -> throw IllegalStateException("Unknown 1.7.10 harvest tool ${block.legacy.harvestTool}")
		}
		when (block.blockMaterial) {
			Material.rock, Material.iron, Material.anvil   -> tools += BlockTags.MINEABLE_WITH_PICKAXE
			Material.wood, Material.plants, Material.vine -> tools += BlockTags.MINEABLE_WITH_AXE
		}
		return tools
	}

	fun tier(block: LegacyBlock): TagKey<Block>? {
		if (block.legacy.harvestTool == null) return null
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
		"sand" to ResourceLocation("forge", "sand"),
	)

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

		for ((name, stack) in OreDictTags.entries()) {
			val block = Block.byItem(stack.item)
			if (block != Blocks.AIR) tag(BlockTags.create(OreDictTags.tag(name))).add(block)
		}
	}
}

class AlfheimItemTags(output: PackOutput, lookup: CompletableFuture<HolderLookup.Provider>, blockTags: CompletableFuture<TagsProvider.TagLookup<Block>>, files: ExistingFileHelper): ItemTagsProvider(output, lookup, blockTags, MODID, files) {

	override fun addTags(provider: HolderLookup.Provider) {
		for ((name, stack) in OreDictTags.entries())
			tag(ItemTags.create(OreDictTags.tag(name))).add(stack.item)
	}
}
