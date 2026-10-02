package alfheim.port.client

import alfheim.api.ModInfo.MODID
import alfheim.common.block.*
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.BlockModelShaper
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.resources.model.*
import net.minecraft.core.*
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.client.event.ModelEvent
import net.minecraftforge.client.model.BakedModelWrapper
import net.minecraftforge.client.model.data.*
import net.minecraftforge.eventbus.api.*

/**
 * Модели блоков, которые в 1.7.10 выбирали иконку в коде (`registerBlockIcons` / `getIcon`), а не одной моделью
 * (MAPPING.md, «Блоки и предметы»). Сами модели строит генерация данных (`alfheim.port.data.AlfheimBlockStates`).
 */
object AlfheimModels {
	
	fun register(bus: IEventBus) {
		bus.addListener(EventPriority.NORMAL, false, ModelEvent.RegisterAdditional::class.java, ::registerAdditional)
		bus.addListener(EventPriority.NORMAL, false, ModelEvent.ModifyBakingResult::class.java, ::modifyBakingResult)
	}
	
	private fun model(path: String) = ResourceLocation(MODID, "block/$path")
	
	/** Модели, на которые не ссылаются состояния блоков: их подставляет [modifyBakingResult] */
	private fun registerAdditional(e: ModelEvent.RegisterAdditional) {
		for (meta in 1..3) e.register(model("alf_storage$meta"))
		e.register(model("living_cobble3_alt"))
	}
	
	private fun modifyBakingResult(e: ModelEvent.ModifyBakingResult) {
		val models = e.models
		
		// BlockAlfStorage.registerBlockIcons: старая текстура вариантов 1–3, если опция newStorageTexture выключена
		for (block in AlfheimBlocks.alfStorage) {
			val storage = block as BlockAlfStorage
			if (storage.meta !in 1..3 || storage.hasNewTexture(storage.meta)) continue
			val old = models[model("alf_storage${storage.meta}")] ?: continue
			models[BlockModelShaper.stateToModelLocation(block.defaultBlockState())] = old
			models[ModelResourceLocation(BuiltInRegistries.BLOCK.getKey(block), "inventory")] = old
		}
		
		// BlockLivingCobble.getIcon: у варианта 3 вторая текстура там, где формула автора даёт 0
		val cobble = AlfheimBlocks.livingcobble[3]
		val location = BlockModelShaper.stateToModelLocation(cobble.defaultBlockState())
		val base = models[location]
		val alt = models[model("living_cobble3_alt")]
		if (base != null && alt != null) models[location] = AltByPosition(base, alt)
	}
	
	/** Модель, у которой на части координат квадраты другой модели — как `getIcon(world, x, y, z, side)` 1.7.10 */
	private class AltByPosition(base: BakedModel, private val alt: BakedModel): BakedModelWrapper<BakedModel>(base) {
		
		override fun getModelData(level: BlockAndTintGetter, pos: BlockPos, state: BlockState, modelData: ModelData): ModelData {
			val x = pos.x
			val y = pos.y
			val z = pos.z
			return modelData.derive().with(ALT, ((31 * (31 * x + y) + z)) % 2 == 0).build()
		}
		
		override fun getQuads(state: BlockState?, side: Direction?, rand: RandomSource, extraData: ModelData, renderType: RenderType?): List<BakedQuad> =
			if (extraData.get(ALT) == true) alt.getQuads(state, side, rand, extraData, renderType) else super.getQuads(state, side, rand, extraData, renderType)
		
		companion object {
			
			val ALT = ModelProperty<Boolean>()
		}
	}
}
