package alfheim.common.block.tile

import net.minecraft.init.Blocks

class TileSecretGlass: TileDoubleCamo() {
	
	init {
		blockBottom = Blocks.stained_glass
		blockBottomMeta = 15
		blockTop = Blocks.stonebrick
	}
}