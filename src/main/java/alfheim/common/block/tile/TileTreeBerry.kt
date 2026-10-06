package alfheim.common.block.tile

// PORT: импорты 1.20.1
import alexsocol.asjlib.extendables.block.ASJTile
import alfheim.common.block.BlockTreeBerry
import alfheim.port.legacy.legacyTileType
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

// PORT: блок-сущность 1.20.1 создаётся сразу в своей точке и со своим типом (alfheim.port.legacy.TileEntity)
class TileTreeBerry(pos: BlockPos, state: BlockState): ASJTile(legacyTileType<TileTreeBerry>(), pos, state) {
//class TileTreeBerry: ASJTile() {
	
	// PORT: имя type у блок-сущности 1.20.1 занято — это её тип (getType); вид ягоды — berryType
	val berryType get() = (getBlockType() as? BlockTreeBerry)?.type ?: 0
//	val type get() = (getBlockType() as? BlockTreeBerry)?.type ?: 0
	
	override fun canUpdate() = false
}
