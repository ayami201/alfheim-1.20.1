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
	
	lateinit var iconBlank: IIcon
	
	init {
		setBlockName("SecretGlass")
		setLightOpacity(255)
		setStepSound(soundTypeGlass)
	}
	
	override fun onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, placer: EntityLivingBase?, stack: ItemStack?) {
		world.setBlockMetadataWithNotify(x, y, z, BlockPistonBase.determineOrientation(world, x, y, z, placer), 2)
	}
	
	override fun registerBlockIcons(reg: IIconRegister) {
		iconBlank = IconHelper.forName(reg, "blank")
	}
	
	override fun isSideSolid(world: IBlockAccess?, x: Int, y: Int, z: Int, side: ForgeDirection?) = true
	override fun getIcon(side: Int, meta: Int) = if ((!reverse && side == thisMeta) || (reverse && side != thisMeta)) super.getIcon(side, meta) else iconBlank
	override fun topSide(world: IBlockAccess, x: Int, y: Int, z: Int) = world.getBlockMetadata(x, y, z)
	override fun getRenderType() = LibRenderIDs.idSecretGlass
	override fun createNewTileEntity(world: World?, meta: Int) = TileSecretGlass()
	
	companion object {
		var reverse = false
		var thisMeta = 0
	}
}
