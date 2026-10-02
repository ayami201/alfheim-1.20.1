package alfheim.common.crafting.recipe.workbench

import alexsocol.asjlib.*
import alexsocol.asjlib.get
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.*
import net.minecraft.item.crafting.ShapelessRecipes
import net.minecraft.world.World
import thaumcraft.common.config.ConfigItems

class RecipeHelmRevealingAlfheim(val output: Item?, val input: Item): ShapelessRecipes(ItemStack(output), listOf(ItemStack(input), ItemStack(ConfigItems.itemGoggles))) {
	
	override fun matches(inv: InventoryCrafting, world: World?): Boolean {
		var foundGoggles = false
		var foundHelm = false
		
		repeat(inv.sizeInventory) {
			val stack = inv[it] ?: return@repeat
			when (stack.item) {
				input                   -> foundHelm = true
				ConfigItems.itemGoggles -> foundGoggles = true
				else                    -> return false // Found an invalid item, breaking the recipe
			}
		}
		
		return foundGoggles && foundHelm
	}
	
	override fun getCraftingResult(inv: InventoryCrafting): ItemStack? {
		var helm: ItemStack? = null
		
		repeat(inv.sizeInventory) {
			val stack = inv[it]
			if (stack?.item === input)
				helm = stack
		}
		
		val helmCopy = helm?.copy() ?: return null
		val result = ItemStack(output)
		
		// Copy Ancient Wills
		repeat(7) {
			if (ItemNBTHelper.getBoolean(helmCopy, "AncientWill$it", false))
				ItemNBTHelper.setBoolean(result, "AncientWill$it", true)
		}
		
		// Copy Enchantments
		ItemNBTHelper.getList(helmCopy, "ench", 10, true)?.let { ItemNBTHelper.setList(result, "ench", it) }
		
		// Copy Runic Hardening
		ItemNBTHelper.setByte(result, "RS.HARDEN", ItemNBTHelper.getByte(helmCopy, "RS.HARDEN", 0.toByte()))
		
		return result
	}
	
	override fun getRecipeSize() = 10
	
	override fun getRecipeOutput() = ItemStack(output)
}
