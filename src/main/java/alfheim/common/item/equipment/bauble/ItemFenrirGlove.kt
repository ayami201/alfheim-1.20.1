package alfheim.common.item.equipment.bauble

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.AlfheimCore
import alfheim.common.core.util.EntityDamageSourceIndirectSpell
import alfheim.common.item.AlfheimItems
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.common.eventhandler.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EntityDamageSourceIndirect
import net.minecraftforge.event.entity.living.LivingHurtEvent
import travellersgear.api.TravellersGearAPI

class ItemFenrirGlove: ItemBaubleGlove("FenrirGlove") {
	
	companion object {
		
		init {
			eventForge()
		}
		
		@SubscribeEvent(priority = EventPriority.LOWEST)
		fun onLivingHurt(e: LivingHurtEvent) {
			if (e.source is EntityDamageSourceIndirect || e.source is EntityDamageSourceIndirectSpell || e.source.damageType != "player") return
			val attacker = e.source.entity as? EntityPlayer ?: return
			
			if (Vector3.entityDistance(attacker, e.entityLiving) > 5) return
			val count = if (AlfheimCore.TravellersGearLoaded) {
				if (TravellersGearAPI.getExtendedInventory(attacker)[2]?.item === AlfheimItems.fenrirGlove) 2 else 0
			} else {
				val baubs = PlayerHandler.getPlayerBaubles(attacker)
				var count = 0
				if (baubs[1]?.item === AlfheimItems.fenrirGlove) count++
				if (baubs[2]?.item === AlfheimItems.fenrirGlove) count++
				count
			}
			
			if (count == 0) return
			
			if (attacker.heldItem == null || attacker.heldItem.item === AlfheimItems.fenrirClaws)
				e.ammount *= 1.35f
			
			if (ASJUtilities.chance(count * 5))
				e.source.setDamageBypassesArmor()
		}
	}
}
