package alfheim.port.registry

import alfheim.api.ModInfo.MODID
import alfheim.common.block.*
import alfheim.common.item.AlfheimItems
import alfheim.port.client.*
import alfheim.port.data.AlfheimData
import alfheim.port.hook.CreativeTabHooks
import alfheim.port.legacy.Explosions1710
import alfheim.port.legacy.Fuel1710
import alfheim.port.legacy.Weather1710
import alfheim.port.legacy.botania.BotaniaBlocks1710
import alfheim.port.legacy.botania.HornHarvest1710
import alfheim.port.loot.FortuneCount
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import net.minecraft.core.registries.Registries
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.*

/**
 * Реестры мода (SPEC, Р-5). В 1.7.10 блоки, предметы, существа и прочее регистрировались вызовами
 * GameRegistry и EntityRegistry в preInit; в 1.20.1 объекты отдаются Forge через DeferredRegister,
 * в событии регистрации. Код автора создаёт объекты так же, а кладёт их сюда.
 */
object AlfheimRegisters {
	
	val BLOCKS: DeferredRegister<net.minecraft.world.level.block.Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID)
	val ITEMS: DeferredRegister<net.minecraft.world.item.Item> = DeferredRegister.create(ForgeRegistries.ITEMS, MODID)
	val BLOCK_ENTITY_TYPES: DeferredRegister<net.minecraft.world.level.block.entity.BlockEntityType<*>> = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID)
	val ENTITY_TYPES: DeferredRegister<net.minecraft.world.entity.EntityType<*>> = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID)
	val MOB_EFFECTS: DeferredRegister<net.minecraft.world.effect.MobEffect> = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID)
	val SOUND_EVENTS: DeferredRegister<net.minecraft.sounds.SoundEvent> = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID)
	val CREATIVE_MODE_TABS: DeferredRegister<net.minecraft.world.item.CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID)
	val RECIPE_SERIALIZERS: DeferredRegister<net.minecraft.world.item.crafting.RecipeSerializer<*>> = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID)
	val LOOT_FUNCTION_TYPES: DeferredRegister<net.minecraft.world.level.storage.loot.functions.LootItemFunctionType> = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, MODID)
	
	/** Все реестры — на шину мода; вызывается из конструктора мода, до события регистрации */
	fun register(bus: IEventBus) {
		// звуковые события автора — все сразу, по sounds.json
		AlfheimSounds
		
		// блоки и предметы автора создаются в событии регистрации; в 1.7.10 — в preInit (CommonProxy)
		LegacyRegistration.onBlocks {
			AlfheimBlocks
			AlfheimFluffBlocks
			// блоки Botania 1.7.10, которых нет в Botania 1.20.1, а Альфхейму они нужны (черепица)
			BotaniaBlocks1710.init()
		}
		LegacyRegistration.onItems {
			AlfheimItems
		}
		LegacyRegistration.register(bus)
		// деревянный блок 1.7.10 — топливо на 300 тиков
		Fuel1710.register()
		// отменённый взрыв 1.7.10 игроки не видят и не слышат
		Explosions1710.register()
		// погодные эффекты 1.7.10 — молнии мира отдельным списком
		Weather1710.register()
		// врезки автора во вкладки творческого режима (HOOKS.md)
		CreativeTabHooks.register(bus)
		BotaniaBlocks1710.register(bus)
		// блоки автора, которые сами решают, что с ними делает рог Botania (IHornHarvestable 1.7.10)
		HornHarvest1710.register(bus)
		// особые рецепты автора — свой сериализатор у каждого, ингредиент «предмет с metadata»; обычные рецепты — данные
		LegacySpecialRecipes.register(bus)
		// функции лута порта — формулы лута автора, которых нет у ванилы 1.20.1
		FortuneCount
		// модели, лут, теги, рецепты и legacy_ids.json — генерация данных (./gradlew runData)
		AlfheimData.register(bus)
		// модели блоков и предметов, которые 1.7.10 выбирал в коде, цвета блоков и предметов и рендер существ — только на клиенте
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT) { Runnable { AlfheimModels.register(bus); AlfheimBlockColors.register(bus); AlfheimItemColors.register(bus); AlfheimEntityRenderers.register(bus) } }
		
		for (register in listOf(BLOCKS, ITEMS, BLOCK_ENTITY_TYPES, ENTITY_TYPES, MOB_EFFECTS, SOUND_EVENTS, CREATIVE_MODE_TABS, RECIPE_SERIALIZERS, LOOT_FUNCTION_TYPES))
			register.register(bus)
	}
	
	/**
	 * Имя в реестре 1.20.1 — имя автора в snake_case (SPEC, Р-5): `DomainDoor` → `domain_door`,
	 * `altWood1` → `alt_wood1`, `ESMItem` → `esm_item`. Цифры остаются при слове перед ними.
	 */
	fun snakeCase(name: String) = name
		.replace(Regex("([a-z0-9])([A-Z])"), "$1_$2")
		.replace(Regex("([A-Z]+)([A-Z][a-z])"), "$1_$2")
		.lowercase()
}
