package alfheim.common.block.mana

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.alt.BlockAltLeaves
import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.TileWorldTree
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderGameOverlayEvent
import vazkii.botania.api.wand.IWandable
import java.awt.Color

class BlockWorldTree: BlockContainerMod(Material.wood), IWandable {
	
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
	
	companion object {
		
		init {
			if (ASJUtilities.isClient) eventForge()
		}
		
		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		fun onDrawScreenPost(e: RenderGameOverlayEvent.Post) {
			if (e.type != RenderGameOverlayEvent.ElementType.ALL) return
			val pos = mc.objectMouseOver ?: return
			val (hitX, hitY, hitZ) = Vector3(pos.hitVec ?: return).sub(pos.blockX, pos.blockY, pos.blockZ).mul(16).F
			val tile = mc.theWorld.getTileEntity(pos.blockX, pos.blockY, pos.blockZ) as? TileWorldTree ?: return
			val idHover = TileWorldTree.appleCoords.indexOfFirst { hitX in it.first && hitY in it.second && hitZ in it.third }
			if (idHover == -1) return
			
			val (i, j, k) = tile.boundList[idHover] ?: return
			val other = mc.theWorld.getTileEntity(i, j, k) as? TileWorldTree ?: return
			val u = e.resolution.scaledWidth / 2 + 10
			val v = e.resolution.scaledHeight / 2 - mc.fontRenderer.FONT_HEIGHT / 2
			
			mc.fontRenderer.drawStringWithShadow("'${other.name.takeIf { it.isNotBlank() } ?: ItemStack(AlfheimBlocks.worldTree).displayName}' ($i $j $k)", u, v, Color.HSBtoRGB(idHover * (360 / 16f) / 360f, 1f, 1f))
		}
	}
}
