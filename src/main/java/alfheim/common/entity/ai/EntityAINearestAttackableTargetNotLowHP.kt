package alfheim.common.entity.ai

import net.minecraft.command.IEntitySelector
import net.minecraft.entity.*
import net.minecraft.entity.ai.EntityAINearestAttackableTarget

class EntityAINearestAttackableTargetNotLowHP(
	host: EntityCreature,
	targetClass: Class<out Entity>,
	targetChance: Int,
	shouldCheckSight: Boolean,
	nearbyOnly: Boolean = false,
	targetEntitySelector: IEntitySelector? = null
): EntityAINearestAttackableTarget(host, targetClass, targetChance, shouldCheckSight, nearbyOnly, targetEntitySelector) {
	
	override fun shouldExecute(): Boolean {
		return if (taskOwner.health > taskOwner.maxHealth * 0.25f) super.shouldExecute() else false
	}
	
	override fun continueExecuting(): Boolean {
		return if (taskOwner.health > taskOwner.maxHealth * 0.25f) super.continueExecuting() else false
	}
}
