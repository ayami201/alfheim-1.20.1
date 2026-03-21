package alfheim.common.core.handler

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.*
import alfheim.api.entity.*
import alfheim.api.event.PlayerInteractAdequateEvent
import alfheim.common.core.handler.AlfheimConfigHandler.damageAddCold
import alfheim.common.core.handler.AlfheimConfigHandler.damageAddHot
import alfheim.common.core.handler.AlfheimConfigHandler.damageModCold
import alfheim.common.core.handler.AlfheimConfigHandler.damageModHot
import alfheim.common.core.handler.AlfheimConfigHandler.potionIDOverheat
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.item.equipment.bauble.ItemPendant
import alfheim.common.item.equipment.bauble.ItemPendant.Companion.EnumPrimalWorldType.*
import alfheim.common.network.*
import alfheim.common.network.packet.Message1d
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.*
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.*
import net.minecraft.item.*
import net.minecraft.potion.Potion
import net.minecraft.util.MathHelper
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.*
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import vazkii.botania.common.block.tile.TileAltar
import kotlin.math.*

object SheerColdHandler {
	
	const val TAG_SHEER_COLD = "${ModInfo.MODID}.SheerCold"
	
	var EntityLivingBase.cold: Float
		get() = entityData.getFloat(TAG_SHEER_COLD)
		set(value) {
			val prev = entityData.getFloat(TAG_SHEER_COLD)
			entityData.setFloat(TAG_SHEER_COLD, value)
			
			if (prev != value && this is EntityPlayerMP)
				NetworkService.sendTo(Message1d(M1d.COLD, value.D), this)
		}
	
	@SubscribeEvent
	fun onPlayerLoggedIn(e: PlayerLoggedInEvent) {
		NetworkService.sendTo(Message1d(M1d.COLD, e.player.cold.D), e.player as EntityPlayerMP)
	}
	
