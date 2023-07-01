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
import net.minecraft.client.renderer.entity.RenderBiped
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.AdvancedModelLoader
import org.lwjgl.opengl.GL11.*
import vazkii.botania.api.item.IBaubleRender

object RenderBooba {
	
	val model = if (AlfheimConfigHandler.minimalGraphics) null else AdvancedModelLoader.loadModel(ResourceLocation(ModInfo.MODID, "model/booba.obj"))
	
	@SideOnly(Side.CLIENT)
	fun render(player: EntityPlayer) {
		if (player !is AbstractClientPlayer) return
		if (!AlfheimConfigHandler.enableElvenStory) return
		val booba = model ?: return
		val skinData = CardinalSystemClient.playerSkinsData[player.commandSenderName]
		if (skinData?.first != true) return
		
		val invisible = player.isInvisible
		val transparet = invisible && !player.isInvisibleToPlayer(mc.thePlayer)
		if (invisible && !transparet) return
		
		glPushMatrix()
		glScaled(0.0625)
		glRotatef(180f, 0f, 1f, 0f)
		glTranslatef(0f, 2.7f, 1.9f)
		glRotatef(180f, 0f, 0f, 1f)
		
		if (transparet) {
			glColor4f(1f, 1f, 1f, 0.15f)
			glDepthMask(false)
			glAlphaFunc(GL_GREATER, 0.003921569f)
			ASJRenderHelper.setBlend()
		}
		
		if (player.isSneaking) {
			IBaubleRender.Helper.applySneakingRotation()
			glTranslatef(0f, -1f, -0.5f)
		}
		
		mc.renderEngine.bindTexture(if (skinData.second) LibResourceLocations.oldFemale[player.raceID - 1] else player.locationSkin)
		booba.renderAll()
		
		player.inventory.armorInventory[2]?.let {
			mc.renderEngine.bindTexture(RenderBiped.getArmorResource(player, it, 1, null))
			glScaled(1.1)
			booba.renderAll()
		}
		
		if (transparet) {
			ASJRenderHelper.discard()
			glAlphaFunc(GL_GREATER, 0.1f)
			glDepthMask(true)
			glColor4f(1f, 1f, 1f, 1f)
		}
		
		glPopMatrix()
	}
}
