package alfheim.common.crafting.recipe

import net.minecraft.item.ItemStack
import vazkii.botania.api.recipe.RecipeRuneAltar

/**
 * Full recipe will consume every item (even runes)
 */
class RecipeRuneAltarFull(output: ItemStack, mana: Int, vararg inputs: Any): RecipeRuneAltar(output, mana, *inputs)