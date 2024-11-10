package alfheim.common.item.block

import alexsocol.asjlib.math.Vector3
import alfheim.common.block.BlockItemFrame
import alfheim.common.block.tile.*
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.*
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection

class ItemBlockItemFrame(block: Block): ItemBlock(block) {
	
	override fun placeBlockAt(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float, metadata: Int): Boolean {
		val block: Block = world.getBlock(x, y, z)
		
		if (block !== field_150939_a) {
			if (!world.canPlaceEntityOnSide(field_150939_a, x, y, z, false, side, null, stack)) return false
			
			if (!super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)) return false
		}
		val tile: TileItemFrame = world.getTileEntity(x, y, z) as TileItemFrame
		if (tile.frames[side xor 1] != null) return false
		
		tile.frames[side xor 1] = Frame()
		world.notifyBlocksOfNeighborChange(x, y, z, block)
		world.markBlockForUpdate(x, y, z)
		return true
	}
	
	override fun onItemUse(stack: ItemStack, player: EntityPlayer, world: World, i: Int, j: Int, k: Int, s: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		var x = i
		var y = j
		var z = k
		var side = s
		val block: Block = world.getBlock(x, y, z)
		
		if (block === Blocks.snow_layer && (world.getBlockMetadata(x, y, z) and 7) < 1) {
			side = 1
		} else if (block !== Blocks.vine && block !== Blocks.tallgrass && block !== Blocks.deadbush && !block.isReplaceable(world, x, y, z)) {
			if (side == 0) --y
			if (side == 1) ++y
			if (side == 2) --z
			if (side == 3) ++z
			if (side == 4) --x
			if (side == 5) ++x
		}
		
		if (stack.stackSize == 0 || !player.canPlayerEdit(x, y, z, side, stack)) return false
		
		if (placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, stack.getItemDamage())) {
			world.playSoundEffect(
				x + 0.5,
				y + 0.5,
				z + 0.5,
				field_150939_a.stepSound.func_150496_b(),
				(field_150939_a.stepSound.getVolume() + 1.0f) / 2.0f,
				field_150939_a.stepSound.pitch * 0.8f
			)
			--stack.stackSize
			return true
		}
		
		return false
	}
	
	/**
	 * Returns true if the given ItemBlock can be placed on the given side of the given block position.
	 */
	override fun func_150936_a(world: World, i: Int, j: Int, k: Int, side: Int, player: EntityPlayer, stack: ItemStack): Boolean {
		val d = ForgeDirection.getOrientation(side).opposite
		val (x, y, z) = Vector3(i, j, k).add(d.offsetX, d.offsetY, d.offsetZ).I
		return BlockItemFrame.canExist(world, x, y, z, side)
	}
}