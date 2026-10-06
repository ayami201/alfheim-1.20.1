package alfheim.port.legacy

import alfheim.port.registry.LegacyRegistration
import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.tags.BlockTags
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.ChunkStatus
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration
import net.minecraftforge.common.IPlantable

/*
 * Мир по координатам с сигнатурами 1.7.10 (SPEC, Р-4): вызовы World и IBlockAccess автора, смысл которых в 1.20.1
 * не изменился. metadata, иконки и числовые id сюда не входят — они переписываются на месте вызова (MAPPING.md).
 * Чтение — у BlockGetter (IBlockAccess 1.7.10), запись — у LevelAccessor.
 */

fun BlockGetter.getBlock(x: Int, y: Int, z: Int): Block = getBlockState(BlockPos(x, y, z)).block

fun BlockGetter.getTileEntity(x: Int, y: Int, z: Int): BlockEntity? = getBlockEntity(BlockPos(x, y, z))

/** Воздух 1.20.1 — ещё и воздух пещер и пустоты; в 1.7.10 их не было */
fun BlockGetter.isAirBlock(x: Int, y: Int, z: Int) = getBlockState(BlockPos(x, y, z)).isAir

/** `setBlock(x, y, z, block)` 1.7.10 ставил блок с metadata 0 и флагами 3; metadata 0 — состояние по умолчанию */
fun LevelAccessor.setBlock(x: Int, y: Int, z: Int, block: Block) = setBlock(BlockPos(x, y, z), block.defaultBlockState(), 3)

/**
 * Вместо `setBlock(x, y, z, block, meta, flags)`: состояние вместо metadata выбирает вызывающий код (SPEC, Р-5).
 * Младшие флаги те же, что в 1.7.10: 1 — обновить соседей, 2 — отправить клиентам, 4 — не перерисовывать
 */
fun LevelAccessor.setBlock(x: Int, y: Int, z: Int, state: BlockState, flags: Int) = setBlock(BlockPos(x, y, z), state, flags)

fun LevelAccessor.setBlockToAir(x: Int, y: Int, z: Int) = setBlock(BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 3)

fun Level.removeTileEntity(x: Int, y: Int, z: Int) = removeBlockEntity(BlockPos(x, y, z))

fun Level.notifyBlocksOfNeighborChange(x: Int, y: Int, z: Int, block: Block) = updateNeighborsAt(BlockPos(x, y, z), block)

fun LevelAccessor.scheduleBlockUpdate(x: Int, y: Int, z: Int, block: Block, delay: Int) = scheduleTick(BlockPos(x, y, z), block, delay)

fun Level.spawnEntityInWorld(entity: Entity) = addFreshEntity(entity)

val Level.isRemote: Boolean get() = isClientSide

/** Номера измерения 1.7.10 (`dimension`) в 1.20.1 нет: измерение хранится строкой — id мира, `minecraft:overworld` */
val Level.dimensionId: String get() = dimension().location().toString()

/** Измерение по id строкой ([dimensionId]) */
fun dimensionKey(id: String): ResourceKey<Level> = ResourceKey.create(Registries.DIMENSION, ResourceLocation(id))

/** `checkChunksExist` 1.7.10: все чанки области загружены — чтобы чтение блоков не загружало и не создавало чанки */
@Suppress("DEPRECATION")
fun LevelReader.checkChunksExist(minX: Int, minY: Int, minZ: Int, maxX: Int, maxY: Int, maxZ: Int) = hasChunksAt(minX, minY, minZ, maxX, maxY, maxZ)

/** `getBlockLightValue` 1.7.10: свет в точке — наибольший из света блоков и света неба с поправкой на время суток */
fun Level.getBlockLightValue(x: Int, y: Int, z: Int) = getMaxLocalRawBrightness(BlockPos(x, y, z))

