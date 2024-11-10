package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.ASJTile
import alexsocol.asjlib.math.Vector3
import alfheim.api.AlfheimAPI
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.asm.hook.AlfheimHookHandler
import alfheim.common.core.asm.hook.extender.SparkExtender.attachTile
import alfheim.common.core.handler.VisualEffectHandler
import alfheim.common.core.util.DamageSourceSpell
import net.minecraft.block.BlockFire
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.entity.*
import net.minecraft.entity.monster.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.AxisAlignedBB
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.common.util.ForgeDirection.VALID_DIRECTIONS
import org.lwjgl.opengl.GL11
import vazkii.botania.api.mana.IManaPool
import vazkii.botania.api.mana.spark.*
import vazkii.botania.client.core.handler.HUDHandler
import vazkii.botania.common.Botania
import vazkii.botania.common.block.tile.mana.TilePool
import java.awt.Color
import kotlin.math.*
import vazkii.botania.common.core.helper.Vector3 as Bector3

class TileAnomalyHarvester: ASJTile(), ISparkAttachable {
	
	var creative = false
	
	var animationTicks = 0
	var prevAnimationTicks = 0
	
	var mana = 0
	var offset = Vector3()
	var power = 1
	var radius = Vector3(1.0)
	
	// Protection from speeding up
	var tick = -1L
	
	var subTiles = HashSet<String>()
	
	fun addSubTile(sub: String) = subTiles.add(sub)
	
	val tunnels = setOf("Antigrav", "Gravity")
	
	override fun updateEntity() {
		if (worldObj.totalWorldTime == tick) return
		tick = worldObj.totalWorldTime
		
		prevAnimationTicks = animationTicks
		
		if (worldObj.isBlockDirectlyGettingPowered(xCoord, yCoord, zCoord) || power <= 0) return
		
		if (creative)
			mana = maxMana
		else {
			val spark = attachedSpark
			if (spark != null) {
				val sparkEntities = SparkHelper.getSparksAround(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5)
				for (otherSpark in sparkEntities) {
					if (spark === otherSpark)
						continue
					
					if (otherSpark.attachedTile != null && otherSpark.attachedTile is IManaPool)
						otherSpark.registerTransfer(spark)
				}
			}
		}
		
		var did = false
		
		if ("ManaTornado" in subTiles) {
			recieveMana(if (worldObj.rand.nextInt(5) == 0) 2 else 1)
			did = true
		}
		
		if (mana > 0 || creative) {
			val aoe = getAoE()
			val volume = ceil((aoe.maxX - aoe.minX) * (aoe.maxY - aoe.minY) * (aoe.maxZ - aoe.minZ)).I
			val tunnel = subTiles.containsAll(tunnels)
			if (tunnel) {
				val effect = AlfheimAPI.anomalyBehaviors["Tunnel"]!!
				var cost = 0
				
				cost += effect.effect(this) * effect.costPerApplication
				cost += effect.costPerBlock * volume
				
				if (!creative) mana -= cost
				
				did = true
			}
			
			for (st in subTiles) {
				if (mana <= 0 && !creative) return
				
				if (tunnel && st in tunnels) continue
				val effect = AlfheimAPI.anomalyBehaviors[st] ?: continue
				var cost = 0
				
				cost += effect.effect(this) * effect.costPerApplication
				cost += effect.costPerBlock * volume
				
				if (!creative) mana -= cost
				
				did = true
			}
		}
		
		if ("ManaVoid" in subTiles || did) animationTicks += power
		
		if (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) return
		
		if (worldObj.isRemote) renderBoundBox()
	}
	
	fun getAoE(): AxisAlignedBB = getBoundingBox(xCoord, yCoord, zCoord).expand(radius.x / 2, radius.y / 2, radius.z / 2).getOffsetBoundingBox(offset.x + 0.5, offset.y + 0.5, offset.z + 0.5)
	
