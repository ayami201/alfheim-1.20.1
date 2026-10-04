package alfheim.common.block.colored

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md); ChunkCoordinates 1.7.10 — BlockPos
import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.base.BlockMod
import alfheim.common.item.block.ItemBlockAurora
import alfheim.port.legacy.*
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction as ForgeDirection
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.api.distmarker.*
import net.minecraftforge.common.IPlantable
import kotlin.math.roundToInt

// PORT: КТ-9 — лексикон (ILexiconable)
class BlockAuroraDirt: BlockMod(Material.ground), IGrowable/*, ILexiconable*/ {
	
	companion object {
		private fun fromVec(vec: ChunkCoordinates): Int = (((vec.posX + 256) % 256) shl 16) or (((vec.posY + 256) % 256) shl 8) or ((vec.posZ + 256) % 256)
		
		private fun fromPos(x: Double, y: Double, z: Double) = fromVec(trilinearInterp(x, y, z))
		
		private fun fromPos(x: Int, y: Int, z: Int): ChunkCoordinates {
			var red = x * 32 + y * 16
			if (red and 256 != 0) {
				red = 255 - (red and 255)
			}
			red = red and 255
			
			var blue = y * 32 + z * 16
			if (blue and 256 != 0) {
				blue = 255 - (blue and 255)
			}
			blue = (blue xor 255) % 256
			
			var green = x * 16 + z * 32
			if (green and 256 != 0) {
				green = 255 - (green and 255)
			}
			green = green and 255
			
			return ChunkCoordinates(red, blue, green)
		}
		
		private fun trilinearInterp(x: Double, y: Double, z: Double): ChunkCoordinates {
			val x0 = (x - 0.5).roundToInt()
			val y0 = (y - 0.5).roundToInt()
			val z0 = (z - 0.5).roundToInt()
			val x1 = (x + 0.5).roundToInt()
			val y1 = (y + 0.5).roundToInt()
			val z1 = (z + 0.5).roundToInt()
			
			val xd = x1 - x
			val xl = x1 - x0
			val yd = y1 - y
			val yl = y1 - y0
			val zd = z1 - z
			val zl = z1 - z0
			
			val c000 = Vector3(fromPos(x0, y0, z0))
			val c001 = Vector3(fromPos(x0, y0, z1))
			val c010 = Vector3(fromPos(x0, y1, z0))
			val c011 = Vector3(fromPos(x0, y1, z1))
			val c100 = Vector3(fromPos(x1, y0, z0))
			val c101 = Vector3(fromPos(x1, y0, z1))
			val c110 = Vector3(fromPos(x1, y1, z0))
			val c111 = Vector3(fromPos(x1, y1, z1))
			
			val c00 = c000.mul(xd).add(c100.mul(xl - xd))
			val c01 = c001.mul(xd).add(c101.mul(xl - xd))
			val c10 = c010.mul(xd).add(c110.mul(xl - xd))
			val c11 = c011.mul(xd).add(c111.mul(xl - xd))
			
			val c0 = c00.mul(yd).add(c10.mul(yl - yd))
			val c1 = c01.mul(yd).add(c11.mul(yl - yd))
			
			val c = c0.mul(zd).add(c1.mul(zl - zd))
			
			return ChunkCoordinates(c.x.mfloor(), c.y.mfloor(), c.z.mfloor())
		}
		
		fun getBlockColor(x: Int, y: Int, z: Int) = fromVec(fromPos(x, y, z))
		
		// PORT-FIX: игрок клиента есть только на клиенте; без пометки выделенный сервер падал бы при загрузке класса
		@OnlyIn(Dist.CLIENT)
		fun getItemColor() = fromPos(mc.player!!.posX, mc.player!!.posY, mc.player!!.posZ)
//		fun getItemColor() = fromPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)
	}
	
