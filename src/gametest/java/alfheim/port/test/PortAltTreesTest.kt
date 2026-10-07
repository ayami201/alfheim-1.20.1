package alfheim.port.test

import alfheim.api.AlfheimAPI
import alfheim.api.ModInfo.MODID
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.AlfheimFluffBlocks
import alfheim.common.block.alt.BlockAltLeaves
import alfheim.common.block.base.BlockLeavesMod
import alfheim.common.block.colored.BlockColoredSapling
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenFoodMetas
import alfheim.port.legacy.*
import alfheim.port.legacy.botania.altGrass
import alfheim.port.registry.LegacyIds
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.tags.ItemTags
import net.minecraft.util.RandomSource
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.inventory.TransientCraftingContainer
import net.minecraft.world.item.*
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.properties.Half
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate
import vazkii.botania.common.block.BotaniaBlocks

/**
 * КТ-2, партия 8г-1: альтернативные деревья — брёвна, доски, плиты, ступеньки и листва деревьев травы Botania (сухое,
 * золотое, яркое, опалённое, пропитанное, мутировавшее), Иггдрасиля и древа мечтаний. Числа и правила — из классов
 * автора (`alfheim.common.block.alt`) и правил 1.7.10 (MAPPING.md)
 */
@GameTestHolder(MODID)
@PrefixGameTestTemplate(false)
object PortAltTreesTest {

	/** Бревно Иггдрасиля — набор 1, вариант 2; листва, доски, плита и ступеньки Иггдрасиля — вид 6 (`BlockAltLeaves.yggMeta`) */
	private const val YGG = 6

