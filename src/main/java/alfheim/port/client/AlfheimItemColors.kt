package alfheim.port.client

import alfheim.port.legacy.Item1710
import alfheim.port.legacy.ItemBlock
import alfheim.port.registry.LegacyRegistration
import net.minecraftforge.client.event.RegisterColorHandlersEvent
import net.minecraftforge.eventbus.api.*

/**
 * Цвет прохода рендера предмета 1.7.10 (`getColorFromItemStack(stack, pass)`) → цвет слоя модели предмета 1.20.1 с
 * тем же номером (MAPPING.md, «Блоки и предметы»). Слои по проходам строит генерация данных
 * (`alfheim.port.data.AlfheimItemModels`); цвет спрашивается на каждом кадре, как в 1.7.10, поэтому переливы
 * (радужная пыль, осколки разлома) идут так же. Предмет-блок ([ItemBlock]) красит грани модели своего блока с
 * tintindex — цветом `getRenderColor` блока, если класс предмета не переопределяет `getColorFromItemStack`
 */
object AlfheimItemColors {
	
	fun register(bus: IEventBus) {
		bus.addListener(EventPriority.NORMAL, false, RegisterColorHandlersEvent.Item::class.java, ::registerItemColors)
	}
	
	private fun registerItemColors(e: RegisterColorHandlersEvent.Item) {
		val items = LegacyRegistration.items.keys.filterIsInstance<Item1710>()
		e.register({ stack, layer -> (stack.item as Item1710).getColorFromItemStack(stack, layer) }, *items.toTypedArray())
		val blocks = LegacyRegistration.items.keys.filterIsInstance<ItemBlock>()
		e.register({ stack, layer -> (stack.item as ItemBlock).getColorFromItemStack(stack, layer) }, *blocks.toTypedArray())
	}
}
