package alfheim.client.render.entity

import alexsocol.asjlib.render.*
import alfheim.api.lib.LibResourceLocations
import net.minecraft.entity.EntityLiving

object RenderEntityFrozenViking: RenderBipedNew(ModelBipedNew(), 0.5f) {
	override fun getEntityTexture(entity: EntityLiving) = LibResourceLocations.frozenViking
}
