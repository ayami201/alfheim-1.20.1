package alfheim.api.item

// PORT: импорты 1.20.1. Фиксированного конвейера GL 1.7.10 в 1.20.1 нет: рамка рисуется на этапе AFTER_LEVEL события
// RenderLevelStageEvent (как RenderWorldLastEvent — после всего мира) через PoseStack события; линии — шейдером линий
// ванилы (толщина — RenderSystem.lineWidth), цифры — шрифтом в мире. Глубина и отсечение граней выключены, как у автора
import alexsocol.asjlib.*
import alfheim.port.legacy.*
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import com.mojang.math.Axis
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level as World
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.world.phys.shapes.Shapes
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.RenderLevelStageEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import vazkii.botania.client.core.handler.ClientTickHandler
import java.awt.Color
import kotlin.math.sqrt

interface IDoubleBoundItem {
	
	@OnlyIn(Dist.CLIENT)
	fun getFirstPosition(stack: ItemStack): ChunkCoordinates?
	
	@OnlyIn(Dist.CLIENT)
	fun getSecondPosition(stack: ItemStack): ChunkCoordinates?
}

interface IRotationDisplay {
	
	/**
	 * @return horizontal rotation [0..30]. -1 means no rotation render
	 */
	@OnlyIn(Dist.CLIENT)
	fun getRotation(stack: ItemStack): Int
}

object DoubleBoundItemRender {
	
	init {
		eventForge()
	}
	
