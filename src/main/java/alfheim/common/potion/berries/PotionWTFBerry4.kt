package alfheim.common.potion.berries

// PORT: импорты 1.20.1; холод миров (КТ-6) закомментирован вместе со своими строками
import alfheim.common.core.handler.*
import alfheim.common.potion.PotionAlfheim
import alfheim.port.legacy.*
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.eventbus.api.*
//import kotlin.math.min

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
	
	// PORT: КТ-6 — холод Нифльхейма (SheerColdHandler)
	/*
	@SubscribeEvent(priority = EventPriority.LOW)
	fun keepPlayerWarm(e: SheerColdHandler.SheerColdTickEvent) {
		if (!e.entityLiving.isPotionActive(this.id)) return
		if (e.delta == null) return
		e.delta = min(e.delta!!, 0f) // minimal so that if other source heats - it won't override
	}
	*/
}