package alfheim.common.item.block

import alfheim.common.block.AlfheimBlocks
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.util.MovingObjectPosition
import net.minecraft.world.World
import net.minecraftforge.common.util.*
import net.minecraftforge.event.ForgeEventFactory

class ItemBlockGrapeWhite(block: Block): ItemBlock(block) {
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		val mop = getMovingObjectPositionFromPlayer(world, player, true) ?: return stack
		if (mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return stack
		
		val i = mop.blockX
		val j = mop.blockY + 1
		val k = mop.blockZ
		
		if (!world.canMineBlock(player, i, j, k))
			return stack
		
		if (!player.canPlayerEdit(i, j, k, mop.sideHit, stack))
			return stack
		
		if (!world.isAirBlock(i, j, k) || !AlfheimBlocks.grapesWhite.canBlockStay(world, i, j, k))
			return stack
		
		val blocksnapshot = BlockSnapshot.getBlockSnapshot(world, i, j, k)
		
		world.setBlock(i, j, k, AlfheimBlocks.grapesWhite)
		
		if (ForgeEventFactory.onPlayerBlockPlace(player, blocksnapshot, ForgeDirection.UP).isCanceled) {
			blocksnapshot.restore(true, false)
			return stack
		}
		
		if (!player.capabilities.isCreativeMode)
			--stack.stackSize
		
		return stack
	}
}