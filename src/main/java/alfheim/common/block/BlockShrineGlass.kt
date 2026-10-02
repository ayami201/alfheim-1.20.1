package alfheim.common.block

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.api.ModInfo
import alfheim.common.core.util.AlfheimTab
import alfheim.port.legacy.*
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState

// PORT: вариант metadata — отдельный блок (BlockModMeta)
class BlockShrineGlass(meta: Int): BlockModMeta(Material.glass, 5, ModInfo.MODID, "ShrineGlass", AlfheimTab, resist = 600f, folder = "decor/", meta = meta) {
	
	init {
		setLightOpacity(0)
	}
	
	override fun getRenderBlockPass() = 1
	
	override fun isOpaqueCube() = false
	
	// PORT: renderAsNormalBlock — форма блока; полный куб без затенения соседей задают isOpaqueCube и модель
//	override fun renderAsNormalBlock() = false
	
	// PORT: блок и так роняет себя (лут — alfheim.port.data.AlfheimBlockLoot)
//	override fun canSilkHarvest() = true
	
	// PORT: shouldSideBeRendered → skipRendering. Вариант — отдельный блок: «тот же блок и та же metadata» — тот же
	// блок; соседа другого варианта или другого блока 1.20.1 рисует, если тот не закрывает грань, как super 1.7.10
	override fun skipRendering(state: BlockState, adjacent: BlockState, side: Direction) = adjacent.block === this
	/*
	@SideOnly(Side.CLIENT)
	override fun shouldSideBeRendered(access: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Boolean {
		val block = access.getBlock(x, y, z)
		
		if (access.getBlockMetadata(x, y, z) != access.getBlockMetadata(x - Facing.offsetsXForSide[side], y - Facing.offsetsYForSide[side], z - Facing.offsetsZForSide[side])) {
			return true
		}
		return if (block === this) {
			false
		} else super.shouldSideBeRendered(access, x, y, z, side)
	}
	*/
}
