package alfheim.common.entity.ai

import alexsocol.asjlib.math.Vector3
import net.minecraft.entity.*
import net.minecraft.entity.ai.*
import net.minecraft.pathfinding.PathEntity

class EntityAIFleeOnLowHP(val host: EntityCreature, val distanceToRun: Float): EntityAIBase() {
	
	var target: EntityLivingBase? = null
	var path: PathEntity? = null
	
	init {
		mutexBits = 1
	}
	
	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	override fun shouldExecute(): Boolean {
		if (target == null || host.aiTarget != null) {
			this.target = host.aiTarget
		}
		
		val target = target
		
		if (host.health > host.maxHealth * 0.25f || target == null) return false
		
		if (Vector3.entityDistance(target, host) > distanceToRun) return false
		
		val vec3 = RandomPositionGenerator.findRandomTargetBlockAwayFrom(host, 16, 7, Vector3.fromEntity(target).toVec3())
		
		return when {
			vec3 == null                                                                                     -> false
			target.getDistanceSq(vec3.xCoord, vec3.yCoord, vec3.zCoord) < target.getDistanceSqToEntity(host) -> false
			else                                                                                             -> {
				path = host.navigator.getPathToXYZ(vec3.xCoord, vec3.yCoord, vec3.zCoord)
				if (path == null) false else path!!.isDestinationSame(vec3)
			}
		}
	}
	
	override fun startExecuting() {
		host.navigator.setPath(path, host.getEntityAttribute(SharedMonsterAttributes.movementSpeed).attributeValue * 3)
		host.setRevengeTarget(null)
	}
	
	override fun continueExecuting() = !host.navigator.noPath() && host.health <= host.maxHealth * 0.25f && target?.isEntityAlive == true
}
