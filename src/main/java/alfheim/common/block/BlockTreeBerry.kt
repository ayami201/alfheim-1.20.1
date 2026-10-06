package alfheim.common.block

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.TileTreeBerry
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenFoodMetas
import alfheim.port.legacy.*
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.item.Item
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.shapes.*
import net.minecraftforge.common.*

// PORT: КТ-9 — лексикон (ILexiconable)
class BlockTreeBerry(val leaves: Block, val type: Int): BlockContainerMod(Material.vine)/*, ILexiconable*/, IPlantable, IGrowable {
	
	// PORT: иконки → модели (alfheim.port.data.AlfheimBlockStates)
//	lateinit var icons: Array<IIcon>
	
	init {
		setBlockName("TreeBerry$type")
		setHardness(0f)
		tickRandomly = true
		setStepSound(soundTypeGrass)
		// PORT: metadata 0–2 — зрелость ягоды, свойство age
		registerDefaultState(stateDefinition.any().setValue(AGE, 0))
	}
	
	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(AGE)
	}
	
	override fun isReplaceable(world: IBlockAccess?, x: Int, y: Int, z: Int) = false
	
	// PORT: блок-сущность 1.20.1 создаётся сразу в своей точке (ITileEntityProvider прослойки)
	override fun newBlockEntity(pos: BlockPos, state: BlockState) = TileTreeBerry(pos, state)
//	override fun createNewTileEntity(world: World?, meta: Int) = TileTreeBerry()
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, random: Random) {
		checkChange(world, x, y, z)
		
		if (random.nextInt(10) != 0) return
		
		// PORT: metadata — свойство age; ягоду мог убрать checkChange, а зрелости у воздуха 1.20.1 нет
		val pos = BlockPos(x, y, z)
		val state = world.getBlockState(pos)
		if (!state.`is`(this)) return
		val meta = state.getValue(AGE)
//		val meta = world.getBlockMetadata(x, y, z)
		if (meta >= 2) return
		
		world.setBlock(pos, state.setValue(AGE, meta + 1), 3)
//		world.setBlockMetadataWithNotify(x, y, z, meta + 1, 3)
	}
	
	fun checkChange(world: World, x: Int, y: Int, z: Int) {
		if (canBlockStay(world, x, y, z)) return
		// PORT: dropBlockAsItem → лут состояния (таблица генерации данных)
		val pos = BlockPos(x, y, z)
		dropResources(world.getBlockState(pos), world, pos)
//		dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0)
		world.setBlockToAir(x, y, z)
	}
	
	// PORT: рамка блока по metadata (setBlockBoundsBasedOnState) — форма состояния; рамки автора — SHAPES
	override fun getShape(state: BlockState, level: IBlockAccess, pos: BlockPos, context: CollisionContext) = SHAPES[state.getValue(AGE)]
	/*
	override fun getSelectedBoundingBoxFromPool(world: World, x: Int, y: Int, z: Int): AxisAlignedBB {
		setBlockBoundsBasedOnState(world, x, y, z)
		return super.getSelectedBoundingBoxFromPool(world, x, y, z)
	}
	
	override fun setBlockBoundsBasedOnState(world: IBlockAccess, x: Int, y: Int, z: Int) {
		val block = world.getBlock(x, y, z)
		when (world.getBlockMetadata(x, y, z)) {
			0 -> block.setBlockBounds(0.28125f, 0.5625f, 0.28125f, 0.71875f, 1f, 0.71875f)
			1 -> block.setBlockBounds(0.21875f, 0.375f, 0.21875f, 0.78125f, 1f, 0.78125f)
			else -> block.setBlockBounds(0.15625f, 0.125f, 0.15625f, 0.84375f, 1f, 0.84375f)
		}
	}
	*/
	
	// PORT: удержание и установку 1.20.1 решает canSurvive (canBlockStay, canPlaceBlockAt 1.7.10): в мире генерации
	// (не World) — тоже; соседа — neighborChanged (onNeighborBlockChange 1.7.10)
	fun canBlockStay(world: IBlockAccess, x: Int, y: Int, z: Int) = world.getBlock(x, y + 1, z) === leaves
//	override fun canBlockStay(world: World, x: Int, y: Int, z: Int) = world.getBlock(x, y + 1, z) === leaves
	
	override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos) = canBlockStay(level, pos.x, pos.y, pos.z)
//	override fun canPlaceBlockAt(world: World, x: Int, y: Int, z: Int) = canBlockStay(world, x, y, z)
	
	override fun neighborChanged(state: BlockState, world: World, pos: BlockPos, block: Block, fromPos: BlockPos, isMoving: Boolean) = checkChange(world, pos.x, pos.y, pos.z)
//	override fun onNeighborBlockChange(world: World, x: Int, y: Int, z: Int, block: Block) = checkChange(world, x, y, z)
	
	// PORT: столкновений нет (getCollisionBoundingBoxFromPool — null)
	override fun getCollisionShape(state: BlockState, level: IBlockAccess, pos: BlockPos, context: CollisionContext): VoxelShape = Shapes.empty()
