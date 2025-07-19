package alfheim.common.integration.minetweaker

import alfheim.api.*
import alfheim.common.crafting.recipe.AlfheimRecipes.registerItemCraftTuning
import alfheim.common.crafting.recipe.TunerIncantationIO
import alfheim.common.integration.minetweaker.MinetweakerAlfheimConfig.getObjects
import alfheim.common.integration.minetweaker.MinetweakerAlfheimConfig.getStack
import alfheim.common.lexicon.page.PageTuningIORecipe
import minetweaker.*
import minetweaker.api.item.*
import net.minecraft.item.*
import stanhebben.zenscript.annotations.*
import vazkii.botania.api.BotaniaAPI

@ZenClass("mods.${ModInfo.MODID}.ManaTuner")
object MTHandlerManaTuner {
	
	@ZenMethod
	@JvmStatic
	fun addRecipe(incantation: String, result: IItemStack, core: IItemStack, inputs: Array<IIngredient?>) {
		MineTweakerAPI.apply(Add(incantation, getStack(result), getStack(core), getObjects(inputs)))
	}
	
	@ZenMethod
	@JvmStatic
	fun addRecipePage(entryName: String, pageUnlocalizedName: String, pageIndex: Int, incantation: String, result: IItemStack, core: IItemStack, recipeIndex: Int) {
		val entry = BotaniaAPI.getAllEntries().first { it.unlocalizedName == entryName }
		val recipe = AlfheimAPI.tunerIncantations[incantation].toList()[recipeIndex]
		val page = PageTuningIORecipe(pageUnlocalizedName, TunerIncantationIO(recipe, getStack(core), getStack(result)))
		entry.pages.add(pageIndex, page)
		page.onPageAdded(entry, pageIndex)
	}
	
	private class Add(val incantation: String, val result: ItemStack, val core: ItemStack, val inputs: Array<Any>): IUndoableAction {
		
		lateinit var added: TunerIncantationIO
		
		override fun apply() {
			added = registerItemCraftTuning(incantation, result, core, *inputs)
		}
		
		override fun canUndo() = true
		
		override fun undo() {
			AlfheimAPI.tunerIncantations[incantation].remove(added.tunerIncantation)
		}
		
		override fun describe() = "Adding Mana Tuner tuning $incantation"
		
		override fun describeUndo() = "Removing Mana Tuner tuning $incantation"
		
		override fun getOverrideKey() = null
	}
}