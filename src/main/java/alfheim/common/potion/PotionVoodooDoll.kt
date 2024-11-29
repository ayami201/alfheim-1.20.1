package alfheim.common.potion

import alexsocol.asjlib.PotionEffectU
import alfheim.api.ModInfo
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.helper.*
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.spell.darkness.SpellVoodooDoll
import cpw.mods.fml.common.eventhandler.*
import net.minecraft.entity.*
import net.minecraft.entity.ai.attributes.BaseAttributeMap
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.event.entity.living.LivingHurtEvent

object PotionVoodooDoll: PotionAlfheim(AlfheimConfigHandler.potionIDVoodooDoll, "voodooDoll", false, 0xD2B10F) {
	
	const val TAG_VOODOO_DOLL = "${ModInfo.MODID}.voodooDoll"
	
	fun isDoll(target: EntityLivingBase) = target.entityData.hasKey(TAG_VOODOO_DOLL)
	
	fun getMage(target: EntityLivingBase) = target.entityData.getString(TAG_VOODOO_DOLL)!!
	
	fun applyTo(target: EntityLivingBase, caster: EntityPlayer, duration: Int) {
		target.addPotionEffect(PotionEffectU(id, duration))
		target.entityData.setString(TAG_VOODOO_DOLL, caster.commandSenderName)
	}
	
	override fun removeAttributesModifiersFromEntity(target: EntityLivingBase, map: BaseAttributeMap?, mod: Int) {
		super.removeAttributesModifiersFromEntity(target, map, mod)
		target.entityData.removeTag(TAG_VOODOO_DOLL)
	}
	
	@Suppress("UNCHECKED_CAST")
	@SubscribeEvent(priority = EventPriority.LOWEST)
	fun onDollHurt(e: LivingHurtEvent) {
		val src = e.source
		val doll = e.entityLiving
		
		if (src.isMagicDamage || !src.isOf(ElementalDamage.COMMON) || !isDoll(doll)) return
		
		val mage = getMage(doll)
		if (mage == "" || src.entity?.commandSenderName != mage)
		
		(e.entity.worldObj.loadedEntityList as List<Entity>).filter { target ->
			target is EntityLivingBase && PotionVoodooTarget.getMage(target) === mage
		}.forEach { target ->
			val prev = target.hurtResistantTime
			target.hurtResistantTime = 0
			target.attackEntityFrom(DamageSourceSpell.curse, SpellVoodooDoll.damage)
			target.hurtResistantTime = prev
		}
	}
}
