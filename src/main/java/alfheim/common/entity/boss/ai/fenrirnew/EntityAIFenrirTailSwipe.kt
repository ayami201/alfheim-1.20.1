package alfheim.common.entity.boss.ai.fenrirnew

import alexsocol.asjlib.knockback
import alexsocol.asjlib.math.Vector3
import alfheim.common.entity.boss.*
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.util.DamageSource

/**
 * 0-18 rotates
 * 19-20 stops
 */
class EntityAIFenrirTailSwipe(host: EntityFenrirNew): EntityAIFenrirSkillBase(host, EnumFenrirSkill.TAIL, EnumActionComponents.ATTACK) {
	
	override fun canStartTask() = Vector3.entityDistancePlane(host, host.attackTarget) < 5
	
	override fun onTaskStart() {
		host.turnTo(Vector3.fromEntity(host.attackTarget))
	}
	
	override fun continueExecuting() = host.skillTicks <= 20
	
	override fun onTaskTick() {
		if (host.skillTicks != 18) return
		
		val target = host.attackTarget
		if (Vector3.entityDistancePlane(host, target) > 5) return
		if (!target.attackEntityFrom(DamageSource.causeMobDamage(host), 5f)) return
		
		target.knockback(host, 3f)
		if (target is EntityPlayerMP) target.playerNetServerHandler.sendPacket(S12PacketEntityVelocity(target))
	}
	
	override fun getMaxSkillCD() = 400
}
