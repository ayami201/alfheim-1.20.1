package alfheim.common.potion.berries

import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.potion.PotionAlfheim
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.EntityLiving
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent

object PotionWTFBerry5: PotionAlfheim(AlfheimConfigHandler.potionIDWtfBerry5, "WTFBerry5", false, 0x9EA3B6) {
	
	@SubscribeEvent
	fun onLivingTargeted(e: LivingSetAttackTargetEvent) {
		if (e.target?.isPotionActive(this.id) != true) return
		
		val living = e.entityLiving
		living.entityLivingToAttack = null
		
		if (living is EntityLiving)
			living.attackTarget = null
	}
}
