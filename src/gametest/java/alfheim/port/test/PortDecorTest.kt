package alfheim.port.test

import alexsocol.asjlib.meta
import alfheim.api.ModInfo.MODID
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.AlfheimFluffBlocks
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.*
import alfheim.port.legacy.botania.BotaniaBlocks1710
import alfheim.port.registry.LegacyIds
import alfheim.port.registry.LegacyRegistration
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.*
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.properties.*
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.BotaniaRegistries
import vazkii.botania.common.block.BotaniaBlocks
import vazkii.botania.common.brew.BotaniaBrews

/**
 * КТ-2, партия 2: декор `AlfheimFluffBlocks` — лестницы, плиты, стены, заборы, калитки, люк, панели, столб и блоки
 * святилища. Значения посчитаны по правилам 1.7.10 (MAPPING.md, «Блоки и предметы»): `setHardness(h)` поднимает
 * внутреннюю взрывоустойчивость до h * 5, `setResistance(r)` ставит r * 3, взрывоустойчивость 1.20.1 — внутренняя / 5
 */
@GameTestHolder(MODID)
@PrefixGameTestTemplate(false)
object PortDecorTest {

	/** Блоки партии 2 и их варианты — под именами SPEC, Р-5; стены из кирпичей живого камня нет — её заменила стена Botania */
	@JvmStatic
	@GameTest(template = "empty")
	fun secondBatchIds(helper: GameTestHelper) {
		val ids = (0..2).map { "wisdomwood$it" } + (0..15).map { "shrine_rock$it" } + listOf("shrine_pillar", "shrine_rock_white_stairs", "shrine_rock0_slab") +
			(0..2).flatMap { listOf("custom_roof$it", "custom_roof${it}_slab", "custom_roof_stairs$it") } + listOf("living_mountain", "living_mountain0_slab") +
			(0..3).map { "dark_living_rock$it" } + listOf(0, 1, 3).flatMap { listOf("dark_living_rock_stairs$it", "dark_living_rock${it}_slab") } + (0..1).map { "dark_living_rock${it}_wall" } +
			listOf("dwarf_lantern") + (0..5).map { "shrine_light$it" } + (0..4).map { "shrine_glass$it" } + (0..3).map { "shrine_panel$it" } +
			listOf("quartz_type_elf0_wall", "dwarf_planks", "dwarf_planks_stairs", "dwarf_planks0_slab") +
			listOf(0, 2).flatMap { listOf("elven_sandstone_stairs$it", "elven_sandstone${it}_slab", "elven_sandstone${it}_wall") } +
			listOf("living_cobble_stairs", "living_cobble_stairs1", "living_cobble_stairs2") + (0..2).map { "living_cobble${it}_slab" } + listOf("living_cobble0_wall") +
			listOf("livingwood", "dreamwood").flatMap { listOf("${it}_bark_fence", "${it}_bark_fence_gate", "${it}_fence", "${it}_fence_gate") } + listOf("dwarf_trap_door")
		helper.assertTrue(ids.size == 87, "ids: ${ids.size}")
		for (id in ids) helper.assertTrue(BuiltInRegistries.BLOCK.containsKey(ResourceLocation(MODID, id)), "$MODID:$id is not registered")
		// 104 блока партий 1 и 2, 6 вариантов эльфийской руды партии 3, 130 цветных блоков партии 8а, 12 радужных и
		// авроровых блоков партии 8б-1, 8 радужных растений партии 8б-2, 10 блоков партии 8б-3 (гриб, 3 варианта
		// мерцающего кварца, его плита и лестница, 4 мягких блока), черепица Botania 1.7.10, 33 блока магических деревьев
		// партии 8в-1 и 12 партии 8в-2 (6 саженцев, 6 ягод), 37 блоков альтернативных деревьев партии 8г-1, саженец древа
		// мечтаний и листва печального дуба партии 8г-2, 20 стадий висячих блоков партии 9а-1
		helper.assertTrue(LegacyRegistration.blocks.size == 375, "blocks of the author: ${LegacyRegistration.blocks.size}")
		helper.succeed()
	}

