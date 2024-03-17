package alfheim.common.world.gen

import net.minecraft.block.Block
import net.minecraft.world.World
import net.minecraft.world.gen.feature.WorldGenAbstractTree
import java.util.*
import kotlin.math.abs

class HeartWoodTreeGen(val minTreeHeight: Int, val regWood: Block, val regMeta: Int, val heartWood: Block, val heartMeta: Int, val leaves: Block, val leavesMeta: Int, val berry: Block): WorldGenAbstractTree(true) {
	
	override fun generate(world: World, random: Random, x: Int, y: Int, z: Int): Boolean {
		val height = random.nextInt(3) + minTreeHeight
		var flag = true
		
		if (y < 1 || y + height + 1 > 256) return false
		
		var b0: Byte
		
		isGen@ for (j in y..(y + 1 + height)) {
			b0 = 1
			
			if (j == y) b0 = 0
			if (j >= (y + 1 + height - 2)) b0 = 2
			
			for (i in (x - b0)..(x + b0)) {
				for (k in (z - b0)..(z + b0)) {
					if (j in 0..255) {
						val block = world.getBlock(i, j, k)
						
						if (block.isReplaceable(world, i, j, k) || block.isLeaves(world, i, j, k) || block == regWood || block == heartWood) continue
						
						flag = false
						break@isGen
					} else {
						flag = false
						break@isGen
					}
				}
			}
		}
		
		if (!flag) return false
		if (y >= 256 - height - 1) return false
		
		world.getBlock(x, y - 1, z).onPlantGrow(world, x, y - 1, z, x, y, z)
		
		for (j in 0..height) {
			val block = world.getBlock(x, y + j, z)
			
			if (!block.isAir(world, x, y + j, z) && !block.isLeaves(world, x, y + j, z)) continue
			
			when (j) {
				height     -> setBlockAndNotifyAdequately(world, x, y + j, z, leaves, leavesMeta)
				height - 1 -> setBlockAndNotifyAdequately(world, x, y + j, z, heartWood, heartMeta)
				else       -> setBlockAndNotifyAdequately(world, x, y + j, z, regWood, regMeta)
			}
		}
		
		var hasBerry = false
		
		// gen leaves cycles
		for (j in y - 3 + height..(y + height)) {
			val i3 = j - (y + height)
			val l1 = 1 - i3 / 2
			
			for (i in (x - l1)..(x + l1)) {
				val wx = i - x
				
				for (k in z - l1..z + l1) {
					if (i == x && k == z) continue
					
					val wz = k - z
					
					if (abs(wx) == l1 && abs(wz) == l1 && !(random.nextInt(2) != 0 && i3 != 0)) continue
					
					val block = world.getBlock(i, j, k)
					
					if (!block.isAir(world, i, j, k) && !block.isLeaves(world, i, j, k)) continue
					setBlockAndNotifyAdequately(world, i, j, k, leaves, leavesMeta)
					
					if (hasBerry) continue
					
					if (!world.isAirBlock(i, j - 1, k) || random.nextInt(50) != 0 || !berry.canBlockStay(world, i, j - 1, k)) continue
					setBlockAndNotifyAdequately(world, i, j - 1, k, berry, 0)
					
					hasBerry = true
				}
			}
		}
		
		return true
	}
}

