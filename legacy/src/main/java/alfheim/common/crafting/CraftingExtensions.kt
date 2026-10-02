package alfheim.common.crafting

import alfheim.AlfheimCore
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.*
import tconstruct.tools.inventory.CraftingStationContainer

val InventoryCrafting.crafter: EntityPlayer?
	get() {
		val container = eventHandler
		
		return if (container is ContainerWorkbench) container.alfheim_synthetic_thePlayer
		else if (AlfheimCore.TiCLoaded && container is CraftingStationContainer) container.player
		else null
	}