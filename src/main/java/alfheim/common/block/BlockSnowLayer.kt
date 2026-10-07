package alfheim.common.block

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md); таяние в измерении Альфхейм
// (AlfheimConfigHandler) — КТ-6
import alexsocol.asjlib.*
//import alfheim.AlfheimCore
import alfheim.common.block.base.BlockMod
//import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.AlfheimTab
import alfheim.port.legacy.*
import alfheim.port.legacy.botania.IHornHarvestable
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.phys.shapes.*

class BlockSnowLayer: BlockMod(Material.snow), IHornHarvestable {
	
	init {
		setBlockBounds(0f, 0f, 0f, 1f, 0.125f, 1f)
		setBlockName("SnowLayer")
		setCreativeTab(AlfheimTab)
		setHardness(0.1f)
		setHarvestLevel("shovel", 0)
		stepSound = soundTypeSnow
		
		setSizeForMeta(0)
		tickRandomly = true
		// PORT: metadata 0–7 (слоёв на один меньше) — свойство layers ванилы, 1–8
		registerDefaultState(stateDefinition.any().setValue(LAYERS, 1))
	}
	
	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(LAYERS)
	}
	
	/* PORT: иконка → модели слоёв снега ванилы (alfheim.port.data.AlfheimBlockStates)
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(reg: IIconRegister) {
		blockIcon = reg.registerIcon("snow")
	}
	*/
	
	// PORT: рамка столкновений по metadata — форма состояния; рамки автора — COLLISION_SHAPES
	override fun getCollisionShape(state: BlockState, level: IBlockAccess, pos: BlockPos, context: CollisionContext) = COLLISION_SHAPES[state.getValue(LAYERS) - 1]
	/*
	override fun getCollisionBoundingBoxFromPool(world: World, x: Int, y: Int, z: Int): AxisAlignedBB {
		val l = world.getBlockMetadata(x, y, z) and 7
		val f = 0.125f
		return getBoundingBox(x + minX, y + minY, z + minZ, x + maxX, (y + l * f), z + maxZ)
	}
	*/
	
	override fun isOpaqueCube() = false
	// PORT: модель — alfheim.port.data.AlfheimBlockStates; рамка предмета (setBlockBoundsForItemRender) — модель предмета
	// в один слой; рамка по metadata (setBlockBoundsBasedOnState) — форма состояния, рамки setSizeForMeta — SHAPES
//	override fun renderAsNormalBlock() = false
//	override fun setBlockBoundsForItemRender() = setSizeForMeta(0)
	override fun getShape(state: BlockState, level: IBlockAccess, pos: BlockPos, context: CollisionContext) = SHAPES[state.getValue(LAYERS) - 1]
//	override fun setBlockBoundsBasedOnState(world: IBlockAccess, x: Int, y: Int, z: Int) = setSizeForMeta(world.getBlockMetadata(x, y, z))
	
	fun setSizeForMeta(meta: Int) {
		val j = meta and 7
		val f = (2 * (1 + j)).F / 16f
		setBlockBounds(0f, 0f, 0f, 1f, f, 1f)
	}
	
	// PORT: установку и удержание 1.20.1 решает canSurvive — по этому же правилу; в мире генерации (не World) — тоже
	fun canPlaceBlockAt(world: IBlockAccess, x: Int, y: Int, z: Int): Boolean {
//	override fun canPlaceBlockAt(world: World, x: Int, y: Int, z: Int): Boolean {
		val block = world.getBlock(x, y - 1, z)
		// PORT: metadata слоя снега ниже — layers − 1
		return if (block !== Blocks.ICE && block !== Blocks.PACKED_ICE) if (block.isLeaves(world, x, y - 1, z)) true else if (block === this && world.getBlockState(BlockPos(x, y - 1, z)).getValue(LAYERS) - 1 and 7 == 7) true else block.isOpaqueCube && block.material.blocksMovement() else false
//		return if (block !== Blocks.ice && block !== Blocks.packed_ice) if (block.isLeaves(world, x, y - 1, z)) true else if (block === this && world.getBlockMetadata(x, y - 1, z) and 7 == 7) true else block.isOpaqueCube && block.material.blocksMovement() else false
	}
	
	override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos) = canPlaceBlockAt(level, pos.x, pos.y, pos.z)
	
	override fun onNeighborBlockChange(world: World, x: Int, y: Int, z: Int, block: Block?) {
		func_150155_m(world, x, y, z)
	}
	
	private fun func_150155_m(world: World, x: Int, y: Int, z: Int): Boolean {
		return if (!canPlaceBlockAt(world, x, y, z)) {
			world.setBlockToAir(x, y, z)
			false
		} else
			true
	}
	
	// PORT: лут — таблица (alfheim.port.data.AlfheimBlockLoot); 1.7.10 звал harvestBlock, когда блок уже убран, —
	// setBlockToAir ничего не менял
	/*
	override fun harvestBlock(world: World, player: EntityPlayer?, x: Int, y: Int, z: Int, meta: Int) {
		super.harvestBlock(world, player, x, y, z, meta)
		world.setBlockToAir(x, y, z)
	}
	*/
	
	fun getItemDropped(meta: Int, rand: Random?, fortune: Int) = Items.SNOWBALL
