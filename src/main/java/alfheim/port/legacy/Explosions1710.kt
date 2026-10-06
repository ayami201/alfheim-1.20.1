package alfheim.port.legacy

import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.level.ExplosionEvent
import net.minecraftforge.eventbus.api.EventPriority
import java.util.*

/*
 * Взрыв 1.7.10 — `net.minecraft.world.Explosion` и `World.newExplosion` (MAPPING.md, «Прослойка `alfheim.port.legacy`»):
 * поля взрыва под именами 1.7.10 и взрыв с теми же параметрами
 */

/** `Explosion.explosionX` 1.7.10 — центр взрыва */
val Explosion.explosionX: Double get() = position.x

val Explosion.explosionY: Double get() = position.y

val Explosion.explosionZ: Double get() = position.z

/** `Explosion.explosionSize` 1.7.10 — сила взрыва (поле открыто `META-INF/accesstransformer.cfg`) */
val Explosion.explosionSize: Float get() = radius

/**
 * `World.newExplosion` 1.7.10: [isFlaming] — поджигает ли, [isSmoking] — рушит ли блоки. Рушит — как взрыв блока 1.20.1:
 * вещи из блоков выпадают с шансом 1 / сила взрыва, как в 1.7.10. Существ взрыв ранит в обоих случаях, как в 1.7.10
 */
fun Level.newExplosion(entity: Entity?, x: Double, y: Double, z: Double, size: Float, isFlaming: Boolean, isSmoking: Boolean): Explosion =
	explode(entity, x, y, z, size, isFlaming, if (isSmoking) Level.ExplosionInteraction.BLOCK else Level.ExplosionInteraction.NONE)

/**
 * Взрыв, отменённый в `ExplosionEvent.Start`. В Forge 1.7.10 его не было вовсе. Forge 1.20.1 его не производит, но
 * сервер всё равно шлёт игрокам рядом пакет взрыва — у них звучит и виден взрыв, которого нет. Отменённые взрывы
 * записываются здесь, и сервер их пакет не шлёт (миксин `alfheim.port.mixin.ServerLevelMixin`) — как в 1.7.10
 */
object Explosions1710 {

	/** Взрыв сравнивается по ссылке (`equals` у него свой не задан); запись живёт, пока жив взрыв */
	private val canceled: MutableSet<Explosion> = Collections.newSetFromMap(WeakHashMap())

	/** Подписка на шине Forge — последней, после всех, кто мог отменить взрыв; вызывается из конструктора мода */
	fun register() {
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, true, ExplosionEvent.Start::class.java) { e ->
			if (e.isCanceled && !e.level.isClientSide) canceled += e.explosion
		}
	}

	/** Отменён ли взрыв в `ExplosionEvent.Start`; запись удаляется */
	@JvmStatic
	fun wasCanceled(explosion: Explosion) = canceled.remove(explosion)
}
