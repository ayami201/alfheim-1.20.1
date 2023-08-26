package alfheim.client.render.entity

import alexsocol.asjlib.render.ModelBipedNew
import alfheim.api.lib.LibResourceLocations
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.renderer.entity.RenderBiped
import net.minecraft.entity.EntityLiving

object RenderEntityFrozenViking: RenderBiped(ModelBipedNew.INSTANCE, 0.5f, 1f) {
	override fun getEntityTexture(entity: EntityLiving) = LibResourceLocations.frozenViking
}
