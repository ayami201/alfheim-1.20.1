package alfheim.common.potion

// PORT: импорты 1.20.1; заклинание и его урон (КТ-7) закомментированы вместе со своей строкой
import alfheim.common.core.handler.AlfheimConfigHandler
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
//import alfheim.common.core.util.DamageSourceSpell
//import alfheim.common.spell.darkness.SpellDeathMark

object PotionDeathMark: PotionAlfheim(AlfheimConfigHandler.potionIDDeathMark, "deathMark", true, 0x553355) {
	
	override fun isReady(time: Int, ampl: Int) = time == 1
	
	override fun performEffect(living: EntityLivingBase, ampl: Int) {
		// PORT: КТ-7 — урон заклинания «Метка смерти» (SpellDeathMark, DamageSourceSpell.mark)
//		if (AlfheimConfigHandler.enableMMO) living.attackEntityFrom(DamageSourceSpell.mark, SpellDeathMark.damage)
	}
}
