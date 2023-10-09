package alfheim.common.world.dim.niflheim.customgens

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import net.minecraft.tileentity.TileEntityChest
import net.minecraft.util.WeightedRandomChestContent
import net.minecraft.world.World
import net.minecraftforge.common.ChestGenHooks
import java.util.*

object WorldGenIglu {
	
	val iglu1 = SchemaUtils.parseWithRotations("${ModInfo.MODID}/schemas/niflheim/worldgen_5") // broken
	val iglu2 = SchemaUtils.parseWithRotations("${ModInfo.MODID}/schemas/niflheim/worldgen_6") // small
	val iglu3 = SchemaUtils.parseWithRotations("${ModInfo.MODID}/schemas/niflheim/worldgen_7") // big
	
	private const val y = 32
	
	fun generate(world: World, random: Random, x: Int, z: Int) {
		val type = when(random.nextInt(10)) {
			in 0..1 -> 0 // broken
			in 2..6 -> 1 // small
			else    -> 2 // big
		}
		
		when (type) {
			0 -> {
				for (i in x.bidiRange(13))
					for (k in z.bidiRange(13))
						for (j in y..y+9)
							if (!world.isAirBlock(i, j, k))
								return
				
				SchemaUtils.generate(world, x, y, z, iglu1.random(random)!!)
				searchAndGenChests(world, random, x, z, 13, 9)
			}
			1 -> {
				for (i in x.bidiRange(5))
					for (k in z.bidiRange(5))
						for (j in y..y+4)
							if (!world.isAirBlock(i, j, k))
								return
				
				SchemaUtils.generate(world, x, y, z, iglu2.random(random)!!)
				searchAndGenChests(world, random, x, z, 5, 4)
			}
			2 -> {
				for (i in x.bidiRange(8))
					for (k in z.bidiRange(8))
						for (j in y..y+5)
							if (!world.isAirBlock(i, j, k))
								return
				
				SchemaUtils.generate(world, x, y, z, iglu3.random(random)!!)
				searchAndGenChests(world, random, x, z, 8, 5)
			}
		}
	}
	
	private fun searchAndGenChests(world: World, random: Random, x: Int, z: Int, xzR: Int, yR: Int) {
		for (i in x.bidiRange(xzR))
			for (k in z.bidiRange(xzR))
				for (j in y..y+yR)
					(world.getTileEntity(i, j, k) as? TileEntityChest)?.let {
						WeightedRandomChestContent.generateChestContents(random, ChestGenHooks.getItems(ChestGenHooks.DUNGEON_CHEST, random), it, ChestGenHooks.getCount(ChestGenHooks.DUNGEON_CHEST, random))
					}
	}
}
