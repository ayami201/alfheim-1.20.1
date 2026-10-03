package alfheim.common.crafting.recipe.workbench

// PORT: импорты 1.20.1 (MAPPING.md, «Рецепты»): IRecipe и сетка верстака 1.7.10 — прослойка alfheim.port.legacy;
// склянка с варевом Botania 1.20.1 — BaseBrewItem, запасное варево — BotaniaBrews.fallbackBrew
import alexsocol.asjlib.get
import alfheim.common.item.AlfheimItems.splashPotion
import alfheim.common.item.ItemSplashPotion
import alfheim.port.legacy.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level as World
import vazkii.botania.common.brew.BotaniaBrews as ModBrews
import vazkii.botania.common.item.BotaniaItems as ModItems
import vazkii.botania.common.item.brew.BaseBrewItem as ItemBrewVial

object RecipeThrowablePotion: IRecipe {
	
	override fun matches(inv: InventoryCrafting, world: World?): Boolean {
		var foundGunpowder = false
		var foundVial = false
		
		for (i in 0 until inv.sizeInventory) {
			val stack = inv[i] ?: continue
			when {
				stack.item === Items.GUNPOWDER   -> foundGunpowder = true
				stack.item === ModItems.brewVial -> foundVial = true
				else                             -> return false // Found an invalid item, breaking the recipe
			}
		}
		
		return foundGunpowder && foundVial
	}
	
	override fun getCraftingResult(inv: InventoryCrafting): ItemStack? {
		var vial: ItemStack? = null
		
		for (i in 0 until inv.sizeInventory) {
			val stack = inv[i]
			if (stack?.item === ModItems.brewVial)
				vial = stack
		}
		
		if (vial == null) return null
		val item = vial.item as ItemBrewVial
		val brew = item.getBrew(vial)
		
		if (brew === ModBrews.fallbackBrew) return null
//		if (brew === BotaniaAPI.fallbackBrew) return null
		
		return (splashPotion as ItemSplashPotion).getItemForBrew(brew, vial)
	}
	
	override fun getRecipeOutput() = (splashPotion as ItemSplashPotion).getItemForBrew(ModBrews.absorption, null)
	
	override fun getRecipeSize() = 2
}