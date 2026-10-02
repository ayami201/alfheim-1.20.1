package alfheim.common.compat

import alexsocol.asjlib.*
import alfheim.common.potion.PotionSoulburn
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.client.event.RenderPlayerEvent
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11
import kotlin.math.*

/**
 * Intermediate class for calls that should be remapped by Angelica
 * Invoked from code in @TransformerExclusions
 * Exctracted calls to:
 * - org.lwjgl.opengl.GL11
 * - ...
 */
object AngelicaCompat {
	
	fun glColor3f(r: Float, g: Float, b: Float) {
		GL11.glColor3f(r, g, b)
	}
	
	fun onPlayerBaubleRender1() {
		GL11.glPushMatrix()
		GL11.glEnable(GL11.GL_BLEND)
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
		GL11.glColor4f(1f, 1f, 1f, 1f)
	}
	
	fun onPlayerBaubleRender2(flying: Boolean, player: EntityPlayer, event: RenderPlayerEvent) {
		GL11.glColor4f(1f, 1f, 1f, 0.5f + if (flying) cos((player.ticksExisted + event.partialRenderTick) * 0.3).F * 0.25f + 0.25f else 0f)
	}
	
	fun onPlayerBaubleRender3(x: Float, h: Float, z: Float, rz: Float, rx: Float, ry: Float) {
		GL11.glTranslatef(x, h, z)
		GL11.glRotatef(rz, 0f, 0f, 1f)
		GL11.glRotatef(rx, 1f, 0f, 0f)
		GL11.glRotatef(ry, 0f, 1f, 0f)
	}
	
	fun onPlayerBaubleRender4(x: Float, h: Float, z: Float, rz: Float, rx: Float, ry: Float) {
		GL11.glRotatef(-ry, 0f, 1f, 0f)
		GL11.glRotatef(-rx, 1f, 0f, 0f)
		GL11.glRotatef(-rz, 0f, 0f, 1f)
		GL11.glTranslatef(-x, -h, -z)
	}
	
	fun onPlayerBaubleRender5() {
		GL11.glScalef(-1f, 1f, 1f)
	}
	
	fun onPlayerBaubleRender6() {
		GL11.glColor4f(1f, 1f, 1f, 1f)
		GL11.glPopMatrix()
	}
	
	fun enableRescaleNormal() {
		GL11.glEnable(GL12.GL_RESCALE_NORMAL)
	}
	
	fun disableRescaleNormal() {
		GL11.glDisable(GL12.GL_RESCALE_NORMAL)
	}
	
	fun glPushMatrix() {
		GL11.glPushMatrix()
	}
	
	fun glPopMatrix() {
		GL11.glPopMatrix()
	}
	
	fun renderQueued(blendmode: Int) {
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, blendmode)
	}
	
	fun preRender(angle: Float, time: Float, randomSpeed: Int) {
		GL11.glRotatef(angle, 0f, 1f, 0f)
		GL11.glTranslatef(0f, -0.5f - sin(time.D / 150 * randomSpeed).F / 8f, 0f)
	}
	
	fun renderOverlays() {
		GL11.glDisable(GL11.GL_ALPHA_TEST)
		PotionSoulburn.renderFireInFirstPerson()
		GL11.glEnable(GL11.GL_ALPHA_TEST)
	}
	
	fun glColor4f(r: Float, g: Float, b: Float, a: Float) {
		GL11.glColor4f(r, g, b, a)
	}
	
	fun enableDepthTest() {
		GL11.glEnable(GL11.GL_DEPTH_TEST)
	}
	
	fun disableDepthTest() {
		GL11.glDisable(GL11.GL_DEPTH_TEST)
	}
	
	fun glTranslatef(x: Float, y: Float, z: Float) {
		GL11.glTranslatef(x, y, z)
	}
}