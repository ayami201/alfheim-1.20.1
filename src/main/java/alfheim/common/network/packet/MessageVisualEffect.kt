package alfheim.common.network.packet

import alfheim.api.network.AlfheimPacket
// PORT: КТ-1 — VisualEffectHandlerClient переносится отдельным шагом этой КТ
//import alfheim.client.render.world.VisualEffectHandlerClient
import io.netty.buffer.ByteBuf

class MessageVisualEffect(var type: Int, vararg var data: Double): AlfheimPacket<MessageVisualEffect>() {
	
	override fun fromCustomBytes(buf: ByteBuf) {
		data = DoubleArray(readI(buf)) { readD(buf) }
	}

	override fun toCustomBytes(buf: ByteBuf) {
		write(buf, data.size)
		for (d in data) write(buf, d)
	}

	override fun handleClient() {
//		VisualEffectHandlerClient.select(VisualEffectHandlerClient.VisualEffects.entries[type], data)
	}
}