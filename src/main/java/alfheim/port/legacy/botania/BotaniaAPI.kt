package alfheim.port.legacy.botania

import alfheim.port.legacy.CraftingManager
import alfheim.port.legacy.IRecipe

/**
 * `vazkii.botania.api.BotaniaAPI` r1.8-249: то, что автор берёт у статического API Botania 1.7.10 при записи рецептов.
 * В Botania 1.20.1 API — объект `BotaniaAPI.instance()` без этих методов; рецепты — данные (SPEC, Р-9). Код автора
 * зовёт `BotaniaAPI.x()` как раньше, импорт — этот объект
 */
object BotaniaAPI {

	/** Последний добавленный рецепт верстака (`CraftingManager`) */
	@JvmStatic
	fun getLatestAddedRecipe(): IRecipe = CraftingManager.getInstance().recipeList.last()

	/** Последние [x] рецептов верстака в порядке добавления */
	@JvmStatic
	fun getLatestAddedRecipes(x: Int): List<IRecipe> = CraftingManager.getInstance().recipeList.takeLast(x).also { require(it.size == x) }
}
