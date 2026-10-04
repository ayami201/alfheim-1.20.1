package alfheim.port.test

import alfheim.api.AlfheimAPI
import alfheim.api.ModInfo.MODID
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.base.BlockLeavesMod
import alfheim.common.block.colored.*
import alfheim.port.legacy.*
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
import net.minecraft.world.level.block.state.properties.SlabType
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate

/**
 * КТ-2, партия 8б: радужные и авроровые блоки. Числа и правила — из классов автора (`alfheim.common.block.colored`,
 * `alfheim.common.block.colored.rainbow`) и правил 1.7.10 (MAPPING.md, «Растения»)
 */
@GameTestHolder(MODID)
@PrefixGameTestTemplate(false)
object PortRainbowTest {

	/** Радужное и авроровое дерево — блоки без вариантов: id — имя автора; у авроровых плиты и ступенек — номер 17 */
	@JvmStatic
	@GameTest(template = "empty")
	fun rainbowAuroraIds(helper: GameTestHelper) {
		val ids = listOf("rainbow_dirt", "rainbow_wood", "rainbow_planks", "rainbow_planks_slab", "rainbow_planks_stairs", "rainbow_leaves",
			"aurora_dirt", "aurora_wood", "aurora_planks", "aurora_planks_slab17", "aurora_planks_stairs17", "aurora_leaves")
		for (id in ids) helper.assertTrue(BuiltInRegistries.BLOCK.containsKey(ResourceLocation(MODID, id)), "$MODID:$id is not registered")
		helper.succeed()
	}

