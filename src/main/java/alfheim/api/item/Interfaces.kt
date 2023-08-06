package alfheim.api.item

import alexsocol.asjlib.get
import alfheim.AlfheimCore
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraftforge.event.entity.living.LivingEvent
import travellersgear.api.TravellersGearAPI
import vazkii.botania.common.item.equipment.bauble.ItemTravelBelt
import java.util.HashSet

private inline fun <reified T> scanEquipmentForItems(entity: EntityLivingBase, apply: (T, ItemStack) -> Unit) {
	for (i in 0..4) {
		val stack = entity.getEquipmentInSlot(i) ?: continue
		val item = stack.item
		
		if (item !is T) continue
		apply(item as T, stack)
	}
	
	if (entity !is EntityPlayer) return
	
	val baubles = PlayerHandler.getPlayerBaubles(entity)
	for (i in 0 until baubles.sizeInventory) {
		val stack = baubles[i] ?: continue
		val item = stack.item
		
		if (item !is T) continue
		apply(item as T, stack)
	}
	
	if (!AlfheimCore.TravellersGearLoaded) return
	
	val travGear = TravellersGearAPI.getExtendedInventory(entity)
	travGear.forEach { stack ->
		stack ?: return@forEach
		val item = stack.item
		
		if (item !is T) return@forEach
		apply(item as T, stack)
	}
	
	TravellersGearAPI.setExtendedInventory(entity, travGear)
}

interface ISpeedUpItem {
	
	fun getSpeedUp(wearer: EntityLivingBase, stack: ItemStack): Float
	
	companion object {
		
		@SubscribeEvent
		fun updateSpeed(event: LivingEvent.LivingUpdateEvent) {
			val entity = event.entityLiving
			if (entity.moveForward <= 0f) return
			
			var speed = 0f
			scanEquipmentForItems<ISpeedUpItem>(entity) { item, stack -> speed += item.getSpeedUp(entity, stack) }
			if (speed == 0f) return
			
			entity.moveFlying(0f, 1f, speed)
		}
	}
}

interface IStepupItem {
	
	fun shouldHaveStepup(wearer: EntityLivingBase, stack: ItemStack): Boolean
	
	companion object {
		
		var playersWithStepup = HashSet<String>()
		
		@SubscribeEvent
		fun updateStep(event: LivingEvent.LivingUpdateEvent) {
			val entity = event.entityLiving
			
			val s = if (entity is EntityPlayer) ItemTravelBelt.playerStr(entity) else entity.uniqueID.toString()
			
			var shouldStepup = false
			scanEquipmentForItems<IStepupItem>(entity) { item, stack -> if (item.shouldHaveStepup(entity, stack)) shouldStepup = true }
			
			if (playersWithStepup.contains(s)) {
				if (shouldStepup) {
					if (entity.isSneaking) entity.stepHeight = 0.50001f // Not 0.5F because that is the default
					else entity.stepHeight = 1f
				} else {
					entity.stepHeight = 0.5f
					playersWithStepup.remove(s)
				}
			} else if (shouldStepup) {
				playersWithStepup.add(s)
				entity.stepHeight = 1f
			}
		}
	}
}