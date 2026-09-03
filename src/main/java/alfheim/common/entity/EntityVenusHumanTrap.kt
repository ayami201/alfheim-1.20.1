package alfheim.common.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.block.*
import alfheim.common.core.helper.*
import net.minecraft.block.*
import net.minecraft.enchantment.*
import net.minecraft.entity.*
import net.minecraft.entity.ai.*
import net.minecraft.entity.monster.*
import net.minecraft.entity.player.*
import net.minecraft.util.*
import net.minecraft.world.*
import java.util.*

class EntityVenusHumanTrap(world: World): EntityMob(world), IElementalEntity, IKudzuIgnoredEntity {
	
	var animationTimerBite = 0
	
	var hasTarget
		get() = getFlag(2)
		set(value) = setFlag(2, value)
	
	var tongueCooldown = 0
	
	var tongueSegments
		get() = dataWatcher.getWatchableObjectInt(2)
		set(value) = dataWatcher.updateObject(2, value)
	
	var tongueTicks
		get() = dataWatcher.getWatchableObjectInt(3)
		set(value) = dataWatcher.updateObject(3, value)
	
	init {
		tasks.addTask(0, EntityAISwimming(this))
		tasks.addTask(1, EntityAIAttackOnCollide(this, EntityPlayer::class.java, 1.0, false))
		tasks.addTask(2, EntityAIMoveTowardsRestriction(this, 1.0))
		tasks.addTask(3, EntityAIWander(this, 1.0))
		targetTasks.addTask(1, EntityAIHurtByTarget(this, true))
		targetTasks.addTask(2, EntityAINearestAttackableTarget(this, EntityPlayer::class.java, 0, false))
		setSize(0.6f, 1.6f)
		
		experienceValue = 15
	}
	
	override fun entityInit() {
		super.entityInit()
		dataWatcher.addObject(2, 0)  // tongue segments
		dataWatcher.addObject(3, 0)  // tongue ticks
	}
	
	override fun applyEntityAttributes() {
		super.applyEntityAttributes()
		getEntityAttribute(SharedMonsterAttributes.attackDamage).baseValue = 5.0
		getEntityAttribute(SharedMonsterAttributes.followRange).baseValue = 40.0
		getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = 50.0
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).baseValue = 0.35
	}
	
	override fun onLivingUpdate() {
		super.onLivingUpdate()
		
		attackTarget?.let { ASJUtilities.faceEntity(this, it, 180f, 180f) }
		renderYawOffset = rotationYaw
		
		if (animationTimerBite > 0)
			--animationTimerBite
		
		if (tongueTicks > 0) {
			ignoreFrustumCheck = true
			--tongueTicks
		} else {
			ignoreFrustumCheck = false
		}
		
		if (tongueCooldown > 0)
			--tongueCooldown
		else
			tryTongueAttack()
	}
	
	fun tryTongueAttack() {
		if (!ASJUtilities.isServer) return
		
		val target = attackTarget ?: return
		val distance = Vector3.entityDistance(this, target)
		if (distance !in 2.0..5.0) return
		
		val oldPitch = rotationPitch
		val oldYaw = rotationYaw
		ASJUtilities.faceEntity(this, target, 180f, 180f)
		
		if (ASJUtilities.getMouseOver(this, 5.0, false)?.entityHit !== target) {
			rotationPitch = oldPitch
			rotationYaw = oldYaw
			return
		}
		
		renderYawOffset = rotationYaw
		tongueTicks = 10
		tongueSegments = MathHelper.ceiling_double_int(distance / 0.25)
		tongueCooldown = ASJUtilities.randInBounds(100, 200)
		target.attackEntityFrom(DamageSource.causeMobDamage(this).setTo(ElementalDamage.NATURE), getEntityAttribute(SharedMonsterAttributes.attackDamage).attributeValue.F * 2)
	}
	
	override fun setAttackTarget(target: EntityLivingBase?) {
		super.setAttackTarget(target)
		hasTarget = attackTarget != null
	}
	
	override fun attackEntityAsMob(target: Entity): Boolean {
		if (animationTimerBite > 0 || tongueTicks > 0) return false
		
		if (!target.attackEntityFrom(DamageSource.causeMobDamage(this).setTo(ElementalDamage.NATURE), getEntityAttribute(SharedMonsterAttributes.attackDamage).attributeValue.F))
			return false
		
		animationTimerBite = 10
		worldObj.setEntityState(this, 1.toByte())
		
		// no idea wtf that is but whatever
		if (target is EntityLivingBase)
			EnchantmentHelper.func_151384_a(target, this)
		EnchantmentHelper.func_151385_b(this, target)
		
		return true
	}
	
	override fun handleHealthUpdate(state: Byte) {
		if (state.I == 1) animationTimerBite = 10
		else super.handleHealthUpdate(state)
	}
	
	override val elements = EnumSet.of(ElementalDamage.NATURE)!!
	override fun isAIEnabled() = true
	override fun getLivingSound() =  "step.grass"
	override fun getHurtSound() = super.getHurtSound()!! // "dig.grass"
	override fun getDeathSound() = "dig.grass"
	override fun func_145780_a(x: Int, y: Int, z: Int, block: Block?) = playSound("step.grass", 0.15f, 1.0f)
	override fun addRandomArmor() = Unit
	override fun getHeldItem() = null
	override fun getEquipmentInSlot(slot: Int) = null
	override fun dropEquipment(no: Boolean, idea: Int) = Unit
	override fun canDespawn() = false
}
