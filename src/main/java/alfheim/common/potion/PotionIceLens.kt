package alfheim.common.potion

import alexsocol.asjlib.getActivePotionEffect
import alfheim.api.spell.SpellBase
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.spell.water.SpellIceLens
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer

object PotionIceLens: PotionAlfheim(AlfheimConfigHandler.potionIDIceLens, "icelens", false, 0xDDFFFF) {
	
	var tick = 0
	
	override fun isReady(time: Int, amp: Int): Boolean {
		tick = time
		return true
	}
	
	override fun performEffect(target: EntityLivingBase?, amp: Int) {
		if (amp != 1 || target !is EntityPlayer) return
		
		if (!SpellBase.consumeMana(target, if (tick % 2 == 0) 13 else 12, true, SpellIceLens))
			target.getActivePotionEffect(id)?.duration = 1
	}
}
