package alfheim.common.spell.tech

import alexsocol.asjlib.ASJUtilities
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.core.handler.CardinalSystem.TargetingSystem
import alfheim.common.potion.PotionTimeAnchor
import net.minecraft.entity.EntityLivingBase

object SpellTimeAnchor: SpellBase("timeAnchor", EnumRace.LEPRECHAUN, 8000, 400, 30) {
	
	override var duration = 60
	
	override val usableParams
		get() = arrayOf(duration)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val tg = TargetingSystem.getTarget(caster)
		val tgt = tg.target ?: return SpellCastResult.NOTARGET
		
		if (tgt !== caster && ASJUtilities.isNotInFieldOfVision(tgt, caster)) return SpellCastResult.NOTSEEING
		
		if (PotionTimeAnchor.hasAnchor(tgt)) return SpellCastResult.WRONGTGT
		
		val result = checkCast(caster)
		if (result == SpellCastResult.OK)
			PotionTimeAnchor.apply(tgt, tg.isParty, duration)
		
		return result
	}
}
