package alfheim.port.legacy

import alfheim.port.registry.LegacyRegistration
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceKey
import net.minecraft.util.RandomSource
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.*
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.entity.projectile.ThrowableProjectile
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import kotlin.math.*

/*
 * Существа 1.7.10 (MAPPING.md, «Прослойка `alfheim.port.legacy`»).
 *
 * Конструктор существа 1.20.1 принимает его тип первым аргументом; тип существа автора — [legacyType], по классу из
 * регистрации автора (`ASJUtilities.registerEntity` → `LegacyRegistration.entity`). Поэтому `super(world)` в коде
 * автора становится `super(legacyType<Класс>(), world)`, остальное — как было.
 */

/** Тип 1.20.1 существа автора [T] */
inline fun <reified T: Entity> legacyType(): EntityType<T> = LegacyRegistration.entityType(T::class.java)

/**
 * `net.minecraft.entity.Entity` 1.7.10 — существо с методами 1.7.10 поверх `Entity` 1.20.1:
 * - [onUpdate] — тик существа (`tick` 1.20.1), [onEntityUpdate] — его общая часть (`baseTick`: огонь, вода, лава,
 *   портал, падение за край мира); `super.onUpdate()` и `super.onEntityUpdate()` в коде автора — они же в 1.20.1;
 * - [entityInit] — данные, которые сервер шлёт клиенту (`DataWatcher` 1.7.10), [readEntityFromNBT] и
 *   [writeEntityToNBT] — сохранение;
 * - [moveEntity] — движение со столкновениями (`move` 1.20.1), после него — [isCollided];
 * - [setSize] — размер: в 1.7.10 его задавало существо, в 1.20.1 — тип; по умолчанию 0,6 × 1,8, как в 1.7.10.
 */
abstract class Entity1710(type: EntityType<*>, world: Level): Entity(type, world) {

	/** `rand` 1.7.10 — случайные числа существа */
	val rand: RandomSource get() = random

	/** `isCollided` 1.7.10: существо столкнулось с блоком при последнем [moveEntity]; код автора поднимает его и сам */
	var isCollided = false

	private var size = EntityDimensions.scalable(0.6f, 1.8f)

	init {
		refreshDimensions()
	}

	/** `setSize(width, height)` 1.7.10 */
	fun setSize(width: Float, height: Float) {
		size = EntityDimensions.scalable(width, height)
		refreshDimensions()
	}

	override fun getDimensions(pose: Pose): EntityDimensions = size

	/** `onUpdate()` 1.7.10 — тик существа */
	open fun onUpdate() = super.tick()

	final override fun tick() = onUpdate()

	/** `onEntityUpdate()` 1.7.10 — общий тик существа (`baseTick` 1.20.1), его зовёт [onUpdate] */
	open fun onEntityUpdate() = super.baseTick()

	final override fun baseTick() = onEntityUpdate()

	/**
	 * `moveEntity(dx, dy, dz)` 1.7.10 — сдвиг со столкновениями (`move` 1.20.1): упор в блок гасит скорость по его оси,
	 * падение копит `fallDistance`, существо задевает блоки на пути
	 */
	fun moveEntity(x: Double, y: Double, z: Double) {
		move(MoverType.SELF, Vec3(x, y, z))
		isCollided = horizontalCollision || verticalCollision
	}

	/** `entityInit()` 1.7.10 — данные, которые сервер шлёт клиенту (`DataWatcher` 1.7.10, MAPPING.md) */
	open fun entityInit() = Unit

	final override fun defineSynchedData() = entityInit()

	/** `readEntityFromNBT(tag)` 1.7.10 */
	open fun readEntityFromNBT(tag: CompoundTag) = Unit

	/** `writeEntityToNBT(tag)` 1.7.10 */
	open fun writeEntityToNBT(tag: CompoundTag) = Unit

	final override fun readAdditionalSaveData(tag: CompoundTag) = readEntityFromNBT(tag)

	final override fun addAdditionalSaveData(tag: CompoundTag) = writeEntityToNBT(tag)
}

/** `worldObj` 1.7.10 */
val Entity.worldObj: Level get() = level()

/** `setDead()` 1.7.10 */
fun Entity.setDead() = discard()

/** `getDistanceSqToEntity(entity)` 1.7.10 */
fun Entity.getDistanceSqToEntity(entity: Entity) = distanceToSqr(entity)

/** `attackEntityFrom(source, amount)` 1.7.10 */
fun Entity.attackEntityFrom(source: DamageSource, amount: Float) = hurt(source, amount)

/** `setFire(seconds)` 1.7.10 */
fun Entity.setFire(seconds: Int) = setSecondsOnFire(seconds)

/**
 * `posX`, `posY`, `posZ` 1.7.10; запись — `setPos`. `posY` — низ существа, как у большинства существ 1.7.10; у своего
 * игрока на клиенте 1.7.10 `posY` был на уровне глаз — такие места переносятся на месте вызова
 */
var Entity.posX: Double
	get() = x
	set(value) = setPos(value, y, z)

var Entity.posY: Double
	get() = y
	set(value) = setPos(x, value, z)

var Entity.posZ: Double
	get() = z
	set(value) = setPos(x, y, value)

/** `motionX`, `motionY`, `motionZ` 1.7.10 — `deltaMovement` 1.20.1 */
var Entity.motionX: Double
	get() = deltaMovement.x
	set(value) {
		deltaMovement = Vec3(value, deltaMovement.y, deltaMovement.z)
	}

