package alfheim.common.entity.boss

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.ModInfo
import alfheim.api.boss.IBotaniaBossWithName
import alfheim.api.entity.IAncientWolf
import alfheim.common.achievement.AlfheimAchievements
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.entity.EntitySniceBall
import alfheim.common.entity.boss.EntityFlugel.Companion.isRecordPlaying
import alfheim.common.entity.boss.EntityFlugel.Companion.playRecord
import alfheim.common.entity.boss.EntityFlugel.Companion.stopRecord
import alfheim.common.entity.boss.ai.fenrirnew.*
import alfheim.common.entity.boss.ai.fenrirnew.EntityAIFenrirSkillBase.EnumFenrirSkill
import alfheim.common.item.*
import alfheim.common.item.material.ElvenResourcesMetas
import alfheim.common.world.dim.domains.gen.FenrirDomain
import cpw.mods.fml.relauncher.*
import net.minecraft.block.Block
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.entity.*
import net.minecraft.entity.player.*
import net.minecraft.item.*
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.util.*
import net.minecraft.world.World
import vazkii.botania.client.core.handler.BossBarHandler
import java.awt.Rectangle
import kotlin.math.*

class EntityFenrirNew(world: World): EntityCreature(world), IBotaniaBossWithName, IAncientWolf {
	
	// cooldowns
	var anySkillCD = 0
	var skillCooldowns = HashMap<EnumFenrirSkill, Int>()
	
	// for animation
	val running get() = navigator.speed > 2
	
	var skill
		get() = EnumFenrirSkill.entries.firstOrNull { it.name == dataWatcher.getWatchableObjectString(2) }
		set(value) = dataWatcher.updateObject(2, value)
	
	var skillTicks
		get() = dataWatcher.getWatchableObjectInt(3)
		set(value) = dataWatcher.updateObject(3, value)
	
	var stage
		get() = dataWatcher.getWatchableObjectInt(4)
		set(value) = dataWatcher.updateObject(4, value)
	
	var source: ChunkCoordinates
		get() = dataWatcher.getWatchableObjectChunkCoordinates(5)
		set(value) = dataWatcher.updateObject(5, value)
	
	var syncedYaw
		get() = dataWatcher.getWatchableObjectFloat(12)
		set(value) = dataWatcher.updateObject(12, value)
	
	init {
		setSize(3f, 4f)
		navigator.avoidsWater = true
		
		tasks.addTask(1, EntityAIFenrirOnslaught(this))
		tasks.addTask(1, EntityAIFenrirJump(this))
		
		tasks.addTask(2, EntityAIFenrirTornado(this))
		tasks.addTask(2, EntityAIFenrirSlash(this))
		tasks.addTask(2, EntityAIFenrirHit(this))
		tasks.addTask(2, EntityAIFenrirBite(this))
		tasks.addTask(2, EntityAIFenrirTailSwipe(this))
		
//		tasks.addTask(9, EntityAIWatchClosest(this, EntityPlayer::class.java, 8f))
//		tasks.addTask(9, EntityAILookIdle(this))
//		targetTasks.addTask(1, EntityAINearestAttackableTarget(this, EntityPlayer::class.java, 0, false))
//		targetTasks.addTask(3, EntityAIHurtByTarget(this, true))
	}
	
	override fun entityInit() {
		super.entityInit()
		
		dataWatcher.addObject(2, "") // skill
		dataWatcher.addObject(3, 0) // skill animation
		dataWatcher.addObject(4, 0) // stage
		dataWatcher.addObject(5, ChunkCoordinates()) // spawn
		dataWatcher.addObject(12, 0f) // yaw
		
	}
	
