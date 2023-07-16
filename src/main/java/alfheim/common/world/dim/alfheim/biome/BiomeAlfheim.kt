package alfheim.common.world.dim.alfheim.biome

import alexsocol.asjlib.*
import alfheim.AlfheimCore
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.world.dim.alfheim.customgens.NiflheimLocationGenerator
import alfheim.common.world.dim.alfheim.structure.StructureDreamsTree
import net.minecraft.entity.EnumCreatureType
import net.minecraft.init.Blocks
import net.minecraft.server.MinecraftServer
import net.minecraft.world.biome.BiomeGenBase
import net.minecraftforge.common.*
import net.minecraftforge.common.BiomeDictionary.Type
import ru.vamig.worldengine.WE_Biome

abstract class BiomeAlfheim(
	minMapValue: Double, maxMapValue: Double,
	persistence: Double, numOctaves: Int,
	sx: Double, sy: Double,
	height: Int, interpolateQuality: Int,
	name: String, vararg types: Type
): WE_Biome(minMapValue, maxMapValue, persistence, numOctaves, sx, sy, height + offset, interpolateQuality) {
	
	init {
		setBiomeName(name)
		
		BiomeDictionary.registerBiomeType(this, Type.MAGICAL, *types)
		
		clearSpawn()
		setColor(0x08F500)
		grassColor = 0x08F500
		waterColorMultiplier = if (AlfheimCore.winter) 0x1D1D4E else 0x00FFFF
		temperature = if (AlfheimCore.winter) 0f else 0.5f
		
		createChunkGen_InXZ_List.clear()
		decorateChunkGen_List.clear()
		
		alfheimBiomes.add(this)
	}
	
	override fun getFloatTemperature(x: Int, y: Int, z: Int): Float {
		val world = if (ASJUtilities.isServer) DimensionManager.getWorld(AlfheimConfigHandler.dimensionIDAlfheim) else mc.theWorld
		val (xOff, zOff) = if (world == null) 0 to 0 else NiflheimLocationGenerator.portalXZ(world)
		return if (AlfheimCore.winter || NiflheimLocationGenerator.yobaFunction2d(x - xOff, z - zOff)) 0f else 0.5f
	}
	
	override fun getSkyColorByTemp(temp: Float) = if (AlfheimCore.winter || temp < 0.5f) 0x576cd9 else 0x266eff
	
	companion object {
		
		const val offset = -7
		
		val alfheimBiomes = ArrayList<BiomeAlfheim>()
		
		val dreamTree = StructureDreamsTree(AlfheimBlocks.altWood1, AlfheimBlocks.altLeaves, 3, 7, 11, 7)
		val sadOak = StructureDreamsTree(Blocks.log, AlfheimBlocks.sadOakLeaves, 0, 4, 8, 0)
		
		fun BiomeGenBase.addEntry(clazz: Class<*>, rate: IntArray, type: EnumCreatureType = EnumCreatureType.creature) {
			val (w, i, x) = rate
			this.getSpawnableList(type).add(SpawnListEntry(clazz, w, i, x))
		}
	}
}