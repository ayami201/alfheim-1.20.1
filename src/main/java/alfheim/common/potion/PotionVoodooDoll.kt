package alfheim.common.potion

// PORT: импорты 1.20.1 (MAPPING.md); стихии (КТ-4) и заклинание (КТ-7) закомментированы вместе со своими строками
import alexsocol.asjlib.PotionEffectU
import alfheim.api.ModInfo
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.port.legacy.*
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.ai.attributes.AttributeMap as BaseAttributeMap
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.eventbus.api.*
import org.lwjgl.opengl.GL11
//import alfheim.common.core.helper.*
//import alfheim.common.core.util.DamageSourceSpell
//import alfheim.common.spell.darkness.SpellVoodooDoll
//import net.minecraft.entity.*

object PotionVoodooDoll: PotionAlfheim(AlfheimConfigHandler.potionIDVoodooDoll, "voodooDoll", false, 0xD2B10F) {
	
	const val TAG_VOODOO_DOLL = "${ModInfo.MODID}.voodooDoll"
	
	// PORT: entityData (NBT существа Forge 1.7.10) → persistentData, commandSenderName игрока → gameProfile.name
	fun isDoll(target: EntityLivingBase) = target.persistentData.hasKey(TAG_VOODOO_DOLL)
	
	fun getMage(target: EntityLivingBase) = target.persistentData.getString(TAG_VOODOO_DOLL)!!
	
	fun applyTo(target: EntityLivingBase, caster: EntityPlayer, duration: Int) {
		target.addPotionEffect(PotionEffectU(id, duration))
		target.persistentData.setString(TAG_VOODOO_DOLL, caster.gameProfile.name)
	}
	
	override fun removeAttributesModifiersFromEntity(target: EntityLivingBase, map: BaseAttributeMap?, mod: Int) {
		super.removeAttributesModifiersFromEntity(target, map, mod)
		target.persistentData.removeTag(TAG_VOODOO_DOLL)
		
		GL11.GL_GEQUAL
	}
	
	@Suppress("UNCHECKED_CAST")
	@SubscribeEvent(priority = EventPriority.LOWEST)
	fun onDollHurt(e: LivingHurtEvent) {
		// PORT: КТ-4 — стихии урона (ElementalDamage), КТ-7 — урон заклинания «Кукла вуду» (SpellVoodooDoll, DamageSourceSpell)
		/*
		val src = e.source
		val doll = e.entityLiving
		
		if (src.isMagicDamage || !src.isOf(ElementalDamage.COMMON) || !isDoll(doll)) return
		
		val mage = getMage(doll)
		if (mage == "" || src.entity?.commandSenderName != mage) return
		
		(e.entity.worldObj.loadedEntityList as List<Entity>).filter { target ->
			target is EntityLivingBase && PotionVoodooTarget.getMage(target) === mage
		}.forEach { target ->
			val prev = target.hurtResistantTime
			target.hurtResistantTime = 0
			target.attackEntityFrom(DamageSourceSpell.curse, SpellVoodooDoll.damage)
			target.hurtResistantTime = prev
		}
		*/
	}
}
