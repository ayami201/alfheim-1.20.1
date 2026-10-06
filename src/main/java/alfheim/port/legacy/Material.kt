package alfheim.port.legacy

import net.minecraft.world.level.block.state.BlockBehaviour
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
 * 1.20.1): сигнал через себя он не проводил, в нём не задыхались.
 */
class Material private constructor(val mapColor: MapColor, kind: Kind = Kind.NORMAL) {

	private enum class Kind { NORMAL, TRANSPARENT, LOGIC, LIQUID, PORTAL, WEB }

	var requiresTool = false
		private set
	var canBurn = false
		private set
	var isReplaceable = false
		private set
	var isTranslucent = false
		private set
	var pushReaction = PushReaction.NORMAL
		private set

	val isLiquid = kind == Kind.LIQUID
	val isSolid = kind == Kind.NORMAL || kind == Kind.WEB
	val blocksMovement = kind == Kind.NORMAL
	
	/** `Material.isOpaque` 1.7.10 */
	val isOpaque get() = !isTranslucent && blocksMovement

	init {
		// конструкторы MaterialTransparent и MaterialLiquid 1.7.10
		if (kind == Kind.TRANSPARENT || kind == Kind.LIQUID) setReplaceable()
		if (kind == Kind.LIQUID) setNoPushMobility()
	}

	private fun setRequiresTool() = apply { requiresTool = true }
	private fun setBurning() = apply { canBurn = true }
	private fun setReplaceable() = apply { isReplaceable = true }
	private fun setTranslucent() = apply { isTranslucent = true }
	private fun setNoPushMobility() = apply { pushReaction = PushReaction.DESTROY }
	private fun setImmovableMobility() = apply { pushReaction = PushReaction.BLOCK }

	/** Свойства 1.20.1, которые в 1.7.10 давал материал */
	fun properties(): BlockBehaviour.Properties {
		val props = BlockBehaviour.Properties.of().mapColor(mapColor).pushReaction(pushReaction)
		if (requiresTool) props.requiresCorrectToolForDrops()
		if (canBurn) props.ignitedByLava()
		if (isReplaceable) props.replaceable()
		if (isLiquid) props.liquid()
		if (isSolid) props.forceSolidOn() else props.forceSolidOff()
		if (!isOpaque) props.isValidSpawn { _, _, _, _ -> false }.isRedstoneConductor { _, _, _ -> false }.isSuffocating { _, _, _ -> false }.isViewBlocking { _, _, _ -> false }
		// как по умолчанию в 1.20.1, кроме источника сигнала
		else props.isRedstoneConductor { state, level, pos -> !state.isSignalSource && state.isCollisionShapeFullBlock(level, pos) }
			.isSuffocating { state, level, pos -> !state.isSignalSource && state.blocksMotion() && state.isCollisionShapeFullBlock(level, pos) }
			.isViewBlocking { state, level, pos -> !state.isSignalSource && state.blocksMotion() && state.isCollisionShapeFullBlock(level, pos) }
		// нотный блок 1.7.10 выбирал инструмент по материалу блока под собой
		when (this) {
			rock  -> props.instrument(NoteBlockInstrument.BASEDRUM)
			sand  -> props.instrument(NoteBlockInstrument.SNARE)
			glass -> props.instrument(NoteBlockInstrument.HAT)
			wood  -> props.instrument(NoteBlockInstrument.BASS)
		}
		return props
	}

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
