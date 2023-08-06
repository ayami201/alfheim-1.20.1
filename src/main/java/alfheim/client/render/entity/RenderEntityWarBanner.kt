package alfheim.client.render.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper.discard
import alexsocol.asjlib.render.ASJRenderHelper.setBlend
import alexsocol.asjlib.render.ASJRenderHelper.setTwoside
import alfheim.api.ModInfo
import alfheim.api.lib.LibResourceLocations
import alfheim.common.entity.EntityWarBanner
import net.minecraft.client.renderer.entity.Render
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.AdvancedModelLoader
import org.lwjgl.opengl.GL11.*

object RenderEntityWarBanner: Render() {
	
	val model = AdvancedModelLoader.loadModel(ResourceLocation(ModInfo.MODID, "model/WarBanner.obj"))
	
	override fun doRender(entity: Entity, x: Double, y: Double, z: Double, yaw: Float, ticks: Float) {
		glPushMatrix()
		glTranslated(x, y, z)
		glRotatef(entity.air / -10f, 0f, 1f, 0f)
		glScalef(2f)
		mc.renderEngine.bindTexture(getEntityTexture(entity))
		model.renderAll()
		glPopMatrix()
	}
	
	override fun getEntityTexture(entity: Entity?) = if ((entity as? EntityWarBanner)?.type == true) LibResourceLocations.warBanner else LibResourceLocations.warBannerAlt
}
