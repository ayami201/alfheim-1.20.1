package alfheim.common.block.tile

import alexsocol.asjlib.extendables.block.ASJTile
import alfheim.common.block.BlockTreeBerry

class TileTreeBerry: ASJTile() {
	
	val type get() = (getBlockType() as? BlockTreeBerry)?.type ?: 0
	
	override fun canUpdate() = false
}
