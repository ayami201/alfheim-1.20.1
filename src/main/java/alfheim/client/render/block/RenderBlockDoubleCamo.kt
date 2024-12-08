package alfheim.client.render.block

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.block.tile.TileDoubleCamo
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler
import net.minecraft.block.Block
import net.minecraft.client.renderer.*
import net.minecraft.world.*
import net.minecraftforge.client.ForgeHooksClient
import org.lwjgl.opengl.GL11.*

abstract class RenderBlockDoubleCamo(val id: Int): ISimpleBlockRenderingHandler {
	
	override fun renderWorldBlock(world: IBlockAccess, x: Int, y: Int, z: Int, block: Block, modelId: Int, rb: RenderBlocks): Boolean {
		val tile = world.getTileEntity(x, y, z) as? TileDoubleCamo ?: return false
		
		if (rb.blockAccess == null) rb.blockAccess = mc.theWorld
		if (tile.worldObj == null) tile.worldObj = mc.theWorld
		
		return try {
			renderBlock(tile.worldObj, rb, x, y, z, world.getBlockMetadata(x, y, z), tile)
		} catch (ignore: Throwable) {
			return false
		}
	}
	
	override fun renderInventoryBlock(block: Block, meta: Int, modelID: Int, rb: RenderBlocks) {
		if (rb.blockAccess == null) rb.blockAccess = mc.theWorld
		
		glPushMatrix()
		glTranslated(-0.5)
		glDisable(GL_LIGHTING)
		val (x, y, z) = (mc.thePlayer?.let { Vector3.fromEntity(it).add(0, 0.1, 0) } ?: Vector3()).mf()
		glTranslatef(-x.F, -y.F, -z.F)
		
		val prevPass = ForgeHooksClient.getWorldRenderPass()
		
		for (i in 0..1) {
			ForgeHooksClient.worldRenderPass = i
			Tessellator.instance.startDrawingQuads()
//			renderBlock(null, rb, x, y, z, ibm ?: meta, tile)
			Tessellator.instance.draw()
		}
		
		ForgeHooksClient.worldRenderPass = prevPass
		glEnable(GL_LIGHTING)
		glPopMatrix()
	}
	
	fun renderIfPossiblePreservingBounds(world: World?, x: Int, y: Int, z: Int, block: Block, meta: Int, render: (Block) -> Boolean): Boolean {
		if (!block.canRenderInPass(ForgeHooksClient.getWorldRenderPass())) return false
		
		val i = block.blockBoundsMinX
		val j = block.blockBoundsMinY
		val k = block.blockBoundsMinZ
		val l = block.blockBoundsMaxX
		val m = block.blockBoundsMaxY
		val n = block.blockBoundsMaxZ
		val oldMeta = world?.getBlockMetadata(x, y, z)
		
		world?.setBlockMetadataWithNotify(x, y, z, meta, 4)
		val result = render(block)
		
		block.setBlockBounds(i, j, k, l, m, n)
		oldMeta?.let { world.setBlockMetadataWithNotify(x, y, z, oldMeta, 4) }
		
		return result
	}
	
	protected fun Block.setBlockBounds(minX: Double, minY: Double, minZ: Double, maxX: Double, maxY: Double, maxZ: Double) {
		setBlockBounds(minX.F, minY.F, minZ.F, maxX.F, maxY.F, maxZ.F)
	}
	
	abstract fun renderBlock(world: World?, rb: RenderBlocks, x: Int, y: Int, z: Int, meta: Int, tile: TileDoubleCamo): Boolean
	override fun shouldRender3DInInventory(modelId: Int) = false
	override fun getRenderId() = id
}
