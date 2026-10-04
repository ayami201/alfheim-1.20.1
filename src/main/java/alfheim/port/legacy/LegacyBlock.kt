package alfheim.port.legacy

import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.*
import kotlin.math.min

/*
 * Блок с сеттерами 1.7.10 (SPEC, Р-4; MAPPING.md, «Блоки и предметы»).
 *
 * В 1.7.10 блок создавался с материалом, а потом настраивался: `setHardness`, `setLightLevel`, `setStepSound`… —
 * в `init` блока или цепочкой после создания. В 1.20.1 свойства передаются в конструктор. Чтобы код автора остался
 * как был, базовые классы порта [Block1710] и [BlockFalling1710] создают блок по материалу, а сеттеры 1.7.10 меняют
 * его потом — до конца регистрации, как в 1.7.10:
 * - твёрдость и свечение записываются в состояния блока (поля открыты `META-INF/accesstransformer.cfg`);
 * - взрывоустойчивость, скользкость, звук, случайные тики, непрозрачность для света 1.20.1 читает у блока методами,
 *   их отвечают базовые классы порта.
 *
 * Правила сеттеров — как в 1.7.10: `setHardness(h)` поднимает внутреннюю взрывоустойчивость до `h * 5`,
 * `setResistance(r)` задаёт её как `r * 3`, игра делила её на 5 — это и есть взрывоустойчивость 1.20.1; поэтому важен
 * порядок вызовов, как у автора. `setLightLevel(f)` — свечение `(int) (15 * f)`. Звук по умолчанию — камень.
 *
 * Инструмент и уровень добычи, имя текстуры 1.20.1 не хранит в блоке: их читает генерация данных (теги, модели).
 */

/** Значения сеттеров 1.7.10 */
class BlockProps(val material: Material) {

	/** `getUnlocalizedName` 1.7.10: `tile.` + имя из `setBlockName` */
	var unlocalizedName = "tile.null"
	/** `setBlockTextureName` 1.7.10, например `alfheim:ElvenSand`; читает генерация моделей */
	var textureName: String? = null
	var blockHardness = 0f
	/** Внутренняя взрывоустойчивость 1.7.10: в 5 раз больше той, что видит игрок */
	var blockResistance = 0f
	var lightValue = 0
	/** `setLightOpacity`, 0..255; `null` — как в 1.7.10 по умолчанию: 255 у непрозрачного куба, 0 у прочих */
	var lightOpacity: Int? = null
	var stepSound: SoundType? = SoundType.STONE
	var slipperiness = 0.6f
	var needsRandomTick = false
	/** `"pickaxe"`, `"axe"`, `"shovel"`; уровень 0 — дерево, 1 — камень, 2 — железо, 3 — алмаз */
	var harvestTool: String? = null
	var harvestLevel = -1
}

/**
 * Блок порта с сеттерами и свойствами 1.7.10; реализуют [Block1710], [BlockFalling1710] и базовые классы блоков особой
 * формы и растений. Методы 1.7.10, которые переопределяет автор, — [LegacyBlockMethods]
 */
interface LegacyBlock: LegacyBlockMethods {

	val legacy: BlockProps

	/** Номер варианта, если в 1.7.10 блок был одной из metadata (SPEC, Р-5); иначе `null` */
	val variant: Int? get() = null

	val blockMaterial get() = legacy.material

	var slipperiness: Float
		get() = legacy.slipperiness
		set(value) {
			legacy.slipperiness = value
		}

	private val self get() = this as Block

	fun setBlockName(name: String): Block {
		legacy.unlocalizedName = "tile.$name"
		return self
	}

	fun setBlockTextureName(name: String): Block {
		legacy.textureName = name
		return self
	}

	/** Вкладка 1.20.1 сама перечисляет свои вещи (`AlfheimTab`) */
	fun setCreativeTab(@Suppress("UNUSED_PARAMETER") tab: Any?) = self

	fun setHardness(hardness: Float): Block {
		legacy.blockHardness = hardness
		if (legacy.blockResistance < hardness * 5f) legacy.blockResistance = hardness * 5f
		for (state in self.stateDefinition.possibleStates) state.destroySpeed = hardness
		return self
	}

	fun setResistance(resistance: Float): Block {
		legacy.blockResistance = resistance * 3f
		return self
	}

	fun setBlockUnbreakable() = setHardness(-1f)

