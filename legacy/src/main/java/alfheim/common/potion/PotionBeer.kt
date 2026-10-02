package alfheim.common.potion

import alexsocol.asjlib.*
import alfheim.common.core.handler.*
import cpw.mods.fml.common.eventhandler.*
import net.minecraft.entity.*
import net.minecraft.entity.ai.attributes.*
import net.minecraftforge.event.entity.living.*
import kotlin.math.min

object PotionBeer: PotionAlfheim(AlfheimConfigHandler.potionIDBeer, "beer", false, 0xFF8000) {
	
	init {
		func_111184_a(SharedMonsterAttributes.maxHealth, "89EF8E75-5F0E-48B6-BC39-497B7A30CFA6", 0.0, 0)
		eventForge()
	}
	
	override fun applyAttributesModifiersToEntity(target: EntityLivingBase, attributes: BaseAttributeMap?, amp: Int) {
		super.applyAttributesModifiersToEntity(target, attributes, amp)
		target.heal(func_111183_a(amp, func_111186_k().values.first() as AttributeModifier).F)
	}
	
	override fun func_111183_a(amp: Int, mod: AttributeModifier) = when (amp) {
		0    -> 3
		1    -> 5
		2    -> 8
		3    -> 13
		else -> 21
	}.D
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun onLivingHurt(e: LivingAttackEvent) {
		val attacker = e.source.entity as? EntityLivingBase ?: return
		
		val attackerChance = attacker.getActivePotionEffect(this)?.amplifier?.plus(1)?.times(10) ?: 0
		val targetChance = e.entityLiving.getActivePotionEffect(this)?.amplifier?.plus(1)?.times(10) ?: 0
		
		if (!ASJUtilities.chance(min(50, attackerChance) + min(50, targetChance))) return
		
		e.isCanceled = true
		e.entityLiving.hurtResistantTime = e.entityLiving.maxHurtResistantTime
		e.entityLiving.lastDamage = e.ammount
	}
}
