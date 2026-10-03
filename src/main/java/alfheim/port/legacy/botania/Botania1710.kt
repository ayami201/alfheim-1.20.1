package alfheim.port.legacy.botania

import alfheim.port.legacy.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import vazkii.botania.common.block.BotaniaBlocks

/**
 * Блоки Botania r1.8-249 (1.7.10), из которых автор делает свои: стена из кирпичей живого камня, калитка из живого
 * дерева. В 1.20.1 это блоки Botania 1.20.1 — вариант metadata стал отдельным блоком (`ModBlocks.livingrock`, 1 →
 * `BotaniaBlocks.livingrockBrick`). Блоку автора нужны их значения 1.7.10 (материал, твёрдость, взрывоустойчивость,
 * звук, имя) — они взяты из кода Botania r1.8-249 (`legacy/libs`). Текстура — Botania 1.20.1: блок автора выглядит
 * так же, как блок Botania, из которого он сделан.
 */
object Botania1710 {

	/**
	 * Блок Botania 1.7.10: имя (`getUnlocalizedName`), материал, `setHardness`, `setResistance` (`null` — не вызывался),
	 * звук; текстуры граней 1.20.1 — бок, верх, низ, как их давал `getIcon` 1.7.10
	 */
	private class Info(val unlocalizedName: String, val material: Material, val hardness: Float, val resistance: Float?, val sound: SoundType, val side: String, val top: String = side, val bottom: String = top)

	private val blocks: Map<Block, Info> by lazy {
		mapOf(
			// BlockLivingrock, вариант 1 — кирпичи
			BotaniaBlocks.livingrockBrick to Info("tile.livingrock", Material.rock, 2f, 10f, SoundType.STONE, "livingrock_bricks"),
			// BlockSpecialQuartz("Elf"), вариант 0: верх и низ — blockElfQuartz1, бок — blockElfQuartz0
			BotaniaBlocks.elfQuartz to Info("tile.quartzTypeElf", Material.rock, 0.8f, 10f, SoundType.STONE, "elf_quartz_side", "elf_quartz_top"),
			// BlockLivingwood, BlockDreamwood: вариант 0 — кора, 1 — доски
			BotaniaBlocks.livingwood to Info("tile.livingwood", Material.wood, 2f, null, SoundType.WOOD, "livingwood_log"),
			BotaniaBlocks.livingwoodPlanks to Info("tile.livingwood", Material.wood, 2f, null, SoundType.WOOD, "livingwood_planks"),
			BotaniaBlocks.dreamwood to Info("tile.dreamwood", Material.wood, 2f, null, SoundType.WOOD, "dreamwood_log"),
			BotaniaBlocks.dreamwoodPlanks to Info("tile.dreamwood", Material.wood, 2f, null, SoundType.WOOD, "dreamwood_planks"),
		)
	}

	/** Иконки Botania 1.7.10 по имени (`BlockFence` 1.7.10 брал текстуру по имени) → текстуры 1.20.1 */
	private val icons = mapOf(
		"botania:livingwood0" to "livingwood_log",
		"botania:livingwood1" to "livingwood_planks",
		"botania:dreamwood0" to "dreamwood_log",
		"botania:dreamwood1" to "dreamwood_planks",
	)

	private fun info(block: Block) = blocks[block] ?: throw IllegalStateException("No Botania 1.7.10 values for $block: add them to Botania1710")

	/** Значения сеттеров 1.7.10 блока Botania — в том порядке, в каком их вызывал его конструктор */
	fun props(block: Block): BlockProps {
		val info = info(block)
		return BlockProps(info.material).apply {
			unlocalizedName = info.unlocalizedName
			blockHardness = info.hardness
			blockResistance = info.hardness * 5f
			info.resistance?.let { blockResistance = it * 3f }
			stepSound = info.sound
		}
	}

	private fun texture(name: String) = ResourceLocation("botania", "block/$name")

	fun side(block: Block) = texture(info(block).side)

	fun top(block: Block) = texture(info(block).top)

	fun bottom(block: Block) = texture(info(block).bottom)

	/** Текстура 1.20.1 для иконки Botania 1.7.10 `botania:имя`, `null` — не иконка Botania */
	fun icon(name: String) = if (name.startsWith("botania:")) texture(icons[name] ?: throw IllegalStateException("No 1.20.1 texture for Botania 1.7.10 icon $name: add it to Botania1710")) else null
}
