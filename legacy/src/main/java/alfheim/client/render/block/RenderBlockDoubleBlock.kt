package alfheim.client.render.block

import alexsocol.asjlib.try_
import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.WorldWrapper
import alfheim.common.block.tile.TileDoubleCamo
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.world.World
import net.minecraftforge.client.ForgeHooksClient

object RenderBlockDoubleBlock: RenderBlockDoubleCamo(LibRenderIDs.idDoubleBlock) {
	
	override fun renderBlock(world: World?, rb: RenderBlocks, x: Int, y: Int, z: Int, meta: Int, tile: TileDoubleCamo): Boolean {
		var did = false
		
		val oldMeta = world?.getBlockMetadata(x, y, z) ?: 0
		
		val oldWorld = rb.blockAccess
		val wrapper = WorldWrapper(rb.blockAccess)
		rb.blockAccess = wrapper
		
		try_ {
			if (tile.blockBottom.canRenderInPass(ForgeHooksClient.getWorldRenderPass())) {
				wrapper.setOverride(x, y, z, tile.blockBottom, tile.blockBottomMeta)
				world?.setBlockMetadataWithNotify(x, y, z, tile.blockBottomMeta, 4)
				did = rb.renderBlockByRenderType(tile.blockBottom, x, y, z)
			}
			
			if (tile.blockTop.canRenderInPass(ForgeHooksClient.getWorldRenderPass())) {
				wrapper.setOverride(x, y, z, tile.blockTop, tile.blockTopMeta)
				world?.setBlockMetadataWithNotify(x, y, z, tile.blockTopMeta, 4)
				did = rb.renderBlockByRenderType(tile.blockTop, x, y, z)
			}
		}
		
		rb.blockAccess = oldWorld
		
		world?.setBlockMetadataWithNotify(x, y, z, oldMeta, 4)
		
		return did
	}
}
