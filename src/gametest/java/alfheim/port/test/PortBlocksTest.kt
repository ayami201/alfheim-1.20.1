package alfheim.port.test

import alfheim.api.ModInfo.MODID
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.AlfheimFluffBlocks
import alfheim.common.block.colored.BlockColoredLamp
import alfheim.port.legacy.LegacyBlock
import alfheim.port.registry.*
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.*
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.*
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraftforge.common.IPlantable
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate

/**
 * КТ-2: блоки автора. Каждый зарегистрирован под своим id, с предметом, лутом и записью в legacy_ids.json; свойства —
 * те, что давали сеттеры 1.7.10 (значения посчитаны по правилам `Block` 1.7.10, MAPPING.md, «Блоки и предметы»).
 */
@GameTestHolder(MODID)
@PrefixGameTestTemplate(false)
object PortBlocksTest {

	@JvmStatic
	@GameTest(template = "empty")
	fun blocksRegistered(helper: GameTestHelper) {
		val problems = ArrayList<String>()
		helper.assertTrue(LegacyRegistration.blocks.isNotEmpty(), "no blocks of the author are registered")
		for ((block, entry) in LegacyRegistration.blocks) {
			if (BuiltInRegistries.BLOCK.getKey(block) != entry.id) problems += "${entry.id} is registered as ${BuiltInRegistries.BLOCK.getKey(block)}"
			val item = block.asItem()
			if (item !is BlockItem || BuiltInRegistries.ITEM.getKey(item) != entry.id) problems += "${entry.id} has no block item"
			val target = LegacyIds.block("$MODID:${entry.oldName}", entry.oldMeta ?: 0)
			if (target?.id != entry.id) problems += "legacy_ids.json: $MODID:${entry.oldName}:${entry.oldMeta} -> ${target?.id}, expected ${entry.id}"
			val loot = helper.level.server.lootData.getLootTable(block.lootTable)
			if (loot === LootTable.EMPTY) problems += "${entry.id} has no loot table"
		}
		// двойная плита 1.7.10 — состояние своей плиты
		for (alias in LegacyRegistration.aliases) {
			val target = LegacyIds.block("$MODID:${alias.oldName}")
			if (target?.id != LegacyRegistration.blocks[alias.block]?.id || target?.state != mapOf("type" to "double")) problems += "legacy_ids.json: $MODID:${alias.oldName} -> $target"
		}
		helper.assertTrue(problems.isEmpty(), problems.toString())
		helper.succeed()
	}

	/** Партия КТ-2 №1: блоки и их варианты на месте под именами SPEC, Р-5 */
	@JvmStatic
	@GameTest(template = "empty")
	fun firstBatchIds(helper: GameTestHelper) {
		val ids = listOf("elven_sand", "mana_ice") + (0..5).map { "alf_storage$it" } + (0..3).map { "living_cobble$it" } + (0..4).map { "elven_sandstone$it" }
		for (id in ids) helper.assertTrue(BuiltInRegistries.BLOCK.containsKey(ResourceLocation(MODID, id)), "$MODID:$id is not registered")
		helper.assertTrue(AlfheimBlocks.alfStorage.size == 6 && AlfheimBlocks.livingcobble.size == 4 && AlfheimFluffBlocks.elvenSandstone.size == 5, "variant arrays")
		helper.succeed()
	}

