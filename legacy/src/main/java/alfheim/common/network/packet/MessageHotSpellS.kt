package alfheim.common.network.packet

import alfheim.api.network.AlfheimPacket
import alfheim.common.core.handler.CardinalSystem
import net.minecraft.entity.player.EntityPlayerMP

class MessageHotSpellS(var slot: Int, var id: Int): AlfheimPacket<MessageHotSpellS>() {
	
	override fun handleServer(player: EntityPlayerMP) {
		CardinalSystem.HotSpellsSystem.setHotSpellID(player, slot, id)
	}
}