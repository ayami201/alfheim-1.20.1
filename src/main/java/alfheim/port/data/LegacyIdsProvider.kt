package alfheim.port.data

import alexsocol.asjlib.extendables.ItemBlockMetaName
import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.api.ModInfo.MODID
import alfheim.common.item.block.*
import alfheim.port.legacy.*
import alfheim.port.legacy.botania.*
import alfheim.port.registry.LegacyRegistration
import com.google.gson.*
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.*
import net.minecraft.world.item.*
import net.minecraft.world.level.block.Block
import java.util.concurrent.CompletableFuture

/**
 * `alfheim/legacy_ids.json` (SPEC, Р-5; MAPPING.md, «legacy_ids.json») — по всему, что зарегистрировано через
 * `LegacyRegistration`: старое имя и metadata → новый id, старый ключ перевода → новый.
 */
class LegacyIdsProvider(private val output: PackOutput): DataProvider {

	override fun run(cache: CachedOutput): CompletableFuture<*> {
		val json = JsonObject()
		json.add("_comment", JsonArray().apply {
			add("Соответствие имён Alfheim 1.7.10 и 1.20.1 (SPEC, Р-5). Файл строит генерация данных (alfheim.port.data.LegacyIdsProvider)")
			add("по блокам и предметам, зарегистрированным в порту; руками не правится. Из него читают все: загрузчик построек,")
			add("перевод ключей lang (tools/convert_lang.py), лексикон, тесты паритета. Формат — docs/port/MAPPING.md, раздел «legacy_ids.json».")
		})
		json.add("blocks", ids(LegacyRegistration.blocks.values).apply {
			// блок 1.7.10, который в 1.20.1 — состояние другого блока (двойная плита)
			for (alias in LegacyRegistration.aliases)
				addProperty("$MODID:${alias.oldName}", "${LegacyRegistration.blocks[alias.block]!!.id}[${alias.state}]")
			// блок автора, вместо которого блок другого мода
			for (replacement in LegacyRegistration.replacements)
				addProperty("$MODID:${replacement.oldName}", BuiltInRegistries.BLOCK.getKey(replacement.block).toString())
		})
		json.add("items", ids(LegacyRegistration.items.values).apply {
			for (replacement in LegacyRegistration.replacements)
				addProperty("$MODID:${replacement.oldName}", BuiltInRegistries.ITEM.getKey(replacement.block.asItem()).toString())
		})
		json.add("entities", ids(LegacyRegistration.entities.values))
		json.add("lang", JsonObject().apply {
			// у вариантов с общим именем 1.7.10 (tile.alfheim:irisWood.name) старый ключ один, новых — по ключу на блок
			val lang = LinkedHashMap<String, MutableList<String>>()
			fun rename(old: String, new: String) = lang.getOrPut(old) { ArrayList() }.add(new)
			for ((block, entry) in LegacyRegistration.blocks)
				legacyLangKey(block)?.let { rename(it, block.descriptionId) }
			for ((item, entry) in LegacyRegistration.items)
				legacyLangKey(item)?.let { rename(it, "item.${entry.id.namespace}.${entry.id.path}") }
			// имя зелья 1.7.10 — ключ из setPotionName, 1.20.1 — ключ эффекта
			for (potion in LegacyRegistration.effects.keys)
				rename((potion as Potion1710).name, potion.descriptionId)
			// имя существа 1.7.10 — `entity.` + имя в EntityList (`alfheim.ThrownPotion`) + `.name`, 1.20.1 — ключ типа
			for ((type, entry) in LegacyRegistration.entities)
				rename("entity.$MODID.${entry.oldName}.name", type.descriptionId)
			for ((old, new) in lang)
				if (new.size == 1) addProperty(old, new[0]) else add(old, JsonArray().apply { new.forEach { add(it) } })
		})
		return DataProvider.saveStable(cache, json, output.outputFolder.resolve("alfheim/legacy_ids.json"))
	}

