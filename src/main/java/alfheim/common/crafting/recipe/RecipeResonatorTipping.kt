package alfheim.common.crafting.recipe

import alfheim.common.item.equipment.tool.ItemResonator
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.World
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.item.equipment.tool.terrasteel.ItemTerraPick

object RecipeResonatorTipping: IRecipe {
	
	override fun matches(inv: InventoryCrafting, world: World?): Boolean {
		var foundResonator = false
		var foundElementiumPick = false
		
		repeat(inv.sizeInventory) {
			val stack = inv.getStackInSlot(it) ?: return@repeat
			
			if (stack.item is ItemResonator && !ItemTerraPick.isTipped(stack) && !foundResonator)
				foundResonator = true
			else if (stack.item === ModItems.elementiumPick && !foundElementiumPick)
				foundElementiumPick = true
			else
				return false // Found an invalid item, breaking the recipe
		}
		
		return foundResonator && foundElementiumPick
	}
	
	override fun getCraftingResult(inv: InventoryCrafting): ItemStack? {
		var resonator: ItemStack? = null
		
		repeat(inv.sizeInventory) {
			val stack = inv.getStackInSlot(it) ?: return@repeat
			
			if (stack.item is ItemResonator)
				resonator = stack
		}
		
		val resonatorCopy = resonator?.copy() ?: return null
		ItemTerraPick.setTipped(resonatorCopy)
		
		return resonatorCopy
	}
	
	override fun getRecipeSize() = 10
	
	override fun getRecipeOutput() = null
}