/** `getStrongestIndirectPower` 1.7.10 — сильнейший сигнал красного камня от соседей */
fun Level.getStrongestIndirectPower(x: Int, y: Int, z: Int) = getBestNeighborSignal(BlockPos(x, y, z))

/**
 * `canSustainPlant` Forge 1.7.10: держит ли блок в точке растение [plantable] на своей стороне [direction]. Решает
 * Forge 1.20.1 — по типу растения, как в 1.7.10; вместо блока — его состояние в точке
 */
@Suppress("UNUSED_PARAMETER")
fun Block.canSustainPlant(world: BlockGetter, x: Int, y: Int, z: Int, direction: ForgeDirection, plantable: IPlantable): Boolean {
	val pos = BlockPos(x, y, z)
	return world.getBlockState(pos).canSustainPlant(world, pos, direction.direction!!, plantable)
}

/**
 * `canSustainLeaves` Forge 1.7.10 — бревно, у которого листва не опадает: в 1.20.1 это тег `minecraft:logs` (по нему
 * ищет бревно листва ванилы). Бревно автора, которое переопределяло `canSustainLeaves`, — в этом теге (генерация данных)
 */
@Suppress("UNUSED_PARAMETER")
fun Block.canSustainLeaves(world: BlockGetter, x: Int, y: Int, z: Int) = builtInRegistryHolder().`is`(BlockTags.LOGS)

/** `isLeaves` Forge 1.7.10 — листва: в 1.20.1 тег `minecraft:leaves` */
@Suppress("UNUSED_PARAMETER")
fun Block.isLeaves(world: BlockGetter, x: Int, y: Int, z: Int) = builtInRegistryHolder().`is`(BlockTags.LEAVES)

/** `isAir(world, x, y, z)` Forge 1.7.10 — воздух в точке; воздух 1.20.1 — ещё и воздух пещер и пустоты */
@Suppress("UNUSED_PARAMETER")
fun Block.isAir(world: BlockGetter, x: Int, y: Int, z: Int) = world.getBlockState(BlockPos(x, y, z)).isAir

/**
 * `isReplaceable(world, x, y, z)` Forge 1.7.10 — блок в точке можно заменить: у блока порта — его метод 1.7.10
 * ([LegacyBlockMethods.isReplaceable]), у прочих — заменяемый материал
 */
fun Block.isReplaceable(world: BlockGetter, x: Int, y: Int, z: Int) =
	if (this is LegacyBlockMethods) (this as LegacyBlockMethods).isReplaceable(world, x, y, z) else world.getBlockState(BlockPos(x, y, z)).canBeReplaced()

/**
 * `onPlantGrow` Forge 1.7.10: под выросшим деревом трава и пашня становятся землёй (флаг 2), прочие блоки — как
 * были; вместо блока — его состояние в точке
 */
@Suppress("UNUSED_PARAMETER")
fun Block.onPlantGrow(world: LevelAccessor, x: Int, y: Int, z: Int, sourceX: Int, sourceY: Int, sourceZ: Int) {
	val pos = BlockPos(x, y, z)
	val state = world.getBlockState(pos)
	if (state.`is`(Blocks.GRASS_BLOCK) || state.`is`(Blocks.FARMLAND)) world.setBlock(pos, Blocks.DIRT.defaultBlockState(), 2)
}

/**
 * `isNormalCube(world, x, y, z)` Forge 1.7.10: блок в точке — непрозрачный полный куб, который не даёт сигнал
 * красного камня. В 1.20.1 это `isRedstoneConductor` состояния (так метод и назывался до 1.16)
 */
@Suppress("UNUSED_PARAMETER")
fun Block.isNormalCube(world: BlockGetter, x: Int, y: Int, z: Int): Boolean {
	val pos = BlockPos(x, y, z)
	return world.getBlockState(pos).isRedstoneConductor(world, pos)
}

