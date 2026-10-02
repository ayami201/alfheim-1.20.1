package alfheim.common.block

// PORT: импорты 1.20.1; BlockTrapDoor 1.7.10 — TrapDoor1710 (MAPPING.md, «Блоки и предметы»)
import alexsocol.asjlib.I
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.block.ItemBlockLeavesMod
import alfheim.port.legacy.*
import net.minecraft.world.level.block.Block

open class BlockModTrapDoor(material: Material, val name: String): TrapDoor1710(material) {
	
	var originalLight: Int = 0
	
	init {
		setCreativeTab(AlfheimTab)
		setBlockName(name)
	}
	
	override fun setBlockName(name: String): Block {
		if (shouldRegisterInNameSet())
			GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
		return super.setBlockName(name)
	}
	
	fun shouldRegisterInNameSet() = true
	
	override fun setLightLevel(level: Float): Block {
		originalLight = (level * 15).I
		return super.setLightLevel(level)
	}
	
	// PORT: иконка → модель люка (alfheim.port.data.AlfheimBlockStates): текстура — по имени блока
	/*
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(reg: IIconRegister) {
		blockIcon = IconHelper.forBlock(reg, this)
	}
	*/
}