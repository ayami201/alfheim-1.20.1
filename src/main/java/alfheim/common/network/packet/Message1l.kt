package alfheim.common.network.packet

// PORT: импорт mc не нужен, пока ветка SEED закомментирована
//import alexsocol.asjlib.mc
import alfheim.api.network.AlfheimPacket
import alfheim.common.network.M1l

class Message1l(t: M1l, var data1: Long, var type: Int = t.ordinal) : AlfheimPacket<Message1l>() {
	
	override fun handleClient() {
		when (M1l.entries[type]) {
			// PORT: КТ-5 — зерно мира на клиенте; в 1.20.1 его нет в данных клиентского мира, решается вместе с WorldEngine
			M1l.SEED -> Unit
//			M1l.SEED -> mc.theWorld.worldInfo.randomSeed = data1
		}
	}
}