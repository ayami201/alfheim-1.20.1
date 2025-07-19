package alfheim.common.item.equipment.tool.rift

import alfheim.api.*
import alfheim.common.core.util.AlfheimTab
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.item.*

class ItemRiftPick: ItemPickaxe(AlfheimAPI.riftToolMaterial) {
	
	init {
		creativeTab = AlfheimTab
		setTextureName("${ModInfo.MODID}:RiftPick")
		setUnlocalizedName("RiftPick")
	}
	
	override fun setUnlocalizedName(name: String): Item {
		GameRegistry.registerItem(this, name)
		return super.setUnlocalizedName(name)
	}
}
