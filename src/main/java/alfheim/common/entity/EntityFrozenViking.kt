package alfheim.common.entity

import alfheim.api.entity.INiflheimEntity
import net.minecraft.block.Block
import net.minecraft.entity.*
import net.minecraft.entity.ai.*
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.world.World

class EntityFrozenViking(world: World): EntityMob(world), INiflheimEntity {
	
	var textureId
		get() = dataWatcher.getWatchableObjectInt(2)
		set(value) = dataWatcher.updateObject(2, value)
	
	init {
		tasks.addTask(0, EntityAISwimming(this))
		tasks.addTask(1, EntityAIAttackOnCollide(this, EntityPlayer::class.java, 1.0, false))
		tasks.addTask(2, EntityAIMoveTowardsRestriction(this, 1.0))
		tasks.addTask(3, EntityAIWander(this, 1.0))
		tasks.addTask(4, EntityAIWatchClosest(this, EntityPlayer::class.java, 8f))
		tasks.addTask(4, EntityAILookIdle(this))
		targetTasks.addTask(1, EntityAIHurtByTarget(this, true))
		targetTasks.addTask(2, EntityAINearestAttackableTarget(this, EntityPlayer::class.java, 0, true))
		setSize(0.6f, 1.8f)
	}
	
	override fun applyEntityAttributes() {
		super.applyEntityAttributes()
		getEntityAttribute(SharedMonsterAttributes.attackDamage).baseValue = 4.0
		getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = 24.0
		getEntityAttribute(SharedMonsterAttributes.followRange).baseValue = 32.0
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).baseValue = 0.3
	}
	
	override fun entityInit() {
		super.entityInit()
		dataWatcher.addObject(2, 0)
	}
	
	override fun interact(player: EntityPlayer): Boolean {
		if (!worldObj.isRemote) textureId = (textureId + 1) % 5
		return true
	}
	
	override fun isAIEnabled() = true
	override fun getLivingSound() = "mob.zombie.say"
	override fun getHurtSound() = "mob.zombie.hurt"
	override fun getDeathSound() = "mob.zombie.death"
	override fun func_145780_a(x: Int, y: Int, z: Int, block: Block) = playSound("mob.zombie.step", 0.15f, 1.0f) // get step sound
	override fun getCreatureAttribute() = EnumCreatureAttribute.UNDEAD
	override fun getDropItem() = Items.rotten_flesh!! // TODO
	override fun dropRareDrop(unknown: Int) {
		when (rand.nextInt(3)) {
			0 -> dropItem(Items.iron_ingot, 1)
			1 -> dropItem(Items.carrot, 1)
			2 -> dropItem(Items.potato, 1)
		}
	}
}
