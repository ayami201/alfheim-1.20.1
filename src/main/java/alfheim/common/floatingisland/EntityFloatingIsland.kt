package alfheim.common.floatingisland

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.ModInfo
import alfheim.api.entity.IMulticollidableEntity
import alfheim.client.sound.EntityBoundMovingSound
import alfheim.common.core.handler.*
import com.google.gson.JsonParseException
import cpw.mods.fml.relauncher.*
import net.minecraft.block.*
import net.minecraft.entity.*
import net.minecraft.init.Blocks
import net.minecraft.nbt.*
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.common.Botania
import kotlin.math.*

class EntityFloatingIsland(world: World): Entity(world), IMulticollidableEntity {
	
	private var shouldUpdate: Boolean
		get() {
			val i = dataWatcher.getWatchableObjectInt(2)
			
			return if (ASJUtilities.isServer)
				ASJBitwiseHelper.getBit(i, 0)
			else
				ASJBitwiseHelper.getBit(i, 1)
		}
		set(value) {
			var i = dataWatcher.getWatchableObjectInt(2)
			
			i = if (ASJUtilities.isServer) {
				if (value) 3 else ASJBitwiseHelper.setBit(i, 0, false)
			} else
				ASJBitwiseHelper.setBit(i, 1, value)
			
			dataWatcher.updateObject(2, i)
		}
	
	private var blocksString: String
		get() {
			var acc = ""
			
			for (it in BLOCK_START..AlfheimConfigHandler.floatingIslandSyncedDataInitLimit) {
				val string = dataWatcher.getWatchedObject(it)?.`object` ?: break
				acc = "$acc$string"
			}
			
			return acc
		}
		set(value) {
			val chunked = value.chunked(Short.MAX_VALUE.I - 1)
			val max = AlfheimConfigHandler.floatingIslandSyncedDataInitLimit
			
			chunked.forEachIndexed { id, it ->
				require(id <= max) { "Cannot save such a big island to synced entity data (max ${32767 * max} bytes, provided ${value.length}).\nPlease, update configs and install mod that extends DataWatcher IDs such as https://curseforge.com/minecraft/mc-mods/confighelper" }
				
				dataWatcher.updateObject(id + BLOCK_START, it)
			}
			
			for (i in (chunked.size + BLOCK_START)..max) {
				dataWatcher.updateObject(i, "")
			}
			
			shouldUpdate = true
		}
	
	var blockAccess = FloatingIslandBlockAccess(emptyList(), this)
	
	var glCallList = -1
	
	var despawnTimer: Short
		get() = dataWatcher.getWatchableObjectShort(1)
		set(value) = dataWatcher.updateObject(1, value)
	
	var duying: Boolean
		get() = getFlag(6)
		set(value) = setFlag(6, value)
	
	var deathTimer = 0
	
	var velocityX: Float
		get() = dataWatcher.getWatchableObjectFloat(3)
		set(value) = dataWatcher.updateObject(3, value)
	
	var velocityZ: Float
		get() = dataWatcher.getWatchableObjectFloat(4)
		set(value) = dataWatcher.updateObject(4, value)
	
	init {
		setSize(1f, 1f)
		despawnTimer = 0
	}
	
	override fun entityInit() {
		dataWatcher.addObject(2, 3)
		dataWatcher.addObject(3, 0f)
		dataWatcher.addObject(4, 0f)
		dataWatcher.addObject(BLOCK_START, "[{\"block\":\"minecraft:stone\",\"location\":[{}]}]")
		(BLOCK_START + 1..AlfheimConfigHandler.floatingIslandSyncedDataInitLimit).forEach { dataWatcher.addObject(it, "") }
	}
	
	fun setVelocity(vx: Float, vz: Float) {
		velocityX = vx
		velocityZ = vz
	}
	
	override fun onEntityUpdate() {
		blockAccess.tickLight()
		
		prevPosX = posX
		prevPosY = posY
		prevPosZ = posZ
		
		setMotion(0.0)
		rotationYaw = 0f
		rotationPitch = 0f
		
		if (shouldUpdate) run {
			val schemaText = blocksString
			if (schemaText.isBlank()) return setDead()
			
			val blockList: List<BlockElement>
			try {
				blockList = UnsafeSchemaUtils.parseText(schemaText)
			} catch (e: JsonParseException) {
				return@run
			}
			
			if (blockList.isEmpty()) return setDead()
			
			blockAccess = FloatingIslandBlockAccess(blockList, this)
			setSize(max(max(abs(blockAccess.startX), blockAccess.endX), max(abs(blockAccess.startZ), blockAccess.endZ)) * 2 + 1f, blockAccess.endY + 1f)
			
			glCallList = -1
			shouldUpdate = false
		}
		
		val thisBB = boundingBox(collisionBorderSize)
		
		val collidedWith = if (velocityX == 0f && velocityZ == 0f)
			emptyList()
		else if (ASJUtilities.isServer) {
			val prevRadius = World.MAX_ENTITY_RADIUS
			World.MAX_ENTITY_RADIUS = MAX_ISLAND_RADIUS.D
			val list = getEntitiesWithinAABB(worldObj, Entity::class.java, thisBB)
			World.MAX_ENTITY_RADIUS = prevRadius
			list
		} else if (mc.thePlayer.boundingBox.intersectsWith(thisBB))
			listOf(mc.thePlayer, this)
		else
			listOf(this)
		
		collidedWith.forEach {
			it.boundingBox.offset(velocityX, 0.0, velocityZ)
			it.posX = (it.boundingBox.minX + it.boundingBox.maxX) / 2.0
			it.posZ = (it.boundingBox.minZ + it.boundingBox.maxZ) / 2.0
		}
		
		if (ASJUtilities.isServer) {
			collidedWith.filterIsInstance<EntityFloatingIsland>().toMutableList().apply {
				if (size < 2) return@apply
				
				forEach {
					it.duying = true
				}
			}
			
			if (worldObj.totalWorldTime % 100 == 0L && worldObj.func_147461_a(thisBB).isNotEmpty())
				duying = true
			
			loadChunk()
			
			if (worldObj.playerEntities.isEmpty()) {
				if (despawnTimer++ >= Short.MAX_VALUE - 1)
					duying = true
			} else despawnTimer = 0
		}
		
		if (duying) onDeathUpdate()
	}
	
