package alfheim.common.potion

// PORT: импорты 1.20.1 (MAPPING.md); смерть в режиме MMO (КТ-7) закомментирована вместе со своими строками
import alexsocol.asjlib.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.port.legacy.*
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.ai.attributes.AttributeMap as BaseAttributeMap
import net.minecraftforge.event.*
import net.minecraftforge.event.entity.item.ItemTossEvent
import net.minecraftforge.event.entity.living.LivingHealEvent
import net.minecraftforge.event.entity.player.PlayerEvent.*
import net.minecraftforge.event.level.BlockEvent.*
import net.minecraftforge.event.level.BlockEvent.EntityMultiPlaceEvent as MultiPlaceEvent
import net.minecraftforge.event.level.BlockEvent.EntityPlaceEvent as PlaceEvent
import net.minecraftforge.eventbus.api.*
//import alfheim.client.render.world.VisualEffectHandlerClient
//import net.minecraft.entity.player.*
//import net.minecraft.util.DamageSource

object PotionLeftFlame: PotionAlfheim(AlfheimConfigHandler.potionIDLeftFlame, "leftFlame", false, 0) {
	
	init {
		eventForge()
	}
	
	// PORT: КТ-7 — смерть игрока в режиме MMO: дух без права строить и дальности действия, таймер смерти
	// (VisualEffectHandlerClient.onDeath, GUIDeathTimer), возрождение по окончании (onDeath)
	override fun applyAttributesModifiersToEntity(target: EntityLivingBase?, attributes: BaseAttributeMap, ampl: Int) {
		super.applyAttributesModifiersToEntity(target, attributes, ampl)
		/*
		if (AlfheimConfigHandler.enableMMO && target is EntityPlayer) {
			target.capabilities.allowEdit = false
			target.capabilities.allowFlying = true
			target.capabilities.disableDamage = true
			target.capabilities.isFlying = true
			target.sendPlayerAbilities()
			if (target is EntityPlayerMP) target.theItemInWorldManager.blockReachDistance = 0.1
			if (ASJUtilities.isClient) VisualEffectHandlerClient.onDeath(target)
		}
		*/
	}
	
	override fun removeAttributesModifiersFromEntity(target: EntityLivingBase?, attributes: BaseAttributeMap, ampl: Int) {
		super.removeAttributesModifiersFromEntity(target, attributes, ampl)
		/*
		if (AlfheimConfigHandler.enableMMO && target is EntityPlayer) {
			target.capabilities.allowEdit = true
			target.capabilities.allowFlying = false
			target.capabilities.disableDamage = false
			target.capabilities.isFlying = false
			target.sendPlayerAbilities()
			if (target is EntityPlayerMP) target.theItemInWorldManager.blockReachDistance = 5.0
		}
		
		if (ampl != 0) return
		
		target?.let {
			it.dataWatcher.updateObject(6, 0f)
			it.onDeath(DamageSource("Respawn"))
		}
		*/
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun onBreakSpeed(e: BreakSpeed) {
		if (check(e.entityLiving)) e.newSpeed = 0f
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun onHarvestCheck(e: HarvestCheck) {
		if (check(e.entityLiving)) e.success = false
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun onBlockBreak(e: BreakEvent) {
		if (check(e.player)) e.isCanceled = true
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun onBlockPlace(e: PlaceEvent) {
		if (check(e.player)) e.isCanceled = true
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun onBlockMultiPlace(e: MultiPlaceEvent) {
		if (check(e.player)) e.isCanceled = true
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun onPlayerSaid(e: ServerChatEvent) {
		if (check(e.player)) e.isCanceled = true
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun onPlayerDrop(e: ItemTossEvent) {
		if (check(e.player)) {
			e.isCanceled = true
			// PORT: entityItem.entityItem → entity.item, addItemStackToInventory → inventory.add
			e.player.inventory.add(e.entity.item.copy())
//			e.player.inventory.addItemStackToInventory(e.entityItem.entityItem.copy())
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun onHeal(e: LivingHealEvent) {
		if (check(e.entityLiving)) e.isCanceled = true
	}
	
	@SubscribeEvent
	fun onChatEvent(e: ServerChatEvent) {
		if (check(e.player)) e.isCanceled = true
	}
	
	@SubscribeEvent
	fun onCommandEvent(e: CommandEvent) {
		if (check(e.sender as? EntityLivingBase)) e.isCanceled = true
	}
	
	fun check(e: EntityLivingBase?) = AlfheimConfigHandler.enableMMO && e?.isPotionActive(this) == true
	
	// PORT: имена полей 1.7.10: игрок события установки блока — тот, кто ставит; отправитель команды — её источник
	private val PlaceEvent.player get() = entity as? EntityLivingBase
	private val CommandEvent.sender get() = parseResults.context.source.entity
}