	/** Твёрдость, взрывоустойчивость, звук — как у блока 1.7.10 */
	@JvmStatic
	@GameTest(template = "empty")
	fun decorProperties(helper: GameTestHelper) {
		fun check(block: Block, hardness: Float, resistance: Float, sound: SoundType, toolForDrops: Boolean) {
			val state = block.defaultBlockState()
			val name = BuiltInRegistries.BLOCK.getKey(block)
			helper.assertTrue(state.getDestroySpeed(helper.level, BlockPos.ZERO) == hardness, "$name hardness ${state.getDestroySpeed(helper.level, BlockPos.ZERO)}, expected $hardness")
			helper.assertTrue(block.explosionResistance == resistance, "$name resistance ${block.explosionResistance}, expected $resistance")
			helper.assertTrue(state.soundType == sound, "$name sound")
			helper.assertTrue(state.requiresCorrectToolForDrops() == toolForDrops, "$name requiresCorrectToolForDrops")
		}

		val f = AlfheimFluffBlocks
		// BlockModMeta: hard 10, setResistance(max(10000, 50)) → 30000
		f.shrineRock.forEach { check(it, 10f, 30000f / 5f, SoundType.STONE, true) }
		// hard 2, setResistance(max(5, 10)) → 30
		f.roofTile.forEach { check(it, 2f, 30f / 5f, SoundType.STONE, true) }
		f.livingrockDark.forEach { check(it, 2f, 30f / 5f, SoundType.STONE, true) }
		// дерево: hard 3, setResistance(max(100, 15)) → 300; hard 100, setResistance(max(1000, 500)) → 3000
		check(f.dwarfPlanks, 3f, 300f / 5f, SoundType.WOOD, false)
		f.yggDecor.forEach { check(it, 100f, 3000f / 5f, SoundType.WOOD, false) }
		// стекло: hard 1, setResistance(6000) → 18000, (600) → 1800
		f.shrineLight.forEach { check(it, 1f, 18000f / 5f, SoundType.GLASS, false) }
		f.shrineGlass.forEach { check(it, 1f, 1800f / 5f, SoundType.GLASS, false) }
		// панель: setHardness(1) → 5, затем setResistance(600) → 1800
		f.shrinePanel.forEach { check(it, 1f, 1800f / 5f, SoundType.GLASS, false) }
		// setHardness(10) → 50, setResistance(10000) → 30000
		check(f.shrinePillar, 10f, 30000f / 5f, SoundType.STONE, true)
		check(f.dwarfLantern, 10f, 30000f / 5f, SoundType.STONE, true)
		// лестница BlockStairs: твёрдость, взрывоустойчивость и звук блока-источника
		check(f.shrineRockWhiteStairs, 10f, 30000f / 5f, SoundType.STONE, true)
		f.livingrockDarkStairs.forEach { check(it, 2f, 30f / 5f, SoundType.STONE, true) }
		check(f.dwarfPlanksStairs, 3f, 300f / 5f, SoundType.WOOD, false)
		check(f.livingcobbleStairs, 2f, 180f / 5f, SoundType.STONE, true)
		// плита Botania: своей твёрдости нет; автор ставит 1.5 (→ 7.5) всем, кроме черепицы и тёмного живого камня
		check(f.livingcobbleSlab, 1.5f, 7.5f / 5f, SoundType.STONE, true)
		check(f.shrineRockWhiteSlab, 1.5f, 7.5f / 5f, SoundType.STONE, true)
		check(f.dwarfPlanksSlab, 1.5f, 7.5f / 5f, SoundType.WOOD, false)
		f.roofTileSlabs.forEach { check(it, 0f, 0f, SoundType.STONE, true) }
		f.livingrockDarkSlabs.forEach { check(it, 0f, 0f, SoundType.STONE, true) }
		// стена BlockWall: твёрдость и взрывоустойчивость блока-источника, у блоков Botania — из Botania r1.8-249
		f.livingrockDarkWalls.forEach { check(it, 2f, 30f / 5f, SoundType.STONE, true) }
		check(f.elvenSandstoneWalls[0], 1f, 15f / 5f, SoundType.STONE, true)
		check(f.elfQuartzWall, 0.8f, 30f / 5f, SoundType.STONE, true)
		// забор и калитка: setHardness(2) → 10, setResistance(5) → 15; люк: setHardness(3) → 15
		for (block in listOf(f.livingwoodFence, f.livingwoodBarkFence, f.dreamwoodFence, f.dreamwoodBarkFence, f.livingwoodFenceGate, f.livingwoodBarkFenceGate, f.dreamwoodFenceGate, f.dreamwoodBarkFenceGate))
			check(block, 2f, 15f / 5f, SoundType.WOOD, false)
		check(f.dwarfTrapDoor, 3f, 15f / 5f, SoundType.WOOD, false)

		helper.assertTrue(f.shrineLight.all { it.defaultBlockState().lightEmission == 15 } && f.dwarfLantern.defaultBlockState().lightEmission == 15, "shrine light and dwarf lantern glow")
		helper.succeed()
	}

