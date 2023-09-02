package alfheim.common.block

import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.tile.TileTable
import net.minecraft.world.*

class BlockTable: BlockDoubleCamo() {
	
	init {
		setBlockName("Table")
	}
	
	override fun topSide(world: IBlockAccess, x: Int, y: Int, z: Int) = 1
	
	override fun getRenderType() = LibRenderIDs.idTable
	
	override fun createNewTileEntity(world: World, meta: Int) = TileTable()
}