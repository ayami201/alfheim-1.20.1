package alfheim.common.spell.nature

import alexsocol.asjlib.*
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.handler.CardinalSystem.TargetingSystem
import net.minecraft.entity.EntityLivingBase
import kotlin.math.max

object SpellEdgeLife: SpellBase("edgeLife", EnumRace.CAITSITH, 25000, 6600, 1) {
	
	override var duration = 100
	override var efficiency = 0.5
	
	override val usableParams
		get() = arrayOf(duration, efficiency)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val tg = TargetingSystem.getTarget(caster)
		val tgt = tg.target ?: return SpellCastResult.NOTARGET
		
		if (!tg.isParty) return SpellCastResult.WRONGTGT
		
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		
		tgt.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDEdgeLife, duration))
		tgt.health = max(1f, tgt.health * efficiency.F)
		
		return result
	}
}