	/** Деревянный блок 1.7.10 горит в печи 300 тиков — по материалу, раньше обработчиков топлива модов */
	@JvmStatic
	@GameTest(template = "empty")
	fun woodBurns(helper: GameTestHelper) {
		val f = AlfheimFluffBlocks
		for (block in listOf(f.dwarfPlanks, f.dwarfPlanksStairs, f.dwarfPlanksSlab, f.yggDecor[0], f.livingwoodFence, f.dreamwoodFenceGate, f.dwarfTrapDoor))
			helper.assertTrue(ForgeHooks.getBurnTime(ItemStack(block), RecipeType.SMELTING) == 300, "${BuiltInRegistries.BLOCK.getKey(block)} burns ${ForgeHooks.getBurnTime(ItemStack(block), RecipeType.SMELTING)}")
		for (block in listOf(f.shrineRock[0], f.livingcobbleSlab, AlfheimBlocks.elvenSand))
			helper.assertTrue(ForgeHooks.getBurnTime(ItemStack(block), RecipeType.SMELTING) == 0, "${BuiltInRegistries.BLOCK.getKey(block)} is not fuel")
		helper.succeed()
	}

	/** Теги формы и инструмента */
	@JvmStatic
	@GameTest(template = "empty")
	fun decorTags(helper: GameTestHelper) {
		val f = AlfheimFluffBlocks
		for (wall in f.livingrockDarkWalls + f.elvenSandstoneWalls + listOf(f.livingcobbleWall, f.elfQuartzWall))
			helper.assertTrue(wall.defaultBlockState().`is`(BlockTags.WALLS), "${BuiltInRegistries.BLOCK.getKey(wall)} is a wall")
		for (fence in listOf(f.livingwoodFence, f.livingwoodBarkFence, f.dreamwoodFence, f.dreamwoodBarkFence)) {
			val state = fence.defaultBlockState()
			helper.assertTrue(state.`is`(BlockTags.FENCES) && !state.`is`(BlockTags.WOODEN_FENCES) && state.`is`(BlockTags.MINEABLE_WITH_AXE), "${BuiltInRegistries.BLOCK.getKey(fence)}: fence, not wooden fence, axe")
		}
		helper.assertTrue(f.dwarfTrapDoor.defaultBlockState().`is`(BlockTags.TRAPDOORS), "dwarf trapdoor is a trapdoor")
		// setHarvestLevelI("pickaxe", 2) → железная кирка; Wisdomwood: кирка 1 и топор по материалу
		helper.assertTrue(f.livingrockDarkWalls.all { it.defaultBlockState().`is`(BlockTags.NEEDS_IRON_TOOL) }, "dark livingrock walls need iron")
		helper.assertTrue(f.yggDecor[0].defaultBlockState().let { it.`is`(BlockTags.MINEABLE_WITH_PICKAXE) && it.`is`(BlockTags.MINEABLE_WITH_AXE) }, "wisdomwood: pickaxe and axe")
		helper.assertTrue(f.shrineRock[0].defaultBlockState().`is`(BlockTags.NEEDS_IRON_TOOL), "shrine rock needs iron")
		helper.succeed()
	}

