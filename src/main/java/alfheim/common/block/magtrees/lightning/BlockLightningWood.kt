package alfheim.common.block.magtrees.lightning

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alfheim.common.block.base.BlockModRotatedPillar
import alfheim.common.block.tile.TileLightningTreeTop
import alfheim.common.item.block.ItemBlockLeavesMod
import alfheim.port.legacy.*
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.state.BlockState

// PORT: вариант metadata (meta and 3: 0 — бревно, 1 — сердцевина) — отдельный блок (SPEC, Р-5): номер варианта — meta,
// создают массивом `Array(2) { BlockLightningWood(it) }`; поворот (meta and 12) — свойство axis (BlockModRotatedPillar)
class BlockLightningWood(val meta: Int): BlockModRotatedPillar(Material.wood), ITileEntityProvider {
	
	override val variant get() = meta
	
	init {
		setBlockName("lightningWood")
		// PORT: блок с блок-сущностью 1.20.1 — EntityBlock (ITileEntityProvider прослойки); флага isBlockContainer нет
//		isBlockContainer = true
		blockHardness = 2f
	}
	
	override fun isInterpolated(): Boolean = true
	
	// PORT: бревно, которое держит листву и считается деревом (canSustainLeaves, isWood), — тег minecraft:logs: в нём
	// бревно по Ore Dictionary (logWood, alfheim.port.data.OreDictTags)
//	override fun canSustainLeaves(world: IBlockAccess, x: Int, y: Int, z: Int): Boolean = true
//
//	override fun isWood(world: IBlockAccess?, x: Int, y: Int, z: Int): Boolean = true
	
	// PORT: листва 1.20.1 сама пересчитывает расстояние до бревна, когда его убрали (updateShape); листва автора опадает
	// и без этого сигнала (beginLeavesDecay у неё пустой). BlockEntity сердцевины 1.20.1 убирает сама (onRemove)
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
		world.removeTileEntity(x, y, z)
	}
	*/
	
	// PORT: событие блока (onBlockEventReceived 1.7.10) — triggerEvent 1.20.1; блок-сущности оно приходит её triggerEvent
	// (receiveClientEvent 1.7.10, alfheim.port.legacy.TileEntity)
	@Deprecated("Deprecated in Java")
	override fun triggerEvent(state: BlockState, world: World, pos: BlockPos, event: Int, eventArg: Int): Boolean {
		@Suppress("DEPRECATION")
		super.triggerEvent(state, world, pos, event, eventArg)
		val tileentity = world.getBlockEntity(pos)
		return tileentity?.triggerEvent(event, eventArg) ?: false
	}
//	override fun onBlockEventReceived(world: World?, x: Int, y: Int, z: Int, event: Int, eventArg: Int): Boolean {
//		super.onBlockEventReceived(world, x, y, z, event, eventArg)
//		val tileentity = world!!.getTileEntity(x, y, z)
//		return tileentity?.receiveClientEvent(event, eventArg) ?: false
//	}
	
	// PORT: блок-сущность 1.20.1 создаётся сразу в своей точке (ITileEntityProvider прослойки); есть ли она у блока
	// (hasTileEntity по metadata 1.7.10, ниже) — по варианту: сердцевина — вариант 1
	override fun newBlockEntity(pos: BlockPos, state: BlockState) = if (isHeartWood(meta)) TileLightningTreeTop(pos, state) else null
//	override fun createNewTileEntity(world: World?, meta: Int) = TileLightningTreeTop()
	
	fun isHeartWood(meta: Int) = meta and 3 == 1
	
	// PORT: лут — таблица (alfheim.port.data.AlfheimBlockLoot): один блок варианта damageDropped — сердцевина роняет
	// обычное бревно
	fun damageDropped(meta: Int) = 0
	
//	override fun quantityDropped(random: Random) = 1
	
	// PORT: есть ли блок-сущность — по варианту блока (newBlockEntity выше)
//	override fun hasTileEntity(metadata: Int) = isHeartWood(metadata)
	
	override fun register(name: String) {
		GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
	}
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) =
//		AlfheimLexiconData.lightningSapling
}
