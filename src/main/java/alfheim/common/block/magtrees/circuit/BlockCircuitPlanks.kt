package alfheim.common.block.magtrees.circuit

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alfheim.common.block.base.BlockMod
import alfheim.port.legacy.*
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.Block

// PORT: КТ-9 — лексикон (ILexiconable)
class BlockCircuitPlanks: BlockMod(Material.wood), ICircuitBlock/*, ILexiconable*/ {
	
	private val name = "circuitPlanks"
	
	init {
		blockHardness = 2F
		
		stepSound = soundTypeWood
		setBlockName(name)
		
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
	
	override fun breakBlock(world: World, x: Int, y: Int, z: Int, block: Block?, meta: Int) {
		onBlockAdded(world, x, y, z)
	}
	
	override fun isToolEffective(type: String?, metadata: Int) = (type != null && type == "axe")
	
	override fun getHarvestTool(metadata: Int) = "axe"
	
	/* PORT: выбор колёсиком — предмет блока (getCloneItemStack 1.20.1)
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int, player: EntityPlayer): ItemStack {
		val meta = world.getBlockMetadata(x, y, z)
		return ItemStack(this, 1, meta)
	}
	*/
	
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