package alfheim.common.entity.boss.ai.fenrir

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.entity.IAncientWolf
import alfheim.common.entity.EntityFenrirDome
import alfheim.common.entity.boss.EntityFenrir
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.DamageSource

class EntityAIFenrirOnslaught(host: EntityFenrir): EntityAIFenrirSkillBase(host, EnumFenrirSkill.ONSLAUGHT, EnumActionComponents.ATTACK, EnumActionComponents.MOTION) {
	
	var playerPos = Vector3()
	var hasLeaped = false
	var hasNavigated = false
	var timesFinished = 0
	
	override fun canStartTask() = host.health < host.maxHealth * 0.8f
	
	override fun onTaskStart() {
		val pos = Vector3.fromEntity(host.attackTarget)
		playerPos.set(pos).mul(1, 0, 1)
		EntityFenrirDome(host.worldObj).apply {
			val (x, _, z) = pos
			setPosition(x, 64.0, z)
		}.spawn()
	}
	
	override fun continueExecuting() = timesFinished < 2
	
	override fun onTaskTick() {
		if (!hasLeaped) {
			hasLeaped = true
			val center = Vector3(host.source).mul(1, 0, 1)
			val (x, _, z) = playerPos.copy().sub(center).normalize().mul((RADIUS - 2) * if (timesFinished == 0) 1 else -1).add(center)
			leapTo(x, z)
		} else {
			if (!host.onGround) return
			
			if (!hasNavigated) {
				host.turnTo(playerPos)
				
				hasNavigated = true
				val center = Vector3(host.source).mul(1, 0, 1)
				val (x, _, z) = playerPos.copy().sub(center).normalize().mul((-RADIUS + 2) * if (timesFinished == 0) 1 else -1).add(center)
				val result = goto(x, z, if (timesFinished == 0) 5 else 10)
				
				// just in case >_<
				if (!result) timesFinished = 2
			} else if (!host.navigator.noPath()) {
				getEntitiesWithinAABB(host.worldObj, EntityLivingBase::class.java, getBoundingBox(host.posX, host.posY, host.posZ, host.lastTickPosX, host.lastTickPosY, host.lastTickPosZ).expand(host.width, host.height, host.width).expand(1)).forEach {
					if (it is IAncientWolf) return@forEach
					if (it.attackEntityFrom(DamageSource.causeMobDamage(host), 20f) && it is EntityPlayer) host.heal(host.maxHealth * 0.05f)
				}
			} else {
				if (++timesFinished < 2) resetFlags()
			}
		}
	}

	fun leapTo(x: Double, z: Double) {
		val target = Vector3(x, 64.5, z)
		val d = Vector3.vecEntityDistance(target, host)
		val (mx, _, mz) = target.sub(host).mul(0.15)
		host.setMotion(mx, d / 48, mz)
	}
	
	fun resetFlags() {
		hasLeaped = false
		hasNavigated = false
		host.navigator.clearPathEntity()
	}
	
	override fun onTaskFinish() {
		resetFlags()
		timesFinished = 0
		
		getEntitiesWithinAABB(host.worldObj, EntityFenrirDome::class.java, host.boundingBox(RADIUS * 2)).forEach(EntityFenrirDome::setDead)
	}
	
	override fun getMaxSkillCD() = 1500
	
	companion object {
		const val RADIUS = 60
	}
}
