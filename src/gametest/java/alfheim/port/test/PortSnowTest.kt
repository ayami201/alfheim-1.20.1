package alfheim.port.test

import alfheim.AlfheimCore
import alfheim.api.ModInfo.MODID
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.BlockSnowGrass
import alfheim.common.block.BlockSnowLayer
import alfheim.common.core.util.AlfheimTab
import alfheim.port.legacy.*
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
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.GameType
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.IPlantable
import net.minecraftforge.common.util.FakePlayerFactory
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate
import ru.vamig.worldengine.WE_PerlinNoise
import java.util.UUID
import kotlin.math.abs

/**
 * КТ-2, партия 9а-2: снежная трава (`BlockSnowGrass`) и слой снега (`BlockSnowLayer`). Числа и правила — из классов
 * автора. Зима (`AlfheimCore.winter`) и дождь меняются только внутри одного вызова теста и сразу возвращаются: тесты
 * идут одновременно
 */
@GameTestHolder(MODID)
@PrefixGameTestTemplate(false)
object PortSnowTest {

	private val grass get() = AlfheimBlocks.snowGrass as BlockSnowGrass
	private val layer get() = AlfheimBlocks.snowLayer as BlockSnowLayer

	/** Случайные числа тика: `nextInt(n)` всегда выпадает на [value] */
	private class FixedRandom(private val value: Int): RandomSource by RandomSource.create(0) {
		override fun nextInt(bound: Int) = value
	}

	/** Каменный пол 5 × 5 на высоте [dy] над точкой теста, над ним — воздух на 5 блоков; возвращает клетку над серединой */
	private fun floor(helper: GameTestHelper, dy: Int): BlockPos {
		val center = helper.absolutePos(BlockPos(0, dy, 0))
		for (i in -2..2) for (k in -2..2) {
			helper.level.setBlock(center.offset(i, -1, k), Blocks.STONE.defaultBlockState(), 3)
			for (j in 0..4) helper.level.setBlock(center.offset(i, j, k), Blocks.AIR.defaultBlockState(), 3)
		}
		return center
	}

	private fun clear(helper: GameTestHelper, center: BlockPos) {
		for (i in -2..2) for (k in -2..2) for (j in -3..4) helper.level.setBlock(center.offset(i, j, k), Blocks.AIR.defaultBlockState(), 3)
	}

	/** Зима и дождь на время [action] */
	private fun <T> weather(helper: GameTestHelper, winter: Boolean, rain: Boolean, action: () -> T): T {
		val oldWinter = AlfheimCore.winter
		val oldRain = helper.level.getRainLevel(1f)
		AlfheimCore.winter = winter
		helper.level.setRainLevel(if (rain) 1f else 0f)
		try {
			return action()
		} finally {
			AlfheimCore.winter = oldWinter
			helper.level.setRainLevel(oldRain)
		}
	}

	private fun layers(n: Int): BlockState = layer.defaultBlockState().setValue(BlockSnowLayer.LAYERS, n)

