package alfheim.client.render.world

import alexsocol.asjlib.*
import alexsocol.asjlib.render.*
import alexsocol.patcher.helper.OFHelper
import alfheim.api.lib.LibShaderIDs
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.WorldClient
import net.minecraftforge.client.IRenderHandler
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11.*
import java.awt.Color

open class SkyRendererDomains(val colorsProvider: () -> Pair<UInt, UInt>): IRenderHandler() {
	
	constructor(color: UInt, secondaryColor: UInt): this({ color to secondaryColor })
	
	override fun render(partialTicks: Float, world: WorldClient?, mc: Minecraft) {
		val dist = mc.gameSettings.renderDistanceChunks
		var size = dist - 1
		
		if (OFHelper.optifine) {
			if (dist > 5)
				size -= 1

			if (dist > 11)
				size -= 1

			if (dist > 17)
				size -= 1

			if (dist > 22)
				size -= 1

			if (dist > 28)
				size -= 1
			
			if (dist > 32)
				size -= 1
		}
		
		glPushMatrix()
		glScalef(-size.F)
		
		glEnable(GL12.GL_RESCALE_NORMAL)
		
		glDisable(GL_LIGHTING)
		glDisable(GL_TEXTURE_2D)
		
		val (col1, col2) = colorsProvider()
		
		ASJRenderHelper.glColor1u(col1)

		if (RenderPostShaders.allowShaders)
			ASJShaderHelper.useShader(LibShaderIDs.idNoise) {
				val (r, g, b, a) = Color(col2.toInt()).getRGBComponents(null)
				GL20.glUniform4f(GL20.glGetUniformLocation(it, "color2"), r, g, b, a)
			}
		
		SpellVisualizations.renderSphere(32.0)
		
		if (RenderPostShaders.allowShaders) ASJShaderHelper.releaseShader()
		
		glColor4f(1f, 1f, 1f, 1f)
		glEnable(GL_TEXTURE_2D)
		glEnable(GL_LIGHTING)
		
		glDisable(GL12.GL_RESCALE_NORMAL)
		
		glPopMatrix()
	}
}
