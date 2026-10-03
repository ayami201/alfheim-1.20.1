package alfheim.port.data

import alfheim.api.ModInfo.MODID
import alfheim.port.legacy.*
import alfheim.port.registry.LegacySpecialRecipes
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraftforge.registries.ForgeRegistries
import java.util.function.Consumer

/**
 * Рецепты автора → JSON 1.20.1 (SPEC, Р-9; MAPPING.md, «Рецепты»). `AlfheimRecipes` записывает рецепты в прослойку
 * 1.7.10 (`CraftingManager`, `FurnaceRecipes`, `alfheim.port.legacy.Recipes1710`), отсюда они выходят рецептами ванилы:
 * по шаблону — `minecraft:crafting_shaped`, без формы — `minecraft:crafting_shapeless`, печь — `minecraft:smelting`
 * (200 тиков, как печь 1.7.10), особый рецепт — свой тип ([LegacySpecialRecipes]).
 *
 * Id рецепта — путь id результата (`alfheim:elvorium_ingot`), у переплавки — с `_from_smelting`; одинаковые — с номером
 * `_2`, `_3` в порядке рецептов автора; у особого рецепта — его имя. Достижений, которые открывают рецепт в книге
 * рецептов, нет: книги рецептов в 1.7.10 не было, рецепт попадает в неё, когда игрок его сделает.
 */
class AlfheimRecipeProvider(output: PackOutput): RecipeProvider(output) {

	override fun buildRecipes(writer: Consumer<FinishedRecipe>) {
		val counts = HashMap<String, Int>()
		fun id(path: String): ResourceLocation {
			val n = counts.merge(path, 1, Int::plus)!!
			return ResourceLocation(MODID, if (n == 1) path else "${path}_$n")
		}

		val written = HashMap<String, ResourceLocation>()
		val unique = Consumer<FinishedRecipe> { recipe ->
			val data = recipe.serializeRecipe().toString()
			written.put(data, recipe.id)?.let { throw IllegalStateException("Recipes ${recipe.id} and $it are the same") }
			writer.accept(recipe)
		}

		for (recipe in CraftingManager.getInstance().recipeList) when (recipe) {
			is ShapedOreRecipe    -> unique.accept(shaped(id(path(recipe.output)), recipe))
			is ShapelessOreRecipe -> unique.accept(shapeless(id(path(recipe.output)), recipe))
			else                  -> unique.accept(special(recipe))
		}

		for (smelting in FurnaceRecipes.smelting().smeltingList)
			unique.accept(smelting(id(path(smelting.output) + "_from_smelting"), smelting))
	}

	private fun shaped(id: ResourceLocation, recipe: ShapedOreRecipe) = Finished(id, RecipeSerializer.SHAPED_RECIPE) { json ->
		val key = recipe.key().filterValues { it !== Ingredient.EMPTY }
		// символ без ингредиента — пустая клетка, как в 1.7.10; ингредиента без символа в шаблоне 1.20.1 не принимает
		val pattern = recipe.pattern.map { row -> row.map { if (it in key) it else ' ' }.joinToString("") }
		// 1.20.1 срезает пустые края шаблона, и рецепт можно сложить в любом месте сетки, а 1.7.10 — только там, где
		// шаблон: такой рецепт переносится отдельно
		check(pattern.first().isNotBlank() && pattern.last().isNotBlank() && pattern.any { it.first() != ' ' } && pattern.any { it.last() != ' ' }) {
			"Recipe $id has empty pattern edges: $pattern"
		}
		json.addProperty("category", if (recipe.output?.item is BlockItem) "building" else "misc")
		json.add("pattern", JsonArray().apply { pattern.forEach(::add) })
		json.add("key", JsonObject().apply { for ((c, ingredient) in key) if (pattern.any { c in it }) add(c.toString(), ingredient.toJson()) })
		json.add("result", result(id, recipe.output))
	}

	private fun shapeless(id: ResourceLocation, recipe: ShapelessOreRecipe) = Finished(id, RecipeSerializer.SHAPELESS_RECIPE) { json ->
		val ingredients = recipe.ingredients()
		check(ingredients.none { it === Ingredient.EMPTY }) { "Recipe $id has an empty ingredient: ${recipe.inputs}" }
		json.addProperty("category", if (recipe.output?.item is BlockItem) "building" else "misc")
		json.add("ingredients", JsonArray().apply { ingredients.forEach { add(it.toJson()) } })
		json.add("result", result(id, recipe.output))
	}

	private fun smelting(id: ResourceLocation, smelting: Smelting1710) = Finished(id, RecipeSerializer.SMELTING_RECIPE) { json ->
		val output = smelting.output
		json.addProperty("category", if (output.isEdible) "food" else if (output.item is BlockItem) "blocks" else "misc")
		json.add("ingredient", smelting.ingredient().toJson())
		val result = result(id, output)
		if (result.size() == 1) json.add("result", result["item"]) else json.add("result", result)
		json.addProperty("experience", smelting.experience)
		json.addProperty("cookingtime", 200)
	}

	/** Особый рецепт: имя `RecipeSorter` автора, класс — в [LegacySpecialRecipes] */
	private fun special(recipe: IRecipe): Finished {
		val name = RecipeSorter.names[recipe.javaClass] ?: throw IllegalStateException("Special recipe ${recipe.javaClass.name} has no RecipeSorter name")
		check(LegacySpecialRecipes.recipes[name]?.invoke() === recipe) { "Special recipe $name (${recipe.javaClass.name}) is not in LegacySpecialRecipes" }
		val id = ResourceLocation(MODID, LegacySpecialRecipes.id(name))
		return Finished(id, ForgeRegistries.RECIPE_SERIALIZERS.getValue(id)!!) { json -> json.addProperty("category", "misc") }
	}

	private fun path(stack: ItemStack?) = BuiltInRegistries.ITEM.getKey(requireNotNull(stack) { "1.7.10 recipe without a result" }.item).path

	/** Результат: предмет, число, NBT; `Damage` 0 у инструмента 1.20.1 ставит сам */
	private fun result(id: ResourceLocation, stack: ItemStack?): JsonObject {
		check(stack != null && !stack.isEmpty) { "Recipe $id has no result" }
		val tag = stack.tag?.copy()?.apply { if (getInt("Damage") == 0) remove("Damage") }
		return JsonObject().apply {
			addProperty("item", BuiltInRegistries.ITEM.getKey(stack.item).toString())
			if (stack.count > 1) addProperty("count", stack.count)
			if (tag != null && !tag.isEmpty) addProperty("nbt", tag.toString())
		}
	}

	private class Finished(private val id: ResourceLocation, private val serializer: RecipeSerializer<*>, private val data: (JsonObject) -> Unit): FinishedRecipe {
		override fun serializeRecipeData(json: JsonObject) = data(json)
		override fun getId() = id
		override fun getType() = serializer
		override fun serializeAdvancement(): JsonObject? = null
		override fun getAdvancementId(): ResourceLocation? = null
	}
}
