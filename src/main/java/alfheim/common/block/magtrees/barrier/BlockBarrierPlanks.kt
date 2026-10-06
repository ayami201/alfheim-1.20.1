package alfheim.common.block.magtrees.barrier

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alfheim.common.block.base.BlockMod
import alfheim.port.legacy.*

// PORT: КТ-9 — лексикон (ILexiconable)
class BlockBarrierPlanks: BlockMod(Material.wood)/*, ILexiconable*/ {
	
	private val name = "barrierPlanks"
	
	init {
		blockHardness = 2F
		
		stepSound = soundTypeWood
		setBlockName(name)
		
		// PORT-FIX: случайный тик досок ничего не делал (updateTick у них нет) — лишняя работа сервера каждый тик
//		tickRandomly = true
	}
	
	override fun isInterpolated() = true
	
	override fun isToolEffective(type: String?, metadata: Int) = (type != null && type == "axe")
	
	override fun getHarvestTool(metadata: Int) = "axe"
	
	/* PORT: выбор колёсиком — предмет блока (getCloneItemStack 1.20.1)
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int, player: EntityPlayer): ItemStack {
		val meta = world.getBlockMetadata(x, y, z)
		return ItemStack(this, 1, meta)
	}
	*/
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.barrierSapling
}