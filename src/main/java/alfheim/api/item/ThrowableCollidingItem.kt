package alfheim.api.item

// PORT: импорты 1.20.1; EntityThrowable и MovingObjectPosition 1.7.10 — alfheim.port.legacy
import alfheim.port.legacy.*
import net.minecraft.world.item.ItemStack

class ThrowableCollidingItem(internal var key: String, internal var stack: ItemStack, internal var event: (EntityThrowable, MovingObjectPosition) -> Unit) {
	
	fun onImpact(throwable: EntityThrowable, movingObject: MovingObjectPosition) {
		event.invoke(throwable, movingObject)
	}
}
