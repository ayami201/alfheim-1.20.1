package alfheim.port.registry

import alfheim.common.crafting.recipe.workbench.RecipeElvenWeed
import alfheim.common.crafting.recipe.workbench.RecipeThrowablePotion
import alfheim.port.legacy.IRecipe
import alfheim.port.legacy.MetaIngredient
import com.google.gson.JsonObject
import net.minecraft.core.RegistryAccess
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.GsonHelper
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.Level
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegisterEvent

/**
 * Особые рецепты автора (SPEC, Р-9; MAPPING.md, «Рецепты»): класс автора с `IRecipe` 1.7.10, который
 * `AlfheimRecipes` добавляет `addRecipe(RecipeX)` и называет `RecipeSorter.register(имя, …)`. В 1.20.1 у каждого свой
 * сериализатор с id — именем автора в snake_case (`alfheim:throwpotion`); JSON рецепта с этим типом пишет генерация
 * данных. Рецепт верстака 1.20.1 ([LegacyCraftingRecipe]) зовёт методы 1.7.10 класса автора.
 *
 * Список — по именам `RecipeSorter`: особый рецепт автора, которого здесь нет, роняет генерацию данных. Здесь же
 * регистрируется ингредиент рецептов 1.7.10 `alfheim:meta` ([MetaIngredient])
 */
object LegacySpecialRecipes {

	/** Имя `RecipeSorter.register` без `alfheim:` → рецепт автора */
	val recipes: Map<String, () -> IRecipe> = linkedMapOf(
		"throwpotion" to { RecipeThrowablePotion },
		"elvenweed" to { RecipeElvenWeed },
	)

	/** Id рецепта и сериализатора: имя автора в snake_case */
	fun id(name: String) = AlfheimRegisters.snakeCase(name)

	fun register(bus: IEventBus) {
		for ((name, recipe) in recipes)
			AlfheimRegisters.RECIPE_SERIALIZERS.register(id(name)) { LegacyRecipeSerializer(recipe) }
		// ингредиент «предмет с metadata» 1.7.10 — как ингредиенты Forge, при регистрации сериализаторов рецептов
		bus.addListener(EventPriority.NORMAL, false, RegisterEvent::class.java) { event ->
			if (event.registryKey == ForgeRegistries.Keys.RECIPE_SERIALIZERS) CraftingHelper.register(MetaIngredient.ID, MetaIngredient.Serializer)
		}
	}
}

/**
 * Рецепт верстака 1.20.1 поверх особого рецепта автора: совпадение и результат считает класс автора. Как
 * `CustomRecipe` ванилы, в книге рецептов его нет: в 1.7.10 особые рецепты тоже показывал только NEI
 */
class LegacyCraftingRecipe(id: ResourceLocation, category: CraftingBookCategory, val recipe: IRecipe, private val serializer: LegacyRecipeSerializer): CustomRecipe(id, category) {

	override fun matches(inv: CraftingContainer, world: Level) = recipe.matches(inv, world)

	override fun assemble(inv: CraftingContainer, access: RegistryAccess): ItemStack = recipe.getCraftingResult(inv) ?: ItemStack.EMPTY

	/** `getRecipeSize` 1.7.10 — сколько клеток нужно рецепту */
	override fun canCraftInDimensions(width: Int, height: Int) = width * height >= recipe.recipeSize

	override fun getSerializer() = serializer
}

/** Сериализатор особого рецепта: в JSON только книга рецептов (`category`), как у `SimpleCraftingRecipeSerializer` */
class LegacyRecipeSerializer(private val recipe: () -> IRecipe): RecipeSerializer<LegacyCraftingRecipe> {

	override fun fromJson(id: ResourceLocation, json: JsonObject) =
		LegacyCraftingRecipe(id, CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(json, "category", null), CraftingBookCategory.MISC), recipe(), this)

	override fun fromNetwork(id: ResourceLocation, buf: FriendlyByteBuf) = LegacyCraftingRecipe(id, buf.readEnum(CraftingBookCategory::class.java), recipe(), this)

	override fun toNetwork(buf: FriendlyByteBuf, value: LegacyCraftingRecipe) {
		buf.writeEnum(value.category())
	}
}