	@Suppress("DuplicatedCode")
	fun renderBoundBox() {
		var i: Double
		val aabb = getAoE()
		
		i = aabb.minX
		while (i <= aabb.maxX) {
			Botania.proxy.sparkleFX(worldObj, i, aabb.minY, aabb.minZ, 1f, 0f, 0f, 0.5f, 1, true)
			i += 0.25
		}
		
		i = aabb.minX
		while (i <= aabb.maxX) {
			Botania.proxy.sparkleFX(worldObj, i, aabb.maxY, aabb.maxZ, 1f, 0f, 0f, 0.5f, 1, true)
			i += 0.25
		}
		
		i = aabb.minX
		while (i <= aabb.maxX) {
			Botania.proxy.sparkleFX(worldObj, i, aabb.maxY, aabb.minZ, 1f, 0f, 0f, 0.5f, 1, true)
			i += 0.25
		}
		
		i = aabb.minX
		while (i <= aabb.maxX) {
			Botania.proxy.sparkleFX(worldObj, i, aabb.minY, aabb.maxZ, 1f, 0f, 0f, 0.5f, 1, true)
			i += 0.25
		}
		
		i = aabb.minY
		while (i <= aabb.maxY) {
			Botania.proxy.sparkleFX(worldObj, aabb.minX, i, aabb.minZ, 1f, 0f, 0f, 0.5f, 1, true)
			i += 0.25
		}
		
		i = aabb.minY
		while (i <= aabb.maxY) {
			Botania.proxy.sparkleFX(worldObj, aabb.maxX, i, aabb.maxZ, 1f, 0f, 0f, 0.5f, 1, true)
			i += 0.25
		}
		
		i = aabb.minY
		while (i <= aabb.maxY) {
			Botania.proxy.sparkleFX(worldObj, aabb.maxX, i, aabb.minZ, 1f, 0f, 0f, 0.5f, 1, true)
			i += 0.25
		}
		
		i = aabb.minY
		while (i <= aabb.maxY) {
			Botania.proxy.sparkleFX(worldObj, aabb.minX, i, aabb.maxZ, 1f, 0f, 0f, 0.5f, 1, true)
			i += 0.25
		}
		
		i = aabb.minZ
		while (i <= aabb.maxZ) {
			Botania.proxy.sparkleFX(worldObj, aabb.minX, aabb.minY, i, 1f, 0f, 0f, 0.5f, 1, true)
			i += 0.25
		}
		
		i = aabb.minZ
		while (i <= aabb.maxZ) {
			Botania.proxy.sparkleFX(worldObj, aabb.maxX, aabb.maxY, i, 1f, 0f, 0f, 0.5f, 1, true)
			i += 0.25
		}
		
		i = aabb.minZ
		while (i <= aabb.maxZ) {
			Botania.proxy.sparkleFX(worldObj, aabb.maxX, aabb.minY, i, 1f, 0f, 0f, 0.5f, 1, true)
			i += 0.25
		}
		
		i = aabb.minZ
		while (i <= aabb.maxZ) {
			Botania.proxy.sparkleFX(worldObj, aabb.minX, aabb.maxY, i, 1f, 0f, 0f, 0.5f, 1, true)
			i += 0.25
		}
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		nbt.setInteger("subtiles", subTiles.size)
		subTiles.forEachIndexed { i, s -> nbt.setString("subtile$i", s) }
		
		nbt.setDouble("rX", radius.x)
		nbt.setDouble("rY", radius.y)
		nbt.setDouble("rZ", radius.z)
		nbt.setDouble("oX", offset.x)
		nbt.setDouble("oY", offset.y)
		nbt.setDouble("oZ", offset.z)
		
		nbt.setInteger("mana", mana)
		nbt.setInteger("power", power)
		
		nbt.setBoolean("creative", creative)
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		val size = nbt.getInteger("subtiles")
		subTiles.clear()
		
		for (i in 0 until size)
			subTiles.add(nbt.getString("subtile$i"))
		
		radius.set(nbt.getDouble("rX"), nbt.getDouble("rY"), nbt.getDouble("rZ"))
		offset.set(nbt.getDouble("oX"), nbt.getDouble("oY"), nbt.getDouble("oZ"))
		
		mana = nbt.getInteger("mana")
		power = nbt.getInteger("power")
		
		creative = nbt.getBoolean("creative")
	}
	