/**
 * `world.getBiomeGenForCoords(x, z).plantFlower(world, random, x, y, z)` Forge 1.7.10 — цветок биома в точке, если он
 * там удержится. Цветы биома 1.20.1 — его цветочные узоры генерации: ставится цветок первого, как от костной муки на
 * траве 1.20.1. Биом — в самой точке: в 1.20.1 он зависит и от высоты
 */
fun Level.plantFlower(random: RandomSource, x: Int, y: Int, z: Int) {
	val level = this as? ServerLevel ?: return
	val pos = BlockPos(x, y, z)
	val flowers = level.getBiome(pos).value().generationSettings.flowerFeatures
	if (flowers.isEmpty()) return
	(flowers[0].config() as RandomPatchConfiguration).feature().value().place(level, level.chunkSource.generator, random, pos)
}

/**
 * `canBlockStay` 1.7.10 у любого блока: удержится ли он в точке. Растение порта решает своим правилом ([Bush1710]),
 * прочие — как `canSurvive` 1.20.1 их состояния по умолчанию
 */
fun Block.canBlockStay(world: Level, x: Int, y: Int, z: Int) =
	if (this is Bush1710) canBlockStay(world, x, y, z) else defaultBlockState().canSurvive(world, BlockPos(x, y, z))

/**
 * `canPlaceBlockAt` 1.7.10 у любого блока: можно ли поставить его в точку. Двойное растение порта решает своим
 * правилом ([DoublePlant1710]), прочие — место заменяемое и блок там удержится (`canSurvive` 1.20.1)
 */
fun Block.canPlaceBlockAt(world: Level, x: Int, y: Int, z: Int): Boolean {
	if (this is DoublePlant1710) return canPlaceBlockAt(world, x, y, z)
	val pos = BlockPos(x, y, z)
	return world.getBlockState(pos).canBeReplaced() && defaultBlockState().canSurvive(world, pos)
}

/**
 * Номер варианта блока в точке — metadata 1.7.10 блока, варианты которого в порту — отдельные блоки (SPEC, Р-5); у
 * блока без вариантов — 0. Metadata-состояние (поворот, рост, половина) сюда не входит: его код блока читает из состояния
 */
fun BlockGetter.getBlockVariant(x: Int, y: Int, z: Int) = (getBlock(x, y, z) as? LegacyBlock)?.variant ?: 0

/**
 * `block == other` 1.7.10, когда у блока есть варианты metadata: в 1.7.10 это один блок, в порту — разные блоки с одним
 * именем 1.7.10 (SPEC, Р-5)
 */
fun Block.isSameBlock1710(other: Block) =
	this === other || LegacyRegistration.blocks[this]?.oldName.let { it != null && it == LegacyRegistration.blocks[other]?.oldName }

/**
 * Может ли в кубе с центром ([x], [y], [z]) и полустороной [range] быть блок, состояние которого подходит под
 * [predicate]: смотрит палитры секций загруженных чанков, сами блоки не перебирает. `false` — такого блока там точно
 * нет: обход автора по всем блокам куба можно пропустить (PORT-OPT). Незагруженный чанк — без блоков, как у
 * `getBlock` (воздух)
 */
fun Level.mayContain(x: Int, y: Int, z: Int, range: Int, predicate: (BlockState) -> Boolean): Boolean {
	for (cx in SectionPos.blockToSectionCoord(x - range)..SectionPos.blockToSectionCoord(x + range))
		for (cz in SectionPos.blockToSectionCoord(z - range)..SectionPos.blockToSectionCoord(z + range)) {
			val chunk = getChunk(cx, cz, ChunkStatus.FULL, false) ?: continue
			for (cy in SectionPos.blockToSectionCoord(y - range)..SectionPos.blockToSectionCoord(y + range)) {
				val index = chunk.getSectionIndexFromSectionY(cy)
				if (index < 0 || index >= chunk.sectionsCount) continue
				val section = chunk.getSection(index)
				if (!section.hasOnlyAir() && section.maybeHas(predicate)) return true
			}
		}
	return false
}
