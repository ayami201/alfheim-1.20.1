package alfheim.client.model.block

import alexsocol.asjlib.mc
import alexsocol.asjlib.render.*
import alfheim.api.lib.*
import net.minecraft.client.model.*
import net.minecraft.entity.Entity
import org.lwjgl.opengl.GL11.*

/**
 * ModelManaReflector - AlexSocol
 * Created using Tabula 4.1.1
 */
object ModelManaReflector: ModelBase() {
	
	var shape3: ModelRenderer
	var shape4: ModelRenderer
	var shape5: ModelRenderer
	var shape6: ModelRenderer
	var shape7: ModelRenderer
	var shape8: ModelRenderer
	var shape12: ModelRenderer
	var shape21: ModelRenderer
	
	init {
		textureWidth = 64
		textureHeight = 32
		shape12 = ModelRenderer(this, 24, 0)
		shape12.setRotationPoint(0.0f, 16.0f, 15.0f)
		shape12.addBox(0.0f, 0.0f, 0.0f, 1, 16, 1, 0.0f)
		setRotateAngle(shape12, 0.0f, 0.0f, -1.5707964f)
		shape3 = ModelRenderer(this, 8, 0)
		shape3.setRotationPoint(0.0f, 1.0f, 15.0f)
		shape3.addBox(0.0f, 0.0f, 0.0f, 1, 14, 1, 0.0f)
		shape5 = ModelRenderer(this, 0, 0)
		shape5.setRotationPoint(0.0f, 1.0f, 1.0f)
		shape5.addBox(0.0f, 0.0f, 0.0f, 1, 14, 1, 0.0f)
		setRotateAngle(shape5, 1.5707964f, 0.0f, 0.0f)
		shape4 = ModelRenderer(this, 12, 0)
		shape4.setRotationPoint(15.0f, 1.0f, 15.0f)
		shape4.addBox(0.0f, 0.0f, 0.0f, 1, 14, 1, 0.0f)
		shape8 = ModelRenderer(this, 20, 0)
		shape8.setRotationPoint(0.0f, 1.0f, 15.0f)
		shape8.addBox(0.0f, 0.0f, 0.0f, 1, 16, 1, 0.0f)
		setRotateAngle(shape8, 0.0f, 0.0f, -1.5707964f)
		shape21 = ModelRenderer(this, 28, 0)
		shape21.setRotationPoint(0.5f, 0.5f, 0.45f)
		shape21.addBox(0.0f, 0.25f, -0.5f, 15, 21, 1, 0.0f)
		setRotateAngle(shape21, 0.7853982f, 0.0f, 0.0f)
		shape6 = ModelRenderer(this, 4, 0)
		shape6.setRotationPoint(15.0f, 1.0f, 1.0f)
		shape6.addBox(0.0f, 0.0f, 0.0f, 1, 14, 1, 0.0f)
		setRotateAngle(shape6, 1.5707964f, 0.0f, 0.0f)
		shape7 = ModelRenderer(this, 16, 0)
		shape7.setRotationPoint(0.0f, 1.0f, 0.0f)
		shape7.addBox(0.0f, 0.0f, 0.0f, 1, 16, 1, 0.0f)
		setRotateAngle(shape7, 0.0f, 0.0f, -1.5707964f)
	}
	
	override fun render(entity: Entity, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
		render(f5)
	}
	
	fun render(f5: Float) {
		shape3.render(f5)
		shape4.render(f5)
		shape5.render(f5)
		shape6.render(f5)
		shape7.render(f5)
		shape8.render(f5)
		shape12.render(f5)
		
		glDisable(GL_CULL_FACE)
		shape21.render(f5)
		glEnable(GL_CULL_FACE)
		
		glPushMatrix()
		mc.renderEngine.bindTexture(LibResourceLocations.manaReflectorMirror)
		ASJShaderHelper.useShader(LibShaderIDs.idCORE)
		ASJRenderHelper.setBlend()  
		glBegin(GL_QUADS)
		glNormal3f(0f, 1f, -1f)
		glTexCoord2f(0f, 0f)
		glVertex3f(1f - 1/32f, 1f - 0.225f/32f, 1f - 1.75f/32f)
		glTexCoord2f(0f, 1f)
		glVertex3f(1f - 1/32f, 0f + 2.1f/32f, 0f + 0.55f/32f)
		glTexCoord2f(1f, 1f)
		glVertex3f(0f + 1/32f, 0f + 2.1f/32f, 0f + 0.55f/32f)
		glTexCoord2f(1f, 0f)
		glVertex3f(0f + 1/32f, 1f - 0.225f/32f, 1f - 1.75f/32f)
		glEnd()
		
		ASJShaderHelper.releaseShader()
		glPopMatrix()
	}
	
	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	fun setRotateAngle(modelRenderer: ModelRenderer, x: Float, y: Float, z: Float) {
		modelRenderer.rotateAngleX = x
		modelRenderer.rotateAngleY = y
		modelRenderer.rotateAngleZ = z
	}
}
