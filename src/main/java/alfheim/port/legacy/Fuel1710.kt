package alfheim.port.legacy

import net.minecraft.world.item.BlockItem
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent
import net.minecraftforge.eventbus.api.EventPriority

/**
 * Топливо 1.7.10 по материалу (`TileEntityFurnace.getItemBurnTime`): предмет-блок из дерева горит 300 тиков. Печь
 * 1.7.10 проверяла материал раньше, чем обработчики топлива модов (`IFuelHandler`), поэтому деревянный блок горел
 * 300 тиков, даже если мод назначал ему другое время. В 1.20.1 топливо задаётся каждому предмету, здесь — блокам
 * порта из дерева (MAPPING.md, «Блоки и предметы»).
 */
object Fuel1710 {

	/** Подписка на шине Forge; вызывается из конструктора мода */
	fun register() {
		MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, FurnaceFuelBurnTimeEvent::class.java, ::burnTime)
	}

	private fun burnTime(e: FurnaceFuelBurnTimeEvent) {
		val block = (e.itemStack.item as? BlockItem)?.block as? LegacyBlock ?: return
		if (block.blockMaterial == Material.wood) e.burnTime = 300
	}
}
