package alfheim.port.legacy

import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.*

/*
 * `MovingObjectPosition` 1.7.10 — результат луча (MAPPING.md, «Прослойка `alfheim.port.legacy`»).
 *
 * В 1.20.1 это `HitResult`: `BlockHitResult` для блока, `EntityHitResult` для существа. Луч ASJCore
 * (`ASJUtilities.getMouseOver`, `getSelectedBlock`) при промахе отдаёт `null`, как в 1.7.10. Поля 1.7.10 —
 * свойствами ниже; сторона блока — номер 1.7.10 (0 — низ, 1 — верх, 2 — север, 3 — юг, 4 — запад, 5 — восток), он
 * совпадает с `Direction.get3DDataValue` 1.20.1.
 */

typealias MovingObjectPosition = HitResult

/** `MovingObjectPosition.MovingObjectType` 1.7.10 */
enum class MovingObjectType { MISS, BLOCK, ENTITY }

val HitResult.typeOfHit: MovingObjectType
	get() = when (type) {
		HitResult.Type.BLOCK  -> MovingObjectType.BLOCK
		HitResult.Type.ENTITY -> MovingObjectType.ENTITY
		else                  -> MovingObjectType.MISS
	}

val HitResult.hitVec: Vec3 get() = location

val HitResult.blockX get() = (this as BlockHitResult).blockPos.x
val HitResult.blockY get() = (this as BlockHitResult).blockPos.y
val HitResult.blockZ get() = (this as BlockHitResult).blockPos.z

/** У существа в 1.7.10 сторона не задавалась и оставалась 0 */
val HitResult.sideHit get() = (this as? BlockHitResult)?.direction?.get3DDataValue() ?: 0

val HitResult.entityHit: Entity? get() = (this as? EntityHitResult)?.entity
