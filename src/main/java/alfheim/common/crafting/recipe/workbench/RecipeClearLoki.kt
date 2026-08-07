package alfheim.common.crafting.recipe.workbench

import alexsocol.asjlib.get
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.World
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.item.relic.*

object RecipeClearLoki: IRecipe {
	
	override fun matches(inv: InventoryCrafting, world: World?): Boolean {
		var foundItem = false
		
		for (i in 0 until inv.sizeInventory) {
			val stack = inv[i] ?: continue
			if (!foundItem && (stack.item.let { it is ItemLokiRing || it is ItemAesirRing }))
				foundItem = true
			else
				return false
		}
		
		return foundItem
	}
	
	override fun getCraftingResult(inv: InventoryCrafting): ItemStack? {
		var item: ItemStack? = null
		
		for (i in 0 until inv.sizeInventory) {
			val stack = inv[i] ?: continue
			if (item == null && (stack.item.let { it is ItemLokiRing || it is ItemAesirRing }))
				item = stack
			else
				return null
		}
		
		if (item == null) return null
		
		val copy = item.copy()
		
		ItemNBTHelper.setInt(copy, "xOrigin", 0)
		ItemNBTHelper.setInt(copy, "yOrigin", -1)
		ItemNBTHelper.setInt(copy, "zOrigin", 0)
		
		ItemLokiRing.setCursorList(copy, null)
		
		return copy
	}
	
	override fun getRecipeSize() = 1
	
	override fun getRecipeOutput() = null
}
