package alfheim.port.client

import alfheim.client.render.entity.*
import alfheim.client.render.tile.RenderTileTreeBerry
import alfheim.common.block.tile.TileTreeBerry
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.entity.*
import alfheim.port.registry.LegacyRegistration
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.eventbus.api.*

/**
 * Рендер существ и блок-сущностей автора (MAPPING.md, «Рендер»): `RenderingRegistry.registerEntityRenderingHandler(класс,
 * рендер)` и `ClientRegistry.bindTileEntitySpecialRenderer(класс, рендер)` из `ClientProxy.registerRenderThings` 1.7.10 →
 * событие `EntityRenderersEvent.RegisterRenderers` 1.20.1. Событие идёт раньше postInit, где автор регистрировал рендер,
 * поэтому пары «существо — рендер» здесь, в том же порядке и с теми же условиями, что у автора. Рендер существа 1.20.1
 * создаётся с контекстом — конструктор класса рендера. У каждого существа нужен рендер: без него клиент падает, когда
 * существо появляется
 */
object AlfheimEntityRenderers {

	fun register(bus: IEventBus) {
		bus.addListener(EventPriority.NORMAL, false, EntityRenderersEvent.RegisterRenderers::class.java, ::registerRenderers)
	}

	private fun registerRenderers(e: EntityRenderersEvent.RegisterRenderers) {
		e.registerEntityRenderer(LegacyRegistration.entityType(EntityFallingHang::class.java), ::RenderEntityFallingHang)
		e.registerEntityRenderer(LegacyRegistration.entityType(EntityThrownPotion::class.java), ::RenderEntityThrownPotion)
		e.registerEntityRenderer(LegacyRegistration.entityType(EntityThrowableItem::class.java), ::RenderEntityThrownItem)
		e.registerEntityRenderer(LegacyRegistration.entityType(FakeLightning::class.java), ::RenderFakeLightning)
		
		if (!AlfheimConfigHandler.minimalGraphics)
			e.registerBlockEntityRenderer(LegacyRegistration.tileType(TileTreeBerry::class.java)) { RenderTileTreeBerry }
	}
}
