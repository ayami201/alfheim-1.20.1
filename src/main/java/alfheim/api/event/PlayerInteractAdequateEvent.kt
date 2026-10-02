package alfheim.api.event

// PORT: EntityPlayer → Player; Event FML → Event шины Forge (конструктор без аргументов дописывает она, как FML в 1.7.10)
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraftforge.eventbus.api.Event

/**
 * Adequate interaction event
 * Do not use base class
 */
open class PlayerInteractAdequateEvent(val player: EntityPlayer, val x: Int, val y: Int, val z: Int, val side: Int, val entity: Entity?): Event() {
	
	class LeftClick(player: EntityPlayer, val action: Action, x: Int, y: Int, z: Int, side: Int, entity: Entity?): PlayerInteractAdequateEvent(player, x, y, z, side, entity) {
		
		enum class Action {
			LEFT_CLICK_AIR,
			LEFT_CLICK_BLOCK,
			LEFT_CLICK_ENTITY,
			LEFT_CLICK_LIQUID
		}
	}
	
	class RightClick(player: EntityPlayer, val action: Action, x: Int, y: Int, z: Int, side: Int, entity: Entity?): PlayerInteractAdequateEvent(player, x, y, z, side, entity) {
		
		enum class Action {
			RIGHT_CLICK_AIR,
			RIGHT_CLICK_BLOCK,
			RIGHT_CLICK_ENTITY,
			RIGHT_CLICK_LIQUID
		}
	}
}
