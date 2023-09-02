package alfheim.client.render.tile

import alexsocol.asjlib.mc
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.common.block.*
import alfheim.common.block.tile.TileSecretGlass
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import org.lwjgl.opengl.GL11.*

object RenderTileSecretGlass: TileEntitySpecialRenderer() {
	
	override fun renderTileEntityAt(tile: TileEntity?, x: Double, y: Double, z: Double, yaw: Float) {
		if (tile !is TileSecretGlass) return
		if (tile.worldObj == null) tile.worldObj = mc.theWorld
		
		glPushMatrix()
		glDisable(GL_LIGHTING)
		glEnable(GL_CULL_FACE)
		ASJRenderHelper.setBlend()
		ASJRenderHelper.interpolatedTranslationReverse(mc.thePlayer)
		
		mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture)
		Tessellator.instance.startDrawingQuads()
		
		val rb = RenderBlocks.getInstance()
		rb.blockAccess = tile.worldObj
		
		val secret = AlfheimFluffBlocks.secretGlass
		
		BlockSecretGlass.thisMeta = tile.getBlockMetadata()
		
		BlockDoubleCamo.iconOverride = tile.blockTop
		BlockDoubleCamo.metaOverride = tile.blockTopMeta
		BlockSecretGlass.reverse = false
		rb.setRenderBoundsFromBlock(secret)
		rb.renderStandardBlock(secret, tile.xCoord, tile.yCoord, tile.zCoord)
		
		BlockDoubleCamo.iconOverride = tile.blockBottom
		BlockDoubleCamo.metaOverride = tile.blockBottomMeta
		BlockSecretGlass.reverse = true
		rb.setRenderBoundsFromBlock(secret)
		rb.renderStandardBlock(secret, tile.xCoord, tile.yCoord, tile.zCoord)
		
		Tessellator.instance.draw()
		
		ASJRenderHelper.discard()
		glEnable(GL_LIGHTING)
		glPopMatrix()
	}
}
