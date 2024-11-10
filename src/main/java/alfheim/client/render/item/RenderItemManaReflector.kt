package alfheim.client.render.item

import alexsocol.asjlib.mc
import alfheim.api.lib.LibResourceLocations
import alfheim.client.model.block.ModelManaReflector
import net.minecraft.item.ItemStack
import net.minecraftforge.client.IItemRenderer
import org.lwjgl.opengl.GL11.*

object RenderItemManaReflector: IItemRenderer {
	
	override fun handleRenderType(item: ItemStack?, type: IItemRenderer.ItemRenderType) = true
	
	override fun shouldUseRenderHelper(type: IItemRenderer.ItemRenderType, item: ItemStack, helper: IItemRenderer.ItemRendererHelper) = true
	
	override fun renderItem(type: IItemRenderer.ItemRenderType, item: ItemStack?, vararg data: Any?) {
		glPushMatrix()
		
		when (type) {
			IItemRenderer.ItemRenderType.INVENTORY -> {
				glRotatef(-90f, 0f, 1f, 0f)
				glTranslatef(0f, -0.1f, -1f)
			}
			IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON,
			IItemRenderer.ItemRenderType.EQUIPPED -> {
				glRotatef(90f, 0f, 1f, 0f)
				glTranslatef(-1f, 0f, 0f)
			}
			IItemRenderer.ItemRenderType.ENTITY -> {
				glRotatef(-90f, 0f, 1f, 0f)
				glTranslatef(-0.5f, -0.5f, -0.5f)
			}
			else -> Unit
		}
		
		mc.renderEngine.bindTexture(LibResourceLocations.manaReflector)
		ModelManaReflector.render(0.0625f)
		
		glPopMatrix()
	}
}
