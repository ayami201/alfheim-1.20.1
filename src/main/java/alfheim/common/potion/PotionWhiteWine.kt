package alfheim.common.potion

// PORT: импорты 1.20.1 (MAPPING.md)
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.port.legacy.*
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract as EntityInteractEvent
import net.minecraftforge.eventbus.api.*

object PotionWhiteWine: PotionAlfheim(AlfheimConfigHandler.potionIDWhiteWine, "whiteWine", false, 0xE5FFE5) {
	
	@SubscribeEvent
	fun rideAnything(e: EntityInteractEvent) {
		if (!e.entityPlayer.isPotionActive(this)) return
		// PORT: riddenByEntity → firstPassenger, ridingEntity → vehicle, mountEntity → startRiding(…, true)
		if (e.entityPlayer.firstPassenger != null) return
		if (e.target.firstPassenger != null) return
		if (e.entityPlayer.vehicle != null) return
		if (e.target.vehicle != null) return
		
		e.entityPlayer.startRiding(e.target, true)
//		if (e.entityPlayer.riddenByEntity != null) return
//		if (e.target.riddenByEntity != null) return
//		if (e.entityPlayer.ridingEntity != null) return
//		if (e.target.ridingEntity != null) return
//		
//		e.entityPlayer.mountEntity(e.target)
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun noRiderAttack(e: LivingAttackEvent) {
		val attacker = e.source.entity ?: return
		val target = e.entityLiving
		
		if (attacker.firstPassenger == target && target.isPotionActive(this))
//		if (attacker.riddenByEntity == target && target.isPotionActive(this))
			e.isCanceled = true
	}
}
