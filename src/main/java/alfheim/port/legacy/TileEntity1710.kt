package alfheim.port.legacy

import alfheim.port.registry.LegacyRegistration
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

/*
 * Блок-сущности 1.7.10 (MAPPING.md, «Мир, блоки, блок-сущности»).
 *
 * Блок-сущность 1.7.10 создавалась пустой (`TileTreeBerry()`), а мир и координаты получала потом. В 1.20.1 она
 * создаётся сразу в своей точке, со своим типом и состоянием блока: конструктор `()` в коде автора становится
 * `(pos, state)`, `super()` — `super(legacyTileType<Класс>(), pos, state)`, а `createNewTileEntity(world, meta)` блока
 * ([ITileEntityProvider]) — `newBlockEntity(pos, state)`. Тип — по классу из регистрации автора
 * (`GameRegistry.registerTileEntity` → `LegacyRegistration.tile`). Остальное — методы 1.7.10 [TileEntity].
 */

/** Тип 1.20.1 блок-сущности автора [T] */
inline fun <reified T: BlockEntity> legacyTileType(): BlockEntityType<T> = LegacyRegistration.tileType(T::class.java)

/** `getDescriptionPacket()` 1.7.10 — данные блок-сущности для клиента (`getUpdatePacket` 1.20.1); `null` — нечего слать */
val BlockEntity.descriptionPacket: Packet<ClientGamePacketListener>? get() = updatePacket

/**
 * `net.minecraft.tileentity.TileEntity` 1.7.10. Методы, которые переопределяет автор: [updateEntity] — каждый тик и на
 * сервере, и на клиенте, если [canUpdate] (тик даёт блок, [ITileEntityProvider]); [readFromNBT] и [writeToNBT] — то,
 * что блок-сущность сохраняет (1.20.1 — `load`, `saveAdditional`; id и координаты 1.20.1 пишет сама);
 * [receiveClientEvent] — событие блока (`triggerEvent`)
 */
abstract class TileEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState): BlockEntity(type, pos, state) {

	init {
		LegacyRegistration.tileCreated(type, state.block)
	}

	/** `worldObj` 1.7.10: мир блок-сущности; 1.7.10 тикал её и звал её методы, когда мир у неё уже был */
	val worldObj: Level get() = level!!

	val xCoord get() = worldPosition.x
	val yCoord get() = worldPosition.y
	val zCoord get() = worldPosition.z

	/** `getBlockType()` 1.7.10: блок, которому принадлежит блок-сущность */
	fun getBlockType(): Block = blockState.block

	/** `markDirty()` 1.7.10: данные изменились — чанк надо сохранить */
	fun markDirty() = setChanged()

	/** `updateEntity()` 1.7.10 — тик блок-сущности */
	open fun updateEntity() = Unit

	/** `canUpdate()` 1.7.10: тикать ли блок-сущность ([updateEntity]) */
	open fun canUpdate() = true
	
	/** Мир 1.7.10 спрашивал [canUpdate], когда блок-сущность попадала в мир; ответ запоминает её тип (`getTicker` блока) */
	override fun setLevel(level: Level) {
		super.setLevel(level)
		LegacyRegistration.tileLoaded(type, level.isClientSide, canUpdate())
	}

	/** `readFromNBT(nbt)` 1.7.10 — данные блок-сущности при загрузке */
	open fun readFromNBT(nbt: CompoundTag) = Unit

	/** `writeToNBT(nbt)` 1.7.10 — данные блок-сущности при сохранении */
	open fun writeToNBT(nbt: CompoundTag) = Unit

	override fun load(tag: CompoundTag) {
		super.load(tag)
		readFromNBT(tag)
	}

	override fun saveAdditional(tag: CompoundTag) {
		super.saveAdditional(tag)
		writeToNBT(tag)
	}

	/** `receiveClientEvent(id, param)` 1.7.10 — событие блока (`addBlockEvent`), которое дошло до блок-сущности */
	open fun receiveClientEvent(id: Int, param: Int) = false

	override fun triggerEvent(id: Int, param: Int) = receiveClientEvent(id, param)
}

/**
 * `net.minecraft.block.BlockContainer` 1.7.10: блок с блок-сущностью ([ITileEntityProvider]), событие блока передаёт
 * ей. Блок-сущность убирает `onRemove` 1.20.1, когда блок сменился (`breakBlock` 1.7.10 — `removeTileEntity`). Модель
 * у блока своя, как у блока 1.7.10 (`BaseEntityBlock` 1.20.1 по умолчанию её не рисует)
 */
abstract class BlockContainer(material: Material): Block1710(material), ITileEntityProvider {

	@Deprecated("Deprecated in Java")
	override fun triggerEvent(state: BlockState, level: Level, pos: BlockPos, id: Int, param: Int): Boolean {
		@Suppress("DEPRECATION")
		super.triggerEvent(state, level, pos, id, param)
		return level.getBlockEntity(pos)?.triggerEvent(id, param) ?: false
	}
}
