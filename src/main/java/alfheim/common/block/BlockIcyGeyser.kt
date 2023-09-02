package alfheim.common.block

import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.TileIcyGeyser
import net.minecraft.block.material.Material
import net.minecraft.init.Blocks
import net.minecraft.world.World

class BlockIcyGeyser: BlockContainerMod(Material.packedIce) {
	
	init {
		setBlockName("IcyGeyser")
		setHardness(0.5F)
		setStepSound(soundTypeGlass)
		slipperiness = 0.98f
	}
	
	override fun getIcon(side: Int, meta: Int) = Blocks.packed_ice.getIcon(side, meta)!!
	
	override fun createNewTileEntity(world: World?, meta: Int) = TileIcyGeyser()
}
