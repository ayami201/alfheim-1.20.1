package alfheim.common.block

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alfheim.common.block.base.BlockMod
import alfheim.port.legacy.*

class BlockDwarfLantern: BlockMod(Material.rock) {
	
	// PORT: иконки → модель (alfheim.port.data.AlfheimBlockStates): верх и низ — decor/DwarfLanternTop, бока —
	// decor/DwarfLantern, плавная анимация (InterpolatedIconHelper → "interpolate" в .mcmeta)
//	lateinit var iconSide: IIcon
	
	init {
		setBlockName("DwarfLantern")
		setHardness(10f)
		setHarvestLevel("pickaxe", 2)
		setLightLevel(1f)
		setResistance(10000f)
		setStepSound(soundTypeStone)
		
		// PORT: анимированную текстуру 1.20.1 рисует сама по .mcmeta; подписка на TextureStitchEvent не нужна
//		if (ASJUtilities.isClient)
//			MinecraftForge.EVENT_BUS.register(this)
	}
	
	// PORT: metadata 1 (все грани — верх) у автора не ставится: предмета с ней нет, в постройках — только 0
	/*
	override fun registerBlockIcons(reg: IIconRegister) {
		blockIcon = IconHelper.forBlock(reg, this, "Top", "decor")
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	override fun loadTextures(event: TextureStitchEvent.Pre) {
		if (event.map.textureType == 0)
			iconSide = InterpolatedIconHelper.forBlock(event.map, this, "", "decor")
	}
	
	override fun getIcon(side: Int, meta: Int) = (if (meta != 1) {
		if (side < 2) blockIcon else iconSide
	} else blockIcon)!!
	*/
}