	fun onDeathUpdate() {
		if (deathTimer++ > 300) return setDead()
		
		val big = deathTimer > 240
		if (ASJUtilities.isServer) run {
			if (big && deathTimer != 270) return@run
			
			val r = Vector3(width, height, width).mul(0.5).mul(1 / if (big) 1.5f else 1.75f).length()
			val center = Vector3.fromEntityCenter(this)
			
			getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, getBoundingBox(posX, posY + height / 2f, posZ).expand(r)).forEach {
				if (Vector3.vecEntityDistance(center, it) > r) return@forEach
				it.attackEntityFrom(EntityDamageSource("explosion", this).setDifficultyScaled().setExplosion(), if (big) 3f else 1f)
				
				if (big) it.knockback(this, 10f)
			}
			
			return
		}
		
		if (deathTimer > 270) return
		if (deathTimer in 170..240) return
		
		if (deathTimer == 1)
			mc.soundHandler.playSound(EntityBoundMovingSound(mc.thePlayer, "${ModInfo.MODID}:ea").apply {
				setRepeat(false)
				volume = 0.6f
			})

		Botania.proxy.setWispFXDistanceLimit(false)
		val v = Vector3()
		for (i in 1..5) {
			v.rand().sub(0.5).normalize().mul(Math.random() * width, Math.random() * height, Math.random() * width).mul(1 / if (big) 1.5f else 1.75f)
			Botania.proxy.wispFX(worldObj, posX + v.x, posY + height / 2f + v.y, posZ + v.z, 1f, Math.random().F * 0.5f, Math.random().F * 0.075f, (Math.random() * min(width, height) / if (big) 0.5f else 3f + 1).F, 0f, if (big) 1.5f else (Math.random() * 3.0 + 2).F)
		}

