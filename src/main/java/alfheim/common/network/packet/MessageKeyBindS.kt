package alfheim.common.network.packet

import alfheim.api.network.AlfheimPacket
import alfheim.client.core.handler.KeyBindingHandlerClient
import alfheim.common.core.handler.KeyBindingHandler
import alfheim.common.entity.EntityLolicorn
import alfheim.common.item.relic.ItemAkashicRecords
import net.minecraft.entity.player.EntityPlayerMP

class MessageKeyBindS(var action: Int, var state: Boolean, var data: Int): AlfheimPacket<MessageKeyBindS>() {
	
	override fun handleServer(player: EntityPlayerMP) {
		when (KeyBindingHandlerClient.KeyBindingIDs.entries[action]) {
			KeyBindingHandlerClient.KeyBindingIDs.AKASHIC -> ItemAkashicRecords.unpack(player)
			KeyBindingHandlerClient.KeyBindingIDs.CORN    -> EntityLolicorn.call(player)
			KeyBindingHandlerClient.KeyBindingIDs.FLIGHT  -> KeyBindingHandler.enableFlight(player, state)
			KeyBindingHandlerClient.KeyBindingIDs.ESMABIL -> KeyBindingHandler.toggleESMAbility(player)
			KeyBindingHandlerClient.KeyBindingIDs.CAST    -> KeyBindingHandler.cast(player, state, data)
			KeyBindingHandlerClient.KeyBindingIDs.UNCAST  -> KeyBindingHandler.unCast(player)
			KeyBindingHandlerClient.KeyBindingIDs.SEL     -> KeyBindingHandler.select(player, state, data)
			KeyBindingHandlerClient.KeyBindingIDs.SECRET  -> KeyBindingHandler.secret(player)
		}
	}
}