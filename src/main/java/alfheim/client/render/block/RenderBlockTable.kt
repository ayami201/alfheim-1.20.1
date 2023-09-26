package alfheim.client.render.block

import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.BlockTable
import alfheim.common.block.tile.TileDoubleCamo
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.world.World

object RenderBlockTable: RenderBlockDoubleCamo(LibRenderIDs.idTable) {
	
	override fun renderBlock(world: World?, rb: RenderBlocks, x: Int, y: Int, z: Int, meta: Int, tile: TileDoubleCamo): Boolean {
		val offset = 0.001f
		
		val standFree = rb.blockAccess.getBlock(x + 1, y, z) is BlockTable &&
		                rb.blockAccess.getBlock(x - 1, y, z) is BlockTable ||
		                rb.blockAccess.getBlock(x, y, z + 1) is BlockTable &&
		                rb.blockAccess.getBlock(x, y, z - 1) is BlockTable
		
		var did = false
		
		if (!standFree) did = renderIfPossiblePreservingBounds(world, x, y, z, tile.blockBottom, tile.blockBottomMeta) {
			it.setBlockBounds(6f / 16, 0f, 6f / 16, 10f / 16, 0.75f + offset, 10f / 16)
			rb.setRenderBoundsFromBlock(it)
			rb.renderStandardBlock(it, x, y, z)
		}
		
		did = did or renderIfPossiblePreservingBounds(world, x, y, z, tile.blockTop, tile.blockTopMeta) {
			it.setBlockBounds(offset, 0.75f, offset, 1 - offset, 1f, 1 - offset)
			rb.setRenderBoundsFromBlock(it)
			rb.renderStandardBlock(it, x, y, z)
		}
		
		return did
	}
}