//	override fun getCollisionBoundingBoxFromPool(world: World?, x: Int, y: Int, z: Int) = null
	
	override fun isOpaqueCube() = false
	
	// PORT: модель — alfheim.port.data.AlfheimBlockStates
//	override fun renderAsNormalBlock() = false
	
	// PORT: вид рендера 1.7.10 → форма рендера 1.20.1: 1 (крест) — модель блока; −1 (без рендера блока) — нет модели,
	// ягоду рисует только RenderTileTreeBerry
	override fun getRenderShape(state: BlockState) = if (AlfheimConfigHandler.minimalGraphics || hasModelErrors[type] == true) RenderShape.MODEL else RenderShape.INVISIBLE
//	override fun getRenderType() = if (AlfheimConfigHandler.minimalGraphics || hasModelErrors[type] == true) 1 else -1
	
	/* PORT: иконки → модели (alfheim.port.data.AlfheimBlockStates): крест с текстурой зрелости
	override fun registerBlockIcons(reg: IIconRegister) {
		icons = Array(3) { IconHelper.forBlock(reg, this, it) }
	}
	
	override fun getIcon(side: Int, meta: Int) = icons.safeGet(meta)
	*/
	
	// PORT: лут — таблица (alfheim.port.data.AlfheimBlockLoot) по этим методам; предмет с metadata — предмет варианта
	// (SPEC, Р-5): ягода дерева — еда варианта damageDropped
	fun getItemDropped(meta: Int, random: Random?, fortune: Int): Item? = if (meta >= 2) AlfheimItems.elvenFood[damageDropped(meta)] else null
//	override fun getItemDropped(meta: Int, random: Random?, fortune: Int) = if (meta >= 2) AlfheimItems.elvenFood else null
	
	fun damageDropped(meta: Int) = if (meta >= 2) ElvenFoodMetas.TreeBerryBarrier.ordinal + type else 0
	
//	override fun quantityDropped(random: Random?) = 1
	
	fun quantityDropped(meta: Int, fortune: Int, random: Random?) = if (meta >= 2) 1 else 0
	
//	override fun quantityDroppedWithBonus(meta: Int, random: Random?) = if (meta >= 2) 1 else 0
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, lexicon: ItemStack) = AlfheimLexiconData.treeBerry
	
	// PORT: растение и его тип 1.20.1 — по точке (IPlantable); metadata растения (getPlantMetadata) — в его состоянии
	override fun getPlantType(world: IBlockAccess, pos: BlockPos) = PlantType.PLAINS
//	override fun getPlantType(world: IBlockAccess?, x: Int, y: Int, z: Int) = EnumPlantType.Plains
	
	override fun getPlant(world: IBlockAccess, pos: BlockPos): BlockState = world.getBlockState(pos)
//	override fun getPlant(world: IBlockAccess, x: Int, y: Int, z: Int) = world.getBlock(x, y, z)!!
//
//	override fun getPlantMetadata(world: IBlockAccess, x: Int, y: Int, z: Int) = world.getBlockMetadata(x, y, z)
	
	// PORT: metadata — свойство age
	override fun func_149851_a(world: World, x: Int, y: Int, z: Int, isRemote: Boolean) = !isRemote && world.getBlockState(BlockPos(x, y, z)).getValue(AGE) < 2 // can bonemeal at all, will use item if true
//	override fun func_149851_a(world: World, x: Int, y: Int, z: Int, isRemote: Boolean) = !isRemote && world.getBlockMetadata(x, y, z) < 2 // can bonemeal at all, will use item if true
	
	override fun func_149852_a(world: World?, random: Random, x: Int, y: Int, z: Int) = random.nextInt(10) == 0 // is bonemeal applied
	
	override fun func_149853_b(world: World, random: Random?, x: Int, y: Int, z: Int) { // bonemeal action
		// PORT: metadata — свойство age
		val pos = BlockPos(x, y, z)
		val state = world.getBlockState(pos)
		world.setBlock(pos, state.setValue(AGE, state.getValue(AGE) + 1), 3)
//		world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) + 1, 3)
	}
	
	// PORT: лут — таблица (alfheim.port.data.AlfheimBlockLoot)
//	override fun getDrops(world: World, x: Int, y: Int, z: Int, metadata: Int, fortune: Int): ArrayList<ItemStack> {
//		return arrayListOf(ItemStack(getItemDropped(metadata, world.rand, fortune) ?: return arrayListOf(), quantityDropped(metadata, fortune, world.rand), damageDropped(metadata)))
//	}
	
	companion object {
		// this is here and not in render class for it to be safely got in `getRenderType`
		val hasModelErrors = hashMapOf(0 to true, 1 to true, 5 to true)
		
		// PORT: metadata зрелости 0–2 — свойство age; рамки — из setBlockBoundsBasedOnState автора
		val AGE = BlockStateProperties.AGE_2
		
		private val SHAPES = arrayOf(
			Shapes.box(0.28125, 0.5625, 0.28125, 0.71875, 1.0, 0.71875),
			Shapes.box(0.21875, 0.375, 0.21875, 0.78125, 1.0, 0.78125),
			Shapes.box(0.15625, 0.125, 0.15625, 0.84375, 1.0, 0.84375),
		)
	}
}
