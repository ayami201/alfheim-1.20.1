package alfheim.client.render.block

import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.WorldWrapper
import net.minecraft.block.Block
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.init.Blocks
import net.minecraft.world.World
import net.minecraftforge.client.ForgeHooksClient

object RenderBlockDoubleBlock: RenderBlockDoubleCamo(LibRenderIDs.idDoubleBlock, Blocks.fire, 0, Blocks.glass, 0) {
	
	override fun renderBlock(world: World?, rb: RenderBlocks, x: Int, y: Int, z: Int, meta: Int, blockTop: Block, blockTopMeta: Int, blockBottom: Block, blockBottomMeta: Int): Boolean {
		var did = false
		
		val oldWorld = rb.blockAccess
		val wrapper = WorldWrapper(rb.blockAccess)
		rb.blockAccess = wrapper
		
		if (blockBottom.canRenderInPass(ForgeHooksClient.getWorldRenderPass())) {
			wrapper.setOverride(x, y, z, blockBottom, blockBottomMeta)
			world?.setBlockMetadataWithNotify(x, y, z, blockBottomMeta, 4)
			did = rb.renderBlockByRenderType(blockBottom, x, y, z)
		}
		
		if (blockTop.canRenderInPass(ForgeHooksClient.getWorldRenderPass())) {
			wrapper.setOverride(x, y, z, blockTop, blockTopMeta)
			world?.setBlockMetadataWithNotify(x, y, z, blockTopMeta, 4)
			did = rb.renderBlockByRenderType(blockTop, x, y, z)
		}
		
		rb.blockAccess = oldWorld
		
		world?.setBlockMetadataWithNotify(x, y, z, meta, 4)
		
		return did
	}
}
