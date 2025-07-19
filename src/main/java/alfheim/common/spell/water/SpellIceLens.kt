package alfheim.common.spell.water

import alexsocol.asjlib.PotionEffectU
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.core.handler.AlfheimConfigHandler
import net.minecraft.entity.EntityLivingBase

object SpellIceLens: SpellBase("icelens", EnumRace.UNDINE, 100, 1, 5) {
	
	override val usableParams = emptyArray<Any>()
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		if (caster.isPotionActive(AlfheimConfigHandler.potionIDIceLens)) {
			caster.removePotionEffect(AlfheimConfigHandler.potionIDIceLens)
			return SpellCastResult.OK
		}
		
		val result = checkCast(caster)
		if (result == SpellCastResult.OK)
			caster.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDIceLens, Int.MAX_VALUE, 1))
		
		return result
	}
}