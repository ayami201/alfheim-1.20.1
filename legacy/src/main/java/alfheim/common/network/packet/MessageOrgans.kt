package alfheim.common.network.packet

import alexsocol.asjlib.mc
import alfheim.api.network.AlfheimPacket
import alfheim.common.item.ItemOrgans.Companion.heart
import alfheim.common.item.ItemOrgans.Companion.liver
import alfheim.common.item.ItemOrgans.Companion.lungs
import alfheim.common.item.ItemOrgans.Companion.nerves

class MessageOrgans(var heart: Int, var lungs: Int, var liver: Int, var nerves: Int): AlfheimPacket<MessageOrgans>() {
	
	override fun handleClient() {
		mc.thePlayer.heart = heart
		mc.thePlayer.lungs = lungs
		mc.thePlayer.liver = liver
		mc.thePlayer.nerves = nerves
	}
}
