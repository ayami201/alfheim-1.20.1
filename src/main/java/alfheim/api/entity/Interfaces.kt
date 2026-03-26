package alfheim.api.entity

import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.helper.*
import alfheim.common.item.equipment.bauble.ItemPendant
import alfheim.common.item.equipment.bauble.ItemPendant.Companion.EnumPrimalWorldType
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.AxisAlignedBB
import java.util.*

interface IAncientWolf

interface IMuspelheimEntity: IElementalEntity {
	override val elements get() = EnumSet.of(ElementalDamage.FIRE)!!
	
	companion object {
		fun checkProtection(entity: Entity, manacost: Int): Boolean {
			if (entity is IMuspelheimEntity) return true
			if (entity is EntityPlayer && (entity.capabilities.isCreativeMode || ItemPendant.canProtect(entity, EnumPrimalWorldType.MUSPELHEIM, manacost))) return true
			return EntityList.getEntityString(entity) in AlfheimConfigHandler.mobBlacklistHot
		}
	}
}

interface INiflheimEntity: IElementalEntity {
	override val elements get() = EnumSet.of(ElementalDamage.ICE)!!
	
	companion object {
		fun checkProtection(entity: Entity, manacost: Int): Boolean {
			if (entity is INiflheimEntity) return true
			if (entity is EntityPlayer && (entity.capabilities.isCreativeMode || ItemPendant.canProtect(entity, EnumPrimalWorldType.NIFLHEIM, manacost))) return true
			return EntityList.getEntityString(entity) in AlfheimConfigHandler.mobBlacklistCold
		}
	}
}

interface IIntersectAttackEntity {
	fun getExtraReach(): Double
	
	/**
	 * Check if this entity is an allie for [e] so it won't set it as attack target
	 */
	fun isAllie(e: Entity?): Boolean
}

interface IMulticollidableEntity {
	fun getAdditionalCollisions(target: AxisAlignedBB): List<AxisAlignedBB>
}