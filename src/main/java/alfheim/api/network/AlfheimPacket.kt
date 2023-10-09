package alfheim.api.network

import alexsocol.asjlib.network.ASJPacket
import cpw.mods.fml.common.network.simpleimpl.*
import net.minecraft.entity.player.EntityPlayerMP

abstract class AlfheimPacket<T : AlfheimPacket<T>>: ASJPacket(), IMessage, IMessageHandler<T, T> {
	
	override fun onMessage(packet: T, ctx: MessageContext): T? {
		if (ctx.side.isClient)
			packet.handleClient()
		else
			packet.handleServer(ctx.serverHandler.playerEntity)
		return null
	}

	open fun handleClient() = Unit
	open fun handleServer(player: EntityPlayerMP) = Unit
}