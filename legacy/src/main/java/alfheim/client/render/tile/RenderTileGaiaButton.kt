package alfheim.client.render.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.common.block.tile.TileGaiaButton
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import org.lwjgl.opengl.GL11.*

object RenderTileGaiaButton: TileEntitySpecialRenderer() {
	
	override fun renderTileEntityAt(tile: TileEntity?, x: Double, y: Double, z: Double, ticks: Float) {
		if (tile !is TileGaiaButton) return
		
		val name = tile.name
		if (name.isEmpty()) return
		
		glPushMatrix()
		glTranslated(x, y, z)
		glScalef(1/32f)
		glRotatef(180f, 0f, 0f, 1f)
		
		val meta = tile.getBlockMetadata()
		val w = mc.fontRenderer.getStringWidth(name)
		val h = mc.fontRenderer.FONT_HEIGHT
		
		ASJRenderHelper.setGlow()
		
		when (meta and 7) {
			1 -> {
				glRotatef(90f, 0f, 1f, 0f)
				glTranslatef(-15.5f - w / 2f, -15f - h / 2f, if (meta == 1) -4.1f else -2.1f)
			}
			2 -> {
				glRotatef(-90f, 0f, 1f, 0f)
				glTranslatef(16.5f - w / 2f, -15f - h / 2f, if (meta == 2) 27.9f else 29.9f)
			}
			3 -> {
				glRotatef(180f, 0f, 1f, 0f)
				glTranslatef(16.5f - w / 2f, -15f - h / 2f, if (meta == 3) -4.1f else -2.1f)
			}
			4 -> {
				glTranslatef(-15.5f - w / 2f, -15f - h / 2f, if (meta == 4) 27.9f else 29.9f)
			}
			5 -> {
				glRotatef(-90f, 1f, 0f, 0f)
				glTranslatef(-15.5f - w / 2f, -15f - h / 2f, if (meta == 5) -4.1f else -2.1f)
			}
			6 -> {
				glRotatef(90f, 1f, 0f, 0f)
				glTranslatef(-15.5f - w / 2f, 17f - h / 2f, if (meta == 6) 27.9f else 29.9f)
			}
		}
		
		mc.fontRenderer.drawString(name, 0, 0, tile.color)
		
		ASJRenderHelper.discard()
		
		glPopMatrix()
	}
}
