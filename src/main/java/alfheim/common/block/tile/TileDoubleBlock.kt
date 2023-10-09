package alfheim.common.block.tile

import net.minecraft.init.Blocks

class TileDoubleBlock: TileDoubleCamo() {
	
	init {
		blockTop = Blocks.fire
		blockBottom = Blocks.glass
	}
}
