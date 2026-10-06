package alfheim.common.block.magtrees.circuit

// PORT: импорты 1.20.1 (BlockSlab 1.7.10 — Slab1710, MAPPING.md)
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.colored.rainbow.*
import alfheim.common.item.block.*
import alfheim.port.legacy.*
import alfheim.port.registry.LegacyRegistration
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SlabBlock as BlockSlab

// PORT: двойная плита — состояние type=double одинарной (BlockSlabMod)
class BlockCircuitWoodSlab(full: Boolean, source: Block = AlfheimBlocks.circuitPlanks): BlockRainbowWoodSlab(full, source), ICircuitBlock {
	
	// PORT: свечение по координатам (getLightValue ниже) 1.20.1 берёт у состояний: пишется в них с создания блока
	init {
		for (state in stateDefinition.possibleStates) state.lightEmission = getLightValue()
	}
	
	override fun onBlockAdded(world: World, x: Int, y: Int, z: Int) {
		val below = world.getBlock(x, y - 1, z)
		if (below !is ICircuitBlock) return
		
		// PORT: генератор мира 1.20.1 — random
		below.updateTick(world, x, y - 1, z, world.random)
//		below.updateTick(world, x, y - 1, z, world.rand)
	}
	
	override fun breakBlock(world: World, x: Int, y: Int, z: Int, block: Block?, meta: Int) {
		onBlockAdded(world, x, y, z)
	}
	
	override fun getFullBlock() = AlfheimBlocks.circuitSlabFull as BlockSlab
	
	override fun register() {
		GameRegistry.registerBlock(this, ItemSlabMod::class.java, name)
		// PORT: двойная плита 1.7.10 — состояние type=double этой плиты (BlockSlabMod); её старое имя — circuitPlanksSlabFull
		LegacyRegistration.alias(name.replaceFirst("Slab", "SlabFull"), this, "type=double")
	}
	
	override fun getSingleBlock() = AlfheimBlocks.circuitSlabs as BlockSlab
	
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
}

class BlockCircuitWoodStairs(source: Block = AlfheimBlocks.circuitPlanks): BlockRainbowWoodStairs(source), ICircuitBlock {
	
	// PORT: свечение по координатам (getLightValue ниже) 1.20.1 берёт у состояний: пишется в них с создания блока
	init {
		for (state in stateDefinition.possibleStates) state.lightEmission = getLightValue()
	}
	
	override fun onBlockAdded(world: World, x: Int, y: Int, z: Int) {
		val below = world.getBlock(x, y - 1, z)
		if (below !is ICircuitBlock) return
		
		// PORT: генератор мира 1.20.1 — random
		below.updateTick(world, x, y - 1, z, world.random)
//		below.updateTick(world, x, y - 1, z, world.rand)
	}
	
	override fun breakBlock(world: World, x: Int, y: Int, z: Int, block: Block?, meta: Int) {
		onBlockAdded(world, x, y, z)
	}
	
	override fun register() {
		GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
	}
	
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
}