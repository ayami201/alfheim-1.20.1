package alfheim.port.test

import alfheim.api.ModInfo.MODID
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.BlockNiflheim
import alfheim.common.block.BlockNiflheimIce
import alfheim.common.block.BlockNiflheimIce.IceType
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenResourcesMetas
import alfheim.common.potion.PotionEternity
import alfheim.port.legacy.updateTick
import alfheim.port.loot.FortuneCount
import alfheim.port.registry.LegacyIds
import com.mojang.authlib.GameProfile
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.tags.ItemTags
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.item.FallingBlockEntity
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.GameType
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.RotatedPillarBlock.AXIS
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.util.FakePlayerFactory
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate
import java.util.UUID

/**
 * КТ-2, партия 9а-3а: камень Нифльхейма (`BlockNiflheim`: 7 вариантов, колонна и руническая колонна с осью) и твердь
 * Хельхейма (`BlockPattern` ASJCore); партия 9а-3б: лёд Нифльхейма (`BlockNiflheimIce`). Числа и правила — из классов
 * автора
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

	private val ice get() = AlfheimBlocks.poisonIce

	private fun ice(type: IceType) = ice.defaultBlockState().setValue(BlockNiflheimIce.TYPE, type)

	/**
	 * Лёд Нифльхейма: `alfheim:niflheim_ice`, старое имя `NiflheimIce`, название — из переводов автора; во вкладке его нет
	 * (`setCreativeTab(null)`). metadata 0–2 — свойство `type`: обычный, замораживающий, вечный
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun niflheimIceRegistry(helper: GameTestHelper) {
		helper.assertTrue(BuiltInRegistries.BLOCK.getKey(ice) == ResourceLocation(MODID, "niflheim_ice") && ice is BlockNiflheimIce, "id")
		helper.assertTrue(LegacyIds.block("$MODID:NiflheimIce")?.id == ResourceLocation(MODID, "niflheim_ice"), "legacy id")
		helper.assertTrue(ItemStack(ice).hoverName.string == "Niflheim Ice Of Eternity", "name: ${ItemStack(ice).hoverName.string}")
		helper.assertTrue(BlockNiflheimIce.TYPE.possibleValues.map { it.serializedName } == listOf("normal", "freezing", "permanent"), "types")
		helper.assertTrue(ice.defaultBlockState().getValue(BlockNiflheimIce.TYPE) == IceType.NORMAL, "normal by default")
		CreativeModeTabs.tryRebuildTabContents(helper.level.enabledFeatures(), true, helper.level.registryAccess())
		helper.assertTrue(AlfheimTab.tab.get().displayItems.none { it.item == ice.asItem() }, "not in the tab")
		helper.succeed()
	}

	/**
	 * Свойства льда Нифльхейма любого вида: не ломается (кирка без кулона — 0), взрыв его не берёт (6 000 000 → 3 600 000),
	 * звук стекла, скользкий, как лёд (0,98), свет проходит, рисуется полупрозрачным; кирка, железная и лучше; цвет на
	 * карте — лёд; случайные тики. Рамка столкновений — куб без 0,01 с каждой стороны, рамка выделения — весь куб; при этом
	 * он нормальный куб 1.7.10 (материал непрозрачный, рисуется кубом): опора со всех сторон, на нём появляются мобы, сигнал
	 * проводит, в нём задыхаются, тень на углах соседей — 0,2. Мобы его обходят (путь 1.7.10 — по материалу). Грани рядом с
	 * собой (любого вида) не рисует. Лута нет — и с шёлковым касанием
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun niflheimIceProperties(helper: GameTestHelper) {
		val level = helper.level
		val abs = helper.absolutePos(BlockPos(1, 1, 1))
		val pick = ItemStack(Items.DIAMOND_PICKAXE)
		val silk = ItemStack(Items.DIAMOND_PICKAXE).apply { enchant(Enchantments.SILK_TOUCH, 1) }
		val player = FakePlayerFactory.get(level, GameProfile(UUID.randomUUID(), "niflheim_ice")).apply {
			setGameMode(GameType.SURVIVAL)
			setItemInHand(InteractionHand.MAIN_HAND, pick.copy())
		}
		helper.assertTrue(ice.explosionResistance == 3600000f && ice.friction == 0.98f, "resistance ${ice.explosionResistance}, slipperiness ${ice.friction}")
		for (type in IceType.entries) {
			val state = ice(type)
			helper.assertTrue(state.getDestroySpeed(level, abs) == -1f && state.getDestroyProgress(player, level, abs) == 0f, "$type: unbreakable")
			helper.assertTrue(state.soundType == SoundType.GLASS && state.getMapColor(level, abs) == MapColor.ICE && state.isRandomlyTicking, "$type: glass sound, ice color, random ticks")
			helper.assertTrue(state.getLightBlock(level, abs) == 0 && state.propagatesSkylightDown(level, abs) && !state.isSolidRender(level, abs), "$type: light passes")
			helper.assertTrue(state.requiresCorrectToolForDrops() && state.`is`(BlockTags.MINEABLE_WITH_PICKAXE) && state.`is`(BlockTags.NEEDS_IRON_TOOL), "$type: iron pickaxe")
			helper.assertTrue(state.getCollisionShape(level, abs).bounds() == AABB(0.01, 0.01, 0.01, 0.99, 0.99, 0.99), "$type: collision ${state.getCollisionShape(level, abs).bounds()}")
			helper.assertTrue(Block.isShapeFullBlock(state.getShape(level, abs)) && Direction.entries.all { state.isFaceSturdy(level, abs, it) }, "$type: full outline and support")
			helper.assertTrue(state.isValidSpawn(level, abs, EntityType.ZOMBIE), "$type: mobs appear on it")
			helper.assertTrue(state.isRedstoneConductor(level, abs) && state.isSuffocating(level, abs) && state.isViewBlocking(level, abs), "$type: normal cube")
			helper.assertTrue(state.getShadeBrightness(level, abs) == 0.2f, "$type: corner shading ${state.getShadeBrightness(level, abs)}")
			helper.assertTrue(!state.isPathfindable(level, abs, PathComputationType.LAND) && !state.isPathfindable(level, abs, PathComputationType.AIR), "$type: mobs go around")
			helper.assertTrue(state.skipRendering(ice(IceType.PERMANENT), Direction.UP) && !state.skipRendering(Blocks.ICE.defaultBlockState(), Direction.UP), "$type: faces")
			helper.assertTrue(Block.getDrops(state, level, abs, null, player, pick).isEmpty() && Block.getDrops(state, level, abs, null, player, silk).isEmpty(), "$type: no loot")
		}
		helper.succeed()
	}

	/**
	 * Кто стоит на льду Нифльхейма или вошёл в него (`onEntityWalking`, `onEntityCollidedWithBlock`), увязает, как в паутине
	 * (`setInWeb`: шаг в 4 раза короче, скорость гаснет), и получает замедление III на 25 тиков и «Вечность» (атака) на
	 * 100; молоко их не снимает (`PotionEffectU`). Паука паутина не держит — в 1.7.10 его `setInWeb` ничего не делал.
	 * Защита существ Нифльхейма, игрока в творчестве и с кулоном — КТ-5. Мобы — с ИИ: моб без ИИ в 1.20.1 не двигается
	 * совсем и блоков не касается
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun niflheimIceStuck(helper: GameTestHelper) {
		val level = helper.level
		val center = helper.absolutePos(BlockPos(0, 170, 0))
		fun area(floor: BlockState) {
			for (i in -3..4) for (k in -2..2) {
				level.setBlock(center.offset(i, -2, k), Blocks.STONE.defaultBlockState(), 3)
				level.setBlock(center.offset(i, -1, k), floor, 3)
				for (j in 0..3) level.setBlock(center.offset(i, j, k), Blocks.AIR.defaultBlockState(), 3)
			}
		}
		area(ice(IceType.NORMAL))
		fun spawn(type: EntityType<out Mob>, dx: Double) = type.create(level)!!.apply {
			moveTo(center.x + dx, center.y.toDouble(), center.z + 0.5, 0f, 0f)
			level.addFreshEntity(this)
		}
		val pig = spawn(EntityType.PIG, -1.5)
		val spider = spawn(EntityType.SPIDER, 2.0)
		var from = 0.0 to 0.0
		helper.startSequence().thenWaitUntil {
			helper.assertTrue(pig.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) && spider.hasEffect(MobEffects.MOVEMENT_SLOWDOWN), "the ice slows them down")
		}.thenExecute {
			for (mob in listOf(pig, spider)) {
				val slowness = mob.getEffect(MobEffects.MOVEMENT_SLOWDOWN)!!
				val eternity = mob.getEffect(PotionEternity)
				helper.assertTrue(slowness.amplifier == 2 && slowness.duration in 20..25 && slowness.curativeItems.isEmpty(), "${mob.type}: $slowness")
				helper.assertTrue(eternity != null && eternity.amplifier == PotionEternity.ATTACK && eternity.duration in 95..100 && eternity.curativeItems.isEmpty(), "${mob.type}: $eternity")
			}
			from = pig.x to spider.x
			pig.deltaMovement = Vec3(0.5, 0.0, 0.0)
			spider.deltaMovement = Vec3(0.5, 0.0, 0.0)
		}.thenExecuteAfter(3) {
			helper.assertTrue(pig.x - from.first < 0.2, "the pig is stuck: moved ${pig.x - from.first}")
			helper.assertTrue(spider.x - from.second > 0.4, "the web does not hold the spider: moved ${spider.x - from.second}")
			pig.discard()
			spider.discard()
			area(Blocks.AIR.defaultBlockState())
		}.thenSucceed()
	}

	/**
	 * Замораживающий лёд (metadata 1; его ставит портал Нифльхейма — КТ-6) на своём тике делает соседнюю воду таким же льдом
	 * с тиком через 1, сам тикает снова через 5 и снимает флаг `destroyNextTick`: волна проходит всю воду. Когда воды
	 * рядом нет, а флаг поднят (его поднимает конец тика мира — тоже портал, КТ-6; здесь — тест), лёд раскалывается в
	 * воздух — вода ушла. На плотном льду он не тает
	 */
	@JvmStatic
	@GameTest(template = "empty", batch = "niflheim_ice")
	fun niflheimIceFreezes(helper: GameTestHelper) {
		val level = helper.level
		val start = helper.absolutePos(BlockPos(0, 182, 0))
		val channel = (0..3).map { start.east(it) }
		fun area(fill: Boolean) {
			for (i in -1..4) for (k in -1..1) {
				level.setBlock(start.offset(i, -1, k), (if (fill) Blocks.PACKED_ICE else Blocks.AIR).defaultBlockState(), 3)
				level.setBlock(start.offset(i, 0, k), (if (!fill) Blocks.AIR else if (k == 0 && i in 0..3) Blocks.WATER else Blocks.STONE).defaultBlockState(), 3)
				level.setBlock(start.offset(i, 1, k), Blocks.AIR.defaultBlockState(), 3)
			}
		}
		area(true)
		// как портал Нифльхейма: лёд на месте воды, тик через 1, флаг снят
		level.setBlock(start, ice(IceType.FREEZING), 2)
		level.scheduleTick(start, ice, 1)
		BlockNiflheimIce.destroyNextTick = false
		helper.startSequence().thenWaitUntil {
			helper.assertTrue(channel.all { level.getBlockState(it) == ice(IceType.FREEZING) }, "the wave froze the water: ${channel.map { level.getBlockState(it) }}")
		}.thenExecute {
			helper.assertTrue(channel.all { level.blockTicks.hasScheduledTick(it, ice) }, "the ice ticks on")
			BlockNiflheimIce.destroyNextTick = true
		}.thenWaitUntil {
			helper.assertTrue(channel.all { level.getBlockState(it).isAir }, "the ice shattered: ${channel.map { level.getBlockState(it) }}")
		}.thenExecute {
			area(false)
		}.thenSucceed()
	}

	/**
	 * Таяние (`updateTick` — случайный тик): вне Нифльхейма (измерения ещё нет — КТ-6), пока горит огонь (`doFireTick`), лёд
	 * тает в воздух с шансом 1/100 — если он не вечный и стоит не на плотном льду и не на таком же льду. За 2000 тиков тает
	 * почти наверняка (не растает с шансом 0,99²⁰⁰⁰ ≈ 2·10⁻⁹), в прочих случаях — никогда
	 */
	@JvmStatic
	@GameTest(template = "empty", batch = "niflheim_ice")
	fun niflheimIceMelts(helper: GameTestHelper) {
		val level = helper.level
		val below = helper.absolutePos(BlockPos(0, 188, 0))
		val at = below.above()
		val fireTick = level.gameRules.getRule(GameRules.RULE_DOFIRETICK)
		fun melts(state: BlockState, under: Block, fire: Boolean = true): Boolean {
			level.setBlock(below, under.defaultBlockState(), 3)
			level.setBlock(at, state, 3)
			fireTick.set(fire, level.server)
			repeat(2000) { if (level.getBlockState(at).`is`(ice)) ice.updateTick(level, at.x, at.y, at.z, level.random) }
			fireTick.set(true, level.server)
			return level.getBlockState(at).isAir
		}
		helper.assertTrue(melts(ice(IceType.NORMAL), Blocks.STONE), "melts on stone")
		helper.assertTrue(melts(ice(IceType.NORMAL), Blocks.AIR), "melts above air")
		helper.assertTrue(!melts(ice(IceType.PERMANENT), Blocks.STONE), "permanent ice does not melt")
		helper.assertTrue(!melts(ice(IceType.NORMAL), Blocks.PACKED_ICE), "not on packed ice")
		helper.assertTrue(!melts(ice(IceType.NORMAL), ice), "not on Niflheim ice")
		helper.assertTrue(!melts(ice(IceType.NORMAL), Blocks.STONE, false), "not while fire does not tick")
		level.setBlock(at, Blocks.AIR.defaultBlockState(), 3)
		level.setBlock(below, Blocks.AIR.defaultBlockState(), 3)
		helper.succeed()
	}
}
