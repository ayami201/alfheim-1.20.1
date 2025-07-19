package alfheim.common.crafting.recipe

import alexsocol.asjlib.*
import alfheim.common.block.AlfheimFluffBlocks
import alfheim.common.block.tile.*
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.*
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.init.Blocks
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World

object RecipeStencil: IRecipe {
	
	override fun matches(inv: InventoryCrafting, world: World?): Boolean {
		var foundComposite = false
		var foundStencil = false
		
		repeat(inv.sizeInventory) {
			val stack = inv[it] ?: return@repeat
			
			if (stack.item === AlfheimItems.elvenResource && stack.meta == ElvenResourcesMetas.Stencil.I && ItemNBTHelper.getNBT(stack).hasKey(ItemElvenResource.TAG_STENCIL) && !foundStencil) {
				foundStencil = true
				return@repeat
			}
			
			val block = stack.block
			if (block !== Blocks.air && !block.hasTileEntity(stack.meta) && !foundComposite)
				foundComposite = true
		}
		
		return foundComposite && foundStencil
	}
	
	override fun getCraftingResult(inv: InventoryCrafting): ItemStack? {
		var composite: ItemStack? = null
		var compositeSlot: Int? = null
		var stencil: ItemStack? = null
		var stencilSlot: Int? = null
		
		fun checkStencil(stack: ItemStack?) = stack?.item === AlfheimItems.elvenResource && stack.meta == ElvenResourcesMetas.Stencil.I
		
		for (it in 0 until inv.sizeInventory) {
			val stack = inv[it] ?: continue
			
			if (stencil == null && checkStencil(stack)) {
				stencil = stack
				stencilSlot = it
				continue
			}
			
			val block = stack.block
			if (block === Blocks.air || block.hasTileEntity(stack.meta) || composite != null) continue
			
			composite = stack
			compositeSlot = it
		}
		
		if (composite == null || stencil == null || compositeSlot == null) return null
		
		val where = if (stencilSlot == compositeSlot + 1) 1 else if (stencilSlot == compositeSlot - 1) -1 else 0
		
		val result = ItemStack(AlfheimFluffBlocks.composite)
		val nbt = ItemNBTHelper.getCompound(stencil, ItemElvenResource.TAG_STENCIL, true)?.copy() as? NBTTagCompound ?: return null
		
		if (where != 0) {
			val tile = TileComposite()
			tile.readCustomNBT(nbt)
			
			val a = tile.composition
			repeat(a.size) { x ->
				val b = a[x]
				repeat(b.size) { y ->
					val c = b[y]
					repeat(c.size) z@ { z ->
						val (block, meta) = c[z] ?: return@z
						
						if (where == -1 && block !== tile.blockBottom || meta != tile.blockBottomMeta) return@z
						
						c[z] = composite.block to composite.meta
					}
				}
			}
			
			nbt.tagMap.clear()
			tile.writeCustomNBT(nbt)
		}
		
		result.tagCompound = nbt
		
		ItemNBTHelper.setString(result, TileDoubleCamo.TAG_BLOCK_BOTTOM, GameRegistry.findUniqueIdentifierFor(composite.block).toString())
		ItemNBTHelper.setInt(result, TileDoubleCamo.TAG_BLOCK_BOTTOM_META, composite.meta)
		
		return result
	}
	
	override fun getRecipeOutput() = ItemStack(AlfheimFluffBlocks.composite)
	
	override fun getRecipeSize() = 10
}