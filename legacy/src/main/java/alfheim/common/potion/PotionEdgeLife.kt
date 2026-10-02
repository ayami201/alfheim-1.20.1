package alfheim.common.potion

import alfheim.common.core.handler.AlfheimConfigHandler
import cpw.mods.fml.common.eventhandler.*
import net.minecraftforge.event.entity.living.LivingDeathEvent
import kotlin.math.max

object PotionEdgeLife: PotionAlfheim(AlfheimConfigHandler.potionIDEdgeLife, "edgeLife", false, 0x600000) {
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun cancelDeath(e: LivingDeathEvent) {
		val target = e.entityLiving
		if (!target.isPotionActive(this)) return
		
		target.health = max(target.health, 1f)
		e.isCanceled = true
	}
}
