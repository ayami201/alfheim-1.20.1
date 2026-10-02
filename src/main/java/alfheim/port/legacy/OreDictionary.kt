package alfheim.port.legacy

import net.minecraft.world.item.ItemStack

/*
 * Ore Dictionary 1.7.10 (SPEC, Р-4; MAPPING.md, «Ore Dictionary»). В 1.20.1 его место заняли теги — данные мода.
 * `regOreDict` автора остаётся как был: `registerOre` записывает пары «имя — вещь», а генерация данных
 * (alfheim.port.data) превращает их в теги по таблице имён. В игре записи ничего не делают.
 */

/** Записи `registerOre` в порядке вызова */
object OreDictionary {
	
	const val WILDCARD_VALUE = Short.MAX_VALUE.toInt()
	
	val entries = ArrayList<Pair<String, ItemStack>>()
}

fun registerOre(name: String, ore: ItemStack) {
	OreDictionary.entries += name to ore.copy()
}
