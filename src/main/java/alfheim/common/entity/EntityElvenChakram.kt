package alfheim.common.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.item.AlfheimItems
import net.minecraft.block.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.item.ItemStack
import net.minecraft.nbt.*
import net.minecraft.potion.*
import net.minecraft.util.MovingObjectPosition
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import vazkii.botania.common.Botania
import vazkii.botania.common.core.helper.MathHelper
import vazkii.botania.common.core.helper.Vector3 as Bector3

class EntityElvenChakram: EntityThrowable {
	
	var tracer: ItemStack?
		get() = dataWatcher.getWatchableObjectItemStack(30)
		set(tracer) = dataWatcher.updateObject(30, tracer)
	
	var timesBounced
		get() = dataWatcher.getWatchableObjectInt(31)
		set(times) = dataWatcher.updateObject(31, times)
	
	val itemStack get() = tracer ?: ItemStack(AlfheimItems.elvenChakram)
	
	@Suppress("unused")
	constructor(world: World): super(world)
	
	constructor(world: World, player: EntityPlayer, tracer: ItemStack?): super(world, player) {
		motionX /= 2.0
		motionY /= 2.0
		motionZ /= 2.0
		
		if (tracer != null) {
			val t = tracer.copy()
			t.stackSize = 1
			
			val tracing = player.isSneaking
			t.tracing = tracing
			
			if (!tracing) {
				val trace = t.trace
				val angle = rotationYaw - t.startYaw
				
				trace.forEach {
					it.rotateOY(angle)
				}
				
				t.trace = trace
				t.index = 0
			}
			
			t.startYaw = rotationYaw
			
			this.tracer = t
		}
	}
	
	override fun entityInit() {
		dataWatcher.addObjectByDataType(30, 5)
		dataWatcher.addObject(31, 0)
	}
	
	override fun onUpdate() {
		val tracer = tracer
		
		tracer?.apply {
			if (tracing) return@apply
			
			val t = trace
			if (t.size <= 0 || index !in t.indices) return@apply
			
			val (mx, my, mz) = t[index++]
			trace = t
			
			motionX = mx
			motionY = my
			motionZ = mz
		}
		
		val mx = motionX
		val my = motionY
		val mz = motionZ
		
		super.onUpdate()
		
		if (tracer == null && worldObj.isRemote && rand.nextInt(5) == 0) {
			val (i, j, k) = Vector3().rand().sub(0.5).normalize().mul(Math.random() * 0.5 + 0.5)
			Botania.proxy.lightningFX(worldObj, Bector3.fromEntity(this), Bector3.fromEntity(this).add(i, j, k), 0.5f, 0x302248, 0x8B7698)
		}
		
		val thrower = getThrower()
		
		if (timesBounced < MAX_BOUNCES && ticksExisted <= 60) {
			if (timesBounced <= 0) {
				motionX = mx
				motionY = my
				motionZ = mz
			}
			
			tracer?.apply { 
				if (!tracing || worldObj.isRemote) return@apply
				
				val t = trace
				t.add(Vector3(motionX, motionY, motionZ))
				trace = t
			}
			?:
			getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox(2)).forEach {
				if (it === thrower) return@forEach
				
				if (!worldObj.isRemote) {
					val damage = if (thrower != null) DamageSourceSpell.lightningIndirect(this, thrower) else DamageSourceSpell.lightning
					it.attackEntityFrom(damage, 8f)
					it.addPotionEffect(PotionEffect(Potion.moveSlowdown.id, 60))
				}
				
				if (worldObj.isRemote)
					Botania.proxy.lightningFX(worldObj, Bector3.fromEntity(this), Bector3.fromEntity(it), 0.5f, 0x302248, 0x8B7698)
			}
			
			return
		} else {
			riddenByEntity?.ridingEntity = null
			riddenByEntity = null
		}
		
		noClip = true
		
		if (thrower == null || !thrower.isEntityAlive || thrower !is EntityPlayer) return dropAndKill()
		
		val motion = Vector3.fromEntityCenter(thrower).sub(Vector3.fromEntityCenter(this)).normalize()
		motionX = motion.x
		motionY = motion.y
		motionZ = motion.z
		
		if (MathHelper.pointDistanceSpace(posX, posY, posZ, thrower.posX, thrower.posY, thrower.posZ) >= 1) return
		
		if (tracer == null && thrower.capabilities.isCreativeMode)
			setDead()
		else if (thrower.inventory.addItemStackToInventory(itemStack))
			setDead()
		else
			dropAndKill()
	}
	
	private fun dropAndKill() {
		if (worldObj.isRemote) return
		
		EntityItem(worldObj, posX, posY, posZ, itemStack).spawn()
		setDead()
	}
	
	override fun onImpact(pos: MovingObjectPosition) {
		if (noClip) return
		
		val block = worldObj.getBlock(pos.blockX, pos.blockY, pos.blockZ)
		if (block is BlockBush || block is BlockLeaves) return
		
		if (pos.entityHit != null) return
		if (timesBounced >= MAX_BOUNCES) return
		
		val currentMovementVec = Vector3(motionX, motionY, motionZ)
		val dir = ForgeDirection.getOrientation(pos.sideHit)
		val normalVector = Vector3(dir.offsetX.toDouble(), dir.offsetY.toDouble(), dir.offsetZ.toDouble()).normalize()
		val movementVec = normalVector.mul(-2 * currentMovementVec.dotProduct(normalVector)).add(currentMovementVec)
		motionX = movementVec.x
		motionY = movementVec.y
		motionZ = movementVec.z
		timesBounced++
	}
	
	override fun getGravityVelocity() = 0f
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		super.writeEntityToNBT(nbt)
		
		tracer?.apply { nbt.setTag("tracer", NBTTagCompound().also { writeToNBT(it) }) }
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		super.readEntityFromNBT(nbt)
		
		if (!nbt.hasKey("tracer")) return
		tracer = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("tracer"))
	}
}

private const val MAX_BOUNCES = 16

private var ItemStack.index
	get() = ItemNBTHelper.getInt(this, "index", -1)
	set(index) = ItemNBTHelper.setInt(this, "index", index)

private var ItemStack.startYaw
	get() = ItemNBTHelper.getFloat(this, "startYaw", 0f)
	set(start) = ItemNBTHelper.setFloat(this, "startYaw", start)

private var ItemStack.tracing
	get() = ItemNBTHelper.getBoolean(this, "tracing", false)
	set(tracing) = ItemNBTHelper.setBoolean(this, "tracing", tracing)

private var ItemStack.trace: ArrayList<Vector3>
	get() {
		val list = ArrayList<Vector3>()
		
		val traceList = ItemNBTHelper.getList(this, "trace", 9)
		for (traceEntry in traceList.tagList) {
			traceEntry as NBTTagList
			
			val mx = traceEntry.func_150309_d(0)
			val my = traceEntry.func_150309_d(1)
			val mz = traceEntry.func_150309_d(2)
			
			list += Vector3(mx, my, mz)
		}
		
		return list
	}
	set(list) {
		val traceList = NBTTagList()
		
		for (vec in list) {
			val (mx, my, mz) = vec
			
			val traceEntry = NBTTagList()
			traceEntry.appendTag(NBTTagDouble(mx))
			traceEntry.appendTag(NBTTagDouble(my))
			traceEntry.appendTag(NBTTagDouble(mz))
			
			traceList.appendTag(traceEntry)
		}
		
		ItemNBTHelper.setList(this, "trace", traceList)
	}