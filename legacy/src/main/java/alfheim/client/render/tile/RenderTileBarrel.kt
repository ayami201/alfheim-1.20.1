package alfheim.client.render.tile

import alexsocol.asjlib.mc
import alfheim.api.lib.LibResourceLocations
import alfheim.client.model.block.ModelBarrel
import alfheim.common.block.tile.TileBarrel
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import org.lwjgl.opengl.GL11.*

object RenderTileBarrel: TileEntitySpecialRenderer() {
	
	override fun renderTileEntityAt(tile: TileEntity, x: Double, y: Double, z: Double, partialTicks: Float) {
		if (tile !is TileBarrel) return
		
		val f5 = 0.0625f
		
		glPushMatrix()
		glTranslated(x, y, z)
		glRotatef(180f, 1f, 0f, 0f)
		glTranslated(0.5, -1.0, -0.5)
		glRotatef(90f, 0f, 1f, 0f)
		
		mc.renderEngine.bindTexture(LibResourceLocations.barrel)
		ModelBarrel.render(f5)
		
		if (tile.closed)
			ModelBarrel.renderCover(f5)
		
		glTranslatef(0f, (tile.amountLevel - 2) / -16f - 0.01f, 0f)
		
		tile.recipe?.renderLiquid(tile, f5)
		
		glPopMatrix()
	}
}