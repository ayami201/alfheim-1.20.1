package alfheim.client.render.tile

import alexsocol.asjlib.mc
import alfheim.api.lib.LibResourceLocations
import alfheim.client.model.block.ModelManaReflector
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import org.lwjgl.opengl.GL11.*

object RenderTileManaReflector: TileEntitySpecialRenderer() {
	
	override fun renderTileEntityAt(tile: TileEntity, x: Double, y: Double, z: Double, ticks: Float) {
		val meta = if (tile.worldObj === null) 3 else tile.getBlockMetadata()
		
		glPushMatrix()
		glTranslated(x, y, z)
		
		when (meta) {
			0 -> {
				glRotatef(180f, 0f, 1f, 0f)
				glTranslatef(-1f, 0f, -1f)
			}
			
			1  -> {
				glRotatef(90f, 0f, 1f, 0f)
				glTranslatef(-1f, 0f, 0f)
			}
			
			3  -> {
				glRotatef(-90f, 0f, 1f, 0f)
				glTranslatef(0f, 0f, -1f)
			}
			
			4  -> {
				glRotatef(180f, 1f, 0f, 0f)
				glTranslatef(0f, -1f, -1f)
			}
			
			5  -> {
				glRotatef(-90f, 1f, 0f, 0f)
				glRotatef(90f, 0f, 0f, 1f)
				glTranslatef(-1f, -1f, 0f)
			}
			
			6  -> {
				glRotatef(-90f, 1f, 0f, 0f)
				glTranslatef(0f, -1f, 0f)
			}
			
			7  -> {
				glRotatef(-90f, 1f, 0f, 0f)
				glRotatef(-90f, 0f, 0f, 1f)
			}
			
			8  -> {
				glRotatef(-90f, 0f, 0f, 1f)
				glRotatef(180f, 1f, 0f, 0f)
				glTranslatef(-1f, -1f, -1f)
			}
			
			9  -> {
				glRotatef(-90f, 0f, 0f, 1f)
				glRotatef(-90f, 1f, 0f, 0f)
				glTranslatef(-1f, -1f, 0f)
			}
			
			10 -> {
				glRotatef(-90f, 0f, 0f, 1f)
				glTranslatef(-1f, 0f, 0f)
			}
			
			11 -> {
				glRotatef(-90f, 0f, 0f, 1f)
				glRotatef(90f, 1f, 0f, 0f)
				glTranslatef(-1f, 0f, -1f)
			}
		}
		
		mc.renderEngine.bindTexture(LibResourceLocations.manaReflector)
		ModelManaReflector.render(0.0625f)
		
		glPopMatrix()
	}
}
