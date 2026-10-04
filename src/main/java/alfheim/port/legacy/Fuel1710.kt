package alfheim.port.legacy

import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent
import net.minecraftforge.eventbus.api.EventPriority

/** `cpw.mods.fml.common.IFuelHandler` 1.7.10: сколько тиков горит вещь в печи, 0 — не топливо */
interface IFuelHandler {

	fun getBurnTime(fuel: ItemStack): Int
}

/**
 * Топливо 1.7.10 (`TileEntityFurnace.getItemBurnTime`): предмет-блок из дерева горит 300 тиков. Печь 1.7.10 проверяла
 * материал раньше, чем обработчики топлива модов (`IFuelHandler`), поэтому деревянный блок горел 300 тиков, даже если
 * мод назначал ему другое время. Остальные вещи — по обработчикам модов (`GameRegistry.registerFuelHandler`):
 * наибольшее время из всех. В 1.20.1 топливо задаётся каждому предмету, здесь — вещам порта (MAPPING.md, «Блоки и
 * предметы»).
 */
object Fuel1710 {

	/** Обработчики топлива автора: `GameRegistry.registerFuelHandler` */
	val handlers = ArrayList<IFuelHandler>()

	/** Подписка на шине Forge; вызывается из конструктора мода */
	fun register() {
		MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, FurnaceFuelBurnTimeEvent::class.java, ::burnTime)
	}

	private fun burnTime(e: FurnaceFuelBurnTimeEvent) {
		val block = (e.itemStack.item as? BlockItem)?.block as? LegacyBlock
		if (block != null && block.blockMaterial == Material.wood) {
			e.burnTime = 300
			return
		}
		// GameRegistry.getFuelValue 1.7.10; без обработчика, который знает вещь, — как решила 1.20.1
		val handled = handlers.maxOfOrNull { it.getBurnTime(e.itemStack) } ?: 0
		if (handled > 0) e.burnTime = handled
	}
}
