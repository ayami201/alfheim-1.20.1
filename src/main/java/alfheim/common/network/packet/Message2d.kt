package alfheim.common.network.packet

import alexsocol.asjlib.*
import alfheim.api.AlfheimAPI
import alfheim.api.entity.*
import alfheim.api.network.AlfheimPacket
import alfheim.api.spell.SpellBase
import alfheim.client.core.handler.CardinalSystemClient
import alfheim.client.core.proxy.ClientProxy
import alfheim.common.core.helper.flight
import alfheim.common.entity.spell.EntitySpellFireball
import alfheim.common.network.M2d
import net.minecraft.entity.EntityLivingBase

class Message2d(ty: M2d, var data1: Double, var data2: Double, var type: Int = ty.ordinal): AlfheimPacket<Message2d>() {
	
	override fun handleClient() {
		when (M2d.entries[type]) {
			M2d.ATTRIBUTE -> {
				when (data1.I) {
					0 -> mc.thePlayer.raceID = data2.I
					1 -> mc.thePlayer.flight = data2
				}
			}

			M2d.COOLDOWN -> {
				when (if (data2 > 0) SpellBase.SpellCastResult.OK else SpellBase.SpellCastResult.entries[(-data2).I]) {
					SpellBase.SpellCastResult.DESYNC -> throw IllegalArgumentException("Client-server spells desynchronization. Not found spell for ${EnumRace[data1.I shr 28 and 0xF]} with id ${data1.I and 0xFFFFFFF}")
					SpellBase.SpellCastResult.NOMANA -> ASJUtilities.say(mc.thePlayer, "alfheimmisc.cast.nomana")
					SpellBase.SpellCastResult.NOTALLOW -> ASJUtilities.say(mc.thePlayer, "alfheimmisc.cast.notallow")
					SpellBase.SpellCastResult.NOTARGET -> ASJUtilities.say(mc.thePlayer, "alfheimmisc.cast.notarget")
					SpellBase.SpellCastResult.NOTREADY -> Unit /*ASJUtilities.say(mc.thePlayer, "alfheimmisc.cast.notready");*/
					SpellBase.SpellCastResult.NOTSEEING -> ASJUtilities.say(mc.thePlayer, "alfheimmisc.cast.notseeing")
					SpellBase.SpellCastResult.OBSTRUCT -> ASJUtilities.say(mc.thePlayer, "alfheimmisc.cast.obstruct")
					SpellBase.SpellCastResult.OK -> CardinalSystemClient.SpellCastingSystemClient.setCoolDown(
						AlfheimAPI.getSpellByIDs(
							data1.I shr 28 and 0xF,
							data1.I and 0xFFFFFFF
						), data2.I
					)

					SpellBase.SpellCastResult.WRONGTGT -> ASJUtilities.say(mc.thePlayer, "alfheimmisc.cast.wrongtgt")
				}
			}

			M2d.UUID -> CardinalSystemClient.PlayerSegmentClient.party?.setUUID(data2.I, data1.I)

			M2d.MODES -> {
				if (data1 > 0) ClientProxy.enableESM() else ClientProxy.disableESM()
				if (data2 > 0) ClientProxy.enableMMO() else ClientProxy.disableMMO()
			}

			M2d.FIREBALLSYNC -> {
				(mc.theWorld.getEntityByID(data1.I) as? EntitySpellFireball)?.target =
					mc.theWorld.getEntityByID(data2.I) as? EntityLivingBase
			}
		}
	}
}