package alfheim.client.render.block

import alexsocol.asjlib.glTranslated
import alfheim.api.lib.LibRenderIDs
import alfheim.client.render.tile.RenderTileWorldTree
import alfheim.common.block.tile.TileWorldTree
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler
import net.minecraft.block.Block
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.world.IBlockAccess
import org.lwjgl.opengl.GL11.*

object RenderBlockWorldTree: ISimpleBlockRenderingHandler {
	
	private val instance = TileWorldTree()
	
	override fun renderInventoryBlock(block: Block, metadata: Int, modelID: Int, renderer: RenderBlocks) {
		glPushMatrix()
		glTranslated(-0.5)
		
		RenderTileWorldTree.forcedApples = true
		TileEntityRendererDispatcher.instance.renderTileEntityAt(instance, 0.0, 0.0, 0.0, 0f)
		RenderTileWorldTree.forcedApples = false
		
		glPopMatrix()
	}
	
	override fun renderWorldBlock(world: IBlockAccess, x: Int, y: Int, z: Int, block: Block, modelId: Int, renderer: RenderBlocks) = false
	override fun shouldRender3DInInventory(modelId: Int) = true
	override fun getRenderId() = LibRenderIDs.idWorldTree
}
