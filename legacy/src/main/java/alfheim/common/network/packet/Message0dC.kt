package alfheim.common.network.packet

import alexsocol.asjlib.*
import alfheim.api.AlfheimAPI
import alfheim.api.network.AlfheimPacket
import alfheim.client.core.handler.KeyBindingHandlerClient
import alfheim.common.network.M0dc
import net.minecraft.client.renderer.EntityRenderer

class Message0dC(ty: M0dc, var type: Int = ty.ordinal): AlfheimPacket<Message0dC>() {
	
	override fun handleClient() {
		when (M0dc.entries[type]) {
			M0dc.MTSPELL -> {
				val spell = AlfheimAPI.getSpellByIDs(KeyBindingHandlerClient.raceID, KeyBindingHandlerClient.spellID) ?: return
				ASJUtilities.say(mc.thePlayer, "spell.$spell.mtinfo", *spell.usableParams)
			}
			M0dc.SEEME   -> mc.gameSettings.thirdPersonView = 2
			M0dc.SSS     -> for (i in 0 until mc.thePlayer.rng.nextInt(EntityRenderer.shaderResourceLocations.size)) mc.entityRenderer.activateNextShader()
			M0dc.ROLL    -> mc.entityRenderer.camRoll += mc.thePlayer.rng.nextInt(270) + 45
		}
	}
}