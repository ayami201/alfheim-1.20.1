package alfheim.common.world.dim.niflheim.customgens

import alexsocol.asjlib.*
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.alt.BlockAltLeaves
import alfheim.common.world.dim.niflheim.ChunkProviderNiflheim
import net.minecraft.init.Blocks
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection.*
import java.util.*

object WorldGenRoot {
	
	val dirs = mutableListOf(NORTH, SOUTH, EAST, WEST)
	
	fun generate(world: World, random: Random, i: Int, j: Int, k: Int): Boolean {
		if (!world.isAirBlock(i, j, k))
			return false
		
		if (world.getBlock(i, j + 1, k) inln ChunkProviderNiflheim.surfaceBlocks)
			return false
		
		world.setBlock(i, j, k, AlfheimBlocks.altWood1, BlockAltLeaves.yggMeta % 4, 3)
		
		val placed = mutableListOf<Triple<Int, Int, Int>>()
		
		repeat (1500) {
			val x = i + random.nextInt(8) - random.nextInt(8)
			val y = j - random.nextInt(12)
			val z = k + random.nextInt(8) - random.nextInt(8)
			
			if (world.getBlock(x, y, z) !== Blocks.air) return@repeat
			
			val yggBlocksNear = VALID_DIRECTIONS.count { world.getBlock(x + it.offsetX, y + it.offsetY, z + it.offsetZ) === AlfheimBlocks.altWood1 }
			
			if (yggBlocksNear != 1) return@repeat
			
			world.setBlock(x, y, z, AlfheimBlocks.altWood1, BlockAltLeaves.yggMeta % 4, 3)
			placed += x to y with z
		}
		
		if (random.nextInt(5) != 0) return true
		
		placed.shuffled().find { (x, y, z) ->
			val d = dirs.shuffled().find { world.isAirBlock(x + it.offsetX, y + it.offsetY, z + it.offsetZ) } ?: return@find false
			
			val meta = when (d) {
				NORTH   -> 0
				SOUTH   -> 2
				WEST    -> 1
				EAST    -> 3
				else    -> return@find false
			}
			
			world.setBlock(x + d.offsetX, y + d.offsetY, z + d.offsetZ, AlfheimBlocks.nidhoggTooth, meta, 3)
		}
		
		return true
	}
}