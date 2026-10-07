package alfheim.port.test

import alfheim.api.ModInfo.MODID
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.BlockHang
import alfheim.common.core.util.AlfheimTab
import alfheim.common.entity.EntityFallingHang
import alfheim.port.legacy.*
import alfheim.port.registry.LegacyIds
import alfheim.port.registry.LegacyRegistration
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.phys.AABB
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * КТ-2, партия 9а-1: висячие блоки — сосульки (4 стадии), сталактиты и сталагмиты (по 8 стадий) — и падающая сосулька
 * (`EntityFallingHang`). Числа и правила — из классов автора (`BlockHang`, `BlockIcicle`, `BlockStalactite`,
 * `BlockStalagmite`, `EntityFallingHang`)
 */
@GameTestHolder(MODID)
@PrefixGameTestTemplate(false)
object PortHangTest {

	private val groups get() = mapOf("icicle" to AlfheimBlocks.icicle, "stalactite" to AlfheimBlocks.stalactite, "stalagmite" to AlfheimBlocks.stalagmite)

	/** Случайные числа тика: `nextInt(n)` всегда выпадает на [value], границы `n` записываются в [bounds] */
	private class FixedRandom(private val value: Int): RandomSource by RandomSource.create(0) {
		val bounds = ArrayList<Int>()
		override fun nextInt(bound: Int): Int {
			bounds += bound
			return value
		}
	}

	/**
	 * Каменный пол 5 × 5 на высоте [dy] над точкой теста, над ним — воздух на 5 блоков (мир тестов — обычный мир);
	 * возвращает клетку над серединой пола. Высота у каждого теста своя: тесты идут рядом и одновременно
	 */
	private fun floor(helper: GameTestHelper, dy: Int): BlockPos {
		val center = helper.absolutePos(BlockPos(0, dy, 0))
		for (i in -2..2) for (k in -2..2) {
			helper.level.setBlock(center.offset(i, -1, k), Blocks.STONE.defaultBlockState(), 3)
			for (j in 0..4) helper.level.setBlock(center.offset(i, j, k), Blocks.AIR.defaultBlockState(), 3)
		}
		return center
	}

	private fun clear(helper: GameTestHelper, center: BlockPos) {
		for (i in -2..2) for (k in -2..2) for (j in -1..4) helper.level.setBlock(center.offset(i, j, k), Blocks.AIR.defaultBlockState(), 3)
	}

	private fun fallingHangs(helper: GameTestHelper, at: BlockPos) = helper.level.getEntitiesOfClass(EntityFallingHang::class.java, AABB(at).inflate(2.0))

