package alfheim.client.render.entity

import alexsocol.asjlib.render.*
import alfheim.api.lib.*
import alfheim.client.render.world.SpellVisualizations
import net.minecraft.client.renderer.entity.Render
import net.minecraft.entity.Entity
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_RESCALE_NORMAL
import org.lwjgl.opengl.GL20.*
import vazkii.botania.client.core.handler.ClientTickHandler

object RenderEntityFenrirDome: Render() {
	
	override fun doRender(entity: Entity, x: Double, y: Double, z: Double, yaw: Float, ticks: Float) {
		glPushMatrix()
		glTranslated(x, y, z)
		glEnable(GL_RESCALE_NORMAL)
		glDisable(GL_TEXTURE_2D)
		glAlphaFunc(GL_GEQUAL, 0f)
		ASJRenderHelper.setBlend()
		glColor4f(0.5f, 0.75f, 0.5f, 1f)
		
		ASJShaderHelper.useShader(LibShaderIDs.idNoise) {
			glUniform1f(glGetUniformLocation(it, "ftime"), ClientTickHandler.total / 5f)
			glUniform4f(glGetUniformLocation(it, "color2"), 0.75f, 1f, 0.75f, 0.5f)
		}
		
		glEnable(GL_CULL_FACE)
		glCullFace(GL_FRONT)
		SpellVisualizations.renderSphere(entity.width / 2.0)
		glCullFace(GL_BACK)
		SpellVisualizations.renderSphere(entity.width / 2.0)
		
		ASJShaderHelper.releaseShader()
		
		glColor4f(1f, 1f, 1f, 1f)
		ASJRenderHelper.discard()
		glAlphaFunc(GL_GREATER, 0.1f)
		glEnable(GL_TEXTURE_2D)
		glDisable(GL_RESCALE_NORMAL)
		glPopMatrix()
	}
	
	override fun getEntityTexture(entity: Entity?) = LibResourceLocations.blank
}
