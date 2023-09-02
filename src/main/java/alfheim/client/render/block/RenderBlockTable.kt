package alfheim.client.render.block

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.tile.TileTable
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler
import net.minecraft.block.Block
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.world.IBlockAccess
import org.lwjgl.opengl.GL11

object RenderBlockTable: ISimpleBlockRenderingHandler {
	
	override fun renderInventoryBlock(block: Block, metadata: Int, modelID: Int, renderer: RenderBlocks) {
		GL11.glPushMatrix()
		val tile = TileTable()
		val (x, y, z) = (mc.thePlayer?.let { Vector3.fromEntity(it).add(0, 0.1, 0) } ?: Vector3()).mf()
		
		tile.xCoord = x
		tile.yCoord = y
		tile.zCoord = z
		
		ASJRenderHelper.interpolatedTranslation(mc.thePlayer)
		GL11.glTranslated(-x.D, -y.D - 0.1, -z.D)
		
		TileEntityRendererDispatcher.instance.renderTileEntityAt(tile, 0.0, 0.0, 0.0, 0f)
		GL11.glPopMatrix()
	}
	
	override fun renderWorldBlock(world: IBlockAccess, x: Int, y: Int, z: Int, block: Block, modelId: Int, renderer: RenderBlocks) = false
	override fun shouldRender3DInInventory(modelId: Int) = true
	override fun getRenderId() = LibRenderIDs.idTable
}
