package alfheim.common.potion

// PORT: импорты 1.20.1
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.port.legacy.*
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
//import alexsocol.asjlib.onEach
//import net.minecraft.potion.*

object PotionChampagne: PotionAlfheim(AlfheimConfigHandler.potionIDChampagne, "champagne", false, 0xFFFFE5)  {
	
	override fun isReady(time: Int, amp: Int) = true
	
	// PORT: remove() из списка и onFinishedPotionEffect — removeEffect 1.20.1 (он делает и то и другое), как у плода
	// Иггдрасиля (ItemElvenResource)
	override fun performEffect(target: EntityLivingBase, amp: Int) {
		target.activePotionEffects.toList().forEach {
			if (!potionTypes[it.potionID].isBadEffect) return@forEach
			
			target.removeEffect(it.effect)
		}
//		target.activePotionEffects.iterator().onEach { it as PotionEffect
//			if (!potionTypes[it.potionID].isBadEffect) return@onEach
//			
//			remove()
//			target.onFinishedPotionEffect(it)
//		}
	}
}
