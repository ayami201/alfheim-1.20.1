package alfheim.client.render.entity

import alexsocol.asjlib.*
import alfheim.common.entity.EntityFallingHang
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.entity.Entity
import org.lwjgl.opengl.GL11.*

object RenderEntityFallingHang: Render() {
	
	val render = RenderBlocks()
	
	override fun doRender(entity: Entity?, x: Double, y: Double, z: Double, yaw: Float, ticks: Float) {
		entity as EntityFallingHang
		
		glPushMatrix()
		glTranslated(x - 0.5, y, z - 0.5)
		glDisable(GL_LIGHTING)
		
		mc.renderEngine.bindTexture(getEntityTexture(entity))
		
		val tes = Tessellator.instance
		tes.setBrightness(entity.block.getMixedBrightnessForBlock(entity.worldObj, entity.posX.mfloor(), entity.posY.mfloor(), entity.posZ.mfloor()))
		tes.startDrawingQuads()
		render.drawCrossedSquares(entity.block.getIcon(0, entity.meta), 0.0, 0.0, 0.0, 1f)
		tes.draw()
		
		glEnable(GL_LIGHTING)
		glPopMatrix()
	}
	
	override fun getEntityTexture(entity: Entity?) = TextureMap.locationBlocksTexture!!
}
