package alfheim.client.render.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.common.floatingisland.EntityFloatingIsland
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.TickEvent
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.entity.Entity
import org.lwjgl.opengl.GL11.*

object RenderEntityFloatingIsland: Render() {
	
	override fun doRender(entity: Entity?, _x: Double, _y: Double, _z: Double, yaw: Float, ticks: Float) {
		if (entity !is EntityFloatingIsland) return
		
		val access = entity.blockAccess
		val renderblocks = RenderBlocks(access)
		
		glPushMatrix()
		glTranslated(_x - 0.5, _y, _z - 0.5)
		glDisable(GL_LIGHTING)
		ASJRenderHelper.setBlend()
		
		mc.renderEngine.bindTexture(getEntityTexture(entity))
		
		val tes = Tessellator.instance
		if (entity.glCallList !in callLists) {
			tes.startDrawingQuads()
			
			for (renderPass in 0..1) {
				var needMorePasses = false
				
				for (y in access.startY..access.endY) {
					for (z in access.startZ..access.endZ) {
						for (x in access.startX..access.endX) {
							val block = access.getBlock(x, y, z)
							
							if (block.isAir(access, x, y, z)) continue
							
							if (block.renderBlockPass > renderPass) needMorePasses = true
							
							if (!block.canRenderInPass(renderPass)) continue
							
							renderblocks.renderBlockByRenderType(block, x, y, z)
						}
					}
				}
				
				if (!needMorePasses) {
					break
				}
			}
			
			entity.glCallList = GLAllocation.generateDisplayLists(1)
			
			glNewList(entity.glCallList, GL_COMPILE)
			tes.draw()
			glEndList()
		} else {
			glCallList(entity.glCallList)
		}
		
		callLists[entity.glCallList] = 1200
		
		glEnable(GL_LIGHTING)
		ASJRenderHelper.discard()
		
		access.tileList.values.forEach {
			try {
				it.worldObj = mc.theWorld
				TileEntityRendererDispatcher.instance.renderTileEntityAt(it, it.xCoord.D, it.yCoord.D, it.zCoord.D, ticks)
				it.worldObj = null
			} catch (e: Throwable) {
				ASJUtilities.error("Exception during rendering tile entity ${it.javaClass} at ${it.xCoord} ${it.yCoord} ${it.zCoord}", e)
			}
		}
		
		glPopMatrix()
	}
	
	override fun getEntityTexture(entity: Entity?) = TextureMap.locationBlocksTexture!!
	
	val callLists = HashMap<Int, Int>()
	
	@SubscribeEvent
	fun onClientTick(e: ClientTickEvent) {
		if (e.phase != TickEvent.Phase.END) return
		
		callLists.entries.iterator().onEach { (k, v) ->
			val timer = v - 1
			callLists[k] = timer
			if (timer >= 0) return@onEach
			remove()
			GLAllocation.deleteDisplayLists(k)
		}
	}
}