	/**
	 * Имена 1.7.10 `SnowGrass`, `SnowLayer` → `alfheim:snow_grass`, `alfheim:snow_layer`, названия — из переводов
	 * автора; Ore Dictionary `grassSnow`, `snowLayer` — теги `alfheim:grass_snow`, `alfheim:snow_layer`; слой снега — в
	 * `minecraft:snow`. Во вкладке оба блока — только зимой, после мягких блоков хранения
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun snowRegistry(helper: GameTestHelper) {
		for ((block, id, legacy, name) in listOf(listOf(grass, "snow_grass", "SnowGrass", "Snowy Grass"), listOf(layer, "snow_layer", "SnowLayer", "Snow Layer"))) {
			block as Block
			helper.assertTrue(BuiltInRegistries.BLOCK.getKey(block) == ResourceLocation(MODID, id as String), "$id id")
			helper.assertTrue(LegacyIds.block("$MODID:$legacy", 0)?.id == ResourceLocation(MODID, id), "$legacy legacy id")
			helper.assertTrue(ItemStack(block).hoverName.string == name, "name of $id: ${ItemStack(block).hoverName.string}")
		}
		helper.assertTrue(grass.defaultBlockState().`is`(BlockTags.create(ResourceLocation(MODID, "grass_snow"))) && ItemStack(grass).`is`(ItemTags.create(ResourceLocation(MODID, "grass_snow"))), "grassSnow tag")
		helper.assertTrue(layer.defaultBlockState().`is`(BlockTags.create(ResourceLocation(MODID, "snow_layer"))) && ItemStack(layer).`is`(ItemTags.create(ResourceLocation(MODID, "snow_layer"))), "snowLayer tag")
		helper.assertTrue(layer.defaultBlockState().`is`(BlockTags.SNOW) && !grass.defaultBlockState().`is`(BlockTags.SNOW), "minecraft:snow")

		val tab = AlfheimTab.tab.get()
		val parameters = CreativeModeTab.ItemDisplayParameters(helper.level.enabledFeatures(), true, helper.level.registryAccess())
		for (winter in listOf(true, false)) {
			val items = weather(helper, winter, false) {
				tab.buildContents(parameters)
				tab.displayItems.map { it.item }
			}
			val at = items.indexOf(grass.asItem())
			if (winter) helper.assertTrue(at > 0 && items[at + 1] == layer.asItem() && items[at - 1] == AlfheimBlocks.softStorage.last().asItem(), "winter tab")
			else helper.assertTrue(at < 0 && layer.asItem() !in items, "no snow outside winter")
		}
		tab.buildContents(parameters)
		helper.succeed()
	}

	/**
	 * Снежная трава: твёрдость 0,6 (взрывоустойчивость 0,6), звук травы, лопата, случайные тики, непрозрачный куб;
	 * роняет землю, с шёлковым касанием — себя. Слой снега: твёрдость 0,1, звук снега, лопата и нужен инструмент
	 * (материал снега), свет проходит, не твёрдый, поршень его ломает; рамка — (слоёв) / 8, столкновения — (слоёв − 1) / 8;
	 * заменяем при слоях < 8; роняет столько снежков, сколько слоёв, с шёлковым касанием — тоже
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun snowProperties(helper: GameTestHelper) {
		val abs = helper.absolutePos(BlockPos(1, 1, 1))
		val g = grass.defaultBlockState()
		helper.assertTrue(!g.getValue(BlockSnowGrass.PERMANENT) && g.getDestroySpeed(helper.level, abs) == 0.6f && abs(grass.explosionResistance - 0.6f) < 1e-6, "grass hardness")
		helper.assertTrue(g.soundType == SoundType.GRASS && g.`is`(BlockTags.MINEABLE_WITH_SHOVEL) && !g.requiresCorrectToolForDrops() && g.isRandomlyTicking, "grass sound, tool, ticks")
		helper.assertTrue(g.getLightBlock(helper.level, abs) == 15 && g.isSolid, "grass is an opaque cube")
		helper.assertTrue(Block.getDrops(g, helper.level, abs, null).map { it.item } == listOf(Items.DIRT), "grass drops dirt")
		val silk = ItemStack(Items.DIAMOND_SHOVEL).apply { enchant(Enchantments.SILK_TOUCH, 1) }
		helper.assertTrue(Block.getDrops(g, helper.level, abs, null, null, silk).map { it.item } == listOf(grass.asItem()), "grass with silk touch")

		val l = layer.defaultBlockState()
		helper.assertTrue(l.getValue(BlockSnowLayer.LAYERS) == 1 && l.getDestroySpeed(helper.level, abs) == 0.1f && abs(layer.explosionResistance - 0.1f) < 1e-6, "layer hardness")
		helper.assertTrue(l.soundType == SoundType.SNOW && l.`is`(BlockTags.MINEABLE_WITH_SHOVEL) && l.requiresCorrectToolForDrops() && l.isRandomlyTicking, "layer sound, tool, ticks")
		helper.assertTrue(!l.isSolid && !l.blocksMotion() && l.pistonPushReaction == PushReaction.DESTROY && l.canBeReplaced(), "layer material")
		for (n in 1..8) {
			val state = layers(n)
			helper.assertTrue(state.getLightBlock(helper.level, abs) == 0 && state.propagatesSkylightDown(helper.level, abs), "$n layers: light passes")
			val shape = state.getShape(helper.level, abs).bounds()
			helper.assertTrue(abs(shape.maxY - n / 8.0) < 1e-6 && shape.minY == 0.0 && shape.maxX == 1.0, "$n layers: bounds $shape")
			val collision = state.getCollisionShape(helper.level, abs)
			helper.assertTrue(if (n == 1) collision.isEmpty else abs(collision.bounds().maxY - (n - 1) / 8.0) < 1e-6, "$n layers: collision")
			helper.assertTrue(layer.isReplaceable(LevelWithState(helper, abs, state), abs.x, abs.y, abs.z) == (n < 8), "$n layers: replaceable")
			for (tool in listOf(ItemStack(Items.WOODEN_SHOVEL), silk)) {
				val drops = Block.getDrops(state, helper.level, abs, null, null, tool)
				helper.assertTrue(drops.all { it.item == Items.SNOWBALL } && drops.sumOf { it.count } == n, "$n layers: drops $drops")
			}
		}
		helper.succeed()
	}

	/** Мир, в котором в точке [pos] — [state]: заменяемость слоя спрашивают у блока в точке */
	private class LevelWithState(helper: GameTestHelper, private val pos: BlockPos, private val state: BlockState): net.minecraft.world.level.BlockGetter by helper.level {
		override fun getBlockState(pos: BlockPos): BlockState = if (pos == this.pos) state else Blocks.AIR.defaultBlockState()
	}

