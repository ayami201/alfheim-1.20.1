package alfheim.common.block.colored.rainbow

// PORT: импорты 1.7.10 заменены на 1.20.1
import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.api.ModInfo
import alfheim.common.core.util.AlfheimTab
import alfheim.port.legacy.*

// PORT: вариант metadata — отдельный блок (BlockModMeta), создают массивом `Array(4) { BlockSoftStorage(it) }`; КТ-9 —
// лексикон (ILexiconable)
class BlockSoftStorage(meta: Int): BlockModMeta(Material.cloth, 4, ModInfo.MODID, "softStorage", AlfheimTab, 0.4f, null, 0, meta = meta)/*, ILexiconable*/ {
//class BlockSoftStorage: BlockModMeta(Material.cloth, 4, ModInfo.MODID, "softStorage", AlfheimTab, 0.4f, null, 0), ILexiconable {
	
	init {
		setStepSound(soundTypeCloth)
		// PORT: анимированные текстуры (InterpolatedIconHelper) — soft_storage0.png.mcmeta и soft_storage3.png.mcmeta с
		// interpolate, их рисует 1.20.1
//		if (ASJUtilities.isClient)
//			MinecraftForge.EVENT_BUS.register(this)
	}
	
	/* PORT: КТ-9 — лексикон
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, lexicon: ItemStack) = when (world.getBlockMetadata(x, y, z)) {
		0    -> AlfheimLexiconData.rainbowFlora
		1    -> LexiconData.elvenResources
		2    -> AlfheimLexiconData.ores
		3    -> LexiconData.gaiaRitual
		else -> null
	}
	*/
	
	fun isInterpolated(meta: Int) = meta == 0 || meta == 3
	
	// PORT: иконки → модели (alfheim.port.data): текстура варианта — softStorage<номер>, у 0 и 3 анимация — .mcmeta
	/*
	@Suppress("UNCHECKED_CAST")
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(reg: IIconRegister) {
		icons = arrayOfNulls<IIcon?>(subtypes) as Array<IIcon>
		
		repeat(subtypes) {
			if (!isInterpolated(it))
				icons[it] = IconHelper.forBlock(reg, this, it)
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun loadTextures(event: TextureStitchEvent.Pre) {
		if (event.map.textureType != 0) return
		
		repeat(subtypes) {
			if (isInterpolated(it))
				icons[it] = InterpolatedIconHelper.forBlock(event.map, this, it)
		}
	}
	*/
}
