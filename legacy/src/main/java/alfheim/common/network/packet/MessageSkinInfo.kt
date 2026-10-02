package alfheim.common.network.packet

import alfheim.api.network.AlfheimPacket
import alfheim.client.core.handler.CardinalSystemClient

class MessageSkinInfo(var name: String, var isFemale: Boolean, var isSkinOn: Boolean): AlfheimPacket<MessageSkinInfo>() {
	
	override fun handleClient() {
		CardinalSystemClient.playerSkinsData[name] = isFemale to isSkinOn
	}
}