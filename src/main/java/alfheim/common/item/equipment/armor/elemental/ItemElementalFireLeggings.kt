package alfheim.common.item.equipment.armor.elemental

import alfheim.api.item.ISpeedUpItem
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.StatCollector
import net.minecraft.world.World
import vazkii.botania.api.mana.ManaItemHandler

class ItemElementalFireLeggings: ElementalArmor(2, "ElementalFireLeggings"), ISpeedUpItem {
	
	override fun getPixieChance(stack: ItemStack): Float {
		return 0.15f
	}
	
	override fun onArmorTick(world: World, player: EntityPlayer, stack: ItemStack) {
		if (player.isBurning && ManaItemHandler.requestManaExact(stack, player, 10, !world.isRemote)) player.extinguish()
	}
	
	@SideOnly(Side.CLIENT)
	override fun addInformation(stack: ItemStack?, player: EntityPlayer?, list: MutableList<Any?>, b: Boolean) {
		list.add(StatCollector.translateToLocal("item.ElementalArmor.desc2"))
		super.addInformation(stack, player, list, b)
	}
	
	override fun getSpeedUp(wearer: EntityLivingBase, stack: ItemStack) =
		if (wearer is EntityPlayer && ManaItemHandler.requestManaExact(stack, wearer, 1, true)) 0.075f else 0f
}
