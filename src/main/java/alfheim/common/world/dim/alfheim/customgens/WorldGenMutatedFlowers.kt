package alfheim.common.world.dim.alfheim.customgens

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.common.block.AlfheimBlocks
import alfheim.common.world.dim.alfheim.biome.BiomeIslandGiantFlowers
import net.minecraft.world.World
import ru.vamig.worldengine.*
import ru.vamig.worldengine.standardcustomgen.StructureBaseClass
import vazkii.botania.common.block.ModBlocks
import java.io.IOException
import java.util.*

object WorldGenMutatedFlowers: StructureBaseClass() {
	
	val flowerStructures: Map<Int, List<String>> = (0..16).associateWith {
		val subStructures = mutableListOf<String>()
		var i = 0
		while (true) {
			try {
				subStructures += SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/flowers/$it-${i++}")
			} catch (e: IOException) {
				break
			} catch (e: NullPointerException) {
				break
			}
		}
		
		subStructures
	}
	
	override fun generate(world: World, rand: Random, x: Int, y: Int, z: Int, chunkProvider: WE_ChunkProvider): Boolean {
		arrayOf(-1 to -1, 1 to 1, -1 to 1, 1 to -1).forEach { (i, k) ->
			if (WE_Biome.getBiomeAt(chunkProvider, x + i * 24, z + k * 24) !== BiomeIslandGiantFlowers)
				return false
		}
		
		val color = flowerStructures.keys.random(rand)!!
		val flower = flowerStructures[color]?.random(rand) ?: return false
		
		val flowerBlock = if (color < 16) ModBlocks.flower else AlfheimBlocks.rainbowGrass
		val flowerMeta = if (color < 16) color else 2
		for (u in 0.. ASJUtilities.randInBounds(8, 16)) {
			val i = x + ASJUtilities.randInBounds(-4, 4)
			val k = z + ASJUtilities.randInBounds(-4, 4)
			val j = world.getTopSolidOrLiquidBlock(i, k)
			world.setBlock(i, j, k, flowerBlock, flowerMeta, 3)
		}
		
		SchemaUtils.generate(world, x, y, z, flower)
		return true
	}
}
