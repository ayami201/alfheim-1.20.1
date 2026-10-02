package alfheim.client.render.block

import alexsocol.asjlib.*
import alfheim.api.lib.LibRenderIDs
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler
import net.minecraft.block.Block
import net.minecraft.client.renderer.*
import net.minecraft.world.IBlockAccess

object RenderBlockNidhoggTooth: ISimpleBlockRenderingHandler {
	
	override fun renderWorldBlock(world: IBlockAccess, x: Int, y: Int, z: Int, block: Block, modelId: Int, renderer: RenderBlocks?): Boolean {
		val meta = world.getBlockMetadata(x, y, z) and 3
		
		val icon = block.getIcon(0, meta)
		val u = icon.minU.D
		val U = icon.maxU.D
		val v = icon.minV.D
		val V = icon.maxV.D
		
		val tes = Tessellator.instance
		tes.addTranslation(x.F, y.F, z.F)
		tes.setColorOpaque_I(0xFFFFFF)
		tes.setBrightness(world.getLightBrightnessForSkyBlocks(x, y, z, meta))
		
		when (meta) {
			0 -> {
				tes.addVertexWithUV(0.0, 0.5, 0.0, U, V)
				tes.addVertexWithUV(0.0, 0.5, 1.0, U, v)
				tes.addVertexWithUV(1.0, 0.5, 1.0, u, v)
				tes.addVertexWithUV(1.0, 0.5, 0.0, u, V)
				
				tes.addVertexWithUV(0.0, 0.5, 0.0, U, V)
				tes.addVertexWithUV(1.0, 0.5, 0.0, u, V)
				tes.addVertexWithUV(1.0, 0.5, 1.0, u, v)
				tes.addVertexWithUV(0.0, 0.5, 1.0, U, v)
				
				tes.addVertexWithUV(0.5, 0.0, 0.0, u, V)
				tes.addVertexWithUV(0.5, 0.0, 1.0, u, v)
				tes.addVertexWithUV(0.5, 1.0, 1.0, U, v)
				tes.addVertexWithUV(0.5, 1.0, 0.0, U, V)
				
				tes.addVertexWithUV(0.5, 0.0, 0.0, u, V)
				tes.addVertexWithUV(0.5, 1.0, 0.0, U, V)
				tes.addVertexWithUV(0.5, 1.0, 1.0, U, v)
				tes.addVertexWithUV(0.5, 0.0, 1.0, u, v)
			}
			1 -> {
				tes.addVertexWithUV(0.0, 0.5, 0.0, u, V)
				tes.addVertexWithUV(0.0, 0.5, 1.0, U, V)
				tes.addVertexWithUV(1.0, 0.5, 1.0, U, v)
				tes.addVertexWithUV(1.0, 0.5, 0.0, u, v)
				
				tes.addVertexWithUV(0.0, 0.5, 0.0, u, V)
				tes.addVertexWithUV(1.0, 0.5, 0.0, u, v)
				tes.addVertexWithUV(1.0, 0.5, 1.0, U, v)
				tes.addVertexWithUV(0.0, 0.5, 1.0, U, V)
				
				tes.addVertexWithUV(0.0, 0.0, 0.5, u, V)
				tes.addVertexWithUV(0.0, 1.0, 0.5, U, V)
				tes.addVertexWithUV(1.0, 1.0, 0.5, U, v)
				tes.addVertexWithUV(1.0, 0.0, 0.5, u, v)
				
				tes.addVertexWithUV(0.0, 0.0, 0.5, u, V)
				tes.addVertexWithUV(1.0, 0.0, 0.5, u, v)
				tes.addVertexWithUV(1.0, 1.0, 0.5, U, v)
				tes.addVertexWithUV(0.0, 1.0, 0.5, U, V)
			}
			2 -> {
				tes.addVertexWithUV(0.0, 0.5, 0.0, u, v)
				tes.addVertexWithUV(0.0, 0.5, 1.0, u, V)
				tes.addVertexWithUV(1.0, 0.5, 1.0, U, V)
				tes.addVertexWithUV(1.0, 0.5, 0.0, U, v)
				
				tes.addVertexWithUV(0.0, 0.5, 0.0, u, v)
				tes.addVertexWithUV(1.0, 0.5, 0.0, U, v)
				tes.addVertexWithUV(1.0, 0.5, 1.0, U, V)
				tes.addVertexWithUV(0.0, 0.5, 1.0, u, V)
				
				tes.addVertexWithUV(0.5, 0.0, 0.0, u, v)
				tes.addVertexWithUV(0.5, 0.0, 1.0, u, V)
				tes.addVertexWithUV(0.5, 1.0, 1.0, U, V)
				tes.addVertexWithUV(0.5, 1.0, 0.0, U, v)
				
				tes.addVertexWithUV(0.5, 0.0, 0.0, u, v)
				tes.addVertexWithUV(0.5, 1.0, 0.0, U, v)
				tes.addVertexWithUV(0.5, 1.0, 1.0, U, V)
				tes.addVertexWithUV(0.5, 0.0, 1.0, u, V)
			}
			3 -> {
				tes.addVertexWithUV(0.0, 0.5, 0.0, U, v)
				tes.addVertexWithUV(0.0, 0.5, 1.0, u, v)
				tes.addVertexWithUV(1.0, 0.5, 1.0, u, V)
				tes.addVertexWithUV(1.0, 0.5, 0.0, U, V)
				
				tes.addVertexWithUV(0.0, 0.5, 0.0, U, v)
				tes.addVertexWithUV(1.0, 0.5, 0.0, U, V)
				tes.addVertexWithUV(1.0, 0.5, 1.0, u, V)
				tes.addVertexWithUV(0.0, 0.5, 1.0, u, v)
				
				tes.addVertexWithUV(0.0, 0.0, 0.5, u, v)
				tes.addVertexWithUV(0.0, 1.0, 0.5, U, v)
				tes.addVertexWithUV(1.0, 1.0, 0.5, U, V)
				tes.addVertexWithUV(1.0, 0.0, 0.5, u, V)
				
				tes.addVertexWithUV(0.0, 0.0, 0.5, u, v)
				tes.addVertexWithUV(1.0, 0.0, 0.5, u, V)
				tes.addVertexWithUV(1.0, 1.0, 0.5, U, V)
				tes.addVertexWithUV(0.0, 1.0, 0.5, U, v)
			}
		}
		
		tes.addTranslation(-x.F, -y.F, -z.F)
		
		return true
	}
	
	override fun renderInventoryBlock(block: Block, metadata: Int, modelId: Int, renderer: RenderBlocks?) = Unit
	
	override fun shouldRender3DInInventory(modelId: Int) = false
	
	override fun getRenderId() = LibRenderIDs.idNidhoggTooth
}
