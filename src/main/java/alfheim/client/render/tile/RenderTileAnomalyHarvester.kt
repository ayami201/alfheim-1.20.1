package alfheim.client.render.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.ModInfo
import alfheim.api.lib.LibResourceLocations
import alfheim.client.model.block.ModelSpreaderFrame
import alfheim.common.block.tile.TileAnomalyHarvester
import alfheim.common.core.handler.AlfheimConfigHandler
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.AdvancedModelLoader
import org.lwjgl.opengl.GL11.*

object RenderTileAnomalyHarvester: TileEntitySpecialRenderer() {
	
	val model = if (AlfheimConfigHandler.minimalGraphics) null else AdvancedModelLoader.loadModel(ResourceLocation(ModInfo.MODID, "model/Harvester.obj"))!!
	
	override fun renderTileEntityAt(tile: TileEntity?, x: Double, y: Double, z: Double, ticks: Float) {
		tile as TileAnomalyHarvester
		
		glPushMatrix()
		glTranslated(x + 0.5, y + 0.5, z + 0.5)
		
		val meta = tile.worldObj?.getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord) ?: 0
		
		if (model == null) {
			when (meta) {
				0 -> glRotatef(90f, 1f, 0f, 0f)
				1 -> glRotatef(-90f, 1f, 0f, 0f)
				2 -> glRotatef(180f, 0f, 1f, 0f)
				3 -> Unit
				4 -> glRotatef(-90f, 0f, 1f, 0f)
				5 -> glRotatef(90f, 0f, 1f, 0f)
			}
			
			glTranslatef(0f, -1f, 0f)
			mc.renderEngine.bindTexture(LibResourceLocations.spreaderMaufFrame)
			ModelSpreaderFrame.render()
		} else {
			when (meta) {
				0 -> Unit
				1 -> glRotatef(180f, 0f, 0f, 1f)
				2 -> glRotatef(90f, 1f, 0f, 0f)
				3 -> glRotatef(-90f, 1f, 0f, 0f)
				4 -> glRotatef(-90f, 0f, 0f, 1f)
				5 -> glRotatef(90f, 0f, 0f, 1f)
			}
			
			mc.renderEngine.bindTexture(LibResourceLocations.harvester)
			
			model.renderPart("cone")
			val t = if (tile.prevAnimationTicks == tile.animationTicks) tile.animationTicks.F else ASJRenderHelper.interpolate(tile.prevAnimationTicks.D, tile.animationTicks.D).F
			val t0 = t * 2f
			val t1 = t * 4f
			val t2 = t * 6f
			val t3 = t * 8f
			glRotatef(t0 + 90, 0f, 0f, 1f)
			model.renderPart("tube3")
			glRotatef(t1 + 90, 1f, 0f, 0f)
			model.renderPart("tube2")
			glRotatef(t2 + 90, 0f, 0f, 1f)
			model.renderPart("tube1")
			glRotatef(180f, 0f, 0f, 1f)
			glRotatef(t3, 0f, 1f, 0f)
			model.renderPart("cube")
		}
		
		glPopMatrix()
	}
}