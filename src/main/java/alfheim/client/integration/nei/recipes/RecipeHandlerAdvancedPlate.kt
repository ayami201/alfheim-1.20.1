//package alfheim.client.integration.nei.recipes
//
//import ab.api.AdvancedBotanyAPI
//import ab.api.recipe.RecipeAdvancedPlate
//import ab.client.core.ClientHelper
//import ab.common.lib.register.BlockListAB
//import codechicken.lib.gui.GuiDraw
//import codechicken.nei.*
//import codechicken.nei.recipe.TemplateRecipeHandler
//import net.minecraft.item.ItemStack
//import net.minecraft.util.StatCollector
//import org.lwjgl.opengl.GL11
//import java.awt.Rectangle
//import kotlin.math.*
//
//// Can't be an object!
//class RecipeHandlerAdvancedPlate: TemplateRecipeHandler() {
//	
//	val recipeID = "ab.advancedPlate"
//	
//	inner class CachedAdvancedPlateRecipe(recipe: RecipeAdvancedPlate): CachedRecipe() {
//		
//		var inputs: MutableList<PositionedStack?> = ArrayList()
//		var output: PositionedStack?
//		var manaUsage: Int
//		
//		init {
//			setIngredients(recipe.inputs)
//			output = PositionedStack(recipe.output, 111, 21)
//			inputs.add(PositionedStack(ItemStack(BlockListAB.nidavellirForge), 73, 55))
//			manaUsage = recipe.manaUsage
//		}
//		
//		fun setIngredients(inputs: MutableList<ItemStack>) {
//			val degreePerInput = 360.0f / inputs.size
//			var currentDegree = -90.0f
//			for (o in inputs) {
//				val posX = (73.0 + cos(currentDegree * Math.PI / 180.0) * 32.0).roundToInt()
//				val posY = (55.0 + sin(currentDegree * Math.PI / 180.0) * 32.0).roundToInt()
//				
//				this.inputs.add(PositionedStack(o, posX, posY))
//				currentDegree += degreePerInput
//			}
//		}
//		
//		override fun getIngredients() = getCycledIngredients(cycleticks / 20, inputs)!!
//		
//		override fun getResult() = output
//	}
//	
//	override fun getRecipeName() = StatCollector.translateToLocal("ab.nei.advancedPlate")!!
//	
//	override fun getGuiTexture() = "botania:textures/gui/neiBlank.png"
//	
//	override fun loadTransferRects() {
//		transferRects.add(RecipeTransferRect(Rectangle(72, 54, 18, 18), recipeID, *arrayOfNulls<Any>(0)))
//	}
//	
//	override fun recipiesPerPage() = 1
//	
//	override fun drawBackground(recipe: Int) {
//		super.drawBackground(recipe)
//		GL11.glEnable(3042)
//		GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.5f)
//		GuiDraw.changeTexture("botania:textures/gui/petalOverlay.png")
//		GuiDraw.drawTexturedModalRect(45, 10, 38, 7, 92, 92)
//		val mana = (arecipes[recipe] as CachedAdvancedPlateRecipe).manaUsage
//		ClientHelper.renderPoolManaBar(32, 112, 0x239ddc, 1.0f, mana)
//	}
//	
//	fun getCachedRecipe(recipe: RecipeAdvancedPlate) = CachedAdvancedPlateRecipe(recipe)
//	
//	override fun loadCraftingRecipes(outputId: String, vararg results: Any?) {
//		if (outputId == recipeID) {
//			for (recipe in AdvancedBotanyAPI.advancedPlateRecipes) {
//				arecipes.add(getCachedRecipe(recipe))
//			}
//		} else {
//			super.loadCraftingRecipes(outputId, *results)
//		}
//	}
//	
//	override fun loadCraftingRecipes(result: ItemStack?) {
//		for (recipe in AdvancedBotanyAPI.advancedPlateRecipes) {
//			if (recipe == null) continue
//			
//			if (recipe.output.stackTagCompound != null && NEIServerUtils.areStacksSameType(recipe.output, result) || recipe.output.stackTagCompound == null && NEIServerUtils.areStacksSameTypeCrafting(recipe.output, result))
//				arecipes.add(getCachedRecipe(recipe))
//		}
//	}
//	
//	override fun loadUsageRecipes(ingredient: ItemStack?) {
//		for (recipe in AdvancedBotanyAPI.advancedPlateRecipes) {
//			if (recipe == null) continue
//			
//			val crecipe = getCachedRecipe(recipe)
//			if (crecipe.contains(crecipe.ingredients, ingredient) || crecipe.contains(crecipe.otherStacks, ingredient))
//				arecipes.add(crecipe)
//		}
//	}
//}
