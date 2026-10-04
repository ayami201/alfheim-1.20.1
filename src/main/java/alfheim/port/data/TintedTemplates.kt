package alfheim.port.data

import alfheim.api.ModInfo.MODID
import com.google.gson.*
import net.minecraft.data.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraftforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

/**
 * Окрашенные копии шаблонов моделей ванилы: `alfheim:block/tinted_<шаблон>` — шаблон с tintindex 0 у каждой грани.
 * Блок 1.7.10, которого красил класс автора (`colorMultiplier`, `getRenderColor`), красился целиком, а шаблоны ванилы
 * 1.20.1 не окрашены (кроме листвы `block/leaves` и креста `block/tinted_cross`). Копия строится из шаблона ванилы:
 * элементы — у ближайшего родителя, у которого они есть, текстурные переменные и прочие ключи — как у шаблона
 */
class TintedTemplates(private val output: PackOutput, private val files: ExistingFileHelper): DataProvider {

	override fun run(cache: CachedOutput): CompletableFuture<*> {
		val folder = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve("$MODID/models/block")
		return CompletableFuture.allOf(*TEMPLATES.map { DataProvider.saveStable(cache, tinted(it), folder.resolve("$PREFIX$it.json")) }.toTypedArray())
	}

	private fun read(location: ResourceLocation): JsonObject =
		files.getResource(location, PackType.CLIENT_RESOURCES, ".json", "models").openAsReader().use { JsonParser.parseReader(it).asJsonObject }

	private fun tinted(template: String): JsonObject {
		// шаблон и его родители до первого с элементами
		val chain = mutableListOf(read(ResourceLocation("block/$template")))
		while (!chain.last().has("elements")) chain += read(ResourceLocation(chain.last()["parent"].asString))
		val source = chain.last()

		val result = JsonObject()
		source["parent"]?.let { result.add("parent", it) }
		val textures = JsonObject()
		for (model in chain.asReversed()) for ((key, value) in model.entrySet()) when (key) {
			"parent", "elements" -> Unit
			"textures"           -> for ((name, texture) in value.asJsonObject.entrySet()) textures.add(name, texture)
			else                 -> result.add(key, value)
		}
		if (textures.size() > 0) result.add("textures", textures)

		val elements = source["elements"].deepCopy().asJsonArray
		for (element in elements) for ((_, face) in element.asJsonObject["faces"].asJsonObject.entrySet()) face.asJsonObject.addProperty("tintindex", 0)
		result.add("elements", elements)
		return result
	}

	override fun getName() = "Alfheim tinted model templates"

	companion object {

		const val PREFIX = "tinted_"

		/** Шаблоны ванилы, у которых есть окрашенная копия */
		val TEMPLATES = listOf("cube_all", "cube_column", "cube_column_horizontal", "slab", "slab_top", "stairs", "inner_stairs", "outer_stairs")
	}
}
