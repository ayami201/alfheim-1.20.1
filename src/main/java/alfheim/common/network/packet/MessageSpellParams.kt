package alfheim.common.network.packet

import alfheim.api.AlfheimAPI
import alfheim.api.network.AlfheimPacket

class MessageSpellParams(var name: String, var damage: Float, var duration: Int, var efficiency: Double, var radius: Double): AlfheimPacket<MessageSpellParams>() {
	
	override fun handleClient() {
		val spell = AlfheimAPI.getSpellInstance(name) ?: return
		spell.damage = damage
		spell.duration = duration
		spell.efficiency = efficiency
		spell.radius = radius
	}
}