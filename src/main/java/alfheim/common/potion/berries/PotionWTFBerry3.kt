package alfheim.common.potion.berries

// PORT: импорты 1.20.1; урон заклинаний (КТ-7) закомментирован вместе со своей строкой
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.potion.PotionAlfheim
import alfheim.port.legacy.*
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
//import alfheim.common.core.util.DamageSourceSpell

object PotionWTFBerry3: PotionAlfheim(AlfheimConfigHandler.potionIDWtfBerry3, "WTFBerry3", false, 0x744679) {
	
	@SubscribeEvent
	fun onPlayerAttacked(e: LivingHurtEvent) {
		if (e.source.damageType == "lightningShieldEffect") return // Stack overflow fix
		
		val attacker = e.source.entity as? EntityLivingBase ?: return
		if (!e.entityLiving.isPotionActive(this.id)) return
		
		// PORT: КТ-7 — ответный удар молнией (DamageSourceSpell.lightningShield)
//		attacker.attackEntityFrom(DamageSourceSpell.lightningShield(e.entityLiving), e.ammount * 0.15f)
		e.ammount *= 0.85f
	}
}
