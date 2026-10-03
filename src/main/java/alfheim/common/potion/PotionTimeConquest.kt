package alfheim.common.potion

// PORT: импорты 1.20.1; заклинания и их перезарядка (КТ-7) закомментированы вместе со своими строками
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.port.legacy.*
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.eventbus.api.*
//import alexsocol.asjlib.D
//import alfheim.api.AlfheimAPI
//import alfheim.common.core.handler.CardinalSystem.SpellCastingSystem
//import alfheim.common.network.*
//import alfheim.common.network.packet.Message2d
//import alfheim.common.spell.tech.SpellTimeConquest
//import kotlin.math.*

object PotionTimeConquest: PotionAlfheim(AlfheimConfigHandler.potionIDTimeConquest, "timeConquest", false, 0x00FABB) {
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	fun onAttacked(e: LivingHurtEvent) {
		if (e.ammount <= 0 || !e.entityLiving.isPotionActive(this)) return
		
		val attacker = e.source.entity as? EntityPlayerMP ?: return
		
		// PORT: КТ-7 — перезарядка заклинаний (AlfheimAPI.spells, CardinalSystem.SpellCastingSystem, SpellTimeConquest)
		/*
		AlfheimAPI.spells.forEach { spell ->
			if (spell === SpellTimeConquest || spell.hard) return@forEach
			
			val old = SpellCastingSystem.getCoolDown(attacker, spell)
			if (old <= 0) return@forEach
			
			val delta = (spell.getCooldown() * SpellTimeConquest.efficiency).roundToInt()
			val new = max(0, old - delta)
			
			SpellCastingSystem.setCoolDown(attacker, spell, new)
			NetworkService.sendTo(Message2d(M2d.COOLDOWN, (spell.race.ordinal and 0xF shl 28 or (AlfheimAPI.getSpellID(spell) and 0xFFFFFFF)).D, new.D), attacker)
		}
		*/
	}
}
