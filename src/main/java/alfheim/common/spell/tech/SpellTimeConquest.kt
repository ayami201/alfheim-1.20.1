package alfheim.common.spell.tech

import alexsocol.asjlib.*
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.handler.CardinalSystem.TargetingSystem
import net.minecraft.entity.EntityLivingBase

object SpellTimeConquest: SpellBase("timeConquest", EnumRace.LEPRECHAUN, 50000, 1500, 50) {
	
	override var duration = 50
	override var efficiency = 0.05
	
	override val usableParams
		get() = arrayOf(duration, efficiency)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val tg = TargetingSystem.getTarget(caster)
		val tgt = tg.target ?: return SpellCastResult.NOTARGET
		
		if (tgt !== caster && ASJUtilities.isNotInFieldOfVision(tgt, caster)) return SpellCastResult.NOTSEEING
		
		val result = checkCast(caster)
		if (result == SpellCastResult.OK)
			tgt.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDTimeConquest, duration))
		
		return result
	}
}
