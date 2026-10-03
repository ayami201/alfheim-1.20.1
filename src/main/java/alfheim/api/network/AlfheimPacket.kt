package alfheim.api.network

import alexsocol.asjlib.ASJUtilities
import alexsocol.asjlib.network.ASJPacket
// PORT: IMessageHandler (FML 1.7.10) → обработчик SimpleChannel; EntityPlayerMP → ServerPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

abstract class AlfheimPacket<T : AlfheimPacket<T>>: ASJPacket() {
	
	// PORT: в 1.7.10 обработчик работал прямо в сетевом потоке. В 1.20.1 игрока и мир из него трогать нельзя, игра
	// падает при одновременном доступе, поэтому пакет обрабатывается в основном потоке, на ближайшем тике
	fun onMessage(packet: T, ctx: Supplier<NetworkEvent.Context>): T? {
		val context = ctx.get()
		// PORT-FIX: ошибка в задаче enqueueWork остаётся в её CompletableFuture и в лог не попадает; в 1.7.10 ошибку
		// обработчика писал в лог FML — так же пишется и здесь
		context.enqueueWork {
			if (context.direction.receptionSide.isClient)
				packet.handleClient()
			else
				packet.handleServer(context.sender!!)
		}.exceptionally { e ->
			ASJUtilities.error("Error handling packet ${packet.javaClass.name}: ", e)
			null
		}
		context.packetHandled = true
		return null
	}

	open fun handleClient() = Unit
	open fun handleServer(player: EntityPlayerMP) = Unit
}