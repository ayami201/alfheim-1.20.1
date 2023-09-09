package alfheim.common.block

import alexsocol.asjlib.*
import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.tile.TileChair
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.world.*

class BlockChair: BlockDoubleCamo() {
	
	init {
		setBlockBoundsBasedOnMeta(0)
		setBlockName("Chair")
		toItem()?.hasSubtypes = true
	}
	
	override fun setBlockBoundsBasedOnState(world: IBlockAccess?, x: Int, y: Int, z: Int) =
		setBlockBoundsBasedOnMeta(world?.getBlockMetadata(x, y, z) ?: 0)
	
	fun setBlockBoundsBasedOnMeta(meta: Int) {
		when (meta) {
			in 0..5 -> {
				val min = 3 / 16f
				val max = 13 / 16f
				setBlockBounds(min, 0f, min, max, 0.75f, max)
			}
			6       -> setBlockBounds(0f, 0f, 0f, 1f, 0.125f, 1f)
			7       -> setBlockBounds(0f, 0f, 0f, 1f, 0.625f, 1f)
		}
	}
	
	override fun onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, placer: EntityLivingBase, stack: ItemStack) {
		super.onBlockPlacedBy(world, x, y, z, placer, stack)
		if (stack.meta !in 2..5) return
		
		world.setBlockMetadataWithNotify(x, y, z, when(((placer.rotationYaw * 4f / 360f) + 0.5).mfloor() and 3) {
			1 -> 5
			2 -> 3
			3 -> 4
			else -> 2
		}, 2)
	}
	
	override fun topSide(meta: Int) = if (meta == 6) -1 else 1
	
	override fun getSubBlocks(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>) {
		list.add(ItemStack(item, 1, 0))
		list.add(ItemStack(item, 1, 1))
		list.add(ItemStack(item, 1, 3))
		list.add(ItemStack(item, 1, 6))
		list.add(ItemStack(item, 1, 7))
	}
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		if (player.heldItem == null) run sit@ {
			val tile = world.getTileEntity(x, y, z) as? TileChair ?: return@sit
			setBlockBoundsBasedOnMeta(world.getBlockMetadata(x, y, z))
			return tile.mount(player, maxY - 0.1)
		}
		
		return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ)
	}
	
	override fun damageDropped(meta: Int) = if (meta in 2..5) 3 else meta
	override fun getDamageValue(world: World, x: Int, y: Int, z: Int) = damageDropped(world.getBlockMetadata(x, y, z))
	override fun createNewTileEntity(world: World, meta: Int) = TileChair()
	override fun getRenderType() = LibRenderIDs.idChair
}
