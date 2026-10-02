package alfheim.client.render.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.TileItemContainer
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.common.block.BlockItemFrame
import alfheim.common.block.tile.TileItemFrame
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.*
import net.minecraft.entity.item.EntityItemFrame
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.tileentity.TileEntity
import org.lwjgl.opengl.GL11.*

object RenderTileItemFrame: TileEntitySpecialRenderer() {
	
	val icon by lazy { (RenderManager.instance.entityRenderMap[EntityItemFrame::class.java] as RenderItemFrame).field_94147_f!! } 
	
	override fun renderTileEntityAt(tile: TileEntity, x: Double, y: Double, z: Double, ticks: Float) {
		if (tile !is TileItemFrame) return
		
		glPushMatrix()
		glTranslated(x + 0.5, y + 0.5, z + 0.5)
		glRotatef(-90f, 0f, 1f, 0f) // какая шваль ебливая это в renderBlockAsItem добавила? 
		
		val d = 1.0 / 16
		
		val aabbs = BlockItemFrame.aabbs(0, 0, 0)
		for ((id, frame) in tile.frames.withIndex()) {
			frame ?: continue
			
			if (!frame.invis || frame.item === null) {
				if (frame.invis) {
					ASJRenderHelper.setBlend()
					glColor4f(1f, 1f, 1f, 0.25f)
					renderBlocks.useInventoryTint = false
				}
				
				val aabb = aabbs[id]
				mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture)
				
				renderBlocks.blockAccess = tile.worldObj
				
				renderBlocks.overrideBlockBounds(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ)
				glPushMatrix()
				renderBlocks.renderBlockAsItem(Blocks.planks, 2, 1.0f)
				glPopMatrix()
				
				renderBlocks.setOverrideBlockTexture(icon)
				renderBlocks.overrideBlockBounds(
					aabb.minX + if (id == 4 || id == 5) -0.001 else d,
					aabb.minY + if (id == 0 || id == 1) -0.001 else d,
					aabb.minZ + if (id == 2 || id == 3) -0.001 else d,
					aabb.maxX - if (id == 4 || id == 5) -0.001 else d,
					aabb.maxY - if (id == 0 || id == 1) -0.001 else d,
					aabb.maxZ - if (id == 2 || id == 3) -0.001 else d)
				glPushMatrix()
				renderBlocks.renderBlockAsItem(Blocks.planks, 2, 1.0f)
				glPopMatrix()
				renderBlocks.clearOverrideBlockTexture()
				
				if (frame.invis) {
					ASJRenderHelper.discard()
					glColor4f(1f, 1f, 1f, 1f)
					renderBlocks.useInventoryTint = true
				}
			}
			
			val stack = frame.item?.copy() ?: continue
			stack.stackSize = 1
			
			glPushMatrix()
			when (id) {
				0 -> glRotatef(-90f, 1f, 0f, 0f)
				1 -> glRotatef(90f, 1f, 0f, 0f)
				2 -> glRotatef(90f, 0f, 1f, 0f)
				3 -> glRotatef(-90f, 0f, 1f, 0f)
				4 -> glRotatef(180f, 0f, 1f, 0f)
			}
			
			glTranslatef(0f, 0f, -0.43125f - if (frame.invis) 0.046875f else 0f)
			glRotatef(-90f * frame.rotation, 0f, 0f, 1f)
			if (stack.item is ItemBlock) glTranslatef(0f, 3/32f, 0f)
			
			TileItemContainer.renderItem(tile, stack)
			glPopMatrix()
		}
		
		renderBlocks.unlockBlockBounds()
		renderBlocks.clearOverrideBlockTexture()
		
		glPopMatrix()
	}
}
