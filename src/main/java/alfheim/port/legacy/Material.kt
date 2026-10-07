package alfheim.port.legacy

import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction

/**
 * Материал блока 1.7.10 (SPEC, Р-4). В 1.20.1 материалов нет: то, что они значили, — свойства блока. Значения взяты
 * из `net.minecraft.block.material.Material` 1.7.10 и сверены с кодом игры 1.7.10 (MAPPING.md, «Блоки и предметы»):
 * цвет на карте, нужен ли инструмент, поджигает ли лава, заменяем ли блок, что делает поршень, жидкость ли.
 *
 * «Твёрдый» материал 1.7.10 — `forceSolidOn` 1.20.1 (`BlockState.isSolid` и есть «твёрдость материала» 1.7.10).
 * Блок из непрозрачного материала (не полупрозрачный и мешает движению) в 1.7.10 мог быть «нормальным кубом»: на нём
 * появлялись мобы, он проводил сигнал красного камня, в нём задыхались. Блок из прочих материалов не мог — для него
 * эти свойства 1.20.1 выключены. Не был нормальным кубом и источник сигнала (`canProvidePower` 1.7.10, `isSignalSource`
 * 1.20.1): сигнал через себя он не проводил, в нём не задыхались. Нормальным кубом 1.7.10 считал блок, который рисуется
 * кубом (`renderAsNormalBlock`); в 1.20.1 это блок, форма опоры которого (`getBlockSupportShape`) — полный куб. По
 * умолчанию форма опоры — форма столкновений; блок, у которого столкновения меньше куба, а в 1.7.10 он был нормальным
 * кубом, отвечает полной формой опоры сам, как песок душ 1.20.1.
 *
 * Материал со своими значениями свойств (`MaterialPublic` ASJCore) — наследник, он переопределяет свойства ниже.
 */
open class Material private constructor(val mapColor: MapColor, kind: Kind) {

	/** Обычный материал (`Material(color)` 1.7.10) — для наследника со своими значениями */
	protected constructor(mapColor: MapColor): this(mapColor, Kind.NORMAL)

	private enum class Kind { NORMAL, TRANSPARENT, LOGIC, LIQUID, PORTAL, WEB }

	private var tool = false
	private var burning = false
	private var replaceable = false
	private var translucent = false
	private var mobility = PushReaction.NORMAL

	open val requiresTool get() = tool
	open val canBurn get() = burning
	open val isReplaceable get() = replaceable
	val isTranslucent get() = translucent
	val pushReaction get() = mobility

	open val isLiquid = kind == Kind.LIQUID
	open val isSolid = kind == Kind.NORMAL || kind == Kind.WEB
	private val blocksMovement = kind == Kind.NORMAL
	
	/** `Material.isOpaque` 1.7.10 */
	open val isOpaque get() = !isTranslucent && blocksMovement()

	/** `blocksMovement()` 1.7.10 */
	open fun blocksMovement() = blocksMovement

	init {
		// конструкторы MaterialTransparent и MaterialLiquid 1.7.10
		if (kind == Kind.TRANSPARENT || kind == Kind.LIQUID) setReplaceable()
		if (kind == Kind.LIQUID) mobility = PushReaction.DESTROY
	}

	private fun setRequiresTool() = apply { tool = true }
	private fun setBurning() = apply { burning = true }
	private fun setReplaceable() = apply { replaceable = true }
	private fun setTranslucent() = apply { translucent = true }
	protected open fun setNoPushMobility(): Material = apply { mobility = PushReaction.DESTROY }
	protected open fun setImmovableMobility(): Material = apply { mobility = PushReaction.BLOCK }

