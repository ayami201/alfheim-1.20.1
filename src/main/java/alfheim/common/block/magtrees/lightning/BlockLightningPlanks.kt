package alfheim.common.block.magtrees.lightning

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alfheim.common.block.base.BlockMod
import alfheim.common.item.block.ItemBlockLeavesMod
import alfheim.port.legacy.*
import net.minecraft.world.level.block.Block

// PORT: КТ-9 — лексикон (ILexiconable)
class BlockLightningPlanks: BlockMod(Material.wood)/*, ILexiconable*/ {
	
	private val name = "lightningPlanks"
	
	init {
		blockHardness = 2F
		setLightLevel(0f)
		stepSound = soundTypeWood
		
		setBlockName(name)
	}
	
	override fun isInterpolated() = true
	
	override fun isToolEffective(type: String?, metadata: Int) = (type != null && type == "axe")
	
	override fun getHarvestTool(metadata: Int) = "axe"
	
	override fun shouldRegisterInNameSet() = false
	
	// PORT: лут — сам блок (alfheim.port.data.AlfheimBlockLoot)
//	override fun damageDropped(par1: Int) = par1
	
	override fun setBlockName(name: String): Block {
		register(name)
		return super.setBlockName(name)
	}
	
	// PORT: лут — сам блок, один (alfheim.port.data.AlfheimBlockLoot)
//	override fun quantityDropped(random: Random) = 1
//
//	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = this.toItem()
	
	internal fun register(name: String) {
		GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
	}
	
	/* PORT: выбор колёсиком — предмет блока (getCloneItemStack 1.20.1)
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int, player: EntityPlayer): ItemStack {
		val meta = world.getBlockMetadata(x, y, z)
		return ItemStack(this, 1, meta)
	}
	*/
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.lightningSapling
}
