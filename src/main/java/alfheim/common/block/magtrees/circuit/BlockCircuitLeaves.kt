package alfheim.common.block.magtrees.circuit

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alexsocol.asjlib.toItem
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.magtrees.BlockMagicLeaves
import alfheim.port.legacy.*
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

// PORT: КТ-9 — лексикон (ILexiconable)
class BlockCircuitLeaves: BlockMagicLeaves("circuitLeaves"), ICircuitBlock/*, ILexiconable*/ {
	
	// PORT: свечение по координатам (getLightValue ниже) 1.20.1 берёт у состояний: пишется в них с создания блока
	init {
		for (state in stateDefinition.possibleStates) state.lightEmission = getLightValue()
	}
	
	override fun isInterpolated() = true
	
	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = AlfheimBlocks.circuitSapling.toItem()
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.circuitSapling
	
	// ####
	
	override fun onBlockAdded(world: World, x: Int, y: Int, z: Int) {
		val below = world.getBlock(x, y - 1, z)
		if (below !is ICircuitBlock) return
		
		// PORT: генератор мира 1.20.1 — random
		below.updateTick(world, x, y - 1, z, world.random)
//		below.updateTick(world, x, y - 1, z, world.rand)
	}
	
	override fun breakBlock(world: World, x: Int, y: Int, z: Int, block: Block?, meta: Int) {
		super.breakBlock(world, x, y, z, block, meta)
		onBlockAdded(world, x, y, z)
	}
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, random: Random) {
		super.updateTick(world, x, y, z, random)
		world.notifyBlocksOfNeighborChange(x, y, z, this)
		onBlockAdded(world, x, y, z)
	}
	
	// PORT: листва 1.7.10 получала случайные тики всегда, Leaves1710 — только та, что может опасть; у этой листвы тик ещё
	// и обновляет сигнал
	override fun isRandomlyTicking(state: BlockState) = true
	
	// PORT: свечение по координатам — постоянное, пишется в состояния (init)
	fun getLightValue() = 8
//	override fun getLightValue(world: IBlockAccess?, x: Int, y: Int, z: Int) = 8
	
	override fun canProvidePower() = true
	
	// PORT: запланированных тиков этому блоку никто не ставит — частоту (tickRate) 1.7.10 не спрашивал
//	override fun tickRate(world: World) = 1
	
	override fun isProvidingWeakPower(blockAccess: IBlockAccess, x: Int, y: Int, z: Int, meta: Int) = ICircuitBlock.getPower(blockAccess, x, y, z)
	
}