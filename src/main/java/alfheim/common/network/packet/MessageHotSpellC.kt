package alfheim.common.network.packet

import alexsocol.asjlib.network.ASJPacket
import alfheim.api.network.AlfheimPacket
import alfheim.client.core.handler.CardinalSystemClient
import io.netty.buffer.ByteBuf

class MessageHotSpellC(var ids: IntArray): ASJPacket(), AlfheimPacket<MessageHotSpellC> {
	override fun fromCustomBytes(buf: ByteBuf) {
		ids = IntArray(12) { readI(buf) }
	}

	override fun toCustomBytes(buf: ByteBuf) {
		for (id in ids) write(buf, id)
	}

	override fun handleClient() {
		CardinalSystemClient.PlayerSegmentClient.hotSpells = ids.clone()
	}
}