package alfheim.client.render.entity

import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.lib.LibResourceLocations
import alfheim.common.entity.EntityElementalSlime
import net.minecraft.client.model.ModelSlime
import net.minecraft.client.renderer.entity.RenderSlime
import net.minecraft.entity.monster.EntitySlime
import net.minecraft.util.ResourceLocation

object RenderEntityElementalSlime: RenderSlime(ModelSlime(16), ModelSlime(0), 0.25f) {
	
	override fun getEntityTexture(slime: EntitySlime): ResourceLocation {
		ASJRenderHelper.glColor1u(ASJRenderHelper.addAlpha((slime as EntityElementalSlime).elements.first().color, 0xFF))
		return LibResourceLocations.elementalSlime
	}
}