	override fun applyEntityAttributes() {
		super.applyEntityAttributes()
		getEntityAttribute(SharedMonsterAttributes.followRange).baseValue = EntityAIFenrirOnslaught.RADIUS * 2.0
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).baseValue = 1.0
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).baseValue = 0.5
		getEntityAttribute(SharedMonsterAttributes.maxHealth).baseValue = 3000.0
		stepHeight = 5f
	}
	
	override fun onLivingUpdate() {
		attackTarget = MinecraftServer.getServer().configurationManager.playerEntityList.firstOrNull() as? EntityPlayerMP
		
		super.onLivingUpdate()
		
		val (x, y, z) = Vector3(source).add(0.5)
		val (sx, sy, sz) = Vector3(source).mf()
		
		if (ASJUtilities.isClient && !isDead && !worldObj.isRecordPlaying(sx, sy, sz))
			worldObj.playRecord(AlfheimItems.discFenrir as ItemRecord, sx, sy, sz)
		
		clearActivePotions()
		updateCDs()
		
		getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, getBoundingBox(x, y + 15, z).expand(63, 15, 63)).forEach {
			if (it is EntityPlayer && !it.capabilities.isCreativeMode) it.capabilities.isFlying = false
		}
		
		rotationYaw = syncedYaw
	}
	
	fun updateCDs() {
		anySkillCD--
		
		skillCooldowns.forEach { (k, v) ->
			skillCooldowns[k] = v - 1
		}
	}
	
	// ########################################################## MISC #########################################################
	
	override fun attackEntityFrom(source: DamageSource, amount: Float): Boolean {
		if (source.entity === this || source.damageType == DamageSource.fall.damageType || source.sourceOfDamage is EntitySniceBall) return false
		val mod = if (source.entity != null && Vector3.entityDistance(source.entity, this) > 7) 10f else 2f
		return super.attackEntityFrom(source, amount / mod)
	}
	
	override fun onDeath(src: DamageSource?) {
		super.onDeath(src)
		
		val (sx, sy, sz) = Vector3(source).mf()
		worldObj.stopRecord(sx, sy, sz)
		
		if (RagnarokHandler.canBringBackSunAndMoon())
			RagnarokHandler.bringBackSunAndMoon()
	}
	
	override fun getDropItem() = AlfheimItems.elvenResource
	
	override fun dropFewItems(gotHit: Boolean, looting: Int) {
		entityDropItem(ElvenResourcesMetas.FenrirFur.stack(rand.nextInt(looting * 2 + 2) + 3), 5f)
		
		val (x, y, z) = Vector3(source).mf()
		getEntitiesWithinAABB(worldObj, EntityPlayer::class.java, FenrirDomain.boundBox.copy().offset(x, y, z)).shuffled().forEach { player ->
			val data = relics.shuffled().firstOrNull { !player.hasAchievement(it.first) } ?: return@forEach
			val stack = ItemStack(data.second)
			
			player.triggerAchievement(data.first)
			entityDropItem(stack, 0f)
			return
		}
		
		if (ASJUtilities.chance(5 + looting * 0.01)) entityDropItem(lightRelics.random().copy(), 0f)
	}
	
	override fun isAIEnabled() = true
	override fun canDespawn() = false
	override fun canAttackClass(clazz: Class<*>?) = true
	override fun canBePushed() = false
	override fun decreaseAirSupply(air: Int) = air
	override fun func_145780_a(x: Int, y: Int, z: Int, block: Block?) = playSound("mob.wolf.step", soundVolume, soundPitch)
	override fun getLivingSound() = if (ASJUtilities.chance(20)) "${ModInfo.MODID}:fenrir.hrrr" else null
	override fun getHurtSound() = if (ASJUtilities.chance(10)) "${ModInfo.MODID}:fenrir.hurt" else null
	override fun getDeathSound() = "${ModInfo.MODID}:fenrir.howl"
	override fun getSoundVolume() = 10f
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		super.writeEntityToNBT(nbt)
		
		val (x, y, z) = source
		nbt.setIntArray(TAG_SOURCE, intArrayOf(x, y, z))
		nbt.setInteger(TAG_STAGE, stage)
	}

	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		super.readEntityFromNBT(nbt)
		
		if (nbt.hasKey(TAG_SOURCE)) {
			val (x, y, z) = nbt.getIntArray(TAG_SOURCE)
			source = ChunkCoordinates(x, y, z)
		}
		
		stage = nbt.getInteger(TAG_STAGE)
	}
	
	// ######################################################### RENDER ########################################################
	
	fun turnTo(pos: Vector3) {
		rotationYaw = ASJUtilities.updateRotation(rotationYaw, (atan2(pos.z - this.posZ, pos.x - this.posX) * 180.0 / Math.PI).F - 90f, 180f)
		syncedYaw = rotationYaw
	}
	
	override fun getEyeHeight() = height * 0.8f
	
	@SideOnly(Side.CLIENT)
	fun getTailRotation(): Float = Math.toRadians(100 + sin((ticksExisted + mc.timer.renderPartialTicks) / 10.0) * 5).F
	
	override fun getNameColor() = 0xA00000
	
	@SideOnly(Side.CLIENT)
	override fun getBossBarTextureRect(): Rectangle {
		return barRect ?: Rectangle(0, 88, 185, 15).apply { barRect = this }
	}
	
	@SideOnly(Side.CLIENT)
	override fun getBossBarHPTextureRect(): Rectangle {
		return hpBarRect ?: Rectangle(0, 59, 181, 7).apply { hpBarRect = this }
	}
	
	override fun getBossBarTexture() = BossBarHandler.defaultBossBar!!
	
	override fun bossBarRenderCallback(res: ScaledResolution?, x: Int, y: Int) = Unit
	
	companion object {
		
		const val TAG_SOURCE = "source"
		const val TAG_STAGE = "stage"
		
		val relics = arrayOf(AlfheimAchievements.gungnir to AlfheimItems.gungnir, AlfheimAchievements.gleipnir to AlfheimItems.gleipnir)
		val lightRelics = arrayOf(ItemStack(AlfheimItems.fenrirClaws), *ItemFenrirLoot.FenrirLootMetas.entries.map(ItemFenrirLoot.FenrirLootMetas::stack).toTypedArray())
		
		var barRect: Rectangle? = null
		var hpBarRect: Rectangle? = null
		
		fun summon(world: World, x: Int, y: Int, z: Int) {
			val fenrir = EntityFenrirNew(world)
			fenrir.setPositionAndRotation(x + 0.5, y + 0.5, z + 0.5 + 31, 0f, 0f)
			fenrir.source = ChunkCoordinates(x, y, z)
			fenrir.forceSpawn = true
			fenrir.spawn()
		}
	}
}
