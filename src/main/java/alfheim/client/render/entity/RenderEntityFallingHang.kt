package alfheim.client.render.entity

// PORT: импорты 1.20.1. Render 1.7.10 → EntityRenderer 1.20.1 (MAPPING.md, «Рендер»): рендер создаётся с контекстом,
// регистрация — alfheim.port.client.AlfheimEntityRenderers
import alexsocol.asjlib.*
import alfheim.common.entity.EntityFallingHang
import alfheim.port.legacy.*
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer as Render
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.BlockPos
import net.minecraft.world.inventory.InventoryMenu
import net.minecraftforge.client.model.data.ModelData

class RenderEntityFallingHang(context: EntityRendererProvider.Context): Render<EntityFallingHang>(context) {
//object RenderEntityFallingHang: Render() {
	
	// PORT: крест RenderBlocks (drawCrossedSquares) — модель блока: крест с той же текстурой (alfheim.port.data)
//	val render = RenderBlocks()
	
	// PORT: doRender(entity, x, y, z, yaw, ticks) → render: смещение к существу уже в матрице
	override fun render(entity: EntityFallingHang, yaw: Float, ticks: Float, ms: PoseStack, buffers: MultiBufferSource, light: Int) {
//	override fun doRender(entity: Entity?, x: Double, y: Double, z: Double, yaw: Float, ticks: Float) {
//		entity as EntityFallingHang
		
		ms.pushPose()
		ms.translate(-0.5, 0.0, -0.5)
//		glPushMatrix()
//		glTranslated(x - 0.5, y, z - 0.5)
		// PORT: без затенения граней (GL_LIGHTING выключен) — вид отрисовки блоков RenderType.cutout: его шейдер не
		// затеняет грани, как у падающего блока ванилы 1.20.1
//		glDisable(GL_LIGHTING)
//
//		mc.renderEngine.bindTexture(getEntityTexture(entity))
		
		// PORT: яркость — в точке существа, как getMixedBrightnessForBlock; иконка getIcon(0, meta) — модель блока своего
		// варианта (variant1710)
		val brightness = LevelRenderer.getLightColor(entity.worldObj, BlockPos(entity.posX.mfloor(), entity.posY.mfloor(), entity.posZ.mfloor()))
		mc.blockRenderer.renderSingleBlock(entity.block.variant1710(entity.meta).defaultBlockState(), ms, buffers, brightness, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.cutout())
//		val tes = Tessellator.instance
//		tes.setBrightness(entity.block.getMixedBrightnessForBlock(entity.worldObj, entity.posX.mfloor(), entity.posY.mfloor(), entity.posZ.mfloor()))
//		tes.startDrawingQuads()
//		render.drawCrossedSquares(entity.block.getIcon(0, entity.meta), 0.0, 0.0, 0.0, 1f)
//		tes.draw()
		
		ms.popPose()
//		glEnable(GL_LIGHTING)
//		glPopMatrix()
	}
	
	// PORT: атлас блоков 1.20.1
	override fun getTextureLocation(entity: EntityFallingHang) = InventoryMenu.BLOCK_ATLAS
//	override fun getEntityTexture(entity: Entity?) = TextureMap.locationBlocksTexture!!
}
