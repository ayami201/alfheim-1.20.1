package alfheim.client.render.tile

import alexsocol.asjlib.mc
import alexsocol.asjlib.render.ASJRenderHelper.discard
import alexsocol.asjlib.render.ASJRenderHelper.interpolatedTranslationReverse
import alexsocol.asjlib.render.ASJRenderHelper.setBlend
import alfheim.common.block.*
import alfheim.common.block.tile.TileTable
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import org.lwjgl.opengl.GL11.*

object RenderTileTable: TileEntitySpecialRenderer() {
	
	override fun renderTileEntityAt(tile: TileEntity?, x: Double, y: Double, z: Double, yaw: Float) {
		if (tile !is TileTable) return
		if (tile.worldObj == null) tile.worldObj = mc.theWorld
		
		glPushMatrix()
		glDisable(GL_LIGHTING)
		setBlend()
		interpolatedTranslationReverse(mc.thePlayer)
		
		mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture)
		Tessellator.instance.startDrawingQuads()
		
		val rb = RenderBlocks.getInstance()
		rb.blockAccess = tile.worldObj
		val table = AlfheimFluffBlocks.table
		val offset = 0.001f
		
		val standFree = tile.worldObj.getTileEntity(tile.xCoord + 1, tile.yCoord, tile.zCoord) is TileTable &&
		                tile.worldObj.getTileEntity(tile.xCoord - 1, tile.yCoord, tile.zCoord) is TileTable ||
		                tile.worldObj.getTileEntity(tile.xCoord, tile.yCoord, tile.zCoord + 1) is TileTable &&
		                tile.worldObj.getTileEntity(tile.xCoord, tile.yCoord, tile.zCoord - 1) is TileTable
		
		if (!standFree) {
			BlockDoubleCamo.iconOverride = tile.blockBottom
			BlockDoubleCamo.metaOverride = tile.blockBottomMeta
			
			table.setBlockBounds(6 / 16f, 0f, 6 / 16f, 10 / 16f, 0.75f + offset, 10 / 16f)
			rb.setRenderBoundsFromBlock(table)
			rb.renderStandardBlock(table, tile.xCoord, tile.yCoord, tile.zCoord)
		}
		
		BlockDoubleCamo.iconOverride = tile.blockTop
		BlockDoubleCamo.metaOverride = tile.blockTopMeta
		
		table.setBlockBounds(0f + offset, 0.75f, 0f + offset, 1f - offset, 1f, 1f - offset)
		rb.setRenderBoundsFromBlock(table)
		rb.renderStandardBlock(table, tile.xCoord, tile.yCoord, tile.zCoord)
		
		table.setBlockBounds(0f, 0f, 0f, 1f, 1f, 1f)
		rb.setRenderBoundsFromBlock(table)
		
		Tessellator.instance.draw()
		
		discard()
		glEnable(GL_LIGHTING)
		glPopMatrix()
	}
}
