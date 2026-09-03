package alfheim.client.render.entity

import alexsocol.asjlib.mc
import alexsocol.asjlib.render.ASJRenderHelper
import alexsocol.asjlib.render.ASJRenderHelper.discard
import alexsocol.asjlib.render.ASJRenderHelper.setBlend
import alexsocol.asjlib.render.ASJRenderHelper.setGlow
import alexsocol.asjlib.render.ASJRenderHelper.setTwoside
import alfheim.api.ModInfo
import alfheim.api.lib.LibResourceLocations
import alfheim.common.entity.EntityFenrirSlash
import net.minecraft.client.renderer.entity.Render
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.AdvancedModelLoader
import org.lwjgl.opengl.GL11.*

object RenderEntityFenrirSlash: Render() {
	
	private var model = AdvancedModelLoader.loadModel(ResourceLocation(ModInfo.MODID, "model/slash4.obj"))
	
	override fun doRender(entity: Entity, x: Double, y: Double, z: Double, yaw: Float, ticks: Float) {
		entity as EntityFenrirSlash
		
		glPushMatrix()
		setBlend()
		setGlow()
		setTwoside()
		
		glTranslated(x, y + 1.5, z)
		glRotatef(-90f + entity.yaw, 0f, 1f, 0f)
		glTranslatef(-2f, 0f, 0f)
		glRotatef(entity.roll, 1f, 0f, 0f)
		glScalef(2f, entity.height / 4f, 2f)
		
		mc.renderEngine.bindTexture(getEntityTexture(entity))
		model.renderAll()
		discard()
		glPopMatrix()
	}
	
	override fun getEntityTexture(entity: Entity?) = LibResourceLocations.slashWind
}
