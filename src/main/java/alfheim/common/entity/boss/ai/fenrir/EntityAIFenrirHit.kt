package alfheim.common.entity.boss.ai.fenrir

import alexsocol.asjlib.PotionEffectU
import alexsocol.asjlib.math.Vector3
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.entity.boss.EntityFenrir
import net.minecraft.util.DamageSource

/**
 * Animation:
 * 0-9 - runs
 * 10-14 - raises paw
 * 15-20 - lowers paw
 */
class EntityAIFenrirHit(host: EntityFenrir): EntityAIFenrirSkillBase(host, EnumFenrirSkill.HIT, EnumActionComponents.ATTACK, EnumActionComponents.MOTION) {
	
	override fun canStartTask() = Vector3.entityDistancePlane(host, host.attackTarget) < 7
	
	override fun onTaskStart() {
		host.turnTo(Vector3.fromEntity(host.attackTarget))
		goto(host.attackTarget, 5)
	}
	
	override fun continueExecuting() = host.skillTicks <= 20
	
	override fun onTaskTick() {
		if (host.skillTicks == 10) return host.navigator.clearPathEntity()
		if (host.skillTicks != 16) return
		if (Vector3.entityDistancePlane(host, host.attackTarget) > 4) return
		if (!host.attackTarget.attackEntityFrom(DamageSource.causeMobDamage(host).setDamageBypassesArmor(), 4f)) return
		
		host.attackTarget.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDBleeding, 100))
	}
	
	override fun getMaxSkillCD() = 100
}
