package alfheim.common.block.magtrees.circuit

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alfheim.common.block.base.BlockModRotatedPillar
import alfheim.common.item.block.ItemBlockLeavesMod
import alfheim.port.legacy.*
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.Block

// PORT: поворот (metadata) — свойство axis (BlockModRotatedPillar). КТ-9 — лексикон
class BlockCircuitWood: BlockModRotatedPillar(Material.wood), ICircuitBlock {
	
	init {
		setBlockName("circuitWood")
		blockHardness = 2f
		tickRandomly = true
		
		// PORT: свечение по координатам (getLightValue ниже) 1.20.1 берёт у состояний: пишется в них с создания блока
		for (state in stateDefinition.possibleStates) state.lightEmission = getLightValue()
	}
	
	override fun onBlockAdded(world: World, x: Int, y: Int, z: Int) {
		val below = world.getBlock(x, y - 1, z)
		if (below !is ICircuitBlock) return
		
		// PORT: генератор мира 1.20.1 — random
		below.updateTick(world, x, y - 1, z, world.random)
//		below.updateTick(world, x, y - 1, z, world.rand)
	}
	
	// PORT: бревно, которое держит листву и считается деревом (canSustainLeaves, isWood), — тег minecraft:logs: в нём
	// бревно по Ore Dictionary (logWood, alfheim.port.data.OreDictTags)
//	override fun canSustainLeaves(world: IBlockAccess, x: Int, y: Int, z: Int) = true
//
//	override fun isWood(world: IBlockAccess?, x: Int, y: Int, z: Int) = true
	
	override fun breakBlock(world: World, x: Int, y: Int, z: Int, block: Block, meta: Int) {
		// PORT: листва 1.20.1 сама пересчитывает расстояние до бревна, когда его убрали (updateShape); листва автора
		// опадает и без этого сигнала (beginLeavesDecay у неё пустой)
		/*
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
		*/
		
		onBlockAdded(world, x, y, z)
	}
	
	// PORT: лут (alfheim.port.data.AlfheimBlockLoot) — сам блок, один
//	override fun damageDropped(meta: Int) = 0
//
//	override fun quantityDropped(random: Random) = 1
	
	override fun register(name: String) {
		GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
	}
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.circuitSapling
	
	// ####
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, random: Random) {
		world.notifyBlocksOfNeighborChange(x, y, z, this)
		onBlockAdded(world, x, y, z)
	}
	
	// PORT: свечение по координатам — постоянное, пишется в состояния (init)
	fun getLightValue() = 8
//	override fun getLightValue(world: IBlockAccess?, x: Int, y: Int, z: Int) = 8
	
	override fun canProvidePower() = true
	
	// PORT: запланированных тиков этому блоку никто не ставит — частоту (tickRate) 1.7.10 не спрашивал
//	override fun tickRate(world: World) = 1
	
	override fun isProvidingWeakPower(blockAccess: IBlockAccess, x: Int, y: Int, z: Int, meta: Int) = ICircuitBlock.getPower(blockAccess, x, y, z)
	
	// PORT: грань полного куба 1.20.1 твёрдая и так (isFaceSturdy — по форме блока); в 1.7.10 источник сигнала не был
	// нормальным кубом, и автор возвращал ей твёрдость сам
//	override fun isSideSolid(world: IBlockAccess?, x: Int, y: Int, z: Int, side: ForgeDirection?) = true
}