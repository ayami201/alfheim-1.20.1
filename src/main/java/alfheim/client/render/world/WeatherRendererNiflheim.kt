package alfheim.client.render.world

import alexsocol.asjlib.*
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.client.renderer.*
import net.minecraft.util.MathHelper
import net.minecraftforge.client.IRenderHandler
import org.lwjgl.opengl.GL11.*
import kotlin.math.sqrt

object WeatherRendererNiflheim: IRenderHandler() {
	
	override fun render(partialTicks: Float, world: WorldClient, mc: Minecraft) {
		mc.entityRenderer.enableLightmap(partialTicks.D)
		
		// the fuck is that ???
		if (mc.entityRenderer.rainXCoords == null) {
			mc.entityRenderer.rainXCoords = FloatArray(1024)
			mc.entityRenderer.rainYCoords = FloatArray(1024)
			
			for (i in 0..31) {
				for (j in 0..31) {
					val f2 = (j - 16).F
					val f3 = (i - 16).F
					val f4 = sqrt(f2 * f2 + f3 * f3)
					mc.entityRenderer.rainXCoords[i shl 5 or j] = -f3 / f4
					mc.entityRenderer.rainYCoords[i shl 5 or j] = f2 / f4
				}
			}
		}
		
		val entity = mc.renderViewEntity
		val k2 = entity.posX.mfloor()
		val l2 = entity.posY.mfloor()
		val i3 = entity.posZ.mfloor()
		val tes = Tessellator.instance
		glDisable(GL_CULL_FACE)
		glNormal3f(0f, 1f, 0f)
		glAlphaFunc(GL_GREATER, 0.1f)
		val d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks.D
		val d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks.D
		val d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks.D
		val k = MathHelper.floor_double(d1)
		val b0 = if (mc.gameSettings.fancyGraphics) 10 else 5
		var b1 = -1
		val f5 = (mc.entityRenderer.rendererUpdateCount + partialTicks) * 8f
		
		glColor4f(1f, 1f, 1f, 1f)
		
		for (l in i3 - b0..i3 + b0) {
			for (i1 in k2 - b0..k2 + b0) {
				val j1 = (l - i3 + 16) * 32 + i1 - k2 + 16
				val f6 = mc.entityRenderer.rainXCoords[j1] * 0.5
				val f7 = mc.entityRenderer.rainYCoords[j1] * 0.5
				val k1 = world.getPrecipitationHeight(i1, l)
				var l1 = l2 - b0
				var i2 = l2 + b0
				if (l1 < k1)
					l1 = k1
				
				if (i2 < k1)
					i2 = k1
				
				var j2 = k1
				if (k1 < k)
					j2 = k
				
				if (l1 == i2) continue
				
				mc.entityRenderer.random.setSeed(i1 * i1 * 3121L + i1 * 45238971L xor l * l * 418711L + l * 13761L)
				
				if (b1 != 1) {
					b1 = 1
					mc.textureManager.bindTexture(EntityRenderer.locationSnowPng)
					tes.startDrawingQuads()
				}
				
				val f10 = ((mc.entityRenderer.rendererUpdateCount and 511) + partialTicks) / 64.0 + mc.entityRenderer.random.nextDouble()
				val f16 = mc.entityRenderer.random.nextDouble() + f5 * 0.015 * mc.entityRenderer.random.nextDouble().let { if (it < 0.5) -1 + it else it }
				tes.setBrightness((world.getLightBrightnessForSkyBlocks(i1, j2, l, 0) * 3 + 15728880) / 4)
				tes.setTranslation(-d0, -d1, -d2)
				tes.addVertexWithUV(i1 - f6 + 0.5, l1.D, l - f7 + 0.5, 0f + f16, l1 / 4.0 + f10)
				tes.addVertexWithUV(i1 + f6 + 0.5, l1.D, l + f7 + 0.5, 1f + f16, l1 / 4.0 + f10)
				tes.addVertexWithUV(i1 + f6 + 0.5, i2.D, l + f7 + 0.5, 1f + f16, i2 / 4.0 + f10)
				tes.addVertexWithUV(i1 - f6 + 0.5, i2.D, l - f7 + 0.5, 0f + f16, i2 / 4.0 + f10)
				tes.setTranslation(0.0, 0.0, 0.0)
			}
		}
		
		if (b1 >= 0)
			tes.draw()
		
		glEnable(GL_CULL_FACE)
		glAlphaFunc(GL_GREATER, 0.1f)
		mc.entityRenderer.disableLightmap(partialTicks.D)
	}
}