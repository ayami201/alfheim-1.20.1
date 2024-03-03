package alfheim.common.world.dim.niflheim.customgens

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import net.minecraft.world.World
import java.util.*

object WorldGenTentacles {
	
	fun generate(world: World, random: Random, x: Int, z: Int) {
		val y = 32
		
		for (i in x.bidiRange(10))
			for (k in z.bidiRange(10))
				for (j in y..(y+11))
					if (!world.isAirBlock(i, j, k))
						return
		
		SchemaUtils.generate(world, x, y, z, "${ModInfo.MODID}/schemas/niflheim/worldgen_0", true, true, true, random.nextInt(4))
	}
}