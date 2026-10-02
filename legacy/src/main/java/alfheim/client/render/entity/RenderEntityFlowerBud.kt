package alfheim.client.render.entity

import alfheim.api.lib.*
import alfheim.client.model.entity.*
import net.minecraft.client.renderer.entity.*
import net.minecraft.entity.*
import org.lwjgl.opengl.GL11.*

object RenderEntityFlowerBud: RendererLivingEntity(ModelEntityFlowerBud, 0.5f) {
	
	override fun getEntityTexture(entity: Entity?) = LibResourceLocations.flowerBudNew
	
	override fun func_110813_b(entity: EntityLivingBase?) = false
	
	override fun preRenderCallback(entity: EntityLivingBase?, partialTicks: Float) {
		glTranslatef(0f, 0.4375f, 0f)
	}
}
