package alfheim.port.test

import alfheim.api.ModInfo.MODID
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.BlockTreeBerry
import alfheim.common.block.colored.BlockColoredSapling
import alfheim.common.block.magtrees.calico.IExplosionDampener
import alfheim.common.block.magtrees.sealing.EventHandlerSealingOak
import alfheim.common.block.magtrees.sealing.ISoundSilencer
import alfheim.common.block.tile.TileLightningTreeTop
import alfheim.common.block.tile.TileTreeCook
import alfheim.common.block.tile.TileTreeWind
import alfheim.common.entity.FakeLightning
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenFoodMetas
import alfheim.common.item.material.ElvenResourcesMetas.*
import alfheim.port.legacy.*
import alfheim.port.registry.LegacyIds
import com.mojang.authlib.GameProfile
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.*
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.util.RandomSource
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.*
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.properties.SlabType
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.common.util.FakePlayerFactory
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate
import java.util.*

/**
 * КТ-2, партии 8в-1, 8в-2 и 8в-3: блоки шести магических деревьев — барьерного, кошачьего, грозового, адского, уплотнённого
 * деревьев и схемодрева — и их механики, саженцы и рост деревьев, ягоды, блок-сущности сердцевин. Числа и правила — из классов автора (`alfheim.common.block.magtrees`) и правил 1.7.10
 * (MAPPING.md). Сердцевины действуют на всё вокруг (толкают существ, жарят еду, перехватывают молнии), поэтому их тесты
 * — в своей партии тестов (`heart_wood`), а сердцевина убирается в конце теста
 */
@GameTestHolder(MODID)
@PrefixGameTestTemplate(false)
object PortMagicTreesTest {

	private val trees = listOf("barrier", "calico", "circuit", "lightning", "nether", "sealing")

