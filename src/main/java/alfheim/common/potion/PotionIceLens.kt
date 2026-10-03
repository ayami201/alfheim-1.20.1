package alfheim.common.potion

// PORT: импорты 1.20.1; заклинание (КТ-7) закомментировано вместе со своей строкой
import alexsocol.asjlib.getActivePotionEffect
import alfheim.common.core.handler.AlfheimConfigHandler
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
//import alfheim.api.spell.SpellBase
//import alfheim.common.spell.water.SpellIceLens

object PotionIceLens: PotionAlfheim(AlfheimConfigHandler.potionIDIceLens, "icelens", false, 0xDDFFFF) {
	
	var tick = 0
	
	override fun isReady(time: Int, amp: Int): Boolean {
		tick = time
		return true
	}
	
	override fun performEffect(target: EntityLivingBase?, amp: Int) {
		if (amp != 1 || target !is EntityPlayer) return
		
		// PORT: КТ-7 — плата маной заклинания «Ледяная линза» (SpellBase, SpellIceLens)
//		if (!SpellBase.consumeMana(target, if (tick % 2 == 0) 13 else 12, true, SpellIceLens))
//			target.getActivePotionEffect(id)?.duration = 1
	}
}