	// PORT: RenderWorldLastEvent → RenderLevelStageEvent, этап AFTER_LEVEL (после мира и погоды, до руки, как в 1.7.10);
	// матрица GL → PoseStack
	@SubscribeEvent
	fun renderWorldLast(e: RenderLevelStageEvent) {
		if (e.stage != RenderLevelStageEvent.Stage.AFTER_LEVEL) return
		
		val player = mc.player ?: return
		val stack = player.mainHandItem.takeUnless { it.isEmpty } ?: return
//		val player = mc.thePlayer
//		val stack = player.currentEquippedItem ?: return
		val item = stack.item
		
		// PORT: на этапе AFTER_LEVEL Forge 1.20.1 кладёт в событие матрицу проекции, а не поворот камеры. Поворот — как
		// у мира в GameRenderer.renderLevel: наклон, затем рыскание + 180°
		val ms = PoseStack()
		ms.mulPose(Axis.XP.rotationDegrees(e.camera.xRot))
		ms.mulPose(Axis.YP.rotationDegrees(e.camera.yRot + 180f))
		ms.pushPose()
		RenderSystem.disableCull()
		RenderSystem.disableDepthTest()
		RenderSystem.enableBlend()
		RenderSystem.defaultBlendFunc()
//		glPushMatrix()
//		glPushAttrib(GL_LIGHTING)
//		glDisable(GL_CULL_FACE)
//		glDisable(GL_DEPTH_TEST)
//		glDisable(GL_TEXTURE_2D)
//		glDisable(GL_LIGHTING)
//		glEnable(GL_BLEND)
//
//		Tessellator.renderingWorldRenderer = false
		
		if (item is IDoubleBoundItem) {
			val color1 = Color.HSBtoRGB(ClientTickHandler.ticksInGame % 300 / 300f, 0.6f, 1f)
			val color2 = Color.HSBtoRGB((ClientTickHandler.ticksInGame + 100) % 300 / 300f, 0.6f, 1f)
			
			// PORT: цифра — шрифт в мире без проверки глубины (SEE_THROUGH), полная яркость, как без света GL у автора
			fun draw(it: ChunkCoordinates, n: String, color: Int) {
				ms.pushPose()
				ms.translate(it.posX - RenderManager.renderPosX, it.posY - RenderManager.renderPosY, it.posZ - RenderManager.renderPosZ)
				ms.translate(0.5, 0.5, 0.5)
				ms.scale(1 / 16f, 1 / 16f, 1 / 16f)
				ms.translate(mc.font.width(n) / 2f, mc.font.lineHeight / 2f, 0f)
				ms.mulPose(Axis.ZP.rotationDegrees(180f))
				val buffers = MultiBufferSource.immediate(Tesselator.getInstance().builder)
				mc.font.drawInBatch(n, 0f, 0f, color, false, ms.last().pose(), buffers, Font.DisplayMode.SEE_THROUGH, 0, LightTexture.FULL_BRIGHT)
				buffers.endBatch()
				ms.popPose()
//				glPushMatrix()
//				glEnable(GL_TEXTURE_2D)
//				glTranslated(it.posX - RenderManager.renderPosX, it.posY - RenderManager.renderPosY, it.posZ - RenderManager.renderPosZ)
//				glTranslated(0.5)
//				glScalef(1 / 16f)
//				glTranslatef(mc.fontRenderer.getStringWidth(n) / 2f, mc.fontRenderer.FONT_HEIGHT / 2f, 0f)
//				glRotatef(180f, 0f, 0f, 1f)
//				mc.fontRenderer.drawString(n, 0, 0, color)
//				glDisable(GL_TEXTURE_2D)
//				glPopMatrix()
				
				renderBlockOutlineAt(ms, it, color, 1f)
			}
			
			item.getFirstPosition(stack)?.also { draw(it, "1", color1) }
			item.getSecondPosition(stack)?.also { draw(it, "2", color2) }
		}
		
		if (item is IRotationDisplay) {
			val rotation = item.getRotation(stack)
			val mop = mc.hitResult
//			val mop = mc.objectMouseOver
			
			// PORT-FIX: стрелка — только у блока под прицелом. В 1.7.10 промах луча — null, а существо давало блок 0, 0, 0
			// со стороной 0 — стрелку и рамку у начала мира
			if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK && rotation != -1) {
//			if (mop != null && rotation != -1) {
				val color = Color.HSBtoRGB((ClientTickHandler.ticksInGame + 200) % 300 / 300f, 0.6f, 1f)
				val side = ForgeDirection.getOrientation(mop.sideHit)
//				val tes = Tessellator.instance
				
				val x = mop.blockX + side.offsetX
				val y = mop.blockY + side.offsetY
				val z = mop.blockZ + side.offsetZ
				
				ms.pushPose()
				ms.translate(x - RenderManager.renderPosX, y - RenderManager.renderPosY, z - RenderManager.renderPosZ)
				ms.mulPose(Axis.YP.rotationDegrees(rotation * 90f))
//				glPushMatrix()
//				glScalef(1f, 1f, 1f)
//				glTranslated(x - RenderManager.renderPosX, y - RenderManager.renderPosY, z - RenderManager.renderPosZ)
//				glRotatef(rotation * 90f, 0f, 1f, 0f)
				
				val colorRGB = Color(color)
//				glColor4ub(colorRGB.red.toByte(), colorRGB.green.toByte(), colorRGB.blue.toByte(), 255.toByte())
				
				RenderSystem.lineWidth(5f)
//				glLineWidth(5f)
				
				when (rotation) {
					1 -> ms.translate(-1.0, 0.0, 0.0)
					2 -> ms.translate(-1.0, 0.0, -1.0)
					3 -> ms.translate(0.0, 0.0, -1.0)
				}
//					1 -> glTranslated(-1.0, 0.0, 0.0)
//					2 -> glTranslated(-1.0, 0.0, -1.0)
//					3 -> glTranslated(0.0, 0.0, -1.0)
				
				// PORT: GL_LINES — пары точек, цвет — у вершин
				drawLines(ms, colorRGB, 255,
						  0.0, 0.5, 0.0, 1.0, 0.5, 1.0,
						  1.0, 0.5, 0.75, 1.0, 0.5, 1.0,
						  0.75, 0.5, 1.0, 1.0, 0.5, 1.0,
						  1.0, 0.5, 0.75, 0.75, 0.5, 1.0)
//				tes.startDrawing(GL_LINES)
//				tes.addVertex(0.0, 0.5, 0.0)
//				tes.addVertex(1.0, 0.5, 1.0)
//				tes.addVertex(1.0, 0.5, 0.75)
//				tes.addVertex(1.0, 0.5, 1.0)
//				tes.addVertex(0.75, 0.5, 1.0)
//				tes.addVertex(1.0, 0.5, 1.0)
//				tes.addVertex(1.0, 0.5, 0.75)
//				tes.addVertex(0.75, 0.5, 1.0)
//				tes.draw()
				
				// PORT: GL_POLYGON из трёх точек — треугольник
				RenderSystem.setShader(GameRenderer::getPositionColorShader)
				val buf = Tesselator.getInstance().builder
				buf.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR)
				buf.vertex(ms.last().pose(), 1f, 0.5f, 1f).color(colorRGB.red, colorRGB.green, colorRGB.blue, 255).endVertex()
				buf.vertex(ms.last().pose(), 1f, 0.5f, 0.75f).color(colorRGB.red, colorRGB.green, colorRGB.blue, 255).endVertex()
				buf.vertex(ms.last().pose(), 0.75f, 0.5f, 1f).color(colorRGB.red, colorRGB.green, colorRGB.blue, 255).endVertex()
				Tesselator.getInstance().end()
//				tes.startDrawing(GL_POLYGON)
//				tes.addVertex(1.0, 0.5, 1.0)
//				tes.addVertex(1.0, 0.5, 0.75)
//				tes.addVertex(0.75, 0.5, 1.0)
//				tes.draw()
				
				ms.popPose()
//				glPopMatrix()
				
				renderBlockOutlineAt(ms, ChunkCoordinates(x, y, z), color, 1f)
			}
		}
		
		RenderSystem.lineWidth(1f)
		RenderSystem.enableDepthTest()
		RenderSystem.disableBlend()
		RenderSystem.enableCull()
		ms.popPose()
