package alfheim.common.item.interaction.thaumcraft

import alfheim.api.ModInfo
import alfheim.common.integration.thaumcraft.ThaumcraftAlfheimModule
import alfheim.common.item.equipment.armor.elvoruim.ItemElvoriumHelmet
import cpw.mods.fml.common.Optional
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import thaumcraft.api.*
import thaumcraft.api.aspects.Aspect
import thaumcraft.api.nodes.IRevealer
import vazkii.botania.common.core.handler.ConfigHandler

@Optional.InterfaceList(
	Optional.Interface(modid = "Thaumcraft", iface = "thaumcraft.api.IGoggles", striprefs = true),
	Optional.Interface(modid = "Thaumcraft", iface = "thaumcraft.api.nodes.IRevealer", striprefs = true),
	Optional.Interface(modid = "Thaumcraft", iface = "thaumcraft.api.IVisDiscountGear", striprefs = true)
)
class ItemElvoriumHelmetRevealing: ItemElvoriumHelmet("ElvoriumHelmetRevealing"), IGoggles, IRevealer, IVisDiscountGear {
	
	init {
		creativeTab = ThaumcraftAlfheimModule.tcnTab
	}
	
	@Optional.Method(modid = "Thaumcraft")
	override fun showNodes(itemstack: ItemStack, player: EntityLivingBase) = true
	
	@Optional.Method(modid = "Thaumcraft")
	override fun showIngamePopups(itemstack: ItemStack, player: EntityLivingBase) = true
	
	override fun getArmorTextureAfterInk(stack: ItemStack?, slot: Int) =
		"${ModInfo.MODID}:textures/model/armor/ElvoriumArmor${if (ConfigHandler.enableArmorModels) "" else "2"}.png"
	
	override fun getVisDiscount(stack: ItemStack, player: EntityPlayer, aspect: Aspect?) = 5 + super.getVisDiscount(stack, player, aspect)
}
