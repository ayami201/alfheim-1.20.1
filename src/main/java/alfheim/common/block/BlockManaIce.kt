package alfheim.common.block

import alfheim.common.block.base.*
import net.minecraft.block.material.*
import net.minecraft.world.*

class BlockManaIce: BlockMod(Material.ice) {
	
	init {
		setBlockName("ManaIce")
		setHardness(0.5F)
		setLightOpacity(3)
		setStepSound(soundTypeGlass)
		slipperiness = 1 / 0.91f
	}
	
	override fun isOpaqueCube() = false
	override fun getRenderBlockPass() = 1
	override fun shouldSideBeRendered(world: IBlockAccess, x: Int, y: Int, z: Int, side: Int) = world.getBlock(x, y, z) != this
}
