package alfheim.common.entity.boss.ai.fenrirnew

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.entity.IAncientWolf
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.VisualEffectHandler
import alfheim.common.entity.EntityFenrirSlash
import alfheim.common.entity.boss.EntityFenrirNew
import net.minecraft.entity.EntityLivingBase

class EntityAIFenrirJump(host: EntityFenrirNew): EntityAIFenrirSkillBase(host, EnumFenrirSkill.JUMP, EnumActionComponents.ATTACK, EnumActionComponents.MOTION) {
	
	override fun canStartTask() = host.health < host.maxHealth * 0.8f
	
	override fun onTaskStart() {
		host.motionY += 1
		host.isAirBorne = true
		host.onGround = false
		
		repeat(18) {
			EntityFenrirSlash(host.worldObj).apply {
				yaw = -host.rotationYaw
				val (x, _, z) = Vector3.oZ.copy().extend(5).rotateOY(-host.rotationYaw)
				setPosition(host, x, 0.0, z)
				attacker = host
			}.spawn()
			
			host.rotationYaw += 20f
		}
	}
	
	override fun continueExecuting() = !host.onGround
	
	override fun onTaskFinish() {
		VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.TREMORS, host)
		
		getEntitiesWithinAABB(host.worldObj, EntityLivingBase::class.java, host.boundingBox.expand(16, 4, 16)).forEach {
			if (it is IAncientWolf) return@forEach
			it.knockback(host, 6f)
		}
	}
	
	override fun getMaxSkillCD() = 1200
}
