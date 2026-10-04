package alfheim.port.test

import alfheim.api.AlfheimAPI
import alfheim.api.ModInfo.MODID
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.base.BlockLeavesMod
import alfheim.common.block.colored.*
import alfheim.port.legacy.*
import com.google.gson.JsonParser
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.util.RandomSource
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.*
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate

/**
 * КТ-2, партия 8а: цветные блоки — земля, трава, двойная трава, брёвна, доски, плиты, ступеньки, листва, саженец и
 * лампа. Числа и правила — из классов автора (`alfheim.common.block.colored`) и правил 1.7.10 (MAPPING.md, «Растения»)
 */
@GameTestHolder(MODID)
@PrefixGameTestTemplate(false)
object PortPlantsTest {

	/** Варианты metadata — отдельные блоки под именами `<имя автора><номер>` (SPEC, Р-5) */
	@JvmStatic
	@GameTest(template = "empty")
	fun coloredBlocksIds(helper: GameTestHelper) {
		val ids = (0..15).flatMap { listOf("colored_dirt$it", "iris_grass$it", "iris_planks$it", "iris_planks_slab$it", "iris_planks_stairs$it") } +
			(0..1).flatMap { set -> (0..7).flatMap { listOf("iris_leaves$set$it", "iris_double_grass$set$it") } } +
			(0..3).flatMap { set -> (0..3).map { "iris_wood$set$it" } } + listOf("iris_lamp", "iris_sapling")
		helper.assertTrue(ids.size == 130, "ids: ${ids.size}")
		for (id in ids) helper.assertTrue(BuiltInRegistries.BLOCK.containsKey(ResourceLocation(MODID, id)), "$MODID:$id is not registered")
		helper.succeed()
	}

	/**
	 * Саженец на цветной земле растёт деревом её цвета (`AlfheimBlocks.registerFlora`): цвет 6 — бревно `irisWood1`
	 * (цвета 4–7) варианта 6 and 3 = 2 и листва `irisLeaves0` варианта 6. Первый шаг роста только помечает саженец
	 * (`markOrGrowMarked`: бит 8 — STAGE), земля под деревом остаётся цветной
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun irisSaplingGrowsTree(helper: GameTestHelper) {
		val soil = BlockPos(0, 0, 0)
		helper.setBlock(soil, AlfheimBlocks.irisDirt[6])
		helper.setBlock(soil.above(), AlfheimBlocks.irisSapling)
		val sapling = AlfheimBlocks.irisSapling as BlockColoredSapling
		val pos = helper.absolutePos(soil.above())
		val random = RandomSource.create(1)

		helper.assertTrue(sapling.canGrowHere(AlfheimBlocks.irisDirt[6]), "colored dirt is an iridescent soil")
		sapling.markOrGrowMarked(helper.level, pos.x, pos.y, pos.z, random)
		helper.assertBlockProperty(soil.above(), Sapling1710.STAGE, 1)
		sapling.markOrGrowMarked(helper.level, pos.x, pos.y, pos.z, random)

		helper.assertBlockPresent(AlfheimBlocks.irisWood1[2], soil.above())
		helper.assertBlockPresent(AlfheimBlocks.irisDirt[6], soil)
		var leaves = 0
		for (p in BlockPos.betweenClosed(-2, 1, -2, 2, 10, 2)) {
			val state = helper.getBlockState(p)
			if (state.`is`(BlockTags.LEAVES)) {
				helper.assertTrue(state.block === AlfheimBlocks.irisLeaves0[6], "leaves ${state.block} at $p")
				helper.assertTrue(!state.getValue(LeavesBlock.PERSISTENT), "leaves of a tree decay")
				leaves++
			} else if (state.`is`(BlockTags.LOGS)) helper.assertTrue(state.block === AlfheimBlocks.irisWood1[2] && p.x == 0 && p.z == 0, "log ${state.block} at $p")
		}
		helper.assertTrue(leaves > 10, "leaves: $leaves")
		helper.succeed()
	}

	/** Варианты дерева по цвету земли (`IridescentSaplingBaseVariant`): бревно — номер and 3, листва — номер − сдвиг */
	@JvmStatic
	@GameTest(template = "empty")
	fun irisTreeVariants(helper: GameTestHelper) {
		val woods = AlfheimBlocks.irisWood0 + AlfheimBlocks.irisWood1 + AlfheimBlocks.irisWood2 + AlfheimBlocks.irisWood3
		val leaves = AlfheimBlocks.irisLeaves0 + AlfheimBlocks.irisLeaves1
		for (color in 0..15) {
			val soil = AlfheimBlocks.irisDirt[color]
			val variant = AlfheimAPI.getTreeVariant(soil, color)
			helper.assertTrue(variant != null, "no tree for colored dirt $color")
			helper.assertTrue(variant!!.getWood(soil, color) === woods[color], "wood of color $color")
			helper.assertTrue(variant.getLeaves(soil, color) === leaves[color], "leaves of color $color")
			helper.assertTrue(variant.getMeta(soil, color, woods[color]) == 0, "variant blocks are placed with metadata 0")
		}
		helper.assertTrue(AlfheimAPI.getTreeVariant(Blocks.DIRT, 0) == null, "no iridescent tree on dirt")
		helper.succeed()
	}