	/**
	 * id — имена автора; бревно барьерного, грозового и адского деревьев — варианты 0 (бревно) и 1 (сердцевина), у
	 * остальных деревьев сердцевины нет. Двойная плита 1.7.10 — состояние type=double одинарной
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun magicTreeIds(helper: GameTestHelper) {
		val ids = trees.flatMap { listOf("${it}_leaves", "${it}_planks", "${it}_planks_slab", "${it}_planks_stairs") } +
			listOf("calico_wood", "circuit_wood", "sealing_wood") + listOf("barrier", "lightning", "nether").flatMap { listOf("${it}_wood0", "${it}_wood1") }
		helper.assertTrue(ids.size == 33, "ids: ${ids.size}")
		for (id in ids) helper.assertTrue(BuiltInRegistries.BLOCK.containsKey(ResourceLocation(MODID, id)), "$MODID:$id is not registered")
		for (meta in 0..1) helper.assertTrue(LegacyIds.block("$MODID:lightningWood", meta)?.id == ResourceLocation(MODID, "lightning_wood$meta"), "legacy id lightningWood:$meta")
		val full = LegacyIds.block("$MODID:netherPlanksSlabFull")
		helper.assertTrue(full?.id == ResourceLocation(MODID, "nether_planks_slab") && full.state == mapOf("type" to "double"), "legacy id netherPlanksSlabFull: $full")
		helper.succeed()
	}

	/**
	 * Твёрдость, взрывоустойчивость, звук, свет и огонь — как у автора. Брёвна и доски: твёрдость записана прямо в
	 * поле — взрывоустойчивость 0 (BUGS.md, B-025); звук брёвен, кроме уплотнённого, не задан — камень (B-029); плита —
	 * setResistance(10): 30 / 5. Адские блоки светятся (0,5 → 7) и не горят, адское бревно — вечный огонь; барьерные
	 * блоки не горят (B-028)
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun magicTreeProperties(helper: GameTestHelper) {
		val b = AlfheimBlocks
		fun check(block: Block, hardness: Float, resistance: Float, sound: SoundType, light: Int) {
			val state = block.defaultBlockState()
			val name = BuiltInRegistries.BLOCK.getKey(block)
			helper.assertTrue(state.getDestroySpeed(helper.level, BlockPos.ZERO) == hardness, "$name hardness ${state.getDestroySpeed(helper.level, BlockPos.ZERO)}, expected $hardness")
			helper.assertTrue(block.explosionResistance == resistance, "$name resistance ${block.explosionResistance}, expected $resistance")
			helper.assertTrue(state.soundType == sound, "$name sound")
			helper.assertTrue(state.lightEmission == light, "$name light ${state.lightEmission}, expected $light")
		}
		for (wood in b.barrierWood + b.lightningWood + b.calicoWood + b.circuitWood) check(wood, 2f, 0f, SoundType.STONE, if (wood === b.circuitWood) 8 else 0)
		b.netherWood.forEach { check(it, 2f, 0f, SoundType.STONE, 7) }
		check(b.sealingWood, 2f, 0f, SoundType.WOOL, 0)
		for (planks in listOf(b.barrierPlanks, b.calicoPlanks, b.lightningPlanks)) check(planks, 2f, 0f, SoundType.WOOD, 0)
		check(b.circuitPlanks, 2f, 0f, SoundType.WOOD, 8)
		check(b.netherPlanks, 2f, 0f, SoundType.WOOD, 7)
		check(b.sealingPlanks, 2f, 0f, SoundType.WOOL, 0)
		check(b.circuitSlabs, 2f, 6f, SoundType.WOOD, 8)
		check(b.netherSlabs, 2f, 6f, SoundType.WOOD, 7)
		check(b.sealingSlabs, 2f, 6f, SoundType.WOOL, 0)
		check(b.circuitStairs, 2f, 0f, SoundType.WOOD, 8)
		check(b.netherStairs, 2f, 0f, SoundType.WOOD, 7)
		check(b.sealingStairs, 2f, 0f, SoundType.WOOL, 0)
		check(b.circuitLeaves, 0.2f, 0.2f, SoundType.GRASS, 8)
		check(b.sealingLeaves, 0.2f, 0.2f, SoundType.WOOL, 0)

		fun fire(block: Block, encouragement: Int, flammability: Int) {
			val state = block.defaultBlockState()
			helper.assertTrue(state.getFireSpreadSpeed(helper.level, BlockPos.ZERO, Direction.UP) == encouragement, "$block encouragement")
			helper.assertTrue(state.getFlammability(helper.level, BlockPos.ZERO, Direction.UP) == flammability, "$block flammability")
		}
		for (tree in listOf(b.calicoLeaves, b.circuitLeaves, b.lightningLeaves, b.sealingLeaves)) fire(tree, 30, 60)
		for (planks in listOf(b.calicoPlanks, b.circuitSlabs, b.lightningStairs, b.sealingPlanks)) fire(planks, 5, 20)
		for (wood in listOf(b.calicoWood, b.circuitWood, b.lightningWood[1], b.sealingWood)) fire(wood, 5, 5)
		for (block in listOf(b.netherLeaves, b.netherPlanks, b.netherWood[0], b.barrierLeaves, b.barrierPlanks, b.barrierWood[0])) fire(block, 0, 0)

		val pos = BlockPos(0, 1, 0)
		helper.setBlock(pos, b.netherWood[0])
		helper.assertTrue(helper.getBlockState(pos).isFireSource(helper.level, helper.absolutePos(pos), Direction.UP), "nether wood is a fire source")
		helper.setBlock(pos, b.barrierWood[0])
		helper.assertTrue(!helper.getBlockState(pos).isFireSource(helper.level, helper.absolutePos(pos), Direction.UP), "barrier wood is not a fire source")
		helper.succeed()
	}

	/**
	 * Лут, теги и топливо: сердцевина роняет обычное бревно (`damageDropped` — 0), двойная плита — две плиты, листва с
	 * ножницами — себя (саженец без ножниц — партия 8в-2). Деревянный блок в печи горит 300 тиков: 2000 адских блоков у
	 * автора не срабатывали (B-027)
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun magicTreeLootAndTags(helper: GameTestHelper) {
		val b = AlfheimBlocks
		fun drops(pos: BlockPos, tool: ItemStack) = Block.getDrops(helper.getBlockState(pos), helper.level, helper.absolutePos(pos), null, null, tool)
		fun only(pos: BlockPos, tool: ItemStack, item: Item, count: Int, what: String) {
			val stacks = drops(pos, tool)
			helper.assertTrue(stacks.size == 1 && stacks[0].item === item && stacks[0].count == count, "$what drops $stacks")
		}
		val pos = BlockPos(1, 1, 1)
		for (wood in listOf(b.barrierWood, b.lightningWood, b.netherWood)) for (variant in wood) {
			helper.setBlock(pos, variant)
			only(pos, ItemStack.EMPTY, wood[0].asItem(), 1, "$variant")
		}
		helper.setBlock(pos, b.circuitSlabs.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE))
		only(pos, ItemStack.EMPTY, b.circuitSlabs.asItem(), 2, "double circuit slab")
		helper.setBlock(pos, b.calicoLeaves)
		only(pos, ItemStack(Items.SHEARS), b.calicoLeaves.asItem(), 1, "calico leaves with shears")
		helper.assertTrue(drops(pos, ItemStack.EMPTY).isEmpty(), "calico leaves without shears")

		for (wood in b.barrierWood + b.lightningWood + b.netherWood + b.calicoWood + b.circuitWood + b.sealingWood) helper.assertTrue(wood.defaultBlockState().`is`(BlockTags.LOGS), "$wood → minecraft:logs")
		for (tree in trees) {
			fun block(name: String) = BuiltInRegistries.BLOCK.get(ResourceLocation(MODID, name))
			helper.assertTrue(block("${tree}_leaves").defaultBlockState().`is`(BlockTags.LEAVES), "$tree leaves → minecraft:leaves")
			helper.assertTrue(block("${tree}_planks").defaultBlockState().`is`(BlockTags.PLANKS), "$tree planks → minecraft:planks")
			helper.assertTrue(block("${tree}_planks_slab").defaultBlockState().`is`(BlockTags.WOODEN_SLABS), "$tree slab → minecraft:wooden_slabs")
			helper.assertTrue(block("${tree}_planks_stairs").defaultBlockState().`is`(BlockTags.WOODEN_STAIRS), "$tree stairs → minecraft:wooden_stairs")
		}

		for (block in listOf(b.netherPlanks, b.netherWood[0], b.netherSlabs, b.netherStairs, b.circuitPlanks, b.sealingWood))
			helper.assertTrue(ForgeHooks.getBurnTime(ItemStack(block), RecipeType.SMELTING) == 300, "$block fuel ${ForgeHooks.getBurnTime(ItemStack(block), RecipeType.SMELTING)}")
		helper.killAllEntities()
		helper.succeed()
	}

	/**
	 * Рецепты из `AlfheimRecipes`: доски — 4 из бревна (сердцевина — не бревно рецепта без формы), плиты — 6,
	 * ступеньки — 4, грозовая и адская ветви — из двух брёвен. Печь: бревно, и сердцевина тоже, — древесный уголь,
	 * адское — опалённый древесный уголь; грозовые и адские доски — две щепки
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun magicTreeRecipes(helper: GameTestHelper) {
		fun result(path: String) = helper.level.recipeManager.byKey(ResourceLocation(MODID, path)).orElse(null)?.getResultItem(helper.level.registryAccess()) ?: ItemStack.EMPTY
		fun check(path: String, item: Item, count: Int) {
			val stack = result(path)
			helper.assertTrue(stack.item === item && stack.count == count, "$MODID:$path → $stack, expected $count × $item")
		}
		for (tree in trees) {
			fun item(name: String) = BuiltInRegistries.ITEM.get(ResourceLocation(MODID, name))
			check("${tree}_planks", item("${tree}_planks"), 4)
			check("${tree}_planks_slab", item("${tree}_planks_slab"), 6)
			check("${tree}_planks_stairs", item("${tree}_planks_stairs"), 4)
		}
		check("thunderwood_twig", AlfheimItems.elvenResource[ThunderwoodTwig.I], 1)
		check("netherwood_twig", AlfheimItems.elvenResource[NetherwoodTwig.I], 1)
		val planks = helper.level.recipeManager.byKey(ResourceLocation(MODID, "barrier_planks")).orElse(null)!!
		val heart = planks.ingredients.single()
		helper.assertTrue(heart.test(ItemStack(AlfheimBlocks.barrierWood[0])) && !heart.test(ItemStack(AlfheimBlocks.barrierWood[1])), "barrier planks from barrier wood only")

		fun smelt(input: ItemLike, output: Item, count: Int, xp: Float) {
			val recipe = helper.level.recipeManager.getRecipeFor(RecipeType.SMELTING, SimpleContainer(ItemStack(input)), helper.level).orElse(null)
			val stack = recipe?.getResultItem(helper.level.registryAccess())
			helper.assertTrue(stack != null && stack.item === output && stack.count == count && recipe.experience == xp, "$input → $stack")
		}
		for (wood in listOf(AlfheimBlocks.lightningWood[0], AlfheimBlocks.lightningWood[1], AlfheimBlocks.barrierWood[1], AlfheimBlocks.calicoWood, AlfheimBlocks.circuitWood, AlfheimBlocks.sealingWood))
			smelt(wood, Items.CHARCOAL, 1, 0.15f)
		smelt(AlfheimBlocks.netherWood[1], AlfheimItems.elvenResource[NetherwoodCoal.I], 1, 0.15f)
		smelt(AlfheimBlocks.lightningPlanks, AlfheimItems.elvenResource[ThunderwoodSplinters.I], 2, 0.1f)
		smelt(AlfheimBlocks.netherPlanks, AlfheimItems.elvenResource[NetherwoodSplinters.I], 2, 0.1f)
		helper.succeed()
	}

	/**
	 * Схемодрево (`ICircuitBlock`): блок даёт слабый сигнал, равный числу блоков схемодрева подряд над ним, сам сигнал
	 * не проводит и светится (8). Новый блок сверху пересчитывает сигнал всего столба: `onBlockAdded` зовёт
	 * `updateTick` блока ниже, тот будит соседей и идёт ещё ниже; снятый блок — так же (`breakBlock`). Случайные тики —
	 * как в 1.7.10: у досок и бревна есть, у листвы — и у поставленной игроком, у лестницы — нет
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun circuitPower(helper: GameTestHelper) {
		val b = AlfheimBlocks
		val wire = BlockPos(0, 1, 1)
		helper.setBlock(BlockPos(0, 0, 1), Blocks.STONE)
		for (y in 1..3) helper.setBlock(BlockPos(1, y, 1), if (y == 2) b.circuitWood else b.circuitPlanks)
		helper.setBlock(wire, Blocks.REDSTONE_WIRE)
		fun power() = helper.getBlockState(wire).getValue(RedStoneWireBlock.POWER)
		helper.assertTrue(power() == 2, "wire next to a column of 3: ${power()}")
		helper.setBlock(BlockPos(1, 4, 1), b.circuitLeaves)
		helper.assertTrue(power() == 3, "wire after the 4th block on top: ${power()}")
		helper.setBlock(BlockPos(1, 4, 1), Blocks.AIR)
		helper.assertTrue(power() == 2, "wire after the top block is removed: ${power()}")

		val planks = helper.getBlockState(BlockPos(1, 1, 1))
		val abs = helper.absolutePos(BlockPos(1, 1, 1))
		helper.assertTrue(planks.isSignalSource && !planks.isRedstoneConductor(helper.level, abs) && !planks.isSuffocating(helper.level, abs), "circuit planks: signal source, not a conductor")
		val plain = b.barrierPlanks.defaultBlockState()
		helper.assertTrue(!plain.isSignalSource && plain.isRedstoneConductor(helper.level, abs) && plain.isSuffocating(helper.level, abs), "barrier planks: plain block")

		helper.assertTrue(b.circuitPlanks.defaultBlockState().isRandomlyTicking && b.circuitWood.defaultBlockState().isRandomlyTicking, "circuit planks and wood tick")
		helper.assertTrue(b.circuitLeaves.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true).isRandomlyTicking, "placed circuit leaves tick")
		helper.assertTrue(!b.circuitStairs.defaultBlockState().isRandomlyTicking, "circuit stairs do not tick")
		helper.assertTrue(!b.barrierPlanks.defaultBlockState().isRandomlyTicking && !b.calicoPlanks.defaultBlockState().isRandomlyTicking, "barrier and calico planks do not tick")
		helper.assertTrue(!b.barrierLeaves.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true).isRandomlyTicking, "placed barrier leaves do not tick")
		helper.succeed()
	}

	/**
	 * Кошачье дерево (`EventHandlerCalico`): взрыв в 8 блоках от его блока переносится в этот блок и там гаснет — ни
	 * блоки, ни сам блок дерева не рушатся; взрыв в самом блоке дерева тоже гаснет. Отменённый взрыв сервер игрокам не
	 * показывает (`Explosions1710`, миксин `ServerLevelMixin`): запись об отмене забрал сервер
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun calicoExplosion(helper: GameTestHelper) {
		val dirt = BlockPos(1, 1, 0)
		fun boom(at: BlockPos): net.minecraft.world.level.Explosion {
			val pos = helper.absolutePos(at)
			return helper.level.explode(null, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, 2f, Level.ExplosionInteraction.TNT)
		}
		helper.setBlock(dirt, Blocks.DIRT)
		boom(BlockPos(0, 1, 0))
		helper.assertBlockNotPresent(Blocks.DIRT, dirt)

		val calico = BlockPos(0, 1, 3)
		helper.setBlock(dirt, Blocks.DIRT)
		helper.setBlock(calico, AlfheimBlocks.calicoPlanks)
		val explosion = boom(BlockPos(0, 1, 0))
		helper.assertBlockPresent(Blocks.DIRT, dirt)
		helper.assertBlockPresent(AlfheimBlocks.calicoPlanks, calico)
		helper.assertTrue(explosion.toBlow.isEmpty() && !Explosions1710.wasCanceled(explosion), "the canceled explosion")

		boom(calico)
		helper.assertBlockPresent(AlfheimBlocks.calicoPlanks, calico)
		helper.assertBlockPresent(Blocks.DIRT, dirt)
		helper.killAllEntities()
		helper.succeed()
	}

	/**
	 * Уплотнённое дерево (`ISoundSilencer`, `EventHandlerSealingOak.calculateMultiplier`): каждый его блок в 8 блоках
	 * от звука делает звук вдвое тише; без таких блоков рядом громкость та же
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun sealingSilencer(helper: GameTestHelper) {
		val ear = helper.absolutePos(BlockPos(0, 2, 0))
		fun multiplier() = EventHandlerSealingOak.calculateMultiplier(helper.level, ear.x, ear.y, ear.z)
		helper.assertTrue(multiplier() == 1f, "no silencers: ${multiplier()}")
		helper.setBlock(BlockPos(0, 1, 0), AlfheimBlocks.sealingPlanks)
		helper.setBlock(BlockPos(1, 1, 0), AlfheimBlocks.sealingLeaves)
		helper.assertTrue(helper.level.mayContain(ear.x, ear.y, ear.z, EventHandlerSealingOak.MAXRANGE) { it.block is ISoundSilencer }, "section palettes see the silencers")
		helper.assertTrue(multiplier() == 0.25f, "two silencers: ${multiplier()}")
		helper.succeed()
	}

	/**
	 * Саженцы (`Block…Sapling`, партия 8в-2) растут на земле и траве 1.7.10 — по материалу (`canGrowHere`: `ground`,
	 * `grass`), на камне и песке — нет. Дерево (`HeartWoodTreeGen`): ствол из бревна высотой 5–7, под верхушкой —
	 * сердцевина, верхушка и крона — листва, которая опадает; трава под деревом становится землёй
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun magicSaplingGrowsTree(helper: GameTestHelper) {
		val sapling = AlfheimBlocks.barrierSapling as BlockColoredSapling
		for (block in listOf(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.PODZOL, Blocks.FARMLAND, Blocks.MYCELIUM, Blocks.HAY_BLOCK, Blocks.ROOTED_DIRT, AlfheimBlocks.irisDirt[0]))
			helper.assertTrue(sapling.canGrowHere(block), "grows on $block")
		for (block in listOf(Blocks.STONE, Blocks.SAND, Blocks.CLAY)) helper.assertTrue(!sapling.canGrowHere(block), "does not grow on $block")

		// крона — до 2 блоков от ствола, за краем площадки теста — камень
		val soil = BlockPos(1, 0, 1)
		helper.setBlock(soil, Blocks.GRASS_BLOCK)
		helper.setBlock(soil.above(), sapling)
		val pos = helper.absolutePos(soil.above())
		sapling.growTree(helper.level, pos.x, pos.y, pos.z, RandomSource.create(5))
		helper.assertBlockPresent(Blocks.DIRT, soil)
		val column = (1..9).map { helper.getBlockState(soil.above(it)).block }
		val height = column.indexOf(AlfheimBlocks.barrierWood[1]) + 1
		helper.assertTrue(height in 5..7, "heart wood at the height ${height - 1} of the trunk: $column")
		helper.assertTrue(column.take(height - 1).all { it === AlfheimBlocks.barrierWood[0] } && column[height] === AlfheimBlocks.barrierLeaves, "trunk and top: $column")
		var leaves = 0
		for (p in BlockPos.betweenClosed(-1, 1, -1, 3, 10, 3)) {
			val state = helper.getBlockState(p)
			if (state.block !== AlfheimBlocks.barrierLeaves) continue
			helper.assertTrue(!state.getValue(LeavesBlock.PERSISTENT), "leaves of a tree decay")
			leaves++
		}
		helper.assertTrue(leaves > 10, "leaves: $leaves")
		// сердцевина толкала бы существ соседних тестов (TileTreeWind)
		helper.setBlock(soil.above(height), Blocks.AIR)
		helper.succeed()
	}

	/**
	 * Свойства саженцев автора: адский светится (0,5 → 7) и горит в печи 800 тиков (обработчик топлива автора: саженец —
	 * не деревянный блок), прочие — 100, как цветной саженец; схемодрево даёт сигнал по числу блоков схемодрева над
	 * ним и светится (8); кошачий гасит взрывы, уплотнённый глушит звуки и звучит шерстью
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun magicSaplings(helper: GameTestHelper) {
		val b = AlfheimBlocks
		val saplings = listOf(b.barrierSapling, b.calicoSapling, b.circuitSapling, b.lightningSapling, b.netherSapling, b.sealingSapling)
		for (sapling in saplings) helper.assertTrue(sapling is BlockColoredSapling && sapling.defaultBlockState().`is`(BlockTags.SAPLINGS), "$sapling is a sapling")
		helper.assertTrue(b.netherSapling.defaultBlockState().lightEmission == 7 && b.circuitSapling.defaultBlockState().lightEmission == 8 && b.barrierSapling.defaultBlockState().lightEmission == 0, "light")
		for (sapling in saplings) {
			val fuel = ForgeHooks.getBurnTime(ItemStack(sapling), RecipeType.SMELTING)
			helper.assertTrue(fuel == if (sapling === b.netherSapling) 800 else 100, "$sapling fuel $fuel")
		}
		helper.assertTrue(b.calicoSapling is IExplosionDampener && b.sealingSapling is ISoundSilencer && b.sealingSapling.defaultBlockState().soundType == SoundType.WOOL, "calico and sealing saplings")

		val wire = BlockPos(0, 1, 1)
		helper.setBlock(BlockPos(0, 0, 1), Blocks.STONE)
		helper.setBlock(BlockPos(1, 0, 1), Blocks.DIRT)
		helper.setBlock(BlockPos(1, 1, 1), b.circuitSapling)
		helper.setBlock(BlockPos(1, 2, 1), b.circuitPlanks)
		helper.setBlock(wire, Blocks.REDSTONE_WIRE)
		helper.assertTrue(helper.getBlockState(wire).getValue(RedStoneWireBlock.POWER) == 1, "wire next to a circuit sapling under circuit planks")
		helper.assertTrue(helper.getBlockState(BlockPos(1, 1, 1)).isSignalSource, "circuit sapling: signal source")
		helper.succeed()
	}

	/**
	 * Ягоды (`BlockTreeBerry`): висят под листвой своего дерева, без неё падают; зреют (зрелость 0–2) от случайного тика
	 * и костной муки (срабатывает в 1 случае из 10), зрелая роняет фрукт своего дерева, незрелая — ничего. Рамка по
	 * зрелости, столкновений нет, место ягоды не занять. Ягоды с OBJ-моделью (виды 2–4) рисует только рендер
	 * блок-сущности, прочие — крест модели блока
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun treeBerries(helper: GameTestHelper) {
		val berry = AlfheimBlocks.lightningBerry as BlockTreeBerry
		val leaves = BlockPos(1, 3, 1)
		val pos = leaves.below()
		val abs = helper.absolutePos(pos)
		helper.setBlock(leaves, AlfheimBlocks.lightningLeaves)
		helper.setBlock(pos, berry)
		helper.assertTrue(helper.getBlockState(pos).canSurvive(helper.level, abs) && !berry.defaultBlockState().canSurvive(helper.level, abs.below()), "under the leaves only")

		val state = helper.getBlockState(pos)
		helper.assertTrue(state.getCollisionShape(helper.level, abs).isEmpty && state.getShape(helper.level, abs).bounds() == AABB(0.28125, 0.5625, 0.28125, 0.71875, 1.0, 0.71875), "shape of an unripe berry")
		helper.assertTrue(state.renderShape == RenderShape.INVISIBLE && AlfheimBlocks.barrierBerry.defaultBlockState().renderShape == RenderShape.MODEL, "render shape")

		fun drops() = Block.getDrops(helper.getBlockState(pos), helper.level, abs, helper.level.getBlockEntity(abs), null, ItemStack.EMPTY)
		helper.assertTrue(drops().isEmpty(), "an unripe berry drops nothing")
		for (age in 1..2) {
			helper.assertTrue(berry.isValidBonemealTarget(helper.level, abs, helper.getBlockState(pos), false), "bone meal on age ${age - 1}")
			berry.performBonemeal(helper.level, RandomSource.create(), abs, helper.getBlockState(pos))
			helper.assertBlockProperty(pos, BlockTreeBerry.AGE, age)
		}
		helper.assertTrue(!berry.isValidBonemealTarget(helper.level, abs, helper.getBlockState(pos), false), "a ripe berry takes no bone meal")
		val ripe = drops()
		helper.assertTrue(ripe.size == 1 && ripe[0].item === AlfheimItems.elvenFood[ElvenFoodMetas.TreeBerryLightning.I] && ripe[0].count == 1, "a ripe berry: $ripe")
		val random = RandomSource.create(7)
		val successes = (0 until 100).count { berry.isBonemealSuccess(helper.level, random, abs, state) }
		helper.assertTrue(successes in 3..20, "bone meal works 1 time in 10: $successes of 100")

		helper.setBlock(leaves, Blocks.AIR)
		helper.assertBlockNotPresent(berry, pos)
		helper.killAllEntities()
		helper.succeed()
	}

	/**
	 * Листва магических деревьев без ножниц роняет свой саженец с шансом 1/20 (лут листвы автора `BlockLeavesMod.getDrops`;
	 * задуманные в `BlockMagicLeaves` 1/400 он не спрашивал — BUGS.md, B-026), саму себя — только с ножницами
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun magicLeavesDropSaplings(helper: GameTestHelper) {
		val pos = BlockPos(1, 1, 1)
		helper.setBlock(pos, AlfheimBlocks.circuitLeaves)
		val state = helper.getBlockState(pos)
		val table = helper.level.server.lootData.getLootTable(AlfheimBlocks.circuitLeaves.lootTable)
		val params = LootParams.Builder(helper.level).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(helper.absolutePos(pos)))
			.withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withParameter(LootContextParams.BLOCK_STATE, state).create(LootContextParamSets.BLOCK)
		var saplings = 0
		// зёрна подряд (0, 1, 2…) у генератора 1.20.1 дают почти одно первое число — зёрна из генератора
		val seeds = RandomSource.create(42)
		repeat(400) { for (stack in table.getRandomItems(params, seeds.nextLong())) {
			helper.assertTrue(stack.item === AlfheimBlocks.circuitSapling.asItem(), "drop without shears: $stack")
			saplings += stack.count
		} }
		helper.assertTrue(saplings in 8..36, "saplings in 400 tries: $saplings")
		helper.succeed()
	}

	/**
	 * Сердцевины барьерного, адского и грозового деревьев (вариант 1 бревна) — с блок-сущностью автора, бревно (вариант 0)
	 * — без; id типов — имена автора в snake_case, старые имена — в `legacy_ids.json`. Все три тикают (`canUpdate` у них
	 * не переопределён) и стоят в списке тикающих блок-сущностей мира (`loadedTileEntityList`); блок-сущность уходит с
	 * блоком
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun heartWoodTiles(helper: GameTestHelper) {
		val pos = BlockPos(1, 1, 1)
		fun check(wood: Array<Block>, clazz: Class<out BlockEntity>, id: String, legacy: String) {
			helper.setBlock(pos, wood[0])
			helper.assertTrue(helper.getBlockEntity(pos) == null, "${wood[0]} has no tile entity")
			helper.setBlock(pos, wood[1])
			val tile = helper.getBlockEntity(pos) ?: throw GameTestAssertException("${wood[1]} has no tile entity")
			helper.assertTrue(clazz.isInstance(tile), "${wood[1]} tile: $tile")
			helper.assertTrue(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(tile.type) == ResourceLocation(MODID, id), "$id type")
			helper.assertTrue(LegacyIds.blockEntities["$MODID:$legacy"]?.get("*")?.id == ResourceLocation(MODID, id), "legacy id $legacy")
			helper.assertTrue(helper.getBlockState(pos).getTicker(helper.level, tile.type) != null, "$id ticks")
			helper.assertTrue(tile in helper.level.loadedTileEntityList, "$id is a ticking tile entity of the world")
			helper.setBlock(pos, Blocks.AIR)
			helper.assertTrue(tile.isRemoved && tile !in helper.level.loadedTileEntityList, "$id is removed with its block")
		}
		check(AlfheimBlocks.barrierWood, TileTreeWind::class.java, "tree_wind", "TreeWind")
		check(AlfheimBlocks.netherWood, TileTreeCook::class.java, "tree_cook", "TreeCook")
		check(AlfheimBlocks.lightningWood, TileLightningTreeTop::class.java, "lightning_tree_top", "LightningTreeTop")
		helper.succeed()
	}

	/**
	 * Ветер барьерного дерева (`TileTreeWind`): игроки в 10 блоках в первый тик сердцевины — её друзья. Живых существ
	 * и чужих игроков (не в творческом режиме) в 10 блоках сердцевина толкает от себя каждый тик — на 1 блок за тик;
	 * игроку шлёт его новую скорость. Друзья сохраняются в NBT
	 */
	@JvmStatic
	@GameTest(template = "empty", batch = "heart_wood")
	fun barrierTreeWind(helper: GameTestHelper) {
		val wood = BlockPos(1, 2, 1)
		helper.setBlock(BlockPos(3, 1, 1), Blocks.STONE)
		helper.setBlock(BlockPos(1, 1, 3), Blocks.STONE)
		// игрок сервера без клиента (FakePlayer Forge: пакеты ему никуда не уходят, сам он не двигается), не в творческом режиме
		val player = FakePlayerFactory.get(helper.level, GameProfile(UUID.randomUUID(), "alfheim-wind-friend"))
		val at = helper.absoluteVec(Vec3(3.5, 2.0, 1.5))
		player.moveTo(at.x, at.y, at.z)
		helper.level.addNewPlayer(player)
		helper.setBlock(wood, AlfheimBlocks.barrierWood[1])
		val tile = helper.getBlockEntity(wood) as? TileTreeWind ?: throw GameTestAssertException("no barrier heart wood tile")
		val pig = helper.spawnWithNoFreeWill(EntityType.PIG, BlockPos(1, 2, 3))
		helper.startSequence().thenExecuteAfter(2) {
			helper.assertTrue(!tile.firstTick && tile.friends == setOf(player.gameProfile.name), "friends: ${tile.friends}")
			helper.assertTrue(pig.deltaMovement.z > 0.5, "the pig is blown away: ${pig.deltaMovement}")
			helper.assertTrue(kotlin.math.abs(player.deltaMovement.x) < 0.1, "a friend is not blown away: ${player.deltaMovement}")
			val nbt = tile.saveWithoutMetadata()
			val friends = nbt.getList("friends", Tag.TAG_STRING.toInt())
			helper.assertTrue(friends.size == 1 && friends.getString(0) == player.gameProfile.name && !nbt.getBoolean("firstTick"), "saved: $nbt")
			val copy = TileTreeWind(helper.absolutePos(wood), helper.getBlockState(wood)).apply { load(nbt) }
			helper.assertTrue(copy.friends == tile.friends && !copy.firstTick, "loaded: ${copy.friends}")
			tile.friends.clear()
		}.thenExecuteAfter(2) {
			helper.assertTrue(player.deltaMovement.x > 0.5, "a stranger is blown away: ${player.deltaMovement}")
		}.thenExecute {
			helper.level.removePlayerImmediately(player, Entity.RemovalReason.DISCARDED)
			pig.discard()
			helper.setBlock(wood, Blocks.AIR)
		}.thenSucceed()
	}

