package alfheim.common.world.dim.alfheim.customgens

import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.handler.AlfheimConfigHandler
import cpw.mods.fml.common.IWorldGenerator
import net.minecraft.init.Blocks
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import java.util.*

class WorldGenMelonPumpkins: IWorldGenerator {
	
	override fun generate(rand: Random, chunkX: Int, chunkZ: Int, world: World, chunkGenerator: IChunkProvider?, chunkProvider: IChunkProvider?) {
		if (world.provider.dimensionId != AlfheimConfigHandler.dimensionIDAlfheim) return
		
		if (rand.nextInt(10) != 0) return
		
		val xc = chunkX * 16 + rand.nextInt(16)
		val zc = chunkZ * 16 + rand.nextInt(16)
		
		val block = if (rand.nextBoolean()) Blocks.melon_block else Blocks.pumpkin
		
		var retries = 64
		var count = rand.nextInt(8) + 4
		
		while (--retries > 0 && count > 0) {
			val x = xc + rand.nextInt(8) - 4
			val z = zc + rand.nextInt(8) - 4
			
			val y = world.getTopSolidOrLiquidBlock(x, z)
			
			if (!block.canPlaceBlockAt(world, x, y, z) || !world.getBlock(x, y - 1, z).let { it === Blocks.grass || it === AlfheimBlocks.snowGrass })
				continue
			
			world.setBlock(x, y, z, block, 0, 2)
			--count
		}
	}
}
