package alfheim.port.config

/**
 * Опции автора, которые лежат в клиентском файле настроек (SPEC, Р-12): интерфейс и графика.
 * Категории и имена — те же, что у автора, меняется только файл.
 *
 * Опция попадает сюда, только если весь код автора, который её читает, работает на клиенте:
 * рендер, интерфейс, подсказки. `minimalGraphics` сюда не входит: у автора от неё зависит и
 * сервер (непрозрачность `BlockPowerStone`).
 */
object ClientOptions {

	/** Файл автора → его клиентская часть */
	val files = mapOf("Alfheim/mod.toml" to "Alfheim/client.toml")

	private val options = mapOf(
		"Alfheim/mod.toml" to setOf(
			// интерфейс: general.elvenstory.mmo.hud
			"partyHUDScale", "selfHealthUI", "spellsFadeOut", "targethUI",
			// графика
			"disableWireframe", "effectScreenOverlay", "eventBanner", "fancies", "floodLightQuality",
			"lightningsSpeed", "numericalMana", "rainbowPolys", "renderBooba",
		)
	)

	fun isClient(fileName: String, key: String) = options[fileName]?.contains(key) == true
}
