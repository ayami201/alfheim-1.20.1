package alfheim.common.potion.berries

import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.potion.PotionAlfheim
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.event.entity.living.LivingHurtEvent

object PotionWTFBerry3: PotionAlfheim(AlfheimConfigHandler.potionIDWtfBerry3, "WTFBerry3", false, 0x744679) {
	
	@SubscribeEvent
	fun onPlayerAttacked(e: LivingHurtEvent) {
		if (e.source.damageType == "lightningShieldEffect") return // Stack overflow fix
		
		val attacker = e.source.entity as? EntityLivingBase ?: return
		if (!e.entityLiving.isPotionActive(this.id)) return
		
		attacker.attackEntityFrom(DamageSourceSpell.lightningShield(e.entityLiving), e.ammount * 0.15f)
		e.ammount *= 0.85f
	}
}
