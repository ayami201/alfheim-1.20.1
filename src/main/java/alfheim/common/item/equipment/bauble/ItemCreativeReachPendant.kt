package alfheim.common.item.equipment.bauble

import alexsocol.asjlib.ASJUtilities
import alexsocol.patcher.handler.PlayerReachDistanceHandler
import com.google.common.collect.HashMultimap
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderPlayerEvent
import vazkii.botania.api.item.IBaubleRender
import vazkii.botania.client.core.helper.IconHelper
import java.util.UUID

class ItemCreativeReachPendant: ItemPendant("CreativeReachPendant") {
	
	override fun onItemRightClick(stack: ItemStack?, world: World?, player: EntityPlayer): ItemStack? {
		if (ASJUtilities.isServer)
			ASJUtilities.say(player, "item.CreativeReachPendant.warn")
		
		return stack
	}
	
	override fun onEquippedOrLoadedIntoWorld(stack: ItemStack?, player: EntityLivingBase?) {
		if (player is EntityPlayerMP)
			player.getAttributeMap().applyAttributeModifiers(mod)
	}
	
	override fun onUnequipped(stack: ItemStack?, player: EntityLivingBase?) {
		if (player is EntityPlayerMP)
			player.getAttributeMap().removeAttributeModifiers(mod)
	}
	
	override fun getUnlocalizedNameInefficiently(stack: ItemStack): String {
		return getUnlocalizedName(stack)
	}
	
	override fun onPlayerBaubleRender(stack: ItemStack, event: RenderPlayerEvent, type: IBaubleRender.RenderType) = Unit
	
	override fun registerIcons(reg: IIconRegister) {
		itemIcon = IconHelper.forItem(reg, this)
	}
	
	companion object {
		private val mod = HashMultimap.create<String, AttributeModifier>().apply {
			put(PlayerReachDistanceHandler.reachDistance.attributeUnlocalizedName, AttributeModifier(UUID.fromString("a4e0e453-8efd-4177-8636-f8913eaaf213"), "Alfheim ItemCreativeReachPendant", 100.0, 0))
		}
	}
}
