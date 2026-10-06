package alfheim.common.crafting.recipe.workbench

// PORT: импорты 1.20.1 (MAPPING.md, «Рецепты»): IRecipe и сетка верстака 1.7.10 — прослойка alfheim.port.legacy;
// предметы Botania 1.20.1 — BotaniaItems
import alexsocol.asjlib.*
import alexsocol.asjlib.get
import alfheim.common.block.AlfheimBlocks
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenResourcesMetas
import alfheim.port.legacy.*
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level as World
import vazkii.botania.common.item.BotaniaItems as ModItems

object RecipeElvenWeed: IRecipe {
	
	// PORT: Thaumcraft выпал (SPEC, п. 7): мана-гриба Thaumcraft нет — рецепт тот же, что у автора без Thaumcraft
	// (manashroom = null)
//	val manashroom = if (Botania.thaumcraftLoaded) GameRegistry.findItem("Thaumcraft", "blockCustomPlant") else null
	
	override fun matches(crafting: InventoryCrafting, world: World?): Boolean {
		val founds = Array(4) { false }
		
		for (i in 0 until crafting.sizeInventory) {
			val stack = crafting[i] ?: continue
			
			when {
				// PORT: предмет с вариантами — массив предметов; ModItems.manaResource 8 (пыльца фей) → pixieDust
				stack.item in AlfheimItems.elvenResource && stack.meta == ElvenResourcesMetas.IffesalDust.I -> {
//				stack.item === AlfheimItems.elvenResource && stack.meta == ElvenResourcesMetas.IffesalDust.I -> {
					if (founds[0]) return false
					founds[0] = true
				}
				stack.item === ModItems.pixieDust                                                            -> {
//				stack.item === ModItems.manaResource && stack.meta == 8                                      -> {
					if (founds[1]) return false
					founds[1] = true
				}
//				stack.item === manashroom && stack.meta == 5                                                 -> {
//					if (founds[2]) return false
//					founds[2] = true
//				}
				stack.item === AlfheimBlocks.rainbowMushroom.toItem()                                        -> {
					if (founds[2]) return false
					founds[2] = true
				}
				// PORT: поля ванилы 1.20.1 — заглавными
				stack.item === Items.PAPER                                                                   -> {
//				stack.item === Items.paper                                                                   -> {
					if (founds[3]) return false
					founds[3] = true
				}
				else                                                                                         -> return false
			}
		}
		
		return founds.all { it }
	}
	
	override fun getCraftingResult(crafting: InventoryCrafting) = recipeOutput
	
	override fun getRecipeOutput() = ElvenResourcesMetas.ElvenWeed.stack
	
	override fun getRecipeSize() = 4
}
