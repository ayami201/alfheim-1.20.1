package alfheim.client.render.entity

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.ModInfo
import alfheim.api.entity.*
import alfheim.api.lib.LibResourceLocations
import alfheim.api.lib.LibResourceLocations.ResourceLocationIL
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.helper.*
import alfheim.common.item.equipment.bauble.ItemElvenDisguise
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.Tessellator
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.potion.Potion
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.*
import vazkii.botania.api.item.IBaubleRender.Helper
import vazkii.botania.api.item.IPhantomInkable
import vazkii.botania.common.Botania
import vazkii.botania.common.item.ModItems
import java.awt.Color
import kotlin.math.*

object RenderWings {
	
	val textures: Map<String, ResourceLocation> by lazy { ContributorsPrivacyHelper.wings.map { (k, v) -> k to ResourceLocationIL(ModInfo.MODID, "textures/model/entity/wings/$v.png") }.toMap() }
	
	@SideOnly(Side.CLIENT)
	fun render(player: EntityPlayer) {
		val match = ContributorsPrivacyHelper.wings.keys.firstOrNull { ContributorsPrivacyHelper.isCorrect(player, it) }
		
		val forced = ItemElvenDisguise.getDisguise(player)
		val race = forced ?: player.race
		
		if (match == null) {
			if (!AlfheimConfigHandler.enableElvenStory && forced == null) return
			if (AlfheimConfigHandler.wingsBlackList.contains(mc.theWorld?.provider?.dimensionId ?: Int.MAX_VALUE)) return
			if (race == EnumRace.HUMAN) return
			if (ContributorsPrivacyHelper.isCorrect(player.commandSenderName, "AlexSocol")) return
		}
		
		if (player.isInvisible || player.isPotionActive(Potion.invisibility) || player.isInvisibleToPlayer(mc.thePlayer)) return
		
		val wings = PlayerHandler.getPlayerBaubles(player)[0]
		if (wings?.item === ModItems.flightTiara!! && (wings.item as? IPhantomInkable)?.hasPhantomInk(wings) != true) return
		
		glPushMatrix()
		glDisable(GL_CULL_FACE)
		glEnable(GL_BLEND)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
		glDepthMask(false)
		glAlphaFunc(GL_GREATER, 1 / 255f)
		ASJRenderHelper.setGlow()
		
		val spd = 0.5
		
		if (match != null) {
			if (ContributorsPrivacyHelper.wings[match] == "Butterfly")
				ASJRenderHelper.glColor1u(ASJRenderHelper.addAlpha(Color.HSBtoRGB(Botania.proxy.worldElapsedTicks % 360 / 360f, 1f, 1f), 255))
			else
				glColor4f(1f, 1f, 1f, 1f)
		} else
			race.glColor(if (player.flight / ElvenFlightHelper.max < 0.05) min(0.75 + cos((player.ticksExisted + mc.timer.renderPartialTicks).D * spd * 0.3).F * 0.2, 1.0) else 1.0)
		
		Helper.rotateIfSneaking(player)
		glTranslated(0.0, -0.15, 0.0)
		
		// Icon
		if (match == null) {
			glPushMatrix()
			glTranslated(-0.25, 0.25, 0.15)
			val si = 0.5
			glScaled(si)
			drawRect(getPlayerIconTexture(player), 0)
			glPopMatrix()
		}
		
		glTranslated(0.0, 0.1, 0.0)
		
		val flying = player.capabilities.isFlying
		val ry = 20f + ((sin((player.ticksExisted + mc.timer.renderPartialTicks).D * spd * (if (flying) 0.4f else 0.2f).D) + 0.5f) * if (flying) 30f else 5f).F
		
		// Wing left
		glPushMatrix()
		glTranslated(0.15, 0.1, 0.15)
		val swr = 1.5
		glScaled(swr)
		glRotated((-ry).D, 0.0, 1.0, 0.0)
		getPlayerWingTexture(player)?.let { drawRect(it, -1) }
		glPopMatrix()
		
		// Wing right
		glPushMatrix()
		glTranslated(-0.15, 0.1, 0.15)
		val swl = 1.5
		glScaled(-swl, swl, swl)
		glRotated((-ry).D, 0.0, 1.0, 0.0)
		getPlayerWingTexture(player)?.let { drawRect(it, -1) }
		glPopMatrix()
		
		ASJRenderHelper.discard()
		glAlphaFunc(GL_GREATER, 0.1f)
		glDepthMask(true)
		glDisable(GL_BLEND)
		glEnable(GL_CULL_FACE)
		glPopMatrix()
		
		if (mc.thePlayer === player && mc.gameSettings.thirdPersonView == 0) return
		
		if (mc.theWorld.totalWorldTime % 2 == 0L && !mc.isGamePaused && !player.onGround) {
			val v = Vector3(Math.random() - 0.5, 0.0, Math.random() - 0.5).normalize().add(0.0, Math.random(), 0.0).mul(Math.random(), 1.0, Math.random()).mul(player.width.D, player.height.D, player.width.D)
			val (r, g, b) = Color(race.rgbColor).getRGBColorComponents(null)
			particleQueue[player.commandSenderName] = QueuedParticle(player.posX + v.x, player.posY + v.y - if (mc.thePlayer === player) 1.62 else 0.0, player.posZ + v.z, r, g, b, 2f * Math.random().F)
		}
	}
	
	fun drawRect(texture: ResourceLocation, i: Int) {
		mc.renderEngine.bindTexture(texture)
		Tessellator.instance.startDrawingQuads()
		Tessellator.instance.addVertexWithUV(0.0, i.D, 0.0, 1.0, 0.0)
		Tessellator.instance.addVertexWithUV(0.0, 1.0, 0.0, 1.0, 1.0)
		Tessellator.instance.addVertexWithUV(1.0, 1.0, 0.0, 0.0, 1.0)
		Tessellator.instance.addVertexWithUV(1.0, i.D, 0.0, 0.0, 0.0)
		Tessellator.instance.draw()
	}
	
	fun getPlayerWingTexture(player: EntityPlayer): ResourceLocation? {
		val raceID = ItemElvenDisguise.getDisguise(player)?.ordinal ?: player.raceID
		return textures[ContributorsPrivacyHelper.wings.keys.firstOrNull { ContributorsPrivacyHelper.isCorrect(player, it) } ?: ""] ?: LibResourceLocations.wings[raceID]
	}
	
	fun getPlayerIconTexture(player: EntityPlayer): ResourceLocationIL {
		val raceID = ItemElvenDisguise.getDisguise(player)?.ordinal ?: player.raceID
		return LibResourceLocations.icons[raceID]
	}
	
	val particleQueue = HashMap<String, QueuedParticle>()
	
	data class QueuedParticle(val x: Double, val y: Double, val z: Double, val r: Float, val g: Float, val b: Float, val size: Float)
	
	fun spawnQueuedParticles() {
		if (mc.theWorld != null)
			particleQueue.values.forEach { (x, y, z, r, g, b, size) ->
				Botania.proxy.sparkleFX(mc.theWorld, x, y, z, r, g, b, size, 20)
			}
		
		particleQueue.clear()
	}
}