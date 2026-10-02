package alfheim.common.world.dim.alfheim.biome

import alfheim.AlfheimCore
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.world.dim.alfheim.customgens.*
import net.minecraft.init.Blocks
import net.minecraftforge.common.BiomeDictionary.Type
import ru.vamig.worldengine.standardcustomgen.*
import vazkii.botania.common.block.ModBlocks

object BiomePitGiantFlowers: BiomeAlfheim(1.0, 10.0, 1.8, 3, 250.0, 2.0, 71, 2, "Mutated field", Type.PLAINS, Type.DENSE, Type.LUSH) {
	
	init {
		var standardBiomeLayers = WE_BiomeLayer()
		standardBiomeLayers.add(Blocks.dirt, 0.toByte(), ModBlocks.livingrock, 0.toByte(), -256, 0, -4, -2, true)
		standardBiomeLayers.add(if (AlfheimCore.winter && AlfheimConfigHandler.winterGrassReadyGen) AlfheimBlocks.snowGrass else Blocks.grass, 0.toByte(), Blocks.dirt, 0.toByte(), -256, 0, -256, 0, false)
		createChunkGen_InXZ_List.add(standardBiomeLayers)
		standardBiomeLayers = WE_BiomeLayer()
		standardBiomeLayers.add(Blocks.bedrock, 0.toByte(), 0, 0, 0, 0, true)
		createChunkGen_InXZ_List.add(standardBiomeLayers)
		val g = WorldGenGrass(true, true, false, false, 2.0)
		decorateChunkGen_List.add(g)
		val t = WE_StructureGen()
		t.add(WorldGenMutatedFlowers, 2)
		decorateChunkGen_List.add(t)
	}
}