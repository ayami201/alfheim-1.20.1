package alfheim.common.potion

// PORT: импорты 1.20.1 (MAPPING.md); поля событий и существ 1.7.10 — alfheim.port.legacy
import alexsocol.asjlib.*
import alfheim.common.core.handler.*
import alfheim.port.legacy.*
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.ai.attributes.*
import net.minecraft.world.entity.ai.attributes.AttributeMap as BaseAttributeMap
import net.minecraftforge.event.entity.living.*
import net.minecraftforge.eventbus.api.*
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