	val maxMana get() = if ("ManaVoid" in subTiles) TilePool.MAX_MANA else TilePool.MAX_MANA_DILLUTED
	
	override fun getCurrentMana() = mana
	
	override fun isFull() = currentMana >= maxMana
	
	override fun recieveMana(add: Int) {
		mana = max(0, min(mana + add, maxMana))
	}
	
	override fun canRecieveManaFromBursts() = true
	
	override fun canAttachSpark(stack: ItemStack?) = true
	
	override fun attachSpark(entity: ISparkEntity?) = entity.attachTile(this)
	
	override fun getAttachedSpark() = getEntitiesWithinAABB(worldObj, ISparkEntity::class.java, boundingBox().offset(0, 1, 0)).firstOrNull()
	
	override fun getAvailableSpaceForMana() = max(0, maxMana - mana)
	
	override fun areIncomingTranfersDone() = false
	
	fun renderHUD(res: ScaledResolution) {
		val name = ItemStack(AlfheimBlocks.anomalyHarvester).displayName
		val color = Color(0xFF9600).rgb
		HUDHandler.drawSimpleManaHUD(color, mana, maxMana, name, res)
		GL11.glColor4f(1f, 1f, 1f, 1f)
	}
}

object AnomalyHarvesterBehaviors {
	
	init {
		AlfheimAPI.anomalyBehaviors["Antigrav"] = AlfheimAPI.AnomalyBehavior(1, 10) { doAntigrav(it as TileAnomalyHarvester) }
		AlfheimAPI.anomalyBehaviors["Gravity"] = AlfheimAPI.AnomalyBehavior(1, 10) { doGravity(it as TileAnomalyHarvester) }
		AlfheimAPI.anomalyBehaviors["Tunnel"] = AlfheimAPI.AnomalyBehavior(2, 20) { doTunnel(it as TileAnomalyHarvester) }
		AlfheimAPI.anomalyBehaviors["Lightning"] = AlfheimAPI.AnomalyBehavior(1, 12) { doLightning(it as TileAnomalyHarvester) }
		AlfheimAPI.anomalyBehaviors["Killer"] = AlfheimAPI.AnomalyBehavior(1, 15) { doKiller(it as TileAnomalyHarvester) }
		AlfheimAPI.anomalyBehaviors["SpeedUp"] = AlfheimAPI.AnomalyBehavior(10, 100) { doSpeedUp(it as TileAnomalyHarvester) }
		AlfheimAPI.anomalyBehaviors["Warp"] = AlfheimAPI.AnomalyBehavior(1, 1) { doWarp(it as TileAnomalyHarvester) }
		
		AlfheimAPI.anomalyBehaviors["ManaTornado"] = AlfheimAPI.AnomalyBehavior(0, 0) { 0 }
		AlfheimAPI.anomalyBehaviors["ManaVoid"] = AlfheimAPI.AnomalyBehavior(0, 0) { 0 }
	}
	
	private fun doAntigrav(tile: TileAnomalyHarvester): Int {
		if (tile.worldObj.isRemote) {
			AlfheimHookHandler.wispNoclip = false
			
			for (c in 0..3) {
				val (x, y, z) = tile.radius.copy().mul(Vector3().rand().sub(0.5)).add(tile.offset)
				Botania.proxy.wispFX(tile.worldObj, tile.xCoord + x + 0.5, tile.yCoord + y - 0.5, tile.zCoord + z + 0.5, 0.5f, 0.9f, 1f, 0.1f, -0.1f, 1f)
			}
			AlfheimHookHandler.wispNoclip = true
			
			return 0
		}
		
		var applications = 0
		
		val aabb = tile.getAoE()
		getEntitiesWithinAABB(tile.worldObj, Entity::class.java, aabb).forEach {
			it.motionY += if (it.isSneaking) 0.05 else 0.085 + tile.power * 0.005
			it.fallDistance = 0f
			applications++
		}
		
		return applications * tile.power
	}
	
