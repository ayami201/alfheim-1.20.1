package alfheim.common.entity.boss.ai.fenrir

import alexsocol.asjlib.ASJBitwiseHelper
import alfheim.common.entity.boss.EntityFenrir
import net.minecraft.entity.*
import net.minecraft.entity.ai.EntityAIBase

abstract class EntityAIFenrirSkillBase(val host: EntityFenrir, val skill: EnumFenrirSkill, comp: EnumActionComponents, vararg comps: EnumActionComponents): EntityAIBase() {
	
	init {
		mutexBits = arrayOf(comp, *comps).fold(0) { acc, it -> ASJBitwiseHelper.setBit(acc, it.ordinal, true) }
	}
	
	final override fun shouldExecute() = host.anySkillCD <= 0 && host.skillCooldowns.getOrDefault(skill, 0) <= 0 && host.attackTarget != null && canStartTask()
	
	abstract fun canStartTask(): Boolean
	
	final override fun startExecuting() {
		host.skill = skill
		host.skillTicks = 0
		onTaskStart()
	}
	
	open fun onTaskStart() {}
	
	override fun continueExecuting() = false
	
	final override fun updateTask() {
		onTaskTick()
		host.skillTicks++
	}
	
	open fun onTaskTick() {}
	
	abstract fun getMaxSkillCD(): Int
	
	final override fun resetTask() {
		host.skillCooldowns[skill] = getMaxSkillCD()
		host.anySkillCD = 50
		host.skillTicks = -1
		
		onTaskFinish()
	}
	
	open fun onTaskFinish() {}
	
	fun getSpeed() = host.attributeMap.getAttributeInstance(SharedMonsterAttributes.movementSpeed).attributeValue
	
	fun goto(x: Double, z: Double, speedModifier: Int): Boolean {
		return host.navigator.tryMoveToXYZ(x, 64.0, z, getSpeed() * speedModifier)
	}
	
	fun goto(target: Entity, speedModifier: Int): Boolean {
		return host.navigator.tryMoveToEntityLiving(target, getSpeed() * speedModifier)
	}
	
	enum class EnumActionComponents {
		ATTACK, MOTION
	}
	
	enum class EnumFenrirSkill {
		INACTIVE, TORNADO, SLASH, ONSLAUGHT, HIT, BITE, TAIL, JUMP
	}
}
