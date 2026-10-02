package alfheim.common.network.packet

import alexsocol.asjlib.*
// PORT: импорты 1.7.10 заменены на 1.20.1; импорты кода, который ещё не перенесён, закомментированы до его КТ
//import alfheim.api.ModInfo
import alfheim.api.network.AlfheimPacket
//import alfheim.client.core.handler.CardinalSystemClient
import alfheim.common.core.handler.*
//import alfheim.common.core.handler.SheerColdHandler.cold
//import alfheim.common.core.handler.ragnarok.RagnarokHandler
//import alfheim.common.core.helper.ElvenFlightHelper
import alfheim.common.network.M1d
//import net.minecraft.client.gui.ChatLine
//import net.minecraft.event.ClickEvent
//import net.minecraft.util.*

class Message1d(ty: M1d, var data1: Double, var type: Int = ty.ordinal) : AlfheimPacket<Message1d>() {
	
	override fun handleClient() {
		when (M1d.entries[type]) {
			// PORT: КТ-6 — SheerColdHandler
			M1d.COLD             -> Unit
//			M1d.COLD             -> mc.thePlayer.cold = data1.F
			M1d.DEATH_TIMER      -> AlfheimConfigHandler.deathScreenAddTime = data1.I
			// PORT: КТ-7 — CardinalSystemClient, ElvenFlightHelper
			M1d.ESMABIL          -> Unit
			M1d.ELVEN_FLIGHT_MAX -> Unit
			M1d.KNOWLEDGE        -> Unit
			M1d.LIMBO            -> Unit
			M1d.TIME_STOP_REMOVE -> Unit
//			M1d.ESMABIL          -> CardinalSystemClient.PlayerSegmentClient.esmAbility = data1 != 0.0
//			M1d.ELVEN_FLIGHT_MAX -> {
//				AlfheimConfigHandler.flightTime = data1.I
//				ElvenFlightHelper.max = data1
//			}
//			M1d.KNOWLEDGE        -> {
//				if (data1 == -1.0)
//					CardinalSystemClient.PlayerSegmentClient.knowledge.clear()
//				else
//					CardinalSystemClient.PlayerSegmentClient.knowledge.add("${CardinalSystem.KnowledgeSystem.Knowledge.entries[data1.I]}")
//			}
//			M1d.LIMBO            -> CardinalSystemClient.PlayerSegmentClient.limbo = data1.I
//			M1d.TIME_STOP_REMOVE -> CardinalSystemClient.TimeStopSystemClient.remove(data1.I)
			// PORT: WIP — стадия 2: RLCM шлют только диалоги эльфов (EntityElfDialogLogic, SPEC п. 6)
			M1d.RLCM             -> Unit
//			M1d.RLCM    -> {
//				// sorry anyone whom RUN_COMMAND chat actions may have been deleted by this :sweat_smile: but I don't really care
//				for (it in mc.ingameGUI.chatGUI.chatLines) {
//					it as ChatLine
//					val answer = it.func_151461_a() as? ChatComponentText ?: break
//					val event = answer.chatStyle.chatClickEvent ?: break

//					if (event.action === ClickEvent.Action.RUN_COMMAND)
//						answer.chatStyle.chatClickEvent = null
//				}

//				for (it in mc.ingameGUI.chatGUI.field_146253_i) {
//					it as ChatLine
//					val compositeLine = it.func_151461_a() as? ChatComponentText ?: break

//					for (sib in compositeLine.siblings) {
//						sib as IChatComponent
//						val event = sib.chatStyle.chatClickEvent ?: continue

//						if (event.action === ClickEvent.Action.RUN_COMMAND)
//							sib.chatStyle.chatClickEvent = null
//					}
//				}

//				while (data1-- > 0)
//					mc.ingameGUI.chatGUI.sentMessages.removeLastOrNull() // for safety
//			}
			// PORT: КТ-8 — RagnarokHandler
			M1d.NOSUNMOON        -> Unit
			M1d.GINNUNGAGAP      -> Unit
			M1d.RAGNAROK         -> Unit
//			M1d.NOSUNMOON        -> RagnarokHandler.noSunAndMoon = data1 == 1.0
//			M1d.GINNUNGAGAP      -> RagnarokHandler.ginnungagap = data1 == 1.0
//			M1d.RAGNAROK         -> {
//				if (data1 == -1.0) {
//					RagnarokHandler.ragnarok = false
//					RagnarokHandler.finished = true
//					RagnarokHandler.fogFade = 1f
//					return
//				}

//				RagnarokHandler.ragnarok = data1 < 1
//				RagnarokHandler.fogFade = data1.F

//				if (0 < data1 && data1 < 1)
//					mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, "${ModInfo.MODID}:fenrir.howl", 50f, 0.5f, false)
//			}
		}
	}
}