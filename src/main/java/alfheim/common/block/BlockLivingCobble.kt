package alfheim.common.block

// PORT: импорты 1.7.10 заменены на 1.20.1
import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.api.ModInfo
import alfheim.common.core.util.AlfheimTab
import alfheim.port.legacy.*

// PORT: вариант metadata — отдельный блок (BlockModMeta); КТ-9 — лексикон (ILexiconable)
class BlockLivingCobble(meta: Int): BlockModMeta(Material.rock, 4, ModInfo.MODID, "LivingCobble", AlfheimTab, 2f, resist = 60f, meta = meta)/*, ILexiconable*/ {
	
	// PORT: иконки → модели (alfheim.port.data). Вторую текстуру варианта 3 по координатам (getIcon ниже) выбирает
	// модель клиента по той же формуле (alfheim.port.client.AlfheimModels)
	/*
	lateinit var iconAlt: IIcon
	
	override fun registerBlockIcons(reg: IIconRegister) {
		super.registerBlockIcons(reg)
		iconAlt = IconHelper.forBlock(reg, this, "3Alt")
	}
	
	override fun getIcon(world: IBlockAccess, x: Int, y: Int, z: Int, side: Int): IIcon {
		val meta = world.getBlockMetadata(x, y, z)
		if (meta == 3 && ((31 * (31 * x + y) + z)) % 2 == 0) return iconAlt
		
		return super.getIcon(world, x, y, z, side)
	}
	*/
	
	/* PORT: КТ-9 — лексикон
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = when (world.getBlockMetadata(x, y, z)) {
		0 -> AlfheimLexiconData.worldgen
		1, 2 -> LexiconData.decorativeBlocks
		3 -> LexiconData.vineBall
		else -> null
	}
	*/
}
