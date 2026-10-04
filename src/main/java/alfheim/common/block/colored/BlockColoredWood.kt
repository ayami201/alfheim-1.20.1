package alfheim.common.block.colored

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md); цвета шерсти 1.7.10 — Sheep1710
import alfheim.common.block.base.BlockModRotatedPillar
import alfheim.common.item.block.ItemIridescentWoodMod
import alfheim.port.legacy.*
import alfheim.port.legacy.Sheep1710 as EntitySheep
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraftforge.api.distmarker.*
import java.awt.Color

// PORT: вариант metadata (цвет, meta and 3) — отдельный блок (SPEC, Р-5): номер варианта — meta, создают массивом
// `Array(4) { BlockColoredWood(colorSet, it) }`; поворот (meta and 12) — свойство axis (BlockModRotatedPillar)
class BlockColoredWood(val colorSet: Int, val meta: Int): BlockModRotatedPillar(Material.wood) {
	
	private val name = "irisWood$colorSet"
	
	override val variant get() = meta
	
	init {
		blockHardness = 2F
		setLightLevel(0f)
		stepSound = soundTypeWood
		
		setBlockName(name)
	}
	
	// PORT: листва 1.20.1 сама пересчитывает расстояние до бревна, когда его убрали (updateShape); листва автора опадает
	// и без этого сигнала (beginLeavesDecay у неё пустой)
	/*
	override fun breakBlock(world: World, x: Int, y: Int, z: Int, block: Block, fortune: Int) {
		val b0: Byte = 4
		val i1: Int = b0 + 1
		
		if (world.checkChunksExist(x - i1, y - i1, z - i1, x + i1, y + i1, z + i1)) {
			for (j1 in -b0..b0) for (k1 in -b0..b0)
				for (l1 in -b0..b0) {
					val blockInWorld: Block = world.getBlock(x + j1, y + k1, z + l1)
					if (blockInWorld.isLeaves(world, x + j1, y + k1, z + l1)) {
						blockInWorld.beginLeavesDecay(world, x + j1, y + k1, z + l1)
					}
				}
		}
	}
	*/
	
	override fun register(name: String) {
		GameRegistry.registerBlock(this, ItemIridescentWoodMod::class.java, name)
	}
	
	@OnlyIn(Dist.CLIENT)
	override fun getBlockColor() = 0xFFFFFF
	
	@OnlyIn(Dist.CLIENT)
	fun colorMeta(meta: Int) = meta + (colorSet * 4)
	
	/**
	 * Returns the color this block should be rendered. Used by leaves.
	 */
	@OnlyIn(Dist.CLIENT)
	override fun getRenderColor(meta: Int): Int {
		if (colorMeta(meta and 3) >= EntitySheep.fleeceColorTable.size)
			return 0xFFFFFF
		
		val color = EntitySheep.fleeceColorTable[colorMeta(meta and 3)]
		return Color(color[0], color[1], color[2]).rgb
	}
	
	@OnlyIn(Dist.CLIENT)
	override fun colorMultiplier(world: IBlockAccess?, x: Int, y: Int, z: Int): Int {
		// PORT: metadata — номер варианта блока (SPEC, Р-5)
		val meta = this.meta
//		val meta = world!!.getBlockMetadata(x, y, z)
		return getRenderColor(meta)
	}
	
	// PORT: бревно, которое держит листву и считается деревом (canSustainLeaves, isWood), — тег minecraft:logs: в нём
	// цветные брёвна по Ore Dictionary (logWood, alfheim.port.data.OreDictTags)
//	override fun canSustainLeaves(world: IBlockAccess, x: Int, y: Int, z: Int) = true
//
//	override fun isWood(world: IBlockAccess, x: Int, y: Int, z: Int) = true
	
	// PORT: варианты во вкладке — отдельные блоки (AlfheimTab)
	/*
	override fun getSubBlocks(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>?) {
		if (list != null && item != null) {
			list.add(ItemStack(this, 1, 0))
			list.add(ItemStack(this, 1, 1))
			list.add(ItemStack(this, 1, 2))
			list.add(ItemStack(this, 1, 3))
		}
	}
	*/
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) =
//		AlfheimLexiconData.irisSapling
}
