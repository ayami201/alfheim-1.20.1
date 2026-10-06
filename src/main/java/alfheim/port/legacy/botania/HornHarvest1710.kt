package alfheim.port.legacy.botania

import alfheim.port.registry.LegacyRegistration
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import vazkii.botania.api.BotaniaForgeCapabilities
import vazkii.botania.api.block.HornHarvestable
import vazkii.botania.forge.CapabilityUtil

/**
 * Блоки порта с [IHornHarvestable] 1.7.10 — для рогов Botania 1.20.1. Рог спрашивает блок через capability
 * `HORN_HARVEST`; у блока без BlockEntity её отдаёт таблица блоков Botania (`CapabilityUtil.registerBlockLookaside`).
 * Без записи в ней рог дикой природы ломал бы любое растение, как рог 1.7.10 — блок без `IHornHarvestable`
 */
object HornHarvest1710 {

	/** Подписка на общую настройку: таблица заполняется в очереди основного потока, когда все блоки зарегистрированы */
	fun register(bus: IEventBus) {
		bus.addListener(EventPriority.NORMAL, false, FMLCommonSetupEvent::class.java) { e ->
			e.enqueueWork {
				for (block in LegacyRegistration.blocks.keys) {
					if (block !is IHornHarvestable) continue
					val harvestable = Harvestable(block)
					CapabilityUtil.registerBlockLookaside(BotaniaForgeCapabilities.HORN_HARVEST, { _, _, _ -> harvestable }, block)
				}
			}
		}
	}

	/** `HornHarvestable` 1.20.1 → методы блока 1.7.10: точка — координаты, рог — тот же по имени */
	private class Harvestable(val block: IHornHarvestable): HornHarvestable {

		override fun canHornHarvest(world: Level, pos: BlockPos, stack: ItemStack, type: HornHarvestable.EnumHornType, user: LivingEntity?) =
			block.canHornHarvest(world, pos.x, pos.y, pos.z, stack, type(type))

		override fun hasSpecialHornHarvest(world: Level, pos: BlockPos, stack: ItemStack, type: HornHarvestable.EnumHornType, user: LivingEntity?) =
			block.hasSpecialHornHarvest(world, pos.x, pos.y, pos.z, stack, type(type))

		override fun harvestByHorn(world: Level, pos: BlockPos, stack: ItemStack, type: HornHarvestable.EnumHornType, user: LivingEntity?) =
			block.harvestByHorn(world, pos.x, pos.y, pos.z, stack, type(type))

		private fun type(type: HornHarvestable.EnumHornType) = IHornHarvestable.EnumHornType.valueOf(type.name)
	}
}
