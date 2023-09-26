package alfheim.common.network.packet

import alfheim.api.network.AlfheimPacket
import alfheim.common.item.rod.*
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTTagCompound

class MessageRedstoneSignalsSync(var signals: HashSet<RedstoneSignal>): AlfheimPacket<MessageRedstoneSignalsSync>() {

	override fun fromCustomBytes(buf: ByteBuf) {
		val size = readI(buf)
		signals = HashSet(size)

		repeat(size) { signals.add(RedstoneSignal.readFromNBT(readLnetminecraftnbtNBTTagCompound(buf) ?: return@repeat)) }
	}

	override fun toCustomBytes(buf: ByteBuf) {
		buf.writeInt(signals.size)

		signals.forEach { write(buf, it.writeToNBT(NBTTagCompound())) }
	}

	override fun handleClient() {
		RedstoneSignalHandlerClient.redstoneSignals = signals
	}
}