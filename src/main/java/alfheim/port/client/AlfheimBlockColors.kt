package alfheim.port.client

import alfheim.port.legacy.*
import alfheim.port.registry.LegacyRegistration
import net.minecraft.world.level.block.Block
import net.minecraftforge.client.event.RegisterColorHandlersEvent
import net.minecraftforge.eventbus.api.*

/**
 * Цвет блоков автора 1.7.10 → цвет граней модели 1.20.1 (MAPPING.md, «Блоки и предметы»). Блок в мире красит
 * `colorMultiplier(world, x, y, z)`, без мира (частицы, падающий блок) — `getRenderColor` по номеру варианта.
 * Цвет ложится только на грани с tintindex: их генерация моделей ставит блокам, которые красит класс автора
 * ([isTinted]). Цвет спрашивается при сборке чанка, как в 1.7.10 — при отрисовке блока
 */
object AlfheimBlockColors {

	fun register(bus: IEventBus) {
		bus.addListener(EventPriority.NORMAL, false, RegisterColorHandlersEvent.Block::class.java, ::registerBlockColors)
	}

	private fun registerBlockColors(e: RegisterColorHandlersEvent.Block) {
		val blocks = LegacyRegistration.blocks.keys.filter { it is LegacyBlock && it.isTinted }
		e.register({ state, level, pos, _ ->
			val block = state.block as LegacyBlock
			if (level != null && pos != null) block.colorMultiplier(level, pos.x, pos.y, pos.z) else block.getRenderColor(block.variant ?: 0)
		}, *blocks.toTypedArray<Block>())
	}
}
