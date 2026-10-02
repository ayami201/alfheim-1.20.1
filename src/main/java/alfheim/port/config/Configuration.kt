package alfheim.port.config

import com.electronwill.nightconfig.core.file.CommentedFileConfig
import com.electronwill.nightconfig.core.io.WritingMode
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.fml.loading.FMLPaths
import java.io.File
import java.nio.file.Files

/**
 * `Configuration` из Forge 1.7.10 поверх `ForgeConfigSpec` 1.20.1 (SPEC, Р-12): ровно та часть,
 * которую вызывают `ASJConfigHandler` и `ASJPreConfigHandler` автора. Имена опций, категории,
 * комментарии и значения по умолчанию задаёт код автора, здесь они не меняются.
 *
 * `ForgeConfigSpec` должен знать все опции до чтения файла, а автор объявляет и читает опцию
 * одним вызовом. Поэтому `readProperties` автора проходит дважды:
 * 1. объявление: `get(...)` описывает опцию, геттер свойства возвращает значение по умолчанию;
 * 2. [build] собирает спецификацию, регистрирует её в Forge и сразу читает файл. Сам Forge
 *    загружает конфиги только после регистрации блоков и предметов, а автор читает их в
 *    preInit, до неё;
 * 3. чтение: те же `get(...)` возвращают значения из файла.
 *
 * Файл `config/Alfheim/<имя>.cfg` становится `config/Alfheim/<имя>.toml`. Опции из
 * [ClientOptions] лежат в клиентском файле рядом; на выделенном сервере его нет, и там они
 * всегда по умолчанию.
 *
 * Значения проверяются так же, как в 1.7.10: значение не того типа заменяется значением по
 * умолчанию, а границы чисел проверяет код автора в `ASJConfigHandler`.
 */
class Configuration(file: File) {

	companion object {

		const val CATEGORY_GENERAL = "general"
		const val CATEGORY_SPLITTER = "."
	}

	/** Имя файла относительно папки config: `config/Alfheim/mod.cfg` → `Alfheim/mod.toml` */
	val fileName = file.path.replace('\\', '/').removePrefix("./").removePrefix("config/").removeSuffix(".cfg") + ".toml"

	private val common = Section(ModConfig.Type.COMMON, fileName)
	private val client = ClientOptions.files[fileName]?.let { Section(ModConfig.Type.CLIENT, it) }
	private val sections = listOfNotNull(common, client)

	private val categoryComments = LinkedHashMap<String, String>()

	/** Первый проход: опции объявляются. После [build] — читаются */
	private var defining = true

	/** Один из файлов этого конфига: по нему обработчик изменений узнаёт свой файл */
	fun isOwnFile(name: String) = sections.any { it.fileName == name }

	/** Файл читается в [build], когда все опции объявлены */
	fun load() = Unit

	/** Шаг 2 из описания класса: спецификации собираются, регистрируются в Forge и файлы читаются сразу */
	fun build() {
		for (section in sections) {
			for ((category, comment) in categoryComments) {
				val path = category.split(CATEGORY_SPLITTER)
				if (section.values.keys.any { it.size > path.size && it.subList(0, path.size) == path })
					section.builder.comment(comment).push(path).pop(path.size)
			}

			section.spec = section.builder.build()
			if (!section.active) continue

			ModLoadingContext.get().registerConfig(section.type, section.spec, section.fileName)
			section.loadNow()
		}

		defining = false
	}

	fun addCustomCategoryComment(category: String, comment: String) {
		categoryComments[category.lowercase()] = comment
	}

	fun getCategory(category: String) = ConfigCategory

	/** `ForgeConfigSpec` сам дописывает недостающие опции и сохраняет файл при чтении */
	fun hasChanged() = false

	fun save() {
		for (section in sections)
			if (section.active && section.isLoaded) section.spec.save()
	}

	fun get(category: String, key: String, defaultValue: Boolean, comment: String?) =
		Property(category, key, comment, defaultValue, Boolean::class.javaObjectType) { it is Boolean }

	fun get(category: String, key: String, defaultValue: Int, comment: String?, minValue: Int, maxValue: Int) =
		Property(category, key, comment, defaultValue, Int::class.javaObjectType) { it is Int }

