package alfheim.common.block.colored

// PORT: импорты 1.20.1; BlockSapling 1.7.10 — alfheim.port.legacy.Sapling1710, WorldGenerator 1.7.10 —
// alfheim.port.legacy.WorldGenerator (MAPPING.md, «Растения»)
import alexsocol.asjlib.toItem
import alfheim.api.AlfheimAPI
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.block.ItemBlockLeavesMod
import alfheim.common.world.gen.SimpleTreeGen
import alfheim.port.legacy.*
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.*

// PORT: КТ-9 — лексикон (ILexiconable)
@Suppress("LeakingThis")
open class BlockColoredSapling(name: String = "irisSapling"): Sapling1710()/*, ILexiconable*/, IFuelHandler {
	
	init {
		setBlockName(name)
		setCreativeTab(AlfheimTab)
		stepSound = soundTypeGrass
		tickRandomly = true
		
		GameRegistry.registerFuelHandler(this)
	}
	
	override fun setBlockName(name: String): Block {
		if (shouldRegisterInNameSet()) GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
		return super.setBlockName(name)
	}
	
	open fun shouldRegisterInNameSet() = true
	
	// PORT: столкновений у растения нет (Bush1710); модель — крест (alfheim.port.data.AlfheimBlockStates)
//	override fun getCollisionBoundingBoxFromPool(world: World?, x: Int, y: Int, z: Int) = null
	
	override fun isOpaqueCube() = false
	
	/* PORT: модель — крест (alfheim.port.data.AlfheimBlockStates). Растение (getPlant) и его тип (Plains) — как у
	   растения 1.20.1 по умолчанию; metadata растения (getPlantMetadata) в 1.20.1 нет — растение и есть состояние
	override fun renderAsNormalBlock() = false
	
	override fun getRenderType() = 1
	
	override fun getPlant(world: IBlockAccess?, x: Int, y: Int, z: Int) = this
	
	override fun getPlantType(world: IBlockAccess?, x: Int, y: Int, z: Int) = EnumPlantType.Plains
	
	override fun getPlantMetadata(world: IBlockAccess, x: Int, y: Int, z: Int) = world.getBlockMetadata(x, y, z)
	*/
	
	// PORT: и установку, и удержание в 1.20.1 решает canBlockStay (Bush1710.canSurvive). Установку 1.7.10 ещё требовала,
	// чтобы блок снизу держал растение, — все почвы радужного саженца (AlfheimAPI.getIridescentSoils) его держат
//	override fun canPlaceBlockAt(world: World, x: Int, y: Int, z: Int) =
//		super.canPlaceBlockAt(world, x, y, z) && canBlockStay(world, x, y, z)
	
	/**
	 * Ticks the block if it's been scheduled
	 */
	override fun updateTick(world: World, x: Int, y: Int, z: Int, random: Random) {
		if (!world.isRemote) {
			checkAndDropBlock(world, x, y, z)
			
			if (world.getBlockLightValue(x, y + 1, z) >= 9 && random.nextInt(7) == 0) {
				markOrGrowMarked(world, x, y, z, random)
			}
		}
	}
	
	override fun checkAndDropBlock(world: World?, x: Int, y: Int, z: Int) {
		if (world != null && !canBlockStay(world, x, y, z)) {
			// PORT: dropBlockAsItem → лут состояния (таблица генерации данных)
			val pos = BlockPos(x, y, z)
			dropResources(world.getBlockState(pos), world, pos)
//			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0)
			world.setBlock(x, y, z, Blocks.AIR.defaultBlockState(), 2)
//			world.setBlock(x, y, z, getBlockById(0), 0, 2)
		}
	}
	
	override fun canBlockStay(world: World, x: Int, y: Int, z: Int) =
		world.getBlock(x, y - 1, z).canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, this) || canGrowHere(world.getBlock(x, y - 1, z))
	
	// PORT: вкладка выдаёт блок сама (AlfheimTab)
//	override fun getSubBlocks(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>) {
//		list.add(ItemStack(item))
//	}
	
	fun markOrGrowMarked(world: World?, x: Int, y: Int, z: Int, random: Random) {
		if (world != null) {
			// PORT: бит 8 metadata («готов расти») — свойство STAGE (Sapling1710); саженец мог убрать checkAndDropBlock
			// (updateTick), а стадии у воздуха 1.20.1 нет (в 1.7.10 metadata воздуха менялась впустую)
			val pos = BlockPos(x, y, z)
			val state = world.getBlockState(pos)
			if (!state.`is`(this)) return
//			val l = world.getBlockMetadata(x, y, z)
			
			if (state.getValue(STAGE) == 0) {
//			if ((l and 8) == 0) {
				world.setBlock(pos, state.setValue(STAGE, 1), 4)
//				world.setBlockMetadataWithNotify(x, y, z, l or 8, 4)
			} else {
				growTree(world, x, y, z, random)
			}
		}
	}
	
	open fun growTree(world: World, x: Int, y: Int, z: Int, random: Random) {
		if (!TerrainGen.saplingGrowTree(world, random, x, y, z)) return
		
		val plantedOn: Block = world.getBlock(x, y - 1, z)
		
		if (!canGrowHere(plantedOn)) return
		
		// PORT: metadata саженца — его состояние (бит 8 — STAGE)
		val state = world.getBlockState(BlockPos(x, y, z))
		val l = if (state.getValue(STAGE) == 1) 8 else 0
//		val l = world.getBlockMetadata(x, y, z)
		val obj: WorldGenerator = getGenerator(l)
		
		world.setBlock(x, y, z, Blocks.AIR.defaultBlockState(), 4)
//		world.setBlock(x, y, z, Blocks.air, 0, 4)
		
		if (obj.generate(world, getRandomForGenerating(l, random), x, y, z)) return
		world.setBlock(x, y, z, state, 4)
//		world.setBlock(x, y, z, this, l, 4)
	}
	
	open fun getGenerator(meta: Int): WorldGenerator = SimpleTreeGen(5)
	
	open fun getRandomForGenerating(meta: Int, default: Random) = default
	
	/**
	 * canFertilize
	 */
	override fun func_149851_a(world: World, x: Int, y: Int, z: Int, isRemote: Boolean) = canGrowHere(world.getBlock(x, y - 1, z))
	
	/**
	 * shouldCallFertilize
	 * If false #fertilize won't be called, but bonemeal stack size will be decremented
	 */
	override fun func_149852_a(world: World, random: Random, x: Int, y: Int, z: Int): Boolean {
		return random.nextDouble() < 0.45
	}
	
	/**
	 * fertilize
	 */
	override fun func_149853_b(world: World, random: Random, x: Int, y: Int, z: Int) {
		markOrGrowMarked(world, x, y, z, random)
	}
	
	open fun canGrowHere(block: Block) = AlfheimAPI.getIridescentSoils().contains(block)
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?): LexiconEntry? = AlfheimLexiconData.irisSapling
	
	override fun getBurnTime(fuel: ItemStack) = if (fuel.item === this.toItem()) 100 else 0
	
	/* PORT: иконка → модель (alfheim.port.data.AlfheimBlockStates): крест с текстурой блока
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(reg: IIconRegister) {
		blockIcon = IconHelper.forBlock(reg, this)
	}
	
	@SideOnly(Side.CLIENT)
	override fun getIcon(side: Int, meta: Int) = blockIcon!!
	*/
}
