package alfheim.common.block

import alfheim.api.lib.LibRenderIDs
import alfheim.client.core.helper.IconHelper
import alfheim.common.block.tile.TileSecretGlass
import net.minecraft.block.BlockPistonBase
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.*
import net.minecraftforge.common.util.ForgeDirection

class BlockSecretGlass: BlockDoubleCamo(Material.glass) {
	
	init {
		setBlockName("SecretGlass")
		setLightOpacity(255)
		setStepSound(soundTypeGlass)
	}
	
	override fun onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, placer: EntityLivingBase, stack: ItemStack) {
		super.onBlockPlacedBy(world, x, y, z, placer, stack)
		world.setBlockMetadataWithNotify(x, y, z, BlockPistonBase.determineOrientation(world, x, y, z, placer), 2)
	}
	
	override fun shouldSideBeRendered(world: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Boolean {
		val default = super.shouldSideBeRendered(world, x, y, z, side)
		if (y <= 0 || y >= world.height) return default
		
		val tileThat = world.getTileEntity(x, y, z) as? TileSecretGlass ?: return default
		
		val sideThis = ForgeDirection.getOrientation(side)
		val sideThat = sideThis.opposite
		
		val tileThis = world.getTileEntity(x + sideThat.offsetX, y + sideThat.offsetY, z + sideThat.offsetZ) as? TileSecretGlass ?: return default
		val block_adj = if (sideThat.ordinal == topSide(world, x, y, z)) tileThat.blockTop else tileThat.blockBottom
		val block_src = if (sideThis.ordinal == topSide(world, x + sideThat.offsetX, y + sideThat.offsetY, z + sideThat.offsetZ)) tileThis.blockTop else tileThis.blockBottom
		
		return !(block_src.isOpaqueCube == block_adj.isOpaqueCube && block_src.renderBlockPass == block_adj.renderBlockPass)
	}
	
	override fun isSideSolid(world: IBlockAccess?, x: Int, y: Int, z: Int, side: ForgeDirection?) = true
	override fun topSide(meta: Int) = meta
	override fun getRenderType() = LibRenderIDs.idSimpleDoubleBlock
	override fun createNewTileEntity(world: World?, meta: Int) = TileSecretGlass()
	
	override fun registerBlockIcons(reg: IIconRegister) {
		grass_side_overlay_default = IconHelper.forName(reg, "grass_side_overlay_default")
	}
	
	companion object {
		lateinit var grass_side_overlay_default: IIcon
	}
}
