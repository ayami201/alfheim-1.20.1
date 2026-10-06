package alfheim.common.block.magtrees.nether

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alexsocol.asjlib.toItem
import alfheim.common.block.base.BlockModRotatedPillar
import alfheim.common.item.block.ItemBlockLeavesMod
import alfheim.port.legacy.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level as World

// PORT: вариант metadata (meta and 3: 0 — бревно, 1 — сердцевина) — отдельный блок (SPEC, Р-5): номер варианта — meta,
// создают массивом `Array(2) { BlockNetherWood(it) }`; поворот (meta and 12) — свойство axis (BlockModRotatedPillar).
// КТ-2 (партия 8в-3) — сердцевина с TileTreeCook (ITileEntityProvider)
class BlockNetherWood(val meta: Int): BlockModRotatedPillar(Material.wood)/*, ITileEntityProvider*/, IFuelHandler {
	
	override val variant get() = meta
	
	init {
		blockHardness = 2f
		setBlockName("netherWood")
		setLightLevel(0.5f)
		
		GameRegistry.registerFuelHandler(this)
	}
	
	override fun isInterpolated() = true
	
	// PORT: бревно, которое держит листву и считается деревом (canSustainLeaves, isWood), — тег minecraft:logs: в нём
	// бревно по Ore Dictionary (logWood, alfheim.port.data.OreDictTags)
//	override fun canSustainLeaves(world: IBlockAccess, x: Int, y: Int, z: Int) = true
//
//	override fun isWood(world: IBlockAccess?, x: Int, y: Int, z: Int) = true
	
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
	}
	*/
	
	override fun isFireSource(world: World?, x: Int, y: Int, z: Int, side: ForgeDirection?) = true
	
	// PORT: горит ли блок, 1.20.1 решает по таблице огня (FireBlock), её заполняет registerBurnables (AlfheimBlocks):
	// адских блоков в ней нет — они не горят и без этих методов
//	override fun isFlammable(world: IBlockAccess?, x: Int, y: Int, z: Int, face: ForgeDirection?) = false
//
//	override fun getFireSpreadSpeed(world: IBlockAccess?, x: Int, y: Int, z: Int, face: ForgeDirection?) = 0
	
	// PORT: лут — таблица (alfheim.port.data.AlfheimBlockLoot): один блок варианта damageDropped — сердцевина роняет
	// обычное бревно
	fun damageDropped(meta: Int) = 0
	
//	override fun quantityDropped(random: Random) = 1
	
	override fun register(name: String) {
		GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
	}
	
	fun isHeartWood(meta: Int) = meta and 3 == 1
	
	/* PORT: КТ-2 (партия 8в-3) — сердцевина с TileTreeCook
	override fun hasTileEntity(metadata: Int) = isHeartWood(metadata)
	
	override fun createNewTileEntity(world: World?, meta: Int) = TileTreeCook()
	*/
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.netherSapling
	
	// PORT: печь 1.7.10 сжигала деревянный блок 300 тиков раньше обработчиков модов, поэтому 2000 не срабатывало
	// (Fuel1710; BUGS.md, B-027)
	override fun getBurnTime(fuel: ItemStack) = if (fuel.item === this.toItem()) 2000 else 0
}
