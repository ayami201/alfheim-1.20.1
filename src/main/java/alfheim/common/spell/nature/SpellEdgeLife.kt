package alfheim.common.spell.nature

import alexsocol.asjlib.*
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.core.handler.AlfheimConfigHandler
import net.minecraft.entity.EntityLivingBase
import kotlin.math.max

object SpellEdgeLife: SpellBase("edgeLife", EnumRace.CAITSITH, 25000, 1200, 1) {
	
	override var duration = 100
	override var efficiency = 0.5
	
	override val usableParams
		get() = arrayOf(duration, efficiency)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		
		caster.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDEdgeLife, duration))
		caster.health = max(1f, caster.health * efficiency.F)
		
		return result
	}
}