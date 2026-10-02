package alfheim.common.spell.sound

import alexsocol.asjlib.*
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.handler.CardinalSystem.TargetingSystem
import alfheim.common.potion.PotionEternity
import net.minecraft.entity.EntityLivingBase

object SpellWhisper: SpellBase("whisper", EnumRace.POOKA, 5000, 500, 1) {
	
	override var duration = 100
	
	override val usableParams
		get() = arrayOf(duration)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val tg = TargetingSystem.getTarget(caster)
		val tgt = tg.target ?: return SpellCastResult.NOTARGET
		
		if (tgt === caster || tg.isParty) return SpellCastResult.WRONGTGT
		if (ASJUtilities.isNotInFieldOfVision(tgt, caster)) return SpellCastResult.NOTSEEING
		
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		
		caster.removePotionEffect(AlfheimConfigHandler.potionIDEternity)
		tgt.removePotionEffect(AlfheimConfigHandler.potionIDEternity)
		
		caster.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDEternity, duration, PotionEternity.STUN or PotionEternity.DISABLE))
		tgt.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDEternity, duration, PotionEternity.STUN or PotionEternity.DISABLE))
		
		return result
	}
}
