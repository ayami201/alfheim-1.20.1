package alfheim.common.spell.illusion

import alexsocol.asjlib.ASJUtilities
import alexsocol.asjlib.math.Vector3
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.core.handler.CardinalSystem
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer

object SpellJoin: SpellBase("join", EnumRace.SPRIGGAN, 10000, 1200, 30) {
	
	override val usableParams
		get() = emptyArray<Any>()
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val tg = CardinalSystem.TargetingSystem.getTarget(caster)
		
		val tgt: EntityLivingBase
		
		if (tg.isParty) {
			val pt = CardinalSystem.PartySystem.getMobParty(caster) ?: return SpellCastResult.NOTARGET
			tgt = pt[tg.partyIndex] ?: return SpellCastResult.WRONGTGT
		} else {
			tgt = tg.target ?: return SpellCastResult.NOTARGET
			if (ASJUtilities.isNotInFieldOfVision(tgt, caster)) return SpellCastResult.NOTSEEING
		}
		
		if (tgt === caster) return SpellCastResult.WRONGTGT
		
		if (tgt !is EntityPlayer && tgt.dimension != caster.dimension) return SpellCastResult.WRONGTGT
		
		val (tx, ty, tz) = Vector3.fromEntity(tgt)
		
		val result = checkCast(caster)
		if (result == SpellCastResult.OK)
			ASJUtilities.sendToDimensionWithoutPortal(caster, tgt.dimension, tx, ty, tz)
		
		return result
	}
}