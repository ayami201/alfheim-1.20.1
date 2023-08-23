package alfheim.common.world.dim.niflheim.customgens

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.common.world.dim.niflheim.biome.BiomeGenIce
import net.minecraft.world.World
import java.util.*

object WorldGenRibs {
	
	val structure = SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/niflheim/worldgen_1")
	val structureRotated = SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/niflheim/worldgen_1r")
	
	fun generate(world: World, random: Random, x: Int, z: Int) {
		val y = 32
		
		val rotated = random.nextBoolean()
		val xo: Int
		val zo: Int
		
		if (rotated) {
			xo = 17
			zo = 20
		} else {
			xo = 20
			zo = 17
		}
		
		for (i in x.bidiRange(xo))
			for (k in z.bidiRange(zo))
				for (j in y..(y+11))
					if (!world.isAirBlock(i, j, k))
						return
		
		arrayOf(x - xo to z - zo, x - xo to z + zo, x + xo to z + zo, x + xo to z - zo).forEach { (i, k) ->
			if (world.getBiomeGenForCoords(i, k) != BiomeGenIce) return
		}
		
		if (rotated)
			SchemaUtils.generate(world, x, y, z, structureRotated)
		else
			SchemaUtils.generate(world, x, y, z, structure)
	}
}