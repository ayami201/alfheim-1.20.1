package alfheim.client.render.particle

// PORT: частица 1.7.10 (EntityFX) → Particle 1.20.1. Автор складывал кубики в очередь и рисовал их отдельным проходом
// после всех частиц (EventHandlerClient.renderParticles): без текстуры, со светом мира, без записи глубины. В 1.20.1 тот
// же проход — свой вид отрисовки частиц (RENDER_TYPE ниже), очередь не нужна
import alexsocol.asjlib.*
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel as World
import net.minecraft.client.particle.Particle as EntityFX
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import java.util.*
import kotlin.math.*

class EntityVoxelFX(world: World, x: Double, y: Double, z: Double, r: Float, g: Float, b: Float): EntityFX(world, x, y, z) {
	
	// PORT: у частицы 1.20.1 нет UUID; разброс цвета автор брал по UUID — здесь своё случайное число частицы
	private val seed = Random().nextLong()
	
	init {
		rCol = r
		gCol = g
		bCol = b
		lifetime = 6000
		gravity = 0f
//		posX = x
//		posY = y
//		posZ = z
//		prevPosX = x
//		prevPosY = y
//		prevPosZ = z
//		particleRed = r
//		particleGreen = g
//		particleBlue = b
//		particleMaxAge = 6000
//		particleGravity = 0f
//		particleTextureIndexX = 0
//		particleTextureIndexY = 0
	}
	
	override fun getRenderType() = RENDER_TYPE
	
	// PORT: renderParticle + postRender 1.7.10 → render 1.20.1: вершины — в буфер вида отрисовки, точка — от камеры
	override fun render(tes: VertexConsumer, camera: Camera, f0: Float) {
		if (removed) return
		
		val rand = Random(seed)
		val randOffset = 8
		
		val i = MathHelper.floor(x)
		val j = MathHelper.floor(z)
		
		val b = if (level.hasChunkAt(BlockPos(i, 0, j))) {
			val k = MathHelper.floor(y)
			LevelRenderer.getLightColor(level, BlockPos(i, k, j))
		} else {
			0
		}
		
		val red = min(1f, max(0f, rCol + ASJUtilities.randInBounds(-randOffset, randOffset, rand) / 255f))
		val green = min(1f, max(0f, gCol + ASJUtilities.randInBounds(-randOffset, randOffset, rand) / 255f))
		val blue = min(1f, max(0f, bCol + ASJUtilities.randInBounds(-randOffset, randOffset, rand) / 255f))
		
		val x = (xo + (x - xo) * f0 - camera.position.x).F
		val y = (yo + (y - yo) * f0 - camera.position.y).F
		val z = (zo + (z - zo) * f0 - camera.position.z).F
		
		val s = 0.03125
		fun addVertex(vx: Double, vy: Double, vz: Double) = tes.vertex(x + vx, y + vy, z + vz).color(red, green, blue, 1f).uv2(b).endVertex()
		
		// Front face
		addVertex(-s, -s, s)  // Bottom-left
		addVertex(s, -s, s)   // Bottom-right
		addVertex(s, s, s)    // Top-right
		addVertex(-s, s, s)   // Top-left
		
		// Back face
		addVertex(s, -s, -s)  // Bottom-left
		addVertex(-s, -s, -s) // Bottom-right
		addVertex(-s, s, -s)  // Top-right
		addVertex(s, s, -s)   // Top-left
		
		// Left face
		addVertex(-s, -s, -s) // Bottom-left
		addVertex(-s, -s, s)  // Bottom-right
		addVertex(-s, s, s)   // Top-right
		addVertex(-s, s, -s)  // Top-left
		
		// Right face
		addVertex(s, -s, s)   // Bottom-left
		addVertex(s, -s, -s)  // Bottom-right
		addVertex(s, s, -s)   // Top-right
		addVertex(s, s, s)    // Top-left
		
		// Top face
		addVertex(-s, s, s)   // Bottom-left
		addVertex(s, s, s)    // Bottom-right
		addVertex(s, s, -s)   // Top-right
		addVertex(-s, s, -s)  // Top-left
		
		// Bottom face
		addVertex(-s, -s, -s) // Bottom-left
		addVertex(s, -s, -s)  // Bottom-right
		addVertex(s, -s, s)   // Top-right
		addVertex(-s, -s, s)  // Top-left
	}
	
	companion object {
		
		/**
		 * Проход автора (EventHandlerClient.renderParticles): свет мира включён, глубина не пишется, смешивание по
		 * прозрачности; кубики без текстуры (GL_TEXTURE_2D выключена)
		 */
		val RENDER_TYPE = object: ParticleRenderType {
			
			override fun begin(builder: BufferBuilder, textures: TextureManager) {
				RenderSystem.setShader(GameRenderer::getPositionColorLightmapShader)
				RenderSystem.depthMask(false)
				RenderSystem.enableBlend()
				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
				builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_LIGHTMAP)
			}
			
			override fun end(tesselator: Tesselator) {
				tesselator.end()
				RenderSystem.disableBlend()
				RenderSystem.depthMask(true)
			}
			
			override fun toString() = "alfheim:voxel"
		}
		
		/*
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
		*/
	}
}
