package alfheim.common.core.handler

import alexsocol.asjlib.ASJUtilities
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.network.NetworkService
import alfheim.common.network.packet.MessageVisualEffect
// PORT: номер измерения → ResourceKey<Level>; поля позиции → x, y, z
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level

object VisualEffectHandler {
	
	fun sendPacket(s: VisualEffects, e: Entity) {
		sendPacket(s, e.level().dimension(), e.x, e.y, e.z)
	}
	
	fun sendPacket(s: VisualEffects, dimension: ResourceKey<Level>, vararg data: Double) {
		if (ASJUtilities.isServer) NetworkService.sendToDim(MessageVisualEffect(s.ordinal, *data), dimension)
	}
	
	fun sendError(dim: ResourceKey<Level>, x: Int, y: Int, z: Int) {
		sendPacket(VisualEffects.WISP, dim, x + 0.5, y + 0.5, z + 0.5, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 5.0, 0.0)
	}
}