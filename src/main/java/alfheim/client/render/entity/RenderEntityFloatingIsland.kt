package alfheim.client.render.entity

import alexsocol.asjlib.mc
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.common.entity.EntityFloatingIsland
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.entity.Entity
import org.lwjgl.opengl.GL11.*

object RenderEntityFloatingIsland: Render() {
	
	override fun doRender(entity: Entity?, _x: Double, _y: Double, _z: Double, yaw: Float, ticks: Float) {
		if (entity !is EntityFloatingIsland) return
		
		val access = entity.blockAccess
		val renderblocks = RenderBlocks(access)
		
		glPushMatrix()
		glTranslated(_x - 0.5, _y, _z - 0.5)
		glDisable(GL_LIGHTING)
		ASJRenderHelper.setBlend()
		
		mc.renderEngine.bindTexture(getEntityTexture(entity))
		val tes = Tessellator.instance
		tes.startDrawingQuads()
		
		for (renderPass in 0..1) {
			var needMorePasses = false
			
			for (y in access.startY..access.endY) {
				for (z in access.startZ..access.endZ) {
					for (x in access.startX..access.endX) {
						
						val block = access.getBlock(x, y, z)
						
						if (block.isAir(access, x, y, z)) continue
						
						if (block.renderBlockPass > renderPass) needMorePasses = true
						
						if (!block.canRenderInPass(renderPass)) continue
						
						renderblocks.renderBlockByRenderType(block, x, y, z)
					}
				}
			}
			
			if (!needMorePasses) {
				break
			}
		}
		
		tes.draw()
		ASJRenderHelper.discard()
		glEnable(GL_LIGHTING)
		glPopMatrix()
	}
	
	override fun getEntityTexture(entity: Entity?) = TextureMap.locationBlocksTexture!!
}