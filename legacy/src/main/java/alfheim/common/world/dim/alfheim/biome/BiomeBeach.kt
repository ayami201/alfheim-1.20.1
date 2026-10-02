package alfheim.common.world.dim.alfheim.biome

import alfheim.common.block.AlfheimBlocks
import net.minecraft.init.Blocks
import net.minecraftforge.common.BiomeDictionary.Type
import ru.vamig.worldengine.standardcustomgen.WE_BiomeLayer
import vazkii.botania.common.block.ModBlocks

object BiomeBeach: BiomeAlfheim(-0.5, -0.35, 1.33, 3, 250.0, 1.4, 65, 4, "Beach", Type.SANDY, Type.SPARSE, Type.BEACH) {
	
	init {
		var standardBiomeLayers = WE_BiomeLayer()
		standardBiomeLayers.add(AlfheimBlocks.elvenSand, 0.toByte(), ModBlocks.livingrock, 0.toByte(), -256, 0, -4, -2, true)
		createChunkGen_InXZ_List.add(standardBiomeLayers)
		standardBiomeLayers = WE_BiomeLayer()
		standardBiomeLayers.add(Blocks.bedrock, 0.toByte(), 0, 0, 0, 0, true)
		createChunkGen_InXZ_List.add(standardBiomeLayers)
	}
}