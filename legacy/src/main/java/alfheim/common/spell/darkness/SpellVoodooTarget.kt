package alfheim.common.spell.darkness

import alexsocol.asjlib.ASJUtilities
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.core.handler.CardinalSystem.TargetingSystem
import alfheim.common.potion.PotionVoodooTarget
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer

object SpellVoodooTarget: SpellBase("voodooTarget", EnumRace.IMP, 15000, 1200, 40) {
	
	override var duration = 160
	
	override val usableParams
		get() = arrayOf(duration)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		if (caster !is EntityPlayer) return SpellCastResult.NOTALLOW
		
		val tg = TargetingSystem.getTarget(caster)
		val tgt = tg.target ?: return SpellCastResult.NOTARGET
		
		if (tgt === caster || tg.isParty || PotionVoodooTarget.isTarget(tgt)) return SpellCastResult.WRONGTGT
		if (ASJUtilities.isNotInFieldOfVision(tgt, caster)) return SpellCastResult.NOTSEEING
		
		val result = checkCast(caster)
		if (result == SpellCastResult.OK)
			PotionVoodooTarget.applyTo(tgt, caster, duration)
		
		return result
	}
}
