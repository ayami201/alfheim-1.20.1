package alfheim.port.test

import alfheim.api.ModInfo.MODID
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.BlockNiflheim
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenResourcesMetas
import alfheim.port.loot.FortuneCount
import alfheim.port.registry.LegacyIds
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.tags.ItemTags
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.item.FallingBlockEntity
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.RotatedPillarBlock.AXIS
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate

/**
 * КТ-2, партия 9а-3а: камень Нифльхейма (`BlockNiflheim`: 7 вариантов, колонна и руническая колонна с осью) и твердь
 * Хельхейма (`BlockPattern` ASJCore). Числа и правила — из классов автора
 */
@GameTestHolder(MODID)
@PrefixGameTestTemplate(false)
object PortNiflheimTest {

	/** Варианты 1.7.10, у которых свой блок: 0–6, колонна 7, руническая колонна 10 */
	private val variants = listOf(0, 1, 2, 3, 4, 5, 6, 7, 10)

	private val names = mapOf(0 to "Deepfrozen Stone", 1 to "Deepfrozen Cobblestone", 2 to "Nifleur Ore", 3 to "Deepfrozen Stone Bricks", 4 to "Cracked Deepfrozen Stone Bricks",
		5 to "Chiseled Deepfrozen Stone Bricks", 6 to "Polished Deepfrozen Stone", 7 to "Deepfrozen Stone Pillar", 10 to "Runic Deepfrozen Stone")

	private fun nifl(meta: Int) = AlfheimBlocks.niflheimBlock[meta]

