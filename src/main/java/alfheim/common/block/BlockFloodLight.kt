package alfheim.common.block

import alfheim.client.core.helper.IconHelper
import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.TileFloodLight
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.util.IIcon
import net.minecraft.world.*

class BlockFloodLight: BlockContainerMod(Material.iron) {
	
	lateinit var iconSource: IIcon
	
	init {
		setBlockName("FloodLight")
	}
	
	override fun registerBlockIcons(reg: IIconRegister) {
		super.registerBlockIcons(reg)
		iconSource = IconHelper.forBlock(reg, this, "Source")
	}
	
	override fun getLightValue() = 15
	override fun getLightValue(world: IBlockAccess, x: Int, y: Int, z: Int) = if (world.getBlockMetadata(x, y, z) > 0) 15 else 0
	override fun getIcon(side: Int, meta: Int) = if (side == 0) iconSource else blockIcon
	override fun createNewTileEntity(world: World?, meta: Int) = TileFloodLight()
}
