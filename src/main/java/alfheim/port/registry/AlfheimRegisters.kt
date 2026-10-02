package alfheim.port.registry

import alfheim.api.ModInfo.MODID
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
	
	/** Все реестры — на шину мода; вызывается из конструктора мода, до события регистрации */
	fun register(bus: IEventBus) {
		// звуковые события автора — все сразу, по sounds.json
		AlfheimSounds
		
		for (register in listOf(BLOCKS, ITEMS, BLOCK_ENTITY_TYPES, ENTITY_TYPES, MOB_EFFECTS, SOUND_EVENTS, CREATIVE_MODE_TABS))
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
