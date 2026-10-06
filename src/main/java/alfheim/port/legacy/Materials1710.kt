package alfheim.port.legacy

import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.AirBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.material.Fluids
import vazkii.botania.common.block.BotaniaBlocks

/**
 * `block.material` 1.7.10 у любого блока (MAPPING.md, «Прослойка `alfheim.port.legacy`»): у блока порта — его материал;
 * у блоков ванилы и Botania — материал того же блока 1.7.10 по таблице [Materials1710]; воздух, вода и лава — свои
 * материалы. Блок 1.20.1, которого в 1.7.10 не было, с тегом `minecraft:dirt` (корнистая земля, грязь, мох) — земля:
 * на нём держится растение, как на земле 1.7.10. Прочие блоки — камень, самый частый материал 1.7.10: таблица
 * дополняется, когда коду автора нужен материал ещё одного блока
 */
val Block.material: Material
	get() = when {
		this is LegacyBlock     -> legacy.material
		this is AirBlock        -> Material.air
		this is LiquidBlock     -> if (fluid.isSame(Fluids.LAVA)) Material.lava else Material.water
		else                    -> Materials1710.of(this)
	}

/** Материалы 1.7.10 блоков ванилы и Botania (`super(Material.…)` их классов 1.7.10) */
object Materials1710 {

	private val table: Map<Block, Material> by lazy {
		mapOf(
			// BlockDirt (земля, каменистая земля, подзол), BlockFarmland
			Blocks.DIRT to Material.ground, Blocks.COARSE_DIRT to Material.ground, Blocks.PODZOL to Material.ground, Blocks.FARMLAND to Material.ground,
			// BlockGrass, BlockMycelium, BlockHay
			Blocks.GRASS_BLOCK to Material.grass, Blocks.MYCELIUM to Material.grass, Blocks.HAY_BLOCK to Material.grass,
			// Botania 1.7.10: BlockEnchantedSoil, BlockAltGrass (6 видов)
			BotaniaBlocks.enchantedSoil to Material.grass,
			BotaniaBlocks.dryGrass to Material.grass, BotaniaBlocks.goldenGrass to Material.grass, BotaniaBlocks.vividGrass to Material.grass,
			BotaniaBlocks.scorchedGrass to Material.grass, BotaniaBlocks.infusedGrass to Material.grass, BotaniaBlocks.mutatedGrass to Material.grass,
		)
	}

	fun of(block: Block) = table[block] ?: if (block.builtInRegistryHolder().`is`(BlockTags.DIRT)) Material.ground else Material.rock
}
