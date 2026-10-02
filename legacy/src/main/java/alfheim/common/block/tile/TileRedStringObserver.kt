package alfheim.common.block.tile

import vazkii.botania.common.block.tile.string.TileRedString

class TileRedStringObserver: TileRedString() {
	override fun acceptBlock(x: Int, y: Int, z: Int) = !worldObj.isAirBlock(x, y, z)
}
