package alfheim.client.render.tile

import alexsocol.asjlib.mc
import alexsocol.asjlib.render.ASJRenderHelper.discard
import alexsocol.asjlib.render.ASJRenderHelper.interpolatedTranslationReverse
import alexsocol.asjlib.render.ASJRenderHelper.setBlend
import alfheim.common.block.*
import alfheim.common.block.tile.TileChair
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import org.lwjgl.opengl.GL11.*

object RenderTileChair: TileEntitySpecialRenderer() {
	
	override fun renderTileEntityAt(tile: TileEntity?, x: Double, y: Double, z: Double, yaw: Float) {
		if (tile !is TileChair) return
		if (tile.worldObj == null) tile.worldObj = mc.theWorld
		
		glPushMatrix()
		glDisable(GL_LIGHTING)
		setBlend()
		interpolatedTranslationReverse(mc.thePlayer)
		
		mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture)
		Tessellator.instance.startDrawingQuads()
		
		val rb = RenderBlocks.getInstance()
		rb.blockAccess = tile.worldObj
		val chair = AlfheimFluffBlocks.chair
		
		val min = 3/16f
		val max = 13/16f
		
		BlockDoubleCamo.iconOverride = tile.blockBottom
		BlockDoubleCamo.metaOverride = tile.blockBottomMeta
		
		if (tile.getBlockMetadata() == 1) {
			val thicc = 2/16f
			val offset = 0.001f
			
			chair.setBlockBounds(min, 0f, min, min + thicc, 0.5f + offset, min + thicc)
			rb.setRenderBoundsFromBlock(chair)
			rb.renderStandardBlock(chair, tile.xCoord, tile.yCoord, tile.zCoord)
			
			chair.setBlockBounds(max - thicc, 0f, min, max, 0.5f + offset, min + thicc)
			rb.setRenderBoundsFromBlock(chair)
			rb.renderStandardBlock(chair, tile.xCoord, tile.yCoord, tile.zCoord)
			
			chair.setBlockBounds(max, 0f, max, max - thicc, 0.5f + offset, max - thicc)
			rb.setRenderBoundsFromBlock(chair)
			rb.renderStandardBlock(chair, tile.xCoord, tile.yCoord, tile.zCoord)
			
			chair.setBlockBounds(min, 0f, max - thicc, min + thicc, 0.5f + offset, max)
			rb.setRenderBoundsFromBlock(chair)
			rb.renderStandardBlock(chair, tile.xCoord, tile.yCoord, tile.zCoord)
		} else {
			chair.setBlockBounds(0.25f, 0f, 0.25f, 0.75f, 1/16f, 0.75f)
			rb.setRenderBoundsFromBlock(chair)
			rb.renderStandardBlock(chair, tile.xCoord, tile.yCoord, tile.zCoord)
			
			val offset = 0.001f
			chair.setBlockBounds(6/16f, 1/16f - offset, 6/16f, 10/16f, 0.5f + offset, 10/16f)
			rb.setRenderBoundsFromBlock(chair)
			rb.renderStandardBlock(chair, tile.xCoord, tile.yCoord, tile.zCoord)
		}
		
		BlockDoubleCamo.iconOverride = tile.blockTop
		BlockDoubleCamo.metaOverride = tile.blockTopMeta
		
		chair.setBlockBounds(min, 0.5f, min, max, 0.75f, max)
		rb.setRenderBoundsFromBlock(chair)
		rb.renderStandardBlock(chair, tile.xCoord, tile.yCoord, tile.zCoord)
		
		chair.setBlockBounds(min, 0f, min, max, 0.75f, max)
		rb.setRenderBoundsFromBlock(chair)
		
		Tessellator.instance.draw()
		
		discard()
		glEnable(GL_LIGHTING)
		glPopMatrix()
	}
}
