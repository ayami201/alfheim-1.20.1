package alfheim.common.block.magtrees.sealing

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alexsocol.asjlib.*
import alfheim.common.block.base.BlockModRotatedPillar
import alfheim.common.item.block.ItemBlockLeavesMod
import alfheim.port.legacy.*
import net.minecraft.world.level.Level as World

// PORT: поворот (metadata) — свойство axis (BlockModRotatedPillar). КТ-9 — лексикон
class BlockSealingWood: BlockModRotatedPillar(Material.wood), ISoundSilencer {
	
	init {
		setBlockName("sealingWood")
		blockHardness = 2f
		setStepSound(soundTypeCloth)
		if (ASJUtilities.isClient)
			EventHandlerSealingOak.eventForge()
	}
	
	override fun canSilence(world: World, x: Int, y: Int, z: Int, dist: Double) = dist <= 8
	
	override fun getVolumeMultiplier(world: World, x: Int, y: Int, z: Int, dist: Double) = 0.5f
	
	// PORT: бревно, которое держит листву и считается деревом (canSustainLeaves, isWood), — тег minecraft:logs: в нём
	// бревно по Ore Dictionary (logWood, alfheim.port.data.OreDictTags)
//	override fun canSustainLeaves(world: IBlockAccess, x: Int, y: Int, z: Int) = true
//
//	override fun isWood(world: IBlockAccess?, x: Int, y: Int, z: Int) = true
	
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
		super.breakBlock(world, x, y, z, block, fortune)
	}
	*/
	
	// PORT: лут (alfheim.port.data.AlfheimBlockLoot) — сам блок, один
//	override fun damageDropped(meta: Int) = 0
//
//	override fun quantityDropped(random: Random) = 1
	
	override fun register(name: String) {
		GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
	}
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.silencer
}