	/**
	 * Где лежит слой снега (`canPlaceBlockAt` — и установка, и удержание): на непрозрачном кубе, мешающем движению
	 * (камень, снежная трава), на листве, на слое снега в 8 слоёв; не на льду и плотном льду, не на стекле, не на слое
	 * в 7 слоёв. Без опоры (сосед сменился) слой просто пропадает — без лута
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun snowLayerSupport(helper: GameTestHelper) {
		val c = floor(helper, 134)
		val level = helper.level
		val cases = listOf(Blocks.STONE.defaultBlockState() to true, grass.defaultBlockState() to true, Blocks.OAK_LEAVES.defaultBlockState() to true,
			layers(8) to true, layers(7) to false, Blocks.ICE.defaultBlockState() to false, Blocks.PACKED_ICE.defaultBlockState() to false,
			Blocks.GLASS.defaultBlockState() to false)
		for ((below, expected) in cases) {
			level.setBlock(c, below, 3)
			helper.assertTrue(layer.defaultBlockState().canSurvive(level, c.above()) == expected, "on $below: ${!expected}")
		}
		level.setBlock(c, Blocks.STONE.defaultBlockState(), 3)
		level.setBlock(c.above(), layers(5), 3)
		level.setBlock(c, Blocks.AIR.defaultBlockState(), 3)
		helper.assertTrue(level.getBlockState(c.above()).isAir, "unsupported layer is removed")
		helper.assertTrue(level.getEntitiesOfClass(ItemEntity::class.java, AABB(c).inflate(3.0)).isEmpty(), "no loot from a removed layer")
		clear(helper, c)
		helper.succeed()
	}

	/**
	 * Нажатие на слой снега: с ним же в руке — слоем больше (до 8), предмет не тратится и в выживании (ошибка автора, BUGS.md); пустой
	 * рукой — слоем меньше (не меньше 1) и снежок в инвентарь. Прослойка `dropPlayerItemWithRandomChoice`: вещь летит
	 * вперёд по взгляду, флаг 1.7.10 не читал
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun snowLayerActivation(helper: GameTestHelper) {
		val c = floor(helper, 140)
		val level = helper.level
		val player = FakePlayerFactory.get(level, GameProfile(UUID.randomUUID(), "snow_layer_test")).apply { inventory.clearContent() }
		// в творческом режиме предметы не тратятся вообще: трату слоя снега в руке проверяет игрок в выживании
		player.setGameMode(GameType.SURVIVAL)
		helper.assertTrue(!player.isCreative && !player.abilities.instabuild, "the player is in survival")
		fun use(): Boolean = level.getBlockState(c).use(level, player, InteractionHand.MAIN_HAND, BlockHitResult(Vec3.atCenterOf(c), Direction.UP, c, false)).consumesAction()

		player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack(layer, 1))
		level.setBlock(c, layers(1), 3)
		for (n in 2..8) {
			helper.assertTrue(use() && level.getBlockState(c).getValue(BlockSnowLayer.LAYERS) == n, "layer added: $n")
			helper.assertTrue(player.mainHandItem.count == 1, "the snow layer in hand is not used")
		}
		helper.assertTrue(!use() && level.getBlockState(c).getValue(BlockSnowLayer.LAYERS) == 8, "no ninth layer")

		// снежок ложится в первый свободный слот; если это слот руки — рука уже не пустая, как в 1.7.10
		player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY)
		helper.assertTrue(use() && player.mainHandItem.item == Items.SNOWBALL && !use(), "the snowball lands in the empty hand")
		helper.assertTrue(level.getBlockState(c).getValue(BlockSnowLayer.LAYERS) == 7, "one layer taken")
		player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY)
		level.setBlock(c, layers(8), 3)
		player.inventory.selected = 8
		for (n in 7 downTo 1) helper.assertTrue(use() && level.getBlockState(c).getValue(BlockSnowLayer.LAYERS) == n, "layer taken: $n")
		helper.assertTrue(!use() && level.getBlockState(c).getValue(BlockSnowLayer.LAYERS) == 1, "the last layer stays")
		helper.assertTrue(player.inventory.countItem(Items.SNOWBALL) == 7, "snowballs: ${player.inventory.countItem(Items.SNOWBALL)}")

		player.moveTo(c.x + 0.5, c.y + 1.0, c.z + 0.5, 90f, 0f)
		val item = player.dropPlayerItemWithRandomChoice(ItemStack(Items.SNOWBALL), true)
		helper.assertTrue(item != null && item.isAlive && item.level() === level, "dropped item is in the world")
		val motion = item!!.deltaMovement
		helper.assertTrue(motion.x < -0.25 && abs(motion.z) < 0.1, "dropped forward (yaw 90 — towards −x): $motion")
		item.discard()
		clear(helper, c)
		helper.succeed()
	}

	/**
	 * Не зимой снежная трава тает: случайный тик с шансом 1/20 (`meltDelay`) делает её травой и убирает снег над ней —
	 * свой слой и слой ванилы. Вечная (permanent, metadata 1) не тает
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun snowGrassMelt(helper: GameTestHelper) {
		val c = floor(helper, 146)
		val level = helper.level
		val random = RandomSource.create(0)
		weather(helper, false, false) {
			for (above in listOf(layers(3), Blocks.SNOW.defaultBlockState(), Blocks.AIR.defaultBlockState())) {
				level.setBlock(c, grass.defaultBlockState(), 3)
				level.setBlock(c.above(), above, 3)
				var ticks = 0
				while (level.getBlockState(c).block === grass && ticks++ < 1000) grass.updateTick(level, c.x, c.y, c.z, random)
				helper.assertTrue(level.getBlockState(c).`is`(Blocks.GRASS_BLOCK) && level.getBlockState(c.above()).isAir, "melted under $above")
			}
			level.setBlock(c, grass.defaultBlockState().setValue(BlockSnowGrass.PERMANENT, true), 3)
			repeat(1000) { grass.updateTick(level, c.x, c.y, c.z, random) }
			helper.assertTrue(level.getBlockState(c).block === grass, "permanent snow grass does not melt")

			var melted = 0
			repeat(4000) {
				level.setBlock(c, grass.defaultBlockState(), 2)
				grass.updateTick(level, c.x, c.y, c.z, random)
				if (level.getBlockState(c).block !== grass) melted++
			}
			helper.assertTrue(melted in 120..280, "melt chance 1/20: $melted of 4000")
		}
		clear(helper, c)
		helper.succeed()
	}

	/**
	 * Зимой: слой снега ванилы над снежной травой становится своим (столько же слоёв); в дождь (снегопад) на открытой
	 * траве появляется слой и растёт до высоты из шума Перлина WorldEngine по зерну мира; под крышей — тоже (ошибка
	 * автора, BUGS.md); без осадков — нет. Трава распространяется на землю и траву ванилы, если высота осадков в столбце
	 * не выше координаты z (ошибка автора: z вместо y, BUGS.md). В темноте под непрозрачным блоком становится землёй
	 */
	@JvmStatic
	@GameTest(template = "empty", timeoutTicks = 200)
	fun snowGrassWinter(helper: GameTestHelper) {
		val c = floor(helper, 152)
		val level = helper.level
		val fixed = FixedRandom(1)
		level.setBlock(c, grass.defaultBlockState(), 3)
		weather(helper, true, true) {
			level.setBlock(c.above(), Blocks.SNOW.defaultBlockState().setValue(BlockStateProperties.LAYERS, 3), 3)
			grass.updateTick(level, c.x, c.y, c.z, fixed)
			helper.assertTrue(level.getBlockState(c.above()) == layers(3), "vanilla snow becomes the mod's: ${level.getBlockState(c.above())}")

			level.setBlock(c.above(), Blocks.AIR.defaultBlockState(), 3)
			grass.updateTick(level, c.x, c.y, c.z, fixed)
			helper.assertTrue(level.getBlockState(c.above()) == layers(1), "snow falls on the grass")
			val upMeta = (WE_PerlinNoise.PerlinNoise2D((level.seed), c.x.toDouble(), c.z.toDouble(), 1.0, 1) * 15).toInt() and 7 shr 1
			repeat(6) { grass.updateTick(level, c.x, c.y, c.z, fixed) }
			helper.assertTrue(level.getBlockState(c.above()).getValue(BlockSnowLayer.LAYERS) == upMeta + 1, "snow grows to ${upMeta + 1} layers: ${level.getBlockState(c.above())}")

			level.setBlock(c.above(), Blocks.AIR.defaultBlockState(), 3)
			level.setBlock(c.above(3), Blocks.STONE.defaultBlockState(), 3)
			grass.updateTick(level, c.x, c.y, c.z, fixed)
			helper.assertTrue(level.getBlockState(c.above()) == layers(1), "snow falls under a roof too")
			level.setBlock(c.above(3), Blocks.AIR.defaultBlockState(), 3)

			// FixedRandom(1): точка распространения — (x, y − 2, z)
			val target = c.below(2)
			for (block in listOf(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.COARSE_DIRT)) {
				level.setBlock(target, block.defaultBlockState(), 3)
				val expected = block !== Blocks.COARSE_DIRT && level.getHeight(Heightmap.Types.MOTION_BLOCKING, target.x, target.z) <= target.z
				grass.updateTick(level, c.x, c.y, c.z, fixed)
				helper.assertTrue((level.getBlockState(target).block === grass) == expected, "spread to $block: expected $expected")
			}
			level.setBlock(target, Blocks.AIR.defaultBlockState(), 3)
		}
		weather(helper, true, false) {
			level.setBlock(c.above(), Blocks.AIR.defaultBlockState(), 3)
			grass.updateTick(level, c.x, c.y, c.z, fixed)
			helper.assertTrue(level.getBlockState(c.above()).isAir, "no snow without precipitation")
		}

		// вечная: случайные тики, пока свет обновляется, её не растопят (тьма от вечности не зависит)
		level.setBlock(c, grass.defaultBlockState().setValue(BlockSnowGrass.PERMANENT, true), 3)
		level.setBlock(c.above(), Blocks.STONE.defaultBlockState(), 3)
		helper.runAfterDelay(40) {
			helper.assertTrue(level.getMaxLocalRawBrightness(c.above()) < 4, "dark under stone")
			weather(helper, true, false) { grass.updateTick(level, c.x, c.y, c.z, fixed) }
			helper.assertTrue(level.getBlockState(c).`is`(Blocks.DIRT), "snow grass in the dark becomes dirt")
			clear(helper, c)
			helper.succeed()
		}
	}

	/**
	 * Почва снежной травы (`canSustainPlant`): куст, который растёт на траве (`mayPlaceOn` — обёртка автора S-03),
	 * растения равнин; пляжные (тростник) — только у воды; прочие (кактус, адский нарост) — нет
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun snowGrassSoil(helper: GameTestHelper) {
		val c = floor(helper, 158)
		val level = helper.level
		level.setBlock(c, grass.defaultBlockState(), 3)
		fun sustains(plant: Block) = level.getBlockState(c).canSustainPlant(level, c, Direction.UP, plant as IPlantable)
		helper.assertTrue(sustains(Blocks.POPPY) && sustains(Blocks.DEAD_BUSH) && sustains(Blocks.OAK_SAPLING), "bushes")
		helper.assertTrue(!sustains(Blocks.CACTUS) && !sustains(Blocks.NETHER_WART), "cactus and nether wart")
		helper.assertTrue(!sustains(Blocks.SUGAR_CANE), "sugar cane without water")
		level.setBlock(c.east(), Blocks.WATER.defaultBlockState(), 3)
		helper.assertTrue(sustains(Blocks.SUGAR_CANE), "sugar cane near water")
		clear(helper, c)
		helper.succeed()
	}
}
