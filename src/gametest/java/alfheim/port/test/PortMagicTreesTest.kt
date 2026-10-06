package alfheim.port.test

import alfheim.api.ModInfo.MODID
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.magtrees.sealing.EventHandlerSealingOak
import alfheim.common.block.magtrees.sealing.ISoundSilencer
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenResourcesMetas.*
import alfheim.port.legacy.*
import alfheim.port.registry.LegacyIds
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.*
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.properties.SlabType
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate

/**
 * КТ-2, партия 8в-1: блоки шести магических деревьев — барьерного, кошачьего, грозового, адского, уплотнённого деревьев
 * и схемодрева — и их механики. Числа и правила — из классов автора (`alfheim.common.block.magtrees`) и правил 1.7.10
 * (MAPPING.md)
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
}
