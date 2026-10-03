package alfheim.common.potion

// PORT: импорты 1.20.1 (MAPPING.md); SharedMonsterAttributes и сеттеры атрибутов 1.7.10 — alfheim.port.legacy
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.port.legacy.*
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.ai.attributes.*
import net.minecraft.world.entity.ai.attributes.AttributeMap as BaseAttributeMap
import java.util.*

object PotionNinja: PotionAlfheim(AlfheimConfigHandler.potionIDNinja, "ninja", false, 0xCCCCCC) {
	
	val uuid = UUID.fromString("3899DCBA-B79F-92AF-727C-2190BBD8ABC5")!!
	
	override fun applyAttributesModifiersToEntity(target: EntityLivingBase?, map: BaseAttributeMap, mod: Int) {
		super.applyAttributesModifiersToEntity(target, map, mod)
		val m = AttributeModifier(uuid, "ninja", 0.2, 2)
		target!!.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(m)
		target.getEntityAttribute(SharedMonsterAttributes.movementSpeed).applyModifier(m)
	}
	
	override fun removeAttributesModifiersFromEntity(target: EntityLivingBase?, map: BaseAttributeMap, mod: Int) {
		super.removeAttributesModifiersFromEntity(target, map, mod)
		val m = target!!.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getModifier(uuid)
		if (m != null) target.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(m)
	}
}