	/**
	 * Имя 1.7.10 `NiflheimBlock` с metadata → `alfheim:niflheim_block<вариант>`, повёрнутые колонны (8, 9, 11, 12) — та же
	 * колонна с осью X или Z; массив — по metadata. Названия — из переводов автора (`tile.NiflheimBlock.<вид>.name`). Во
	 * вкладке — 9 вариантов подряд, без повёрнутых колонн; тверди Хельхейма во вкладке автора нет. Ore Dictionary:
	 * `niflStone` — камень, `oreNifleur` — руда; функция лута `alfheim:fortune_count` зарегистрирована
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun niflheimRegistry(helper: GameTestHelper) {
		helper.assertTrue(AlfheimBlocks.niflheimBlock.size == 13 && AlfheimBlocks.niflheimBlock.distinct().size == 9, "13 metas, 9 blocks")
		helper.assertTrue(nifl(8) === nifl(7) && nifl(9) === nifl(7) && nifl(11) === nifl(10) && nifl(12) === nifl(10), "rotated pillars are the pillars")
		for (meta in variants) {
			val block = nifl(meta)
			helper.assertTrue(BuiltInRegistries.BLOCK.getKey(block) == ResourceLocation(MODID, "niflheim_block$meta"), "niflheim_block$meta: ${BuiltInRegistries.BLOCK.getKey(block)}")
			helper.assertTrue((block as BlockNiflheim).meta == meta && block.variant == meta, "variant $meta")
			helper.assertTrue(LegacyIds.block("$MODID:NiflheimBlock", meta)?.id == ResourceLocation(MODID, "niflheim_block$meta"), "legacy id $meta")
			helper.assertTrue(ItemStack(block).hoverName.string == names[meta], "name $meta: ${ItemStack(block).hoverName.string}")
		}
		for ((meta, base, axis) in listOf(Triple(8, 7, "x"), Triple(9, 7, "z"), Triple(11, 10, "x"), Triple(12, 10, "z"))) {
			val target = LegacyIds.block("$MODID:NiflheimBlock", meta)
			helper.assertTrue(target?.id == ResourceLocation(MODID, "niflheim_block$base") && target.state == mapOf("axis" to axis), "legacy id $meta: $target")
		}
		helper.assertTrue(BuiltInRegistries.BLOCK.getKey(AlfheimBlocks.helheimBlock) == ResourceLocation(MODID, "helheim_block"), "helheim id")
		helper.assertTrue(LegacyIds.block("$MODID:HelheimBlock")?.id == ResourceLocation(MODID, "helheim_block"), "helheim legacy id")
		helper.assertTrue(ItemStack(AlfheimBlocks.helheimBlock).hoverName.string == "Solid of Helheim", "helheim name")

		CreativeModeTabs.tryRebuildTabContents(helper.level.enabledFeatures(), true, helper.level.registryAccess())
		val tab = AlfheimTab.tab.get().displayItems.map { it.item }
		val niflheim = variants.map { nifl(it).asItem() }
		val first = tab.indexOf(niflheim[0])
		helper.assertTrue(first >= 0 && tab.subList(first, first + niflheim.size) == niflheim && tab.count { it in niflheim } == niflheim.size, "Niflheim stone in the tab")
		helper.assertTrue(tab[first + niflheim.size] == AlfheimBlocks.stalactite[0].asItem(), "stalactites after the Niflheim stone")
		helper.assertTrue(AlfheimBlocks.helheimBlock.asItem() !in tab, "no Helheim solid in the tab")

		helper.assertTrue(nifl(0).defaultBlockState().`is`(BlockTags.create(ResourceLocation(MODID, "nifl_stone"))) && ItemStack(nifl(0)).`is`(ItemTags.create(ResourceLocation(MODID, "nifl_stone"))), "niflStone")
		helper.assertTrue(nifl(2).defaultBlockState().`is`(BlockTags.create(ResourceLocation("forge", "ores/nifleur"))) && ItemStack(nifl(2)).`is`(ItemTags.create(ResourceLocation("forge", "ores/nifleur"))), "oreNifleur")
		helper.assertTrue(BuiltInRegistries.LOOT_FUNCTION_TYPE.getKey(FortuneCount.TYPE.get()) == ResourceLocation(MODID, "fortune_count"), "alfheim:fortune_count")
		helper.succeed()
	}

	/**
	 * Камень Нифльхейма: твёрдость 3 (взрывоустойчивость 3: 3 × 5 / 5), звук камня, кирпич каменной кирки и лучше (уровень 1),
	 * без кирки — ничего. Колонна по умолчанию стоит, ставится по оси стороны, на которую её ставят, поворачивается с
	 * постройкой; у прочих вариантов оси нет. Твердь Хельхейма: не ломается, взрыв её не берёт, свет не пропускает, не падает
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun niflheimProperties(helper: GameTestHelper) {
		val abs = helper.absolutePos(BlockPos(1, 1, 1))
		for (meta in variants) {
			val state = nifl(meta).defaultBlockState()
			helper.assertTrue(state.getDestroySpeed(helper.level, abs) == 3f && nifl(meta).explosionResistance == 3f, "$meta: hardness")
			helper.assertTrue(state.soundType == SoundType.STONE && state.requiresCorrectToolForDrops(), "$meta: stone")
			helper.assertTrue(state.`is`(BlockTags.MINEABLE_WITH_PICKAXE) && state.`is`(BlockTags.NEEDS_STONE_TOOL), "$meta: stone pickaxe")
			helper.assertTrue(state.hasProperty(AXIS) == (meta == 7 || meta == 10), "$meta: axis")
		}
		val player = helper.makeMockPlayer()
		for (meta in listOf(7, 10)) {
			helper.assertTrue(nifl(meta).defaultBlockState().getValue(AXIS) == Direction.Axis.Y, "$meta stands by default")
			for ((face, axis) in listOf(Direction.UP to Direction.Axis.Y, Direction.NORTH to Direction.Axis.Z, Direction.SOUTH to Direction.Axis.Z, Direction.WEST to Direction.Axis.X, Direction.EAST to Direction.Axis.X)) {
				val context = BlockPlaceContext(helper.level, player, InteractionHand.MAIN_HAND, ItemStack(nifl(meta)), BlockHitResult(Vec3.atCenterOf(abs), face, abs, false))
				helper.assertTrue(nifl(meta).getStateForPlacement(context)?.getValue(AXIS) == axis, "$meta placed on $face")
			}
			val x = nifl(meta).defaultBlockState().setValue(AXIS, Direction.Axis.X)
			helper.assertTrue(x.rotate(Rotation.CLOCKWISE_90).getValue(AXIS) == Direction.Axis.Z, "$meta rotates")
		}

		val helheim = AlfheimBlocks.helheimBlock.defaultBlockState()
		helper.assertTrue(helheim.getDestroySpeed(helper.level, abs) == -1f && AlfheimBlocks.helheimBlock.explosionResistance == Float.POSITIVE_INFINITY, "helheim is unbreakable")
		helper.assertTrue(helheim.soundType == SoundType.STONE && helheim.getLightBlock(helper.level, abs) == 15 && helheim.isSolidRender(helper.level, abs), "helheim is an opaque stone cube")
		helper.succeed()
	}

	/** Твердь Хельхейма в воздухе не падает: падающий блок 1.7.10 (`BlockPattern`, `isFalling = false`) не трогался с места */
	@JvmStatic
	@GameTest(template = "empty")
	fun helheimDoesNotFall(helper: GameTestHelper) {
		val pos = helper.absolutePos(BlockPos(0, 164, 0))
		for (j in -3..1) helper.level.setBlock(pos.above(j), Blocks.AIR.defaultBlockState(), 3)
		helper.level.setBlock(pos, AlfheimBlocks.helheimBlock.defaultBlockState(), 3)
		helper.level.getBlockState(pos).tick(helper.level, pos, RandomSource.create(0))
		helper.runAfterDelay(5) {
			helper.assertTrue(helper.level.getBlockState(pos).block === AlfheimBlocks.helheimBlock, "Helheim solid stays")
			helper.assertTrue(helper.level.getEntitiesOfClass(FallingBlockEntity::class.java, AABB(pos).inflate(3.0)).isEmpty(), "no falling block")
			helper.level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3)
			helper.succeed()
		}
	}

	/**
	 * Лут (`getDrops`, `createStackedBlock`): руда — нифлёр, удача + 1 штук (и при удаче выше III), с шёлковым касанием —
	 * руда; прочие варианты — себя, повёрнутая колонна — колонну
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun niflheimLoot(helper: GameTestHelper) {
		val abs = helper.absolutePos(BlockPos(1, 1, 1))
		val nifleur = AlfheimItems.elvenResource[ElvenResourcesMetas.Nifleur.I]
		fun drops(state: net.minecraft.world.level.block.state.BlockState, tool: ItemStack) = Block.getDrops(state, helper.level, abs, null, null, tool)
		val ore = nifl(2).defaultBlockState()
		for (fortune in listOf(0, 1, 2, 3, 10)) {
			val pick = ItemStack(Items.DIAMOND_PICKAXE).apply { if (fortune > 0) enchant(Enchantments.BLOCK_FORTUNE, fortune) }
			val got = drops(ore, pick)
			helper.assertTrue(got.all { it.item == nifleur } && got.sumOf { it.count } == fortune + 1, "fortune $fortune: $got")
		}
		val silk = ItemStack(Items.DIAMOND_PICKAXE).apply { enchant(Enchantments.SILK_TOUCH, 1) }
		helper.assertTrue(drops(ore, silk).map { it.item } == listOf(nifl(2).asItem()), "silk touch: ore")
		for (meta in variants - 2)
			helper.assertTrue(drops(nifl(meta).defaultBlockState(), ItemStack(Items.IRON_PICKAXE)).map { it.item } == listOf(nifl(meta).asItem()), "$meta drops itself")
		helper.assertTrue(drops(nifl(7).defaultBlockState().setValue(AXIS, Direction.Axis.X), ItemStack(Items.IRON_PICKAXE)).map { it.item } == listOf(nifl(7).asItem()), "a lying pillar drops the pillar")
		helper.succeed()
	}

	/**
	 * Рецепты автора: 4 камня → 4 кирпича, 4 кирпича → 4 резных, 8 камней кольцом → 8 полированных, 2 камня столбиком →
	 * 2 колонны, 2 резных столбиком → 2 рунические колонны, камень → булыжник; кирпичи в печи → треснутые (опыта нет)
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun niflheimRecipes(helper: GameTestHelper) {
		val recipes = helper.level.recipeManager
		val access = helper.level.registryAccess()
		for ((id, count) in listOf("niflheim_block3" to 4, "niflheim_block5" to 4, "niflheim_block6" to 8, "niflheim_block7" to 2, "niflheim_block10" to 2, "niflheim_block1" to 1)) {
			val recipe = recipes.byKey(ResourceLocation(MODID, id)).orElse(null)
			val result = recipe?.getResultItem(access)
			helper.assertTrue(recipe?.type == RecipeType.CRAFTING && result?.item == BuiltInRegistries.ITEM.get(ResourceLocation(MODID, id)) && result.count == count, "$id: $result")
		}
		val smelting = recipes.byKey(ResourceLocation(MODID, "niflheim_block4_from_smelting")).orElse(null) as? net.minecraft.world.item.crafting.SmeltingRecipe
		helper.assertTrue(smelting != null && smelting.getResultItem(access).item == nifl(4).asItem() && smelting.ingredients[0].test(ItemStack(nifl(3))) && smelting.experience == 0f, "bricks smelt into cracked bricks")
		helper.succeed()
	}
}
