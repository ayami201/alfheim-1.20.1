package alfheim.common.potion

import alexsocol.asjlib.onEach
import alfheim.common.core.handler.AlfheimConfigHandler
import net.minecraft.entity.EntityLivingBase
import net.minecraft.potion.*

object PotionChampagne: PotionAlfheim(AlfheimConfigHandler.potionIDChampagne, "champagne", false, 0xFFFFE5)  {
	
	override fun isReady(time: Int, amp: Int) = true
	
	override fun performEffect(target: EntityLivingBase, amp: Int) {
		target.activePotionEffects.iterator().onEach { it as PotionEffect
			if (!Potion.potionTypes[it.potionID].isBadEffect) return@onEach
			
			remove()
			target.onFinishedPotionEffect(it)
		}
	}
}
