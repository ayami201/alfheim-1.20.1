package alfheim.client.render.block

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.tile.TileChair
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler
import net.minecraft.block.Block
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.world.IBlockAccess
import org.lwjgl.opengl.GL11.*

object RenderBlockChair: ISimpleBlockRenderingHandler {
	
	override fun renderInventoryBlock(block: Block, metadata: Int, modelID: Int, renderer: RenderBlocks) {
		glPushMatrix()
		val tile = TileChair()
		val (x, y, z) = (mc.thePlayer?.let { Vector3.fromEntity(it).add(0, 0.1, 0) } ?: Vector3()).mf()
		
		tile.xCoord = x
		tile.yCoord = y
		tile.zCoord = z
		tile.blockMetadata = metadata
		
		ASJRenderHelper.interpolatedTranslation(mc.thePlayer)
		glTranslated(-x.D, -y.D, -z.D)
		
		TileEntityRendererDispatcher.instance.renderTileEntityAt(tile, 0.0, 0.0, 0.0, 0f)
		glPopMatrix()
	}
	
	override fun renderWorldBlock(world: IBlockAccess, x: Int, y: Int, z: Int, block: Block, modelId: Int, renderer: RenderBlocks) = false
	override fun shouldRender3DInInventory(modelId: Int) = true
	override fun getRenderId() = LibRenderIDs.idChair
}
