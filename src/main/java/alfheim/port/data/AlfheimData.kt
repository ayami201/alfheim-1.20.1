package alfheim.port.data

import net.minecraft.data.loot.LootTableProvider
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.IEventBus

/**
 * Генерация данных (SPEC, Р-9): модели, лут, теги и `legacy_ids.json` строит код по блокам и предметам автора,
 * зарегистрированным через `alfheim.port.registry.LegacyRegistration`, рецепты — по рецептам автора (`AlfheimRecipes`).
 * Запуск — `./gradlew runData`, результат коммитится в `src/generated/resources`; CI проверяет, что он совпадает с кодом.
 */
object AlfheimData {
	
	fun register(bus: IEventBus) {
		bus.addListener(EventPriority.NORMAL, false, GatherDataEvent::class.java, ::gather)
	}
	
	private fun gather(e: GatherDataEvent) {
		val generator = e.generator
		val output = generator.packOutput
		val files = e.existingFileHelper
		
		generator.addProvider(e.includeClient(), TintedTemplates(output, files))
		generator.addProvider(e.includeClient(), AlfheimBlockStates(output, files))
		generator.addProvider(e.includeClient(), AlfheimItemModels(output, files))
		generator.addProvider(e.includeServer(), LootTableProvider(output, emptySet(), listOf(LootTableProvider.SubProviderEntry(::AlfheimBlockLoot, LootContextParamSets.BLOCK))))
		val blockTags = generator.addProvider(e.includeServer(), AlfheimBlockTags(output, e.lookupProvider, files))
		generator.addProvider(e.includeServer(), AlfheimItemTags(output, e.lookupProvider, blockTags.contentsGetter(), files))
		generator.addProvider(e.includeServer(), AlfheimRecipeProvider(output))
		generator.addProvider(e.includeServer(), LegacyIdsProvider(output))
	}
}
