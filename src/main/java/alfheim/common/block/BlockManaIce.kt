package alfheim.common.block

// PORT: импорты 1.7.10 заменены на 1.20.1
import alfheim.common.block.base.*
import alfheim.port.legacy.*
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState

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
	// PORT: shouldSideBeRendered (рисовать грань, если сосед — не этот блок) → skipRendering (не рисовать, если этот)
	override fun skipRendering(state: BlockState, adjacent: BlockState, side: Direction) = adjacent.block == this
//	override fun shouldSideBeRendered(world: IBlockAccess, x: Int, y: Int, z: Int, side: Int) = world.getBlock(x, y, z) != this
}
