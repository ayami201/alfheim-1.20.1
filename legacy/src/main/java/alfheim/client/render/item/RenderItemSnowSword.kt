package alfheim.client.render.item

import alexsocol.asjlib.F
import alexsocol.asjlib.glScalef
import alexsocol.asjlib.glTranslated
import alexsocol.asjlib.mc
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.ModInfo
import alfheim.api.lib.LibResourceLocations
import alfheim.client.render.item.RenderEntityItemImmortal.RES_ITEM_GLINT
import alfheim.common.core.handler.AlfheimConfigHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.IItemRenderer
import net.minecraftforge.client.IItemRenderer.ItemRenderType
import net.minecraftforge.client.IItemRenderer.ItemRenderType.*
import net.minecraftforge.client.model.AdvancedModelLoader
import org.lwjgl.opengl.GL11.*
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.item.equipment.bauble.ItemIcePendant

object RenderItemSnowSword: IItemRenderer {
	
	val model = if (AlfheimConfigHandler.minimalGraphics) null else AdvancedModelLoader.loadModel(ResourceLocation(ModInfo.MODID, "model/Katana.obj"))
	
	override fun renderItem(type: ItemRenderType, stack: ItemStack, vararg data: Any?) {
		if (model == null) return // renderer won't be registered at all
		
		glPushMatrix()
		
		if (type == EQUIPPED_FIRST_PERSON || type == EQUIPPED) {
			glRotatef(135f, 0f, 1f, 0f)
			glTranslatef(-0.7f, 0.4f, -0.15f)
			glRotatef(if (type == EQUIPPED_FIRST_PERSON) -5f else -15f, 1f, 0f, 0f)
			
			data.firstOrNull { it is EntityPlayer }?.let {
				it as EntityPlayer
				if (it.isBlocking) {
					glRotatef(-90f, 0f, 1f, 0f)
					glTranslated(-0.1, -0.2, -0.15)
				}
			}
		}
		
		if (type == INVENTORY) {
			glRotatef(-45f, 1f, 1f, 1f)
			glTranslatef(0f, -1f, 0f)
			alexsocol.asjlib.glScaled(0.625)
		}
		
		val maru = stack.displayName.trim().equals("chunchunmaru", true)
		
		mc.renderEngine.bindTexture(if (maru) LibResourceLocations.snowKatana else LibResourceLocations.snowSword)
		
		if (maru) {
			glPushMatrix()
			glScalef(1f, 0.75f, 1f)
			glTranslatef(0f, 0.15f, 0f)
		}
		model.renderAll()
		
		if (stack.hasEffect(0)) {
			mc.renderEngine.bindTexture(RES_ITEM_GLINT)
			glDepthFunc(GL_EQUAL)
			glDisable(GL_LIGHTING)
			glEnable(GL_BLEND)
			glBlendFunc(GL_SRC_COLOR, GL_ONE)
			val f11 = 0.76f
			glColor4f(0.5f * f11, 0.25f * f11, 0.8f * f11, 1f)
			glMatrixMode(GL_TEXTURE)
			glPushMatrix()
			val f12 = 0.125f
			glScalef(f12)
			var f13 = (Minecraft.getSystemTime() % 3000L).F / 3000f * 8f
			glTranslatef(f13, 0f, 0f)
			glRotatef(-50f, 0f, 0f, 1f)
			model.renderAll()
			glPopMatrix()
			glPushMatrix()
			glScalef(f12)
			f13 = (Minecraft.getSystemTime() % 4873L).F / 4873f * 8f
			glTranslatef(-f13, 0f, 0f)
			glRotatef(10f, 0f, 0f, 1f)
			model.renderAll()
			glPopMatrix()
			glMatrixMode(GL_MODELVIEW)
			glDisable(GL_BLEND)
			glEnable(GL_LIGHTING)
			glDepthFunc(GL_LEQUAL)
			glColor4f(1f, 1f, 1f, 1f)
		}
		
		if (maru)
			glPopMatrix()
		else {
			glRotatef(90f, 1f, 0f, 0f)
			glRotatef(45f, 0f, 0f, 1f)
			glTranslated(-0.475)
			
			if (type == INVENTORY) ASJRenderHelper.setBlend()
			
			val icon = (ModItems.icePendant as ItemIcePendant).gemIcon
			mc.renderEngine.bindTexture(TextureMap.locationItemsTexture)
			ItemRenderer.renderItemIn2D(Tessellator.instance, icon.maxU, icon.minV, icon.minU, icon.maxV, icon.iconWidth, icon.iconHeight, 1f / 16f)
			
			if (type == INVENTORY) ASJRenderHelper.discard()
		}
		
		glPopMatrix()
	}
	
	override fun handleRenderType(item: ItemStack?, type: ItemRenderType?) = true
	override fun shouldUseRenderHelper(type: ItemRenderType?, item: ItemStack?, helper: IItemRenderer.ItemRendererHelper?) = helper != IItemRenderer.ItemRendererHelper.BLOCK_3D
}