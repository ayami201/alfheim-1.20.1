package alfheim.port.registry

import alfheim.api.ModInfo.MODID
import alfheim.port.legacy.EntityWeatherEffect
import alfheim.port.legacy.LegacyItem
import alfheim.port.legacy.Potion1710
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.registries.RegisterEvent
import java.util.concurrent.ConcurrentHashMap

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

	/**
	 * Новый id и имя 1.7.10; [oldMeta] — metadata варианта, `null` — вещь без вариантов. Имя с `modid:` — вещь другого
	 * мода 1.7.10, которую вернул порт (`Botania:customBrick`, `alfheim.port.legacy.botania.BotaniaBlocks1710`)
	 */
	class Entry(val id: ResourceLocation, val oldName: String, val oldMeta: Int?) {

		/** Имя в реестре 1.7.10: `alfheim:` + имя автора или имя вещи другого мода с его modid */
		val legacyId get() = if (':' in oldName) oldName else "$MODID:$oldName"
	}

	/**
	 * Блок 1.7.10, у которого в 1.20.1 нет своего блока, — состояние другого блока: двойная плита — `type=double` плиты.
	 * [state] — свойства состояния, как в `legacy_ids.json`: `type=double`; [oldMeta] — metadata варианта, `null` — блок
	 * без вариантов
	 */
	class Alias(val oldName: String, val block: Block, val state: String, val oldMeta: Int? = null)

	/**
	 * Блок автора, вместо которого в порту — такой же блок другого мода (решение автора, TASKS.md, журнал решений): своего
	 * блока и предмета у старого имени [oldName] нет, в `legacy_ids.json` оно ведёт на [block] — его ставит загрузчик
	 * построек
	 */
	class Replacement(val oldName: String, val block: Block)

	val blocks = LinkedHashMap<Block, Entry>()
	val items = LinkedHashMap<Item, Entry>()
	val effects = LinkedHashMap<MobEffect, Entry>()
	val entities = LinkedHashMap<EntityType<*>, Entry>()
	val tiles = LinkedHashMap<BlockEntityType<*>, Entry>()
	val aliases = ArrayList<Alias>()
	val replacements = ArrayList<Replacement>()
	
	private val entityTypes = HashMap<Class<out Entity>, EntityType<*>>()
	private val pendingEntities = ArrayList<Triple<Class<out Entity>, String, MobCategory>>()
	
	private val tileTypes = HashMap<Class<out BlockEntity>, BlockEntityType<*>>()
	private val tileBlocks = HashMap<BlockEntityType<*>, MutableSet<Block>>()
	private val tileUpdates = ConcurrentHashMap<Pair<BlockEntityType<*>, Boolean>, Boolean>()
	private val pendingTiles = ArrayList<Pair<Class<out BlockEntity>, String>>()

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
		if (e.registryKey == Registries.ENTITY_TYPE) return registerEntities(e)
		if (e.registryKey == Registries.BLOCK_ENTITY_TYPE) return registerTiles(e)
		
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

	/** Варианты блоков автора: имя 1.7.10 → номер варианта → блок; строится, когда все блоки зарегистрированы */
	private val variants: Map<String, Map<Int, Block>> by lazy {
		check(event?.registryKey != Registries.BLOCK) { "Block variants are read during the block registration" }
		blocks.entries.filter { it.value.oldMeta != null }.groupBy({ it.value.oldName }, { it.value.oldMeta!! to it.key }).mapValues { it.value.toMap() }
	}

	/** Блок варианта [meta] с тем же именем 1.7.10, что у [block]; `null` — у блока нет вариантов или нет такого варианта */
	fun variant(block: Block, meta: Int): Block? {
		val entry = blocks[block] ?: return null
		if (entry.oldMeta == null) return null
		return variants[entry.oldName]?.get(meta)
	}

	/** Старое имя [name] с metadata [meta] (`null` — без вариантов) — состояние [state] блока [block] ([Alias]) */
	fun alias(name: String, block: Block, state: String, meta: Int? = null) {
		check(block in blocks) { "Alias $name: block is not registered" }
		check(aliases.none { it.oldName == name && (it.oldMeta == meta || it.oldMeta == null || meta == null) }) { "Alias $name${meta ?: ""} is registered twice" }
		aliases += Alias(name, block, state, meta)
	}

	/** Старое имя [name] — блок другого мода [block] ([Replacement]) */
	fun replace(name: String, block: Block) {
		check(blocks.values.none { it.oldName == name } && aliases.none { it.oldName == name }) { "Replacement $name: the name is registered" }
		check(replacements.none { it.oldName == name }) { "Replacement $name is registered twice" }
		replacements += Replacement(name, block)
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
	
	/**
	 * Существо автора под именем [name] (`EntityRegistry.registerModEntity(класс, имя, номер, мод, 128, 1, true)` 1.7.10):
	 * слежение на 128 блоков, обновление каждый тик, скорость — клиентам. Создаётся конструктором `(World)`, как в
	 * 1.7.10; [category] — для спавна (в 1.7.10 тип существа задавался при добавлении спавна). Id — имя в snake_case
	 * (`ThrownPotion` → `alfheim:thrown_potion`), имя 1.7.10 — `alfheim.ThrownPotion`
	 */
	fun entity(clazz: Class<out Entity>, name: String, category: MobCategory = MobCategory.MISC) {
		check(pendingEntities.none { it.first == clazz } && clazz !in entityTypes) { "Entity $name is registered twice" }
		pendingEntities += Triple(clazz, name, category)
	}
	
	/** Тип 1.20.1 существа автора: конструктор существа 1.20.1 принимает его первым аргументом */
	@Suppress("UNCHECKED_CAST")
	fun <T: Entity> entityType(clazz: Class<T>) = entityTypes[clazz] as EntityType<T>? ?: throw IllegalStateException("Entity ${clazz.name} is not registered: ASJUtilities.registerEntity")
	
	private fun registerEntities(e: RegisterEvent) {
		for ((clazz, name, category) in pendingEntities) {
			val entry = Entry(id(name, null), name, null)
			check(entities.values.none { it.id == entry.id }) { "Entity id ${entry.id} is registered twice" }
			val constructor = clazz.getConstructor(Level::class.java)
			val builder = EntityType.Builder.of<Entity>({ _, level -> constructor.newInstance(level) }, category)
			if (EntityWeatherEffect::class.java.isAssignableFrom(clazz))
				// погодный эффект 1.7.10 — как молния 1.20.1: мир его не сохраняет, клиент видит его за 16 чанков, а
				// положение после появления сервер не шлёт — эффект стоит на месте
				builder.noSave().clientTrackingRange(16).updateInterval(Int.MAX_VALUE)
			else
				builder.clientTrackingRange(8) // 128 блоков
					.updateInterval(1)
					.setShouldReceiveVelocityUpdates(true)
			val type = builder.build(entry.id.toString())
			entityTypes[clazz] = type
			entities[type] = entry
			e.register(Registries.ENTITY_TYPE, entry.id) { type }
		}
		pendingEntities.clear()
	}
	
	/**
	 * Блок-сущность автора под именем [name] (`GameRegistry.registerTileEntity(класс, имя)` 1.7.10, имя — с `modid:`).
	 * Создаётся конструктором `(BlockPos, BlockState)` (`alfheim.port.legacy.TileEntity`). Id — имя без `modid:` в
	 * snake_case: `alfheim:TreeBerry` → `alfheim:tree_berry`
	 */
	fun tile(clazz: Class<out BlockEntity>, name: String) {
		check(pendingTiles.none { it.first == clazz } && clazz !in tileTypes) { "Block entity $name is registered twice" }
		pendingTiles += clazz to name
	}
	
	/** Тип 1.20.1 блок-сущности автора: конструктор блок-сущности 1.20.1 принимает его первым аргументом */
	@Suppress("UNCHECKED_CAST")
	fun <T: BlockEntity> tileType(clazz: Class<T>) = tileTypes[clazz] as BlockEntityType<T>? ?: throw IllegalStateException("Block entity ${clazz.name} is not registered: GameRegistry.registerTileEntity")
	
	/**
	 * Блок-сущность типа [type] создана в блоке [block]. Блок-сущность 1.7.10 не знала свой блок заранее, поэтому тип
	 * 1.20.1 считает своими блоки, в которых его блок-сущности создавались: тикает и рисует блок-сущность, только если
	 * она стоит в таком блоке (`BlockEntityType.isValid`)
	 */
	fun tileCreated(type: BlockEntityType<*>, block: Block) {
		tileBlocks[type]?.add(block)
	}
	
	/**
	 * Блок-сущность типа [type] попала в мир на клиенте ([client]) или сервере и ответила `canUpdate()` 1.7.10
	 * [canUpdate]. Мир 1.7.10 спрашивал об этом каждую блок-сущность и не тикал ту, что ответила `false`; 1.20.1 решает,
	 * тикать ли, по типу и состоянию блока (`getTicker`). У блок-сущностей автора ответ зависит только от класса и
	 * стороны (`TileItemDisplay` тикает только на сервере), поэтому тип запоминает ответ первой своей блок-сущности на
	 * каждой стороне
	 */
	fun tileLoaded(type: BlockEntityType<*>, client: Boolean, canUpdate: Boolean) {
		tileUpdates.putIfAbsent(type to client, canUpdate)
	}
	
	/** Тикают ли блок-сущности типа [type] на клиенте ([client]) или сервере ([tileLoaded]); пока ни одной не было — да */
	fun tileUpdates(type: BlockEntityType<*>, client: Boolean) = tileUpdates[type to client] ?: true
	
	private fun registerTiles(e: RegisterEvent) {
		for ((clazz, name) in pendingTiles) {
			val entry = Entry(id(name, null), name, null)
			check(tiles.values.none { it.id == entry.id }) { "Block entity id ${entry.id} is registered twice" }
			val constructor = clazz.getConstructor(BlockPos::class.java, BlockState::class.java)
			// блок-сущности создаются и в потоках загрузки чанков
			val blocks = ConcurrentHashMap.newKeySet<Block>()
			val type = BlockEntityType({ pos, state -> constructor.newInstance(pos, state) }, blocks, null)
			tileTypes[clazz] = type
			tileBlocks[type] = blocks
			tiles[type] = entry
			e.register(Registries.BLOCK_ENTITY_TYPE, entry.id) { type }
		}
		pendingTiles.clear()
	}
	
	private fun item(item: Item, entry: Entry) {
		val e = event?.takeIf { it.registryKey == Registries.ITEM } ?: throw IllegalStateException("Item ${entry.id} is created outside of the item registration: create it from LegacyRegistration.onItems")
		check(items.values.none { it.id == entry.id }) { "Item id ${entry.id} is registered twice" }
		items[item] = entry
		e.register(Registries.ITEM, entry.id) { item }
	}
}
