package alfheim.common.world.gen

// PORT: импорты 1.20.1; WorldGenAbstractTree 1.7.10 — alfheim.port.legacy.WorldGenAbstractTree (MAPPING.md, «Растения»)
import alfheim.api.AlfheimAPI
import alfheim.port.legacy.*
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.Block
import kotlin.math.abs

// PORT: metadata почвы — номер её варианта (getBlockVariant, SPEC, Р-5); высота мира 1.7.10 (0–255) — границы мира
// 1.20.1 (minBuildHeight, maxBuildHeight)
class SimpleTreeGen(val minTreeHeight: Int): WorldGenAbstractTree(true) {
	
	override fun generate(world: World?, random: Random?, x: Int, y: Int, z: Int): Boolean {
		if (world != null) {
			val l: Int = random!!.nextInt(3) + minTreeHeight
			var flag = true
			
			val variant = AlfheimAPI.getTreeVariant(world.getBlock(x, y - 1, z), world.getBlockVariant(x, y - 1, z))
			if (variant != null) {
				val wood = variant.getWood(world.getBlock(x, y - 1, z), world.getBlockVariant(x, y - 1, z))
				val leaves = variant.getLeaves(world.getBlock(x, y - 1, z), world.getBlockVariant(x, y - 1, z))
				
				if (y >= world.minBuildHeight + 1 && y + l + 1 <= world.maxBuildHeight) {
//				if (y >= 1 && y + l + 1 <= 256) {
					var b0: Byte
					var block: Block
					
					isGen@ for (i1 in y..(y + 1 + l)) {
						b0 = 1
						
						if (i1 == y) b0 = 0
						if (i1 >= (y + 1 + l - 2)) b0 = 2
						
						
						for (j1 in (x - b0)..(x + b0)) {
							for (i2 in (z - b0)..(z + b0)) {
								if (i1 in world.minBuildHeight until world.maxBuildHeight) {
//								if (i1 in 0..255) {
									block = world.getBlock(j1, i1, i2)
									
									// PORT: wood 1.7.10 — блок со всеми вариантами metadata (isSameBlock1710)
									if (!block.isReplaceable(world, j1, i1, i2) && !block.isLeaves(world, j1, i1, i2) && !block.isSameBlock1710(wood)) {
//									if (!block.isReplaceable(world, j1, i1, i2) && !block.isLeaves(world, j1, i1, i2) && block != wood) {
										flag = false
										break@isGen
									}
								} else {
									flag = false
									break@isGen
								}
							}
						}
					}
					
					if (!flag) return false
					val block2: Block = world.getBlock(x, y - 1, z)
					val soilMeta = world.getBlockVariant(x, y - 1, z)
					
					if (y < world.maxBuildHeight - l - 1) {
//					if (y < 256 - l - 1) {
						block2.onPlantGrow(world, x, y - 1, z, x, y, z)
						b0 = 3
						val b1: Byte = 0
						var l1: Int
						var j2: Int
						var i3: Int
						
						for (k1 in y - b0 + l..(y + l)) {
							i3 = k1 - (y + l)
							l1 = b1 + 1 - i3 / 2
							
							for (i2 in (x - l1)..(x + l1)) {
								j2 = i2 - x
								
								for (k2 in z - l1..z + l1) {
									val l2: Int = k2 - z
									
									if (abs(j2) != l1 || abs(l2) != l1 || random.nextInt(2) != 0 && i3 != 0) {
										val block1: Block = world.getBlock(i2, k1, k2)
										
										if (block1.isAir(world, i2, k1, k2) || block1.isLeaves(world, i2, k1, k2)) {
											setBlockAndNotifyAdequately(world, i2, k1, k2, leaves, variant.getMeta(block2, soilMeta, leaves))
										}
									}
								}
							}
						}
						
						for (k1 in 0 until l) {
							block = world.getBlock(x, y + k1, z)
							
							if (block.isAir(world, x, y + k1, z) || block.isLeaves(world, x, y + k1, z)) {
								setBlockAndNotifyAdequately(world, x, y + k1, z, wood, variant.getMeta(block2, soilMeta, wood))
							}
						}
						return true
					}
					return false
				}
			}
		}
		return false
	}
}
