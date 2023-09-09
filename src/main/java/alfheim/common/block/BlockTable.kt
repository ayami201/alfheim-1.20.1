package alfheim.common.block

import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.tile.TileTable
import net.minecraft.world.*
import net.minecraftforge.common.util.ForgeDirection

class BlockTable: BlockDoubleCamo() {
	
	init {
		setBlockName("Table")
	}
	
	override fun isSideSolid(world: IBlockAccess?, x: Int, y: Int, z: Int, side: ForgeDirection?) = side == ForgeDirection.UP
	override fun topSide(meta: Int) = 1
	override fun getRenderType() = LibRenderIDs.idTable
	override fun createNewTileEntity(world: World, meta: Int) = TileTable()
}