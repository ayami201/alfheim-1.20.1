package alfheim.common.entity

import alfheim.api.entity.*
import alfheim.common.entity.ai.*
import net.minecraft.block.Block
import net.minecraft.enchantment.*
import net.minecraft.entity.*
import net.minecraft.entity.ai.*
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityArrow
import net.minecraft.init.Items
import net.minecraft.item.*
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.MovingObjectPosition
import net.minecraft.world.World
import kotlin.math.min

class EntityFrozenViking(world: World): EntityMob(world), INiflheimEntity, IAlfheimMob, IRangedAttackMob {
	
	private val aiArrowAttack = EntityAIArrowAttack(this, 1.0, 20, 60, 15.0f)
	private val aiAttackOnCollide = EntityAIAttackOnCollide(this, EntityPlayer::class.java, 1.0, false)
	
	init {
		tasks.addTask(0, EntityAISwimming(this))
		tasks.addTask(1, EntityAIAttackOnCollide(this, EntityPlayer::class.java, 1.0, false))
		tasks.addTask(2, EntityAIMoveTowardsRestriction(this, 1.0))
		tasks.addTask(3, EntityAIWander(this, 1.0))
		tasks.addTask(4, EntityAIWatchClosest(this, EntityPlayer::class.java, 8f))
		tasks.addTask(4, EntityAILookIdle(this))
		targetTasks.addTask(0, EntityAIFleeOnLowHP(this, 8f))
		targetTasks.addTask(1, EntityAIHurtByTargetNotLowHP(this, true))
		targetTasks.addTask(2, EntityAINearestAttackableTargetNotLowHP(this, EntityPlayer::class.java, 0, true))
		setSize(0.6f, 1.8f)
		
		setCurrentItemOrArmor(0, ItemStack(if (rng.nextBoolean()) Items.iron_sword else Items.bow))
		enchantEquipment()
	}
	
	override fun applyEntityAttributes() {
		super.applyEntityAttributes()
		getEntityAttribute(SharedMonsterAttributes.attackDamage).baseValue = 2.0
		getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = 24.0
		getEntityAttribute(SharedMonsterAttributes.followRange).baseValue = 32.0
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).baseValue = 0.3
	}
	
	override fun getTotalArmorValue() = min(24, super.getTotalArmorValue() + 6)
	override fun isAIEnabled() = true
	override fun getLivingSound() = "mob.zombie.say"
	override fun getHurtSound() = "mob.zombie.hurt"
	override fun getDeathSound() = "mob.zombie.death"
	override fun func_145780_a(x: Int, y: Int, z: Int, block: Block) = playSound("mob.zombie.step", 0.15f, 1.0f) // get step sound
	override fun getCreatureAttribute() = EnumCreatureAttribute.UNDEAD
	override fun getDropItem() = if (rng.nextBoolean()) Items.rotten_flesh else Items.bone
	override fun dropRareDrop(unknown: Int) {
		fun randomDamaged(item: Item) = ItemStack(item, 1, rng.nextInt(item.maxDamage))
		
		when (rand.nextInt(4)) {
			0 -> entityDropItem(randomDamaged(Items.iron_helmet), 0f)
			1 -> entityDropItem(randomDamaged(Items.chainmail_chestplate), 0f)
			2 -> entityDropItem(randomDamaged(Items.chainmail_leggings), 0f)
			3 -> entityDropItem(randomDamaged(Items.iron_boots), 0f)
		}
	}
	
	override fun getPickedResult(target: MovingObjectPosition?) = super<IAlfheimMob>.getPickedResult(target)
	
	override fun attackEntityWithRangedAttack(target: EntityLivingBase?, damage: Float) {
		val arrow = EntityArrow(worldObj, this, target, 1.6f, 14 - worldObj.difficultySetting.difficultyId * 4f)
		val i = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, heldItem)
		val j = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, heldItem)
		arrow.damage = damage * 2.0 + rand.nextGaussian() * 0.25 + worldObj.difficultySetting.difficultyId * 0.11
		
		if (i > 0) arrow.damage += i.toDouble() * 0.5 + 0.5
		if (j > 0) arrow.setKnockbackStrength(j)
		if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, heldItem) > 0) arrow.setFire(100)
		
		playSound("random.bow", 1f, 1f / (rng.nextFloat() * 0.4f + 0.8f))
		worldObj.spawnEntityInWorld(arrow)
	}
	
	override fun setCurrentItemOrArmor(slot: Int, stack: ItemStack?) {
		super.setCurrentItemOrArmor(slot, stack)
		if (!worldObj.isRemote && slot == 0) setCombatTask()
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound?) {
		super.readEntityFromNBT(nbt)
		setCombatTask()
	}
	
	/**
	 * sets this entity's combat AI.
	 */
	fun setCombatTask() {
		tasks.removeTask(aiAttackOnCollide)
		tasks.removeTask(aiArrowAttack)
		
		if (heldItem?.item is ItemBow) {
			tasks.addTask(4, aiArrowAttack)
		} else {
			tasks.addTask(4, aiAttackOnCollide)
		}
	}
}
