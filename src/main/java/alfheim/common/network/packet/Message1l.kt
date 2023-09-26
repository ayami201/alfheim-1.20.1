package alfheim.common.network.packet

import alexsocol.asjlib.mc
import alfheim.api.network.AlfheimPacket
import alfheim.common.network.M1l

class Message1l(t: M1l, var data1: Long, var type: Int = t.ordinal) : AlfheimPacket<Message1l>() {
	
	override fun handleClient() {
		when (M1l.entries[type]) {
			M1l.SEED -> mc.theWorld.worldInfo.randomSeed = data1
		}
	}
}