var Entity.motionY: Double
	get() = deltaMovement.y
	set(value) {
		deltaMovement = Vec3(deltaMovement.x, value, deltaMovement.z)
	}

var Entity.motionZ: Double
	get() = deltaMovement.z
	set(value) {
		deltaMovement = Vec3(deltaMovement.x, deltaMovement.y, value)
	}

/** `rotationYaw`, `rotationPitch` 1.7.10 */
var Entity.rotationYaw: Float
	get() = yRot
	set(value) {
		yRot = value
	}

var Entity.rotationPitch: Float
	get() = xRot
	set(value) {
		xRot = value
	}

/** `setLocationAndAngles(x, y, z, yaw, pitch)` 1.7.10 */
fun Entity.setLocationAndAngles(x: Double, y: Double, z: Double, yaw: Float, pitch: Float) = moveTo(x, y, z, yaw, pitch)

/** `setPosition(x, y, z)` 1.7.10 */
fun Entity.setPosition(x: Double, y: Double, z: Double) = setPos(x, y, z)

/** `lastTickPosX`, `lastTickPosY`, `lastTickPosZ` 1.7.10 — где существо было в начале тика (`xOld` … 1.20.1) */
val Entity.lastTickPosX: Double get() = xOld
val Entity.lastTickPosY: Double get() = yOld
val Entity.lastTickPosZ: Double get() = zOld

/** `air` 1.7.10 — запас воздуха (`airSupply` 1.20.1); сервер шлёт его клиенту, как в 1.7.10 */
var Entity.air: Int
	get() = airSupply
	set(value) {
		airSupply = value
	}

/**
 * Источник урона 1.7.10 без виновника (`DamageSource.fallingBlock`, `DamageSource.inWall`…) — тип урона 1.20.1 с тем же
 * именем из реестра мира. Без виновника урон не отбрасывает, как в 1.7.10; `damageSources().fallingBlock(существо)`
 * 1.20.1 указал бы существо — и отбрасывал бы от него
 */
fun Level.damageSource(type: ResourceKey<DamageType>) = DamageSource(registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type))

/** `entityItem` 1.7.10 — стак предмета, который лежит на земле */
val ItemEntity.entityItem: ItemStack get() = item

/**
 * `EntityThrowable` 1.7.10 поверх `ThrowableProjectile` 1.20.1. Полёт, сопротивление воздуха и воды, поиск удара — 1.20.1;
 * бросок, скорость, сила тяжести и удар — методы и числа 1.7.10:
 * - бросок — из глаз бросающего, на 0,16 в сторону и 0,1 вниз, направление — взгляд с поправкой угла
 *   [func_70183_g], скорость — [func_70182_d], разброс 1; скорость бросающего не прибавляется, как в 1.7.10;
 * - сила тяжести — [getGravityVelocity];
 * - удар — [onImpact] автора, вместо которого 1.20.1 зовёт `onHit`. Блоки 1.20.1, которые отвечают на удар снаряда
 *   (мишень, колокол), не отвечают — в 1.7.10 их не было;
 * - размер 0,25 × 0,25, как в 1.7.10.
 */
abstract class EntityThrowable(type: EntityType<out EntityThrowable>, world: Level): ThrowableProjectile(type, world) {

	/** `EntityThrowable(world, thrower)` 1.7.10 */
	constructor(type: EntityType<out EntityThrowable>, world: Level, thrower: LivingEntity): this(type, world) {
		owner = thrower
		moveTo(thrower.x, thrower.eyeY, thrower.z, thrower.yRot, thrower.xRot)
		setPos(x - cos(yRot / 180f * PI) * 0.16, y - 0.1, z - sin(yRot / 180f * PI) * 0.16)
		val f = 0.4
		val motionX = -sin(yRot / 180f * PI) * cos(xRot / 180f * PI) * f
		val motionZ = cos(yRot / 180f * PI) * cos(xRot / 180f * PI) * f
		val motionY = -sin((xRot + func_70183_g()) / 180f * PI) * f
		shoot(motionX, motionY, motionZ, func_70182_d(), 1f)
	}

	private var size = EntityDimensions.scalable(0.25f, 0.25f)

	init {
		refreshDimensions()
	}

	/** `setSize(width, height)` 1.7.10 */
	fun setSize(width: Float, height: Float) {
		size = EntityDimensions.scalable(width, height)
		refreshDimensions()
	}

	override fun getDimensions(pose: Pose): EntityDimensions = size

	/** `getThrower()` 1.7.10 */
	val thrower: LivingEntity? get() = owner as? LivingEntity

	/** `setThrowableHeading(x, y, z, velocity, inaccuracy)` 1.7.10 — `shoot` 1.20.1 (разброс треугольный, той же величины) */
	fun setThrowableHeading(x: Double, y: Double, z: Double, velocity: Float, inaccuracy: Float) = shoot(x, y, z, velocity, inaccuracy)

	/** Скорость броска; в 1.7.10 по умолчанию 1,5 */
	open fun func_70182_d() = 1.5f

	/** Поправка угла броска вверх, в градусах; в 1.7.10 по умолчанию 0 */
	open fun func_70183_g() = 0f

	/** Сила тяжести за тик; в 1.7.10 по умолчанию 0,03 */
	open fun getGravityVelocity() = 0.03f

	final override fun getGravity() = getGravityVelocity()

	/** `onImpact` 1.7.10: удар о блок или существо, на обеих сторонах */
	abstract fun onImpact(movingObject: MovingObjectPosition?)

	final override fun onHit(result: HitResult) = onImpact(result)

	override fun defineSynchedData() = Unit
}