//	override fun getItemDropped(meta: Int, rand: Random?, fortune: Int) = Items.snowball!!
	
	fun quantityDropped(rand: Random?) = 1
//	override fun quantityDropped(rand: Random?) = 1
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, rand: Random?) {
		// PORT: КТ-6 — измерение Альфхейм (AlfheimConfigHandler.dimensionIDAlfheim): вне зимы слой снега тает только там.
		// Шанс таяния — 1/20 за случайный тик (`== 0`), как у снежной травы и слоя снега ванилы; у автора — 19/20 (`!= 0`):
		// исправлено решением владельца (BUGS.md, B-045), вернуть как у автора — `!= 0`. Ниже — строка для КТ-6 и строка автора
//		if (!AlfheimCore.winter && world.provider.dimensionId == AlfheimConfigHandler.dimensionIDAlfheim && !world.isRemote && world.rand.nextInt(20) == 0)
//			world.setBlockToAir(x, y, z)
//		if (!AlfheimCore.winter && world.provider.dimensionId == AlfheimConfigHandler.dimensionIDAlfheim && !world.isRemote && world.rand.nextInt(20) != 0)
//			world.setBlockToAir(x, y, z)
	}
	
	/* PORT: грани — модели слоёв снега ванилы: верх рисуется всегда, остальные — если сосед не скрывает грань
	@SideOnly(Side.CLIENT)
	override fun shouldSideBeRendered(world: IBlockAccess, x: Int, y: Int, z: Int, side: Int) =
		if (side == 1) true else super.shouldSideBeRendered(world, x, y, z, side)
	*/
	
	fun quantityDropped(meta: Int, fortune: Int, random: Random?) = (meta and 7) + 1
//	override fun quantityDropped(meta: Int, fortune: Int, random: Random?) = (meta and 7) + 1
	
	override fun isReplaceable(world: IBlockAccess, x: Int, y: Int, z: Int): Boolean {
		// PORT: metadata — layers − 1
		val meta = world.getBlockState(BlockPos(x, y, z)).getValue(LAYERS) - 1
//		val meta = world.getBlockMetadata(x, y, z)
		return if (meta >= 7) false else blockMaterial.isReplaceable
	}
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hx: Float, hy: Float, hz: Float): Boolean {
		// PORT: metadata — свойство layers (metadata + 1)
		val state = world.getBlockState(BlockPos(x, y, z))
		val meta = state.getValue(LAYERS) - 1
//		val meta = world.getBlockMetadata(x, y, z)
		if (player.currentEquippedItem?.item === this.toItem() && meta < 7) {
			world.setBlock(x, y, z, state.setValue(LAYERS, meta + 2), 3)
//			world.setBlockMetadataWithNotify(x, y, z, meta + 1, 3)
			return true
		}
		
		if (player.currentEquippedItem == null && meta > 0) {
			world.setBlock(x, y, z, state.setValue(LAYERS, meta), 3)
//			world.setBlockMetadataWithNotify(x, y, z, meta - 1, 3)
			if (!player.inventory.addItemStackToInventory(ItemStack(Items.SNOWBALL))) {
				player.dropPlayerItemWithRandomChoice(ItemStack(Items.SNOWBALL), true)
			}
//			if (!player.inventory.addItemStackToInventory(ItemStack(Items.snowball))) {
//				player.dropPlayerItemWithRandomChoice(ItemStack(Items.snowball), true)
//			}
			return true
		}
		
		return false
	}
	
	override fun canHornHarvest(world: World?, x: Int, y: Int, z: Int, stack: ItemStack?, hornType: IHornHarvestable.EnumHornType) =
		hornType == IHornHarvestable.EnumHornType.COVERING
	
	override fun hasSpecialHornHarvest(world: World?, x: Int, y: Int, z: Int, stack: ItemStack?, hornType: IHornHarvestable.EnumHornType?) = false
	
	override fun harvestByHorn(world: World?, x: Int, y: Int, z: Int, stack: ItemStack?, hornType: IHornHarvestable.EnumHornType?) = Unit
	
	companion object {
		// PORT: metadata 0–7 — свойство layers ванилы (metadata + 1)
		val LAYERS: IntegerProperty = BlockStateProperties.LAYERS
		
		/** Рамки по metadata — setSizeForMeta: высота (metadata + 1) / 8 */
		private val SHAPES = Array(8) { meta -> Shapes.box(0.0, 0.0, 0.0, 1.0, (2 * (1 + meta)) / 16.0, 1.0) }
		
		/** Рамки столкновений по metadata — getCollisionBoundingBoxFromPool: высота metadata / 8, у одного слоя — нет */
		private val COLLISION_SHAPES = Array(8) { meta -> Shapes.box(0.0, 0.0, 0.0, 1.0, meta * 0.125, 1.0) }
	}
}
