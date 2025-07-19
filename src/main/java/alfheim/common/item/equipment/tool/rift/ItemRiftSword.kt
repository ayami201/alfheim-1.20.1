package alfheim.common.item.equipment.tool.rift

import alexsocol.asjlib.*
import alfheim.api.*
import alfheim.common.core.util.AlfheimTab
import alfheim.common.entity.EntityRift
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.*

class ItemRiftSword: ItemSword(AlfheimAPI.riftToolMaterial) {
	
	init {
		creativeTab = AlfheimTab
		setTextureName("${ModInfo.MODID}:RiftSword")
		setUnlocalizedName("RiftSword")
	}
	
	override fun setUnlocalizedName(name: String): Item {
		GameRegistry.registerItem(this, name)
		return super.setUnlocalizedName(name)
	}
	
	override fun hitEntity(stack: ItemStack?, target: EntityLivingBase, hitter: EntityLivingBase?): Boolean {
		EntityRift(target.worldObj).apply { setPosition(target, oY = target.eyeHeight.D) }.spawn()
		
		return true
	}
}
