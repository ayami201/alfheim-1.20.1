package alfheim.common.world.gen

import alexsocol.asjlib.ASJUtilities
import net.minecraft.block.Block
import net.minecraft.world.World
import net.minecraft.world.gen.feature.WorldGenAbstractTree
import java.util.*
import kotlin.math.abs

class HeartWoodTreeGen(val minTreeHeight: Int, val regWood: Block, val regMeta: Int, val heartWood: Block, val heartMeta: Int, val leaves: Block, val leavesMeta: Int, val berry: Block): WorldGenAbstractTree(true) {
	
	override fun generate(world: World?, random: Random?, x: Int, y: Int, z: Int): Boolean {
		val l: Int = random!!.nextInt(3) + minTreeHeight
		var flag = true
		
		if (y < 1 || y + l + 1 > 256) return false
		
		var b0: Byte
		var block: Block
		
		isGen@ for (i1 in y..(y + 1 + l)) {
			b0 = 1
			
			if (i1 == y) b0 = 0
			if (i1 >= (y + 1 + l - 2)) b0 = 2
			
			for (j1 in (x - b0)..(x + b0)) {
				for (i2 in (z - b0)..(z + b0)) {
					if (i1 in 0..255) {
						block = world!!.getBlock(j1, i1, i2)
						
						if (block.isReplaceable(world, j1, i1, i2) || block.isLeaves(world, j1, i1, i2) || block == regWood || block == heartWood) continue
						
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
		val block2: Block = world!!.getBlock(x, y - 1, z)
		
		if (y >= 256 - l - 1) return false
		
		block2.onPlantGrow(world, x, y - 1, z, x, y, z)
		
		for (k1 in 0 until l) {
			block = world.getBlock(x, y + k1, z)
			
			if (!block.isAir(world, x, y + k1, z) && !block.isLeaves(world, x, y + k1, z)) continue
			
			if (k1 == l - 1) {
				setBlockAndNotifyAdequately(world, x, y + k1, z, heartWood, heartMeta)
			} else {
				setBlockAndNotifyAdequately(world, x, y + k1, z, regWood, regMeta)
			}
		}
		
		var hasBerry = false
		
		for (k1 in y - 3 + l..(y + l)) {
			val i3 = k1 - (y + l)
			val l1 = 1 - i3 / 2
			
			for (i2 in (x - l1)..(x + l1)) {
				val j2 = i2 - x
				
				for (k2 in z - l1..z + l1) {
					if (i2 == x && k2 == z) continue
					
					val l2: Int = k2 - z
					
					if (abs(j2) == l1 && abs(l2) == l1 && !(random.nextInt(2) != 0 && i3 != 0)) continue
					
					val block1: Block = world.getBlock(i2, k1, k2)
					
					if (!block1.isAir(world, i2, k1, k2) && !block1.isLeaves(world, i2, k1, k2)) continue
					setBlockAndNotifyAdequately(world, i2, k1, k2, leaves, leavesMeta)
					
					if (hasBerry || !world.isAirBlock(i2, k1 - 1, k2) || random.nextInt(50) != 0 || !berry.canBlockStay(world, i2, k1 - 1, k2)) continue
					setBlockAndNotifyAdequately(world, i2, k1 - 1, k2, berry, 0)
					hasBerry = true
				}
			}
		}
		
		return true
	}
}

