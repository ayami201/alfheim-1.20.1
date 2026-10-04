package alfheim.port.legacy

import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.LevelAccessor
import net.minecraftforge.event.ForgeEventFactory
import net.minecraftforge.eventbus.api.Event

/** `net.minecraftforge.event.terraingen.TerrainGen` 1.7.10: события роста, которые другие моды могут отменить */
object TerrainGen {

	/**
	 * `saplingGrowTree`: событие роста дерева из саженца (`SaplingGrowTreeEvent` 1.20.1, без дерева-фичи — генератор
	 * автора не фича 1.20.1); `false` — мод запретил рост
	 */
	@JvmStatic
	fun saplingGrowTree(world: LevelAccessor, random: RandomSource, x: Int, y: Int, z: Int) =
		ForgeEventFactory.blockGrowFeature(world, random, BlockPos(x, y, z), null).result != Event.Result.DENY
}
