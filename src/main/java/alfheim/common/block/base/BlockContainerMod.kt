package alfheim.common.block.base

// PORT: импорты 1.20.1 (BlockContainer 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.block.ItemBlockLeavesMod
import alfheim.port.legacy.*
import net.minecraft.world.level.block.Block

@Suppress("LeakingThis")
abstract class BlockContainerMod(material: Material): BlockContainer(material) {
	
	init {
		setCreativeTab(AlfheimTab)
		// PORT: анимированные текстуры 1.20.1 рисует сама по .mcmeta; подписка на TextureStitchEvent не нужна
//		if (ASJUtilities.isClient && isInterpolated())
//			MinecraftForge.EVENT_BUS.register(this)
	}
	
	override fun setBlockName(name: String): Block {
		if (shouldRegisterInNameSet()) {
			GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
		}
		
		return super.setBlockName(name)
	}
	
	open fun shouldRegisterInNameSet() = true
	
	// PORT: иконки 1.7.10 → модели блоков, их строит генерация данных (alfheim.port.data); текстура — по имени блока
	/*
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(reg: IIconRegister) {
		if (!isInterpolated())
			blockIcon = IconHelper.forBlock(reg, this)
	}
	*/
	
	open fun isInterpolated(): Boolean = false
	
	/*
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun loadTextures(event: TextureStitchEvent.Pre) {
		if (event.map.textureType == 0)
			loadTextures(event.map)
	}
	
	@SideOnly(Side.CLIENT)
	open fun loadTextures(map: TextureMap) {
		if (isInterpolated())
			blockIcon = InterpolatedIconHelper.forBlock(map, this)
	}
	*/
}
