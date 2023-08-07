package alfheim.client.render.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.AlfheimAPI
import alfheim.common.block.tile.TileAnomaly
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.Vec3
import org.lwjgl.opengl.GL11.*

// Render from Thaumcraft nodes by Azanor
object RenderTileAnomaly: TileEntitySpecialRenderer() {
	
	override fun renderTileEntityAt(tile: TileEntity, x: Double, y: Double, z: Double, partialTicks: Float) {
		if (tile !is TileAnomaly) return
		
		val mainSTE = tile.subTiles[tile.mainSubTile] ?: return
		
		mainSTE.bindTexture()
		
		glPushMatrix()
		glAlphaFunc(GL_GREATER, 1 / 255f)
		glEnable(GL_BLEND)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
		glDisable(GL_LIGHTING)
		glDepthMask(false)
		
		glTranslated(mainSTE.x + 0.5, mainSTE.y + 0.5, mainSTE.z + 0.5)
		
		val strip = AlfheimAPI.getAnomaly(tile.mainSubTile ?: "").strip
		val frame = System.nanoTime().div(40000000L).plus(tile.seed).rem(32).I
		
		renderFacingStrip(strip, frame)
		
		glDepthMask(true)
		glEnable(GL_LIGHTING)
		glDisable(GL_BLEND)
		glAlphaFunc(GL_GREATER, 0.1f)
		glPopMatrix()
	}
	
	fun renderFacingStrip(strip: Int, frame: Int) {
		val tessellator = Tessellator.instance
		val arX = ActiveRenderInfo.rotationX.D
		val arZ = ActiveRenderInfo.rotationZ.D
		val arYZ = ActiveRenderInfo.rotationYZ.D
		val arXY = ActiveRenderInfo.rotationXY.D
		val arXZ = ActiveRenderInfo.rotationXZ.D
		val player = mc.renderViewEntity
		ASJRenderHelper.interpolatedTranslationReverse(player)
		tessellator.startDrawingQuads()
		tessellator.setBrightness(220)
		tessellator.setColorRGBA_F(1f, 1f, 1f, 1f)
		val v1 = Vec3.createVectorHelper(-arX - arYZ, -arXZ, -arZ - arXY)
		val v2 = Vec3.createVectorHelper(-arX + arYZ, arXZ, -arZ + arXY)
		val v3 = Vec3.createVectorHelper(arX + arYZ, arXZ, arZ + arXY)
		val v4 = Vec3.createVectorHelper(arX - arYZ, -arXZ, arZ - arXY)
		val u = frame / 32.0
		val U = (frame + 1) / 32.0
		val v = strip / 32.0
		val V = (strip + 1) / 32.0
		tessellator.setNormal(0f, 0f, -1f)
		tessellator.addVertexWithUV(v1.xCoord, v1.yCoord, v1.zCoord, U, V)
		tessellator.addVertexWithUV(v2.xCoord, v2.yCoord, v2.zCoord, U, v)
		tessellator.addVertexWithUV(v3.xCoord, v3.yCoord, v3.zCoord, u, v)
		tessellator.addVertexWithUV(v4.xCoord, v4.yCoord, v4.zCoord, u, V)
		tessellator.draw()
	}
}