package alfheim.common.network.packet

import alexsocol.asjlib.*
import alfheim.api.lib.LibResourceLocations
import alfheim.api.network.AlfheimPacket
import alfheim.common.item.equipment.bauble.ItemElvenDisguise.Companion.TAG_SKIN
import baubles.common.lib.PlayerHandler
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import java.io.FileNotFoundException

class MessageDisguise(var skin: String, var gurl: Boolean, var race: Int, var respond: Boolean, var targetName: String): AlfheimPacket<MessageDisguise>() {
	
	override fun onMessage(packet: MessageDisguise, ctx: MessageContext): MessageDisguise? {
		return packet.handle(ctx)
	}
	
	companion object {
		
		fun MessageDisguise.handle(ctx: MessageContext): MessageDisguise? {
			if (ctx.side.isClient) {
				val me: Boolean
				
				val target = if (mc.thePlayer.commandSenderName == targetName) {
					me = true
					mc.thePlayer
				} else {
					me = false
					mc.theWorld.playerEntities.firstOrNull { (it as EntityPlayer).commandSenderName == targetName } as AbstractClientPlayer
				}
				
				val prev = target.locationSkin.toString()
				
				val new = if (skin.isNotEmpty()) {
					if (skin.indexOf(":") == -1) return null
					
					val (modid, name) = skin.split(":")
					validateSkin(ResourceLocation(modid, name))
				} else if (race == 0 || race == 10) AbstractClientPlayer.locationStevePng else (if (gurl) LibResourceLocations.oldFemale else LibResourceLocations.oldMale)[race - 1]
				
				target.func_152121_a(MinecraftProfileTexture.Type.SKIN, new)
				
				if (respond && me)
					return MessageDisguise(prev, false, 0, false, "")
			} else {
				val belt = PlayerHandler.getPlayerBaubles(ctx.serverHandler.playerEntity)[3] ?: return null
				ItemNBTHelper.setString(belt, TAG_SKIN, skin)
			}
			
			return null
		}
		
		fun validateSkin(skin: ResourceLocation): ResourceLocation {
			try {
				mc.resourceManager.getResource(skin)
				return skin
			} catch (e: FileNotFoundException) {
				return AbstractClientPlayer.locationStevePng
			}
		}
	}
}