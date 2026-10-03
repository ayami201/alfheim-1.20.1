package alfheim.client.render.entity

// PORT: импорты 1.20.1. Render 1.7.10 → EntityRenderer 1.20.1: создаётся с контекстом, поэтому класс, а не объект;
// регистрация — alfheim.port.client.AlfheimEntityRenderers. Квадрат лицом к камере — в буфер вида отрисовки (свет и
// цвет — у вершин), а не GL; иконки — alfheim.port.client.getIcon
import alexsocol.asjlib.*
import alfheim.common.entity.EntityThrownPotion
import alfheim.common.item.AlfheimItems
import alfheim.port.client.getIcon
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer as Render
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.renderer.texture.TextureAtlasSprite as IIcon
import net.minecraft.world.inventory.InventoryMenu

class RenderEntityThrownPotion(ctx: EntityRendererProvider.Context): Render<EntityThrownPotion>(ctx) {
	
	// PORT: doRender(entity, x, y, z, yaw, partialTicks) → render: смещение к существу уже в матрице; свет существа —
	// light; полупрозрачность (GL_BLEND) — вид отрисовки entityTranslucent
	override fun render(e: EntityThrownPotion, yaw: Float, partialTicks: Float, ms: PoseStack, buffers: MultiBufferSource, light: Int) {
		val iicon = e.stack.item.getIcon(e.stack, 0)
		if (iicon != null) {
			ms.pushPose()
			ms.scale(0.5f, 0.5f, 0.5f)
			val tessellator = buffers.getBuffer(RenderType.entityTranslucent(getTextureLocation(e)))
			func_77026_a(ms, tessellator, iicon, light, 0xFFFFFF)
			// PORT: яркость 240 1.7.10 — свет блока 15, неба 0
			AlfheimItems.splashPotion.getIcon(e.stack, 1)?.let { func_77026_a(ms, tessellator, it, 240, e.color) }
			ms.popPose()
		}
//		val e = p_76986_1_ as EntityThrownPotion
//		val iicon = e.stack.item.getIcon(e.stack, 0)
//		if (iicon != null) {
//			glPushMatrix()
//			glTranslatef(p_76986_2_.F, p_76986_4_.F, p_76986_6_.F)
//			glEnable(GL_RESCALE_NORMAL)
//			glEnable(GL_BLEND)
//			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
//
//			glScaled(0.5)
//			bindEntityTexture(p_76986_1_)
//			val tessellator = Tessellator.instance
//			func_77026_a(tessellator, iicon, -1)
//			ASJRenderHelper.glColor1u(ASJRenderHelper.addAlpha(e.color, 255))
//			func_77026_a(tessellator, AlfheimItems.splashPotion.getIcon(e.stack, 1), 240)
//			glColor4f(1f, 1f, 1f, 1f)
//
//			glDisable(GL_BLEND)
//			glDisable(GL_RESCALE_NORMAL)
//			glPopMatrix()
//		}
	}
	
	// PORT: атлас предметов 1.7.10 — атлас блоков и предметов 1.20.1
	override fun getTextureLocation(p_110775_1_: EntityThrownPotion) = InventoryMenu.BLOCK_ATLAS
//	override fun getEntityTexture(p_110775_1_: Entity) = TextureMap.locationItemsTexture!!
	
	// PORT: поворот к камере 1.7.10 (180° − playerViewY, −playerViewX) — ориентация камеры и 180°, как у брошенного
	// предмета ванилы 1.20.1; цвет и свет — у каждой вершины
	private fun func_77026_a(ms: PoseStack, p_77026_1_: VertexConsumer, p_77026_2_: IIcon, light: Int, color: Int) {
		val f = p_77026_2_.u0
		val f1 = p_77026_2_.u1
		val f2 = p_77026_2_.v0
		val f3 = p_77026_2_.v1
		val f4 = 1f
		val f5 = 0.5f
		val f6 = 0.25f
		ms.pushPose()
		ms.mulPose(entityRenderDispatcher.cameraOrientation())
		ms.mulPose(Axis.YP.rotationDegrees(180f))
		val pose = ms.last()
		fun addVertexWithUV(x: Float, y: Float, u: Float, v: Float) = p_77026_1_.vertex(pose.pose(), x, y, 0f).color(color shr 16 and 0xFF, color shr 8 and 0xFF, color and 0xFF, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), 0f, 1f, 0f).endVertex()
		
		addVertexWithUV(0f - f5, 0f - f6, f, f3)
		addVertexWithUV(f4 - f5, 0f - f6, f1, f3)
		addVertexWithUV(f4 - f5, f4 - f6, f1, f2)
		addVertexWithUV(0f - f5, f4 - f6, f, f2)
		ms.popPose()
//		glPushMatrix()
//		glRotatef(180f - renderManager.playerViewY, 0f, 1f, 0f)
//		glRotatef(-renderManager.playerViewX, 1f, 0f, 0f)
//		p_77026_1_.startDrawingQuads()
//		p_77026_1_.setNormal(0f, 1f, 0f)
//		if (light != -1) {
//			p_77026_1_.setBrightness(light)
//		}
//
//		p_77026_1_.addVertexWithUV((0f - f5).D, (0f - f6).D, 0.0, f.D, f3.D)
//		p_77026_1_.addVertexWithUV((f4 - f5).D, (0f - f6).D, 0.0, f1.D, f3.D)
//		p_77026_1_.addVertexWithUV((f4 - f5).D, (f4 - f6).D, 0.0, f1.D, f2.D)
//		p_77026_1_.addVertexWithUV((0f - f5).D, (f4 - f6).D, 0.0, f.D, f2.D)
//		p_77026_1_.draw()
//		glPopMatrix()
	}
}
