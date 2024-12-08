package alfheim.client.render.block

import alexsocol.asjlib.*
import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.tile.*
import net.minecraft.block.*
import net.minecraft.client.renderer.*
import net.minecraft.init.Blocks
import net.minecraft.util.IIcon
import net.minecraft.world.*
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.common.util.ForgeDirection

object RenderBlockComposite: RenderBlockDoubleCamo(LibRenderIDs.idComposite) {
	
	override fun renderBlock(world: World?, rb: RenderBlocks, x: Int, y: Int, z: Int, meta: Int, tile: TileDoubleCamo): Boolean {
		if (tile !is TileComposite) return false
		
		val step = 1.0 / tile.size
		
		val oldWorld = rb.blockAccess
		val compositeWorld = CompositionWorld(oldWorld, tile.composition)
		rb.blockAccess = compositeWorld
		var did = false
		
		try_ {
			for ((i, sub) in tile.composition.withIndex())
				for ((j, subber) in sub.withIndex())
					for ((k, data) in subber.withIndex()) {
						val block = data?.first ?: continue
						
						if (!block.canRenderInPass(ForgeHooksClient.getWorldRenderPass())) continue
						
						rb.setRenderBounds(i * step, j * step, k * step, (i + 1) * step, (j + 1) * step, (k + 1) * step)
						did = did or renderStandardBlock(rb, block, x, y, z, i, j, k)
					}
		}
		
		rb.blockAccess = oldWorld
		
		return did
	}
	
	fun renderStandardBlock(rb: RenderBlocks, block: Block, x: Int, y: Int, z: Int, i: Int, j: Int, k: Int): Boolean {
		val l = block.colorMultiplier(rb.blockAccess, i, j, k)
		var r = (l shr 16 and 255) / 255.0f
		var g = (l shr 8 and 255) / 255.0f
		var b = (l and 255) / 255.0f
		
		if (EntityRenderer.anaglyphEnable) {
			r = (r * 30.0f + g * 59.0f + b * 11.0f) / 100.0f
			g = (r * 30.0f + g * 70.0f) / 100.0f
			b = (r * 30.0f + b * 70.0f) / 100.0f
		}
		
		return renderStandardBlockWithColorMultiplier(rb, block, x, y, z, r, g, b, i, j, k)
	}
	
