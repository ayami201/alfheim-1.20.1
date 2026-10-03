package alfheim.port.registry

import alfheim.api.ModInfo.MODID
import alfheim.port.legacy.LegacyItem
import alfheim.port.legacy.Potion1710
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.registries.RegisterEvent

/**
 * Регистрация блоков и предметов автора (SPEC, Р-5; MAPPING.md, «Блоки и предметы»).
 *
 * В 1.7.10 блок регистрировался прямо при создании: базовые классы автора вызывали `GameRegistry.registerBlock` в
 * `setBlockName`. В 1.20.1 блок или предмет можно создать только во время события регистрации, пока реестр открыт.
 * Поэтому реестры автора (`AlfheimBlocks`, `AlfheimItems`) создаются здесь, внутри `RegisterEvent`, а
 * `GameRegistry` прослойки передаёт каждый блок и предмет в это событие. Предмет-блок создаётся позже, в событии
 * регистрации предметов.
 *
 * Новый id — имя автора в snake_case, у варианта с metadata — с номером варианта: `ElvenOre` + 3 → `elven_ore3`
 * (SPEC, Р-5). Всё зарегистрированное записывается в [blocks] и [items]: по ним генерация данных строит
 * `legacy_ids.json`, а тесты проверяют паритет с оригиналом.
 */
object LegacyRegistration {

	/** Новый id и имя 1.7.10; [oldMeta] — metadata варианта, `null` — вещь без вариантов */
	class Entry(val id: ResourceLocation, val oldName: String, val oldMeta: Int?)

	/**
	 * Блок 1.7.10, у которого в 1.20.1 нет своего блока, — состояние другого блока: двойная плита — `type=double` плиты.
	 * [state] — свойства состояния, как в `legacy_ids.json`: `type=double`
	 */
	class Alias(val oldName: String, val block: Block, val state: String)
	
	val blocks = LinkedHashMap<Block, Entry>()
	val items = LinkedHashMap<Item, Entry>()
	val effects = LinkedHashMap<MobEffect, Entry>()
	val aliases = ArrayList<Alias>()

	private val pendingItems = ArrayList<Pair<Item, String>>()
	private val pendingEffects = ArrayList<Potion1710>()
	private val blockSources = ArrayList<() -> Any>()
	private val itemSources = ArrayList<() -> Any>()
	private val blockItems = ArrayList<Pair<Block, (Block) -> Item>>()
	private var event: RegisterEvent? = null

	/** Подписка на событие регистрации; вызывается из конструктора мода */
	fun register(bus: IEventBus) {
		bus.addListener(EventPriority.NORMAL, false, RegisterEvent::class.java, ::onRegister)
		// id 1.7.10 остальным эффектам — когда реестр заполнен всеми модами
		bus.addListener(EventPriority.NORMAL, false, FMLCommonSetupEvent::class.java) { Potion1710.assignIds() }
	}

	/** Что создаёт блоки автора: обычно обращение к его реестру (`AlfheimBlocks`), создание объекта и есть регистрация */
	fun onBlocks(source: () -> Any) {
		blockSources += source
	}

	/** Что создаёт предметы автора (`AlfheimItems`) */
	fun onItems(source: () -> Any) {
		itemSources += source
	}

	private fun onRegister(e: RegisterEvent) {
		if (e.registryKey == Registries.MOB_EFFECT) return registerEffects(e)
		
		val sources = when (e.registryKey) {
			Registries.BLOCK -> blockSources
			Registries.ITEM  -> itemSources
			else             -> return
		}

		event = e
		try {
			if (e.registryKey == Registries.ITEM)
				for ((block, factory) in blockItems) item(factory(block), blocks[block]!!)
			sources.forEach { it() }
			// вариант предмета известен после его конструктора: предмет автора регистрируется в конструкторе
			// базового класса (ItemMod), раньше, чем у наследника появляется номер варианта
			for ((item, name) in pendingItems) {
				val legacy = item as? LegacyItem
				val meta = legacy?.variant
				item(item, Entry(legacy?.variantName?.let { id(it, null) } ?: id(name, meta), name, meta))
			}
			pendingItems.clear()
		} finally {
			event = null
		}
	}

	/** Новый id из имени автора и номера варианта */
	fun id(name: String, meta: Int?) = ResourceLocation(MODID, AlfheimRegisters.snakeCase(name.substringAfter(':') + (meta ?: "")))

	/**
	 * Блок автора под именем [name] (как в `GameRegistry.registerBlock` 1.7.10); [meta] — номер варианта, если блок был
	 * одной из metadata; [item] — как создать его предмет-блок, `null` — блок без предмета
	 */
	fun block(block: Block, name: String, meta: Int?, item: ((Block) -> Item)?) {
		val e = event?.takeIf { it.registryKey == Registries.BLOCK } ?: throw IllegalStateException("Block $name${meta ?: ""} is created outside of the block registration: create it from LegacyRegistration.onBlocks")
		val entry = Entry(id(name, meta), name, meta)
		check(blocks.values.none { it.id == entry.id }) { "Block id ${entry.id} is registered twice" }
		blocks[block] = entry
		e.register(Registries.BLOCK, entry.id) { block }
		if (item != null) blockItems += block to item
	}

	/** Старое имя [name] — состояние [state] блока [block] ([Alias]) */
	fun alias(name: String, block: Block, state: String) {
		check(block in blocks) { "Alias $name: block is not registered" }
		check(aliases.none { it.oldName == name }) { "Alias $name is registered twice" }
		aliases += Alias(name, block, state)
	}
	
	/**
	 * Предмет автора под именем [name] (как в `GameRegistry.registerItem` 1.7.10). Id — по имени и номеру варианта
	 * ([LegacyItem.variant]) или по имени варианта ([LegacyItem.variantName]); регистрируется в конце события
	 */
	fun item(item: Item, name: String) {
		event?.takeIf { it.registryKey == Registries.ITEM } ?: throw IllegalStateException("Item $name is created outside of the item registration: create it from LegacyRegistration.onItems")
		pendingItems += item to name
	}

	/**
	 * Зелье автора ([Potion1710]): создаётся, когда к нему впервые обращаются (у автора — в preInit), и ждёт
	 * регистрации эффектов. Id — имя из `setPotionName` без приставки: `alfheim.potion.whiteWine` → `alfheim:white_wine`
	 */
	fun effect(potion: Potion1710) {
		check(potion !in pendingEffects && potion !in effects) { "Potion ${potion.id} is registered twice" }
		pendingEffects += potion
	}
	
	private fun registerEffects(e: RegisterEvent) {
		for (potion in pendingEffects) {
			val name = potion.name.substringAfterLast('.')
			val entry = Entry(id(name, null), name, null)
			check(effects.values.none { it.id == entry.id }) { "Effect id ${entry.id} is registered twice" }
			effects[potion] = entry
			e.register(Registries.MOB_EFFECT, entry.id) { potion }
		}
		pendingEffects.clear()
	}
	
	private fun item(item: Item, entry: Entry) {
		val e = event?.takeIf { it.registryKey == Registries.ITEM } ?: throw IllegalStateException("Item ${entry.id} is created outside of the item registration: create it from LegacyRegistration.onItems")
		check(items.values.none { it.id == entry.id }) { "Item id ${entry.id} is registered twice" }
		items[item] = entry
		e.register(Registries.ITEM, entry.id) { item }
	}
}