	/**
	 * Варианты metadata — блоки под именами `<имя автора><номер>` (SPEC, Р-5): брёвна двух наборов по 4, доски, плиты и
	 * ступеньки 7 видов, листва 8 видов — 37 блоков. Двойная плита 1.7.10 с metadata варианта — состояние type=double
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun altTreeIds(helper: GameTestHelper) {
		val ids = (0..1).flatMap { set -> (0..3).map { "alt_wood$set$it" } } + (0..6).flatMap { listOf("alt_planks$it", "alt_planks_slab$it", "alt_planks_stairs$it") } + (0..7).map { "alt_leaves$it" }
		helper.assertTrue(ids.size == 37, "ids: ${ids.size}")
		for (id in ids) helper.assertTrue(BuiltInRegistries.BLOCK.containsKey(ResourceLocation(MODID, id)), "$MODID:$id is not registered")
		helper.assertTrue(BlockAltLeaves.yggMeta == YGG, "yggMeta ${BlockAltLeaves.yggMeta}")
		for (meta in 0..6) {
			val full = LegacyIds.block("$MODID:altPlanksSlabFull", meta)
			helper.assertTrue(full?.id == ResourceLocation(MODID, "alt_planks_slab$meta") && full.state == mapOf("type" to "double"), "legacy id altPlanksSlabFull:$meta → $full")
		}
		helper.assertTrue(LegacyIds.block("$MODID:altWood1", 2)?.id == ResourceLocation(MODID, "alt_wood12"), "legacy id altWood1:2")
		helper.assertTrue(LegacyIds.block("$MODID:altLeaves", 7)?.id == ResourceLocation(MODID, "alt_leaves7"), "legacy id altLeaves:7")
		helper.succeed()
	}

	/**
	 * Иггдрасиль: бревно и листву не сломать (твёрдость −1) и не взорвать (`Float.MAX_VALUE`), доски, плита и ступеньки —
	 * твёрдость 100 и взрывоустойчивость 1000; ничто из Иггдрасиля не горит. Остальное дерево — твёрдость 2 и
	 * взрывоустойчивость 0 (твёрдость записана прямо в поле, BUGS.md), листва — 0,2; горит, как у автора в
	 * `registerBurnables`. Ступеньки из других досок, перевёрнутые к югу (metadata 6), — твёрдость 100 (BUGS.md)
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun altTreeProperties(helper: GameTestHelper) {
		val b = AlfheimBlocks
		val explosion = Explosion(helper.level, null, 0.0, 0.0, 0.0, 1f, false, Explosion.BlockInteraction.KEEP)
		fun check(block: Block, hardness: Float, resistance: Float, encouragement: Int, flammability: Int) {
			val state = block.defaultBlockState()
			val name = BuiltInRegistries.BLOCK.getKey(block)
			helper.assertTrue(state.getDestroySpeed(helper.level, BlockPos.ZERO) == hardness, "$name hardness ${state.getDestroySpeed(helper.level, BlockPos.ZERO)}, expected $hardness")
			helper.assertTrue(state.getExplosionResistance(helper.level, BlockPos.ZERO, explosion) == resistance, "$name resistance ${state.getExplosionResistance(helper.level, BlockPos.ZERO, explosion)}, expected $resistance")
			helper.assertTrue(state.getFireSpreadSpeed(helper.level, BlockPos.ZERO, Direction.UP) == encouragement, "$name encouragement")
			helper.assertTrue(state.getFlammability(helper.level, BlockPos.ZERO, Direction.UP) == flammability, "$name flammability")
		}
		for (wood in b.altWood0 + b.altWood1) if (wood === b.altWood1[2]) check(wood, -1f, Float.MAX_VALUE, 0, 0) else check(wood, 2f, 0f, 5, 5)
		for (i in 0..6) {
			if (i == YGG) {
				check(b.altPlanks[i], 100f, 1000f, 0, 0)
				check(b.altSlabs[i], 100f, 1000f, 0, 0)
				check(b.altStairs[i], 100f, 1000f, 0, 0)
			} else {
				check(b.altPlanks[i], 2f, 0f, 5, 20)
				check(b.altSlabs[i], 2f, 0f, 5, 20)
				check(b.altStairs[i], 2f, 0f, 5, 20)
			}
		}
		for (i in 0..7) if (i == YGG) check(b.altLeaves[i], -1f, Float.MAX_VALUE, 0, 0) else check(b.altLeaves[i], 0.2f, 0.2f, 30, 60)
		helper.assertTrue(b.altWood1[0].defaultBlockState().soundType == SoundType.STONE && b.altPlanks[0].defaultBlockState().soundType == SoundType.WOOD, "sounds")

		// перевёрнутые ступеньки к югу — metadata 6: доски видят в ней свой вид Иггдрасиля
		val stairs = b.altStairs[1].defaultBlockState()
		fun hardness(facing: Direction, half: Half) = stairs.setValue(StairBlock.FACING, facing).setValue(StairBlock.HALF, half).getDestroySpeed(helper.level, BlockPos.ZERO)
		helper.assertTrue(hardness(Direction.SOUTH, Half.TOP) == 100f, "upside down stairs facing south: ${hardness(Direction.SOUTH, Half.TOP)}")
		for (facing in Direction.Plane.HORIZONTAL) for (half in Half.values())
			if (facing != Direction.SOUTH || half != Half.TOP) helper.assertTrue(hardness(facing, half) == 2f, "stairs $facing $half: ${hardness(facing, half)}")

		// свет: альтернативная листва его не задерживает (setLightOpacity(0)), прочая листва автора — на 1
		val alt = b.altLeaves[0].defaultBlockState()
		helper.assertTrue(alt.getLightBlock(helper.level, BlockPos.ZERO) == 0 && alt.propagatesSkylightDown(helper.level, BlockPos.ZERO), "alt leaves let the light through")
		val iris = b.irisLeaves0[0].defaultBlockState()
		helper.assertTrue(iris.getLightBlock(helper.level, BlockPos.ZERO) == 1 && !iris.propagatesSkylightDown(helper.level, BlockPos.ZERO), "iris leaves dim the light by 1")
		helper.succeed()
	}

	/**
	 * Ore Dictionary автора → теги: бревно Иггдрасиля не держит листву (не в `minecraft:logs`), его листва — не листва
	 * (не в `minecraft:leaves`); ступеньки Иггдрасиля — не `stairWood`, а доски и плита — в своих тегах. Бревно мечтаний —
	 * `logDeamwood`. Деревянный блок горит в печи 300 тиков — время обработчиков автора (у Иггдрасиля — Int.MAX_VALUE / 13
	 * и меньше) печь 1.7.10 не спрашивала (BUGS.md)
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun altTreeTagsAndFuel(helper: GameTestHelper) {
		val b = AlfheimBlocks
		for (wood in b.altWood0 + b.altWood1) {
			val log = wood.defaultBlockState().`is`(BlockTags.LOGS)
			helper.assertTrue(log == (wood !== b.altWood1[2]), "$wood in minecraft:logs: $log")
			helper.assertTrue(wood.canSustainLeaves(helper.level, 0, 0, 0) == log, "$wood sustains leaves")
		}
		for (i in 0..7) {
			val leaves = b.altLeaves[i].defaultBlockState().`is`(BlockTags.LEAVES)
			helper.assertTrue(leaves == (i != YGG), "alt leaves $i in minecraft:leaves: $leaves")
			helper.assertTrue((b.altLeaves[i] as BlockAltLeaves).isLeaves(null, 0, 0, 0) == (i != YGG), "alt leaves $i isLeaves")
		}
		for (i in 0..6) {
			helper.assertTrue(b.altPlanks[i].defaultBlockState().`is`(BlockTags.PLANKS), "alt planks $i → minecraft:planks")
			helper.assertTrue(b.altSlabs[i].defaultBlockState().`is`(BlockTags.WOODEN_SLABS), "alt slab $i → minecraft:wooden_slabs")
			helper.assertTrue(b.altStairs[i].defaultBlockState().`is`(BlockTags.WOODEN_STAIRS) == (i != YGG), "alt stairs $i → minecraft:wooden_stairs")
		}
		val dreamwood = ItemTags.create(ResourceLocation(MODID, "log_deamwood"))
		helper.assertTrue(ItemStack(b.altWood1[3]).`is`(dreamwood) && !ItemStack(b.altWood1[2]).`is`(dreamwood), "dreamwood log → $MODID:log_deamwood")

		for (block in listOf(b.altWood1[2], b.altPlanks[YGG], b.altSlabs[YGG], b.altStairs[YGG], b.altWood0[1], b.altPlanks[2], b.altSlabs[3], b.altStairs[4]))
			helper.assertTrue(ForgeHooks.getBurnTime(ItemStack(block), RecipeType.SMELTING) == 300, "$block fuel ${ForgeHooks.getBurnTime(ItemStack(block), RecipeType.SMELTING)}")
		helper.succeed()
	}

	/**
	 * Опадание (`BlockAltLeaves.getDecayRange`, `canDecay`): листва мечтаний держится за бревно в 8 шагах по листве,
	 * прочая — в 4; листва Иггдрасиля не опадает совсем
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun altLeavesDecay(helper: GameTestHelper) {
		fun column(x: Int, leaves: Block, height: Int) {
			helper.setBlock(BlockPos(x, 1, 0), AlfheimBlocks.altWood0[0])
			for (y in 2..height + 1) helper.setBlock(BlockPos(x, y, 0), leaves)
		}
		fun tick(x: Int, y: Int, leaves: Block) {
			val pos = helper.absolutePos(BlockPos(x, y, 0))
			(leaves as BlockLeavesMod).updateTick(helper.level, pos.x, pos.y, pos.z, RandomSource.create(y.toLong()))
		}
		val dream = AlfheimBlocks.altLeaves[7]
		val dry = AlfheimBlocks.altLeaves[0]
		val ygg = AlfheimBlocks.altLeaves[YGG]
		column(0, dream, 9)
		column(2, dry, 5)
		for (y in 10 downTo 2) tick(0, y, dream)
		for (y in 2..9) helper.assertBlockPresent(dream, BlockPos(0, y, 0))
		helper.assertBlockNotPresent(dream, BlockPos(0, 10, 0))
		for (y in 6 downTo 2) tick(2, y, dry)
		for (y in 2..5) helper.assertBlockPresent(dry, BlockPos(2, y, 0))
		helper.assertBlockNotPresent(dry, BlockPos(2, 6, 0))

		helper.setBlock(BlockPos(4, 2, 0), ygg)
		tick(4, 2, ygg)
		helper.assertBlockPresent(ygg, BlockPos(4, 2, 0))
		helper.killAllEntities()
		helper.succeed()
	}

	/**
	 * Лут листвы: листва травы Botania без ножниц иногда роняет саженец ириса; листва мечтаний — вишню мечтаний с шансом
	 * 1/100 (`func_150124_c`), с ножницами — себя и так же вишню, с шёлковым касанием — только себя; саженца древа мечтаний
	 * пока нет (партия 8г-2). Листва Иггдрасиля без ножниц не роняет ничего
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun altLeavesLoot(helper: GameTestHelper) {
		fun drops(pos: BlockPos, tool: ItemStack) = Block.getDrops(helper.getBlockState(pos), helper.level, helper.absolutePos(pos), null, null, tool)
		val pos = BlockPos(1, 1, 1)
		val cherry = AlfheimItems.elvenFood[ElvenFoodMetas.DreamCherry.I]
		val dream = AlfheimBlocks.altLeaves[7]
		helper.setBlock(pos, dream)
		var cherries = 0
		repeat(3000) {
			for (stack in drops(pos, ItemStack.EMPTY)) {
				helper.assertTrue(stack.item === cherry && stack.count == 1, "dreamwood leaves drop $stack")
				cherries++
			}
		}
		helper.assertTrue(cherries in 10..60, "cherries from 3000 dreamwood leaves: $cherries")
		val silkTouch = ItemStack(Items.DIAMOND_PICKAXE).also { it.enchant(Enchantments.SILK_TOUCH, 1) }
		repeat(300) { helper.assertTrue(drops(pos, silkTouch).map { it.item } == listOf(dream.asItem()), "dreamwood leaves with silk touch") }
		repeat(300) {
			val sheared = drops(pos, ItemStack(Items.SHEARS))
			helper.assertTrue(sheared.count { it.item === dream.asItem() } == 1 && sheared.all { it.item === dream.asItem() || it.item === cherry }, "sheared dreamwood leaves drop $sheared")
		}

		helper.setBlock(pos, AlfheimBlocks.altLeaves[3])
		val saplings = (1..400).sumOf { drops(pos, ItemStack.EMPTY).count { stack -> stack.item === AlfheimBlocks.irisSapling.asItem() } }
		helper.assertTrue(saplings in 5..40, "iris saplings from 400 scorched leaves: $saplings")
		helper.setBlock(pos, AlfheimBlocks.altLeaves[YGG])
		repeat(100) { helper.assertTrue(drops(pos, ItemStack.EMPTY).isEmpty(), "yggdrasil leaves drop nothing") }
		helper.succeed()
	}

	/**
	 * Саженец ириса на траве Botania растёт деревом её вида (`AlfheimBlocks.registerFlora`): трава 0–3 — бревно
	 * `altWood0` и листва 0–3, трава 4–5 (пропитанная, мутировавшая) — `altWood1` 0–1 и листва 4–5
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun altTreesOnBotaniaGrass(helper: GameTestHelper) {
		for ((meta, grass) in altGrass.withIndex()) {
			val variant = AlfheimAPI.getTreeVariant(grass, meta)
			helper.assertTrue(variant != null, "no tree for Botania alt grass $meta")
			val wood = if (meta < 4) AlfheimBlocks.altWood0[meta] else AlfheimBlocks.altWood1[meta - 4]
			helper.assertTrue(variant!!.getWood(grass, meta) === wood, "wood of alt grass $meta")
			helper.assertTrue(variant.getLeaves(grass, meta) === AlfheimBlocks.altLeaves[meta], "leaves of alt grass $meta")
		}

		val soil = BlockPos(0, 0, 0)
		helper.setBlock(soil, BotaniaBlocks.infusedGrass)
		helper.setBlock(soil.above(), AlfheimBlocks.irisSapling)
		val sapling = AlfheimBlocks.irisSapling as BlockColoredSapling
		val pos = helper.absolutePos(soil.above())
		val random = RandomSource.create(1)
		sapling.markOrGrowMarked(helper.level, pos.x, pos.y, pos.z, random)
		sapling.markOrGrowMarked(helper.level, pos.x, pos.y, pos.z, random)
		helper.assertBlockPresent(AlfheimBlocks.altWood1[0], soil.above())
		var leaves = 0
		for (p in BlockPos.betweenClosed(-2, 1, -2, 2, 10, 2)) {
			val state = helper.getBlockState(p)
			if (state.`is`(BlockTags.LEAVES)) {
				helper.assertTrue(state.block === AlfheimBlocks.altLeaves[4], "leaves ${state.block} at $p")
				leaves++
			}
		}
		helper.assertTrue(leaves > 10, "leaves: $leaves")
		helper.succeed()
	}

	/**
	 * Рецепты автора: доски из бревна (4 шт., у бревна Иггдрасиля — доски Иггдрасиля), из двух плит (1), плиты (6) и
	 * ступеньки (4) из досок, декор Иггдрасиля из его досок (из 4 досок квадратом — верстак, как в 1.7.10); бревно в
	 * печи — древесный уголь, кроме бревна Иггдрасиля
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun altTreeRecipes(helper: GameTestHelper) {
		fun result(path: String) = helper.level.recipeManager.byKey(ResourceLocation(MODID, path)).orElse(null)?.getResultItem(helper.level.registryAccess()) ?: ItemStack.EMPTY
		fun check(path: String, block: Block, count: Int) {
			val stack = result(path)
			helper.assertTrue(stack.item === block.asItem() && stack.count == count, "$MODID:$path → $stack, expected $count × $block")
		}
		for (i in 0..6) {
			check("alt_planks${i}_2", AlfheimBlocks.altPlanks[i], 4)
			check("alt_planks$i", AlfheimBlocks.altPlanks[i], 1)
			check("alt_planks_slab$i", AlfheimBlocks.altSlabs[i], 6)
			check("alt_planks_stairs$i", AlfheimBlocks.altStairs[i], 4)
		}
		check("wisdomwood0", AlfheimFluffBlocks.yggDecor[0], 1)
		check("wisdomwood2", AlfheimFluffBlocks.yggDecor[2], 4)
		// 4 доски Иггдрасиля квадратом — верстак, как в 1.7.10: рецепт декора автора тот перекрывал (BUGS.md)
		val square = helper.level.recipeManager.getRecipeFor(RecipeType.CRAFTING, craftingGrid(*Array(2) { ItemStack(AlfheimBlocks.altPlanks[YGG]) }, null, *Array(2) { ItemStack(AlfheimBlocks.altPlanks[YGG]) }), helper.level).orElse(null)
		helper.assertTrue(square?.getResultItem(helper.level.registryAccess())?.item === Items.CRAFTING_TABLE, "4 yggdrasil planks → crafting table: ${square?.id}")
		val yggLog = helper.level.recipeManager.getRecipeFor(RecipeType.CRAFTING, craftingGrid(ItemStack(AlfheimBlocks.altWood1[2])), helper.level).orElse(null)
		helper.assertTrue(yggLog?.getResultItem(helper.level.registryAccess())?.item === AlfheimBlocks.altPlanks[YGG].asItem(), "yggdrasil log → yggdrasil planks")

		fun smelted(block: Block) = helper.level.recipeManager.getRecipeFor(RecipeType.SMELTING, SimpleContainer(ItemStack(block)), helper.level).orElse(null)
		for (wood in AlfheimBlocks.altWood0 + AlfheimBlocks.altWood1) {
			val recipe = smelted(wood)
			if (wood === AlfheimBlocks.altWood1[2]) helper.assertTrue(recipe == null, "yggdrasil wood is not smelted")
			else helper.assertTrue(recipe != null && recipe.getResultItem(helper.level.registryAccess()).item === Items.CHARCOAL && recipe.experience == 0.15f, "$wood → charcoal")
		}
		helper.succeed()
	}

	/** Сетка верстака 3×3 без меню; [stacks] — слоты по строкам */
	private fun craftingGrid(vararg stacks: ItemStack?): CraftingContainer {
		val menu = object: AbstractContainerMenu(null, -1) {
			override fun quickMoveStack(player: Player, index: Int): ItemStack = ItemStack.EMPTY
			override fun stillValid(player: Player) = true
		}
		return TransientCraftingContainer(menu, 3, 3).apply { stacks.forEachIndexed { i, stack -> setItem(i, stack ?: ItemStack.EMPTY) } }
	}
}
