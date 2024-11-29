package alfheim.common.potion

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.spell.sound.SpellPriorityTarget
import cpw.mods.fml.common.eventhandler.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.ai.attributes.BaseAttributeMap
import net.minecraftforge.event.entity.living.LivingHurtEvent

object PotionPriorityTarget: PotionAlfheim(AlfheimConfigHandler.potionIDPriorityTarget, "priorityTarget", true, 0x004DFF) {
	
	const val TAG_PT = "${ModInfo.MODID}.PriorityTarget"
	
	fun hasPriorityTarget(target: EntityLivingBase) = target.entityData.hasKey(TAG_PT)
	
	fun getPriorityTarget(target: EntityLivingBase) = target.entityData.getString(TAG_PT)
	
	fun applyTo(target: EntityLivingBase, pm: EntityLivingBase, duration: Int) {
		pm.addPotionEffect(PotionEffectU(id, duration))
		pm.entityData.setString(TAG_PT, target.uniqueID.toString())
	}
	
	override fun removeAttributesModifiersFromEntity(target: EntityLivingBase, map: BaseAttributeMap?, mod: Int) {
		super.removeAttributesModifiersFromEntity(target, map, mod)
		target.entityData.removeTag(TAG_PT)
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	fun onPriorityTarget(e: LivingHurtEvent) {
		val attacker = e.source.entity as? EntityLivingBase ?: return
		if (!hasPriorityTarget(attacker)) return
		
		val target = getPriorityTarget(attacker)
		val victim = e.entityLiving.uniqueID.toString()
		
		if (target == victim) {
			e.ammount *= 1 + SpellPriorityTarget.efficiency.F
			attacker.heal(e.ammount * SpellPriorityTarget.efficiency.F)
		} else {
			e.ammount *= 1 - SpellPriorityTarget.efficiency.F
			
			val prev = attacker.hurtResistantTime
			attacker.hurtResistantTime = 0
			attacker.attackEntityFrom(DamageSourceSpell.notPriorityTarget, e.ammount * SpellPriorityTarget.efficiency.F)
			attacker.hurtResistantTime = prev
		}
	}
}
