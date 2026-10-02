package alfheim.common.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.*
import alfheim.common.block.*
import alfheim.common.core.helper.*
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.*
import net.minecraft.item.*
import net.minecraft.nbt.*
import net.minecraft.potion.*
import net.minecraft.util.*
import net.minecraft.world.*
import java.util.*

class EntityFlowerBud(world: World): EntityLivingBase(world), IElementalEntity, IKudzuIgnoredEntity {
	
	var timer = 0
	
	init {
		setSize(1f, 1.125f)
		ignoreFrustumCheck = true
	}
	
	override fun applyEntityAttributes() {
		super.applyEntityAttributes()
		
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).baseValue = 1.0
		getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = 200.0
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).baseValue = 0.0
	}
	
	override fun onUpdate() {
		if (!worldObj.isRemote) tick()
		
		super.onUpdate()
		
		rotationYaw = 0f
		newRotationYaw = 0.0
		prevRotationYaw = 0f
		rotationYawHead = 0f
		prevRotationYawHead = 0f
		rotationPitch = 0f
		newRotationPitch = 0.0
		prevRotationPitch = 0f
	}
	
	fun tick() {
		if (++timer >= 2400 && isEntityAlive) {
			EntityVenusHumanTrap(worldObj).apply { setPosition(this@EntityFlowerBud) }.spawn()
			return setDead()
		}
		
		repeat(2) {
			val bb by lazy { OrientedBB(8.5, 0.25, 0.25).rotateOY(45f + it * 90f).translate(posX, posY, posZ) }
			
			getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox().expand(3, 0, 3)).forEach { entity ->
				if (entity !== this && entity !is IKudzuIgnoredEntity && bb.intersectsWith(entity.boundingBox()))
					entity.attackEntityFrom(DamageSource.cactus, 2f)
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	override fun getBrightnessForRender(partialTicks: Float): Int {
		val x = posX.mfloor()
		val z = posZ.mfloor()
		
		if (!worldObj.blockExists(x, 0, z)) return 0

		val midpoint = (boundingBox.maxY - boundingBox.minY) * 0.66
		var y = (posY - yOffset + midpoint).mfloor()
		while (worldObj.getBlock(x, y, z) === AlfheimBlocks.kudzuVine) y++
		return worldObj.getLightBrightnessForSkyBlocks(x, y, z, 0)
	}
	
	override fun getBrightness(partialTicks: Float): Float {
		val x = posX.mfloor()
		val z = posZ.mfloor()
		
		if (!worldObj.blockExists(x, 0, z)) return 0f

		val midpoint = (boundingBox.maxY - boundingBox.minY) * 0.66
		var y = (posY - yOffset + midpoint).mfloor()
		while (worldObj.getBlock(x, y, z) === AlfheimBlocks.kudzuVine) y++
		return worldObj.getLightBrightness(x, y, z)
	}
	
	override fun writeToNBT(nbt: NBTTagCompound) {
		super.writeToNBT(nbt)
		nbt.setInteger("timer", timer)
	}
	
	override fun readFromNBT(nbt: NBTTagCompound) {
		super.readFromNBT(nbt)
		timer = nbt.getInteger("timer")
	}
	
	override val elements = EnumSet.of(ElementalDamage.NATURE)!!
	
	override fun getBoundingBox() = boundingBox!!
	override fun getCollisionBox(entity: Entity?) = entity?.boundingBox
	override fun canBeCollidedWith() = true
	override fun addVelocity(x: Double, y: Double, z: Double) = Unit
	override fun moveFlying(i: Float, d: Float, k: Float) = Unit
	override fun moveEntity(x: Double, y: Double, z: Double) = Unit
	override fun setAngles(yaw: Float, pich: Float) = Unit
	override fun setRotation(yaw: Float, pich: Float) = Unit
	override fun moveEntityWithHeading(strafe: Float, forward: Float) = Unit
	override fun isMovementBlocked() = true
	override fun fall(distance: Float) = Unit
	override fun knockBack(attacker: Entity?, wtf: Float, x: Double, z: Double) = Unit
	override fun updatePotionEffects() = Unit
	override fun addPotionEffect(pe: PotionEffect?) = Unit
	override fun updateFallState(distance: Double, inGround: Boolean) = Unit
	override fun canBreatheUnderwater() = true
	override fun canBePushed() = false
	override fun isPushedByWater() = false
	override fun getHeldItem() = null
	override fun getEquipmentInSlot(slot: Int) = null
	override fun setCurrentItemOrArmor(slot: Int, stack: ItemStack?) = Unit
	override fun getLastActiveItems() = emptyArray<ItemStack>()
}
