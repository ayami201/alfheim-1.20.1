package alfheim.port.legacy.botania

import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.server.ServerLifecycleHooks
import vazkii.botania.client.core.handler.ClientTickHandler

/**
 * `vazkii.botania.common.Botania` r1.8-249: то, что автор берёт у `Botania.proxy`. В Botania 1.20.1 прокси нет; часть
 * его методов — здесь, с той же работой
 */
object Botania {
	
	val proxy = Proxy
	
	object Proxy {
		
		/**
		 * `getWorldElapsedTicks`: на клиенте — тики игры Botania (`ClientTickHandler.ticksInGame`), на выделенном
		 * сервере — время основного мира, как у прокси 1.7.10 своей стороны
		 */
		val worldElapsedTicks: Long
			get() = if (FMLEnvironment.dist.isClient) ClientTicks.ticks() else ServerLifecycleHooks.getCurrentServer()?.overworld()?.gameTime ?: 0L
	}
}

/** Отдельный класс: на выделенном сервере не загружается, а с ним и классы клиента Botania */
private object ClientTicks {
	
	fun ticks() = ClientTickHandler.ticksInGame.toLong()
}
