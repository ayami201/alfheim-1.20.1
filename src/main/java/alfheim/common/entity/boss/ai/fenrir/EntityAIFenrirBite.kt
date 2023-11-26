package alfheim.common.entity.boss.ai.fenrir

import alexsocol.asjlib.math.Vector3
import alfheim.common.entity.boss.EntityFenrir
import net.minecraft.potion.*
import net.minecraft.util.DamageSource

/**
 * Animation:
 * 0-8 - rotates head
 * 9-11 - closes jaws
 * 12-20 - rotates back
 */
class EntityAIFenrirBite(host: EntityFenrir): EntityAIFenrirSkillBase(host, EnumFenrirSkill.BITE, EnumActionComponents.ATTACK) {
	
	override fun canStartTask() = Vector3.entityDistancePlane(host, host.attackTarget) < 5
	
	override fun onTaskStart() {
		host.turnTo(Vector3.fromEntity(host.attackTarget))
	}
	
	override fun continueExecuting() = host.skillTicks <= 20
	
	override fun onTaskTick() {
		if (host.skillTicks != 10) return
		if (Vector3.entityDistancePlane(host, host.attackTarget) > 3) return
		if (!host.attackTarget.attackEntityFrom(DamageSource.causeMobDamage(host), 10f)) return
		
		host.attackTarget.addPotionEffect(PotionEffect(Potion.moveSlowdown.id, 100, 1))
	}
	
	override fun getMaxSkillCD() = 200
}
