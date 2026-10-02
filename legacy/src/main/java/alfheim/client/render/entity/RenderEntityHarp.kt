package alfheim.client.render.entity

import alexsocol.asjlib.mc
import alfheim.api.ModInfo
import alfheim.api.lib.LibResourceLocations
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.item.material.ItemElvenResource
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.AdvancedModelLoader
import org.lwjgl.opengl.GL11.*
import vazkii.botania.client.core.handler.ClientTickHandler
import kotlin.math.sin

object RenderEntityHarp: Render() {
	
	val model = if (AlfheimConfigHandler.minimalGraphics) null else AdvancedModelLoader.loadModel(ResourceLocation(ModInfo.MODID, "model/Harp.obj"))
	
	init {
		shadowSize = 0f
	}
	
	override fun getEntityTexture(entity: Entity) = LibResourceLocations.harp
	
	override fun doRender(entity: Entity, x: Double, y: Double, z: Double, yaw: Float, partialTick: Float) {
		glPushMatrix()
		glTranslated(x, y + 0.2 + sin(ClientTickHandler.total / 50.0 + entity.entityId % 100) / 10.0, z)
		glRotatef(ClientTickHandler.total % 360f + entity.entityId % 100, 0f, 1f, 0f)
		
		if (model == null) {
			glTranslated(-0.5, 0.0, 0.0)
			mc.renderEngine.bindTexture(TextureMap.locationItemsTexture)
			ItemRenderer.renderItemIn2D(Tessellator.instance, ItemElvenResource.harp.maxU, ItemElvenResource.harp.minV, ItemElvenResource.harp.minU, ItemElvenResource.harp.maxV, ItemElvenResource.harp.iconWidth, ItemElvenResource.harp.iconHeight, 1f / 16f)
			glTranslated(0.5, 0.0, 0.0)
		} else {
			mc.renderEngine.bindTexture(getEntityTexture(entity))
			model.renderAll()
		}
		glPopMatrix()
	}
}