	/**
	 * Листва опадает по алгоритму автора (`BlockLeavesMod.updateTick`): без бревна в 4 шагах по листве — исчезает, с ним —
	 * остаётся; поставленная игроком (persistent — бит опадания 1.7.10) не опадает
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun irisLeavesDecay(helper: GameTestHelper) {
		val leaves = AlfheimBlocks.irisLeaves1[2] as BlockLeavesMod
		val log = BlockPos(0, 1, 0)
		helper.setBlock(log, AlfheimBlocks.irisWood3[1])
		for (y in 2..5) helper.setBlock(BlockPos(0, y, 0), leaves.defaultBlockState())
		helper.setBlock(BlockPos(0, 6, 0), leaves.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true))
		fun tick(y: Int) {
			val pos = helper.absolutePos(BlockPos(0, y, 0))
			leaves.updateTick(helper.level, pos.x, pos.y, pos.z, RandomSource.create(y.toLong()))
		}

		for (y in 2..5) tick(y)
		for (y in 2..5) helper.assertBlockPresent(leaves, BlockPos(0, y, 0))
		helper.setBlock(log, Blocks.AIR)
		for (y in 2..6) tick(y)
		for (y in 2..5) helper.assertBlockNotPresent(leaves, BlockPos(0, y, 0))
		helper.assertBlockPresent(leaves, BlockPos(0, 6, 0))
		helper.killAllEntities()
		helper.succeed()
	}

	/**
	 * Костная мука на цветной земле (`BlockColoredDirt.func_149853_b`): над землёй вырастает трава ириса цвета земли или
	 * цветок биома; на траве ириса — двойная трава того же цвета (`BlockColoredGrass.func_149853_b`): цвета 8–15 —
	 * `irisTallGrass1`
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun irisBonemeal(helper: GameTestHelper) {
		val soil = BlockPos(0, 0, 0)
		helper.setBlock(soil, AlfheimBlocks.irisDirt[11])
		val dirt = AlfheimBlocks.irisDirt[11] as BlockColoredDirt
		val pos = helper.absolutePos(soil)
		dirt.func_149853_b(helper.level, RandomSource.create(3), pos.x, pos.y, pos.z)
		val grown = helper.getBlockState(soil.above())
		helper.assertTrue(grown.block === AlfheimBlocks.irisGrass[11] || grown.`is`(BlockTags.SMALL_FLOWERS), "grown on colored dirt: $grown")

		helper.setBlock(soil.above(), AlfheimBlocks.irisGrass[11])
		val grass = AlfheimBlocks.irisGrass[11] as BlockColoredGrass
		val above = helper.absolutePos(soil.above())
		helper.assertTrue(grass.func_149851_a(helper.level, above.x, above.y, above.z, false), "iris grass takes bone meal")
		grass.func_149853_b(helper.level, RandomSource.create(4), above.x, above.y, above.z)
		helper.assertBlockPresent(AlfheimBlocks.irisTallGrass1[3], soil.above())
		helper.assertBlockProperty(soil.above(), DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)
		helper.assertBlockPresent(AlfheimBlocks.irisTallGrass1[3], soil.above(2))
		helper.assertBlockProperty(soil.above(2), DoublePlantBlock.HALF, DoubleBlockHalf.UPPER)
		helper.assertTrue(!(AlfheimBlocks.irisTallGrass1[3] as BlockColoredDoubleGrass).func_149851_a(helper.level, above.x, above.y, above.z, false), "double grass takes no bone meal")
		helper.succeed()
	}

	/**
	 * Двойная трава рисуется смещённой по X и Z, как в 1.7.10 (`RenderBlockColoredDoubleGrass`): на ±0,15 по хэшу x и z,
	 * обе половины одинаково
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun irisDoubleGrassOffset(helper: GameTestHelper) {
		val state = AlfheimBlocks.irisTallGrass0[2].defaultBlockState()
		for ((x, z) in listOf(0 to 0, 5 to -3, -117 to 2049, 30000 to -77)) {
			var j1 = (x * 3129871).toLong() xor z.toLong() * 116129781L
			j1 = j1 * j1 * 42317861L + j1 * 11L
			val dx = (((j1 shr 16 and 15L).toFloat() / 15f).toDouble() - 0.5) * 0.3
			val dz = (((j1 shr 24 and 15L).toFloat() / 15f).toDouble() - 0.5) * 0.3
			for (y in listOf(0, 64)) {
				val offset = state.getOffset(helper.level, BlockPos(x, y, z))
				helper.assertTrue(offset.x == dx && offset.y == 0.0 && offset.z == dz, "offset at $x, $y, $z: $offset, expected $dx, 0, $dz")
			}
		}
		helper.assertTrue(AlfheimBlocks.irisGrass[2].defaultBlockState().getOffset(helper.level, BlockPos(5, 0, -3)) == Vec3.ZERO, "iris grass is not offset (1.7.10 offset only vanilla tall grass)")
		helper.succeed()
	}

	/** Лампа (`BlockColoredLamp`): metadata — сила сигнала (POWER), светит 15 при любом сигнале */
	@JvmStatic
	@GameTest(template = "empty")
	fun irisLampPower(helper: GameTestHelper) {
		val lamp = BlockPos(0, 1, 0)
		helper.setBlock(lamp, AlfheimBlocks.irisLamp)
		helper.assertBlockProperty(lamp, BlockColoredLamp.POWER, 0)
		helper.assertTrue(helper.getBlockState(lamp).lightEmission == 0, "lamp without signal is dark")
		helper.setBlock(lamp.east(), Blocks.REDSTONE_BLOCK)
		helper.assertBlockProperty(lamp, BlockColoredLamp.POWER, 15)
		helper.assertTrue(helper.getBlockState(lamp).lightEmission == 15, "powered lamp light")
		helper.setBlock(lamp.east(), Blocks.AIR)
		helper.assertBlockProperty(lamp, BlockColoredLamp.POWER, 0)
		// лампа, поставленная у сигнала, сразу берёт его силу (onBlockAdded)
		helper.setBlock(lamp, Blocks.AIR)
		helper.setBlock(lamp.west(), Blocks.REDSTONE_BLOCK)
		helper.setBlock(lamp, AlfheimBlocks.irisLamp)
		helper.assertBlockProperty(lamp, BlockColoredLamp.POWER, 15)
		helper.succeed()
	}

