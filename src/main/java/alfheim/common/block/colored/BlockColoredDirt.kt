package alfheim.common.block.colored

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md); цвета шерсти 1.7.10 — Sheep1710
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.base.BlockMod
import alfheim.common.item.block.ItemSubtypedBlockMod
import alfheim.port.legacy.*
import alfheim.port.legacy.Sheep1710 as EntitySheep
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction as ForgeDirection
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.api.distmarker.*
import net.minecraftforge.common.IPlantable
import java.awt.Color

// PORT: вариант metadata (цвет) — отдельный блок (SPEC, Р-5): номер варианта — meta, создают массивом
// `Array(16) { BlockColoredDirt(it) }`. КТ-9 — лексикон (ILexiconable)
class BlockColoredDirt(val meta: Int): BlockMod(Material.ground), IGrowable/*, ILexiconable*/ {
	
	private val name = "coloredDirt"
	private val TYPES = 16
	
	override val variant get() = meta
	
	init {
		setHardness(0.5f)
		setLightLevel(0f)
		setBlockName(name)
		stepSound = soundTypeGravel
	}
	
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
					
					// PORT: this 1.7.10 — цветная земля любого цвета: варианты в порту — разные блоки (isSameBlock1710);
					// isNormalCube — с координатами (World.kt). КТ-2, партия 8б — радужная и авроровая земля
					if ((world.getBlock(i1, j1 - 1, k1).isSameBlock1710(this)/* || world.getBlock(i1, j1 - 1, k1) == AlfheimBlocks.rainbowDirt || world.getBlock(i1, j1 - 1, k1) == AlfheimBlocks.auroraDirt*/) && !world.getBlock(i1, j1, k1).isNormalCube(world, i1, j1, k1)) {
//					if ((world.getBlock(i1, j1 - 1, k1) == this || world.getBlock(i1, j1 - 1, k1) == AlfheimBlocks.rainbowDirt || world.getBlock(i1, j1 - 1, k1) == AlfheimBlocks.auroraDirt) && !world.getBlock(i1, j1, k1).isNormalCube) {
						++l1
						continue
					}
				} else if (world.getBlock(i1, j1, k1).isAir(world, i1, j1, k1)) {
					if (random.nextInt(8) != 0) {
						// PORT: трава — массив вариантов по цвету (SPEC, Р-5); цвет — номер варианта земли под ней
						val meta = world.getBlockVariant(i1, j1 - 1, k1)
						if (AlfheimBlocks.irisGrass[meta].canBlockStay(world, i1, j1, k1)) {
//						if (AlfheimBlocks.irisGrass.canBlockStay(world, i1, j1, k1)) {
//							val meta = world.getBlockMetadata(i1, j1 - 1, k1)
							world.setBlock(i1, j1, k1, AlfheimBlocks.irisGrass[meta].defaultBlockState(), 3)
//							world.setBlock(i1, j1, k1, AlfheimBlocks.irisGrass, meta, 3)
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
	
	@OnlyIn(Dist.CLIENT)
	override fun getBlockColor() = 0xFFFFFF
	
	/**
	 * Returns the color this block should be rendered. Used by leaves.
	 */
	@OnlyIn(Dist.CLIENT)
	override fun getRenderColor(meta: Int): Int {
		val color = EntitySheep.fleeceColorTable.getOrNull(meta) ?: FloatArray(3) {1f}
		return Color(color[0], color[1], color[2]).rgb
	}
	
	@OnlyIn(Dist.CLIENT)
	override fun colorMultiplier(world: IBlockAccess, x: Int, y: Int, z: Int): Int {
		// PORT: metadata — номер варианта блока (SPEC, Р-5)
		val meta = this.meta
//		val meta = world.getBlockMetadata(x, y, z)
		return getRenderColor(meta)
	}
	
	override fun shouldRegisterInNameSet() = false
	
	// PORT: лут — сам блок (alfheim.port.data.AlfheimBlockLoot): вариант — сам блок
//	override fun damageDropped(par1: Int) = par1
	
	override fun setBlockName(name: String): Block {
		register(name)
		return super.setBlockName(name)
	}
	
	internal fun register(name: String) {
		GameRegistry.registerBlock(this, ItemSubtypedBlockMod::class.java, name)
	}
	
	/* PORT: выбор колёсиком — предмет блока (getCloneItemStack 1.20.1); варианты во вкладке — отдельные блоки (AlfheimTab)
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int, player: EntityPlayer): ItemStack {
		val meta = world.getBlockMetadata(x, y, z)
		return ItemStack(this, 1, meta)
	}
	
	override fun getSubBlocks(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>?) {
		if (list != null && item != null)
			for (i in 0 until TYPES) {
				list.add(ItemStack(item, 1, i))
			}
	}
	*/
	
	// PORT: в 1.20.1 почву спрашивают по состоянию и BlockPos
	override fun canSustainPlant(state: BlockState, world: IBlockAccess, pos: BlockPos, direction: ForgeDirection, plantable: IPlantable) = true
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.coloredDirt
}
