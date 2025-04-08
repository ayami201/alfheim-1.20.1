package alfheim.common.entity.spell

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.spell.*
import alfheim.client.core.handler.CardinalSystemClient
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.*
import alfheim.common.core.handler.CardinalSystem.PartySystem
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.spell.tech.SpellDriftingMine
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraft.world.World
import java.util.*
import kotlin.math.*

class EntitySpellDriftingMine(world: World): Entity(world), ITimeStopSpecific {
	
	val caster: EntityPlayer? 
		get() = worldObj.getPlayerEntityByName(casterName)
	
	var casterName
		get() = dataWatcher.getWatchableObjectString(2)!!
		set(value) = dataWatcher.updateObject(2, value)
	
	override val isImmune = false
	
	init {
		setSize(1f, 1f)
	}
	
	constructor(world: World, shooter: EntityLivingBase): this(world) {
		casterName = shooter.commandSenderName
		setPositionAndRotation(shooter.posX, shooter.posY + shooter.height * 0.75, shooter.posZ, shooter.rotationYaw, shooter.rotationPitch)
		
		if (shooter.isSneaking) return
		
		val m = Vector3(shooter.lookVec).mul(SpellDriftingMine.efficiency)
		setMotion(m.x, m.y, m.z)
	}
	
	override fun entityInit() {
		dataWatcher.addObject(2, "")
	}
	
	fun onImpact(mop: MovingObjectPosition?) {
		if (!worldObj.isRemote) {
			val caster = caster
			
			mop?.entityHit?.attackEntityFrom(DamageSourceSpell.explosion(this, caster), SpellBase.over(caster, SpellDriftingMine.damage))
			
			getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox(SpellDriftingMine.radius)).forEach {
				it.attackEntityFrom(DamageSourceSpell.explosion(this, caster), SpellBase.over(caster, SpellDriftingMine.damage))
			}
			
			worldObj.playSoundEffect(posX, posY, posZ, "random.explode", 4f, (1f + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2f) * 0.7f)
			VisualEffectHandler.sendPacket(VisualEffects.EXPL, this)
			setDead()
		}
	}
	
	override fun onUpdate() {
		val caster: EntityLivingBase? = caster
		
		if (!AlfheimConfigHandler.enableMMO || !worldObj.isRemote && (caster == null || caster.isDead || !worldObj.blockExists(posX.I, posY.I, posZ.I))) {
			return setDead()
		}
		
		if (ASJUtilities.isClient && CardinalSystemClient.PlayerSegmentClient.party?.isMember(caster) != true) {
			return setDead()
		}
		
		moveEntity(motionX, motionY, motionZ)
		
		super.onUpdate()
		
		if (ticksExisted == SpellDriftingMine.duration || isBurning) onImpact(null)
		
		val vec3 = Vec3.createVectorHelper(posX, posY, posZ)
		val vec31 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ)
		var movingobjectposition: MovingObjectPosition? = worldObj.rayTraceBlocks(vec3, vec31)
		
		if (movingobjectposition == null) {
			val l = getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox.addCoord(motionX, motionY, motionZ).expand(1))
			l.remove(caster)
			
			for (e in l)
				if (e.canBeCollidedWith() && !PartySystem.mobsSameParty(caster, e) && Vector3.entityDistance(this, e) < 3) {
					movingobjectposition = MovingObjectPosition(e)
					break
				}
		}
		
		if (movingobjectposition != null) onImpact(movingobjectposition)
		
		val f1 = sqrt(motionX * motionX + motionZ * motionZ)
		rotationYaw = (atan2(motionZ, motionX) * 180.0 / Math.PI).F + 90f
		
		rotationPitch = (atan2(f1.D, motionY) * 180.0 / Math.PI).F - 90f
		while (rotationPitch - prevRotationPitch < -180f) prevRotationPitch -= 360f
		while (rotationPitch - prevRotationPitch >= 180f) prevRotationPitch += 360f
		while (rotationYaw - prevRotationYaw < -180f) prevRotationYaw -= 360f
		while (rotationYaw - prevRotationYaw >= 180f) prevRotationYaw += 360f
		
		rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2f
		rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2f
	}
	
	override fun canBeCollidedWith() = true
	
	override fun getCollisionBorderSize() = 0.5f
	
	@SideOnly(Side.CLIENT)
	override fun getShadowSize() = 0f
	
	override fun affectedBy(uuid: UUID) = caster?.uniqueID != uuid
	
	public override fun readEntityFromNBT(nbt: NBTTagCompound) {
		casterName = nbt.getString("castername")
	}
	
	public override fun writeEntityToNBT(nbt: NBTTagCompound) {
		nbt.setString("castername", casterName)
	}
}