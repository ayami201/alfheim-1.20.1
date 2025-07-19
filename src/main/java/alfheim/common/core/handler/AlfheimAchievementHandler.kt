package alfheim.common.core.handler

import alexsocol.asjlib.*
import alfheim.api.event.AlfheimModeChangedEvent
import alfheim.common.achievement.AlfheimAchievements
import alfheim.common.item.AlfheimItems
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.item.EntityFireworkRocket
import net.minecraft.entity.passive.EntityHorse
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.*
import net.minecraft.stats.AchievementList
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import vazkii.botania.common.item.ModItems

object AlfheimAchievementHandler {
	
	init {
		eventForge()
	}
	
	@SubscribeEvent
	fun grass(e: PlayerInteractEvent) {
		if (e.entityPlayer.dimension != AlfheimConfigHandler.dimensionIDAlfheim || e.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return
		val block = e.world.getBlock(e.x, e.y, e.z)
		if (block !== Blocks.grass && block !== Blocks.tallgrass) return
		e.entityPlayer.triggerAchievement(AlfheimAchievements.grass)
	}
	
	@SubscribeEvent
	fun onLivingUpdate(e: LivingUpdateEvent) {
		val player = e.entityLiving as? EntityPlayer ?: return
		
		firework(player)
		wingedHussars(player)
	}
	
	fun firework(player: EntityPlayer) {
		if (player.ridingEntity is EntityFireworkRocket) player.triggerAchievement(AlfheimAchievements.firework)
	}
	
	fun wingedHussars(player: EntityPlayer) {
		val armorFlag = (0..4).all {
			player.getEquipmentInSlot(it)?.item == when (it) {
				0    -> AlfheimItems.realitySword
				1    -> AlfheimItems.elvoriumBoots
				2    -> AlfheimItems.elvoriumLeggings
				3    -> AlfheimItems.elvoriumChestplate
				4    -> AlfheimItems.elvoriumHelmet
				else -> Blocks.air.toItem()
			}
		}
		
		val tiaraStack = PlayerHandler.getPlayerBaubles(player)[0]
		val baublesFlag = tiaraStack?.item == ModItems.flightTiara && tiaraStack?.meta == 4
		
		val horse = player.ridingEntity
		val horseFlag = horse is EntityHorse &&
						horse.horseType == 0 &&
						horse.horseVariant and 255 == 1 &&
						horse.horseChest[0]?.item == Items.saddle &&
						horse.horseChest[1]?.item == Items.golden_horse_armor
		
		if (armorFlag && baublesFlag && horseFlag) player.triggerAchievement(AlfheimAchievements.wingedHussar)
	}
	
	@SubscribeEvent
	fun hideEsm(e: AlfheimModeChangedEvent) {
		val enabled = e.esm && !e.esmOld
		val disabled = !e.esm && e.esmOld
		
		if (disabled) {
			val set = setOf(AlfheimAchievements.fuckup)
			AchievementList.achievementList.removeAll(set)
			AlfheimAchievements.page.achievements.removeAll(set)
		} else if (enabled) {
			if (AlfheimAchievements.fuckup !in AchievementList.achievementList) AchievementList.achievementList.add(AlfheimAchievements.fuckup)
			if (AlfheimAchievements.fuckup !in AlfheimAchievements.page.achievements) AlfheimAchievements.page.achievements.add(AlfheimAchievements.fuckup)
		}
	}
}