	/**
	 * Цветной свет лампы для мода Colorful Lighting (`assets/alfheim/light/emitters.json` в jar мода): у каждой силы
	 * сигнала, при которой лампа светит, — цвет `powerColor` автора; ключ — свойство состояния `power`
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun irisLampColoredLight(helper: GameTestHelper) {
		val stream = PortPlantsTest::class.java.getResourceAsStream("/assets/$MODID/light/emitters.json")
		helper.assertTrue(stream != null, "light/emitters.json is not in the mod jar")
		val json = stream!!.reader(Charsets.UTF_8).use { JsonParser.parseReader(it).asJsonObject }
		val lamp = AlfheimBlocks.irisLamp as BlockColoredLamp
		val states = json.getAsJsonObject(BuiltInRegistries.BLOCK.getKey(lamp).toString()).getAsJsonObject("states")
		for (state in lamp.stateDefinition.possibleStates) {
			if (state.lightEmission == 0) continue
			val power = state.getValue(BlockColoredLamp.POWER)
			val color = states.get("${BlockColoredLamp.POWER.name}=$power")?.asString
			helper.assertTrue(color == "#%06X".format(lamp.powerColor(power) and 0xFFFFFF), "power $power: $color")
		}
		helper.succeed()
	}

	/**
	 * Лут: листва с ножницами — сама, без них — радужный саженец с шансом 1/20; трава ириса с ножницами — сама; двойная
	 * трава с ножницами — две травы ириса своего цвета, без них — ничего
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun irisLoot(helper: GameTestHelper) {
		val shears = ItemStack(Items.SHEARS)
		fun drops(pos: BlockPos, tool: ItemStack) = Block.getDrops(helper.getBlockState(pos), helper.level, helper.absolutePos(pos), null, null, tool)

		val leaves = BlockPos(1, 1, 1)
		helper.setBlock(leaves, AlfheimBlocks.irisLeaves0[4])
		helper.assertTrue(drops(leaves, shears).map { it.item } == listOf(AlfheimBlocks.irisLeaves0[4].asItem()), "sheared leaves: ${drops(leaves, shears)}")
		var saplings = 0
		repeat(400) {
			for (stack in drops(leaves, ItemStack.EMPTY)) {
				helper.assertTrue(stack.item === AlfheimBlocks.irisSapling.asItem() && stack.count == 1, "leaves drop $stack")
				saplings++
			}
		}
		helper.assertTrue(saplings in 5..40, "saplings from 400 leaves: $saplings")

		val grass = BlockPos(0, 1, 0)
		helper.setBlock(grass.below(), AlfheimBlocks.irisDirt[0])
		helper.setBlock(grass, AlfheimBlocks.irisGrass[9])
		helper.assertTrue(drops(grass, shears).map { it.item } == listOf(AlfheimBlocks.irisGrass[9].asItem()), "sheared grass")
		repeat(50) { for (stack in drops(grass, ItemStack.EMPTY)) helper.assertTrue(stack.item === Items.WHEAT_SEEDS, "grass drops $stack") }

		DoublePlantBlock.placeAt(helper.level, AlfheimBlocks.irisTallGrass0[5].defaultBlockState(), helper.absolutePos(grass), 3)
		for (half in listOf(grass, grass.above())) {
			val sheared = drops(half, shears)
			helper.assertTrue(sheared.size == 1 && sheared[0].item === AlfheimBlocks.irisGrass[5].asItem() && sheared[0].count == 2, "sheared double grass: $sheared")
			helper.assertTrue(drops(half, ItemStack.EMPTY).isEmpty(), "double grass without shears")
		}
		helper.succeed()
	}

	/** Теги и свойства 1.7.10: брёвна держат листву, листва — листва, горение (`registerBurnables`), топливо */
	@JvmStatic
	@GameTest(template = "empty")
	fun irisTagsAndFuel(helper: GameTestHelper) {
		val wood = AlfheimBlocks.irisWood2[3]
		helper.assertTrue(wood.canSustainLeaves(helper.level, 0, 0, 0), "iris wood sustains leaves (minecraft:logs)")
		helper.assertTrue(AlfheimBlocks.irisLeaves1[7].isLeaves(helper.level, 0, 0, 0), "iris leaves are leaves")
		helper.assertTrue(AlfheimBlocks.irisSapling.defaultBlockState().`is`(BlockTags.SAPLINGS), "treeSapling → minecraft:saplings")
		helper.assertTrue(AlfheimBlocks.irisPlanks[0].defaultBlockState().`is`(BlockTags.PLANKS), "plankWood → minecraft:planks")
		helper.assertTrue(AlfheimBlocks.irisSlabs[0].defaultBlockState().`is`(BlockTags.WOODEN_SLABS), "slabWood → minecraft:wooden_slabs")
		helper.assertTrue(AlfheimBlocks.irisStairs[0].defaultBlockState().`is`(BlockTags.WOODEN_STAIRS), "stairWood → minecraft:wooden_stairs")
		helper.assertTrue(AlfheimBlocks.irisDirt[0].defaultBlockState().canSustainPlant(helper.level, BlockPos.ZERO, Direction.UP, Blocks.OAK_SAPLING as SaplingBlock), "colored dirt sustains any plant")

		fun fire(block: Block, encouragement: Int, flammability: Int) {
			val state = block.defaultBlockState()
			helper.assertTrue(state.getFireSpreadSpeed(helper.level, BlockPos.ZERO, Direction.UP) == encouragement, "$block encouragement")
			helper.assertTrue(state.getFlammability(helper.level, BlockPos.ZERO, Direction.UP) == flammability, "$block flammability")
		}
		fire(AlfheimBlocks.irisGrass[3], 60, 100)
		fire(AlfheimBlocks.irisLeaves0[3], 30, 60)
		fire(AlfheimBlocks.irisPlanks[3], 5, 20)
		fire(AlfheimBlocks.irisSlabs[3], 5, 20)
		fire(AlfheimBlocks.irisStairs[3], 5, 20)
		fire(AlfheimBlocks.irisTallGrass1[3], 60, 100)
		fire(AlfheimBlocks.irisWood0[3], 5, 5)

		// топливо: саженец — 100 тиков (обработчик автора); дерево — 300 (печь 1.7.10 проверяла материал раньше обработчиков)
		helper.assertTrue(ForgeHooks.getBurnTime(ItemStack(AlfheimBlocks.irisSapling), RecipeType.SMELTING) == 100, "iris sapling fuel")
		helper.assertTrue(ForgeHooks.getBurnTime(ItemStack(AlfheimBlocks.irisSlabs[2]), RecipeType.SMELTING) == 300, "iris slab fuel")
		helper.assertTrue(ForgeHooks.getBurnTime(ItemStack(AlfheimBlocks.irisWood1[0]), RecipeType.SMELTING) == 300, "iris wood fuel")
		helper.succeed()
	}

