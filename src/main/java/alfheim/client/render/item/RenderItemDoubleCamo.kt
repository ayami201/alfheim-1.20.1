package alfheim.client.render.item

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.render.ASJRenderHelper.discard
import alexsocol.asjlib.render.ASJRenderHelper.setBlend
import alfheim.client.render.block.RenderBlockDoubleCamo
import alfheim.common.block.tile.TileDoubleCamo
import cpw.mods.fml.client.registry.RenderingRegistry
import net.minecraft.client.renderer.Tessellator
import net.minecraft.item.ItemStack
import net.minecraftforge.client.*
import net.minecraftforge.client.IItemRenderer.ItemRenderType.*
import org.lwjgl.opengl.GL11.*

// shitty generic hacks
inline fun <reified T: TileDoubleCamo> RenderItemDoubleCamo(renderId: Int) = RenderItemDoubleCamo(renderId, T::class.java::newInstance)

class RenderItemDoubleCamo(val renderId: Int, val getTile: () -> TileDoubleCamo): IItemRenderer {
	
	@Suppress("DEPRECATION")
	override fun renderItem(type: IItemRenderer.ItemRenderType?, stack: ItemStack, vararg data: Any?) {
		val render = RenderingRegistry.instance().blockRenderers[renderId] as? RenderBlockDoubleCamo ?: return
		
		val rb = mc.renderGlobal.renderBlocksRg
		if (rb.blockAccess == null) rb.blockAccess = mc.theWorld
		
		glPushMatrix()
		setBlend()
		glDisable(GL_LIGHTING)
		
		if (type == EQUIPPED_FIRST_PERSON) {
			glRotatef(-90f, 0f, 1f, 0f)
			glTranslated(0.0, 0.5, -0.5)
		}
		val (x, y, z) = intArrayOf(0, 300, 0)
		glTranslatef(-x.F, -y.F, -z.F)
		if (type == ENTITY || type == INVENTORY) glTranslated(-0.5)
		
		val prevPass = ForgeHooksClient.getWorldRenderPass()
		
		val tile = getTile()
		stack.tagCompound?.let { tile.readCustomNBT(it) }
		
		for (i in 0..1) {
			ForgeHooksClient.worldRenderPass = i
			Tessellator.instance.startDrawingQuads()
			try_ {
				render.renderBlock(mc.theWorld, rb, x, y, z, stack.meta, tile)
			}
			Tessellator.instance.draw()
		}
		
		ForgeHooksClient.worldRenderPass = prevPass
		glEnable(GL_LIGHTING)
		discard()
		glPopMatrix()
	}
	
	override fun handleRenderType(item: ItemStack?, type: IItemRenderer.ItemRenderType?) = true
	
	override fun shouldUseRenderHelper(type: IItemRenderer.ItemRenderType?, item: ItemStack?, helper: IItemRenderer.ItemRendererHelper?) = true
}
