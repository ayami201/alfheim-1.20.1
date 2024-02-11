package alfheim.common.world.dim.niflheim.customgens

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import net.minecraft.world.World
import java.util.*

object WorldGenGigaRoot {
	
	fun generate(world: World, random: Random, x: Int, z: Int) {
		var y = 100
		while (world.isAirBlock(x, y, z) && y < 110) y++
		--y
		
		for (i in x.bidiRange(17))
			for (k in z.bidiRange(17))
				for (j in (y-28)..y)
					if (!world.isAirBlock(i, j, k))
						return
		
		SchemaUtils.generate(world, x, ++y, z, "${ModInfo.MODID}/schemas/niflheim/worldgen_3", true, true, true, random.nextInt(3))
	}
}