package alfheim.common.network.packet

import alexsocol.asjlib.*
import alexsocol.asjlib.network.ASJPacket
import alfheim.api.network.AlfheimPacket
import net.minecraft.entity.*
import net.minecraft.potion.*

/**
 * @param state 1 - add, 0 - update, -1 - remove
 */
class MessageEffect @JvmOverloads constructor(var entity: Int, var id: Int, var dur: Int, var amp: Int, var readd: Boolean = false, var state: Byte = 1): ASJPacket(), AlfheimPacket<MessageEffect> {
	constructor(e: Entity, p: PotionEffect): this(e.entityId, p.potionID, p.duration, p.amplifier)

	override fun handleClient() {
		val e = mc.theWorld.getEntityByID(entity)

		if (e !is EntityLivingBase) return

		val pe = e.getActivePotionEffect(id)

		when (state.toInt()) {
			1 -> {
				if (pe == null) {
					e.addPotionEffect(PotionEffect(id, dur, amp))
					Potion.potionTypes[id].applyAttributesModifiersToEntity(e, e.getAttributeMap(), amp)
				} else {
					if (readd) Potion.potionTypes[id].removeAttributesModifiersFromEntity(e, e.getAttributeMap(), amp)
					pe.amplifier = amp
					pe.duration = dur
					if (readd) Potion.potionTypes[id].applyAttributesModifiersToEntity(e, e.getAttributeMap(), amp)
				}
			}

			0 -> {
				if (pe == null) {
					e.addPotionEffect(PotionEffect(id, dur, amp))
					Potion.potionTypes[id].applyAttributesModifiersToEntity(e, e.getAttributeMap(), amp)
				} else {
					if (readd) Potion.potionTypes[id].removeAttributesModifiersFromEntity(e, e.getAttributeMap(), amp)
					pe.amplifier = amp
					pe.duration = dur
					if (readd) Potion.potionTypes[id].applyAttributesModifiersToEntity(e, e.getAttributeMap(), amp)
				}
			}

			-1 -> {
				if (pe != null) {
					e.removePotionEffect(id)
					Potion.potionTypes[id].removeAttributesModifiersFromEntity(e, e.getAttributeMap(), amp)
				}
			}
		}
	}
}