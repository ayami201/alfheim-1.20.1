package alfheim.common.block.magtrees.calico

// PORT: импорты 1.20.1
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level as World

interface IExplosionDampener {
	
	/**
	 * Make sure to remove explosion processing
	 */
	fun onBlockExploded(world: World, x: Int, y: Int, z: Int, explosion: Explosion)
}