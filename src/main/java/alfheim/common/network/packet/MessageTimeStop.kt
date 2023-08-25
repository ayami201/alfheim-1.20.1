package alfheim.common.network.packet

import alexsocol.asjlib.network.ASJPacket
import alfheim.api.network.AlfheimPacket
import alfheim.client.core.handler.CardinalSystemClient
import alfheim.common.core.handler.CardinalSystem
import io.netty.buffer.ByteBuf

class MessageTimeStop(var party: CardinalSystem.PartySystem.Party?, var x: Double, var y: Double, var z: Double, var id: Int): ASJPacket(), AlfheimPacket<MessageTimeStop> {
	override fun fromCustomBytes(buf: ByteBuf) {
		if (readZ(buf)) party = CardinalSystem.PartySystem.Party.read(buf)
	}

	override fun toCustomBytes(buf: ByteBuf) {
		write(buf, party != null)
		if (party != null) party!!.write(buf)
	}

	override fun handleClient() {
		if (party == null) party = CardinalSystem.PartySystem.Party()
		CardinalSystemClient.TimeStopSystemClient.stop(x, y, z, party!!, id)
	}
}