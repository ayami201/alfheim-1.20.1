package alfheim.common.network.packet

import alexsocol.asjlib.*
// PORT: импорты 1.7.10 заменены на 1.20.1; импорты кода, который ещё не перенесён, закомментированы до его КТ
//import alfheim.api.AlfheimAPI
import alfheim.api.network.AlfheimPacket
//import alfheim.client.core.handler.KeyBindingHandlerClient
import alfheim.common.network.M0dc
import net.minecraft.client.CameraType

class Message0dC(ty: M0dc, var type: Int = ty.ordinal): AlfheimPacket<Message0dC>() {
	
	override fun handleClient() {
		when (M0dc.entries[type]) {
			// PORT: выпало — MTSPELL шлёт только CommandMTSpellInfo, а MineTweaker отсутствует на 1.20.1 (SPEC, п. 7)
			M0dc.MTSPELL -> Unit
//			M0dc.MTSPELL -> {
//				val spell = AlfheimAPI.getSpellByIDs(KeyBindingHandlerClient.raceID, KeyBindingHandlerClient.spellID) ?: return
//				ASJUtilities.say(mc.thePlayer, "spell.$spell.mtinfo", *spell.usableParams)
//			}
			// PORT: thirdPersonView = 2 — вид от третьего лица спереди
			M0dc.SEEME   -> mc.options.cameraType = CameraType.THIRD_PERSON_FRONT
			// PORT: КТ-6 — постэффекты и крен камеры; шлёт BlockLootbox
			M0dc.SSS     -> Unit
			M0dc.ROLL    -> Unit
//			M0dc.SSS     -> for (i in 0 until mc.thePlayer.rng.nextInt(EntityRenderer.shaderResourceLocations.size)) mc.entityRenderer.activateNextShader()
//			M0dc.ROLL    -> mc.entityRenderer.camRoll += mc.thePlayer.rng.nextInt(270) + 45
		}
	}
}