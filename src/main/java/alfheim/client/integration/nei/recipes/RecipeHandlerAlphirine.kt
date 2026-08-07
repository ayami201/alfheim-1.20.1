//package alfheim.client.integration.nei.recipes
//
//import ab.api.AdvancedBotanyAPI
//import ab.api.recipe.RecipeAncientAlphirine
//import ab.client.core.ClientHelper
//import codechicken.lib.gui.GuiDraw
//import codechicken.nei.*
//import codechicken.nei.recipe.TemplateRecipeHandler
//import net.minecraft.item.ItemStack
//import net.minecraft.util.StatCollector
//import org.lwjgl.opengl.GL11
//import vazkii.botania.common.item.block.ItemBlockSpecialFlower
//import java.awt.Rectangle
//
//// Can't be an object!
//class RecipeHandlerAlphirine: TemplateRecipeHandler() {
//	
//	val recipeID = "alfheim.alphirine"
//	
//	inner class CachedHandlerAlphirine(recipe: RecipeAncientAlphirine?): CachedRecipe() {
//		
//		var inputs = ArrayList<PositionedStack>()
//		var output: PositionedStack? = null
//		var chance = 0
//		
//		init {
//			if (recipe != null) {
//				inputs.add(PositionedStack(ItemBlockSpecialFlower.ofType("ancientAlphirine"), 71, 23))
//				inputs.add(PositionedStack(recipe.input, 42, 23))
//				output = PositionedStack(recipe.output, 101, 23)
//				chance = recipe.chance
//			}
//		}
//		
//		override fun getIngredients(): List<PositionedStack> {
//			return getCycledIngredients(cycleticks / 20, inputs)
//		}
//		
//		override fun getResult(): PositionedStack? {
//			return output
//		}
//		
//		override fun contains(ingredients: Collection<PositionedStack>, ingredient: ItemStack?): Boolean {
//			if (ingredients === inputs)
//				for (stack in ingredients) {
//					if (stack.contains(ingredient))
//						return true
//			}
//			
//			return super.contains(ingredients, ingredient)
//		}
//	}
//	
//	override fun getRecipeName() = StatCollector.translateToLocal("alfheim.nei.alphirine")!!
//	
//	override fun getGuiTexture() = "botania:textures/gui/neiBlank.png"
//	
//	override fun recipiesPerPage() = 2
//	
//	override fun loadTransferRects() {
//		transferRects.add(RecipeTransferRect(Rectangle(70, 22, 18, 18), recipeID))
//	}
//	
//	override fun drawBackground(recipe: Int) {
//		super.drawBackground(recipe)
//		GL11.glEnable(3042)
//		GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.5f)
//		GuiDraw.changeTexture("botania:textures/gui/pureDaisyOverlay.png")
//		GuiDraw.drawTexturedModalRect(45, 10, 0, 0, 65, 44)
//		val chance = (arecipes[recipe] as CachedHandlerAlphirine).chance
//		ClientHelper.drawChanceBar(52, 58, chance)
//	}
//	
//	override fun loadCraftingRecipes(outputId: String, vararg results: Any?) {
//		if (outputId == recipeID) {
//			for (recipe in AdvancedBotanyAPI.alphirineRecipes) {
//				if (recipe == null) continue
//				arecipes.add(CachedHandlerAlphirine(recipe))
//			}
//		} else {
//			super.loadCraftingRecipes(outputId, *results)
//		}
//	}
//	
//	override fun loadCraftingRecipes(result: ItemStack?) {
//		for (recipe in AdvancedBotanyAPI.alphirineRecipes) {
//			if (recipe == null) continue
//			
//			if (NEIServerUtils.areStacksSameTypeCrafting(recipe.output, result))
//				arecipes.add(CachedHandlerAlphirine(recipe))
//		}
//	}
//	
//	override fun loadUsageRecipes(ingredient: ItemStack?) {
//		for (recipe in AdvancedBotanyAPI.alphirineRecipes) {
//			if (recipe == null) continue
//			
//			val crecipe = CachedHandlerAlphirine(recipe)
//			if (crecipe.contains(crecipe.ingredients, ingredient) || crecipe.contains(crecipe.otherStacks, ingredient))
//				arecipes.add(crecipe)
//		}
//	}
//}