	fun doGravity(tile: TileAnomalyHarvester): Int {
		val (x, y, z) = Vector3.fromTileEntityCenter(tile).add(tile.offset)
		
		if (tile.worldObj.isRemote) {
			Botania.proxy.wispFX(tile.worldObj, x, y, z, 1f, 1f, 1f, 0.5f, 0f, 0.5f)
			return 0
		}
		
		var applications = 0
		
		getEntitiesWithinAABB(tile.worldObj, Entity::class.java, tile.getAoE()).forEach {
			if (it is EntityPlayer && it.capabilities.disableDamage) return@forEach
			if (it.isSneaking) return@forEach
			
			val (mx, my, mz) = Vector3(x, y, z).sub(Vector3.fromEntity(it)).normalize().mul(0.1 * tile.power)
			
			it.motionX += mx
			it.motionY += my
			it.motionZ += mz
			it.fallDistance = 0f
			
			applications++
		}
		
		return applications * tile.power
	}
	
	fun doTunnel(tile: TileAnomalyHarvester): Int {
		val dir = VALID_DIRECTIONS.safeGet(tile.worldObj.getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord))
		
		val mX = dir.offsetX / 10f
		val mY = dir.offsetY / 10f
		val mZ = dir.offsetZ / 10f
		
		if (tile.worldObj.isRemote) {
			AlfheimHookHandler.wispNoclip = false
			
			for (c in 0..3) {
				val (x, y, z) = tile.radius.copy().mul(Vector3().rand().sub(0.5)).add(tile.offset)
				Botania.proxy.wispFX(tile.worldObj, tile.xCoord + x + 0.5 - mX * 10, tile.yCoord + y + 0.5 - mY * 10, tile.zCoord + z + 0.5 - mZ * 10, 0.3f, 0.9f, 0.8f, 0.1f, mX, mY, mZ, 1f)
			}
			
			AlfheimHookHandler.wispNoclip = true
			
			return 0
		}
		
		var applications = 0
		val aabb = tile.getAoE()
		
		getEntitiesWithinAABB(tile.worldObj, Entity::class.java, aabb).forEach {
			it.motionX = mX * tile.power / 2.0
			it.motionY = mY * tile.power / 2.0
			it.motionZ = mZ * tile.power / 2.0
			it.fallDistance = 0f
			
			applications++

//			if (it !is EntityPlayer) return@forEach
//
//			val rotations = arrayOf(it.rotationYaw, it.rotationYaw, 180f, 0f, 90f, -90f)
//
//			if (it.rotationYaw != rotations[dir.ordinal]) {
//				it.cameraYaw += (rotations[dir.ordinal] - it.cameraYaw) % 5f
//			}
		}
		
