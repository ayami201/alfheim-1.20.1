package alfheim.common.item.interaction.thaumcraft

import alfheim.api.ModInfo
import alfheim.common.integration.thaumcraft.ThaumcraftAlfheimModule
import alfheim.common.item.equipment.armor.ItemVolcanoArmor
import cpw.mods.fml.common.Optional
import cpw.mods.fml.common.Optional.InterfaceList
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import thaumcraft.api.*
import thaumcraft.api.aspects.Aspect
import thaumcraft.api.nodes.IRevealer
import vazkii.botania.common.Botania
import vazkii.botania.common.core.handler.ConfigHandler

@InterfaceList(
	Optional.Interface(modid = "Thaumcraft", iface = "thaumcraft.api.IGoggles", striprefs = true),
	Optional.Interface(modid = "Thaumcraft", iface = "thaumcraft.api.nodes.IRevealer", striprefs = true),
	Optional.Interface(modid = "Thaumcraft", iface = "thaumcraft.api.IVisDiscountGear", striprefs = true)
)
class ItemVolcanoHelmetRevealing: ItemVolcanoArmor(0, "VolcanoHelmetRevealing"), IGoggles, IRevealer, IVisDiscountGear {
	
	init {
		creativeTab = ThaumcraftAlfheimModule.tcnTab
	}
	
	@Optional.Method(modid = "Thaumcraft")
	override fun showNodes(itemstack: ItemStack?, player: EntityLivingBase?) = true
	
	@Optional.Method(modid = "Thaumcraft")
	override fun showIngamePopups(itemstack: ItemStack?, player: EntityLivingBase?) = true
	
	override fun getArmorTextureAfterInk(stack: ItemStack?, slot: Int) =
		"${ModInfo.MODID}:textures/model/armor/volcano${if (ConfigHandler.enableArmorModels) "New" else "2"}.png"
	
	override fun getVisDiscount(stack: ItemStack?, player: EntityPlayer?, aspect: Aspect?) = 5
	
	override fun addInformation(stack: ItemStack, player: EntityPlayer, list: MutableList<Any?>, adv: Boolean) {
		super.addInformation(stack, player, list, adv)
		if (!Botania.thaumcraftLoaded) return
		
		list.add(EnumChatFormatting.DARK_PURPLE.toString() + StatCollector.translateToLocal("tc.visdiscount") + ": " + getVisDiscount(stack, player, null) + "%")
	}
}