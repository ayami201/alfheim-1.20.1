package alfheim.client.render.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.ModInfo
import alfheim.common.block.tile.TileIcyGeyser
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.*

object RenderTileIcyGeyser: TileEntitySpecialRenderer() {
	
	val textures = HashMap<Triple<Int, Int, Int>, ResourceLocation>()
	
	override fun renderTileEntityAt(tile: TileEntity?, x: Double, y: Double, z: Double, ticks: Float) {
		if (tile !is TileIcyGeyser) return
		
		val small = tile.cooldown in 450..900 || tile.cooldown in (tile.maxCD - 900)..(tile.maxCD - 450)
		val medium = tile.cooldown in 0..449 || tile.cooldown in (tile.maxCD - 449)..tile.maxCD
		val large = tile.timer > 0
		
		if (!small && !medium && !large) return
		
		val index = if (small) 1 else if (medium) 2 else 3
		
		glPushMatrix()
		glTranslated(x, y, z)
		ASJRenderHelper.setBlend()
		
		val size = if (small) 0 else 1
		val tes = Tessellator.instance
		
		for (i in -size..size)
			for (k in -size..size) {
				val block = tile.worldObj.getBlock(tile.xCoord + i, tile.yCoord, tile.zCoord + k)
				if (!block.isOpaqueCube || (block.material !== Material.ice && block.material !== Material.packedIce)) continue
				
				mc.renderEngine.bindTexture(textures.computeIfAbsent(index to i with k) {
					ResourceLocation(ModInfo.MODID, "textures/model/block/Crack$index${if (size == 0) "" else "$i$k"}.png")
				})
				
				tes.startDrawingQuads()
				tes.addVertexWithUV(i + 0.0, 1.0001, k + 0.0, 0.0, 0.0)
				tes.addVertexWithUV(i + 0.0, 1.0001, k + 1.0, 0.0, 1.0)
				tes.addVertexWithUV(i + 1.0, 1.0001, k + 1.0, 1.0, 1.0)
				tes.addVertexWithUV(i + 1.0, 1.0001, k + 0.0, 1.0, 0.0)
				tes.draw()
			}
		
		ASJRenderHelper.discard()
		glPopMatrix()
	}
}
