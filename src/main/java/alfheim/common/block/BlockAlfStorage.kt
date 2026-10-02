package alfheim.common.block

// PORT: импорты 1.7.10 заменены на 1.20.1
import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.api.ModInfo
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.AlfheimTab
import alfheim.port.legacy.*
import net.minecraft.world.level.BlockGetter as IBlockAccess

// PORT: вариант metadata — отдельный блок (BlockModMeta); КТ-9 — лексикон (ILexiconable)
class BlockAlfStorage(meta: Int): BlockModMeta(Material.iron, 6, ModInfo.MODID, "alfStorage", AlfheimTab, 5f, resist = 60f, meta = meta)/*, ILexiconable*/ {
	
	// PORT: анимированная текстура (InterpolatedIconHelper) — alf_storage4.png.mcmeta с interpolate, её рисует 1.20.1
	/*
	init {
		if (ASJUtilities.isClient)
			MinecraftForge.EVENT_BUS.register(this)
	}
	*/
	
	override fun isBeaconBase(worldObj: IBlockAccess?, x: Int, y: Int, z: Int, beaconX: Int, beaconY: Int, beaconZ: Int) = true
	
	/* PORT: КТ-9 — лексикон
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) =
		when (world.getBlockMetadata(x, y, z)) {
			0       -> AlfheimLexiconData.elvorium
			in 1..3 -> AlfheimLexiconData.essences
			4       -> LexiconData.gaiaRitualHardmode
			5       -> LexiconData.pool
//			6       -> RecipeListAB.advandedAgglomerationPlate TODO back
			else    -> null
		}
	*/
	
	fun isInterpolated(meta: Int) = meta == 4
	
	fun hasNewTexture(meta: Int) = AlfheimConfigHandler.newStorageTexture && (meta == 1 || meta == 2 || meta == 3)
	
	// PORT: иконки → модели (alfheim.port.data): у вариантов 1–3 две модели, со старой и новой текстурой; нужную по
	// hasNewTexture выбирает клиент при сборке моделей (alfheim.port.client.AlfheimModels)
	/*
	@Suppress("UNCHECKED_CAST")
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(reg: IIconRegister) {
		icons = arrayOfNulls<IIcon?>(subtypes) as Array<IIcon>
		
		repeat(subtypes) {
			if (!isInterpolated(it))
				icons[it] = IconHelper.forBlock(reg, this, "${if (hasNewTexture(it)) "New" else ""}$it")
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