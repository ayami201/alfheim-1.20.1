package alfheim.port.data

import alexsocol.asjlib.extendables.ItemBlockMetaName
import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.api.ModInfo.MODID
import alfheim.common.item.block.ItemBlockLeavesMod
import alfheim.port.legacy.LegacyBlock
import alfheim.port.registry.LegacyRegistration
import com.google.gson.*
import net.minecraft.data.*
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
		json.add("blocks", ids(LegacyRegistration.blocks.values))
		json.add("items", ids(LegacyRegistration.items.values))
		json.add("entities", JsonObject())
		json.add("lang", JsonObject().apply {
			for ((block, entry) in LegacyRegistration.blocks)
				legacyLangKey(block)?.let { addProperty(it, block.descriptionId) }
		})
		return DataProvider.saveStable(cache, json, output.outputFolder.resolve("alfheim/legacy_ids.json"))
	}

	/** Старое имя → новый id, у вариантов metadata — объект «metadata → новый id» */
	private fun ids(entries: Collection<LegacyRegistration.Entry>) = JsonObject().apply {
		for ((oldName, group) in entries.groupBy { "$MODID:${it.oldName}" }) {
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
		 * `ItemBlockLeavesMod` — приставку `alfheim:`. Предмет 1.20.1 берёт ключ блока, поэтому старый ключ
		 * переименовывается в ключ блока
		 */
		fun legacyLangKey(block: Block): String? {
			val legacy = block as? LegacyBlock ?: return null
			val item = block.asItem()
			var key = legacy.legacy.unlocalizedName
			if (item is ItemBlockMetaName && ((block as? BlockModMeta)?.subtypes ?: 16) > 1) key += legacy.variant ?: 0
			if (item is ItemBlockLeavesMod) key = key.replace("tile.", "tile.$MODID:")
			return "$key.name"
		}
	}
}
