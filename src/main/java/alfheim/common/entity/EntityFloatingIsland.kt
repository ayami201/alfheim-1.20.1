package alfheim.common.entity

import alexsocol.asjlib.*
import alfheim.api.entity.IMulticollidableEntity
import alfheim.common.world.data.BlockElementBlockAccess
import cpw.mods.fml.relauncher.*
import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraft.world.World
import kotlin.math.*

class EntityFloatingIsland(world: World): Entity(world), IMulticollidableEntity {
	
	private var blocksString
		get() = dataWatcher.getWatchableObjectString(2)
		set(value) {
			dataWatcher.updateObject(2, value)
			shouldUpdate = true
		}
	
	private var shouldUpdate: Boolean
		get() {
			val i = dataWatcher.getWatchableObjectInt(3)
			
			return if (ASJUtilities.isServer)
				ASJBitwiseHelper.getBit(i, 0)
			else
				ASJBitwiseHelper.getBit(i, 1)
		}
		set(value) {
			var i = dataWatcher.getWatchableObjectInt(3)
			
			i = if (ASJUtilities.isServer) {
				if (value) 3 else ASJBitwiseHelper.setBit(i, 0, false)
			} else
				ASJBitwiseHelper.setBit(i, 1, value)
			
			dataWatcher.updateObject(3, i)
		}
	
	var blockList = emptyList<BlockElement>()
		set(value) {
			field = value
			
			blockAccess = BlockElementBlockAccess(value, this)
		}
	
	var blockAccess = BlockElementBlockAccess(emptyList(), this)
	
	init {
		setSize(1f, 1f)
	}
	
	override fun entityInit() {
		dataWatcher.addObject(2, "[{\"block\":\"minecraft:stone\",\"location\":[{}]}]")
		dataWatcher.addObject(3, 3)
	}
	
	override fun onEntityUpdate() {
		prevPosX = posX
		prevPosY = posY
		prevPosZ = posZ
		
		if (shouldUpdate) run {
			val schemaText = blocksString
			if (schemaText.isNullOrBlank()) return setDead()
			
			blockList = SchemaUtils.parse(schemaText)
			if (blockList.isEmpty()) return setDead()
			
			var x = 0
			var X = 0
			var y = 0
			var z = 0
			var Z = 0
			
			blockList.forEach { e ->
				e.location.forEach { l ->
					if (l.y < 0)
						throw IllegalArgumentException("Y pos should not be negative (got ${l.y})")
					
					x = min(x, l.x)
					X = max(X, l.x)
					y = max(y, l.y)
					z = min(z, l.z)
					Z = max(Z, l.z)
				}
			}
			
			setSize(max(max(abs(x), X), max(abs(z), Z)) * 2 + 1f, y + 1f)
			
			shouldUpdate = false
		}
		
		getEntitiesWithinAABB(worldObj, Entity::class.java, boundingBox(collisionBorderSize)).forEach {
			if (motionX == 0.0 && motionZ == 0.0) return@forEach
			
			it.boundingBox.offset(motionX, 0.0, motionZ)
			it.posX = (it.boundingBox.minX + it.boundingBox.maxX) / 2.0
			it.posZ = (it.boundingBox.minZ + it.boundingBox.maxZ) / 2.0
		}
		
		motionX = 0.0
		motionY = 0.0
		motionZ = 0.0
		
		rotationYaw = 0f
		rotationPitch = 0f
	}
	
	@SideOnly(Side.CLIENT)
	override fun setPositionAndRotation2(x: Double, y: Double, z: Double, yaw: Float, pitch: Float, nope: Int) {
		setPosition(x, y, z)
		setRotation(yaw, pitch)
		// fuck you "push out of blocks"!
	}
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		nbt.setString(TAG_BLOCK_LIST, blocksString)
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		val string = nbt.getString(TAG_BLOCK_LIST)
		if (string.isNullOrBlank()) return
		
		blocksString = string
	}
	
	override fun canBeCollidedWith() = true
	override fun canBePushed() = false
	override fun getBoundingBox() = null
	override fun getCollisionBox(against: Entity?) = null
	override fun getCollisionBorderSize() = 0.1f
	override fun applyEntityCollision(against: Entity?) = Unit
	
	override fun getAdditionalCollisions(): List<AxisAlignedBB> {
		return blockList.flatMap { e ->
			val block = Block.getBlockFromName(e.block) ?: return@flatMap emptyList()
			e.location.map {
				l -> block.getCollisionBoundingBoxFromPool(worldObj, l.x + posX.mfloor(), l.y + posY.mfloor(), l.z + posZ.mfloor())
				?.offset(posX % 1 + if (posX < 0) 0.5 else -0.5, posY % 1, posZ % 1 + if (posZ < 0) 0.5 else -0.5)
			}
		}.filterNotNull()
	}
	
	companion object {
		const val TAG_BLOCK_LIST = "blockList"
	}
}