package alfheim.port.data

import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.colored.BlockColoredLamp
import com.google.gson.JsonObject
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.*
import java.util.concurrent.CompletableFuture

/**
 * Цветной свет для мода Colorful Lighting (Forge 1.20.1) — `assets/alfheim/light/emitters.json` (решение владельца,
 * TASKS.md, журнал решений). У автора цвет свету давал мод Easy Colored Lights, которого нет на 1.20.1: блок отдавал ему
 * цвет из кода (`getLightValue`). Здесь тот же цвет записан по состояниям блока, яркость — своя у блока. Без Colorful
 * Lighting файл ничего не меняет.
 *
 * Лампа ириса: при силе сигнала 1–15 — цвет `powerColor` автора (при 15 — белый), при 0 лампа не светит. Радужный гриб
 * светит белым: у автора — `ColoredLightHelper.getPackedColor(0, …)` Botania 1.7.10, цвет шерсти 0. Без записи
 * Colorful Lighting покрасил бы его свет по цвету текстуры.
 */
class ColoredLights(private val output: PackOutput): DataProvider {

	override fun run(cache: CachedOutput): CompletableFuture<*> {
		val json = JsonObject()
		val lamp = AlfheimBlocks.irisLamp as BlockColoredLamp
		json.add(BuiltInRegistries.BLOCK.getKey(lamp).toString(), JsonObject().apply {
			addProperty("default", color(lamp.powerColor(15)))
			add("states", JsonObject().apply {
				for (power in 1..15) addProperty("${BlockColoredLamp.POWER.name}=$power", color(lamp.powerColor(power)))
			})
		})
		json.add(BuiltInRegistries.BLOCK.getKey(AlfheimBlocks.rainbowMushroom).toString(), JsonObject().apply {
			addProperty("default", color(0xFFFFFF))
		})
		return DataProvider.saveStable(cache, json, output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve("alfheim/light/emitters.json"))
	}

	/** `#RRGGBB`, как его читает Colorful Lighting */
	private fun color(rgb: Int) = "#%06X".format(rgb and 0xFFFFFF)

	override fun getName() = "Alfheim colored lights"
}