	fun setLightLevel(level: Float): Block {
		legacy.lightValue = (15f * level).toInt()
		for (state in self.stateDefinition.possibleStates) state.lightEmission = legacy.lightValue
		return self
	}

	fun setLightOpacity(opacity: Int): Block {
		legacy.lightOpacity = opacity
		return self
	}

	/** `null` в 1.7.10 ронял игру на первом же шаге по блоку; здесь вместо него звук камня */
	fun setStepSound(sound: SoundType?): Block {
		legacy.stepSound = sound
		return self
	}

	fun setTickRandomly(tick: Boolean): Block {
		legacy.needsRandomTick = tick
		return self
	}

	fun setHarvestLevel(toolClass: String?, level: Int) {
		legacy.harvestTool = toolClass
		legacy.harvestLevel = level
	}

	/**
	 * Для одной metadata. Вариант с metadata в порту — отдельный блок, поэтому уровень ставится, если [metadata] — его
	 * номер; у блока без вариантов metadata — состояние, и уровень ставится для всех состояний
	 */
	fun setHarvestLevel(toolClass: String?, level: Int, metadata: Int) {
		if (variant == null || variant == metadata) setHarvestLevel(toolClass, level)
	}

	fun isOpaqueCube() = true

	/** 1 — полупрозрачный проход рендера 1.7.10; генерация моделей ставит такому блоку `render_type` translucent */
	fun getRenderBlockPass() = 0

	/** Основание маяка; генерация данных кладёт блок в тег `minecraft:beacon_base_blocks` */
	fun isBeaconBase(world: BlockGetter?, x: Int, y: Int, z: Int, beaconX: Int, beaconY: Int, beaconZ: Int) = false

	/** Сколько света задерживает блок, 0..255, как в 1.7.10 */
	fun lightOpacity() = legacy.lightOpacity ?: if (isOpaqueCube()) 255 else 0
}

/*
 * Поля 1.7.10, которые автор пишет напрямую (`blockHardness = 2F`, `tickRandomly = true`; `stepSound` —
 * LegacyBlockTypes.kt). Запись поля — не сеттер: `blockHardness` меняет только твёрдость, взрывоустойчивость остаётся
 * прежней, как в 1.7.10. Расширения, а не свойства интерфейса: у Java-классов порта свойство и сеттер 1.7.10
 * (`setTickRandomly`) с одним именем не уживаются
 */

var LegacyBlock.blockHardness: Float
	get() = legacy.blockHardness
	set(value) {
		legacy.blockHardness = value
		for (state in (this as Block).stateDefinition.possibleStates) state.destroySpeed = value
	}

var LegacyBlock.tickRandomly: Boolean
	get() = legacy.needsRandomTick
	set(value) {
		legacy.needsRandomTick = value
	}

/**
 * Блок красит класс автора: переопределяет `colorMultiplier` или `getRenderColor` 1.7.10 ([LegacyBlockMethods]). Его
 * моделям генерация ставит tintindex, клиент красит их (alfheim.port.client.AlfheimBlockColors)
 */
val LegacyBlock.isTinted: Boolean
	get() {
		val methods = LegacyBlockMethods::class.java
		val world = javaClass.getMethod("colorMultiplier", BlockGetter::class.java, Int::class.java, Int::class.java, Int::class.java)
		val item = javaClass.getMethod("getRenderColor", Int::class.java)
		return world.declaringClass != methods || item.declaringClass != methods
	}

/*
 * Сеттер 1.7.10 в цепочке после другого сеттера (`BlockX().setCreativeTab(tab).setHardness(1.5f)`): предыдущий вернул
 * Block, как в 1.7.10, — у блока порта сеттеры те же
 */
