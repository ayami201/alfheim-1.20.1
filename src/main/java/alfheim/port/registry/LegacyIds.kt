package alfheim.port.registry

import com.google.gson.*
import net.minecraft.resources.ResourceLocation

/**
 * Соответствие имён 1.7.10 и 1.20.1 из `alfheim/legacy_ids.json` (SPEC, Р-5).
 *
 * `blocks`, `items`, `entities`, `block_entities`: старое имя в реестре 1.7.10 (`modid:name`) → новый id. Если вещь
 * различалась metadata — объект «metadata → новый id», ключ `*` — для любой metadata. К новому id блока можно
 * дописать свойства состояния: `alfheim:alt_wood[axis=y]`.
 * `lang`: старый ключ перевода → новый; если у вариантов одно имя 1.7.10 — список новых ключей (тексты у них одинаковые).
 */
object LegacyIds {
	
	/** Новый id и свойства BlockState (пусто, если состояние по умолчанию) */
	data class Target(val id: ResourceLocation, val state: Map<String, String>)
	
	private val json: JsonObject by lazy {
		val stream = LegacyIds::class.java.getResourceAsStream("/alfheim/legacy_ids.json") ?: throw IllegalStateException("alfheim/legacy_ids.json not found")
		stream.reader(Charsets.UTF_8).use { JsonParser.parseReader(it).asJsonObject }
	}
	
	val blocks by lazy { section("blocks") }
	val items by lazy { section("items") }
	val entities by lazy { section("entities") }
	val blockEntities by lazy { section("block_entities") }
	/** Старый ключ → новый; у общего имени вариантов — первый из новых ключей: текст у них один */
	val lang: Map<String, String> by lazy { json.getAsJsonObject("lang").entrySet().associate { (k, v) -> k to if (v.isJsonArray) v.asJsonArray[0].asString else v.asString } }
	
	/** Новый блок для старого имени и metadata; null, если соответствия нет */
	fun block(oldName: String, meta: Int = 0) = find(blocks, oldName, meta)
	
	/** Новый предмет для старого имени и metadata; null, если соответствия нет */
	fun item(oldName: String, meta: Int = 0) = find(items, oldName, meta)
	
	private fun find(section: Map<String, Map<String, Target>>, oldName: String, meta: Int): Target? {
		val metas = section[oldName] ?: return null
		return metas[meta.toString()] ?: metas["*"]
	}
	
	private fun section(name: String): Map<String, Map<String, Target>> = json.getAsJsonObject(name).entrySet().associate { (old, value) ->
		old to if (value.isJsonPrimitive) mapOf("*" to target(value.asString)) else value.asJsonObject.entrySet().associate { (meta, id) -> meta to target(id.asString) }
	}
	
	private fun target(text: String): Target {
		val bracket = text.indexOf('[')
		if (bracket < 0) return Target(ResourceLocation(text), emptyMap())
		require(text.endsWith("]")) { "Bad legacy id: $text" }
		val state = text.substring(bracket + 1, text.length - 1).split(',').filter { it.isNotBlank() }.associate {
			val (k, v) = it.split('=')
			k.trim() to v.trim()
		}
		return Target(ResourceLocation(text.substring(0, bracket)), state)
	}
}
