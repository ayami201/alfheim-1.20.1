package alfheim.common.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.core.asm.hook.replacer.HookReplacerHandler
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.item.AlfheimItems
import alfheim.common.item.equipment.tool.ItemResonator
import cpw.mods.fml.relauncher.*
import net.minecraft.block.Block
import net.minecraft.entity.*
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.potion.Potion
import net.minecraft.util.DamageSource
import net.minecraft.world.World
import vazkii.botania.common.core.handler.ConfigHandler
import vazkii.botania.common.item.equipment.tool.ToolCommons

class EntityResonance(world: World, var host: EntityPlayer?, x: Int, y: Int, z: Int, _mode: Int, _target: Int, _voiding: Boolean, _persistent: Boolean, _dilated: Boolean, _chance: Int): Entity(world) {
	
	var chance // to spread to other blocks
		get() = dataWatcher.getWatchableObjectInt(2)
		set(value) = dataWatcher.updateObject(2, value)
	
	var dilated // less time on auto, more time on manual
		get() = getFlag(2)
		set(value) = setFlag(2, value)
	
	var mode // 0 - 2s cd, 1 - 5s cd, 2 - 5s cd no dig
		get() = dataWatcher.getWatchableObjectInt(3)
		set(value) = dataWatcher.updateObject(3, value)
	
	var persistent // higher chance to spread on each iteration
		get() = getFlag(6)
		set(value) = setFlag(6, value)
	
	var target // 0 - any id/meta, 1 - same id, any meta, 2 - same id&meta
		get() = dataWatcher.getWatchableObjectInt(4)
		set(value) = dataWatcher.updateObject(4, value)
	
	var voiding // void "trash" loot
		get() = getFlag(7)
		set(value) = setFlag(7, value)
	
	var rupturing = false
	
	init {
		setSize(0f, 0f)
		setPosition(x + 0.5, y + 0.5, z + 0.5)
		chance = _chance
		dilated = _dilated
		mode = _mode
		persistent = _persistent
		target = _target
		voiding = _voiding
	}
	
	constructor(world: World): this(world, null, 0, 0, 0, 0, 0, false, false, false, 1)
	
	override fun onUpdate() {
		val noSkip = mode == 2 && getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox(0.5)).isNotEmpty()
		
		if (!noSkip) if (ticksExisted < activationTime) return
		
