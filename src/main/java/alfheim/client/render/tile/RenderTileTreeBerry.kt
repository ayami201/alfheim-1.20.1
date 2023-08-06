package alfheim.client.render.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper.discard
import alexsocol.asjlib.render.ASJRenderHelper.setTwoside
import alfheim.api.ModInfo
import alfheim.common.block.BlockTreeBerry
import alfheim.common.block.tile.TileTreeBerry
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.*
import org.lwjgl.opengl.GL11.*

object RenderTileTreeBerry: TileEntitySpecialRenderer() {
	
	val textures = HashMap<Int, Array<ResourceLocation>>()
	val models = HashMap<Int, Array<IModelCustom?>>()
	
	override fun renderTileEntityAt(tile: TileEntity, x: Double, y: Double, z: Double, ticks: Float) {
		if (tile !is TileTreeBerry) return
		
		val type = tile.type
		if (BlockTreeBerry.hasModelErrors[type] == true) return
		
		val meta = tile.worldObj.getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord)
		
		glPushMatrix()
		setTwoside()
		glTranslated(x + 0.5, y, z + 0.5)
		
		mc.renderEngine.bindTexture(textures.computeIfAbsent(type) {
			Array(3) { ResourceLocation(ModInfo.MODID, "textures/blocks/TreeBerry$type$it.png") }
		}.safeGet(meta))
		
		models.computeIfAbsent(type) {
			try {
				Array (3) { AdvancedModelLoader.loadModel(ResourceLocation(ModInfo.MODID, "model/TreeBerry$type$it.obj")) }
			} catch (e: Throwable) {
				ASJUtilities.error("Error loading berry model for $type'th set. It will be flat. Reason:", e)
				BlockTreeBerry.hasModelErrors[type] = true
				emptyArray()
			}
		}.safeZeroGet(meta)?.renderAll()
		
		discard()
		glPopMatrix()
	}
}