	/** Двойная плита 1.7.10 — состояние type=double: роняет две одинарные */
	@JvmStatic
	@GameTest(template = "empty")
	fun doubleSlabDropsTwo(helper: GameTestHelper) {
		val slab = AlfheimFluffBlocks.livingcobbleSlab
		helper.assertTrue(AlfheimFluffBlocks.livingcobbleSlabFull === slab && AlfheimFluffBlocks.roofTileSlabsFull === AlfheimFluffBlocks.roofTileSlabs, "Full slab fields are the slabs")
		val pos = helper.absolutePos(BlockPos(0, 1, 0))
		fun drops(type: SlabType) = Block.getDrops(slab.defaultBlockState().setValue(SlabBlock.TYPE, type), helper.level, pos, null).sumOf { if (it.item == slab.asItem()) it.count else 100 }
		helper.assertTrue(drops(SlabType.BOTTOM) == 1 && drops(SlabType.TOP) == 1 && drops(SlabType.DOUBLE) == 2, "slab drops ${drops(SlabType.BOTTOM)} / ${drops(SlabType.DOUBLE)}")
		helper.succeed()
	}

	/**
	 * `BlockModFence.canConnectFenceTo`: забор автора тянется к любому забору и к своей калитке; заборы ванилы к нему — нет
	 * (не `minecraft:wooden_fences`). Стены соединяются друг с другом (тег `minecraft:walls`)
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun fencesAndWallsConnect(helper: GameTestHelper) {
		val f = AlfheimFluffBlocks
		val a = BlockPos(0, 1, 0)
		val b = a.east()
		fun shaped(pos: BlockPos) = Block.updateFromNeighbourShapes(helper.getBlockState(pos), helper.level, helper.absolutePos(pos))

		helper.setBlock(a, f.livingwoodFence)
		helper.setBlock(b, Blocks.OAK_FENCE)
		helper.assertTrue(shaped(a).getValue(CrossCollisionBlock.EAST), "author's fence connects to the oak fence")
		helper.assertTrue(!shaped(b).getValue(CrossCollisionBlock.WEST), "oak fence does not connect to the author's fence")
		helper.setBlock(b, f.dreamwoodFence)
		helper.assertTrue(shaped(a).getValue(CrossCollisionBlock.EAST) && shaped(b).getValue(CrossCollisionBlock.WEST), "author's fences connect")
		// калитка повёрнута вдоль забора, к которому не примыкает: своя калитка всё равно соединяется
		helper.setBlock(b, f.livingwoodFenceGate.defaultBlockState().setValue(FenceGateBlock.FACING, Direction.EAST))
		helper.assertTrue(shaped(a).getValue(CrossCollisionBlock.EAST), "fence connects to its gate in any direction")

		helper.setBlock(a, f.livingrockDarkWalls[0])
		helper.setBlock(b, f.elvenSandstoneWalls[0])
		helper.assertTrue(shaped(a).getValue(WallBlock.EAST_WALL) != WallSide.NONE && shaped(b).getValue(WallBlock.WEST_WALL) != WallSide.NONE, "walls connect")
		helper.succeed()
	}

	/** Ось столба святилища — по стороне, на которую его ставят, как `onBlockPlaced` 1.7.10 */
	@JvmStatic
	@GameTest(template = "empty")
	fun pillarAxis(helper: GameTestHelper) {
		val pillar = AlfheimFluffBlocks.shrinePillar
		val player = helper.makeMockPlayer()
		val pos = helper.absolutePos(BlockPos(0, 1, 0))
		for ((side, axis) in listOf(Direction.UP to Direction.Axis.Y, Direction.NORTH to Direction.Axis.Z, Direction.EAST to Direction.Axis.X)) {
			val context = BlockPlaceContext(player, InteractionHand.MAIN_HAND, ItemStack(pillar), BlockHitResult(Vec3.atCenterOf(pos), side, pos, false))
			helper.assertTrue(pillar.getStateForPlacement(context)?.getValue(RotatedPillarBlock.AXIS) == axis, "pillar placed on $side")
		}
		helper.succeed()
	}

