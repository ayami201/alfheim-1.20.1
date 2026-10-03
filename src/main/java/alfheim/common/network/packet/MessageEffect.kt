package alfheim.common.network.packet

// PORT: импорты 1.20.1; зелья 1.7.10 (Potion, PotionEffect) — alfheim.port.legacy
import alexsocol.asjlib.*
import alfheim.api.network.AlfheimPacket
import alfheim.port.legacy.*
import alfheim.port.legacy.Potion1710 as Potion
import net.minecraft.world.entity.*
import net.minecraft.world.entity.LivingEntity as EntityLivingBase

/**
 * @param state 1 - add, 0 - update, -1 - remove
 */
class MessageEffect(var entity: Int, var id: Int, var dur: Int, var amp: Int, var readd: Boolean = false, var state: Byte = 1): AlfheimPacket<MessageEffect>() {
	
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
	
	// PORT: имена 1.7.10
	private val net.minecraft.client.Minecraft.theWorld get() = level!!
	private fun net.minecraft.world.level.Level.getEntityByID(id: Int) = getEntity(id)
	private fun EntityLivingBase.getAttributeMap() = attributes
}

// PORT: имя 1.7.10; вне класса — его зовёт и второй конструктор
private val Entity.entityId get() = id