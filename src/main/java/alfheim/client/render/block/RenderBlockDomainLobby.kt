package alfheim.client.render.block

import alexsocol.asjlib.render.RenderGlowingLayerBlock
import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.BlockDomainDoor
import net.minecraft.block.Block
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.world.IBlockAccess

object RenderBlockDomainLobby: RenderGlowingLayerBlock() {
	
	override fun renderWorldBlock(world: IBlockAccess, x: Int, y: Int, z: Int, block: Block, modelId: Int, renderer: RenderBlocks): Boolean {
		if (world.getBlockMetadata(x, y, z) == 1)
			return super.renderWorldBlock(world, x, y, z, block, modelId, renderer)
		
		renderer.renderStandardBlock(block, x, y, z)
		return true
	}
	
	override fun renderInventoryBlock(block: Block, meta: Int, modelID: Int, renderer: RenderBlocks) {
		if (meta == 1)
			return super.renderInventoryBlock(block, meta, modelID, renderer)
		
		(block as? BlockDomainDoor)?.setBlockBoundsBasedOnMeta(meta)
		renderer.setRenderBoundsFromBlock(block)
		
		drawFaces(renderer, block) { block.getIcon(it, meta) }
	}
	
	override fun shouldRender3DInInventory(modelId: Int) = true
	override fun getRenderId() = LibRenderIDs.idDomainDoor
}