	fun get(category: String, key: String, defaultValues: IntArray, comment: String?) =
		Property(category, key, comment, defaultValues.toList(), List::class.java) { it is List<*> && it.all { e -> e is Int } }

	fun get(category: String, key: String, defaultValue: Double, comment: String?, minValue: Double, maxValue: Double) =
		Property(category, key, comment, defaultValue, Double::class.javaObjectType) { it is Number }

	fun get(category: String, key: String, defaultValues: DoubleArray, comment: String?) =
		Property(category, key, comment, defaultValues.toList(), List::class.java) { it is List<*> && it.all { e -> e is Number } }

	fun get(category: String, key: String, defaultValue: String, comment: String?) =
		Property(category, key, comment, defaultValue, String::class.java) { it is String }

	fun get(category: String, key: String, defaultValues: Array<String>, comment: String?) =
		Property(category, key, comment, defaultValues.toList(), List::class.java) { it is List<*> && it.all { e -> e is String } }

	/** Категория 1.7.10 нужна автору только ради ключа перевода для экрана настроек */
	object ConfigCategory {

		fun setLanguageKey(key: String) = this
	}

	inner class Property internal constructor(category: String, key: String, private val comment: String?, private val default: Any, private val clazz: Class<*>, private val validator: (Any?) -> Boolean) {

		private val section = if (client != null && ClientOptions.isClient(fileName, key)) client else common
		// Точка в имени опции (wire.overpowered, TiC.materialIDs) — тоже уровень пути: ForgeConfigSpec не умеет
		// комментарии у ключей с точкой и переписывает такой файл при каждом чтении. Полное имя опции то же
		private val path = category.lowercase().split(CATEGORY_SPLITTER) + key.split(CATEGORY_SPLITTER)
		private var restart = false

		fun setRequiresMcRestart(requiresMcRestart: Boolean): Property {
			restart = requiresMcRestart
			return this
		}

		fun getBoolean(default: Boolean) = value() as Boolean
		fun getInt(default: Int) = (value() as Number).toInt()
		fun getDouble(default: Double) = (value() as Number).toDouble()

		val intList get() = (value() as List<*>).map { (it as Number).toInt() }.toIntArray()
		val doubleList get() = (value() as List<*>).map { (it as Number).toDouble() }.toDoubleArray()
		val string get() = value() as String
		val stringList get() = (value() as List<*>).map { it as String }.toTypedArray()

		/** Комментарий опции задаётся при объявлении: в отличие от 1.7.10, `get(...)` с другим комментарием его не меняет */
		fun set(value: Boolean) = setValue(value)

		private fun value(): Any {
			if (defining) {
				if (path !in section.values) {
					if (!comment.isNullOrBlank()) section.builder.comment(comment)
					// В 1.20.1 у общих конфигов есть только «нужен перезаход в мир», а не перезапуск игры
					if (restart) section.builder.worldRestart()
					section.values[path] = section.builder.define(path, { default }, { validator(it) }, clazz)
				}

				return default
			}

			if (!section.active) return default
			val value = section.values[path] ?: throw IllegalStateException("Config option ${path.joinToString(CATEGORY_SPLITTER)} was not declared in the first pass of readProperties")
			return value.get()
		}

		private fun setValue(value: Any) {
			if (defining || !section.active) return
			section.values[path]?.set(value)
		}
	}

	private class Section(val type: ModConfig.Type, val fileName: String) {

		val builder = ForgeConfigSpec.Builder()
		val values = HashMap<List<String>, ForgeConfigSpec.ConfigValue<Any>>()
		lateinit var spec: ForgeConfigSpec

		/** Клиентский файл на выделенном сервере не читается */
		val active get() = type != ModConfig.Type.CLIENT || FMLEnvironment.dist == Dist.CLIENT

		val isLoaded get() = ::spec.isInitialized && spec.isLoaded

		/** Тот же файл Forge откроет ещё раз на своём шаге загрузки конфигов; до того значения берутся отсюда */
		fun loadNow() {
			val path = FMLPaths.CONFIGDIR.get().resolve(fileName)
			Files.createDirectories(path.parent)
			val data = CommentedFileConfig.builder(path).sync().preserveInsertionOrder().writingMode(WritingMode.REPLACE).build()
			data.load()
			spec.setConfig(data)
		}
	}
}
