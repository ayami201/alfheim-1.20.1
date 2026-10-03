package alfheim.port.legacy

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i

/*
 * Координаты и стороны 1.7.10 (MAPPING.md, «Прослойка `alfheim.port.legacy`»).
 */

/**
 * `ChunkCoordinates` 1.7.10 — `BlockPos` 1.20.1; поля `posX`, `posY`, `posZ` — свойствами ниже. В 1.7.10 точка
 * изменяемая; код автора, который её меняет, переписывается на месте
 */
typealias ChunkCoordinates = BlockPos

val Vec3i.posX get() = x
val Vec3i.posY get() = y
val Vec3i.posZ get() = z

/** `EnumFacing.frontOffsetX`, `frontOffsetY`, `frontOffsetZ` 1.7.10 */
val Direction.frontOffsetX get() = stepX
val Direction.frontOffsetY get() = stepY
val Direction.frontOffsetZ get() = stepZ

/**
 * `ForgeDirection` 1.7.10: шесть сторон по номерам 1.7.10 (0 — низ, 1 — верх, 2 — север, 3 — юг, 4 — запад,
 * 5 — восток; тот же порядок у `Direction` 1.20.1) и `UNKNOWN` — «стороны нет», смещение 0. Для API 1.20.1 —
 * [direction]
 */
enum class ForgeDirection(@JvmField val offsetX: Int, @JvmField val offsetY: Int, @JvmField val offsetZ: Int) {

	DOWN(0, -1, 0), UP(0, 1, 0), NORTH(0, 0, -1), SOUTH(0, 0, 1), WEST(-1, 0, 0), EAST(1, 0, 0), UNKNOWN(0, 0, 0);

	/** Сторона 1.20.1; у `UNKNOWN` — `null` */
	val direction: Direction? get() = if (this == UNKNOWN) null else Direction.from3DDataValue(ordinal)

	companion object {

		@JvmField
		val VALID_DIRECTIONS = arrayOf(DOWN, UP, NORTH, SOUTH, WEST, EAST)

		/** Номер вне 0–5 — `UNKNOWN`, как в 1.7.10 */
		@JvmStatic
		fun getOrientation(id: Int) = if (id in VALID_DIRECTIONS.indices) VALID_DIRECTIONS[id] else UNKNOWN
	}
}
