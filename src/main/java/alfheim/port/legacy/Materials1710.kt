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
 * материалы. Блок 1.20.1, которого в 1.7.10 не было, — по тегу, как такие же блоки 1.7.10: с тегом `minecraft:dirt`
 * (корнистая земля, грязь, мох) — земля, на нём держится растение, как на земле 1.7.10; листва, брёвна, доски и
 * деревянные лестницы, плиты, заборы, калитки, двери, люки и нажимные плиты, саженцы и цветы, огонь — материал таких
 * же блоков 1.7.10. Прочие блоки — камень, самый частый материал 1.7.10: таблица дополняется, когда коду автора нужен
 * материал ещё одного блока
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
			// BlockWorkbench, BlockBookshelf, BlockNote, BlockHugeMushroom — дерево (брёвна, доски и деревянные вещи — по тегам)
			Blocks.CRAFTING_TABLE to Material.wood, Blocks.BOOKSHELF to Material.wood, Blocks.NOTE_BLOCK to Material.wood,
			Blocks.BROWN_MUSHROOM_BLOCK to Material.wood, Blocks.RED_MUSHROOM_BLOCK to Material.wood, Blocks.MUSHROOM_STEM to Material.wood,
			// BlockDoublePlant (высокая трава, большой папоротник), BlockMushroom, BlockReed, BlockCrops, BlockCarrot, BlockPotato,
			// BlockLilyPad, BlockStem, BlockCocoa, BlockNetherWart — растения (саженцы и цветы — по тегам)
			Blocks.TALL_GRASS to Material.plants, Blocks.LARGE_FERN to Material.plants, Blocks.BROWN_MUSHROOM to Material.plants,
			Blocks.RED_MUSHROOM to Material.plants, Blocks.SUGAR_CANE to Material.plants, Blocks.WHEAT to Material.plants,
			Blocks.CARROTS to Material.plants, Blocks.POTATOES to Material.plants, Blocks.LILY_PAD to Material.plants,
			Blocks.PUMPKIN_STEM to Material.plants, Blocks.ATTACHED_PUMPKIN_STEM to Material.plants, Blocks.MELON_STEM to Material.plants,
			Blocks.ATTACHED_MELON_STEM to Material.plants, Blocks.COCOA to Material.plants, Blocks.NETHER_WART to Material.plants,
			// BlockTallGrass (трава, папоротник), BlockDeadBush, BlockVine — лианы
			Blocks.GRASS to Material.vine, Blocks.FERN to Material.vine, Blocks.DEAD_BUSH to Material.vine, Blocks.VINE to Material.vine,
			// BlockSnow (слой снега), BlockSnowBlock
			Blocks.SNOW to Material.snow, Blocks.SNOW_BLOCK to Material.craftedSnow,
			// BlockCactus, BlockPumpkin (тыква, светильник Джека), BlockMelon, BlockWeb
			Blocks.CACTUS to Material.cactus, Blocks.PUMPKIN to Material.gourd, Blocks.CARVED_PUMPKIN to Material.gourd,
			Blocks.JACK_O_LANTERN to Material.gourd, Blocks.MELON to Material.gourd, Blocks.COBWEB to Material.web,
		)
	}

	/**
	 * Материал по тегу — у блоков, которых нет в таблице: так и блок 1.20.1, которого в 1.7.10 не было, получает
	 * материал таких же блоков 1.7.10. Первый подходящий: цветущая листва и в `minecraft:leaves`, и в `minecraft:flowers`
	 */
	private val tags = listOf(
		// земля 1.20.1 (корнистая земля, грязь, мох); BlockLeaves; BlockLog, BlockWood (доски), деревянные BlockStairs,
		// BlockWoodSlab, BlockFence, BlockFenceGate, BlockDoor, BlockTrapDoor, BlockPressurePlate
		BlockTags.DIRT to Material.ground, BlockTags.LEAVES to Material.leaves,
		BlockTags.LOGS to Material.wood, BlockTags.PLANKS to Material.wood, BlockTags.WOODEN_STAIRS to Material.wood,
		BlockTags.WOODEN_SLABS to Material.wood, BlockTags.WOODEN_FENCES to Material.wood, BlockTags.FENCE_GATES to Material.wood,
		BlockTags.WOODEN_DOORS to Material.wood, BlockTags.WOODEN_TRAPDOORS to Material.wood, BlockTags.WOODEN_PRESSURE_PLATES to Material.wood,
		// BlockSapling, BlockFlower, цветы BlockDoublePlant; BlockFire
		BlockTags.SAPLINGS to Material.plants, BlockTags.FLOWERS to Material.plants, BlockTags.FIRE to Material.fire,
	)

	fun of(block: Block): Material {
		table[block]?.let { return it }
		val holder = block.builtInRegistryHolder()
		return tags.firstOrNull { holder.`is`(it.first) }?.second ?: Material.rock
	}
}
