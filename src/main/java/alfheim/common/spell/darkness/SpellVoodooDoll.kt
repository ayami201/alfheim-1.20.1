package alfheim.common.spell.darkness

import alexsocol.asjlib.ASJUtilities
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.core.handler.CardinalSystem.TargetingSystem
import alfheim.common.potion.PotionVoodooDoll
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer

object SpellVoodooDoll: SpellBase("voodooDoll", EnumRace.IMP, 2500, 20, 1) {
	
	override var damage = 10f
	override var duration = 1200
	
	override val usableParams
		get() = arrayOf(duration, damage)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		if (caster !is EntityPlayer) return SpellCastResult.NOTALLOW
		
		val tg = TargetingSystem.getTarget(caster)
		val tgt = tg.target ?: return SpellCastResult.NOTARGET
		
		if (tgt === caster || tg.isParty || PotionVoodooDoll.isDoll(tgt)) return SpellCastResult.WRONGTGT
		if (ASJUtilities.isNotInFieldOfVision(tgt, caster)) return SpellCastResult.NOTSEEING
		
		val result = checkCast(caster)
		if (result == SpellCastResult.OK)
			PotionVoodooDoll.applyTo(tgt, caster, duration)
		
		return result
	}
}