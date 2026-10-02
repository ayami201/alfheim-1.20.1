package alfheim.common.item.block

import alfheim.common.block.BlockSubspacian
import alfheim.common.item.equipment.bauble.ItemSpatiotemporalRing
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.world.World

class ItemBlockSubspacian(block: Block): ItemBlockLeavesMod(block) {

	override fun onEaten(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		--stack.stackSize
		player.foodStats.addStats(2, 0.2f)
		world.playSoundAtEntity(player, "random.burp", 0.5f, world.rand.nextFloat() * 0.1f + 0.9f)
		
		if (ItemSpatiotemporalRing.hasProtection(player) || world.isRemote) return stack
		
		player.stopUsingItem()
		
		BlockSubspacian.doBadThings(player)
		
		return stack
	}
	
	override fun getMaxItemUseDuration(stack: ItemStack?) = 32
	
	override fun getItemUseAction(stack: ItemStack?) = EnumAction.eat
	
	override fun onItemRightClick(stack: ItemStack?, world: World?, player: EntityPlayer): ItemStack? {
		player.setItemInUse(stack, getMaxItemUseDuration(stack))
		return stack
	}
}