	@SubscribeEvent
	fun onLivingUpdate(e: LivingUpdateEvent) {
		val target = e.entityLiving
		
		if (!AlfheimConfigHandler.mobTemperature && target !is EntityPlayer) return
		if (target.worldObj.isRemote || !target.isEntityAlive) return
		
		if (target is EntityPlayerMP && target.capabilities.isCreativeMode) {
			target.cold = 0f
			return
		}
		
		val event = SheerColdTickEvent(target)
		if (MinecraftForge.EVENT_BUS.post(event)) return
		
		var value = target.cold
		var defaultDelta = if (value == 0f) 0f else 0.5f * value.sign * -1
		
		if (defaultDelta != 0f) run {
			val (x, y, z) = Vector3.fromEntity(target).mf()
			
			for (i in x.bidiRange(2))
				for (j in y.bidiRange(2))
					for (k in z.bidiRange(2)) run loop@ {
						(target.worldObj.getTileEntity(i, j, k) as? TileAltar)?.apply {
							if (!hasLava || defaultDelta >= 0) return@loop
							
							defaultDelta = -1f
							return@run
						}
						
						val near = target.worldObj.getBlock(i, j, k)
						
						defaultDelta = when (near) {
							in AlfheimAPI.coldBlocks -> if (defaultDelta > 0) 1f else return@loop
							in AlfheimAPI.warmBlocks -> if (defaultDelta < 0) -1f else return@loop
							else                     -> return@loop
						}
						
						return@run
					}
		}
		
		var delta = event.delta ?: if (value > 0) max(-value, defaultDelta) else min(-value, defaultDelta)
		if (delta.isNaN()) delta = 0f
		if (event.delta != null && (event.delta!! > 0 || value > 0) && defaultDelta == -1f) delta = max(defaultDelta + delta, -value)
		if (event.delta != null && (event.delta!! < 0 || value < 0) && defaultDelta == 1f) delta = min(defaultDelta + delta, -value)
		delta = MathHelper.clamp_float(delta, -100f, 100f)
		
		value = MathHelper.clamp_float(value + delta, -100f, 100f)
		
		if (value > 0f && INiflheimEntity.checkProtection(target, MathHelper.ceiling_float_int(value))) value = 0f
		if (value < 0f && IMuspelheimEntity.checkProtection(target, MathHelper.ceiling_float_int(-value))) value = 0f
		
		target.cold = value
		
		if (AlfheimConfigHandler.potionIDOvercold != -1) {
			if (value >= 25f)
				target.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDOvercold, 10, (value / 25).I - 1))
			else
				target.removePotionEffect(AlfheimConfigHandler.potionIDOvercold)
		}
		if (potionIDOverheat != -1) {
			if (value <= -25f)
				target.addPotionEffect(PotionEffectU(
					potionIDOverheat, 10, when {
					value <= -90f -> 2
					value <= -50f -> 1
					else         -> 0
				}))
			else
				target.removePotionEffect(potionIDOverheat)
		}
		
		// DoT instead of constant
		if (target.ticksExisted % 50 != 0) return
		
		if (value >= 100f) target.attackEntityFrom(DamageSourceSpell.nifleice, (target.maxHealth * damageModCold.F + damageAddCold.F))
		if (value <= -100f) target.attackEntityFrom(DamageSourceSpell.soulburn, (target.maxHealth * damageModHot.F + damageAddHot.F))
	}
	
	val neutralSounds = arrayOf("bat.idle", "cat.meow", "chicken.say", "cow.say", "pig.say", "sheep.say", "wolf.bark")
	val hostileSounds = arrayOf("blaze.breathe", "creeper.say", "enderdragon.growl", "enderdragon.wings", "endermen.idle", "endermen.scream", "ghast.moan", "ghast.scream", "magmacube.big", "silverfish.say", "skeleton.say", "slime.big", "spider.say", "wither.idle", "wolf.growl", "zombie.say", "zombiepig.zpig")
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	fun resetCold(e: LivingDeathEvent) {
		e.entityLiving.cold = 0f
	}
	
	@SubscribeEvent
	fun onPlayerOvercold(e: LivingUpdateEvent) {
		val target = e.entityLiving
		
		val cold = target.cold
		
		if (cold >= 25f)
			target.addPotionEffect(PotionEffectU(Potion.moveSlowdown.id, 100, (cold / 25).I - 1))
	}
	
	// additional "lag" with controls - AlfheimHookHandler#updatePlayerMoveState
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	fun onPlayerOverheat(e: LivingUpdateEvent) {
		val player = mc.thePlayer
		if (player !== e.entityLiving) return
		if (ItemPendant.canProtect(player, MUSPELHEIM, 0)) return
		
		val heat = -player.cold
		
		if (heat < 25f) return
		if (player.rng.nextInt(1000) == 0) return player.playSoundAtEntity("mob." + (if (ASJUtilities.chance((heat + 50) * 2)) hostileSounds else neutralSounds).random(), 1f, 1f)
		
		if (heat < 50f) return
		if (player.rng.nextInt(3000) == 0) {
			var entity: Entity? = null
			
			if (ASJUtilities.chance(25))
				while (entity !is EntityLivingBase) {
					entity = EntityList.createEntityByName(EntityList.stringToClassMapping.keys.random(player.rng)!!.toString(), mc.theWorld)
				}
			else {
				val item = Item.itemRegistry.toList().random(player.rng) as? Item ?: return
				
				entity = EntityItem(mc.theWorld, 0.0, 0.0, 0.0, ItemStack(item))
			}
			
			var tries = 50
			do {
				val (x, _, z) = Vector3().rand().mul(64).add(player)
				entity.setPosition(x, mc.theWorld.getTopSolidOrLiquidBlock(x.I, z.I) + 1.0, z)
			} while (!ASJUtilities.isNotInFieldOfVision(entity, player) && --tries > 0)
			
			entity.spawn()
		}
	}
	
	@SubscribeEvent
	fun weakHands(e: PlayerInteractAdequateEvent) {
		val cold = e.player.cold
		if (abs(cold) < 90) return

		if (cold > 0 && ItemPendant.canProtect(e.player, NIFLHEIM, 0)) return
		if (cold < 0 && ItemPendant.canProtect(e.player, MUSPELHEIM, 0)) return

		if (ASJUtilities.chance(0.5))
			e.player.dropOneItem(true)
	}
	
	@Suppress("UNCHECKED_CAST")
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	fun blockTemperatureInfo(e: ItemTooltipEvent) {
		val block = e.itemStack.block
		if (block in AlfheimAPI.coldBlocks)
			addStringToTooltip(e.toolTip as MutableList<Any?>, "alfheimmisc.blockcold")
		else if (block in AlfheimAPI.warmBlocks)
			addStringToTooltip(e.toolTip as MutableList<Any?>, "alfheimmisc.blockwarm")
	}
	
	/**
	 * Event for calculating temperature [delta] to add to [entity]'s temperature.
	 *
	 * General contract for handling this event:
	 * * if you want to add some source, such as cold weather — use [high][EventPriority.HIGH] priority, so protection is processed after it;
	 * * if you want to add some protection, such as warm closes — use [low][EventPriority.LOW] priority;
	 * * use [lowest][EventPriority.LOWEST] and [highest][EventPriority.HIGHEST] priority for special cases;
	 * * [normal][EventPriority.NORMAL] priority at your discretion.
	 *
	 * Canceling event is allowed only if you want to *lock* [entity]'s temperature for some reason.
	 */
	@Cancelable
	class SheerColdTickEvent(entity: EntityLivingBase, var delta: Float? = null): LivingEvent(entity)
}
