package alfheim.common.potion

import alfheim.common.core.handler.AlfheimConfigHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.event.entity.player.EntityInteractEvent

object PotionWhiteWine: PotionAlfheim(AlfheimConfigHandler.potionIDWhiteWine, "whiteWine", false, 0xE5FFE5) {
	
	@SubscribeEvent
	fun onInteract(e: EntityInteractEvent) {
		if (!e.entityPlayer.isPotionActive(this)) return
		if (e.target.riddenByEntity != null) return
		if (e.entityPlayer.isRiding) return
		
		e.entityPlayer.mountEntity(e.target)
	}
}
