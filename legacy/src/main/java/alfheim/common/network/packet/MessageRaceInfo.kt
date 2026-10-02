package alfheim.common.network.packet

import alfheim.api.network.AlfheimPacket
import alfheim.client.core.handler.CardinalSystemClient

class MessageRaceInfo(var name: String, var raceID: Int): AlfheimPacket<MessageRaceInfo>() {
	
	override fun handleClient() {
		CardinalSystemClient.playerRaceIDs[name] = raceID
	}
}