package alexsocol.asjlib.extendables

// PORT: Configuration 1.7.10 → прослойка alfheim.port.config.Configuration поверх ForgeConfigSpec (SPEC, Р-12)
import alfheim.port.config.Configuration
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.fml.event.config.ModConfigEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import java.io.File

abstract class ASJConfigHandler {
	
	lateinit var config: Configuration
	
	/**
	 * Function to be called once in [FMLConstructionEvent]
	 * to initialize categories and properties
	 */
	fun loadConfig(cfg: File) {
		config = Configuration(cfg)
		config.load()
		addCategories()
		syncConfig()
		// PORT: ForgeConfigSpec описывает все опции до чтения файла. Проход выше только объявил опции;
		// build() читает файл, второй проход берёт из него значения
		config.build()
		syncConfig()
	}
	
	/**
	 * Call this if you have config GUI for your mod
	 */
	fun registerChangeHandler(modid: String) {
		// PORT: OnConfigChangedEvent (экран настроек 1.7.10) → ModConfigEvent.Reloading на шине мода. Forge шлёт его,
		// когда файл настроек изменили во время игры: вручную или экраном настроек другого мода (например, Configured)
		MOD_BUS.addListener(EventPriority.HIGHEST, false, ModConfigEvent.Reloading::class.java) { e ->
			if (e.config.modId == modid && config.isOwnFile(e.config.fileName)) syncConfig()
		}
	}
	
	fun addCategory(cat: String, comment: String) {
		config.addCustomCategoryComment(cat, comment)
		config.getCategory(cat).setLanguageKey(cat)
	}
	
	fun syncConfig() {
		readProperties()
		
		if (config.hasChanged()) config.save()
	}
	
	open fun addCategories() = Unit
	
	abstract fun readProperties()
	
	fun loadProp(category: String, propName: String, default: Boolean, restart: Boolean, desc: String?): Boolean {
		val prop = config.get(category, propName, default, desc)
		prop.setRequiresMcRestart(restart)
		return prop.getBoolean(default)
	}
	
	@JvmOverloads
	fun loadProp(category: String, propName: String, default: Int, restart: Boolean, desc: String?, min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE): Int {
		val prop = config.get(category, propName, default, desc, min, max)
		prop.setRequiresMcRestart(restart)
		return prop.getInt(default).also { if (it !in min..max) throw IllegalArgumentException("Int $propName is not within required min/max bounds ($it), must be in range $min..$max") }
	}
	
	@JvmOverloads
	fun loadProp(category: String, propName: String, default: IntArray, restart: Boolean, desc: String?, ensureLength: Boolean = true): IntArray {
		val prop = config.get(category, propName, default, desc)
		prop.setRequiresMcRestart(restart)
		return prop.intList.also { if (ensureLength && it.size < default.size) throw IllegalArgumentException("Array $propName is not of suitable length (${it.size}), must be ${default.size}") }
	}
	
	@JvmOverloads
	fun loadProp(category: String, propName: String, default: Double, restart: Boolean, desc: String?, min: Double = -Double.MIN_VALUE, max: Double = Double.MAX_VALUE): Double {
		val prop = config.get(category, propName, default, desc, min, max)
		prop.setRequiresMcRestart(restart)
		return prop.getDouble(default)
	}
	
	@JvmOverloads
	fun loadProp(category: String, propName: String, default: DoubleArray, restart: Boolean, desc: String?, ensureLength: Boolean = true): DoubleArray {
		val prop = config.get(category, propName, default, desc)
		prop.setRequiresMcRestart(restart)
		return prop.doubleList.also { if (ensureLength && it.size < default.size) throw IllegalArgumentException("Array $propName is not of suitable length (${it.size}), must be ${default.size}") }
	}
	
	fun loadProp(category: String, propName: String, default: String, restart: Boolean, desc: String?): String {
		val prop = config.get(category, propName, default, desc)
		prop.setRequiresMcRestart(restart)
		return prop.string
	}
	
	@JvmOverloads
	fun loadProp(category: String, propName: String, default: Array<String>, restart: Boolean, desc: String?, ensureLength: Boolean = true): Array<String> {
		val prop = config.get(category, propName, default, desc)
		prop.setRequiresMcRestart(restart)
		return prop.stringList.also { if (ensureLength && it.size < default.size) throw IllegalArgumentException("Array $propName is not of suitable length (${it.size}), must be ${default.size}") }
	}
}
