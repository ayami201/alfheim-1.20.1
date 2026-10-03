package alfheim.common.potion

// PORT: импорты 1.20.1; заклинание (КТ-7) закомментировано вместе со своей строкой
import alfheim.common.core.handler.AlfheimConfigHandler
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
//import alfheim.common.spell.water.SpellWellOLife

object PotionWellOLife: PotionAlfheim(AlfheimConfigHandler.potionIDWellOLife, "wellolife", false, 0x00FFFF) {
	
	override fun isReady(time: Int, ampl: Int) = AlfheimConfigHandler.enableMMO && time % 10 == 0
	
	override fun performEffect(living: EntityLivingBase, ampl: Int) {
		if (!AlfheimConfigHandler.enableMMO) return
		// PORT: КТ-7 — лечение силой заклинания «Колодец жизни» (SpellWellOLife)
//		if (living.isWet) living.heal(SpellWellOLife.damage * (ampl + 1))
	}
}
