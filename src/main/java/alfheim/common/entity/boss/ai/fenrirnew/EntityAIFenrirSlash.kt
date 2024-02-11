package alfheim.common.entity.boss.ai.fenrirnew

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.entity.EntityFenrirSlash
import alfheim.common.entity.boss.*

/**
 * Animation:
 * 0-9 - raises front paws
 * 10 - hist with first paw
 * 20 - hits with second paw
 * 21-30 - lands back
 */
class EntityAIFenrirSlash(host: EntityFenrirNew): EntityAIFenrirSkillBase(host, EnumFenrirSkill.SLASH, EnumActionComponents.ATTACK) {
	
	override fun canStartTask() = Vector3.entityDistancePlane(host, host.attackTarget) > 20
	
	override fun continueExecuting() = host.skillTicks <= 30
	
	override fun onTaskTick() {
		host.turnTo(Vector3.fromEntity(host.attackTarget))
		
		val tick = host.skillTicks
		
		if (tick != 10 && tick != 20) return
		
		EntityFenrirSlash(host.worldObj).apply {
			yaw = -host.rotationYaw
			roll = if (tick == 10) -15f else 15f
			
			val (x, _, z) = Vector3.oZ.copy().extend(5).rotateOY(-host.rotationYaw)
			setPosition(host, x, 0.0, z)
			attacker = host
		}.spawn()
	}
	
	override fun getMaxSkillCD() = 300
}
