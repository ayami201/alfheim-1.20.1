package alfheim.common.potion.berries

// PORT: импорты 1.20.1 (MAPPING.md)
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.potion.PotionAlfheim
import alfheim.port.legacy.*
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.Mob as EntityLiving
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent as LivingSetAttackTargetEvent
import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object PotionWTFBerry5: PotionAlfheim(AlfheimConfigHandler.potionIDWtfBerry5, "WTFBerry5", false, 0x9EA3B6) {
	
	@SubscribeEvent
	fun onLivingTargeted(e: LivingSetAttackTargetEvent) {
		// PORT: событие 1.20.1 приходит до того, как цель запомнена: цель снимается в самом событии (newTarget); цель
		// мести entityLivingToAttack → lastHurtByMob
		if (e.newTarget?.isPotionActive(id) != true) return
		
		val living = e.entityLiving
		living.setLastHurtByMob(null)
		
		if (living is EntityLiving)
			e.newTarget = null
//		if (e.target?.isPotionActive(id) != true) return
//		
//		val living = e.entityLiving
//		living.entityLivingToAttack = null
//		
//		if (living is EntityLiving)
//			living.attackTarget = null
	}
	
	@SubscribeEvent
	fun onLivingHurt(e: LivingHurtEvent) {
		val attacker = e.source.entity as? EntityLivingBase ?: return
		if (attacker.isPotionActive(id))
			attacker.removePotionEffect(id)
	}
	
	// also has hook to World#getClosestVulnerablePlayerToEntity
}
