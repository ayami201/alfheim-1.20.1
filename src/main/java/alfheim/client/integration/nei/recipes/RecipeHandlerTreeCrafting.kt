package alfheim.client.integration.nei.recipes

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.*
import alfheim.api.crafting.recipe.RecipeTreeCrafting
import alfheim.api.lib.LibResourceLocations
import alfheim.common.block.AlfheimBlocks
import codechicken.nei.*
import codechicken.nei.recipe.TemplateRecipeHandler
import net.minecraft.item.ItemStack
import net.minecraft.util.StatCollector
import net.minecraftforge.oredict.OreDictionary
import org.lwjgl.opengl.GL11
import vazkii.botania.client.core.handler.HUDHandler
import vazkii.botania.client.lib.LibResources
import vazkii.botania.common.block.tile.mana.TilePool
import java.awt.Rectangle
import kotlin.math.*

// Can't be an object!
class RecipeHandlerTreeCrafting: TemplateRecipeHandler() {
	
	override fun getRecipeName(): String = StatCollector.translateToLocal("alfheim.nei.treeCrafter")
	
	override fun drawBackground(recipe: Int) {
		super.drawBackground(recipe)
		GL11.glEnable(GL11.GL_BLEND)
		GL11.glColor4f(1f, 1f, 1f, 0.5F)
		mc.renderEngine.bindTexture(LibResourceLocations.suffuserOverlay)
		ASJRenderHelper.drawTexturedModalRect(7, 2, 0, 0, 0, 142, 93)
		HUDHandler.renderManaBar(32, 113, 0x0000FF, 0.75F, (arecipes[recipe] as CachedTreeRecipe).manaUsage, TilePool.MAX_MANA / 10)
	}
	
	override fun getGuiTexture() = LibResources.GUI_NEI_BLANK
	
	override fun loadTransferRects() {
		transferRects.add(RecipeTransferRect(Rectangle(72, 54, 18, 18), RECIPE_ID))
	}
	
	override fun recipiesPerPage() = 1
	
	fun getCachedRecipe(recipe: RecipeTreeCrafting) = CachedTreeRecipe(recipe)
	
	override fun loadCraftingRecipes(outputId: String, vararg results: Any) {
		if (outputId == RECIPE_ID) {
			for (rec in AlfheimAPI.treeRecipes)
				arecipes.add(getCachedRecipe(rec))
		} else
			super.loadCraftingRecipes(outputId, *results)
	}
	
	override fun loadCraftingRecipes(result: ItemStack?) {
		for (recipe in AlfheimAPI.treeRecipes) {
			if ((recipe.output.stackTagCompound == null || !NEIServerUtils.areStacksSameType(recipe.output, result)) && (recipe.output.stackTagCompound != null || !NEIServerUtils.areStacksSameTypeCrafting(recipe.output, result)))
				continue
			
			arecipes.add(getCachedRecipe(recipe))
		}
	}
	
	override fun loadUsageRecipes(ingredient: ItemStack?) {
		for (recipe in AlfheimAPI.treeRecipes) {
			val crecipe = getCachedRecipe(recipe)
			
			if (crecipe.contains(crecipe.inputs, ingredient))
				arecipes.add(crecipe)
		}
	}
	
	inner class CachedTreeRecipe(recipe: RecipeTreeCrafting): CachedRecipe() {
		
		var inputs: MutableList<PositionedStack> = ArrayList()
		var output: PositionedStack
		var manaUsage = 0
		
		init {
			setIngredients(recipe.inputs)
			output = PositionedStack(recipe.output, 126, 55)
			manaUsage = recipe.manaUsage
			inputs.add(PositionedStack(recipe.core, 20, 55))
			inputs.add(PositionedStack(ItemStack(AlfheimBlocks.treeCrafterBlock), 73, 55))
			inputs.add(PositionedStack(ItemStack(AlfheimBlocks.treeCrafterBlockAU), 73, 55))
			inputs.add(PositionedStack(ItemStack(AlfheimBlocks.treeCrafterBlockRB), 73, 55))
		}
		
		fun setIngredients(inputs: List<Any>) {
			val degreePerInput = 360f / inputs.size.F
			var currentDegree = -90f
			
			for (input in inputs) {
				val posX = (73.0 + cos(currentDegree.D * 3.141592653589793 / 180.0) * 32.0).roundToInt()
				val posY = (55.0 + sin(currentDegree.D * 3.141592653589793 / 180.0) * 32.0).roundToInt()
				if (input is String) {
					this.inputs.add(PositionedStack(OreDictionary.getOres(input), posX, posY))
				} else {
					this.inputs.add(PositionedStack(input, posX, posY))
				}
				currentDegree += degreePerInput
			}
		}
		
		override fun getIngredients(): List<PositionedStack> = getCycledIngredients(cycleticks / 20, inputs)
		
		override fun getResult() = output
	}
	
	companion object {
		const val RECIPE_ID = "${ModInfo.MODID}.treeCrafter"
	}
}
