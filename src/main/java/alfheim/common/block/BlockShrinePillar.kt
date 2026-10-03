package alfheim.common.block

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alfheim.common.block.base.BlockModRotatedPillar
import alfheim.common.item.block.ItemBlockLeavesMod
import alfheim.port.legacy.*

class BlockShrinePillar: BlockModRotatedPillar(Material.rock) {
	
	init {
		setBlockName("ShrinePillar")
		setHardness(10f)
		setHarvestLevel("pickaxe", 2)
		setResistance(10000f)
		setStepSound(soundTypeStone)
	}
	
	override fun register(name: String) {
		GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
	}
	
	// PORT: иконки → модель столба (alfheim.port.data.AlfheimBlockStates): торцы — decor/ShrinePillarTop, бока — decor/ShrinePillar
	/*
	override fun registerBlockIcons(reg: IIconRegister) {
		iconTop = reg.registerIcon(ModInfo.MODID + ":decor/ShrinePillarTop")
		iconSide = reg.registerIcon(ModInfo.MODID + ":decor/ShrinePillar")
	}
	*/
	
	/* PORT: КТ-9 — лексикон
	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, stack: ItemStack?) = null
	*/
}
