package alfheim.client.render.entity

// PORT: импорты 1.20.1. Render 1.7.10 → EntityRenderer 1.20.1 (MAPPING.md, «Рендер»): рендер создаётся с контекстом,
// регистрация — alfheim.port.client.AlfheimEntityRenderers
import alexsocol.asjlib.D
import alfheim.common.entity.FakeLightning
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.resources.ResourceLocation
import java.util.*

class RenderFakeLightning(context: EntityRendererProvider.Context): EntityRenderer<FakeLightning>(context) {
//object RenderFakeLightning: Render() {
	
	// not gonna prettify this mess
	// PORT: doRender(entity, x, y, z, yaw, ticks) → render: смещение к существу уже в матрице, поэтому x, y, z — 0. Без
	// текстуры и света, смешение GL_SRC_ALPHA, GL_ONE — вид отрисовки молнии 1.20.1 (RenderType.lightning)
	override fun render(entity: FakeLightning, yaw: Float, ticks: Float, ms: PoseStack, buffers: MultiBufferSource, light: Int) {
//	override fun doRender(entity: Entity, x: Double, y: Double, z: Double, yaw: Float, ticks: Float) {
//		entity as FakeLightning
		
		val x = 0.0
		val y = 0.0
		val z = 0.0
		val buffer = buffers.getBuffer(RenderType.lightning())
		val matrix = ms.last().pose()
		val strip = FloatArray(30)
//		val tessellator = Tessellator.instance
//		glDisable(GL_TEXTURE_2D)
//		glDisable(GL_LIGHTING)
//		glEnable(GL_BLEND)
//		glBlendFunc(GL_SRC_ALPHA, GL_ONE)
		val adouble = DoubleArray(8)
		val adouble1 = DoubleArray(8)
		var d3 = 0.0
		var d4 = 0.0
		val random = Random(entity.boltVertex)
		for (i in 7 downTo 0) {
			adouble[i] = d3
			adouble1[i] = d4
			d3 += (random.nextInt(11) - 5).D
			d4 += (random.nextInt(11) - 5).D
		}
		for (k1 in 0..3) {
			val random1 = Random(entity.boltVertex)
			for (j in 0..2) {
				var k = 7
				var l = 0
				if (j > 0) {
					k = 7 - j
				}
				if (j > 0) {
					l = k - 2
				}
				var d5 = adouble[k] - d3
				var d6 = adouble1[k] - d4
				for (i1 in k downTo l) {
					val d7 = d5
					val d8 = d6
					if (j == 0) {
						d5 += (random1.nextInt(11) - 5).D
						d6 += (random1.nextInt(11) - 5).D
					} else {
						d5 += (random1.nextInt(31) - 15).D
						d6 += (random1.nextInt(31) - 15).D
					}
//					tessellator.startDrawing(5)
					val f2 = 0.5f
//					tessellator.setColorRGBA_F(0.9f * f2, 0.9f * f2, 1.0f * f2, 0.3f)
					var d9 = 0.1 + k1.D * 0.2
					if (j == 0) {
						d9 *= i1.D * 0.1 + 1.0
					}
					var d10 = 0.1 + k1.D * 0.2
					if (j == 0) {
						d10 *= (i1 - 1).D * 0.1 + 1.0
					}
					for (j1 in 0..4) {
						var d11 = x + 0.5 - d9
						var d12 = z + 0.5 - d9
						if (j1 == 1 || j1 == 2) {
							d11 += d9 * 2.0
						}
						if (j1 == 2 || j1 == 3) {
							d12 += d9 * 2.0
						}
						var d13 = x + 0.5 - d10
						var d14 = z + 0.5 - d10
						if (j1 == 1 || j1 == 2) {
							d13 += d10 * 2.0
						}
						if (j1 == 2 || j1 == 3) {
							d14 += d10 * 2.0
						}
						// PORT: вершины полосы треугольников (startDrawing(5)) — в массив, низ и верх по очереди
						strip.vertex(j1 * 2, d13 + d5, y + (i1 * 16).D, d14 + d6)
						strip.vertex(j1 * 2 + 1, d11 + d7, y + ((i1 + 1) * 16).D, d12 + d8)
//						tessellator.addVertex(d13 + d5, y + (i1 * 16).D, d14 + d6)
//						tessellator.addVertex(d11 + d7, y + ((i1 + 1) * 16).D, d12 + d8)
					}
					// PORT: полоса из пяти пар вершин — четыре четырёхугольника между соседними парами, как у молнии ванилы 1.20.1
					for (q in 0..3) for (v in QUAD) {
						val i = (q * 2 + v) * 3
						buffer.vertex(matrix, strip[i], strip[i + 1], strip[i + 2]).color(0.9f * f2, 0.9f * f2, 1.0f * f2, 0.3f).endVertex()
					}
//					tessellator.draw()
				}
			}
		}
//		glDisable(GL_BLEND)
//		glEnable(GL_LIGHTING)
//		glEnable(GL_TEXTURE_2D)
	}
	
	// PORT: текстуры у молнии нет, а 1.20.1 требует её имя — атлас блоков, как у молнии ванилы
	override fun getTextureLocation(entity: FakeLightning): ResourceLocation = TextureAtlas.LOCATION_BLOCKS
//	override fun getEntityTexture(entity: Entity?) = null
	
	/** Вершина [index] полосы — три числа подряд */
	private fun FloatArray.vertex(index: Int, x: Double, y: Double, z: Double) {
		this[index * 3] = x.toFloat()
		this[index * 3 + 1] = y.toFloat()
		this[index * 3 + 2] = z.toFloat()
	}
	
	companion object {
		
		/** Вершины четырёхугольника полосы от пары q: низ q, верх q, верх q + 1, низ q + 1 */
		private val QUAD = intArrayOf(0, 1, 3, 2)
	}
}
