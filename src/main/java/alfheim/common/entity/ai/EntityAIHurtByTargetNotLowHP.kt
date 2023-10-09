package alfheim.common.entity.ai

import net.minecraft.entity.EntityCreature
import net.minecraft.entity.ai.EntityAIHurtByTarget

class EntityAIHurtByTargetNotLowHP(host: EntityCreature, entityCallsForHelp: Boolean): EntityAIHurtByTarget(host, entityCallsForHelp) {
	
	override fun shouldExecute(): Boolean {
		return if (taskOwner.health > taskOwner.maxHealth * 0.25f) super.shouldExecute() else false
	}
	
	override fun continueExecuting(): Boolean {
		return if (taskOwner.health > taskOwner.maxHealth * 0.25f) super.shouldExecute() else false
	}
}
