package alfheim.port.legacy

import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.AirBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.material.Fluids
import net.minecraftforge.common.Tags
import vazkii.botania.common.block.BotaniaBlocks

/**
 * `block.material` 1.7.10 у любого блока (MAPPING.md, «Прослойка `alfheim.port.legacy`»): у блока порта — его материал;
 * у блоков ванилы и Botania — материал того же блока 1.7.10 по таблице [Materials1710]; воздух, вода и лава — свои
 * материалы. Блок 1.20.1, которого в 1.7.10 не было, — как такие же блоки 1.7.10: по тегу (корнистая земля, грязь и мох
 * в `minecraft:dirt` — земля, на нём держится растение, как на земле 1.7.10; листва, брёвна, доски и деревянные вещи,
 * саженцы и цветы, огонь, пески, стёкла и панели, шерсть, кровати, ковры, рельсы, кнопки, горшки, таблички, наковальни,
 * котлы) или по таблице (подтаявший и синий лёд, мокрая губка, факел душ, головы, командные блоки, врата Края). Прочие
 * блоки — камень: в 1.7.10 это материал всех остальных блоков ванилы
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
			// BlockChest (сундук, сундук-ловушка), BlockJukebox, BlockDaylightDetector — тоже дерево
			Blocks.CHEST to Material.wood, Blocks.TRAPPED_CHEST to Material.wood, Blocks.JUKEBOX to Material.wood, Blocks.DAYLIGHT_DETECTOR to Material.wood,
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
			// BlockIce, BlockPackedIce; подтаявший лёд 1.20.1 — как лёд, синий — как плотный
			Blocks.ICE to Material.ice, Blocks.FROSTED_ICE to Material.ice, Blocks.PACKED_ICE to Material.packedIce, Blocks.BLUE_ICE to Material.packedIce,
			// BlockCactus, BlockPumpkin (тыква, светильник Джека), BlockMelon, BlockWeb
			Blocks.CACTUS to Material.cactus, Blocks.PUMPKIN to Material.gourd, Blocks.CARVED_PUMPKIN to Material.gourd,
			Blocks.JACK_O_LANTERN to Material.gourd, Blocks.MELON to Material.gourd, Blocks.COBWEB to Material.web,
			// BlockGravel, BlockSoulSand — песок (пески — по тегу); подозрительный гравий 1.20.1 — как гравий
			Blocks.GRAVEL to Material.sand, Blocks.SUSPICIOUS_GRAVEL to Material.sand, Blocks.SOUL_SAND to Material.sand,
			// BlockClay; BlockSilverfish — камни с чешуйницей, глубинный сланец с ней (1.20.1) — как они
			Blocks.CLAY to Material.clay, Blocks.INFESTED_STONE to Material.clay, Blocks.INFESTED_COBBLESTONE to Material.clay,
			Blocks.INFESTED_STONE_BRICKS to Material.clay, Blocks.INFESTED_MOSSY_STONE_BRICKS to Material.clay,
			Blocks.INFESTED_CRACKED_STONE_BRICKS to Material.clay, Blocks.INFESTED_CHISELED_STONE_BRICKS to Material.clay,
			Blocks.INFESTED_DEEPSLATE to Material.clay,
			// BlockSponge; мокрая губка 1.20.1 — как губка
			Blocks.SPONGE to Material.sponge, Blocks.WET_SPONGE to Material.sponge,
			// BlockGlowstone, BlockBeacon — стекло (стёкла и панели — по тегам)
			Blocks.GLOWSTONE to Material.glass, Blocks.BEACON to Material.glass,
			// BlockCompressed, BlockCompressedPowered (блоки хранения), железные BlockDoor и BlockPane, BlockBrewingStand,
			// BlockHopper, BlockCommandBlock (цепной и повторяющийся 1.20.1 — как он), BlockPressurePlateWeighted — железо
			// (котлы — по тегу)
			Blocks.IRON_BLOCK to Material.iron, Blocks.GOLD_BLOCK to Material.iron, Blocks.DIAMOND_BLOCK to Material.iron,
			Blocks.EMERALD_BLOCK to Material.iron, Blocks.LAPIS_BLOCK to Material.iron, Blocks.REDSTONE_BLOCK to Material.iron,
			Blocks.IRON_DOOR to Material.iron, Blocks.IRON_BARS to Material.iron, Blocks.BREWING_STAND to Material.iron, Blocks.HOPPER to Material.iron,
			Blocks.COMMAND_BLOCK to Material.iron, Blocks.CHAIN_COMMAND_BLOCK to Material.iron, Blocks.REPEATING_COMMAND_BLOCK to Material.iron,
			Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE to Material.iron, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE to Material.iron,
			// BlockPistonBase, BlockPistonExtension, BlockPistonMoving
			Blocks.PISTON to Material.piston, Blocks.STICKY_PISTON to Material.piston, Blocks.PISTON_HEAD to Material.piston,
			Blocks.MOVING_PISTON to Material.piston,
			// BlockTorch, BlockRedstoneTorch (факел душ 1.20.1 — как факел), BlockRedstoneWire, BlockLadder, BlockLever,
			// BlockRedstoneRepeater, BlockRedstoneComparator, BlockTripWireHook, BlockTripWire, BlockSkull (головы 1.20.1 —
			// как она) — «схемы» (рельсы, кнопки и горшки — по тегам)
			Blocks.TORCH to Material.circuits, Blocks.WALL_TORCH to Material.circuits, Blocks.SOUL_TORCH to Material.circuits,
			Blocks.SOUL_WALL_TORCH to Material.circuits, Blocks.REDSTONE_TORCH to Material.circuits, Blocks.REDSTONE_WALL_TORCH to Material.circuits,
			Blocks.REDSTONE_WIRE to Material.circuits, Blocks.LADDER to Material.circuits, Blocks.LEVER to Material.circuits,
			Blocks.REPEATER to Material.circuits, Blocks.COMPARATOR to Material.circuits, Blocks.TRIPWIRE_HOOK to Material.circuits,
			Blocks.TRIPWIRE to Material.circuits,
			Blocks.SKELETON_SKULL to Material.circuits, Blocks.SKELETON_WALL_SKULL to Material.circuits, Blocks.WITHER_SKELETON_SKULL to Material.circuits,
			Blocks.WITHER_SKELETON_WALL_SKULL to Material.circuits, Blocks.ZOMBIE_HEAD to Material.circuits, Blocks.ZOMBIE_WALL_HEAD to Material.circuits,
			Blocks.PLAYER_HEAD to Material.circuits, Blocks.PLAYER_WALL_HEAD to Material.circuits, Blocks.CREEPER_HEAD to Material.circuits,
			Blocks.CREEPER_WALL_HEAD to Material.circuits, Blocks.DRAGON_HEAD to Material.circuits, Blocks.DRAGON_WALL_HEAD to Material.circuits,
			Blocks.PIGLIN_HEAD to Material.circuits, Blocks.PIGLIN_WALL_HEAD to Material.circuits,
			// BlockTNT, BlockPortal, BlockEndPortal (врата Края 1.20.1 — как он), BlockCake, BlockDragonEgg, BlockRedstoneLight
			Blocks.TNT to Material.tnt, Blocks.NETHER_PORTAL to Material.portal, Blocks.END_PORTAL to Material.portal,
			Blocks.END_GATEWAY to Material.portal, Blocks.CAKE to Material.cake, Blocks.DRAGON_EGG to Material.dragonEgg,
			Blocks.REDSTONE_LAMP to Material.redstoneLight,
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
		// BlockSand (подозрительный песок 1.20.1 — как песок); BlockGlass, BlockStainedGlass (тонированное стекло 1.20.1 —
		// как стекло), BlockPane и BlockStainedGlassPane из стекла
		BlockTags.SAND to Material.sand, Tags.Blocks.GLASS to Material.glass, Tags.Blocks.GLASS_PANES to Material.glass,
		// BlockColored (шерсть), BlockBed — ткань; BlockCarpet
		BlockTags.WOOL to Material.cloth, BlockTags.BEDS to Material.cloth, BlockTags.WOOL_CARPETS to Material.carpet,
		// BlockRailBase, BlockButton, BlockFlowerPot — «схемы»; BlockSign (подвесные таблички 1.20.1 — как таблички) — дерево
		BlockTags.RAILS to Material.circuits, BlockTags.BUTTONS to Material.circuits, BlockTags.FLOWER_POTS to Material.circuits,
		BlockTags.ALL_SIGNS to Material.wood,
		// BlockAnvil; BlockCauldron (котлы с водой, лавой и снегом 1.20.1 — как котёл)
		BlockTags.ANVIL to Material.anvil, BlockTags.CAULDRONS to Material.iron,
	)

	fun of(block: Block): Material {
		table[block]?.let { return it }
		val holder = block.builtInRegistryHolder()
		return tags.firstOrNull { holder.`is`(it.first) }?.second ?: Material.rock
	}
}
