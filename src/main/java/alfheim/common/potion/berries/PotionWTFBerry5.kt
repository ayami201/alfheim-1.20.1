package alfheim.common.potion.berries

import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.potion.PotionAlfheim
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent

object PotionWTFBerry5: PotionAlfheim(AlfheimConfigHandler.potionIDWtfBerry5, "WTFBerry5", false, 0x9EA3B6) {
	
	@SubscribeEvent
	fun onLivingTargeted(e: LivingSetAttackTargetEvent) {
		if (e.target?.isPotionActive(id) != true) return
		
		val living = e.entityLiving
		living.entityLivingToAttack = null
		
		if (living is EntityLiving)
			living.attackTarget = null
	}
	
	@SubscribeEvent
	fun onLivingHurt(e: LivingHurtEvent) {
		val attacker = e.source.entity as? EntityLivingBase ?: return
		if (attacker.isPotionActive(id))
			attacker.removePotionEffect(id)
	}
	
	// also has hook to World#getClosestVulnerablePlayerToEntity
}
