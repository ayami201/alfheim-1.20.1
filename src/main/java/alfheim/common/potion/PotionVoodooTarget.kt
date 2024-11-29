package alfheim.common.potion

import alexsocol.asjlib.PotionEffectU
import alfheim.api.ModInfo
import alfheim.common.core.handler.AlfheimConfigHandler
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.ai.attributes.BaseAttributeMap
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.potion.Potion

object PotionVoodooTarget: PotionAlfheim(AlfheimConfigHandler.potionIDVoodooTarget, "voodooTarget", true, 0xD2B10F) {
	
	const val TAG_VOODOO_TARGET = "${ModInfo.MODID}.VoodooTarget"
	
	fun isTarget(target: EntityLivingBase) = target.entityData.hasKey(TAG_VOODOO_TARGET)
	
	fun getMage(target: EntityLivingBase) = target.entityData.getString(TAG_VOODOO_TARGET)!!
	
	fun applyTo(target: EntityLivingBase, caster: EntityPlayer, duration: Int) {
		target.addPotionEffect(PotionEffectU(id, duration))
		target.entityData.setString(TAG_VOODOO_TARGET, caster.commandSenderName)
	}
	
	override fun isReady(time: Int, ampl: Int) = true
	
	override fun performEffect(target: EntityLivingBase, ampl: Int) {
		target.removePotionEffect(Potion.invisibility.id)
	}
	
	override fun removeAttributesModifiersFromEntity(target: EntityLivingBase, map: BaseAttributeMap?, mod: Int) {
		super.removeAttributesModifiersFromEntity(target, map, mod)
		target.entityData.removeTag(TAG_VOODOO_TARGET)
	}
}
