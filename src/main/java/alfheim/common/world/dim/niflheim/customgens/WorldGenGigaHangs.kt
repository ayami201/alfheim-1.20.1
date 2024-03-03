package alfheim.common.world.dim.niflheim.customgens

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import net.minecraft.world.World
import java.util.*

object WorldGenGigaHangs {
	
	fun generate(world: World, random: Random, x: Int, z: Int) {
		fun genUpper() {
			var y = 100
			while (world.isAirBlock(x, y, z) && y < 110) y++
			y--
			
			for (i in x.bidiRange(5))
				for (k in z.bidiRange(5))
					for (j in (y-20)..y)
						if (!world.isAirBlock(i, j, k))
							return
			
			SchemaUtils.generate(world, x, y, z, "${ModInfo.MODID}/schemas/niflheim/worldgen_2-2", true, true, true, random.nextInt(3))
		}
		
		val r = random.nextInt(9)
		when (r) {
			in 0..4 -> {
				val y = 32
				
				for (i in x.bidiRange(5))
					for (k in z.bidiRange(5))
						for (j in y..(y+20))
							if (!world.isAirBlock(i, j, k))
								return
				
				SchemaUtils.generate(world, x, y, z, "${ModInfo.MODID}/schemas/niflheim/worldgen_2-1", true, true, true, random.nextInt(3))
				
				if (r == 4) genUpper()
			}
			in 5..8 -> genUpper()
		}
	}
}