package alfheim.common.crafting.recipe

import alfheim.api.crafting.recipe.TunerIncantation
import net.minecraft.item.ItemStack

data class TunerIncantationIO(val tunerIncantation: TunerIncantation<*>, val core: ItemStack, val output: ItemStack)