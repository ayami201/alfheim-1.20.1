package alfheim.common.block

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.api.ModInfo
import alfheim.common.core.util.AlfheimTab
import alfheim.port.legacy.*

class BlockLivingMountain: BlockModMeta(Material.rock, 1, ModInfo.MODID, "LivingMountain", AlfheimTab, 5f) {
	
	// PORT: иконки → 4 модели (decor/LivingMountain1–4, alfheim.port.data.AlfheimBlockStates); по координатам грань
	// выбирает модель клиента (alfheim.port.client.AlfheimModels) по той же формуле; в руке — LivingMountain1
	/*
	override fun registerBlockIcons(reg: IIconRegister) {
		icons = Array(4) { IconHelper.forBlock(reg, this, it + 1, "decor") }
	}
	
	override fun getIcon(side: Int, meta: Int) = icons[0]
	
	@SideOnly(Side.CLIENT)
	override fun getIcon(world: IBlockAccess?, x: Int, y: Int, z: Int, side: Int) =
		when (side) {
			0, 1 -> icons.safeGet(abs(x % 2) + abs(z % 2) * 2)
			2, 3 -> icons.safeGet(abs(x % 2) + abs(y % 2) * 2)
			4, 5 -> icons.safeGet(abs(z % 2) + abs(y % 2) * 2)
			else -> icons[0]
		}
	*/
}
