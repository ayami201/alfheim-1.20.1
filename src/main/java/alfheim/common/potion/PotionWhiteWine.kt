package alfheim.common.potion

import alfheim.common.core.handler.AlfheimConfigHandler
import cpw.mods.fml.common.eventhandler.*
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.event.entity.player.EntityInteractEvent

object PotionWhiteWine: PotionAlfheim(AlfheimConfigHandler.potionIDWhiteWine, "whiteWine", false, 0xE5FFE5) {
	
	@SubscribeEvent
	fun rideAnything(e: EntityInteractEvent) {
		if (!e.entityPlayer.isPotionActive(this)) return
		if (e.entityPlayer.riddenByEntity != null) return
		if (e.target.riddenByEntity != null) return
		if (e.entityPlayer.ridingEntity != null) return
		if (e.target.ridingEntity != null) return
		
		e.entityPlayer.mountEntity(e.target)
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun noRiderAttack(e: LivingAttackEvent) {
		val attacker = e.source.entity ?: return
		val target = e.entityLiving
		
		if (attacker.riddenByEntity == target && target.isPotionActive(this))
			e.isCanceled = true
	}
}
