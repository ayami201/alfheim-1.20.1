package alfheim.common.spell.nature

import alexsocol.asjlib.PotionEffectU
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.handler.CardinalSystem.TargetingSystem
import net.minecraft.entity.EntityLivingBase

object SpellHystrix: SpellBase("hystrix", EnumRace.CAITSITH, 10000, 360, 60) {
	
	override var damage = 4f  
	override var duration = 100
	override var radius = 5.0
	
	override val usableParams
		get() = arrayOf(damage, duration, radius)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val tg = TargetingSystem.getTarget(caster)
		if (tg.target == null) return SpellCastResult.NOTARGET
		
		if (!tg.isParty) return SpellCastResult.WRONGTGT
		
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		
		tg.target.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDHystrix, duration))
		
		return result
	}
}