	/**
	 * Стадии — блоки под именами `<имя автора><стадия>` (SPEC, Р-5): сосульки 0–3, сталактиты и сталагмиты 0–7, имена —
	 * из переводов автора; во вкладке — все стадии: сталактиты, сталагмиты, сосульки. Падающая сосулька — существо
	 * `alfheim:falling_hang` (`FallingHang` автора), слежение — как `registerModEntity(…, 128, 1, true)`
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun hangRegistry(helper: GameTestHelper) {
		val legacy = mapOf("icicle" to "Icicle", "stalactite" to "Stalactite", "stalagmite" to "Stalagmite")
		val names = mapOf("icicle" to "Icicles", "stalactite" to "Stalactite", "stalagmite" to "Stalagmite")
		helper.assertTrue(AlfheimBlocks.icicle.size == 4 && AlfheimBlocks.stalactite.size == 8 && AlfheimBlocks.stalagmite.size == 8, "stages")
		for ((id, blocks) in groups) blocks.forEachIndexed { stage, block ->
			helper.assertTrue(BuiltInRegistries.BLOCK.getKey(block) == ResourceLocation(MODID, "$id$stage"), "$id$stage is ${BuiltInRegistries.BLOCK.getKey(block)}")
			helper.assertTrue(LegacyIds.block("$MODID:${legacy[id]}", stage)?.id == ResourceLocation(MODID, "$id$stage"), "legacy id ${legacy[id]}:$stage")
			helper.assertTrue(ItemStack(block).hoverName.string == names[id], "name of $id$stage: ${ItemStack(block).hoverName.string}")
			helper.assertTrue((block as BlockHang).meta == stage && block.variant == stage, "$id$stage: stage ${block.meta}")
		}

		CreativeModeTabs.tryRebuildTabContents(helper.level.enabledFeatures(), true, helper.level.registryAccess())
		val tab = AlfheimTab.tab.get().displayItems.map { it.item }
		val hangs = (AlfheimBlocks.stalactite + AlfheimBlocks.stalagmite + AlfheimBlocks.icicle).map { it.asItem() }
		val first = tab.indexOf(hangs[0])
		helper.assertTrue(first >= 0 && tab.subList(first, first + hangs.size) == hangs, "stalactites, stalagmites and icicles in the tab")

		val type = LegacyRegistration.entityType(EntityFallingHang::class.java)
		helper.assertTrue(BuiltInRegistries.ENTITY_TYPE.getKey(type) == ResourceLocation(MODID, "falling_hang"), "falling hang id")
		helper.assertTrue(LegacyIds.entities["$MODID:FallingHang"]?.get("*")?.id == ResourceLocation(MODID, "falling_hang"), "falling hang legacy id")
		helper.assertTrue(type.clientTrackingRange() * 16 == 128 && type.updateInterval() == 1 && type.trackDeltas(), "falling hang tracking")
		helper.succeed()
	}

	/**
	 * Свойства `BlockModMeta` автора: твёрдость 0,3, взрывоустойчивость 5 (3 в 1.20.1: 5 × 3 / 5), звук материала — у
	 * сосулек стекло, у сталактитов и сталагмитов камень; кирка, уровень 1. Свет проходит, столкновений нет; рамка — 0,25–0,75
	 * по сторонам, у сосулек и сталактитов 0,1–1 в высоту, у сталагмитов 0–0,9. Случайные тики — только у тех, что падают
	 * (сосульки, сталактиты). Лута нет (`getItemDropped` — null), и с шёлковым касанием тоже; колёсиком — своя стадия
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun hangProperties(helper: GameTestHelper) {
		val pos = BlockPos(1, 1, 1)
		val abs = helper.absolutePos(pos)
		for ((id, blocks) in groups) for (block in blocks) {
			val state = block.defaultBlockState()
			helper.assertTrue(state.getDestroySpeed(helper.level, abs) == 0.3f && block.explosionResistance == 3f, "$block: hardness ${state.getDestroySpeed(helper.level, abs)}, resistance ${block.explosionResistance}")
			helper.assertTrue(state.soundType == if (id == "icicle") SoundType.GLASS else SoundType.STONE, "$block: sound")
			helper.assertTrue(state.`is`(BlockTags.MINEABLE_WITH_PICKAXE) && state.`is`(BlockTags.NEEDS_STONE_TOOL), "$block: pickaxe, level 1")
			helper.assertTrue(state.requiresCorrectToolForDrops() == (id != "icicle"), "$block: a tool for drops")
			helper.assertTrue(state.getLightBlock(helper.level, abs) == 0 && state.propagatesSkylightDown(helper.level, abs), "$block: light passes")
			helper.assertTrue(state.getCollisionShape(helper.level, abs).isEmpty, "$block: no collision")
			val bounds = state.getShape(helper.level, abs).bounds()
			val expected = if (id == "stalagmite") AABB(0.25, 0.0, 0.25, 0.75, 0.9, 0.75) else AABB(0.25, 0.1, 0.25, 0.75, 1.0, 0.75)
			helper.assertTrue(abs(bounds.minX - expected.minX) < 1e-6 && abs(bounds.minY - expected.minY) < 1e-6 && abs(bounds.maxY - expected.maxY) < 1e-6 && abs(bounds.maxZ - expected.maxZ) < 1e-6, "$block: bounds $bounds")
			helper.assertTrue(state.isRandomlyTicking == (id != "stalagmite"), "$block: random ticks")
			helper.assertTrue(Block.getDrops(state, helper.level, abs, null).isEmpty(), "$block: no loot")
			helper.assertTrue(block.getCloneItemStack(helper.level, abs, state).item == block.asItem(), "$block: pick block")
		}
		helper.succeed()
	}

	/**
	 * Где висячий блок держится (`canBlockStay`) — и установка, и удержание (`canSurvive`): сосулька — подо льдом или
	 * плотным льдом (материал 1.7.10; синий лёд 1.20.1 — как плотный), сталактит — под камнем, сталагмит — на камне.
	 * Без опоры (сосед сменился — `onNeighborBlockChange`) сосулька и сталактит падают: на их месте — воздух и падающая
	 * сосулька со своим блоком и стадией; сталагмит просто пропадает — без существа и без лута
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun hangSupport(helper: GameTestHelper) {
		val pos = BlockPos(1, 3, 1)
		val abs = helper.absolutePos(pos)
		fun survives(block: Block, above: Block, below: Block = Blocks.AIR): Boolean {
			helper.setBlock(pos.above(), above)
			helper.setBlock(pos.below(), below)
			return block.defaultBlockState().canSurvive(helper.level, abs)
		}
		val icicle = AlfheimBlocks.icicle[2]
		val stalactite = AlfheimBlocks.stalactite[5]
		val stalagmite = AlfheimBlocks.stalagmite[4]
		for (ice in listOf(Blocks.ICE, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.FROSTED_ICE)) helper.assertTrue(survives(icicle, ice), "an icicle under $ice")
		for (other in listOf(Blocks.STONE, Blocks.SNOW_BLOCK, Blocks.GLASS, Blocks.AIR)) helper.assertTrue(!survives(icicle, other), "no icicle under $other")
		for (rock in listOf(Blocks.STONE, Blocks.COBBLESTONE, Blocks.DEEPSLATE, Blocks.NETHERRACK, Blocks.OBSIDIAN)) helper.assertTrue(survives(stalactite, rock), "a stalactite under $rock")
		for (other in listOf(Blocks.ICE, Blocks.PACKED_ICE, Blocks.GLASS, Blocks.DIRT, Blocks.GRAVEL, Blocks.WHITE_WOOL, Blocks.AIR)) helper.assertTrue(!survives(stalactite, other), "no stalactite under $other")
		helper.assertTrue(survives(stalagmite, Blocks.AIR, Blocks.STONE) && !survives(stalagmite, Blocks.STONE, Blocks.AIR) && !survives(stalagmite, Blocks.AIR, Blocks.SAND), "a stalagmite stands on rock")

		// сосулька без льда над собой падает
		helper.setBlock(pos.below(), Blocks.AIR)
		helper.setBlock(pos.above(), Blocks.ICE)
		helper.setBlock(pos, icicle)
		helper.assertTrue(helper.getBlockState(pos).`is`(icicle), "the icicle hangs")
		helper.setBlock(pos.above(), Blocks.AIR)
		helper.assertTrue(helper.getBlockState(pos).isAir, "the icicle falls")
		var hangs = fallingHangs(helper, abs)
		helper.assertTrue(hangs.size == 1, "falling icicles: ${hangs.size}")
		helper.assertTrue(hangs[0].block === icicle && hangs[0].meta == 2 && hangs[0].x == abs.x + 0.5 && hangs[0].y == abs.y.toDouble() && hangs[0].z == abs.z + 0.5, "the falling icicle: ${hangs[0].block}, stage ${hangs[0].meta}, at ${hangs[0].position()}")
		hangs.forEach { it.discard() }

		// сталактит без камня над собой — тоже
		helper.setBlock(pos.above(), Blocks.STONE)
		helper.setBlock(pos, stalactite)
		helper.setBlock(pos.above(), Blocks.DIRT)
		hangs = fallingHangs(helper, abs)
		helper.assertTrue(helper.getBlockState(pos).isAir && hangs.size == 1 && hangs[0].block === stalactite && hangs[0].meta == 5, "the stalactite falls")
		hangs.forEach { it.discard() }

		// сталагмит без камня под собой пропадает
		helper.setBlock(pos.above(), Blocks.AIR)
		helper.setBlock(pos.below(), Blocks.STONE)
		helper.setBlock(pos, stalagmite)
		helper.setBlock(pos.below(), Blocks.AIR)
		helper.assertTrue(helper.getBlockState(pos).isAir && fallingHangs(helper, abs).isEmpty(), "the stalagmite disappears")
		helper.assertTrue(helper.level.getEntitiesOfClass(ItemEntity::class.java, AABB(abs).inflate(2.0)).isEmpty(), "no loot")
		fallingHangs(helper, abs).forEach { it.discard() }
		helper.succeed()
	}

	/**
	 * Рост (`updateTick`): стадия `n` растёт с шансом 1 / (4 × (n + 1)) — сосулька 1/4, 1/8, 1/12, сталактит до 1/28.
	 * Последняя стадия с шансом 1/16 (у сталактита 1/32) обламывается: падающая сосулька появляется блоком ниже, со
	 * стадией того, что было ниже (воздух — 0), а сам блок снова растёт с нулевой стадии. Блок ниже при этом пропадает,
	 * даже камень, — так у автора (BUGS.md). Сталагмит не растёт
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun hangGrowth(helper: GameTestHelper) {
		val pos = BlockPos(1, 3, 1)
		val abs = helper.absolutePos(pos)
		fun tick(value: Int): List<Int> {
			val random = FixedRandom(value)
			(helper.getBlockState(pos).block as BlockHang).updateTick(helper.level, abs.x, abs.y, abs.z, random)
			return random.bounds
		}
		helper.setBlock(pos.below(), Blocks.AIR)
		helper.setBlock(pos.above(), Blocks.ICE)
		helper.setBlock(pos, AlfheimBlocks.icicle[0])
		helper.assertTrue(tick(1) == listOf(4) && helper.getBlockState(pos).`is`(AlfheimBlocks.icicle[0]), "the icicle does not grow")
		for (stage in 1..3) {
			helper.assertTrue(tick(0) == listOf(4 * stage), "growth chance of stage ${stage - 1}")
			helper.assertTrue(helper.getBlockState(pos).`is`(AlfheimBlocks.icicle[stage]), "the icicle grows to stage $stage: ${helper.getBlockState(pos)}")
		}
		helper.assertTrue(tick(0) == listOf(16), "break chance of the last stage")
		helper.assertTrue(helper.getBlockState(pos).`is`(AlfheimBlocks.icicle[0]), "the icicle starts again: ${helper.getBlockState(pos)}")
		var hangs = fallingHangs(helper, abs)
		helper.assertTrue(hangs.size == 1 && hangs[0].block === AlfheimBlocks.icicle[3] && hangs[0].meta == 0 && hangs[0].y == abs.y - 1.0, "the broken icicle falls from below: ${hangs.map { "${it.block} ${it.meta} ${it.position()}" }}")
		hangs.forEach { it.discard() }

		// под обломившейся сосулькой был камень — он пропадает
		helper.setBlock(pos.below(), Blocks.STONE)
		helper.setBlock(pos, AlfheimBlocks.icicle[3])
		tick(0)
		hangs = fallingHangs(helper, abs)
		helper.assertTrue(helper.getBlockState(pos.below()).isAir && hangs.size == 1 && hangs[0].meta == 0, "the stone under the icicle is removed")
		hangs.forEach { it.discard() }

		helper.setBlock(pos.above(), Blocks.STONE)
		helper.setBlock(pos, AlfheimBlocks.stalactite[0])
		for (stage in 1..7) tick(0)
		helper.assertTrue(helper.getBlockState(pos).`is`(AlfheimBlocks.stalactite[7]) && tick(0) == listOf(32), "the stalactite grows to stage 7 and breaks with chance 1/32")
		helper.assertTrue(helper.getBlockState(pos).`is`(AlfheimBlocks.stalactite[0]), "the stalactite starts again")
		fallingHangs(helper, abs).forEach { it.discard() }

		helper.setBlock(pos.above(), Blocks.AIR)
		helper.setBlock(pos.below(), Blocks.STONE)
		helper.setBlock(pos, AlfheimBlocks.stalagmite[3])
		helper.assertTrue(tick(0).isEmpty() && helper.getBlockState(pos).`is`(AlfheimBlocks.stalagmite[3]), "the stalagmite does not grow")
		helper.setBlock(pos, Blocks.AIR)
		helper.setBlock(pos.below(), Blocks.AIR)
		fallingHangs(helper, abs).forEach { it.discard() }
		helper.succeed()
	}

	/**
	 * Падающая сосулька в полёте: каждый тик скорость вниз растёт на 0,04 (без сопротивления воздуха), сосулька — куб 1 × 1;
	 * коснувшись блока, она исчезает и ничего не ставит. Блок и стадия сохраняются в NBT («block», запас воздуха — стадия)
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun fallingHangFlight(helper: GameTestHelper) {
		val center = floor(helper, 120)
		val hang = EntityFallingHang(helper.level).apply {
			setPosition(center.x + 0.5, center.y + 4.0, center.z + 0.5)
			block = AlfheimBlocks.stalactite[6]
			meta = 6
		}
		helper.level.addFreshEntity(hang)
		helper.assertTrue(hang.bbWidth == 1f && hang.bbHeight == 1f, "size ${hang.bbWidth} × ${hang.bbHeight}")

		val tag = hang.saveWithoutId(CompoundTag())
		helper.assertTrue(tag.getString(EntityFallingHang.TAG_BLOCK) == "$MODID:stalactite6" && tag.getShort("Air").toInt() == 6, "saved: $tag")
		val copy = EntityFallingHang(helper.level).apply { load(tag) }
		helper.assertTrue(copy.block === AlfheimBlocks.stalactite[6] && copy.meta == 6, "loaded: ${copy.block} ${copy.meta}")
		copy.blockName = "$MODID:no_such_block"
		helper.assertTrue(copy.block === Blocks.STONE, "an unknown block is stone")

		var checked = false
		helper.onEachTick {
			if (checked || hang.isRemoved || hang.deltaMovement.y >= 0) return@onEachTick
			val ticks = (-hang.deltaMovement.y / 0.04).roundToInt()
			val drop = center.y + 4.0 - hang.y
			helper.assertTrue(abs(hang.deltaMovement.y + 0.04 * ticks) < 1e-9 && abs(drop - 0.04 * ticks * (ticks + 1) / 2) < 1e-9, "after $ticks ticks: speed ${hang.deltaMovement.y}, drop $drop")
			checked = true
		}
		helper.succeedWhen {
			helper.assertTrue(checked && hang.isRemoved, "the falling icicle lands")
			helper.assertTrue(helper.level.getBlockState(center).isAir, "nothing is placed")
			clear(helper, center)
		}
	}

	/**
	 * Удар падающей сосульки: живые существа на её пути (от прошлой точки до новой, с запасом 0,5) получают урон «падающий
	 * блок» — вдвое больше пролёта, но не меньше 40, без отбрасывания; сосулька при этом исчезает
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun fallingHangHits(helper: GameTestHelper) {
		val center = floor(helper, 126)
		// свинья без брони и без стойкости к отбрасыванию, с запасом здоровья 100
		val pig = EntityType.PIG.create(helper.level)!!.apply {
			moveTo(center.x + 0.5, center.y.toDouble(), center.z + 0.5, 0f, 0f)
			setNoAi(true)
			getAttribute(Attributes.MAX_HEALTH)!!.baseValue = 100.0
			health = 100f
			helper.level.addFreshEntity(this)
		}
		val hang = EntityFallingHang(helper.level).apply {
			setPosition(center.x + 0.5, center.y + 1.0, center.z + 0.5)
			block = AlfheimBlocks.icicle[3]
			meta = 3
		}
		helper.level.addFreshEntity(hang)
		helper.succeedWhen {
			helper.assertTrue(hang.isRemoved, "the falling icicle hits")
			helper.assertTrue(pig.health == 60f, "the pig takes 40: health ${pig.health}")
			helper.assertTrue(abs(pig.deltaMovement.x) < 1e-6 && abs(pig.deltaMovement.z) < 1e-6, "no knockback: ${pig.deltaMovement}")
			pig.discard()
			clear(helper, center)
		}
	}
}
