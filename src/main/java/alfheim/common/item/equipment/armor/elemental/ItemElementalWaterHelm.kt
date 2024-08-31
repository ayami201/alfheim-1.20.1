package alfheim.common.item.equipment.armor.elemental

import alexsocol.asjlib.*
import alfheim.api.item.equipment.IElementalItem
import alfheim.common.core.helper.ElementalDamage
import cpw.mods.fml.relauncher.*
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.potion.Potion
import net.minecraft.util.StatCollector
import net.minecraft.world.World
import vazkii.botania.api.mana.*

open class ItemElementalWaterHelm: ElementalArmor, IManaDiscountArmor, IElementalItem {
	
	constructor(): super(0, "ElementalWaterHelm")
	constructor(name: String): super(0, name)
	
	override fun getPixieChance(stack: ItemStack): Float {
		return 0.11f
	}
	
	override fun getDiscount(stack: ItemStack, slot: Int, player: EntityPlayer): Float {
		return if (hasArmorSet(player)) 0.1f else 0f
	}
	
	override fun onArmorTick(world: World, player: EntityPlayer, stack: ItemStack) {
		super.onArmorTick(world, player, stack)
		
		if (player.inventory.armorInventory[3] !== stack) return
		
		if (world.getBlock(player, y = 1).material == Material.water && ManaItemHandler.requestManaExact(stack, player, 1, !world.isRemote)) {
			player.addPotionEffect(PotionEffectU(Potion.waterBreathing.id, 20, -1))
			player.addPotionEffect(PotionEffectU(Potion.nightVision.id, 20, -1))
		}
	}
	
	override fun getElement(stack: ItemStack) = ElementalDamage.WATER
	
	override fun getElementLevel(stack: ItemStack) = 4
	
	@SideOnly(Side.CLIENT)
	override fun addInformation(stack: ItemStack?, player: EntityPlayer?, list: MutableList<Any?>, adv: Boolean) {
		list.add(StatCollector.translateToLocal("item.ElementalArmor.desc4"))
		super.addInformation(stack, player, list, adv)
	}
}
