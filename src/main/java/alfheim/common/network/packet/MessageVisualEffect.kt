package alfheim.common.network.packet

import alexsocol.asjlib.network.ASJPacket
import alfheim.api.network.AlfheimPacket
import alfheim.client.render.world.VisualEffectHandlerClient
import io.netty.buffer.ByteBuf

class MessageVisualEffect(var type: Int, vararg var data: Double): ASJPacket(), AlfheimPacket<MessageVisualEffect> {
	override fun fromCustomBytes(buf: ByteBuf) {
		data = DoubleArray(readI(buf)) { readD(buf) }
	}

	override fun toCustomBytes(buf: ByteBuf) {
		write(buf, data.size)
		for (d in data) write(buf, d)
	}

	override fun handleClient() {
		VisualEffectHandlerClient.select(VisualEffectHandlerClient.VisualEffects.entries[type], data)
	}
}