	/** Стекло святилища: грань между блоками одного варианта не рисуется, между разными — рисуется */
	@JvmStatic
	@GameTest(template = "empty")
	fun shrineGlassFaces(helper: GameTestHelper) {
		val glass = AlfheimFluffBlocks.shrineGlass
		helper.assertTrue(glass[0].defaultBlockState().skipRendering(glass[0].defaultBlockState(), Direction.UP), "same variant")
		helper.assertTrue(!glass[0].defaultBlockState().skipRendering(glass[1].defaultBlockState(), Direction.UP), "another variant")
		helper.succeed()
	}

	/**
	 * Вкладки: во вкладке Alfheim — все блоки и предметы автора, кроме мана-льда (его нет и у автора), стены из
	 * эльфийского кварца — она во вкладке Botania сразу за лестницей из эльфийского кварца (врезка H-023), — и
	 * эльфийских ресурсов, которых автор во вкладку не выдавал (`ElvenResourcesMetas.displayBlackList`); накопитель
	 * разлома выдаётся по аномалиям (КТ-3). Эльфийские ресурсы — в порядке номеров, как выдавал `getSubItems`.
	 * Черепица Botania 1.7.10 — во вкладке Botania перед первым азулежу, как в Botania 1.7.10. Закопанных радужных
	 * лепестков во вкладке нет и у автора (`BlockRainbowGrass.getSubBlocks`), сердцевин барьерного, грозового и адского
	 * деревьев — тоже: `getSubBlocks` их брёвен выдавал только metadata 0; ягод магических деревьев и листвы печального
	 * дуба вкладка автора не выдавала
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun creativeTabs(helper: GameTestHelper) {
		CreativeModeTabs.tryRebuildTabContents(helper.level.enabledFeatures(), true, helper.level.registryAccess())
		val alfheim = AlfheimTab.tab.get().displayItems.map { it.item }
		val hidden = listOf(AlfheimBlocks.manaIce.asItem(), AlfheimFluffBlocks.elfQuartzWall.asItem(), BotaniaBlocks1710.roofTile.asItem(), AlfheimBlocks.rainbowGrass[4].asItem(), AlfheimBlocks.sadOakLeaves.asItem()) +
			listOf(AlfheimBlocks.barrierWood, AlfheimBlocks.lightningWood, AlfheimBlocks.netherWood).map { it[1].asItem() } +
			listOf(AlfheimBlocks.barrierBerry, AlfheimBlocks.calicoBerry, AlfheimBlocks.circuitBerry, AlfheimBlocks.lightningBerry, AlfheimBlocks.netherBerry, AlfheimBlocks.sealingBerry).map { it.asItem() } +
			listOf(ElvenResourcesMetas.ElvenWeed, ElvenResourcesMetas.WisdomBottle, ElvenResourcesMetas.RiftDrive).map { AlfheimItems.elvenResource[it.I] }
		// рог души — дважды: обычный и заряженный (metadata 1); брызгающее зелье — по разу на варево Botania, кроме
		// запасного, как у автора
		val brews = BotaniaAPI.instance().brewRegistry!!.count { it !== BotaniaBrews.fallbackBrew }
		val expected = LegacyRegistration.items.keys.filter { it !in hidden } + AlfheimItems.soulHorn + List(brews - 1) { AlfheimItems.splashPotion }
		helper.assertTrue(alfheim.toSet() == expected.toSet() && alfheim.size == expected.size, "Alfheim tab: ${alfheim.size} items, expected ${expected.size}; missing ${expected - alfheim.toSet()}")
		helper.assertTrue(AlfheimTab.tab.get().displayItems.filter { it.item == AlfheimItems.soulHorn }.map { it.meta } == listOf(0, 1), "soul horns in the tab")
		val resources = alfheim.filterIsInstance<ItemElvenResource>().map { it.meta }
		helper.assertTrue(resources == resources.sorted(), "elven resources in the tab: $resources")

		val botania = BuiltInRegistries.CREATIVE_MODE_TAB.get(BotaniaRegistries.BOTANIA_TAB_KEY)!!.displayItems.map { it.item }
		val stairs = botania.indexOf(BotaniaBlocks.elfQuartzStairs.asItem())
		helper.assertTrue(stairs >= 0 && botania.indexOf(AlfheimFluffBlocks.elfQuartzWall.asItem()) == stairs + 1, "elven quartz wall is not right after elven quartz stairs in the Botania tab")
		val azulejo = botania.indexOf(BotaniaBlocks.azulejo0.asItem())
		helper.assertTrue(azulejo > 0 && botania.indexOf(BotaniaBlocks1710.roofTile.asItem()) == azulejo - 1, "Botania roof tile is not right before the first azulejo in the Botania tab")
		helper.succeed()
	}

	/**
	 * Черепица Botania 1.7.10 (`ModBlocks.customBrick`, 3), которую вернул порт: id по правилу порта, старое имя Botania
	 * в `legacy_ids.json`; камень: `setHardness(2)` → 10, `setResistance(5)` → 15; добывается киркой, роняет себя
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun botaniaRoofTile(helper: GameTestHelper) {
		val tile = BotaniaBlocks1710.roofTile
		helper.assertTrue(BuiltInRegistries.BLOCK.getKey(tile) == ResourceLocation(MODID, "custom_brick3"), "roof tile id ${BuiltInRegistries.BLOCK.getKey(tile)}")
		helper.assertTrue(LegacyIds.block("Botania:customBrick", 3)?.id == ResourceLocation(MODID, "custom_brick3") && LegacyIds.item("Botania:customBrick", 3)?.id == ResourceLocation(MODID, "custom_brick3"), "legacy_ids.json: Botania:customBrick 3")
		val state = tile.defaultBlockState()
		helper.assertTrue(state.getDestroySpeed(helper.level, BlockPos.ZERO) == 2f && tile.explosionResistance == 15f / 5f && state.soundType == SoundType.STONE, "roof tile properties")
		helper.assertTrue(state.requiresCorrectToolForDrops() && state.`is`(BlockTags.MINEABLE_WITH_PICKAXE), "roof tile needs a pickaxe")
		val pos = helper.absolutePos(BlockPos(0, 1, 0))
		val drops = Block.getDrops(state, helper.level, pos, null, null, ItemStack(Items.WOODEN_PICKAXE))
		helper.assertTrue(drops.size == 1 && drops[0].item == tile.asItem() && drops[0].count == 1, "roof tile drops $drops")
		helper.succeed()
	}

	/**
	 * Стены автора из кирпичей живого камня нет (решение автора): в коде автора — стена Botania 1.20.1, на неё ведёт
	 * старое имя в `legacy_ids.json` (в постройках автора она стоит на арене и в руинах)
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun livingrockBrickWallIsBotanias(helper: GameTestHelper) {
		val wall = ResourceLocation("botania", "livingrock_bricks_wall")
		helper.assertTrue(AlfheimFluffBlocks.livingrockBrickWall === BotaniaBlocks.livingrockBrickWall, "livingrockBrickWall is the Botania wall")
		helper.assertTrue(!BuiltInRegistries.BLOCK.containsKey(ResourceLocation(MODID, "livingrock1_wall")), "the author's wall is registered")
		helper.assertTrue(LegacyIds.block("$MODID:livingrock1Wall")?.id == wall && LegacyIds.item("$MODID:livingrock1Wall")?.id == wall, "legacy_ids.json: $MODID:livingrock1Wall")
		helper.succeed()
	}
}
