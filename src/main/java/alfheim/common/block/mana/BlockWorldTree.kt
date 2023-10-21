package alfheim.common.block.mana

import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.TileWorldTree
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import vazkii.botania.api.wand.IWandable

class BlockWorldTree: BlockContainerMod(Material.wood), IWandable {
	
	init {
		setBlockBounds(1/16f, 0f, 1/16f, 15/16f, 1f, 15/16f)
		setBlockName("WorldTree")
		setHardness(1f)
		setStepSound(soundTypeWood)
	}
	
	override fun isOpaqueCube() = false
	
	override fun renderAsNormalBlock() = false
	
	override fun getRenderType() = LibRenderIDs.idWorldTree
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		return !world.isRemote && (world.getTileEntity(x, y, z) as? TileWorldTree)?.onActivated(player, hitX, hitY, hitZ) == true
	}
	
	override fun createNewTileEntity(world: World?, meta: Int) = TileWorldTree()
	
	override fun onUsedByWand(player: EntityPlayer?, stack: ItemStack?, world: World?, x: Int, y: Int, z: Int, side: Int) = false
}