	/**
	 * Жаровня адского дерева (`TileTreeCook`): когда время мира кратно 20 тикам, первая еда на земле в 8 блоках, которую
	 * печь делает едой, жарится — рядом появляется одна жареная, сырой становится на одну меньше. Руда — не еда
	 */
	@JvmStatic
	@GameTest(template = "empty", batch = "heart_wood")
	fun netherTreeCooks(helper: GameTestHelper) {
		val wood = BlockPos(1, 2, 1)
		helper.setBlock(wood, AlfheimBlocks.netherWood[1])
		helper.setBlock(BlockPos(1, 0, 3), Blocks.STONE)
		helper.setBlock(BlockPos(3, 0, 1), Blocks.STONE)
		fun drop(item: Item, count: Int, at: BlockPos): ItemEntity {
			val pos = helper.absoluteVec(Vec3.atBottomCenterOf(at))
			return ItemEntity(helper.level, pos.x, pos.y, pos.z, ItemStack(item, count)).apply {
				setDeltaMovement(0.0, 0.0, 0.0)
				helper.level.addFreshEntity(this)
			}
		}
		val beef = drop(Items.BEEF, 3, BlockPos(1, 1, 3))
		val iron = drop(Items.RAW_IRON, 1, BlockPos(3, 1, 1))
		fun count(item: Item) = helper.level.getEntitiesOfClass(ItemEntity::class.java, AABB(helper.absolutePos(wood)).inflate(9.0)).filter { it.item.`is`(item) }.sumOf { it.item.count }
		helper.succeedWhen {
			val cooked = count(Items.COOKED_BEEF)
			helper.assertTrue(cooked >= 1, "cooked beef")
			helper.assertTrue(beef.item.count + cooked == 3, "one raw beef for one cooked: ${beef.item.count} raw, $cooked cooked")
			helper.assertTrue(iron.item.count == 1 && count(Items.IRON_INGOT) == 0, "raw iron is not food")
			helper.setBlock(wood, Blocks.AIR)
			helper.killAllEntities()
		}
	}

