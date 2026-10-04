package alfheim.port.client

import alfheim.AlfheimCore
import alfheim.api.ModInfo.MODID
import alfheim.common.block.*
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.*
import alfheim.port.data.AlfheimItemModels
import alfheim.port.legacy.Leaves1710
import alfheim.port.registry.LegacyRegistration
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.BlockModelShaper
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.client.resources.model.*
import net.minecraft.core.*
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.SlabBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.SlabType
import net.minecraftforge.client.event.ModelEvent
import net.minecraftforge.client.model.BakedModelWrapper
import net.minecraftforge.client.model.data.*
import net.minecraftforge.eventbus.api.*
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import kotlin.math.abs

/**
 * Модели блоков и предметов, которые в 1.7.10 выбирали иконку в коде (`registerBlockIcons` / `registerIcons` /
 * `getIcon`), а не одной моделью (MAPPING.md, «Блоки и предметы»). Сами модели строит генерация данных
 * (`alfheim.port.data.AlfheimBlockStates`, `alfheim.port.data.AlfheimItemModels`).
 */
object AlfheimModels {
	
	fun register(bus: IEventBus) {
		bus.addListener(EventPriority.NORMAL, false, ModelEvent.RegisterAdditional::class.java, ::registerAdditional)
		bus.addListener(EventPriority.NORMAL, false, ModelEvent.ModifyBakingResult::class.java, ::modifyBakingResult)
		bus.addListener(EventPriority.NORMAL, false, FMLClientSetupEvent::class.java, ::registerProperties)
	}
	
	/** Свойства моделей предметов: иконка, которую 1.7.10 выбирал по стаку (`getIcon(stack, pass)`) */
	private fun registerProperties(e: FMLClientSetupEvent) = e.enqueueWork {
		// ItemElvenFood.getIcon: пиво с именем «Cerveza Cristal» рисуется своей иконкой
		ItemProperties.register(AlfheimItems.elvenFood[ElvenFoodMetas.Beer.I], AlfheimItemModels.CC_PROPERTY) { stack, _, _, _ -> if (ItemElvenFood.isCC(stack)) 1f else 0f }
	}
	
	private fun model(path: String) = ResourceLocation(MODID, "block/$path")
	
	/** Модели, на которые не ссылаются состояния блоков: их подставляет [modifyBakingResult] */
	private fun registerAdditional(e: ModelEvent.RegisterAdditional) {
		for (meta in 1..3) e.register(model("alf_storage$meta"))
		e.register(model("living_cobble3_alt"))
		for (i in 2..4) for (name in listOf("living_mountain", "living_mountain0_slab", "living_mountain0_slab_top")) e.register(model("${name}_icon$i"))
		e.register(ResourceLocation(MODID, "item/${AlfheimItemModels.INFUSED_CANDY}"))
		for (leaves in leaves()) e.register(model(opaque(leaves)))
	}
	
	/** Листва автора ([Leaves1710]) */
	private fun leaves() = LegacyRegistration.blocks.keys.filterIsInstance<Leaves1710>()
	
	/** Модель листвы с непрозрачной текстурой `_opaque` (`BlockLeavesMod.registerBlockIcons`) */
	private fun opaque(leaves: Leaves1710) = LegacyRegistration.blocks[leaves]!!.id.path + "_opaque"
	
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
		
		// BlockLivingMountain.getIcon(world, x, y, z, side) и BlockLivingMountainSlab: 4 иконки, грань выбирает по
		// координатам. Двойная плита — блок живой горы целиком
		for (block in listOf(AlfheimFluffBlocks.livingMountain, AlfheimFluffBlocks.livingMountainSlab))
			for (state in block.stateDefinition.possibleStates) {
				val name = when (if (state.hasProperty(SlabBlock.TYPE)) state.getValue(SlabBlock.TYPE) else null) {
					null, SlabType.DOUBLE -> "living_mountain"
					SlabType.BOTTOM       -> "living_mountain0_slab"
					SlabType.TOP          -> "living_mountain0_slab_top"
				}
				val stateLocation = BlockModelShaper.stateToModelLocation(state)
				val icons = listOf(models[stateLocation]) + (2..4).map { models[model("${name}_icon$it")] }
				if (icons.all { it != null }) models[stateLocation] = IconByPosition(icons.map { it!! }, ::livingMountainIcon)
			}
		
