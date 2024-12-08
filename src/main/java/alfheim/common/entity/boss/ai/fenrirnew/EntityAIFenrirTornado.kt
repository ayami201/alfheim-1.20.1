package alfheim.common.entity.boss.ai.fenrirnew

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.entity.EntityTornado
import alfheim.common.entity.boss.EntityFenrirNew

class EntityAIFenrirTornado(host: EntityFenrirNew): EntityAIFenrirSkillBase(host, EnumFenrirSkill.TORNADO, EnumActionComponents.ATTACK) {
	
	override fun canStartTask() = Vector3.entityDistancePlane(host, host.attackTarget) > 20
	
	override fun onTaskStart() {
		host.turnTo(Vector3.fromEntity(host.attackTarget))
		
		host.rotationYaw -= 30f
		
		repeat(5) {
			EntityTornado(host.worldObj).apply {
				val (x, _, z) = Vector3.oZ.copy().extend(5).rotateOY(-host.rotationYaw)
				setPosition(host, x, 0.0, z)
				attacker = host
				target = host.attackTarget
				delay = it * 50
			}.spawn()
			
			host.rotationYaw += 15f
		}
		
		host.rotationYaw -= 30f
	}
	
	override fun getMaxSkillCD() = 600
}
