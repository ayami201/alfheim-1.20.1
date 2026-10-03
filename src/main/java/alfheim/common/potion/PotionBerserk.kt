package alfheim.common.potion

// PORT: импорты 1.20.1 (MAPPING.md); SharedMonsterAttributes и сеттеры атрибутов 1.7.10 — alfheim.port.legacy
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.port.legacy.*
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.ai.attributes.*
import net.minecraft.world.entity.ai.attributes.AttributeMap as BaseAttributeMap
import java.util.*

object PotionBerserk: PotionAlfheim(AlfheimConfigHandler.potionIDBerserk, "berserk", false, 0xAA1111) {
	
	val uuid = UUID.fromString("23593E88-FFE3-1707-DE94-6BCD1756B35D")!!
	
	override fun applyAttributesModifiersToEntity(target: EntityLivingBase, map: BaseAttributeMap, mod: Int) {
		super.applyAttributesModifiersToEntity(target, map, mod)
		val m = AttributeModifier(uuid, name, -0.2, 2)
		target.getEntityAttribute(SharedMonsterAttributes.maxHealth).removeModifier(m)
		target.getEntityAttribute(SharedMonsterAttributes.maxHealth).applyModifier(m)
		target.health = target.health.coerceAtMost(target.maxHealth)
	}
	
	override fun removeAttributesModifiersFromEntity(target: EntityLivingBase, map: BaseAttributeMap, mod: Int) {
		super.removeAttributesModifiersFromEntity(target, map, mod)
		target.getEntityAttribute(SharedMonsterAttributes.maxHealth).removeModifier(target.getEntityAttribute(SharedMonsterAttributes.maxHealth).getModifier(uuid) ?: return)
	}
}
