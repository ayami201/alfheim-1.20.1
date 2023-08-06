package alfheim.common.network.packet

import alexsocol.asjlib.*
import alexsocol.asjlib.network.ASJPacket
import alfheim.api.network.AlfheimPacket
import alfheim.client.core.handler.CardinalSystemClient
import alfheim.client.core.proxy.ClientProxy
import alfheim.common.core.handler.CardinalSystem.PartySystem.Party.PartyStatus
import alfheim.common.network.M3d

class Message3d(ty: M3d, var data1: Double, var data2: Double, var data3: Double, var type: Int = ty.ordinal) : ASJPacket(), AlfheimPacket<Message3d> {
	override fun handleClient() {
		when (M3d.entries[type]) {
			M3d.KEY_BIND     -> Unit

			M3d.PARTY_STATUS -> {
				when (PartyStatus.entries[data1.I]) {
					PartyStatus.DEAD      -> CardinalSystemClient.PlayerSegmentClient.party?.setDead(data2.I, data3.I == -10)
					PartyStatus.MANA      -> CardinalSystemClient.PlayerSegmentClient.party?.setMana(data2.I, data3.I)
					PartyStatus.HEALTH    -> CardinalSystemClient.PlayerSegmentClient.party?.setHealth(data2.I, data3.F)
					PartyStatus.MAXHEALTH -> CardinalSystemClient.PlayerSegmentClient.party?.setMaxHealth(data2.I, data3.F)
					PartyStatus.TYPE      -> CardinalSystemClient.PlayerSegmentClient.party?.setType(data2.I, data3.I)
				}
			}

			M3d.WEATHER -> {
				if (mc.theWorld == null) return

				mc.theWorld.setRainStrength(if (data1.I > 0) 1f else 0f)
				mc.theWorld.setThunderStrength(if (data1.I > 1) 1f else 0f)

				val info = mc.theWorld.worldInfo
				info.isRaining = data1.I > 0
				info.rainTime = data2.I
				info.isThundering = data1.I > 1
				info.thunderTime = data3.I
			}

			M3d.TOGGLER      -> ClientProxy.toggelModes(data1 > 0, data2.I and 1 > 0, data3.I and 1 > 0, data2.I shr 1 and 1 > 0, data3.I shr 1 and 1 > 0)
		}
	}
}