	/**
	 * Твёрдость, взрывоустойчивость, звук, скользкость, свет — как у блока 1.7.10. Взрывоустойчивость 1.20.1 —
	 * внутренняя 1.7.10 / 5: `setHardness(h)` поднимает внутреннюю до h * 5, `setResistance(r)` ставит r * 3
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun legacyProperties(helper: GameTestHelper) {
		fun check(block: Block, hardness: Float, resistance: Float, sound: SoundType, toolForDrops: Boolean) {
			val state = block.defaultBlockState()
			val name = BuiltInRegistries.BLOCK.getKey(block)
			helper.assertTrue(state.getDestroySpeed(helper.level, BlockPos.ZERO) == hardness, "$name hardness ${state.getDestroySpeed(helper.level, BlockPos.ZERO)}, expected $hardness")
			helper.assertTrue(block.explosionResistance == resistance, "$name resistance ${block.explosionResistance}, expected $resistance")
			helper.assertTrue(state.soundType == sound, "$name sound")
			helper.assertTrue(state.requiresCorrectToolForDrops() == toolForDrops, "$name requiresCorrectToolForDrops")
		}

		// BlockPatternLexicon: hardness 1 → внутренняя 5, setResistance(5) → 15
		check(AlfheimBlocks.elvenSand, 1f, 15f / 5f, SoundType.SAND, false)
		// BlockModMeta: hard 5, setResistance(max(60, 25)) → 180
		AlfheimBlocks.alfStorage.forEach { check(it, 5f, 180f / 5f, SoundType.METAL, true) }
		// BlockModMeta: hard 2, setResistance(max(60, 10)) → 180
		AlfheimBlocks.livingcobble.forEach { check(it, 2f, 180f / 5f, SoundType.STONE, true) }
		// BlockModMeta: hard 1, setResistance(max(5, 5)) → 15
		AlfheimFluffBlocks.elvenSandstone.forEach { check(it, 1f, 15f / 5f, SoundType.STONE, true) }
		// BlockManaIce: setHardness(0.5) → внутренняя 2.5
		check(AlfheimBlocks.manaIce, 0.5f, 2.5f / 5f, SoundType.GLASS, false)

		val ice = AlfheimBlocks.manaIce
		helper.assertTrue(ice.friction == 1 / 0.91f, "mana ice slipperiness ${ice.friction}")
		helper.assertTrue(ice.defaultBlockState().getLightBlock(helper.level, BlockPos.ZERO) == 3, "mana ice light opacity")
		helper.assertTrue(!ice.defaultBlockState().isSolidRender(helper.level, BlockPos.ZERO), "mana ice is not an opaque cube")
		helper.assertTrue(!ice.defaultBlockState().isRedstoneConductor(helper.level, BlockPos.ZERO), "ice material is translucent: no redstone")
		helper.assertTrue(AlfheimBlocks.elvenSand.defaultBlockState().getLightBlock(helper.level, BlockPos.ZERO) == 15, "elven sand light opacity")
		helper.succeed()
	}

	/** Инструмент добычи и маяк — теги из генерации данных */
	@JvmStatic
	@GameTest(template = "empty")
	fun legacyTags(helper: GameTestHelper) {
		val sand = AlfheimBlocks.elvenSand.defaultBlockState()
		helper.assertTrue(sand.`is`(BlockTags.MINEABLE_WITH_SHOVEL) && !sand.`is`(BlockTags.NEEDS_STONE_TOOL), "elven sand: shovel, level 0")
		for (block in AlfheimBlocks.alfStorage) {
			val state = block.defaultBlockState()
			helper.assertTrue(state.`is`(BlockTags.MINEABLE_WITH_PICKAXE) && state.`is`(BlockTags.NEEDS_STONE_TOOL), "alfStorage: pickaxe, level 1")
			helper.assertTrue(state.`is`(BlockTags.BEACON_BASE_BLOCKS), "alfStorage is a beacon base")
		}
		helper.assertTrue(!AlfheimBlocks.manaIce.defaultBlockState().`is`(BlockTags.MINEABLE_WITH_PICKAXE), "mana ice has no harvest tool")
		helper.assertTrue(AlfheimBlocks.elvenSand.asItem().builtInRegistryHolder().`is`(ItemTags.create(ResourceLocation("forge", "sand"))), "Ore Dictionary sand → forge:sand")
		helper.succeed()
	}

	/** BlockElvenSand.canSustainPlant: кактус растёт всегда, тростник — у воды */
	@JvmStatic
	@GameTest(template = "empty")
	fun elvenSandPlants(helper: GameTestHelper) {
		val pos = BlockPos(0, 1, 0)
		helper.setBlock(pos, AlfheimBlocks.elvenSand)
		val absolute = helper.absolutePos(pos)
		val state = helper.getBlockState(pos)
		helper.assertTrue(state.canSustainPlant(helper.level, absolute, Direction.UP, Blocks.CACTUS as IPlantable), "cactus on elven sand")
		helper.assertTrue(!state.canSustainPlant(helper.level, absolute, Direction.UP, Blocks.SUGAR_CANE as IPlantable), "sugar cane without water")
		helper.setBlock(pos.east(), Blocks.WATER)
		helper.assertTrue(state.canSustainPlant(helper.level, absolute, Direction.UP, Blocks.SUGAR_CANE as IPlantable), "sugar cane next to water")
		helper.succeed()
	}

	/** Эльфийский песок падает (isFalling), как песок */
	@JvmStatic
	@GameTest(template = "empty", timeoutTicks = 100)
	fun elvenSandFalls(helper: GameTestHelper) {
		helper.setBlock(BlockPos(0, 1, 0), Blocks.STONE)
		helper.setBlock(BlockPos(0, 5, 0), AlfheimBlocks.elvenSand)
		helper.succeedWhen {
			helper.assertBlockPresent(AlfheimBlocks.elvenSand, BlockPos(0, 2, 0))
			helper.assertBlockNotPresent(AlfheimBlocks.elvenSand, BlockPos(0, 5, 0))
		}
	}

	/**
	 * Сеттеры 1.7.10 у всех блоков автора дошли до состояний: свечение и твёрдость те же, что у блока. Свечение лампы
	 * ириса — по силе сигнала (`getLightValue` с координатами), его проверяет `PortPlantsTest.irisLampPower`
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun settersReachStates(helper: GameTestHelper) {
		for (block in LegacyRegistration.blocks.keys) {
			val legacy = (block as LegacyBlock).legacy
			for (state in block.stateDefinition.possibleStates) {
				helper.assertTrue(state.getDestroySpeed(helper.level, BlockPos.ZERO) == legacy.blockHardness, "${BuiltInRegistries.BLOCK.getKey(block)} hardness")
				if (block !is BlockColoredLamp) helper.assertTrue(state.lightEmission == legacy.lightValue, "${BuiltInRegistries.BLOCK.getKey(block)} light")
			}
		}
		helper.succeed()
	}
}
