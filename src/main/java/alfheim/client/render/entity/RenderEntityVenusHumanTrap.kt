package alfheim.client.render.entity

import alfheim.api.lib.LibResourceLocations
import alfheim.client.model.entity.ModelEntityVenusHumanTrap
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.entity.Entity

object RenderEntityVenusHumanTrap: RenderLiving(ModelEntityVenusHumanTrap, 0.25f) {
	override fun getEntityTexture(entity: Entity?) = LibResourceLocations.venusHumanTrap
}
