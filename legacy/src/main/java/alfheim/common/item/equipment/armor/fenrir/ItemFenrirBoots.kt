package alfheim.common.item.equipment.armor.fenrir

import alfheim.api.item.IStepupItem
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

class ItemFenrirBoots: ItemFenrirArmor(3, "FenrirBoots"), IStepupItem {
	
	override fun shouldHaveStepup(wearer: EntityLivingBase, stack: ItemStack) =
		wearer is EntityPlayer && hasSet(wearer)
}
