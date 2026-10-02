package alfheim.port.legacy

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

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

fun Level.notifyBlocksOfNeighborChange(x: Int, y: Int, z: Int, block: Block) = updateNeighborsAt(BlockPos(x, y, z), block)

fun LevelAccessor.scheduleBlockUpdate(x: Int, y: Int, z: Int, block: Block, delay: Int) = scheduleTick(BlockPos(x, y, z), block, delay)

fun Level.spawnEntityInWorld(entity: Entity) = addFreshEntity(entity)

val Level.isRemote: Boolean get() = isClientSide
