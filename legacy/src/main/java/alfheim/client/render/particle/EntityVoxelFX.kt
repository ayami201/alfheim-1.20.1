package alfheim.client.render.particle

import alexsocol.asjlib.*
import net.minecraft.client.particle.EntityFX
import net.minecraft.client.renderer.Tessellator
import net.minecraft.util.MathHelper
import net.minecraft.world.World
import org.lwjgl.opengl.GL11.*
import java.util.*
import kotlin.math.*

class EntityVoxelFX(world: World, x: Double, y: Double, z: Double, r: Float, g: Float, b: Float): EntityFX(world, x, y, z) {
	
	var f0 = 0f
	
	init {
		posX = x
		posY = y
		posZ = z
		prevPosX = x
		prevPosY = y
		prevPosZ = z
		particleRed = r
		particleGreen = g
		particleBlue = b
		particleMaxAge = 6000
		particleGravity = 0f
		particleTextureIndexX = 0
		particleTextureIndexY = 0
	}
	
	override fun renderParticle(tessellator: Tessellator, f0: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
		this.f0 = f0
		renderQueue.add(this)
	}
	
	fun postRender() {
		if (isDead) return
		
		val rand = Random(uniqueID.mostSignificantBits)
		val randOffset = 8
		
		val i = MathHelper.floor_double(posX)
		val j = MathHelper.floor_double(posZ)
		
		val b = if (worldObj.blockExists(i, 0, j)) {
			val k = MathHelper.floor_double(posY)
			worldObj.getLightBrightnessForSkyBlocks(i, k, j, 0)
		} else {
			0
		}
		
		val tes = Tessellator.instance
		tes.setBrightness(b)
		tes.setColorOpaque_F(
			min(1f, max(0f, particleRed + ASJUtilities.randInBounds(-randOffset, randOffset, rand) / 255f)),
			min(1f, max(0f, particleGreen + ASJUtilities.randInBounds(-randOffset, randOffset, rand) / 255f)),
			min(1f, max(0f, particleBlue + ASJUtilities.randInBounds(-randOffset, randOffset, rand) / 255f))
		)
		
		val x = (prevPosX + (posX - prevPosX) * f0 - interpPosX).F
		val y = (prevPosY + (posY - prevPosY) * f0 - interpPosY).F
		val z = (prevPosZ + (posZ - prevPosZ) * f0 - interpPosZ).F
		
		val s = 0.03125
		tes.addTranslation(x, y, z)
		
		// Front face
		tes.addVertex(-s, -s, s)  // Bottom-left
		tes.addVertex(s, -s, s)   // Bottom-right
		tes.addVertex(s, s, s)    // Top-right
		tes.addVertex(-s, s, s)   // Top-left
		
		// Back face
		tes.addVertex(s, -s, -s)  // Bottom-left
		tes.addVertex(-s, -s, -s) // Bottom-right
		tes.addVertex(-s, s, -s)  // Top-right
		tes.addVertex(s, s, -s)   // Top-left
		
		// Left face
		tes.addVertex(-s, -s, -s) // Bottom-left
		tes.addVertex(-s, -s, s)  // Bottom-right
		tes.addVertex(-s, s, s)   // Top-right
		tes.addVertex(-s, s, -s)  // Top-left
		
		// Right face
		tes.addVertex(s, -s, s)   // Bottom-left
		tes.addVertex(s, -s, -s)  // Bottom-right
		tes.addVertex(s, s, -s)   // Top-right
		tes.addVertex(s, s, s)    // Top-left
		
		// Top face
		tes.addVertex(-s, s, s)   // Bottom-left
		tes.addVertex(s, s, s)    // Bottom-right
		tes.addVertex(s, s, -s)   // Top-right
		tes.addVertex(-s, s, -s)  // Top-left
		
		// Bottom face
		tes.addVertex(-s, -s, -s) // Bottom-left
		tes.addVertex(s, -s, -s)  // Bottom-right
		tes.addVertex(s, -s, s)   // Top-right
		tes.addVertex(-s, -s, s)  // Top-left
		
		tes.addTranslation(-x, -y, -z)
	}
	
	companion object {
		
		val renderQueue: Queue<EntityVoxelFX> = ArrayDeque()
		
		fun renderQueue() {
			glDisable(GL_TEXTURE_2D)
			val tes = Tessellator.instance
			tes.startDrawingQuads()
			for (foxelFX in renderQueue) {
				foxelFX.postRender()
			}
			tes.draw()
			renderQueue.clear()
			glEnable(GL_TEXTURE_2D)
		}
	}
}