	/**
	 * Саженец на радужной земле растёт радужным деревом, на авроровой — авроровым (`AlfheimBlocks.registerFlora`).
	 * Деревья растут по очереди на одном месте: соседние тесты стоят в 6 блоках
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun rainbowAuroraTrees(helper: GameTestHelper) {
		val sapling = AlfheimBlocks.irisSapling as BlockColoredSapling
		val soil = BlockPos(0, 0, 2)
		for (tree in listOf(Triple(AlfheimBlocks.rainbowDirt, AlfheimBlocks.rainbowWood, AlfheimBlocks.rainbowLeaves),
		                    Triple(AlfheimBlocks.auroraDirt, AlfheimBlocks.auroraWood, AlfheimBlocks.auroraLeaves))) {
			val (dirt, wood, leaves) = tree
			for (p in BlockPos.betweenClosed(-2, 1, 0, 2, 10, 4)) helper.setBlock(p, Blocks.AIR)
			helper.setBlock(soil, dirt)
			helper.setBlock(soil.above(), AlfheimBlocks.irisSapling)
			val pos = helper.absolutePos(soil.above())
			val random = RandomSource.create(1)
			helper.assertTrue(sapling.canGrowHere(dirt), "$dirt is an iridescent soil")
			sapling.markOrGrowMarked(helper.level, pos.x, pos.y, pos.z, random)
			sapling.markOrGrowMarked(helper.level, pos.x, pos.y, pos.z, random)
			helper.assertBlockPresent(wood, soil.above())
			helper.assertBlockPresent(dirt, soil)
			var count = 0
			for (p in BlockPos.betweenClosed(-2, 1, 0, 2, 10, 4)) {
				val state = helper.getBlockState(p)
				if (state.`is`(BlockTags.LEAVES)) {
					helper.assertTrue(state.block === leaves && !state.getValue(LeavesBlock.PERSISTENT), "leaves ${state.block} at $p")
					count++
				}
			}
			helper.assertTrue(count > 10, "$leaves: $count")
		}
		val variant = AlfheimAPI.getTreeVariant(AlfheimBlocks.auroraDirt, 0)
		helper.assertTrue(variant?.getWood(AlfheimBlocks.auroraDirt, 0) === AlfheimBlocks.auroraWood, "aurora tree variant")
		helper.succeed()
	}

	/**
	 * Цвет авроровых блоков — по координатам (`BlockAuroraDirt.getBlockColor`). Числа посчитаны по формуле автора вручную:
	 * (0, 0, 0) — красный 0, «синий» 0 xor 255 = 255, «зелёный» 0; (1, 2, 3) — 64, 112 xor 255 = 143, 112;
	 * (−5, 70, −9) — 960 с битом 256 → 255 − 192 = 63, 2096 xor 255 → 207, −368 → 144
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun auroraColor(helper: GameTestHelper) {
		helper.assertTrue(BlockAuroraDirt.getBlockColor(0, 0, 0) == 0x00FF00, "color at 0, 0, 0: ${Integer.toHexString(BlockAuroraDirt.getBlockColor(0, 0, 0))}")
		helper.assertTrue(BlockAuroraDirt.getBlockColor(1, 2, 3) == 0x408F70, "color at 1, 2, 3: ${Integer.toHexString(BlockAuroraDirt.getBlockColor(1, 2, 3))}")
		helper.assertTrue(BlockAuroraDirt.getBlockColor(-5, 70, -9) == 0x3FCF90, "color at -5, 70, -9: ${Integer.toHexString(BlockAuroraDirt.getBlockColor(-5, 70, -9))}")
		helper.succeed()
	}

	/** Радужная листва: бит опадания автора — 0x1 (`decayBit`), поставленная игроком (persistent) не опадает */
	@JvmStatic
	@GameTest(template = "empty")
	fun rainbowLeavesDecay(helper: GameTestHelper) {
		val leaves = AlfheimBlocks.rainbowLeaves as BlockLeavesMod
		helper.assertTrue(leaves.decayBit() == 0x1, "rainbow leaves decay bit")
		for (y in 1..3) helper.setBlock(BlockPos(0, y, 0), leaves.defaultBlockState())
		helper.setBlock(BlockPos(0, 4, 0), leaves.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true))
		for (y in 1..4) {
			val pos = helper.absolutePos(BlockPos(0, y, 0))
			leaves.updateTick(helper.level, pos.x, pos.y, pos.z, RandomSource.create(y.toLong()))
		}
		for (y in 1..3) helper.assertBlockNotPresent(leaves, BlockPos(0, y, 0))
		helper.assertBlockPresent(leaves, BlockPos(0, 4, 0))
		helper.killAllEntities()
		helper.succeed()
	}

	/**
	 * Лут, теги, горение, топливо и плодородие: листва — саженец ириса с шансом 1/20, двойная плита — две плиты; радужная и
	 * авроровая земля плодородна (`isFertile`), цветная — нет; авроровая листва не горит — её нет в `registerBurnables`
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun rainbowAuroraLootAndTags(helper: GameTestHelper) {
		fun drops(pos: BlockPos) = Block.getDrops(helper.getBlockState(pos), helper.level, helper.absolutePos(pos), null, null, ItemStack.EMPTY)

		val pos = BlockPos(1, 1, 1)
		for (leaves in listOf(AlfheimBlocks.rainbowLeaves, AlfheimBlocks.auroraLeaves)) {
			helper.setBlock(pos, leaves)
			var saplings = 0
			repeat(400) {
				for (stack in drops(pos)) {
					helper.assertTrue(stack.item === AlfheimBlocks.irisSapling.asItem() && stack.count == 1, "$leaves drop $stack")
					saplings++
				}
			}
			helper.assertTrue(saplings in 5..40, "saplings from 400 $leaves: $saplings")
		}
		for (slab in listOf(AlfheimBlocks.rainbowSlab, AlfheimBlocks.auroraSlab)) {
			helper.setBlock(pos, slab.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE))
			val double = drops(pos)
			helper.assertTrue(double.size == 1 && double[0].item === slab.asItem() && double[0].count == 2, "double $slab drops $double")
		}

		for (wood in listOf(AlfheimBlocks.rainbowWood, AlfheimBlocks.auroraWood)) helper.assertTrue(wood.defaultBlockState().`is`(BlockTags.LOGS), "$wood → minecraft:logs")
		for (leaves in listOf(AlfheimBlocks.rainbowLeaves, AlfheimBlocks.auroraLeaves)) helper.assertTrue(leaves.defaultBlockState().`is`(BlockTags.LEAVES), "$leaves → minecraft:leaves")
		for (planks in listOf(AlfheimBlocks.rainbowPlanks, AlfheimBlocks.auroraPlanks)) helper.assertTrue(planks.defaultBlockState().`is`(BlockTags.PLANKS), "$planks → minecraft:planks")
		for (slab in listOf(AlfheimBlocks.rainbowSlab, AlfheimBlocks.auroraSlab)) helper.assertTrue(slab.defaultBlockState().`is`(BlockTags.WOODEN_SLABS), "$slab → minecraft:wooden_slabs")
		for (stairs in listOf(AlfheimBlocks.rainbowStairs, AlfheimBlocks.auroraStairs)) helper.assertTrue(stairs.defaultBlockState().`is`(BlockTags.WOODEN_STAIRS), "$stairs → minecraft:wooden_stairs")

		for (dirt in listOf(AlfheimBlocks.rainbowDirt, AlfheimBlocks.auroraDirt)) {
			helper.assertTrue(dirt.defaultBlockState().isFertile(helper.level, BlockPos.ZERO), "$dirt is fertile")
			helper.assertTrue(dirt.defaultBlockState().canSustainPlant(helper.level, BlockPos.ZERO, Direction.UP, Blocks.WHEAT as CropBlock), "$dirt sustains crops")
		}
		helper.assertTrue(!AlfheimBlocks.irisDirt[0].defaultBlockState().isFertile(helper.level, BlockPos.ZERO), "colored dirt is not fertile")

		fun fire(block: Block, encouragement: Int, flammability: Int) {
			val state = block.defaultBlockState()
			helper.assertTrue(state.getFireSpreadSpeed(helper.level, BlockPos.ZERO, Direction.UP) == encouragement, "$block encouragement")
			helper.assertTrue(state.getFlammability(helper.level, BlockPos.ZERO, Direction.UP) == flammability, "$block flammability")
		}
		fire(AlfheimBlocks.rainbowLeaves, 30, 60)
		fire(AlfheimBlocks.rainbowPlanks, 5, 20)
		fire(AlfheimBlocks.rainbowWood, 5, 5)
		fire(AlfheimBlocks.auroraLeaves, 0, 0)
		fire(AlfheimBlocks.auroraStairs, 5, 20)
		fire(AlfheimBlocks.auroraWood, 5, 5)

		helper.assertTrue(ForgeHooks.getBurnTime(ItemStack(AlfheimBlocks.rainbowSlab), RecipeType.SMELTING) == 300, "rainbow slab fuel")
		helper.assertTrue(ForgeHooks.getBurnTime(ItemStack(AlfheimBlocks.auroraSlab), RecipeType.SMELTING) == 300, "aurora slab fuel")

		// взрывоустойчивость — как у автора: твёрдость, записанная прямо в поле, её не задаёт (BUGS.md, B-020); авроровое
		// бревно — через setHardness(2): 10 / 5; плита — setResistance(10): 30 / 5
		helper.assertTrue(AlfheimBlocks.rainbowDirt.explosionResistance == 0f && AlfheimBlocks.rainbowWood.explosionResistance == 0f, "rainbow dirt and wood resistance")
		helper.assertTrue(AlfheimBlocks.auroraWood.explosionResistance == 2f, "aurora wood resistance")
		helper.assertTrue(AlfheimBlocks.rainbowSlab.explosionResistance == 6f, "rainbow slab resistance")
		helper.succeed()
	}

	/** Рецепты партии 8б — числа и ингредиенты из `AlfheimRecipes`; переплавка бревна — древесный уголь */
	@JvmStatic
	@GameTest(template = "empty")
	fun rainbowAuroraRecipes(helper: GameTestHelper) {
		fun result(path: String) = helper.level.recipeManager.byKey(ResourceLocation(MODID, path)).orElse(null)?.getResultItem(helper.level.registryAccess()) ?: ItemStack.EMPTY
		fun check(path: String, block: Block, count: Int) {
			val stack = result(path)
			helper.assertTrue(stack.item === block.asItem() && stack.count == count, "$MODID:$path → $stack, expected $count × $block")
		}
		check("rainbow_dirt", AlfheimBlocks.rainbowDirt, 8)
		check("aurora_dirt", AlfheimBlocks.auroraDirt, 8)
		check("rainbow_planks", AlfheimBlocks.rainbowPlanks, 1)
		check("rainbow_planks_2", AlfheimBlocks.rainbowPlanks, 4)
		check("aurora_planks", AlfheimBlocks.auroraPlanks, 1)
		check("aurora_planks_2", AlfheimBlocks.auroraPlanks, 4)
		check("rainbow_planks_slab", AlfheimBlocks.rainbowSlab, 6)
		check("aurora_planks_slab17", AlfheimBlocks.auroraSlab, 6)
		check("rainbow_planks_stairs", AlfheimBlocks.rainbowStairs, 4)
		check("aurora_planks_stairs17", AlfheimBlocks.auroraStairs, 4)
		for (wood in listOf(AlfheimBlocks.rainbowWood, AlfheimBlocks.auroraWood)) {
			val smelted = helper.level.recipeManager.getRecipeFor(RecipeType.SMELTING, SimpleContainer(ItemStack(wood)), helper.level).orElse(null)
			helper.assertTrue(smelted != null && smelted.getResultItem(helper.level.registryAccess()).item === Items.CHARCOAL && smelted.experience == 0.15f, "$wood → charcoal")
		}
		helper.succeed()
	}
}
