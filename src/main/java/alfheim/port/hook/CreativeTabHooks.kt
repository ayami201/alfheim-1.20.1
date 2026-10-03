package alfheim.port.hook

import alfheim.common.block.AlfheimFluffBlocks
import net.minecraft.world.item.*
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.eventbus.api.*
import vazkii.botania.api.BotaniaRegistries
import vazkii.botania.common.block.BotaniaBlocks

/**
 * Врезки автора во вкладки творческого режима (HOOKS.md), перенесённые на событие Forge
 * `BuildCreativeModeTabContentsEvent`.
 */
object CreativeTabHooks {

	/** Подписка на шине мода; вызывается из конструктора мода */
	fun register(bus: IEventBus) {
		bus.addListener(EventPriority.NORMAL, false, BuildCreativeModeTabContentsEvent::class.java, ::buildContents)
	}

	private fun buildContents(e: BuildCreativeModeTabContentsEvent) {
		// H-023 (AlfheimHookHandler.addBlock, врезка в BotaniaCreativeTab.addBlock): стена из эльфийского кварца —
		// во вкладке Botania, сразу за лестницей из эльфийского кварца. Во вкладке Alfheim её нет и у автора
		if (e.tabKey == BotaniaRegistries.BOTANIA_TAB_KEY)
			e.entries.putAfter(ItemStack(BotaniaBlocks.elfQuartzStairs), ItemStack(AlfheimFluffBlocks.elfQuartzWall), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS)
	}
}