	/** Старое имя → новый id, у вариантов metadata — объект «metadata → новый id» */
	private fun ids(entries: Collection<LegacyRegistration.Entry>) = JsonObject().apply {
		for ((oldName, group) in entries.groupBy { it.legacyId }) {
			if (group.size == 1 && group[0].oldMeta == null)
				addProperty(oldName, group[0].id.toString())
			else
				add(oldName, JsonObject().apply { group.forEach { addProperty(it.oldMeta.toString(), it.id.toString()) } })
		}
	}

	override fun getName() = "Alfheim legacy ids"

	companion object {

		/**
		 * Ключ перевода предмета-блока в 1.7.10: `tile.` + имя блока; `ItemBlockMetaName` дописывал номер варианта,
		 * `ItemBlockLeavesMod` — приставку `alfheim:`, `ItemBlockMod` и `ItemBlockModSlab` Botania — `botania:`,
		 * `ItemBlockWithMetadataAndName` Botania — `botania:` и номер варианта, `ItemBlockSpecialQuartz` Botania — имя
		 * варианта из `getNames` блока. `ItemSubtypedBlockMod`, `ItemIridescentBlockMod`, `ItemSlabMod` и
		 * `ItemShimmerSlabMod` — приставку `alfheim:` и убирали номер в конце
		 * имени (у всех цветов одно имя), `ItemUniqueSubtypedBlockMod` — дописывал номер варианта по модулю числа видов,
		 * `ItemMetaSlabMod` — номер варианта без бита 8, `ItemRainbowGrassMod` — приставку `alfheim:` и номер варианта (у
		 * блока без вариантов — 0). Предмет 1.20.1 берёт ключ блока, поэтому старый ключ
		 * переименовывается в ключ блока
		 */
		fun legacyLangKey(block: Block): String? {
			val legacy = block as? LegacyBlock ?: return null
			val item = block.asItem()
			var key = legacy.getUnlocalizedName()
			val variant = legacy.variant ?: 0
			if (item is ItemBlockMetaName && ((block as? BlockModMeta)?.subtypes ?: 16) > 1) key += variant
			if (item is ItemBlockLeavesMod) key = key.replace("tile.", "tile.$MODID:")
			if (item is ItemBlockMod || item is ItemBlockModSlab) key = key.replace("tile.", "tile.botania:")
			if (item is ItemBlockWithMetadataAndName) key = key.replace("tile.", "tile.botania:") + variant
			if (item is ItemBlockSpecialQuartz) key = (block as BlockSpecialQuartz).getNames()[variant]
			if (item is ItemSubtypedBlockMod || item is ItemIridescentBlockMod || item is ItemSlabMod || item is ItemShimmerSlabMod) key = key.replace("tile.", "tile.$MODID:").replace(Regex("\\d+$"), "")
			if (item is ItemUniqueSubtypedBlockMod) key = key.replace("tile.", "tile.$MODID:") + variant % item.subtypes.toInt()
			if (item is ItemMetaSlabMod) key = key.replace("tile.", "tile.$MODID:") + (variant and 0x8.inv())
			if (item is ItemRainbowGrassMod) key = key.replace("tile.", "tile.$MODID:") + variant
			return "$key.name"
		}

		/**
		 * Ключ имени предмета автора в 1.7.10 — `getUnlocalizedNameInefficiently(stack) + ".name"` его вещи без NBT,
		 * как у `getItemStackDisplayName`. Имена, которые автор собирает по NBT (шарик слизи стихии), остаются его
		 * ключами. Имя, которое он выбирает по дате, — не основное: генерация данных берёт будничное ([HOLIDAY_NAMES]),
		 * чтобы файл не зависел от дня запуска
		 */
		fun legacyLangKey(item: Item): String? {
			if (item !is Item1710) return null
			val key = item.getUnlocalizedNameInefficiently(ItemStack(item)) + ".name"
			return HOLIDAY_NAMES[key] ?: key
		}

		/** Праздничное имя → будничное: на праздник (`AlfheimCore.jingleTheBells`) прутик — конфета (ItemElvenResource) */
		private val HOLIDAY_NAMES = mapOf("item.$MODID:InfusedCandy.name" to "item.$MODID:InfusedDreamwoodTwig.name")
	}
}
