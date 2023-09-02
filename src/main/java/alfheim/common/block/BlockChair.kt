package alfheim.common.block

import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.tile.TileChair
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.world.*

class BlockChair: BlockDoubleCamo() {
	
	init {
		val min = 3/16f
		val max = 13/16f
		setBlockBounds(min, 0f, min, max, 0.75f, max)
		setBlockName("Chair")
	}
	
	override fun topSide(world: IBlockAccess, x: Int, y: Int, z: Int) = 1
	
	override fun getSubBlocks(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>) {
		list.add(ItemStack(item, 1, 0))
		list.add(ItemStack(item, 1, 1))
	}
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		if (player.heldItem == null) run sit@ {
			val tile = world.getTileEntity(x, y, z) as? TileChair ?: return@sit
			return tile.mount(player)
		}
		
		return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ)
	}
	
	override fun damageDropped(meta: Int) = meta
	override fun getDamageValue(world: World, x: Int, y: Int, z: Int) = world.getBlockMetadata(x, y, z)
	override fun createNewTileEntity(world: World, meta: Int) = TileChair()
	override fun getRenderType() = LibRenderIDs.idChair
}
