package alfheim.port.legacy

import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block

/*
 * Ore Dictionary 1.7.10 (SPEC, Р-4; MAPPING.md, «Ore Dictionary»). В 1.20.1 его место заняли теги — данные мода.
 * `regOreDict` автора остаётся как был: `registerOre` записывает пары «имя — вещь», а генерация данных
 * (alfheim.port.data) превращает их в теги по таблице имён. В игре записи ничего не делают.
 */

/** Записи `registerOre` в порядке вызова */
object OreDictionary {
	
	const val WILDCARD_VALUE = Short.MAX_VALUE.toInt()
	
	val entries = ArrayList<Pair<String, ItemStack>>()

	/** `OreDictionary.registerOre(name, stack)` 1.7.10 */
	fun registerOre(name: String, ore: ItemStack) {
		entries += name to ore.copy()
	}
}

/** `registerOre` из `import net.minecraftforge.oredict.OreDictionary.*` */
fun registerOre(name: String, ore: ItemStack) = OreDictionary.registerOre(name, ore)

/** `registerOre(name, block)` 1.7.10 — вещь блока с любой metadata; вариант в порту — свой блок (SPEC, Р-5) */
fun registerOre(name: String, ore: Block) = OreDictionary.registerOre(name, ItemStack(ore))

/** `registerOre(name, item)` 1.7.10 — предмет с любой metadata */
fun registerOre(name: String, ore: Item) = OreDictionary.registerOre(name, ItemStack(ore))
