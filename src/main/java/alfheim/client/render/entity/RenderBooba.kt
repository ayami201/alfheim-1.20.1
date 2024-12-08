package alfheim.client.render.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.ModInfo
import alfheim.api.entity.raceID
import alfheim.api.lib.LibResourceLocations
import alfheim.client.core.handler.CardinalSystemClient
import alfheim.common.core.handler.AlfheimConfigHandler
import cpw.mods.fml.relauncher.*
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.entity.RenderBiped
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.AdvancedModelLoader
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12
import vazkii.botania.api.item.IBaubleRender
import java.util.*
import kotlin.math.*

object RenderBooba {
	
	val model = if (AlfheimConfigHandler.minimalGraphics) null else AdvancedModelLoader.loadModel(ResourceLocation(ModInfo.MODID, "model/booba.obj"))
	// flugel boobas
	val model1 = if (AlfheimConfigHandler.minimalGraphics) null else AdvancedModelLoader.loadModel(ResourceLocation(ModInfo.MODID, "model/booba_1.obj"))
	val model2 = if (AlfheimConfigHandler.minimalGraphics) null else AdvancedModelLoader.loadModel(ResourceLocation(ModInfo.MODID, "model/booba_2.obj"))
	
	@SideOnly(Side.CLIENT)
	fun render(player: EntityPlayer) {
		if (!AlfheimConfigHandler.renderBooba) return
		if (player !is AbstractClientPlayer) return
		if (!AlfheimConfigHandler.enableElvenStory) return
		val booba = model ?: return
		val skinData = CardinalSystemClient.playerSkinsData[player.commandSenderName]
		if (skinData?.first != true) return
		
		val invisible = player.isInvisible
		val transparent = invisible && !player.isInvisibleToPlayer(player)
		if (invisible && !transparent) return
		
		glPushMatrix()
		RenderHelper.disableStandardItemLighting()
		glEnable(GL_LIGHTING)
		glEnable(GL_LIGHT0)
		glEnable(GL_LIGHT1)
		glEnable(GL_COLOR_MATERIAL)
		glEnable(GL12.GL_RESCALE_NORMAL)
		glScaled(0.0625)
		glRotatef(180f, 0f, 1f, 0f)
		val offset = (Random(player.commandSenderName.hashCode().toLong()).nextFloat() * 0.5f - 0.5f) * 2f
		glTranslatef(0f, 4.1f, 1.9f + offset)
		glRotatef(180f, 0f, 0f, 1f)
		glRotatef(-Math.toDegrees(sin(sqrt(player.getSwingProgress(mc.timer.renderPartialTicks)) * Math.PI * 2) * 0.2).F, 0f, 1f, 0f)
		
		if (transparent) {
			glColor4f(1f, 1f, 1f, 0.15f)
			glDepthMask(false)
			glAlphaFunc(GL_GREATER, 0.003921569f)
			ASJRenderHelper.setBlend()
		} else {
			glColor4f(1f, 1f, 1f, 1f)
		}
		
		if (player.isSneaking) {
			IBaubleRender.Helper.applySneakingRotation()
			glTranslatef(0f, -1f, -0.5f)
		}
		
		val hurt = player.hurtTime > 0 || player.deathTime > 0
		
		fun hurt() {
			if (!hurt) return
			
			glDisable(GL_TEXTURE_2D)
			glDisable(GL_ALPHA_TEST)
			glEnable(GL_BLEND)
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
			glDepthFunc(GL_EQUAL)
			glColor4f(player.getBrightness(mc.timer.renderPartialTicks), 0.0f, 0.0f, 0.4f)
			
			booba.renderAll()
			
			glColor4f(1f, 1f, 1f, 1f)
			glDepthFunc(GL_LEQUAL)
			glDisable(GL_BLEND)
			glEnable(GL_ALPHA_TEST)
			glEnable(GL_TEXTURE_2D)
		}
		
		val id = player.raceID - 1
		mc.renderEngine.bindTexture(if (skinData.second && id in LibResourceLocations.oldFemale.indices) LibResourceLocations.oldFemale[id] else player.locationSkin)
		booba.renderAll()
		hurt()
		
		player.inventory.armorInventory[2]?.let {
			mc.renderEngine.bindTexture(RenderBiped.getArmorResource(player, it, 1, null))
			glScaled(1.1)
			booba.renderAll()
			hurt()
		}
		
		if (transparent) {
			ASJRenderHelper.discard()
			glAlphaFunc(GL_GREATER, 0.1f)
			glDepthMask(true)
			glColor4f(1f, 1f, 1f, 1f)
		}
		
		glPopMatrix()
	}
}