fun Block.setBlockName(name: String) = (this as LegacyBlock).setBlockName(name)
fun Block.setBlockTextureName(name: String) = (this as LegacyBlock).setBlockTextureName(name)
fun Block.setCreativeTab(tab: Any?) = (this as LegacyBlock).setCreativeTab(tab)
fun Block.setHardness(hardness: Float) = (this as LegacyBlock).setHardness(hardness)
fun Block.setResistance(resistance: Float) = (this as LegacyBlock).setResistance(resistance)
fun Block.setBlockUnbreakable() = (this as LegacyBlock).setBlockUnbreakable()
fun Block.setLightLevel(level: Float) = (this as LegacyBlock).setLightLevel(level)
fun Block.setLightOpacity(opacity: Int) = (this as LegacyBlock).setLightOpacity(opacity)
fun Block.setStepSound(sound: SoundType?) = (this as LegacyBlock).setStepSound(sound)
fun Block.setTickRandomly(tick: Boolean) = (this as LegacyBlock).setTickRandomly(tick)
fun Block.setHarvestLevel(toolClass: String?, level: Int) = (this as LegacyBlock).setHarvestLevel(toolClass, level)
fun Block.setHarvestLevel(toolClass: String?, level: Int, metadata: Int) = (this as LegacyBlock).setHarvestLevel(toolClass, level, metadata)

/** Имена звуков блока 1.7.10 (`soundTypeStone` и др.) внутри классов блоков, как в 1.7.10 */
interface SoundTypes1710 {

	val soundTypeStone: SoundType get() = SoundType.STONE
	val soundTypeWood: SoundType get() = SoundType.WOOD
	val soundTypeGravel: SoundType get() = SoundType.GRAVEL
	val soundTypeGrass: SoundType get() = SoundType.GRASS
	val soundTypePiston: SoundType get() = SoundType.STONE
	val soundTypeMetal: SoundType get() = SoundType.METAL
	val soundTypeGlass: SoundType get() = SoundType.GLASS
	val soundTypeCloth: SoundType get() = SoundType.WOOL
	val soundTypeSand: SoundType get() = SoundType.SAND
	val soundTypeSnow: SoundType get() = SoundType.SNOW
	val soundTypeLadder: SoundType get() = SoundType.LADDER
	val soundTypeAnvil: SoundType get() = SoundType.ANVIL
}

/** `net.minecraft.block.Block` 1.7.10 */
open class Block1710(material: Material): Block(material.properties()), LegacyBlock {

	final override val legacy = BlockProps(material)

	override fun getExplosionResistance() = legacy.blockResistance / 5f

	override fun getFriction() = legacy.slipperiness

	@Deprecated("Deprecated in Java")
	override fun getSoundType(state: BlockState) = legacy.stepSound ?: SoundType.STONE

	override fun isRandomlyTicking(state: BlockState) = legacy.needsRandomTick

	/** Не непрозрачный куб 1.7.10 не скрывает грани соседей и не считается сплошным для света */
	@Deprecated("Deprecated in Java")
	override fun getOcclusionShape(state: BlockState, level: BlockGetter, pos: BlockPos): VoxelShape = if (isOpaqueCube()) super.getOcclusionShape(state, level, pos) else Shapes.empty()

	@Deprecated("Deprecated in Java")
	override fun getLightBlock(state: BlockState, level: BlockGetter, pos: BlockPos) = min(lightOpacity(), 15)

	/** Свет неба 1.7.10 проходил блок с непрозрачностью 0 не ослабевая */
	override fun propagatesSkylightDown(state: BlockState, level: BlockGetter, pos: BlockPos) = lightOpacity() == 0

	companion object: SoundTypes1710
}

/** `net.minecraft.block.BlockFalling` 1.7.10: падает так же — тиком после установки и после изменения соседа */
open class BlockFalling1710(material: Material): FallingBlock(material.properties()), LegacyBlock {

	final override val legacy = BlockProps(material)

	override fun getExplosionResistance() = legacy.blockResistance / 5f

	override fun getFriction() = legacy.slipperiness

	@Deprecated("Deprecated in Java")
	override fun getSoundType(state: BlockState) = legacy.stepSound ?: SoundType.STONE

	override fun isRandomlyTicking(state: BlockState) = legacy.needsRandomTick

	@Deprecated("Deprecated in Java")
	override fun getOcclusionShape(state: BlockState, level: BlockGetter, pos: BlockPos): VoxelShape = if (isOpaqueCube()) super.getOcclusionShape(state, level, pos) else Shapes.empty()

	@Deprecated("Deprecated in Java")
	override fun getLightBlock(state: BlockState, level: BlockGetter, pos: BlockPos) = min(lightOpacity(), 15)

	override fun propagatesSkylightDown(state: BlockState, level: BlockGetter, pos: BlockPos) = lightOpacity() == 0

	/** Пыль под висящим блоком появилась в 1.12; блок 1.7.10 не пылил */
	override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: RandomSource) = Unit

	companion object: SoundTypes1710
}
