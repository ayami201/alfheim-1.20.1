package alfheim.client.render.block

import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.BlockTable
import net.minecraft.block.Block
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.init.Blocks
import net.minecraft.world.World

object RenderBlockTable: RenderBlockDoubleCamo(LibRenderIDs.idTable, Blocks.planks, 0, Blocks.log, 0) {
	
	override fun renderBlock(world: World?, rb: RenderBlocks, x: Int, y: Int, z: Int, meta: Int, blockTop: Block, blockTopMeta: Int, blockBottom: Block, blockBottomMeta: Int): Boolean {
		val offset = 0.001f
		
		val standFree = rb.blockAccess.getBlock(x + 1, y, z) is BlockTable &&
		                rb.blockAccess.getBlock(x - 1, y, z) is BlockTable ||
		                rb.blockAccess.getBlock(x, y, z + 1) is BlockTable &&
		                rb.blockAccess.getBlock(x, y, z - 1) is BlockTable
		
		var did = false
		
		if (!standFree) did = renderIfPossiblePreservingBounds(world, x, y, z, blockBottom, blockBottomMeta) {
			it.setBlockBounds(6f / 16, 0f, 6f / 16, 10f / 16, 0.75f + offset, 10f / 16)
			rb.setRenderBoundsFromBlock(it)
			rb.renderStandardBlock(it, x, y, z)
		}
		
		did = did or renderIfPossiblePreservingBounds(world, x, y, z, blockTop, blockTopMeta) {
			it.setBlockBounds(offset, 0.75f, offset, 1 - offset, 1f, 1 - offset)
			rb.setRenderBoundsFromBlock(it)
			rb.renderStandardBlock(it, x, y, z)
		}
		
		return did
	}
}