		Botania.proxy.setWispFXDistanceLimit(true)
	}
	
	fun loadChunk() {
		ChunkLoadingHandler.requestChunkLoad(worldObj, (posX.mfloor() shr 4),     (posZ.mfloor() shr 4))
		ChunkLoadingHandler.requestChunkLoad(worldObj, (posX.mfloor() shr 4) + 1, (posZ.mfloor() shr 4))
		ChunkLoadingHandler.requestChunkLoad(worldObj, (posX.mfloor() shr 4) - 1, (posZ.mfloor() shr 4))
		ChunkLoadingHandler.requestChunkLoad(worldObj, (posX.mfloor() shr 4),     (posZ.mfloor() shr 4) + 1)
		ChunkLoadingHandler.requestChunkLoad(worldObj, (posX.mfloor() shr 4),     (posZ.mfloor() shr 4) - 1)
	}
	
	@SideOnly(Side.CLIENT)
	override fun setPositionAndRotation2(x: Double, y: Double, z: Double, yaw: Float, pitch: Float, nope: Int) {
		setPosition(x, y, z)
		setRotation(yaw, pitch)
		// fuck you "push out of blocks"!
	}
	
	override fun setDead() {
		super.setDead()
		glCallList = -1
	}
	
	public override fun writeEntityToNBT(nbt: NBTTagCompound) {
		nbt.setFloat(TAG_VELOCITY_X, velocityX)
		nbt.setFloat(TAG_VELOCITY_Z, velocityZ)
		
		val chunked = blockAccess.toSchema().chunked(Short.MAX_VALUE.I * 2 - 1)
		nbt.setTag(TAG_BLOCK_LIST, NBTTagList().apply { chunked.forEach { appendTag(NBTTagString(it)) } })
	}
	
	public override fun readEntityFromNBT(nbt: NBTTagCompound) {
		velocityX = nbt.getFloat(TAG_VELOCITY_X)
		velocityZ = nbt.getFloat(TAG_VELOCITY_Z)
		
		val list = nbt.getTagList(TAG_BLOCK_LIST, Constants.NBT.TAG_STRING)
		if (list.tagCount() == 0) return
		
		blocksString = (0 until list.tagCount()).fold("") { acc, id -> "$acc${list.getStringTagAt(id)}" }
	}
	
	override fun canBeCollidedWith() = true
	override fun canBePushed() = false
	override fun getBoundingBox() = null
	override fun getCollisionBox(against: Entity?) = null
	override fun getCollisionBorderSize() = 0.1f
	override fun applyEntityCollision(against: Entity?) = Unit
	
	override fun getAdditionalCollisions(target: AxisAlignedBB): List<AxisAlignedBB> {
		val list = mutableListOf<AxisAlignedBB>()
		
		val xs = (target.minX - posX).mfloor() - 1
		val ys = (target.minY - posY).mfloor() - 1
		val zs = (target.minZ - posZ).mfloor() - 1
		val xe = (target.maxX - posX).mceil() + 1
		val ye = (target.maxY - posY).mceil() + 1
		val ze = (target.maxZ - posZ).mceil() + 1
		
		for (x in xs..xe)
			for (y in ys..ye)
				for (z in zs..ze) {
					val block = blockAccess.getBlock(x, y, z)
					if (block === Blocks.air) continue
					
					val sublist = mutableListOf<AxisAlignedBB?>()
					fun addCollisions() {
						block.getCollisionBoundingBoxFromPool(worldObj, x + posX.mfloor(), y + posY.mfloor(), z + posZ.mfloor())?.let { sublist += it }
					}
					when (block) {
						is BlockStairs   -> {
							block.func_150147_e(blockAccess, x, y, z)
							addCollisions()
							val flag = block.func_150145_f(blockAccess, x, y, z)
							addCollisions()
							if (flag && block.func_150144_g(blockAccess, x, y, z)) addCollisions()
							block.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f)
						}
						is BlockSlab -> {
							block.setBlockBoundsBasedOnState(blockAccess, x, y, z)
							block.getCollisionBoundingBoxFromPool(worldObj, x + posX.mfloor(), y + posY.mfloor(), z + posZ.mfloor())?.let { sublist += it }
						}
						else     -> {
							block.addCollisionBoxesToList(worldObj, x + posX.mfloor(), y + posY.mfloor(), z + posZ.mfloor(), TileEntity.INFINITE_EXTENT_AABB, sublist, null)
						}
					}
					
					sublist.forEach {
						if (it == null) return@forEach
						
						it.offset(posX % 1 + if (posX < 0) 0.5 else -0.5, posY % 1, posZ % 1 + if (posZ < 0) 0.5 else -0.5)
						
						if (it.intersectsWith(target))
							list += it
					}
				}
		
//		blockAccess.blockMap.forEach { (coords, data) ->
//			val block = data.block
//			if (block === Blocks.air) return@forEach
//
//			val (x, y, z) = coords
//
//			val sublist = mutableListOf<AxisAlignedBB>()
//
//			fun addCollisions() {
//				block.getCollisionBoundingBoxFromPool(worldObj, x + posX.mfloor(), y + posY.mfloor(), z + posZ.mfloor())?.let { sublist += it }
//			}
//
//			when (block) {
//				is BlockStairs   -> {
//					block.func_150147_e(blockAccess, x, y, z)
//					addCollisions()
//					val flag = block.func_150145_f(blockAccess, x, y, z)
//					addCollisions()
//					if (flag && block.func_150144_g(blockAccess, x, y, z)) addCollisions()
//					block.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f)
//				}
//
//				is BlockSlab -> {
//					block.setBlockBoundsBasedOnState(blockAccess, x, y, z)
//					block.getCollisionBoundingBoxFromPool(worldObj, x + posX.mfloor(), y + posY.mfloor(), z + posZ.mfloor())?.let { sublist += it }
//				}
//
//				else     -> {
//					block.addCollisionBoxesToList(worldObj, x + posX.mfloor(), y + posY.mfloor(), z + posZ.mfloor(), TileEntity.INFINITE_EXTENT_AABB, sublist, null)
//				}
//			}
//
//			sublist.mapTo(list) { it.offset(posX % 1 + if (posX < 0) 0.5 else -0.5, posY % 1, posZ % 1 + if (posZ < 0) 0.5 else -0.5) }
//		}
		
		return list
	}
	
	override fun getAir() = 0
	override fun setAir(air: Int) = Unit
	
	override fun setSize(w: Float, h: Float) {
		firstUpdate = true
		super.setSize(w, h)
		boundingBox.setBB(boundingBox())
		MAX_ISLAND_RADIUS = max(MAX_ISLAND_RADIUS, max(w / 2, h / 2) + 1)
	}
	
	companion object {
		
		const val BLOCK_START = 5
		
		const val TAG_BLOCK_LIST = "blockList"
		const val TAG_VELOCITY_X = "vecolityX"
		const val TAG_VELOCITY_Z = "vecolityZ"
		
		var MAX_ISLAND_RADIUS = 0.5f
		
		init {
			FloatingIslandInteractionHandler.eventForge()
			BotaniaAPI.blacklistEntityFromGravityRod(EntityFloatingIsland::class.java)
		}
	}
}