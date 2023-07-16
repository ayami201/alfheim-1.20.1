package alfheim.api.network

import cpw.mods.fml.common.network.simpleimpl.*
import net.minecraft.entity.player.EntityPlayerMP

interface AlfheimPacket<T : AlfheimPacket<T>> : IMessage, IMessageHandler<T, T> {
	
	override fun onMessage(packet: T, ctx: MessageContext): T? {
		if (ctx.side.isClient)
			packet.handleClient()
		else
			packet.handleServer(ctx.serverHandler.playerEntity)
		return null
	}

	fun handleClient() = Unit
	fun handleServer(player: EntityPlayerMP) = Unit
}