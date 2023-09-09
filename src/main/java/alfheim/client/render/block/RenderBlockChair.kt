package alfheim.client.render.block

import alfheim.api.lib.LibRenderIDs
import net.minecraft.block.Block
import net.minecraft.client.renderer.*
import net.minecraft.init.Blocks
import net.minecraft.world.World

object RenderBlockChair: RenderBlockDoubleCamo(LibRenderIDs.idChair, Blocks.planks, 0, Blocks.log, 0) {
	
	override fun renderBlock(world: World?, rb: RenderBlocks, x: Int, y: Int, z: Int, meta: Int, blockTop: Block, blockTopMeta: Int, blockBottom: Block, blockBottomMeta: Int): Boolean {
		val min = 3.0 / 16
		val max = 13.0 / 16
		val thicc = 2.0 / 16
		val offset = 0.001
		
		var did = renderIfPossiblePreservingBounds(world, x, y, z, blockBottom, blockBottomMeta) {
			when (meta) {
				0       -> {
					it.setBlockBounds(0.25, 0.0, 0.25, 0.75, 1.0 / 16, 0.75)
					rb.setRenderBoundsFromBlock(it)
					rb.renderStandardBlock(it, x, y, z)
					
					it.setBlockBounds(6.0 / 16, 1.0 / 16 - offset, 6.0 / 16, 10.0 / 16, 0.5 + offset, 10.0 / 16)
					rb.setRenderBoundsFromBlock(it)
					rb.renderStandardBlock(it, x, y, z)
				}
				
				in 1..5 -> {
					val maxmaxHeight = if (meta == 2 || meta == 4) 1.25 else 0.5
					val maxminHeight = if (meta == 3 || meta == 4) 1.25 else 0.5
					val minminHeight = if (meta == 3 || meta == 5) 1.25 else 0.5
					val minmaxHeight = if (meta == 2 || meta == 5) 1.25 else 0.5
					
					it.setBlockBounds(min - offset, 0.0, min - offset, min + offset + thicc, minminHeight + offset, min + offset + thicc)
					rb.setRenderBoundsFromBlock(it)
					rb.renderStandardBlock(it, x, y, z)
					
					it.setBlockBounds(max - offset - thicc, 0.0, min - offset, max + offset, maxminHeight + offset, min + offset + thicc)
					rb.setRenderBoundsFromBlock(it)
					rb.renderStandardBlock(it, x, y, z)
					
					it.setBlockBounds(max - offset - thicc, 0.0, max - offset - thicc, max + offset, maxmaxHeight + offset, max + offset)
					rb.setRenderBoundsFromBlock(it)
					rb.renderStandardBlock(it, x, y, z)
					
					it.setBlockBounds(min - offset, 0.0, max - offset - thicc, min + offset + thicc, minmaxHeight + offset, max + offset)
					rb.setRenderBoundsFromBlock(it)
					rb.renderStandardBlock(it, x, y, z)
				}
				
				6       -> {
					it.setBlockBounds(0.0, 0.0, 0.0, 1.0, 0.125, 1.0)
					rb.setRenderBoundsFromBlock(it)
					rb.renderStandardBlock(it, x, y, z)
				}
				
				7       -> {
					it.setBlockBounds(0.0, 0.0, 0.0, 1.0, 0.5, 1.0)
					rb.setRenderBoundsFromBlock(it)
					rb.renderStandardBlock(it, x, y, z)
				}
				else -> return@renderIfPossiblePreservingBounds false
			}
			
			true
		}
		
		did = did or renderIfPossiblePreservingBounds(world, x, y, z, blockTop, blockTopMeta) {
			when (meta) {
				in 0..5 -> {
					it.setBlockBounds(min, 0.5, min, max, 0.75, max)
					rb.setRenderBoundsFromBlock(it)
					rb.renderStandardBlock(it, x, y, z)
					
					if (meta in 2..5) {
						when (meta) {
							2 -> it.setBlockBounds(min + thicc, 0.3125, max - thicc, max - thicc, 0.6875 + offset, max)
							3 -> it.setBlockBounds(min + thicc, 0.3125, min, max - thicc, 0.6875 + offset, min + thicc)
							4 -> it.setBlockBounds(max - thicc, 0.3125, min + thicc, max, 0.6875 + offset, max - thicc)
							5 -> it.setBlockBounds(min, 0.3125, min + thicc, min + thicc, 0.6875 + offset, max - thicc)
						}
						
						Tessellator.instance.addTranslation(0f, 0.5625f, 0f)
						rb.setRenderBoundsFromBlock(it)
						rb.renderStandardBlock(it, x, y, z)
						Tessellator.instance.addTranslation(0f, -0.5625f, 0f)
					}
				}
				7       -> {
					it.setBlockBounds(0.0, 0.5 + offset, 0.0, 1.0, 0.625, 1.0)
					rb.setRenderBoundsFromBlock(it)
					rb.renderStandardBlock(it, x, y, z)
				}
				else -> return@renderIfPossiblePreservingBounds false
			}
			
			true
		}
		
		return did
	}
}