		// BlockLeavesMod.getIcon: при «быстрой» графике (`setGraphicsLevel`) — непрозрачная текстура `_opaque`, и в мире,
		// и в инвентаре. Сплошной листву рисует 1.20.1 сама (LeavesBlock)
		for (leaves in leaves()) {
			val opaque = models[model(opaque(leaves))] ?: continue
			for (state in leaves.stateDefinition.possibleStates) {
				val location = BlockModelShaper.stateToModelLocation(state)
				models[location]?.let { models[location] = FancyGraphics(it, opaque) }
			}
			val item = ModelResourceLocation(BuiltInRegistries.BLOCK.getKey(leaves), "inventory")
			models[item]?.let { models[item] = FancyGraphics(it, opaque) }
		}
		
		// ItemElvenResource.getIcon: на праздник (AlfheimCore.jingleTheBells) прутик рисуется конфетой
		if (AlfheimCore.jingleTheBells) {
			val twig = AlfheimItems.elvenResource[ElvenResourcesMetas.InfusedDreamwoodTwig.I]
			val candy = models[ResourceLocation(MODID, "item/${AlfheimItemModels.INFUSED_CANDY}")]
			if (candy != null) models[ModelResourceLocation(BuiltInRegistries.ITEM.getKey(twig), "inventory")] = candy
		}
	}
	
	/** Номер иконки грани `BlockLivingMountain.getIcon(world, x, y, z, side)` (0 — первая) */
	private fun livingMountainIcon(pos: BlockPos, side: Direction): Int {
		val x = pos.x
		val y = pos.y
		val z = pos.z
		return when (side.get3DDataValue()) {
			0, 1 -> abs(x % 2) + abs(z % 2) * 2
			2, 3 -> abs(x % 2) + abs(y % 2) * 2
			4, 5 -> abs(z % 2) + abs(y % 2) * 2
			else -> 0
		}
	}
	
	/**
	 * Модель, у которой каждая грань — из одной из моделей [icons] с одинаковой формой: номер выбирает [index] по
	 * координатам блока и стороне грани, как `getIcon(world, x, y, z, side)` 1.7.10. Без координат (предмет) — первая
	 */
	private class IconByPosition(private val icons: List<BakedModel>, private val index: (BlockPos, Direction) -> Int): BakedModelWrapper<BakedModel>(icons[0]) {
		
		override fun getModelData(level: BlockAndTintGetter, pos: BlockPos, state: BlockState, modelData: ModelData): ModelData =
			modelData.derive().with(POS, pos.immutable()).build()
		
		override fun getQuads(state: BlockState?, side: Direction?, rand: RandomSource, extraData: ModelData, renderType: RenderType?): List<BakedQuad> {
			val pos = extraData.get(POS) ?: return super.getQuads(state, side, rand, extraData, renderType)
			if (side != null) return icons[index(pos, side)].getQuads(state, side, rand, extraData, renderType)
			// грани без стороны отсечения (верх нижней плиты) — по направлению самой грани
			return Direction.values().flatMap { dir -> icons[index(pos, dir)].getQuads(state, null, rand, extraData, renderType).filter { it.direction == dir } }
		}
		
		companion object {
			
			val POS = ModelProperty<BlockPos>()
		}
	}
	
	/**
	 * Модель, у которой при «быстрой» графике квадраты модели [fast] — как иконка, которую 1.7.10 выбирал по настройке
	 * графики. Настройку игра меняет с пересборкой чанков, модели при этом не перезагружаются — поэтому выбор здесь, при
	 * каждой сборке
	 */
	private class FancyGraphics(fancy: BakedModel, private val fast: BakedModel): BakedModelWrapper<BakedModel>(fancy) {
		
		override fun getQuads(state: BlockState?, side: Direction?, rand: RandomSource, extraData: ModelData, renderType: RenderType?): List<BakedQuad> =
			if (ItemBlockRenderTypes.isFancy()) super.getQuads(state, side, rand, extraData, renderType) else fast.getQuads(state, side, rand, extraData, renderType)
		
		// предмет рисуется своими проходами: у обёртки 1.20.1 это модель внутри неё, мимо getQuads выше
		override fun getRenderPasses(itemStack: ItemStack, fabulous: Boolean): List<BakedModel> = listOf(this)
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