//		glEnable(GL_DEPTH_TEST)
//		glEnable(GL_TEXTURE_2D)
//		glDisable(GL_BLEND)
//		glEnable(GL_CULL_FACE)
//		glPopAttrib()
//		glPopMatrix()
	}
	
	// PORT: PoseStack события вместо матрицы GL. Рамка 1.7.10 — getSelectedBoundingBoxFromPool блока, у воздуха — весь
	// блок; в 1.20.1 — границы формы выделения блока, у пустой формы — весь блок. Сдвиг автора на +1 по z и обратно
	// ничего не менял и убран
	fun renderBlockOutlineAt(ms: PoseStack, pos: ChunkCoordinates, color: Int, thickness: Float) {
		ms.pushPose()
		ms.translate(pos.posX - RenderManager.renderPosX, pos.posY - RenderManager.renderPosY, pos.posZ - RenderManager.renderPosZ)
//		glPushMatrix()
//		glTranslated(pos.posX - RenderManager.renderPosX, pos.posY - RenderManager.renderPosY, pos.posZ - RenderManager.renderPosZ + 1)
		
		val colorRGB = Color(color)
//		glColor4ub(colorRGB.red.toByte(), colorRGB.green.toByte(), colorRGB.blue.toByte(), 255.toByte())
		val world: World = mc.level!!
//		val world: World = mc.theWorld
		val shape = world.getBlockState(pos).getShape(world, pos)
		val axis: AxisAlignedBB = (if (shape.isEmpty) Shapes.block() else shape).bounds()
		
		RenderSystem.lineWidth(thickness)
		renderBlockOutline(ms, axis, colorRGB, 255)
		RenderSystem.lineWidth(thickness + 3f)
		renderBlockOutline(ms, axis, colorRGB, 64)
//		val block = world.getBlock(pos.posX, pos.posY, pos.posZ)
//
//		run drawWireframe@{
//			if (block != null) {
//				val axis: AxisAlignedBB = block.getSelectedBoundingBoxFromPool(world, pos.posX, pos.posY, pos.posZ) ?: return@drawWireframe
//				axis.minX -= pos.posX.D
//				axis.maxX -= pos.posX.D
//				axis.minY -= pos.posY.D
//				axis.maxY -= pos.posY.D
//				axis.minZ -= pos.posZ + 1.D
//				axis.maxZ -= pos.posZ + 1.D
//				glScalef(1f, 1f, 1f)
//				glLineWidth(thickness)
//				renderBlockOutline(axis)
//				glLineWidth(thickness + 3f)
//				glColor4ub(colorRGB.red.toByte(), colorRGB.green.toByte(), colorRGB.blue.toByte(), 64.toByte())
//				renderBlockOutline(axis)
//			}
//		}
		
		ms.popPose()
//		glPopMatrix()
	}
	
	// PORT: 12 рёбер рамки — те же, что рисовал автор; LevelRenderer.renderLineBox ванилы, цвет — у вершин
	private fun renderBlockOutline(ms: PoseStack, aabb: AxisAlignedBB, color: Color, alpha: Int) {
		RenderSystem.setShader(GameRenderer::getRendertypeLinesShader)
		val tes = Tesselator.getInstance()
		tes.builder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL)
		LevelRenderer.renderLineBox(ms, tes.builder, aabb, color.red / 255f, color.green / 255f, color.blue / 255f, alpha / 255f)
		tes.end()
