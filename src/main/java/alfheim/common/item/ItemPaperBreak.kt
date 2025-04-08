package alfheim.common.item

import alexsocol.asjlib.*
import alfheim.client.core.helper.IconHelper
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.handler.CardinalSystem.PartySystem
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.world.World
import vazkii.botania.common.core.helper.ItemNBTHelper.getCompound

class ItemPaperBreak: ItemMod("PaperBreak") {
	
	override fun getIconIndex(stack: ItemStack) = textures[if (stack.hasDisplayName()) 1 else 0]
	
	override fun getIcon(stack: ItemStack, pass: Int) = getIconIndex(stack)
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		textures = Array(2) { IconHelper.forItem(reg, this, it) }
	}
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		if (world.isRemote) return stack
		
		if (!AlfheimConfigHandler.enableMMO) {
			ASJUtilities.say(player, "alfheimmisc.mmoDisabled")
			return stack
		}
		
		val name = getCompound(stack, "display", false).getString("Name")
		val pt = PartySystem.getParty(player)
		val pl = pt.pl
		val flag1 = name != null && name.isNotEmpty()
		val flag2 = flag1 && name!!.equals(player.commandSenderName, ignoreCase = true)
		
		if (player != pl && !flag2) {
			ASJUtilities.say(player, "alfheimmisc.party.notpl")
			return stack
		}
		
		if (flag1) {
			if (pt.remove(name))
				--stack.stackSize
			else
				ASJUtilities.say(player, "alfheimmisc.party.notinpartyoffline", name)
			return stack
		}
		
		return stack
	}
	
	override fun addInformation(stack: ItemStack?, player: EntityPlayer?, list: MutableList<Any?>, adv: Boolean) {
		if (!AlfheimConfigHandler.enableMMO)
			addStringToTooltip(list, "alfheimmisc.mmoDisabled")
	}
	
	companion object {
		
		lateinit var textures: Array<IIcon>
	}
}
