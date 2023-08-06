package alfheim.common.potion.berries

import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.potion.PotionAlfheim
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.event.entity.living.LivingAttackEvent

object PotionWTFBerry4: PotionAlfheim(AlfheimConfigHandler.potionIDWtfBerry4, "WTFBerry4", false, 0xDA6103) {
	
	@SubscribeEvent
	fun onLivingAttack(e: LivingAttackEvent) {
		when(e.source.damageType) {
			"inFire", "onFire", "lava" -> Unit
			else -> return
		}
		
		if (!e.entityLiving.isPotionActive(this.id)) return
		
		e.entityLiving.heal(e.ammount)
		e.isCanceled = true
	}
}
