package alfheim.common.block

// PORT: импорты 1.7.10 заменены на 1.20.1
import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.api.ModInfo
import alfheim.common.core.util.AlfheimTab
import alfheim.port.legacy.*

// PORT: вариант metadata — отдельный блок (BlockModMeta)
class BlockElvenSandstone(meta: Int): BlockModMeta(Material.rock, 5, ModInfo.MODID, "ElvenSandstone", AlfheimTab, meta = meta) {
	
	// PORT: иконки → модели (alfheim.port.data): грани каждого варианта — как в getIcon ниже
	/*
	lateinit var sides: Array<IIcon>
	lateinit var top: IIcon
	lateinit var bottom: IIcon
	
	/**
	 * Gets the block's texture. Args: side, meta
	 */
	@SideOnly(Side.CLIENT)
	override fun getIcon(side: Int, meta: Int): IIcon {
		if (meta == names.size + 1) return bottom
		if (meta == names.size) return top
		
		val id = if (meta in names.indices) meta else 0
		
		return when (side) {
			0       -> if (meta in 1..2) top else bottom
			1       -> top
			in 2..5 -> sides[id]
			else    -> top
		}
	}
	
	@SideOnly(Side.CLIENT)
	override fun getSubBlocks(item: Item, tab: CreativeTabs?, list: MutableList<Any?>) {
		(0 until subtypes).mapTo(list) { ItemStack(item, 1, it) }
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(reg: IIconRegister) {
		sides = Array(names.size) {
			reg.registerIcon("${ModInfo.MODID}:decor/ElvenSandstone${names[it]}")
		}
		
		top = reg.registerIcon("${ModInfo.MODID}:decor/ElvenSandstoneTop")
		bottom = reg.registerIcon("${ModInfo.MODID}:decor/ElvenSandstoneBottom")
	}
	*/
	
	val names = arrayOf("Normal", "Carved", "Smooth")
}
