package alfheim.client.render.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.common.block.tile.TileFloodLight
import alfheim.common.core.handler.AlfheimConfigHandler
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3
import kotlin.math.*

object RenderTileFloodLight: TileEntitySpecialRenderer() {
	
	override fun renderTileEntityAt(tile: TileEntity, x: Double, y: Double, z: Double, partialTicks: Float) {
		if (tile !is TileFloodLight || !tile.redstone) return
		
		val world = tile.worldObj ?: mc.theWorld ?: return
		if (world.getBlock(tile.xCoord, tile.yCoord - 1, tile.zCoord)?.isOpaqueCube == true) return
		
		glPushMatrix()
		ASJRenderHelper.interpolatedTranslationReverse(mc.renderViewEntity)
		ASJRenderHelper.setGlow()
		glAlphaFunc(GL_GREATER, 0f)
		glDisable(GL_TEXTURE_2D)
		glDisable(GL_CULL_FACE)
		glEnable(GL_BLEND)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE)
		glDepthMask(false)
		
		val tes = Tessellator.instance
		tes.startDrawing(GL_TRIANGLE_FAN)
		
		val (r, g, b) = Color(0xFFFFCC).getRGBColorComponents(null)
		tes.setColorRGBA_F(r, g, b, 0.2f)
		
		var target = tile.target
		
		val pos = Vector3.fromTileEntity(tile).add(0.5, -0.1, 0.5)
		var tp = target?.let {
			val i = ASJRenderHelper.interpolate(it.prevPosX, it.posX)
			val j = ASJRenderHelper.interpolate(it.prevPosY - if (mc.thePlayer === it) 1.62 else 0.0, it.posY - if (mc.thePlayer === it) 1.62 else 0.0)
			val k = ASJRenderHelper.interpolate(it.prevPosZ, it.posZ)
			Vector3(i, j, k)
		}
		
		val radius = if (target != null) {
			val dist = Vector3.vecDistance(pos, tp!!)
			if (dist > 160 || tp.y > pos.y) {
				target = null
				tp = null
				12.0
			} else cbrt(dist) / 2
		} else 12.0
		
		val step = AlfheimConfigHandler.floodLightQuality
		tes.addVertex(pos.x, pos.y + 1, pos.z)
		
		for (deg in 0..360 step step) {
			val angle = Math.toRadians(deg.D)
			
			val i = cos(angle) * radius
			val j = if (tp != null) tp.y - pos.y else -160.0
			val k = sin(angle) * radius
			
			val dest = pos.copy().add(i, j, k)
			if (tp != null) dest.add(Vector3(tp.x, 0, tp.z).sub(pos.x, 0, pos.z))
			val hit = world.func_147447_a(pos.toVec3(), dest.toVec3(), false, false, target == null)?.hitVec ?: dest.toVec3()
			
			tes.addVertex(hit.xCoord, hit.yCoord, hit.zCoord)
		}
		
		tes.draw()
		
		glDepthMask(true)
		glDisable(GL_BLEND)
		glEnable(GL_CULL_FACE)
		glEnable(GL_LIGHTING)
		glEnable(GL_TEXTURE_2D)
		glAlphaFunc(GL_GREATER, 0.1f)
		ASJRenderHelper.discard()
		glPopMatrix()
	}
}