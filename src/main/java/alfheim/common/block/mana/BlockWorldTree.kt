package alfheim.common.block.mana

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.alt.BlockAltLeaves
import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.TileWorldTree
import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import vazkii.botania.api.wand.*
import java.awt.Color

class BlockWorldTree: BlockContainerMod(Material.wood), IWandable, IWandHUD {
	
	init {
		setBlockBounds(1/16f, 0f, 1/16f, 15/16f, 1f, 15/16f)
		setBlockName("WorldTree")
		setHardness(1f)
		setStepSound(soundTypeWood)
	}
	
	override fun registerBlockIcons(reg: IIconRegister) = Unit

	override fun getIcon(side: Int, meta: Int) = AlfheimBlocks.altLeaves.getIcon(side, BlockAltLeaves.yggMeta)!!
	
	override fun isOpaqueCube() = false
	
	override fun renderAsNormalBlock() = false
	
	override fun getRenderType() = LibRenderIDs.idWorldTree
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		return !world.isRemote && (world.getTileEntity(x, y, z) as? TileWorldTree)?.onActivated(player, hitX, hitY, hitZ) == true
	}
	
	override fun createNewTileEntity(world: World?, meta: Int) = TileWorldTree()
	
	override fun onUsedByWand(player: EntityPlayer?, stack: ItemStack?, world: World?, x: Int, y: Int, z: Int, side: Int) = false
	
	override fun renderHUD(mc: Minecraft, res: ScaledResolution, world: World, x: Int, y: Int, z: Int) {
		val tile = world.getTileEntity(x, y, z) as? TileWorldTree ?: return
		
		val (hitX, hitY, hitZ) = Vector3(mc.objectMouseOver.hitVec).sub(x, y, z).mul(16).F
		
		val idHover = TileWorldTree.appleCoords.indexOfFirst { hitX in it.first && hitY in it.second && hitZ in it.third }
		if (idHover == -1) return
		
		val (i, j, k) = tile.boundList[idHover] ?: return
		val other = world.getTileEntity(i, j, k) as? TileWorldTree ?: return
		
		val u = res.scaledWidth / 2 + 10
		val v = res.scaledHeight / 2 - mc.fontRenderer.FONT_HEIGHT / 2
		
		mc.fontRenderer.drawStringWithShadow("'${other.name.takeIf { it.isNotBlank() } ?: ItemStack(this).displayName}' ($i $j $k)", u, v, Color.HSBtoRGB(idHover * (360 / 16f) / 360f, 1f, 1f))
	}
}
