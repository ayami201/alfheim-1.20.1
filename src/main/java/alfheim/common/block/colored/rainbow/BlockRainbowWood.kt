package alfheim.common.block.colored.rainbow

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alfheim.common.block.base.BlockModRotatedPillar
import alfheim.common.item.block.ItemIridescentBlockMod
import alfheim.port.legacy.*

// PORT: поворот (metadata) — свойство axis (BlockModRotatedPillar); переливающаяся текстура — .mcmeta. КТ-9 — лексикон
class BlockRainbowWood: BlockModRotatedPillar(Material.wood) {
	
	private val name = "rainbowWood"
	
	init {
		blockHardness = 2F
		setLightLevel(0f)
		stepSound = soundTypeWood
		
		setBlockName(name)
	}
	
	override fun register(name: String) {
		GameRegistry.registerBlock(this, ItemIridescentBlockMod::class.java, name)
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
	
	// PORT: бревно, которое держит листву и считается деревом (canSustainLeaves, isWood), — тег minecraft:logs: в нём
	// радужное бревно по Ore Dictionary (logWood, alfheim.port.data.OreDictTags)
//	override fun canSustainLeaves(world: IBlockAccess, x: Int, y: Int, z: Int) = true
//
//	override fun isWood(world: IBlockAccess, x: Int, y: Int, z: Int) = true
	
	override fun isInterpolated() = true
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.irisSapling
}