		return applications * tile.power
	}
	
	fun doLightning(tile: TileAnomalyHarvester): Int {
		val (x, y, z) = Vector3.fromTileEntityCenter(tile).add(tile.offset)
		
		if (tile.worldObj.isRemote) {
			val (i, j, k) = Vector3(x, y, z).rand().sub(0.5)
			Botania.proxy.lightningFX(tile.worldObj, Bector3(x, y, z), Bector3(i, j, k), 1f, 0, 0xFF0000)
			
			return 0
		}
		
		getEntitiesWithinAABB(tile.worldObj, IMob::class.java, tile.getAoE()).random(tile.worldObj.rand)?.let { it as Entity
			if (it is EntityCreeper && !it.powered) {
				it.dataWatcher.updateObject(17, 1.toByte())
			} else if (!it.attackEntityFrom(DamageSourceSpell.anomaly, Math.random().F * tile.power / 2f + tile.power / 2f)) return@let
			
			val (i, j, k) = Vector3.fromEntityCenter(it)
			VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.LIGHTNING, tile.worldObj.provider.dimensionId, x, y, z, i, j, k, 1.0, 0.0, 0xFF0000.D)
			
			return tile.power
		}
		
		return 0
	}
	
	fun doKiller(tile: TileAnomalyHarvester): Int {
		val (x, y, z) = Vector3.fromTileEntityCenter(tile).add(tile.offset)
		
		if (tile.worldObj.isRemote) {
			Botania.proxy.sparkleFX(tile.worldObj, x, y, z, 1f, 0f, 0f, 2f, 1, true)
			Botania.proxy.sparkleFX(tile.worldObj, x, y, z, 1f, 1f, 1f, 1f, 1, true)
			
			return 0
		}
		
		getEntitiesWithinAABB(tile.worldObj, EntityLivingBase::class.java, tile.getAoE()).random(tile.worldObj.rand)?.let {
			if (it.attackEntityFrom(DamageSourceSpell.anomaly, (Math.random() * tile.power / 2 + tile.power / 2).F))
				return tile.power
		}
		
		return 0
	}
	
	fun doSpeedUp(tile: TileAnomalyHarvester): Int {
		if (tile.worldObj.isRemote) {
			val (x, y, z) = Vector3.fromTileEntityCenter(tile).add(tile.radius.copy().mul(Vector3().rand().sub(0.5))).add(tile.offset)
			Botania.proxy.sparkleFX(tile.worldObj, x, y, z, 0.5f, 1f, 0.5f, 1f, 1, true)
		}
		
		var applications = 0
		
		val aabb = tile.getAoE()
		
		for (i in 0 until tile.power.I) {
			getEntitiesWithinAABB(tile.worldObj, Entity::class.java, aabb).forEach {
				if (!it.isEntityAlive) return@forEach
				
				applications++
				it.onUpdate()
			}
			
			tile.worldObj.loadedTileEntityList.forEach { it as TileEntity
				if (!aabb.isVecInside(Vector3.fromTileEntity(it).add(0.5).toVec3()) || !it.canUpdate()) return@forEach
				it.updateEntity()
				applications++
			}
		}
		
		for (x in aabb.minX.I..aabb.maxX.I.minus(1))
			for (y in aabb.minY.I..aabb.maxY.I.minus(1))
				for (z in aabb.minZ.I..aabb.maxZ.I.minus(1)) {
					val block = tile.worldObj.getBlock(x, y, z)
					
					if (block === Blocks.air ||
					    block is BlockFire && !tile.worldObj.gameRules.getGameRuleBooleanValue("doFireTick"))
						continue
					
					block.updateTick(tile.worldObj, x, y, z, tile.worldObj.rand)
				}
		
		return applications * tile.power
	}
	
	fun doWarp(tile: TileAnomalyHarvester): Int {
		if (tile.worldObj.isRemote) {
			val (x, y, z) = Vector3.fromTileEntityCenter(tile).add(tile.radius.copy().mul(Vector3().rand().sub(0.5))).add(tile.offset)
			tile.worldObj.spawnParticle("portal", x, y, z, 0.0, 0.0, 0.0)
			return 0
		}
		
		val applicable = getEntitiesWithinAABB(tile.worldObj, Entity::class.java, tile.getAoE()).filterTo(ArrayList()) { !it.isSneaking }
		if (applicable.isEmpty()) return 0
		
		val target = applicable.removeRandom() ?: return 0
		
		val d = ForgeDirection.entries[tile.getBlockMetadata()]
		val (x, y, z) = Vector3.fromTileEntityCenter(tile).add(d.offsetX, d.offsetY, d.offsetZ)
		
		val cost = (Vector3.vecEntityDistance(Vector3(x, y, z), target)).mfloor() * 10
		
		if (target is EntityLivingBase)
			target.setPositionAndUpdate(x, y, z)
		else
			target.setPosition(x, y, z)
		
		target.setMotion(0.0, 0.0, 0.0)
		
		return cost
	}
}