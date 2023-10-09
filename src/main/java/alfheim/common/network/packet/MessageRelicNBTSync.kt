package alfheim.common.network.packet

import alfheim.api.network.AlfheimPacket
import alfheim.common.core.asm.hook.extender.RelicHooks
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTTagCompound

class MessageRelicNBTSync(var syncMap: MutableMap<String, NBTTagCompound>): AlfheimPacket<MessageRelicNBTSync>() {
	
	override fun toCustomBytes(buf: ByteBuf) {
		write(buf, syncMap.entries.size)
		
		syncMap.entries.forEach { (key, nbt) ->
			write(buf, key)
			write(buf, nbt)
		}
	}
	
	override fun fromCustomBytes(buf: ByteBuf) {
		syncMap = HashMap()
		
		repeat(readI(buf)) {
			syncMap[readLjavalangString(buf) ?: return@repeat] = readLnetminecraftnbtNBTTagCompound(buf) ?: return@repeat
		}
	}
	
	override fun handleClient() {
		syncMap.forEach { (key, nbt) ->
			RelicHooks.relicNBTClient[key] = nbt
		}
	}
}