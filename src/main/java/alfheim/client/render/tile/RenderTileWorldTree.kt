package alfheim.client.render.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.ModInfo
import alfheim.api.lib.LibResourceLocations
import alfheim.common.block.tile.TileWorldTree
import alfheim.common.core.handler.AlfheimConfigHandler
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.AdvancedModelLoader
import org.lwjgl.opengl.GL11.*
import java.awt.Color

object RenderTileWorldTree: TileEntitySpecialRenderer() {
	
	val model = if (AlfheimConfigHandler.minimalGraphics) null else AdvancedModelLoader.loadModel(ResourceLocation(ModInfo.MODID, "model/WorldTree.obj"))
	var forcedApples = false
	
	override fun renderTileEntityAt(tile: TileEntity?, x: Double, y: Double, z: Double, ticks: Float) {
		if (tile !is TileWorldTree || model == null) return
		
		glPushMatrix()
		glTranslated(x + 0.5, y, z + 0.5)
		
		mc.renderEngine.bindTexture(LibResourceLocations.worldTree)
		model.renderPart("tree")
		
		for ((id, it) in tile.boundList.withIndex()) {
			if (!forcedApples && it == null) continue
			
			ASJRenderHelper.glColor1u(ASJRenderHelper.addAlpha(Color.HSBtoRGB(id * (360 / 16f) / 360f, 1f, 1f), 255))
			model.renderPart("apple$id")
		}
		
		if (tile.name.isNotBlank()) {
			glTranslated(-0.5, 0.0, -0.5)
			
			glScalef(1 / 64f)
			glRotatef(180f, 1f, 0f, 0f)
			glTranslatef(32f, -8f, -52.1f)
			val w = mc.fontRenderer.getStringWidth(tile.name)
			
			mc.fontRenderer.drawString(tile.name, w / -2, 0, 0xFFD400)
			
			glRotatef(180f, 0f, 1f, 0f)
			glTranslatef(0f, 0f, -40.2f)
			mc.fontRenderer.drawString(tile.name, w / -2, 0, 0xFFD400)
			
			glRotatef(90f, 0f, 1f, 0f)
			glTranslatef(-20.1f, 0f, -20.1f)
			mc.fontRenderer.drawString(tile.name, w / -2, 0, 0xFFD400)
			
			glRotatef(180f, 0f, 1f, 0f)
			glTranslatef(0f, 0f, -40.2f)
			mc.fontRenderer.drawString(tile.name, w / -2, 0, 0xFFD400)
		}
		
		glColor4f(1f, 1f, 1f, 1f)
		glPopMatrix()
	}
}