//		val tes = Tessellator.instance
//
//		val ix = aabb.minX
//		val iy = aabb.minY
//		val iz = aabb.minZ
//		val ax = aabb.maxX
//		val ay = aabb.maxY
//		val az = aabb.maxZ
//
//		tes.startDrawing(GL_LINES)
//
//		tes.addVertex(ix, iy, iz)
//		tes.addVertex(ix, ay, iz)
//
//		tes.addVertex(ix, ay, iz)
//		tes.addVertex(ax, ay, iz)
//
//		tes.addVertex(ax, ay, iz)
//		tes.addVertex(ax, iy, iz)
//
//		tes.addVertex(ax, iy, iz)
//		tes.addVertex(ix, iy, iz)
//
//		tes.addVertex(ix, iy, az)
//		tes.addVertex(ix, ay, az)
//
//		tes.addVertex(ix, iy, az)
//		tes.addVertex(ax, iy, az)
//
//		tes.addVertex(ax, iy, az)
//		tes.addVertex(ax, ay, az)
//
//		tes.addVertex(ix, ay, az)
//		tes.addVertex(ax, ay, az)
//
//		tes.addVertex(ix, iy, iz)
//		tes.addVertex(ix, iy, az)
//
//		tes.addVertex(ix, ay, iz)
//		tes.addVertex(ix, ay, az)
//
//		tes.addVertex(ax, iy, iz)
//		tes.addVertex(ax, iy, az)
//
//		tes.addVertex(ax, ay, iz)
//		tes.addVertex(ax, ay, az)
//
//		tes.draw()
	}
	
	/** PORT: отрезки GL_LINES — пары точек подряд; шейдеру линий 1.20.1 у вершины нужно направление отрезка */
	private fun drawLines(ms: PoseStack, color: Color, alpha: Int, vararg p: Double) {
		RenderSystem.setShader(GameRenderer::getRendertypeLinesShader)
		val tes = Tesselator.getInstance()
		val buf = tes.builder
		val pose = ms.last()
		buf.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL)
		for (i in p.indices step 6) {
			val dx = (p[i + 3] - p[i]).toFloat()
			val dy = (p[i + 4] - p[i + 1]).toFloat()
			val dz = (p[i + 5] - p[i + 2]).toFloat()
			val l = sqrt(dx * dx + dy * dy + dz * dz)
			for (j in intArrayOf(i, i + 3))
				buf.vertex(pose.pose(), p[j].toFloat(), p[j + 1].toFloat(), p[j + 2].toFloat()).color(color.red, color.green, color.blue, alpha).normal(pose.normal(), dx / l, dy / l, dz / l).endVertex()
		}
		tes.end()
	}
	
	/** PORT: RenderManager.renderPosX/Y/Z 1.7.10 — точка камеры */
	private object RenderManager {
		
		val renderPosX get() = mc.gameRenderer.mainCamera.position.x
		val renderPosY get() = mc.gameRenderer.mainCamera.position.y
		val renderPosZ get() = mc.gameRenderer.mainCamera.position.z
	}
}
