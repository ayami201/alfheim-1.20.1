package alfheim.common.item

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.item.ColorOverrideHelper
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.VisualEffectHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import vazkii.botania.api.mana.ManaItemHandler
import java.awt.Color

class ItemChalk: ItemMod("Chalk") {
	
	init {
		maxStackSize = 1
	}
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		player.setItemInUse(stack, getMaxItemUseDuration(stack))
		return stack
	}
	
	override fun getMaxItemUseDuration(stack: ItemStack?) = 72000
	
	override fun onUsingTick(stack: ItemStack, player: EntityPlayer, count: Int) {
		if (ASJUtilities.isClient) return
		
		if (!ManaItemHandler.requestManaExact(stack, player, 1, true)) return
		
		val distance = 3.0
		val mop = ASJUtilities.getMouseOver(player, distance.D, true)
		
		val hit = if (mop?.hitVec == null)
			Vector3(player.lookVec).normalize().mul(distance.D).add(player.posX, player.posY + player.eyeHeight, player.posZ)
		else
			Vector3(mop.hitVec)
		
		val (x, y, z) = hit
		val (r, g, b) = Color(ColorOverrideHelper.getColor(player, 0xFFD400)).getRGBColorComponents(null)
		
		VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.CHALK, player.worldObj.provider.dimensionId, x, y, z, r.D, g.D, b.D)
	}
}
