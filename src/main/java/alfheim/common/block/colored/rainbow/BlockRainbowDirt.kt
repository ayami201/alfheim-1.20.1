package alfheim.common.block.colored.rainbow

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.base.BlockMod
import alfheim.common.item.block.ItemIridescentBlockMod
import alfheim.port.legacy.*
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction as ForgeDirection
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.IPlantable

// PORT: КТ-9 — лексикон (ILexiconable)
class BlockRainbowDirt: BlockMod(Material.ground), IGrowable/*, ILexiconable*/ {
	
	private val name = "rainbowDirt"
	
	init {
		blockHardness = 0.5F
		setLightLevel(0f)
		stepSound = soundTypeGravel
		setBlockName(name)
	}
	
	// PORT: в 1.20.1 плодородие почвы спрашивают по состоянию и BlockPos
	override fun isFertile(state: BlockState, world: IBlockAccess, pos: BlockPos) = true
//	override fun isFertile(world: World?, x: Int, y: Int, z: Int) = true
	
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
					if ((world.getBlock(i1, j1 - 1, k1) == this || world.getBlock(i1, j1 - 1, k1) in AlfheimBlocks.irisDirt || world.getBlock(i1, j1 - 1, k1) == AlfheimBlocks.auroraDirt) && !world.getBlock(i1, j1, k1).isNormalCube(world, i1, j1, k1)) {
//					if ((world.getBlock(i1, j1 - 1, k1) == this || world.getBlock(i1, j1 - 1, k1) == AlfheimBlocks.irisDirt || world.getBlock(i1, j1 - 1, k1) == AlfheimBlocks.auroraDirt) && !world.getBlock(i1, j1, k1).isNormalCube) {
						++l1
						continue
					}
				} else if (world.getBlock(i1, j1, k1).isAir(world, i1, j1, k1)) {
					if (random.nextInt(8) != 0) {
						// PORT: радужная трава — массив вариантов (SPEC, Р-5)
						if (AlfheimBlocks.rainbowGrass[0].canBlockStay(world, i1, j1, k1)) {
//						if (AlfheimBlocks.rainbowGrass.canBlockStay(world, i1, j1, k1)) {
							world.setBlock(i1, j1, k1, AlfheimBlocks.rainbowGrass[0].defaultBlockState(), 3)
//							world.setBlock(i1, j1, k1, AlfheimBlocks.rainbowGrass, 0, 3)
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
	
	override fun isToolEffective(type: String?, metadata: Int) = (type != null && type == "shovel")
	
	override fun getHarvestTool(metadata: Int) = "shovel"
	
	override fun shouldRegisterInNameSet() = false
	
	// PORT: лут — сам блок (alfheim.port.data.AlfheimBlockLoot)
//	override fun damageDropped(par1: Int) = par1
	
	override fun setBlockName(name: String): Block {
		register(name)
		return super.setBlockName(name)
	}
	
	internal fun register(name: String) {
		GameRegistry.registerBlock(this, ItemIridescentBlockMod::class.java, name)
	}
	
	override fun isInterpolated() = true
	
	// PORT: в 1.20.1 почву спрашивают по состоянию и BlockPos
	override fun canSustainPlant(state: BlockState, world: IBlockAccess, pos: BlockPos, direction: ForgeDirection, plantable: IPlantable) = true
//	override fun canSustainPlant(world: IBlockAccess?, x: Int, y: Int, z: Int, direction: ForgeDirection?, plantable: IPlantable?) = true
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.coloredDirt
}
