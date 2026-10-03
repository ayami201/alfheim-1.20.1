package alfheim.common.potion

// PORT: импорты 1.20.1 (MAPPING.md)
import alexsocol.asjlib.PotionEffectU
import alfheim.api.ModInfo
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.port.legacy.*
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.ai.attributes.AttributeMap as BaseAttributeMap
import net.minecraft.world.entity.player.Player as EntityPlayer

object PotionVoodooTarget: PotionAlfheim(AlfheimConfigHandler.potionIDVoodooTarget, "voodooTarget", true, 0xD2B10F) {
	
	const val TAG_VOODOO_TARGET = "${ModInfo.MODID}.VoodooTarget"
	
	// PORT: entityData (NBT существа Forge 1.7.10) → persistentData, commandSenderName игрока → gameProfile.name
	fun isTarget(target: EntityLivingBase) = target.persistentData.hasKey(TAG_VOODOO_TARGET)
	
	fun getMage(target: EntityLivingBase) = target.persistentData.getString(TAG_VOODOO_TARGET)!!
	
	fun applyTo(target: EntityLivingBase, caster: EntityPlayer, duration: Int) {
		target.addPotionEffect(PotionEffectU(id, duration))
		target.persistentData.setString(TAG_VOODOO_TARGET, caster.gameProfile.name)
	}
	
	override fun isReady(time: Int, ampl: Int) = true
	
	override fun performEffect(target: EntityLivingBase, ampl: Int) {
		target.removePotionEffect(invisibility.id)
	}
	
	override fun removeAttributesModifiersFromEntity(target: EntityLivingBase, map: BaseAttributeMap?, mod: Int) {
		super.removeAttributesModifiersFromEntity(target, map, mod)
		target.persistentData.removeTag(TAG_VOODOO_TARGET)
	}
}