	/** Рецепты партии 8а — числа и ингредиенты из `AlfheimRecipes`; переплавка бревна — древесный уголь */
	@JvmStatic
	@GameTest(template = "empty")
	fun irisRecipes(helper: GameTestHelper) {
		fun recipe(path: String) = helper.level.recipeManager.byKey(ResourceLocation(MODID, path)).orElse(null)
		fun result(path: String) = recipe(path)?.getResultItem(helper.level.registryAccess()) ?: ItemStack.EMPTY
		fun check(path: String, block: Block, count: Int) {
			val stack = result(path)
			helper.assertTrue(stack.item === block.asItem() && stack.count == count, "$MODID:$path → $stack, expected $count × $block")
		}
		check("iris_lamp", AlfheimBlocks.irisLamp, 1)
		check("colored_dirt14", AlfheimBlocks.irisDirt[14], 8)
		check("iris_planks_slab9", AlfheimBlocks.irisSlabs[9], 6)
		check("iris_planks_stairs9", AlfheimBlocks.irisStairs[9], 4)
		val smelted = helper.level.recipeManager.getRecipeFor(RecipeType.SMELTING, SimpleContainer(ItemStack(AlfheimBlocks.irisWood3[2])), helper.level).orElse(null)
		helper.assertTrue(smelted != null && smelted.getResultItem(helper.level.registryAccess()).item === Items.CHARCOAL && smelted.experience == 0.15f, "iris wood → charcoal")
		helper.succeed()
	}
}
