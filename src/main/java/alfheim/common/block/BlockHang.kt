package alfheim.common.block

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.api.ModInfo
import alfheim.common.core.util.AlfheimTab
import alfheim.common.entity.EntityFallingHang
import alfheim.port.legacy.*
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.*

// PORT: стадия роста (metadata) — отдельный блок, как вариант BlockModMeta (SPEC, Р-5): номер стадии — meta, создают
// массивом `Array(sub) { BlockX(it) }`; рост — замена блоком следующей стадии (variant1710)
open class BlockHang(mat: Material, name: String, sub: Int, val fallable: Boolean = true, meta: Int = 0): BlockModMeta(mat, sub, ModInfo.MODID, name, AlfheimTab, 0.3f, meta = meta) {
//open class BlockHang(mat: Material, name: String, sub: Int, val fallable: Boolean = true): BlockModMeta(mat, sub, ModInfo.MODID, name, AlfheimTab, 0.3f) {
	
	init {
		tickRandomly = fallable
	}
	
	override fun onNeighborBlockChange(world: World, x: Int, y: Int, z: Int, block: Block) = checkChange(world, x, y, z)
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, random: Random) {
		if (fallable) {
			// PORT: metadata в точке — номер варианта блока там, у этого блока — его стадия (SPEC, Р-5)
			val nextMeta = world.getBlockVariant(x, y, z) + 1
//			val nextMeta = world.getBlockMetadata(x, y, z) + 1
			
			if (random.nextInt(4 * nextMeta) == 0) {
				if (nextMeta >= subtypes) {
					fall(world, x, y - 1, z)
					// PORT: стадия (metadata) — блок своей стадии (variant1710)
					world.setBlock(x, y, z, variant1710(0).defaultBlockState(), 3)
//					world.setBlock(x, y, z, this, 0, 3)
					return
				} else
					world.setBlock(x, y, z, variant1710(nextMeta).defaultBlockState(), 3)
//					world.setBlockMetadataWithNotify(x, y, z, nextMeta, 3)
			}
		}
		
		checkChange(world, x, y, z)
	}
	
	fun checkChange(world: World, x: Int, y: Int, z: Int) {
		if (canBlockStay(world, x, y, z)) return
		fall(world, x, y, z)
	}
	
	fun fall(world: World, x: Int, y: Int, z: Int) {
		if (fallable) EntityFallingHang(world).apply {
			setPosition(x + 0.5, y.D, z + 0.5)
			block = this@BlockHang
			// PORT: metadata в точке — номер варианта блока там (SPEC, Р-5)
			meta = world.getBlockVariant(x, y, z)
//			meta = world.getBlockMetadata(x, y, z)
		}.spawn()
		world.setBlockToAir(x, y, z)
	}
	
	// PORT: и установку, и удержание в 1.20.1 решает canSurvive (canPlaceBlockAt и canBlockStay 1.7.10); мир генерации
	// (не World) 1.7.10 не спрашивал — там блок держится
	override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos) = level !is World || canBlockStay(level, pos.x, pos.y, pos.z)
//	override fun canPlaceBlockAt(world: World, x: Int, y: Int, z: Int) = canBlockStay(world, x, y, z)
	
	// PORT: canBlockStay 1.7.10 — у блока порта свой метод (в Block1710 его нет); по умолчанию 1.7.10 блок держится
	open fun canBlockStay(world: World, x: Int, y: Int, z: Int) = true
	
	// PORT: столкновений нет (getCollisionBoundingBoxFromPool — null)
	override fun getCollisionShape(state: BlockState, level: IBlockAccess, pos: BlockPos, context: CollisionContext): VoxelShape = Shapes.empty()
//	override fun getCollisionBoundingBoxFromPool(world: World, x: Int, y: Int, z: Int) = null
	
	override fun isOpaqueCube() = false
	
	// PORT: модель — крест с текстурой стадии (alfheim.port.data.AlfheimBlockStates)
//	override fun renderAsNormalBlock() = false
//
//	override fun getRenderType() = 1
}