	/**
	 * Громоотвод грозового дерева (`TileLightningTreeTop`): молнию в 64 блоках от сердцевины мир в начале следующего тика
	 * заменяет ложной (`FakeLightning`) на 1,5 блока выше сердцевины — только видимость и гром. Молнии — погодные
	 * эффекты мира (`weatherEffects`). Ложную молнию мир не сохраняет, клиент видит её за 16 чанков, как молнию 1.20.1
	 */
	@JvmStatic
	@GameTest(template = "empty", batch = "heart_wood")
	fun lightningTreeRod(helper: GameTestHelper) {
		val type = legacyType<FakeLightning>()
		helper.assertTrue(BuiltInRegistries.ENTITY_TYPE.getKey(type) == ResourceLocation(MODID, "fake_lightning"), "fake lightning id")
		helper.assertTrue(LegacyIds.entities["$MODID:FakeLightning"]?.get("*")?.id == ResourceLocation(MODID, "fake_lightning"), "fake lightning legacy id")
		helper.assertTrue(!type.canSerialize() && type.clientTrackingRange() == 16 && type.updateInterval() == Int.MAX_VALUE, "fake lightning is a weather effect")

		val wood = BlockPos(1, 1, 1)
		helper.setBlock(wood, AlfheimBlocks.lightningWood[1])
		val rod = helper.absolutePos(wood)
		val bolt = EntityType.LIGHTNING_BOLT.create(helper.level)!!
		bolt.moveTo(helper.absoluteVec(Vec3(3.5, 1.0, 3.5)))
		bolt.setVisualOnly(true)
		helper.level.addFreshEntity(bolt)
		helper.assertTrue(bolt in helper.level.weatherEffects, "the bolt is a weather effect")
		helper.succeedWhen {
			helper.assertTrue(bolt.isRemoved && bolt !in helper.level.weatherEffects, "the bolt is replaced")
			val fakes = helper.level.getEntitiesOfClass(FakeLightning::class.java, AABB(rod).inflate(3.0))
			helper.assertTrue(fakes.any { it.x == rod.x.toDouble() && it.y == rod.y + 1.5 && it.z == rod.z.toDouble() && it in helper.level.weatherEffects },
				"fake lightning above the heart wood: ${fakes.map { it.position() }}")
			helper.setBlock(wood, Blocks.AIR)
			fakes.forEach { it.discard() }
		}
	}
}
