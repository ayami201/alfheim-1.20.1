package alfheim.common.item.block

import alfheim.common.block.alt.BlockAltWood
import net.minecraft.block.Block
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.world.World

class ItemBlockNidhoggTooth(block: Block): ItemBlock(block) {
	
	init {
		setFull3D()
		setMaxStackSize(1)
	}
	
	override fun onBlockDestroyed(stack: ItemStack, world: World, block: Block, x: Int, y: Int, z: Int, player: EntityLivingBase): Boolean {
		if (block !is BlockAltWood || block.set != 1 || world.getBlockMetadata(x, y, z) % 4 != 2) return false
		
		if (player is EntityPlayer && player.capabilities.isCreativeMode) return true
		if (player.rng.nextBoolean()) --stack.stackSize
		
		return true
	}
	
	// nope
	
	override fun onItemUse(stack: ItemStack?, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float) = false
	
	override fun func_150936_a(world: World?, x: Int, y: Int, z: Int, side: Int, player: EntityPlayer?, stack: ItemStack?) = false
	
	override fun placeBlockAt(stack: ItemStack?, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float, meta: Int) = false
}