	fun renderStandardBlockWithColorMultiplier(rb: RenderBlocks, block: Block, x: Int, y: Int, z: Int, r: Float, g: Float, b: Float, i: Int, j: Int, k: Int): Boolean {
		rb.enableAO = false
		
		val tes = Tessellator.instance
		var flag = false
		
		val cMulY = 0.5f
		val cMulZ = 0.8f
		val cMulX = 0.6f
		
		var ry = cMulY
		var rz = cMulZ
		var rx = cMulX
		var gy = cMulY
		var gz = cMulZ
		var gx = cMulX
		var by = cMulY
		var bz = cMulZ
		var bx = cMulX
		
		if (block !== Blocks.grass) {
			ry = cMulY * r
			rz = cMulZ * r
			rx = cMulX * r
			gy = cMulY * g
			gz = cMulZ * g
			gx = cMulX * g
			by = cMulY * b
			bz = cMulZ * b
			bx = cMulX * b
		}
		
		val l = block.getMixedBrightnessForBlock(rb.blockAccess, x, y, z)
		
		if (block.shouldSideBeRendered(rb.blockAccess, i, j - 1, k, 0)) {
			tes.setBrightness(l)
			tes.setColorOpaque_F(ry, gy, by)
			rb.renderFaceYNeg(block, x.D, y.D, z.D, rb.getBlockIcon(block, rb.blockAccess, i, j, k, 0))
			flag = true
		}
		
		if (block.shouldSideBeRendered(rb.blockAccess, i, j + 1, k, 1)) {
			tes.setBrightness(l)
			tes.setColorOpaque_F(r, g, b)
			rb.renderFaceYPos(block, x.D, y.D, z.D, rb.getBlockIcon(block, rb.blockAccess, i, j, k, 1))
			flag = true
		}
		
		var iicon: IIcon
		if (block.shouldSideBeRendered(rb.blockAccess, i, j, k - 1, 2)) {
			tes.setBrightness(l)
			tes.setColorOpaque_F(rz, gz, bz)
			iicon = rb.getBlockIcon(block, rb.blockAccess, i, j, k, 2)
			rb.field_152631_f = true
			rb.renderFaceZNeg(block, x.D, y.D, z.D, iicon)
			if (RenderBlocks.fancyGrass && iicon.iconName == "grass_side" && !rb.hasOverrideBlockTexture()) {
				tes.setColorOpaque_F(rz * r, gz * g, bz * b)
				rb.renderFaceZNeg(block, x.D, y.D, z.D, BlockGrass.getIconSideOverlay())
			}
			rb.field_152631_f = false
			flag = true
		}
		
		if (block.shouldSideBeRendered(rb.blockAccess, i, j, k + 1, 3)) {
			tes.setBrightness(l)
			tes.setColorOpaque_F(rz, gz, bz)
			iicon = rb.getBlockIcon(block, rb.blockAccess, i, j, k, 3)
			rb.renderFaceZPos(block, x.D, y.D, z.D, iicon)
			if (RenderBlocks.fancyGrass && iicon.iconName == "grass_side" && !rb.hasOverrideBlockTexture()) {
				tes.setColorOpaque_F(rz * r, gz * g, bz * b)
				rb.renderFaceZPos(block, x.D, y.D, z.D, BlockGrass.getIconSideOverlay())
			}
			flag = true
		}
		
		if (block.shouldSideBeRendered(rb.blockAccess, i - 1, j, k, 4)) {
			tes.setBrightness(l)
			tes.setColorOpaque_F(rx, gx, bx)
			iicon = rb.getBlockIcon(block, rb.blockAccess, i, j, k, 4)
			rb.renderFaceXNeg(block, x.D, y.D, z.D, iicon)
			if (RenderBlocks.fancyGrass && iicon.iconName == "grass_side" && !rb.hasOverrideBlockTexture()) {
				tes.setColorOpaque_F(rx * r, gx * g, bx * b)
				rb.renderFaceXNeg(block, x.D, y.D, z.D, BlockGrass.getIconSideOverlay())
			}
			flag = true
		}
		
		if (block.shouldSideBeRendered(rb.blockAccess, i + 1, j, k, 5)) {
			tes.setBrightness(l)
			tes.setColorOpaque_F(rx, gx, bx)
			iicon = rb.getBlockIcon(block, rb.blockAccess, i, j, k, 5)
			rb.field_152631_f = true
			rb.renderFaceXPos(block, x.D, y.D, z.D, iicon)
			if (RenderBlocks.fancyGrass && iicon.iconName == "grass_side" && !rb.hasOverrideBlockTexture()) {
				tes.setColorOpaque_F(rx * r, gx * g, bx * b)
				rb.renderFaceXPos(block, x.D, y.D, z.D, BlockGrass.getIconSideOverlay())
			}
			rb.field_152631_f = false
			flag = true
		}
		
		return flag
	}
}

private class CompositionWorld(val original: IBlockAccess, val composition: Array<Array<Array<Pair<Block, Int>?>>>): IBlockAccess {
	
	private operator fun Array<Array<Array<Pair<Block, Int>?>>>.get(i: Int, j: Int, k: Int) = composition.getOrNull(i)?.getOrNull(j)?.getOrNull(k)
	
	override fun getBlock(x: Int, y: Int, z: Int) = composition[x, y, z]?.first ?: Blocks.air
	override fun getBlockMetadata(x: Int, y: Int, z: Int) = composition[x, y, z]?.second ?: 0
	override fun getTileEntity(x: Int, y: Int, z: Int) = null
	override fun isAirBlock(x: Int, y: Int, z: Int) = composition[x, y, z] == null
	
	override fun getLightBrightnessForSkyBlocks(x: Int, y: Int, z: Int, lightValue: Int) = original.getLightBrightnessForSkyBlocks(x, y, z, lightValue)
	override fun isBlockProvidingPowerTo(x: Int, y: Int, z: Int, side: Int) = original.isBlockProvidingPowerTo(x, y, z, side)
	override fun getBiomeGenForCoords(x: Int, z: Int) = original.getBiomeGenForCoords(x, z)
	override fun getHeight() = original.height
	override fun extendedLevelsInChunkCache() = original.extendedLevelsInChunkCache()
	override fun isSideSolid(x: Int, y: Int, z: Int, side: ForgeDirection?, default: Boolean) = original.isSideSolid(x, y, z, side, default)
}