package alfheim.common.item.equipment.bauble

import alexsocol.asjlib.*
import alfheim.AlfheimCore
import alfheim.api.item.equipment.bauble.IManaDiscountBauble
import alfheim.client.core.helper.IconHelper
import alfheim.common.core.helper.ContributorsPrivacyHelper
import alfheim.common.integration.travellersgear.ITravellersGearSynced
import cpw.mods.fml.common.Optional
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import vazkii.botania.client.core.proxy.ClientProxy

@Optional.Interface(modid = "TravellersGear", iface = "alfheim.common.integration.travellersgear.ITravellersGearSynced", striprefs = true)
class ItemManaweaveGlove: ItemBaubleGlove("ManaweaveGlove"), IManaDiscountBauble {
	
	lateinit var iconChristmas: IIcon
	lateinit var iconKAIIIAK: IIcon
	
	override fun addInformation(stack: ItemStack, player: EntityPlayer, list: MutableList<Any?>, adv: Boolean) {
		addStringToTooltip(list, "${super.getUnlocalizedName(stack)}.desc")
	}
	
	override fun getDiscount(stack: ItemStack, slot: Int, player: EntityPlayer) = 0.075f * if (AlfheimCore.TravellersGearLoaded) 2 else 1
	
	@SideOnly(Side.CLIENT)
	override fun getUnlocalizedName(stack: ItemStack?): String? {
		var name = super.getUnlocalizedName(stack)
		
		if (ClientProxy.jingleTheBells)
			name = name.replace("Manaweave", "Santaweave")
		
		return name
	}
	
	@SideOnly(Side.CLIENT)
	override fun getIconFromDamage(dmg: Int) = (if (ClientProxy.jingleTheBells) iconChristmas else super.getIconFromDamage(dmg))!!
	
	@SideOnly(Side.CLIENT)
	override fun getIconIndex(stack: ItemStack) = (if (catHands(stack)) iconKAIIIAK else super.getIconIndex(stack))!!
	
	@SideOnly(Side.CLIENT)
	override fun getIcon(stack: ItemStack, pass: Int) = super.getIconIndex(stack)!!
	
	@SideOnly(Side.CLIENT)
	private fun catHands(stack: ItemStack): Boolean {
		return if (ContributorsPrivacyHelper.isCorrect(mc.thePlayer, "KAIIIAK"))
			ItemNBTHelper.getBoolean(stack, ITravellersGearSynced.TAG_EQUIPPED, false)
		else false
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		super.registerIcons(reg)
		iconChristmas = IconHelper.forItem(reg, this, "Holiday")
		iconKAIIIAK = IconHelper.forItem(reg, this, "KAIIIAK")
	}
}