		try {
			burst()
		} catch (e: StackOverflowError) {
			ASJUtilities.error("Super resonance: happens. Minecraft: ", e)
		}
	}
	
	fun burst() {
		if (ticksExisted < 2) return
		
		val host = host ?: return setDead()
		if (host.heldItem?.item !== AlfheimItems.resonator) return setDead()
		
		val (x, y, z) = Vector3.fromEntity(this).mf()
		
		rupturing = true
		playSound("alfheim:resonator.blast", rand.nextFloat() * 0.5f + 0.75f, rand.nextFloat() * 0.5f + 0.75f)
		
		val targetBlock = worldObj.getBlock(x, y, z)
		val targetMeta = worldObj.getBlockMetadata(x, y, z)
		val targetTile = worldObj.getTileEntity(x, y, z)
		
		if (!explode(host, x, y, z)) return setDead()
		
		getEntitiesWithinAABB(worldObj, EntityResonance::class.java, boundingBox(1.5)).forEach {
			if (it !== this && !it.rupturing) it.burst()
		}
		
		if (targetTile == null && mode != 2 && rand.nextInt(chance) <= 10 + target * 5)
			for (i in x.bidiRange(1))
				for (j in y.bidiRange(1))
					for (k in z.bidiRange(1)) {
						val atBlock = worldObj.getBlock(i, j, k)
						if (atBlock.isAir(worldObj, i, j, k) || atBlock.material.isLiquid) continue
						
						if (target > 0 && atBlock !== targetBlock) continue
						if (target > 1 && worldObj.getBlockMetadata(i, j, k) != targetMeta) continue
						
						if (worldObj.getTileEntity(i, j, k) != null) continue
						
						if (getEntitiesWithinAABB(worldObj, EntityResonance::class.java, getBoundingBox(i, j, k).offset(0.5).expand(1)).isEmpty())
							EntityResonance(worldObj, host, i, j, k, 0, target, voiding, persistent, dilated, chance + if (persistent) CHANCE_INCR_PERS else CHANCE_INCR).spawn(-1)
					}
		
		setDead()
	}
	
	fun explode(host: EntityPlayer, x: Int, y: Int, z: Int): Boolean {
		var attacked = false
		getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox(0.5)).forEach {
			val prev = it.hurtResistantTime
			it.hurtResistantTime = 0
			
			if (it.attackEntityFrom(damageResonance, 5f)) {
				attacked = true
				it.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDBleeding, 50))
				
				if (mode == 2) {
					it.addPotionEffect(PotionEffectU(Potion.moveSlowdown.id, 600, 3))
					it.addPotionEffect(PotionEffectU(Potion.weakness.id, 600, 1))
				}
			}
			it.hurtResistantTime = prev
		}
		
		if (attacked)
			ToolCommons.damageItem(host.heldItem, 1, host, ItemResonator.MANA_PER_DAMAGE)
		
		return removeBlockWithDrops(host, host.heldItem, worldObj, x, y, z, voiding)
	}
	
	fun spawn(limit: Int) {
		val samePos = getEntitiesWithinAABB(worldObj, EntityResonance::class.java, boundingBox(0.5))
		
		samePos.forEach {
			try {
				it.burst()
			} catch (e: StackOverflowError) {
				ASJUtilities.error("Super resonance: happens. Minecraft: ", e)
			}
		}
		
		if (samePos.isNotEmpty()) return
		
		if (limit != -1)
			if (worldObj.loadedEntityList.filterIsInstance<EntityResonance>().count { it.host === host } >= limit)
				return
		
		worldObj.spawnEntityInWorld(this)
		
		if (limit != -1)
			playSound("alfheim:resonator.fire", rand.nextFloat() * 0.5f + 0.75f, rand.nextFloat() * 0.5f + 0.75f)
	}
	
	override fun isInRangeToRenderDist(rangeSquare: Double) = rangeSquare <= 1024.0
	
	@SideOnly(Side.CLIENT)
	override fun setPositionAndRotation2(x: Double, y: Double, z: Double, yaw: Float, pitch: Float, nope: Int) {
		setPosition(x, y, z)
		setRotation(yaw, pitch)
		// fuck you "push out of blocks"!
	}
	
	override fun entityInit() {
		dataWatcher.addObject(2, 1)
		dataWatcher.addObject(3, 0)
		dataWatcher.addObject(4, 0)
	}
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		nbt.setInteger(TAG_CHANCE, chance)
		nbt.setBoolean(TAG_DILATED, dilated)
		nbt.setInteger(TAG_MODE, mode)
		nbt.setBoolean(TAG_PERSISTENT, persistent)
		nbt.setInteger(TAG_TARGET, target)
		nbt.setBoolean(TAG_VOIDING, voiding)
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		chance = nbt.getInteger(TAG_CHANCE)
		dilated = nbt.getBoolean(TAG_DILATED)
		mode = nbt.getInteger(TAG_MODE)
		persistent = nbt.getBoolean(TAG_PERSISTENT)
		target = nbt.getInteger(TAG_TARGET)
		voiding = nbt.getBoolean(TAG_VOIDING)
	}
	
	companion object {
		
		const val CHANCE_INCR = 50
		const val CHANCE_INCR_PERS = 30
		
		const val TAG_CHANCE = "chance"
		const val TAG_DILATED = "dilated"
		const val TAG_MODE = "mode"
		const val TAG_PERSISTENT = "persistent"
		const val TAG_TARGET = "target"
		const val TAG_VOIDING = "voiding"
		
		val damageResonance = DamageSource("resonance").setDamageBypassesArmor()!!
		
		private val EntityResonance.activationTime
			get() = if (mode > 0) (if (dilated) 200 else 100) else (if (dilated) 3 else 40)
		
		fun removeBlockWithDrops(player: EntityPlayer, stack: ItemStack, world: World, x: Int, y: Int, z: Int, dispose: Boolean): Boolean {
			if (world.isRemote || !world.blockExists(x, y, z)) return false
			
			val block = world.getBlock(x, y, z)
			val meta = world.getBlockMetadata(x, y, z)
			
			if (block.isAir(world, x, y, z) || block.getPlayerRelativeBlockHardness(player, world, x, y, z) <= 0 || !block.canHarvestBlock(player, meta)) return false
			
			if (!player.capabilities.isCreativeMode) {
				block.onBlockHarvested(world, x, y, z, meta, player)
				
				if (block.removedByPlayer(world, player, x, y, z, true)) {
					block.onBlockDestroyedByPlayer(world, x, y, z, meta)
					
					val prev = player.foodStats.foodExhaustionLevel
					block.captureDrops(true)
					block.harvestBlock(world, player, x, y, z, meta)
					val drops = block.captureDrops(false)
					player.foodStats.foodExhaustionLevel = prev
					
					if (!world.isRemote && world.gameRules.getGameRuleBooleanValue("doTileDrops") && !world.restoringBlockSnapshots)
						drops.forEach {
							if (dispose && HookReplacerHandler.isDisposable(it.block, it.meta)) return@forEach
							EntityItem(world, x + 0.5, y + 0.5, z + 0.5, it).apply { delayBeforeCanPickup = 10 }.spawn()
						}
				}
				
				ToolCommons.damageItem(stack, 1, player, ItemResonator.MANA_PER_DAMAGE)
			} else
				world.setBlockToAir(x, y, z)
			
			if (!world.isRemote && ConfigHandler.blockBreakParticles && ConfigHandler.blockBreakParticlesTool)
				world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta shl 12))
			
			return true
		}
	}
}
