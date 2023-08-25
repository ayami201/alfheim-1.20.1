package alfheim.client.render.entity

import alfheim.api.lib.LibResourceLocations
import alfheim.common.entity.EntityFrozenViking
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.renderer.entity.RenderBiped
import net.minecraft.entity.EntityLiving

object RenderEntityFrozenViking: RenderBiped(ModelBiped(), 0.5f, 1f) {
	override fun getEntityTexture(entity: EntityLiving) = LibResourceLocations.frozenViking[(entity as EntityFrozenViking).textureId]
}
