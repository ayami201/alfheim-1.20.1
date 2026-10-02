package alfheim.port.legacy

import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraftforge.network.PacketDistributor

/**
 * `NetworkRegistry.TargetPoint` из FML 1.7.10: точка и радиус рассылки пакета (SPEC, Р-4).
 * Измерение — `ResourceKey<Level>` вместо номера; радиус — как у автора, не в квадрате.
 */
class TargetPoint(@JvmField val dimension: ResourceKey<Level>, @JvmField val x: Double, @JvmField val y: Double, @JvmField val z: Double, @JvmField val range: Double) {
	
	/** Точка рассылки Forge 1.20.1: радиус в ней задаётся квадратом */
	fun toForge() = PacketDistributor.TargetPoint(x, y, z, range * range, dimension)
}
