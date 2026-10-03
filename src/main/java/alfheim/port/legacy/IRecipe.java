package alfheim.port.legacy;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * {@code net.minecraft.item.crafting.IRecipe} 1.7.10 (SPEC, Р-9; MAPPING.md, «Рецепты»): рецепт верстака.
 * <p>
 * Обычные рецепты автора (по шаблону, без формы, печь) в 1.20.1 — данные: их записывают {@link CraftingManager} и
 * {@link FurnaceRecipes}, JSON строит генерация данных. Особые рецепты — классы автора с этим интерфейсом; в игре их
 * зовёт рецепт верстака 1.20.1 {@code LegacyCraftingRecipe}. Пустой стек 1.20.1 здесь, как в 1.7.10, — {@code null}.
 * <p>
 * Интерфейс написан на Java, как в 1.7.10: код автора читает {@code recipe.recipeOutput} как свойство.
 */
public interface IRecipe {

	boolean matches(CraftingContainer inv, @Nullable Level world);

	@Nullable
	ItemStack getCraftingResult(CraftingContainer inv);

	int getRecipeSize();

	@Nullable
	ItemStack getRecipeOutput();
}
