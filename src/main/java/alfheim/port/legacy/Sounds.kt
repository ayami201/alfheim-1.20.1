package alfheim.port.legacy

import alfheim.api.ModInfo.MODID
import alfheim.port.registry.AlfheimSounds
import com.google.gson.JsonParser
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level

/**
 * Звук по имени 1.7.10 (SPEC, Р-4). Звуки Alfheim (`alfheim:quad`) — события из sounds.json мода ([AlfheimSounds]);
 * звуки ванилы и Botania (`random.fizz`, `botania:ding`) — по таблице `alfheim/legacy_sounds.json`: событие 1.20.1,
 * которое играет те же файлы (собирает tools/legacy_sounds.py). Категория — из sounds.json 1.7.10: в 1.20.1 её
 * передаёт код, иначе звук попал бы под другой ползунок громкости.
 */
object LegacySounds {

	private class Entry(val event: ResourceLocation, val source: SoundSource)

	private val table: Map<String, Entry> = LegacySounds::class.java.getResourceAsStream("/$MODID/legacy_sounds.json")!!.reader().use { reader ->
		JsonParser.parseReader(reader).asJsonObject.getAsJsonObject("sounds").entrySet().associate { (name, value) ->
			val category = value.asJsonObject["category"].asString
			name to Entry(ResourceLocation(value.asJsonObject["event"].asString), SoundSource.values().first { it.getName() == category })
		}
	}

	private fun alfheim(name: String) = name.removePrefix("$MODID:").takeIf { name.startsWith("$MODID:") }

	fun event(name: String): SoundEvent {
		alfheim(name)?.let { return AlfheimSounds.events[it]?.get() ?: unknown(name) }
		val entry = table[name.removePrefix("minecraft:")] ?: unknown(name)
		return BuiltInRegistries.SOUND_EVENT.getOptional(entry.event).orElseThrow { IllegalStateException("Sound event ${entry.event} for $name is not registered") }
	}

	fun source(name: String): SoundSource {
		alfheim(name)?.let { return AlfheimSounds.source(it) }
		return (table[name.removePrefix("minecraft:")] ?: unknown(name)).source
	}

	private fun unknown(name: String): Nothing = throw IllegalArgumentException("Sound $name is not in legacy_sounds.json: run python3 tools/legacy_sounds.py")
}

/** 1.7.10: на сервере — звук всем игрокам рядом, на клиенте — ничего (звук приходит с сервера); так же в 1.20.1 с игроком null */
fun Level.playSoundEffect(x: Double, y: Double, z: Double, name: String, volume: Float, pitch: Float) =
	playSound(null, x, y, z, LegacySounds.event(name), LegacySounds.source(name), volume, pitch)

/** 1.7.10: звук у ног сущности (`posY - yOffset`), то есть в точке `x, y, z` 1.20.1 */
fun Level.playSoundAtEntity(entity: Entity, name: String, volume: Float, pitch: Float) =
	playSound(null, entity.x, entity.y, entity.z, LegacySounds.event(name), LegacySounds.source(name), volume, pitch)

/** 1.7.10 `World.playSound`: звук только на этом клиенте */
fun Level.playSound(x: Double, y: Double, z: Double, name: String, volume: Float, pitch: Float, distanceDelay: Boolean) =
	playLocalSound(x, y, z, LegacySounds.event(name), LegacySounds.source(name), volume, pitch, distanceDelay)