	/** Свойства 1.20.1, которые в 1.7.10 давал материал */
	fun properties(): BlockBehaviour.Properties {
		val props = BlockBehaviour.Properties.of().mapColor(mapColor).pushReaction(pushReaction)
		if (requiresTool) props.requiresCorrectToolForDrops()
		if (canBurn) props.ignitedByLava()
		if (isReplaceable) props.replaceable()
		if (isLiquid) props.liquid()
		if (isSolid) props.forceSolidOn() else props.forceSolidOff()
		if (!isOpaque) props.isValidSpawn { _, _, _, _ -> false }.isRedstoneConductor { _, _, _ -> false }.isSuffocating { _, _, _ -> false }.isViewBlocking { _, _, _ -> false }
		// как по умолчанию в 1.20.1, кроме источника сигнала; полный куб — по форме опоры
		else props.isRedstoneConductor { state, level, pos -> !state.isSignalSource && state.isSupportFullBlock(level, pos) }
			.isSuffocating { state, level, pos -> !state.isSignalSource && state.blocksMotion() && state.isSupportFullBlock(level, pos) }
			.isViewBlocking { state, level, pos -> !state.isSignalSource && state.blocksMotion() && state.isSupportFullBlock(level, pos) }
		// нотный блок 1.7.10 выбирал инструмент по материалу блока под собой
		when (this) {
			rock  -> props.instrument(NoteBlockInstrument.BASEDRUM)
			sand  -> props.instrument(NoteBlockInstrument.SNARE)
			glass -> props.instrument(NoteBlockInstrument.HAT)
			wood  -> props.instrument(NoteBlockInstrument.BASS)
		}
		return props
	}

	/** Форма опоры состояния — полный куб (нормальный куб 1.7.10, см. выше) */
	private fun BlockState.isSupportFullBlock(level: BlockGetter, pos: BlockPos) = Block.isShapeFullBlock(getBlockSupportShape(level, pos))

	companion object {

		@JvmField val air = Material(MapColor.NONE, Kind.TRANSPARENT)
		@JvmField val grass = Material(MapColor.GRASS)
		@JvmField val ground = Material(MapColor.DIRT)
		@JvmField val wood = Material(MapColor.WOOD).setBurning()
		@JvmField val rock = Material(MapColor.STONE).setRequiresTool()
		@JvmField val iron = Material(MapColor.METAL).setRequiresTool()
		@JvmField val anvil = Material(MapColor.METAL).setRequiresTool().setImmovableMobility()
		@JvmField val water = Material(MapColor.WATER, Kind.LIQUID).setNoPushMobility()
		@JvmField val lava = Material(MapColor.FIRE, Kind.LIQUID).setNoPushMobility()
		@JvmField val leaves = Material(MapColor.PLANT).setBurning().setTranslucent().setNoPushMobility()
		@JvmField val plants = Material(MapColor.PLANT, Kind.LOGIC).setNoPushMobility()
		@JvmField val vine = Material(MapColor.PLANT, Kind.LOGIC).setBurning().setNoPushMobility().setReplaceable()
		@JvmField val sponge = Material(MapColor.WOOL)
		@JvmField val cloth = Material(MapColor.WOOL).setBurning()
		@JvmField val fire = Material(MapColor.NONE, Kind.TRANSPARENT).setNoPushMobility()
		@JvmField val sand = Material(MapColor.SAND)
		@JvmField val circuits = Material(MapColor.NONE, Kind.LOGIC).setNoPushMobility()
		@JvmField val carpet = Material(MapColor.WOOL, Kind.LOGIC).setBurning()
		@JvmField val glass = Material(MapColor.NONE).setTranslucent()
		@JvmField val redstoneLight = Material(MapColor.NONE)
		@JvmField val tnt = Material(MapColor.FIRE).setBurning().setTranslucent()
		@JvmField val coral = Material(MapColor.PLANT).setNoPushMobility()
		@JvmField val ice = Material(MapColor.ICE).setTranslucent()
		@JvmField val packedIce = Material(MapColor.ICE)
		@JvmField val snow = Material(MapColor.SNOW, Kind.LOGIC).setReplaceable().setTranslucent().setRequiresTool().setNoPushMobility()
		@JvmField val craftedSnow = Material(MapColor.SNOW).setRequiresTool()
		@JvmField val cactus = Material(MapColor.PLANT).setTranslucent().setNoPushMobility()
		@JvmField val clay = Material(MapColor.CLAY)
		@JvmField val gourd = Material(MapColor.PLANT).setNoPushMobility()
		@JvmField val dragonEgg = Material(MapColor.PLANT).setNoPushMobility()
		@JvmField val portal = Material(MapColor.NONE, Kind.PORTAL).setImmovableMobility()
		@JvmField val cake = Material(MapColor.NONE).setNoPushMobility()
		@JvmField val web = Material(MapColor.WOOL, Kind.WEB).setRequiresTool().setNoPushMobility()
		@JvmField val piston = Material(MapColor.STONE).setImmovableMobility()
	}
}
