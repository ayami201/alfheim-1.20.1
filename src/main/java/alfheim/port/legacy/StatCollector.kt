package alfheim.port.legacy

import net.minecraft.locale.Language
import java.util.IllegalFormatException

/**
 * `StatCollector` из 1.7.10: перевод строки по ключу на языке этой стороны (SPEC, Р-4). На клиенте — язык,
 * выбранный игроком, на сервере — английский, как в 1.7.10. Для текста, который уходит игроку и переводится
 * у него, в 1.20.1 используется `Component.translatable`.
 */
object StatCollector {
	
	fun translateToLocal(key: String): String = Language.getInstance().getOrDefault(key)
	
	/** Как `StringTranslate.translateKeyFormat` 1.7.10: при ошибке формата — «Format error: …» */
	fun translateToLocalFormatted(key: String, vararg args: Any?): String {
		val text = translateToLocal(key)
		return try {
			String.format(text, *args)
		} catch (e: IllegalFormatException) {
			"Format error: $text"
		}
	}
	
	fun canTranslate(key: String) = Language.getInstance().has(key)
}
