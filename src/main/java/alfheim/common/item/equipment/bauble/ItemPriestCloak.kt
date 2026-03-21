package alfheim.common.item.equipment.bauble

import alexsocol.asjlib.*
import alfheim.AlfheimCore
import alfheim.api.ModInfo
import alfheim.api.lib.LibResourceLocations
import alfheim.client.core.helper.IconHelper
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.AlfheimItems
import alfheim.common.item.equipment.bauble.faith.IFaithHandler
import baubles.common.lib.PlayerHandler
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.util.IIcon
import travellersgear.api.TravellersGearAPI
import vazkii.botania.api.mana.IManaUsingItem

class ItemPriestCloak: ItemBaubleCloak("priestCloak"), IManaUsingItem {
	
	companion object {
		
		lateinit var icons: Array<IIcon>
		
		fun getCloak(meta: Int, player: EntityPlayer): ItemStack? {
			val stack = if (AlfheimCore.TravellersGearLoaded) {
				TravellersGearAPI.getExtendedInventory(player)[0]
			} else {
				PlayerHandler.getPlayerBaubles(player)[3]
			}
			
			return if (stack != null && ((stack.item == AlfheimItems.priestCloak && stack.meta == meta) || (stack.item === AlfheimItems.aesirCloak))) stack else null
		}
	}
	
	init {
		creativeTab = AlfheimTab
	}
	
	override fun getUnlocalizedNameInefficiently(par1ItemStack: ItemStack) =
		super.getUnlocalizedNameInefficiently(par1ItemStack).replace("item\\.botania:".toRegex(), "item.${ModInfo.MODID}:")
	
	override fun getItemStackDisplayName(stack: ItemStack) =
		super.getItemStackDisplayName(stack).replace("&".toRegex(), "\u00a7")
	
	override fun registerIcons(reg: IIconRegister) {
		icons = Array(ItemPriestEmblem.TYPES) { IconHelper.forItem(reg, this, it) }
	}
	
	override fun getIconFromDamage(meta: Int) = icons.safeGet(meta)
	
	override fun getSubItems(item: Item, tab: CreativeTabs?, list: MutableList<Any?>) {
		for (i in 0 until ItemPriestEmblem.TYPES)
			list.add(ItemStack(item, 1, i))
	}
	
	override fun getUnlocalizedName(stack: ItemStack) =
		super.getUnlocalizedName(stack) + stack.meta
	
	override fun usesMana(stack: ItemStack) = true
	
	override fun onEquippedOrLoadedIntoWorld(stack: ItemStack, player: EntityLivingBase) {
		if (player is EntityPlayer)
			IFaithHandler.getFaithHandler(stack).onEquipped(stack, player, IFaithHandler.FaithBauble.CLOAK)
	}
	
	override fun onWornTick(stack: ItemStack, player: EntityLivingBase) {
		super.onWornTick(stack, player)
		if (player is EntityPlayer)
			IFaithHandler.getFaithHandler(stack).onWornTick(stack, player, IFaithHandler.FaithBauble.CLOAK)
	}
	
	override fun onUnequipped(stack: ItemStack, player: EntityLivingBase) {
		if (player is EntityPlayer)
			IFaithHandler.getFaithHandler(stack).onUnequipped(stack, player, IFaithHandler.FaithBauble.CLOAK)
	}
	
	fun addStringToTooltip(s: String, tooltip: MutableList<Any?>) {
		tooltip.add(s.replace("&".toRegex(), "\u00a7"))
	}
	
	override fun getCloakTexture(stack: ItemStack) = LibResourceLocations.godCloak.safeGet(stack.meta)
	
	override fun getCloakGlowTexture(stack: ItemStack) = LibResourceLocations.godCloakGlow.safeGet(stack.meta)
}
