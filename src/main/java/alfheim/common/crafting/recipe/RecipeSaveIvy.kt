package alfheim.common.crafting.recipe

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.common.entity.item.*
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenResourcesMetas
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.item.EntityItem
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.World
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.item.ItemTossEvent

object RecipeSaveIvy: IRecipe {
	
	const val TAG_SAVE = "${ModInfo.MODID}_saveIvy"
	
	init {
		eventForge()
	}
	
	override fun matches(inv: InventoryCrafting, world: World?): Boolean {
		var foundIvy = false
		var foundItem = false
		
		for (i in 0 until inv.sizeInventory) {
			val stack = inv.getStackInSlot(i) ?: continue
			if (!foundIvy && stack.item === AlfheimItems.elvenResource && stack.meta == ElvenResourcesMetas.SaveIvy.I)
				foundIvy = true
			else if (!foundItem && !(ItemNBTHelper.getBoolean(stack, TAG_SAVE, false)) && !stack.item.hasContainerItem(stack))
				foundItem = true
			else
				return false
		}
		
		return foundIvy && foundItem
	}
	
	override fun getCraftingResult(inv: InventoryCrafting): ItemStack? {
		var item: ItemStack? = null
		
		for (i in 0 until inv.sizeInventory) {
			val stack = inv.getStackInSlot(i) ?: continue
			if (stack.item !== AlfheimItems.elvenResource || stack.meta != ElvenResourcesMetas.SaveIvy.I)
				item = stack
		}
		
		val copy = item?.copy() ?: return null
		ItemNBTHelper.setBoolean(copy, TAG_SAVE, true)
		copy.stackSize = 1
		return copy
	}
	
	override fun getRecipeSize() = 10
	
	override fun getRecipeOutput() = null
	
	@SubscribeEvent
	fun onSaveItemSpawn(e: EntityJoinWorldEvent) {
		val entity = e.entity as? EntityItem ?: return
		
		if (!checkEntity(entity)) return
		
		e.isCanceled = true
		
		EntityItemImmortal(e.world, entity, entity.entityItem.copy()).spawn()
		
		entity.entityItem.stackSize = 0
		entity.setEntityItemStack(null)
		entity.setDead()
	}
	
	@SubscribeEvent
	fun onSaveItemToss(e: ItemTossEvent) {
		if (!checkEntity(e.entityItem)) return
		
		e.isCanceled = true
		e.player.inventory.addItemStackToInventory(e.entityItem.entityItem)
	}
	
	fun checkEntity(entity: EntityItem): Boolean {
		if (entity.isDead) return false
		return checkStack(entity.dataWatcher.getWatchableObjectItemStack(10)?.copy() ?: return false)
	}
	
	fun checkStack(stack: ItemStack?): Boolean {
		stack ?: return false
		if (stack.stackSize < 1) return false
		return ItemNBTHelper.getBoolean(stack, TAG_SAVE, false)
	}
}
