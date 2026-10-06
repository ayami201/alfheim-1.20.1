package alfheim.client.render.tile

// PORT: импорты 1.20.1. TileEntitySpecialRenderer 1.7.10 → BlockEntityRenderer 1.20.1: регистрация —
// alfheim.port.client.AlfheimEntityRenderers. OBJ-модели грузит загрузчик Forge (SPEC, Р-13), грани — в буфер вида
// отрисовки, а не GL
import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.common.block.BlockTreeBerry
import alfheim.common.block.tile.TileTreeBerry
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer as TileEntitySpecialRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.InventoryMenu
import net.minecraftforge.client.model.data.ModelData

object RenderTileTreeBerry: TileEntitySpecialRenderer<TileTreeBerry> {
	
	// PORT: модели ягод — модели загрузчика OBJ Forge alfheim:block/tree_berry<вид><зрелость>_obj с текстурой
	// TreeBerry<вид><зрелость> (alfheim.port.data.AlfheimBlockStates), клиент запекает их с ресурсами
	// (alfheim.port.client.AlfheimModels)
	val models = HashMap<Int, Array<ResourceLocation>>()
//	val textures = HashMap<Int, Array<ResourceLocation>>()
//	val models = HashMap<Int, Array<IModelCustom?>>()
	val hasModels = arrayOf(2, 3, 4)
	
	// PORT: renderTileEntityAt(tile, x, y, z, ticks) → render: смещение к блоку уже в матрице, свет блока — light; обе
	// стороны граней (setTwoside), прозрачное отсекается — вид отрисовки entityCutoutNoCull
	override fun render(tile: TileTreeBerry, ticks: Float, ms: PoseStack, buffers: MultiBufferSource, light: Int, overlay: Int) {
//	override fun renderTileEntityAt(tile: TileEntity, x: Double, y: Double, z: Double, ticks: Float) {
//		if (tile !is TileTreeBerry) return
		
		val type = tile.berryType
		if (type !in hasModels || BlockTreeBerry.hasModelErrors[type] == true) return
		
		// PORT: metadata — свойство age
		val meta = tile.blockState.getValue(BlockTreeBerry.AGE)
//		val meta = tile.worldObj.getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord)
		
		val model = mc.modelManager.getModel(models.computeIfAbsent(type) {
			Array(3) { ResourceLocation(ModInfo.MODID, "block/tree_berry$type${it}_obj") }
		}.safeGet(meta))
		// PORT: модель, которую загрузчик не смог прочитать, клиент 1.20.1 заменяет «нет модели»
		if (model === mc.modelManager.missingModel) {
			ASJUtilities.error("Error loading berry model for $type'th set. It will be flat.")
			BlockTreeBerry.hasModelErrors[type] = true
			return
		}
		
		ms.pushPose()
		ms.translate(0.5, 0.0, 0.5)
		mc.blockRenderer.modelRenderer.renderModel(ms.last(), buffers.getBuffer(RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS)), null, model, 1f, 1f, 1f, light, overlay, ModelData.EMPTY, null)
		ms.popPose()
//		glPushMatrix()
//		setTwoside()
//		glTranslated(x + 0.5, y, z + 0.5)
//		
//		mc.renderEngine.bindTexture(textures.computeIfAbsent(type) {
//			Array(3) { ResourceLocation(ModInfo.MODID, "textures/blocks/TreeBerry$type$it.png") }
//		}.safeGet(meta))
//		
//		models.computeIfAbsent(type) {
//			try {
//				Array (3) { AdvancedModelLoader.loadModel(ResourceLocation(ModInfo.MODID, "model/TreeBerry$type$it.obj")) }
//			} catch (e: Throwable) {
//				ASJUtilities.error("Error loading berry model for $type'th set. It will be flat. Reason:", e)
//				BlockTreeBerry.hasModelErrors[type] = true
//				arrayOfNulls(3)
//			}
//		}.safeGet(meta)?.renderAll()
//		
//		discard()
//		glPopMatrix()
	}
}
