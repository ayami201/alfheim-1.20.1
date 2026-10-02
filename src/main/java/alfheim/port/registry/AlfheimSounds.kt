package alfheim.port.registry

import alfheim.api.ModInfo.MODID
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraftforge.registries.RegistryObject

/**
 * Звуковые события автора (`assets/alfheim/sounds.json`). В 1.7.10 звук задавался только записью в sounds.json и
 * проигрывался по имени (`alfheim:fenrir.attack`); в 1.20.1 событие ещё и регистрируется. Список берётся из того же
 * файла, чтобы имена не повторялись в коде.
 *
 * Поле `category` 1.20.1 не читает: громкость какого ползунка меняет звук, решает код при проигрывании
 * (`SoundSource`). Его читает [source], чтобы звук играл в той же категории, что у автора.
 */
object AlfheimSounds {

	private val json: JsonObject = AlfheimSounds::class.java.getResourceAsStream("/assets/$MODID/sounds.json")!!.reader().use { JsonParser.parseReader(it).asJsonObject }

	/** Имя события автора (`fenrir.attack`) → событие в реестре */
	val events: Map<String, RegistryObject<SoundEvent>> = json.keySet().associateWith { name ->
		AlfheimRegisters.SOUND_EVENTS.register(name) { SoundEvent.createVariableRangeEvent(ResourceLocation(MODID, name)) }
	}

	/** Категория звука автора → `SoundSource`; имена категорий 1.7.10 и 1.20.1 совпадают (`player`, `hostile`, `block`…) */
	fun source(name: String): SoundSource {
		val category = json.getAsJsonObject(name)?.get("category")?.asString ?: return SoundSource.MASTER
		return SoundSource.values().first { it.getName() == category }
	}
}
