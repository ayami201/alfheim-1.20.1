package alfheim.common.potion

import alfheim.api.AlfheimAPI
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.handler.CardinalSystem.SpellCastingSystem.getCoolDown
import alfheim.common.core.handler.CardinalSystem.SpellCastingSystem.setCoolDown
import alfheim.common.spell.tech.SpellTimeConquest
import cpw.mods.fml.common.eventhandler.*
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.event.entity.living.LivingHurtEvent
import kotlin.math.*

object PotionTimeConquest: PotionAlfheim(AlfheimConfigHandler.potionIDTimeConquest, "timeConquest", false, 0x00FABB) {
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	fun onAttacked(e: LivingHurtEvent) {
		if (e.ammount <= 0 || !e.entityLiving.isPotionActive(this)) return
		
		val attacker = e.source.entity as? EntityPlayerMP ?: return
		
		AlfheimAPI.spells.forEach { spell ->
			if (spell === SpellTimeConquest) return@forEach
			
			val old = getCoolDown(attacker, spell)
			val delta = (spell.getCooldown() * SpellTimeConquest.efficiency).roundToInt()
			val new = max(0, old - delta)
			
			setCoolDown(attacker, spell, new)
		}
	}
}
