package alfheim.client.render.entity

import alfheim.api.lib.LibResourceLocations
import alfheim.client.model.entity.ModelEntityFrozenViking
import net.minecraft.client.renderer.entity.RenderBiped
import net.minecraft.entity.EntityLiving

object RenderEntityFrozenViking: RenderBiped(ModelEntityFrozenViking, 0.5f) {
	override fun getEntityTexture(entity: EntityLiving) = LibResourceLocations.frozenViking
}
