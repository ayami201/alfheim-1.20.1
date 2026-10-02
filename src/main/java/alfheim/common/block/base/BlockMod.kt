package alfheim.common.block.base

// PORT: импорты 1.7.10 заменены на 1.20.1 (блок 1.7.10 — alfheim.port.legacy.Block1710, MAPPING.md)
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.block.ItemBlockLeavesMod
import alfheim.port.legacy.*
import net.minecraft.world.level.block.Block

@Suppress("LeakingThis")
open class BlockMod(material: Material): Block1710(material) {
	
	init {
		setCreativeTab(AlfheimTab)
		
		// PORT: анимированные текстуры 1.20.1 рисует сама по .mcmeta; подписка на TextureStitchEvent не нужна
//		if (ASJUtilities.isClient && isInterpolated())
//			MinecraftForge.EVENT_BUS.register(this)
	}
	
	override fun setBlockName(name: String): Block {
		if (shouldRegisterInNameSet())
			GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
		
		return super.setBlockName(name)
	}
	
	protected open fun shouldRegisterInNameSet() = true
	
	open fun isInterpolated() = false
	
	// PORT: иконки 1.7.10 → модели блоков, их строит генерация данных (alfheim.port.data); текстура — по имени блока
	/*
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(reg: IIconRegister) {
		if (!isInterpolated())
			blockIcon = IconHelper.forBlock(reg, this)
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	open fun loadTextures(event: TextureStitchEvent.Pre) {
		if (event.map.textureType == 0 && isInterpolated())
			blockIcon = InterpolatedIconHelper.forBlock(event.map, this)
	}
	*/
}
