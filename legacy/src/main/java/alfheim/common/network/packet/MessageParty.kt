package alfheim.common.network.packet

import alfheim.api.network.AlfheimPacket
import alfheim.client.core.handler.CardinalSystemClient
import alfheim.common.core.handler.CardinalSystem
import io.netty.buffer.ByteBuf

class MessageParty(var party: CardinalSystem.PartySystem.Party): AlfheimPacket<MessageParty>() {
	
	override fun fromCustomBytes(buf: ByteBuf) {
		party = CardinalSystem.PartySystem.Party.read(buf)
	}

	override fun toCustomBytes(buf: ByteBuf) {
		party.write(buf)
	}

	override fun handleClient() {
		CardinalSystemClient.PlayerSegmentClient.party = party
		CardinalSystemClient.PlayerSegmentClient.partyIndex = 0
	}
}