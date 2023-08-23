package alfheim.common.world.dim.niflheim.customgens

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import net.minecraft.world.World

object WorldGenGigaRoot {
	
	val gigaroot = SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/niflheim/worldgen_3")
	
	fun generate(world: World, x: Int, z: Int) {
		var y = 100
		while (world.isAirBlock(x, y, z) && y < 110) y++
		--y
		
		for (i in x.bidiRange(17))
			for (k in z.bidiRange(17))
				for (j in (y-28)..y)
					if (!world.isAirBlock(i, j, k))
						return
		
		SchemaUtils.generate(world, x, ++y, z, gigaroot)
	}
}