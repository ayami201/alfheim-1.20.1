package alfheim.api.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.event.PlayerChangedRaceEvent
import alfheim.client.core.handler.CardinalSystemClient
import alfheim.common.core.handler.CardinalSystem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.common.MinecraftForge

enum class EnumRace(val rgbColor: Int, val enumColor: EnumChatFormatting) {
	
	HUMAN(0xFFFFFF, EnumChatFormatting.WHITE),
	SALAMANDER(0xb61f24, EnumChatFormatting.DARK_RED),
	SYLPH(0x5ee52e, EnumChatFormatting.GREEN),
	CAITSITH(0xcdb878, EnumChatFormatting.YELLOW),
	POOKA(0x99cb3b, EnumChatFormatting.GOLD),
	GNOME(0x816b57, EnumChatFormatting.DARK_GREEN),
	LEPRECHAUN(0x6d6b7b, EnumChatFormatting.GRAY),
	SPRIGGAN(0x282739, EnumChatFormatting.WHITE),
	UNDINE(0x40c0a4, EnumChatFormatting.AQUA),
	IMP(0x786a89, EnumChatFormatting.LIGHT_PURPLE),
	ALV(0xFFEE99, EnumChatFormatting.WHITE);
	
	fun glColor(alpha: Double) {
		ASJRenderHelper.glColor1u(ASJRenderHelper.addAlpha(rgbColor, (alpha * 255).I))
	}
	
	companion object {
		
		operator fun get(id: Int) = EnumRace.entries.getOrNull(id) ?: HUMAN
		
		operator fun get(player: EntityPlayer): EnumRace {
			val id = if (ASJUtilities.isServer)
				CardinalSystem.forPlayer(player).raceID
			else
				CardinalSystemClient.playerRaceIDs[player.commandSenderName] ?: 0
			
			return get(id)
		}
		
		fun getRaceID(player: EntityPlayer): Int {
			return if (ASJUtilities.isServer)
				CardinalSystem.forPlayer(player).raceID
			else
				CardinalSystemClient.playerRaceIDs[player.commandSenderName] ?: 0
		}
		
		operator fun set(player: EntityPlayer, race: EnumRace) {
			if (ASJUtilities.isServer)
				CardinalSystem.forPlayer(player).raceID = race.ordinal
			else
				CardinalSystemClient.playerRaceIDs[player.commandSenderName] = race.ordinal
			
			MinecraftForge.EVENT_BUS.post(PlayerChangedRaceEvent(player, player.race, race))
		}
		
		internal fun setRaceID(player: EntityPlayer, raceID: Int) {
			if (ASJUtilities.isServer)
				CardinalSystem.forPlayer(player).raceID = raceID
			else
				CardinalSystemClient.playerRaceIDs[player.commandSenderName] = raceID
		}
	}
}

var EntityPlayer.race
	get() = EnumRace[this]
	set(value) {
		EnumRace[this] = value
	}

/**
 * Internal Alfheim value, please, don't set it unless you know what you are doing
 * <br>
 * and fire [an event][alfheim.api.event.PlayerChangedRaceEvent] if needed
 */
var EntityPlayer.raceID
	get() = EnumRace.getRaceID(this)
	internal set(value) {
		EnumRace.setRaceID(this, value)
	}