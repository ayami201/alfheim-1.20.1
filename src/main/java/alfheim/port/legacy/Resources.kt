package alfheim.port.legacy

import alfheim.port.registry.AlfheimRegisters

/**
 * Путь ресурса автора → путь 1.20.1. В 1.20.1 в пути ресурса допустимы только строчные буквы, цифры и `_-./`,
 * поэтому каждая часть пути переводится в snake_case, как имена в реестре (SPEC, Р-5), расширение — строчными:
 * `textures/model/item/AkashicRecordsCube.png` → `textures/model/item/akashic_records_cube.png`.
 * Ресурсы автора переносятся из `legacy/` под этими именами.
 */
fun legacyPath(path: String) = path.split('/').joinToString("/") { part ->
	val dot = part.lastIndexOf('.')
	if (dot > 0) AlfheimRegisters.snakeCase(part.substring(0, dot)) + part.substring(dot).lowercase() else AlfheimRegisters.snakeCase(part)
}
