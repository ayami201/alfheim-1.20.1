package alfheim.common.block.magtrees.circuit

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.colored.BlockColoredSapling
import alfheim.common.world.gen.HeartWoodTreeGen
import alfheim.port.legacy.*
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.Block

class BlockCircuitSapling: BlockColoredSapling("circuitSapling"), ICircuitBlock {
	
	// PORT: свечение по координатам (getLightValue ниже) 1.20.1 берёт у состояний: пишется в них с создания блока
	init {
		for (state in stateDefinition.possibleStates) state.lightEmission = getLightValue()
	}
	
	override fun getGenerator(meta: Int) = HeartWoodTreeGen(5, AlfheimBlocks.circuitWood, 0, AlfheimBlocks.circuitWood, 0, AlfheimBlocks.circuitLeaves, 0, AlfheimBlocks.circuitBerry)
	
	override fun canGrowHere(block: Block) =
		block.material == Material.ground || block.material == Material.grass
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.circuitSapling
	
	// ####
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, random: Random) {
		super.updateTick(world, x, y, z, random)
		world.notifyBlocksOfNeighborChange(x, y, z, this)
	}
	
	// PORT: свечение по координатам — постоянное, пишется в состояния (init)
	fun getLightValue() = 8
//	override fun getLightValue(world: IBlockAccess?, x: Int, y: Int, z: Int) = 8
	
	override fun canProvidePower() = true
	
	// PORT: запланированных тиков этому блоку никто не ставит — частоту (tickRate) 1.7.10 не спрашивал
//	override fun tickRate(world: World) = 1
	
	override fun isProvidingWeakPower(blockAccess: IBlockAccess, x: Int, y: Int, z: Int, meta: Int) = ICircuitBlock.getPower(blockAccess, x, y, z)
}