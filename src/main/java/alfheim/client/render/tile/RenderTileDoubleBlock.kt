package alfheim.client.render.tile

import alexsocol.asjlib.mc
import alexsocol.asjlib.render.ASJRenderHelper.discard
import alexsocol.asjlib.render.ASJRenderHelper.interpolatedTranslationReverse
import alexsocol.asjlib.render.ASJRenderHelper.setBlend
import alfheim.common.block.WorldWrapper
import alfheim.common.block.tile.TileDoubleBlock
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import org.lwjgl.opengl.GL11.*

object RenderTileDoubleBlock: TileEntitySpecialRenderer() {
	
	override fun renderTileEntityAt(tile: TileEntity?, x: Double, y: Double, z: Double, yaw: Float) {
		if (tile !is TileDoubleBlock) return
		if (tile.worldObj == null) tile.worldObj = mc.theWorld
		
		glPushMatrix()
		glDisable(GL_LIGHTING)
		glEnable(GL_CULL_FACE)
		setBlend()
		interpolatedTranslationReverse(mc.thePlayer)
		
		mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture)
		Tessellator.instance.startDrawingQuads()
		
		val wrapper = WorldWrapper(tile.worldObj)
		val rb = RenderBlocks.getInstance()
		rb.blockAccess = wrapper
		
		wrapper.setOverride(tile.xCoord, tile.yCoord, tile.zCoord, tile.blockBottom, tile.blockBottomMeta)
		rb.setRenderBoundsFromBlock(wrapper.blockOverride)
		rb.renderBlockByRenderType(wrapper.blockOverride, tile.xCoord, tile.yCoord, tile.zCoord)
		
		wrapper.setOverride(tile.xCoord, tile.yCoord, tile.zCoord, tile.blockTop, tile.blockTopMeta)
		rb.setRenderBoundsFromBlock(wrapper.blockOverride)
		rb.renderBlockByRenderType(wrapper.blockOverride, tile.xCoord, tile.yCoord, tile.zCoord)
		
		Tessellator.instance.draw()
		
		discard()
		glEnable(GL_LIGHTING)
		glPopMatrix()
	}
}