	init {
		stepSound = soundTypeGravel
		blockHardness = 0.5f
		
		setBlockName("auroraDirt")
	}

//	override fun addInformation(stack: ItemStack, player: World?, tooltip: MutableList<String>, advanced: ITooltipFlag) {
//		addToTooltip(tooltip, "misc.${LibMisc.MOD_ID}.color.aurora")
//	}
	
	// PORT: в 1.20.1 плодородие почвы спрашивают по состоянию и BlockPos
	override fun isFertile(state: BlockState, world: IBlockAccess, pos: BlockPos) = true
//	override fun isFertile(world: World?, x: Int, y: Int, z: Int) = true
	
	override fun shouldRegisterInNameSet() = false
	
	override fun setBlockName(name: String): Block {
		GameRegistry.registerBlock(this, ItemBlockAurora::class.java, name)
		return super.setBlockName(name)
	}
	
	override fun colorMultiplier(world: IBlockAccess, x: Int, y: Int, z: Int) = getBlockColor(x, y, z)
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.aurora
	
	override fun isToolEffective(type: String?, metadata: Int) = (type != null && type == "shovel")
	
	override fun getHarvestTool(metadata: Int) = "shovel"
	
	// PORT: в 1.20.1 почву спрашивают по состоянию и BlockPos
	override fun canSustainPlant(state: BlockState, world: IBlockAccess, pos: BlockPos, direction: ForgeDirection, plantable: IPlantable) = true
//	override fun canSustainPlant(world: IBlockAccess?, x: Int, y: Int, z: Int, direction: ForgeDirection?, plantable: IPlantable?) = true
	
	override fun func_149851_a(world: World, x: Int, y: Int, z: Int, remote: Boolean) = true
	
	override fun func_149852_a(world: World, random: Random, x: Int, y: Int, z: Int) = true
	
	override fun func_149853_b(world: World, random: Random, x: Int, y: Int, z: Int) {
		var l = 0
		
		while (l < 128) {
			var i1 = x
			var j1 = y + 1
			var k1 = z
			var l1 = 0
			
			while (true) {
				if (l1 < l / 16) {
					i1 += random.nextInt(3) - 1
					j1 += (random.nextInt(3) - 1) * random.nextInt(3) / 2
					k1 += random.nextInt(3) - 1
					
					// PORT: цветная земля — массив блоков-вариантов (SPEC, Р-5); isNormalCube — с координатами (World.kt)
					if ((world.getBlock(i1, j1 - 1, k1) == this || world.getBlock(i1, j1 - 1, k1) in AlfheimBlocks.irisDirt || world.getBlock(i1, j1 - 1, k1) == AlfheimBlocks.rainbowDirt) && !world.getBlock(i1, j1, k1).isNormalCube(world, i1, j1, k1)) {
//					if ((world.getBlock(i1, j1 - 1, k1) == this || world.getBlock(i1, j1 - 1, k1) == AlfheimBlocks.irisDirt || world.getBlock(i1, j1 - 1, k1) == AlfheimBlocks.rainbowDirt) && !world.getBlock(i1, j1, k1).isNormalCube) {
						++l1
						continue
					}
				} else if (world.getBlock(i1, j1, k1).isAir(world, i1, j1, k1)) {
					if (random.nextInt(8) != 0) {
						// PORT: радужная трава — массив вариантов (SPEC, Р-5)
						if (AlfheimBlocks.rainbowGrass[1].canBlockStay(world, i1, j1, k1)) {
//						if (AlfheimBlocks.rainbowGrass.canBlockStay(world, i1, j1, k1)) {
							world.setBlock(i1, j1, k1, AlfheimBlocks.rainbowGrass[1].defaultBlockState(), 3)
//							world.setBlock(i1, j1, k1, AlfheimBlocks.rainbowGrass, 1, 3)
						}
					} else {
						// PORT: цветок биома — World.kt (plantFlower)
						world.plantFlower(random, i1, j1, k1)
//						world.getBiomeGenForCoords(i1, k1).plantFlower(world, random, i1, j1, k1)
					}
				}
				
				++l
				break
			}
		}
	}
}