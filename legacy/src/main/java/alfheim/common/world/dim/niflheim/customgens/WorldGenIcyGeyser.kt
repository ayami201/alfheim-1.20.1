package alfheim.common.world.dim.niflheim.customgens

import alfheim.common.block.AlfheimBlocks
import net.minecraft.init.Blocks
import net.minecraft.world.World

object WorldGenIcyGeyser {
	
	fun generate(world: World, x: Int, z: Int): Boolean {
		if (world.getBlock(x, 31, z) !== Blocks.packed_ice) return false
		
		for (y in 32..40) if (!world.isAirBlock(x, y, z)) return false
		
		return world.setBlock(x, 31, z, AlfheimBlocks.icyGeyser)
	}
}