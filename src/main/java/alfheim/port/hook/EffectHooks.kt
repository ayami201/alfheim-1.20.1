package alfheim.port.hook

import alexsocol.asjlib.PotionEffectU
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.port.legacy.*
import alfheim.port.legacy.Potion1710 as Potion
import net.minecraft.world.effect.*
import net.minecraft.world.entity.LivingEntity as EntityLivingBase

/**
 * Врезки автора в эффекты существа (HOOKS.md), миксин `alfheim.port.mixin.LivingEntityMixin`: зелье «Танк»
 * (`PotionTank`) засчитывается как «Сопротивление», его сила прибавляется к силе сопротивления
 */
object EffectHooks {
	
	// PORT: activePotionsMap[id] → activeEffectsMap[зелье]
	
	/** H-008 (AlfheimHookHandler.isPotionActive) */
	@JvmStatic
	fun isPotionActive(e: EntityLivingBase, p: MobEffect) =
		if (p === Potion.resistance) {
			e.activeEffectsMap.containsKey(Potion.resistance) || e.activeEffectsMap.containsKey(Potion.byId(AlfheimConfigHandler.potionIDTank))
		} else e.activeEffectsMap.containsKey(p)
	
	/**
	 * H-009 (AlfheimHookHandler.getActivePotionEffect). Сила «Танка» прибавляется к самому эффекту сопротивления на
	 * существе, если он есть: при каждом вызове (у каждого удара) сопротивление растёт — так у автора
	 */
	@JvmStatic
	fun getActivePotionEffect(e: EntityLivingBase, p: MobEffect): MobEffectInstance? {
		var pe = e.activeEffectsMap[p]
		if (p !== Potion.resistance || !e.isPotionActive(AlfheimConfigHandler.potionIDTank)) return pe
		
		val tank = e.activeEffectsMap[Potion.byId(AlfheimConfigHandler.potionIDTank)]!!
		if (pe == null) pe = PotionEffectU(Potion.resistance.id, tank.duration)
		pe.amplifier += tank.amplifier
		
		return pe
	}
}
