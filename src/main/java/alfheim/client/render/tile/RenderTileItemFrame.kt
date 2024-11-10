package alfheim.client.render.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.TileItemContainer
import alfheim.common.block.BlockItemFrame
import alfheim.common.block.tile.TileItemFrame
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.tileentity.TileEntity
import org.lwjgl.opengl.GL11.*

object RenderTileItemFrame: TileEntitySpecialRenderer() {
	
	override fun renderTileEntityAt(tile: TileEntity, x: Double, y: Double, z: Double, ticks: Float) {
		if (tile !is TileItemFrame) return
		
		glPushMatrix()
		glTranslated(x + 0.5, y + 0.5, z + 0.5)
		glRotatef(-90f, 0f, 1f, 0f) // какая шваль ебливая это в renderBlockAsItem добавила? 
		
		val aabbs = BlockItemFrame.aabbs(0, 0, 0)
		for ((id, frame) in tile.frames.withIndex()) {
			frame ?: continue
			
			val aabb = aabbs[id]
			mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture)
			
			renderBlocks.blockAccess = tile.worldObj
			renderBlocks.setOverrideBlockTexture(Blocks.planks.getIcon(1, 2))
			renderBlocks.overrideBlockBounds(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ)
			glPushMatrix()
			renderBlocks.renderBlockAsItem(Blocks.planks, 0, 1.0f)
			glPopMatrix()
			
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
			
			glTranslatef(0f, 0f, -0.43125f)
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
