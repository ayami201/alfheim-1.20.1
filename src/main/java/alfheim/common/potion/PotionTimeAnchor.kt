package alfheim.common.potion

// PORT: импорты 1.20.1 (MAPPING.md); NBT 1.7.10 — alfheim.port.legacy
import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.ModInfo
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.port.legacy.*
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.ai.attributes.AttributeMap as BaseAttributeMap
import kotlin.math.*

object PotionTimeAnchor: PotionAlfheim(AlfheimConfigHandler.potionIDTimeAnchor, "timeAnchor", false, 0xFFD400) {
	
	const val TAG_TIME_ANCHOR = "${ModInfo.MODID}.TimeAnchor"
	const val TAG_HP = "hp"
	const val TAG_D = "d"
	const val TAG_X = "x"
	const val TAG_Y = "y"
	const val TAG_Z = "z"
	
	// PORT: entityData (NBT существа Forge 1.7.10) → persistentData
	fun hasAnchor(target: EntityLivingBase) = target.persistentData.hasKey(TAG_TIME_ANCHOR)
	
	fun apply(target: EntityLivingBase, isParty: Boolean, duration: Int) {
		target.addPotionEffect(PotionEffectU(id, duration, if (isParty) 1 else 0))
		
		val anchor = NBTTagCompound()
		
		// PORT: номеров измерений в 1.20.1 нет — id измерения строкой
		anchor.setString(TAG_D, target.level().dimension().location().toString())
//		anchor.setInteger(TAG_D, target.dimension)
		val (x, y, z) = Vector3.fromEntity(target)
		anchor.setDouble(TAG_X, x)
		anchor.setDouble(TAG_Y, y)
		anchor.setDouble(TAG_Z, z)
		anchor.setFloat(TAG_HP, target.health)
		
		target.persistentData.setTag(TAG_TIME_ANCHOR, anchor)
	}
	
	override fun removeAttributesModifiersFromEntity(target: EntityLivingBase, map: BaseAttributeMap?, amp: Int) {
		super.removeAttributesModifiersFromEntity(target, map, amp)
		
		if (!hasAnchor(target)) return
		
		val anchor = target.persistentData.getCompoundTag(TAG_TIME_ANCHOR)
		
		val hp = anchor.getFloat(TAG_HP)
		target.health = if (amp == 0) min(target.health, hp) else max(target.health, hp)
		// PORT: КТ-7 — возврат на якорь; ASJUtilities.sendToDimensionWithoutPortal переносится с первым, кому нужна
//		ASJUtilities.sendToDimensionWithoutPortal(target, anchor.getInteger(TAG_D), anchor.getDouble(TAG_X), anchor.getDouble(TAG_Y), anchor.getDouble(TAG_Z))
		
		target.persistentData.removeTag(TAG_TIME_ANCHOR)
	}
}
