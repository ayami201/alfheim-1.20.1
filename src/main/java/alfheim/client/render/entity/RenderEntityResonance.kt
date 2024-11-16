package alfheim.client.render.entity

import alexsocol.asjlib.render.*
import alfheim.api.lib.LibResourceLocations
import alfheim.common.entity.EntityResonance
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.Render
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12
import kotlin.math.abs

object RenderEntityResonance: Render() {
	
	override fun doRender(entity: Entity?, x: Double, y: Double, z: Double, yaw: Float, ticks: Float) {
		if (entity !is EntityResonance) return
		
		glPushMatrix()
		glTranslated(x, y, z)
		glDisable(GL_DEPTH_TEST)
		glEnable(GL12.GL_RESCALE_NORMAL)
		ASJRenderHelper.setBlend()
		ASJRenderHelper.setGlow()
		
		val tex = getEntityTexture(entity) as ResourceLocationAnimated
		tex.bind()
		
		val tes = Tessellator.instance
		glRotatef(180f - renderManager.playerViewY, 0f, 1f, 0f)
		glRotatef(-renderManager.playerViewX, 1f, 0f, 0f)
		tes.startDrawingQuads()
		tes.setNormal(0f, 1f, 0f)
		val s = 0.1 * (0.5 - abs(((entity.ticksExisted + ticks) / 10.0 + 1) % 2 - 1)) + 0.4
		tes.addVertexWithUV(-s, -s, 0.0, 0.0, 1.0)
		tes.addVertexWithUV(s, -s, 0.0, 1.0, 1.0)
		tes.addVertexWithUV(s, s, 0.0, 1.0, 0.0)
		tes.addVertexWithUV(-s, s, 0.0, 0.0, 0.0)
		tes.draw()
		
		glDisable(GL12.GL_RESCALE_NORMAL)
		glEnable(GL_DEPTH_TEST)
		ASJRenderHelper.discard()
		glPopMatrix()
	}
	
	override fun getEntityTexture(entity: Entity?): ResourceLocation {
		if (entity !is EntityResonance) return LibResourceLocations.resonance1
		return if (entity.mode == 2) LibResourceLocations.resonance2 else LibResourceLocations.resonance1
	}
}
