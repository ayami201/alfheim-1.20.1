package alfheim.port.client

import alfheim.client.render.entity.*
import alfheim.common.entity.*
import alfheim.port.registry.LegacyRegistration
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.eventbus.api.*

/**
 * Рендер существ автора (MAPPING.md, «Рендер»): `RenderingRegistry.registerEntityRenderingHandler(класс, рендер)` из
 * `ClientProxy.registerRenderThings` 1.7.10 → событие `EntityRenderersEvent.RegisterRenderers` 1.20.1. Событие идёт
 * раньше postInit, где автор регистрировал рендер, поэтому пары «существо — рендер» здесь, в том же порядке, что у
 * автора. Рендер 1.20.1 создаётся с контекстом — конструктор класса рендера. У каждого существа нужен рендер: без него
 * клиент падает, когда существо появляется
 */
object AlfheimEntityRenderers {

	fun register(bus: IEventBus) {
		bus.addListener(EventPriority.NORMAL, false, EntityRenderersEvent.RegisterRenderers::class.java, ::registerRenderers)
	}

	private fun registerRenderers(e: EntityRenderersEvent.RegisterRenderers) {
		e.registerEntityRenderer(LegacyRegistration.entityType(EntityThrownPotion::class.java), ::RenderEntityThrownPotion)
		e.registerEntityRenderer(LegacyRegistration.entityType(EntityThrowableItem::class.java), ::RenderEntityThrownItem)
	}
}
