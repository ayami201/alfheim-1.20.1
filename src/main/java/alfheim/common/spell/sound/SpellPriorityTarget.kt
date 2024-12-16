package alfheim.common.spell.sound

import alexsocol.asjlib.ASJUtilities
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.core.handler.CardinalSystem
import alfheim.common.core.handler.CardinalSystem.TargetingSystem
import alfheim.common.potion.PotionPriorityTarget
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer

object SpellPriorityTarget: SpellBase("priorityTarget", EnumRace.POOKA, 10000, 900, 20) {
	
	override var duration = 200
	override var efficiency = 0.25
	
	override val usableParams
		get() = arrayOf(duration, efficiency)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val tg = TargetingSystem.getTarget(caster)
		val tgt = tg.target ?: return SpellCastResult.NOTARGET
		
		if (tgt === caster || tg.isParty) return SpellCastResult.WRONGTGT
		if (ASJUtilities.isNotInFieldOfVision(tgt, caster)) return SpellCastResult.NOTSEEING
		
		val party = if (caster is EntityPlayer)
			CardinalSystem.PartySystem.getParty(caster)
		else
			CardinalSystem.PartySystem.getMobParty(caster) ?: return SpellCastResult.WRONGTGT
		
		if (party.count < 2) return SpellCastResult.WRONGTGT
		
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		
		repeat(party.count) {
			val pm = party[it] ?: return@repeat
			if (pm === caster) return@repeat
			PotionPriorityTarget.applyTo(tgt, pm, duration) }
		
		return result
	}
}
