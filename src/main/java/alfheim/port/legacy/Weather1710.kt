package alfheim.port.legacy

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LightningBolt
import net.minecraft.world.level.Level
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.EntityJoinLevelEvent
import net.minecraftforge.event.entity.EntityLeaveLevelEvent
import net.minecraftforge.eventbus.api.EventPriority
import java.util.*

/*
 * Погодные эффекты 1.7.10 (MAPPING.md, «Прослойка `alfheim.port.legacy`»): молнии и `EntityWeatherEffect` жили в мире
 * отдельным списком `weatherEffects`, а не среди существ. В 1.20.1 молния — обычное существо мира; список погодных
 * эффектов каждого мира ведёт прослойка — по событиям появления существа в мире и ухода из него ([Weather1710]).
 */

/**
 * `EntityWeatherEffect` 1.7.10 — погодный эффект: существо 1.20.1 с методами 1.7.10. Тип такого существа регистрация
 * порта строит, как тип молнии 1.20.1 (`LegacyRegistration`): мир его не сохраняет (погодные эффекты 1.7.10 не
 * сохранялись), клиент видит его за 16 чанков. Рисуется, даже если его точка за краем экрана (`noCulling`): 1.7.10
 * рисовал погодные эффекты без этой проверки, а молния уходит от своей точки высоко вверх
 */
abstract class EntityWeatherEffect(type: EntityType<*>, world: Level): Entity(type, world) {

	init {
		noCulling = true
	}

	/** `rand` 1.7.10 — случайные числа существа */
	val rand: RandomSource get() = random

	/** `onUpdate()` 1.7.10 — тик существа; `super.onUpdate()` в коде автора — тик существа 1.20.1 */
	open fun onUpdate() = super.tick()

	final override fun tick() = onUpdate()

	/** `entityInit()` 1.7.10 — данные, которые сервер шлёт клиенту (`DataWatcher` 1.7.10) */
	open fun entityInit() = Unit

	final override fun defineSynchedData() = entityInit()

	/** `readEntityFromNBT(tag)` 1.7.10 */
	open fun readEntityFromNBT(tag: CompoundTag) = Unit

	/** `writeEntityToNBT(tag)` 1.7.10 */
	open fun writeEntityToNBT(tag: CompoundTag) = Unit

	final override fun readAdditionalSaveData(tag: CompoundTag) = readEntityFromNBT(tag)

	final override fun addAdditionalSaveData(tag: CompoundTag) = writeEntityToNBT(tag)

	override fun getAddEntityPacket(): Packet<ClientGamePacketListener> = ClientboundAddEntityPacket(this)
}

/**
 * `weatherEffects` 1.7.10 — погодные эффекты мира: молнии и [EntityWeatherEffect], которые сейчас в мире. Удалить
 * эффект через итератор — убрать его из списка, как в 1.7.10; из мира его убирает `setDead`
 */
val Level.weatherEffects: MutableList<Entity> get() = Weather1710.effects.computeIfAbsent(this) { ArrayList() }

/** `addWeatherEffect(entity)` 1.7.10 — погодный эффект появляется в мире; в 1.20.1 — существо мира */
fun Level.addWeatherEffect(entity: Entity) = addFreshEntity(entity)

/** Списки погодных эффектов миров ([weatherEffects]) по событиям Forge */
object Weather1710 {

	/** Погодные эффекты каждого мира; список мира меняет только его поток, запись живёт, пока жив мир */
	internal val effects: MutableMap<Level, MutableList<Entity>> = Collections.synchronizedMap(WeakHashMap())

	/** Подписка на шине Forge — последней, когда появление существа уже никто не отменит; вызывается из конструктора мода */
	fun register() {
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, EntityJoinLevelEvent::class.java) { e ->
			if (isWeatherEffect(e.entity)) e.level.weatherEffects += e.entity
		}
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, EntityLeaveLevelEvent::class.java) { e ->
			if (isWeatherEffect(e.entity)) e.level.weatherEffects -= e.entity
		}
	}

	/** Погодный эффект 1.7.10: молния (`EntityLightningBolt`) или [EntityWeatherEffect] */
	private fun isWeatherEffect(entity: Entity) = entity is LightningBolt || entity is EntityWeatherEffect
}
