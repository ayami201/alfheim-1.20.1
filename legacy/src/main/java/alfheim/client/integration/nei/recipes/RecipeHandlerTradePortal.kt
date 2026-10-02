package alfheim.client.integration.nei.recipes

import alexsocol.asjlib.mc
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.AlfheimAPI
import alfheim.api.lib.LibResourceLocations
import alfheim.common.block.BlockTradePortal
import codechicken.nei.*
import codechicken.nei.recipe.TemplateRecipeHandler
import net.minecraft.client.renderer.entity.RenderItem
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.item.ItemStack
import net.minecraft.util.StatCollector
import net.minecraftforge.oredict.OreDictionary
import org.lwjgl.opengl.GL11.*
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.recipe.RecipeElvenTrade
import vazkii.botania.client.lib.LibResources
import java.awt.Rectangle

// Can't be an object!
class RecipeHandlerTradePortal: TemplateRecipeHandler() {
	
	inner class CachedTradePortalRecipe(recipe: RecipeElvenTrade?): CachedRecipe() {
		
		val outputs: MutableList<PositionedStack> = ArrayList()
		var input: PositionedStack? = null
		
		init {
			if (recipe != null) {
				setIngredients(recipe.inputs)
				input = PositionedStack(recipe.output, 107, 46)
			}
		}
		
		fun setIngredients(inputs: List<Any>) {
			for ((i, o) in inputs.withIndex()) {
				if (o is String)
					outputs.add(PositionedStack(OreDictionary.getOres(o), 60 + i * 18, 6))
				else
					outputs.add(PositionedStack(o, 60 + i * 18, 6))
			}
		}
		
		override fun getIngredients(): List<PositionedStack> = getCycledIngredients(cycleticks / 20, outputs)
		
		override fun getResult() = input
	}
	
	override fun getRecipeName() = StatCollector.translateToLocal("alfheim.nei.tradeportal")!!
	
	override fun getGuiTexture() = LibResources.GUI_NEI_BLANK
	
	override fun loadTransferRects() {
		transferRects.add(RecipeTransferRect(Rectangle(35, 30, 48, 48), RECIPE_ID))
	}
	
	override fun recipiesPerPage() = 1
	
	override fun drawBackground(recipe: Int) {
		super.drawBackground(recipe)
		glEnable(GL_BLEND)
		glColor4f(1f, 1f, 1f, 0.7f)
		mc.renderEngine.bindTexture(LibResourceLocations.tradePortalOverlay)
		ASJRenderHelper.drawTexturedModalRect(30, 10, 0, 17, 17, 100, 80)
		glDisable(GL_BLEND)
		mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture)
		RenderItem.getInstance().renderIcon(35, 29, BlockTradePortal.textures[1], 48, 48)
	}
	
	override fun loadCraftingRecipes(outputId: String, vararg results: Any) {
		if (outputId == RECIPE_ID) {
			for (recipe in BotaniaAPI.elvenTradeRecipes) {
				if (recipe == null || !AlfheimAPI.isRetradeable(recipe.output))
					continue
				
				arecipes.add(CachedTradePortalRecipe(recipe))
			}
		} else
			super.loadCraftingRecipes(outputId, *results)
	}
	
	override fun loadCraftingRecipes(result: ItemStack?) {
		for (recipe in BotaniaAPI.elvenTradeRecipes) {
			if (recipe == null || !AlfheimAPI.isRetradeable(recipe.output))
				continue
			
			val crecipe = CachedTradePortalRecipe(recipe)
			if (crecipe.contains(crecipe.outputs, result))
				arecipes.add(crecipe)
		}
	}
	
	override fun loadUsageRecipes(ingredient: ItemStack?) {
		for (recipe in BotaniaAPI.elvenTradeRecipes) {
			if (recipe == null || !AlfheimAPI.isRetradeable(recipe.output))
				continue
			
			if (NEIServerUtils.areStacksSameTypeCrafting(recipe.output, ingredient))
				arecipes.add(CachedTradePortalRecipe(recipe))
		}
	}
	
	companion object {
		const val RECIPE_ID = "alfheim.tradeportal"
	}
}