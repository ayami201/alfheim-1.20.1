package alfheim.common.item

import alexsocol.asjlib.*
import alfheim.common.floatingisland.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class ItemFloatingIslandGenerator: ItemMod("FloatingIslandGenerator") {
	
	init {
		setMaxStackSize(1)
	}
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		if (!player.capabilities.isCreativeMode || world.isRemote) return stack
		
		if (player.isSneaking) run {
			val mop = ASJUtilities.getMouseOver(player, 64.0, true) ?: return stack
			if (mop.entityHit !is EntityFloatingIsland) return stack
			mop.entityHit.setDead()
			return stack
		}
		
		EntityFloatingIsland(world).apply {
			setPosition(player, oY = 5.0)
			spawn()
			FloatingIslandGenerator.generate(this)
		}
		
		return stack
	}
	
	override fun addInformation(stack: ItemStack, player: EntityPlayer?, tooltip: MutableList<Any?>, advanced: Boolean) {
		addStringToTooltip(tooltip, "${getUnlocalizedNameInefficiently(stack)}.usage")
		addStringToTooltip(tooltip, "alfheimmisc.creative")
	}
}
