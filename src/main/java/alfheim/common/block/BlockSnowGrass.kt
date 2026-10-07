package alfheim.common.block

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md); обёртка автора SuperWrapperHandler (S-03,
// HOOKS.md) — миксин BushBlockInvoker; Рагнарёк (RagnarokHandler) — КТ-8
import alexsocol.asjlib.*
import alfheim.AlfheimCore
import alfheim.common.block.base.BlockMod
//import alfheim.common.core.asm.superwrapper.SuperWrapperHandler
//import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.core.util.AlfheimTab
import alfheim.port.legacy.*
import alfheim.port.legacy.botania.IHornHarvestable
import alfheim.port.legacy.botania.LibMisc
import alfheim.port.mixin.BushBlockInvoker
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction as ForgeDirection
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraftforge.common.*
import ru.vamig.worldengine.WE_PerlinNoise

class BlockSnowGrass: BlockMod(Material.grass), IGrowable, IHornHarvestable {
	
	init {
		setBlockName("SnowGrass")
		setCreativeTab(AlfheimTab)
		setHardness(0.6f)
		setHarvestLevel("shovel", 0)
		setStepSound(soundTypeGrass)
		
		tickRandomly = true
		// PORT: metadata 1 (вечная снежная трава: её ставят снежные семена ириса) — свойство permanent
		registerDefaultState(stateDefinition.any().setValue(PERMANENT, false))
	}
	
	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(PERMANENT)
	}
	
	/* PORT: иконки → модель (alfheim.port.data.AlfheimBlockStates): низ — земля, верх — снег, бока — заснеженная трава
	override fun getIcon(side: Int, meta: Int) = when (side) {
		0       -> Blocks.dirt.getIcon(0, 0)
		1       -> Blocks.snow.getIcon(1, 0)
		else    -> Blocks.grass.field_149993_M
	}!!
	
	override fun registerBlockIcons(reg: IIconRegister) = Unit
	*/
	override fun func_149851_a(world: World, x: Int, y: Int, z: Int, isRemote: Boolean) = true
	override fun func_149852_a(world: World?, random: Random?, x: Int, y: Int, z: Int) = true
	override fun func_149853_b(world: World?, random: Random?, x: Int, y: Int, z: Int) = Unit
	// PORT: лут — таблица (alfheim.port.data.AlfheimBlockLoot): земля, с шёлковым касанием — сама трава
	fun getItemDropped(meta: Int, random: Random?, fortune: Int) = Blocks.DIRT.toItem()
//	override fun getItemDropped(meta: Int, random: Random?, fortune: Int) = Blocks.dirt.toItem()
	
	// PORT: почву 1.20.1 спрашивают по состоянию и BlockPos, EnumPlantType → PlantType
	override fun canSustainPlant(state: BlockState, world: IBlockAccess, pos: BlockPos, direction: ForgeDirection, plantable: IPlantable): Boolean { val x = pos.x; val y = pos.y; val z = pos.z
//	override fun canSustainPlant(world: IBlockAccess, x: Int, y: Int, z: Int, direction: ForgeDirection, plantable: IPlantable): Boolean {
		// PORT: обёртка SuperWrapperHandler.canPlaceBlockOn (S-03) — правило куста mayPlaceOn через миксин BushBlockInvoker
		if (plantable is BushBlock && (plantable as BushBlockInvoker).`alfheim$mayPlaceOn`(Blocks.GRASS_BLOCK.defaultBlockState(), world, pos)) {
//		if (plantable is BlockBush && SuperWrapperHandler.canPlaceBlockOn(plantable, Blocks.grass)) {
			return true
		}
		
		return when (plantable.getPlantType(world, pos.above())) {
			PlantType.PLAINS -> true
			PlantType.BEACH  -> LibMisc.CARDINAL_DIRECTIONS.any { d -> world.getBlock(x + d.offsetX, y, z + d.offsetZ).material === Material.water }
			else             -> false
		}
//		return when (plantable.getPlantType(world, x, y + 1, z)) {
//			EnumPlantType.Plains -> true
//			EnumPlantType.Beach  -> LibMisc.CARDINAL_DIRECTIONS.any { d -> world.getBlock(x + d.offsetX, y, z + d.offsetZ).material === Material.water }
//			else                 -> false
//		}
	}
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, random: Random) {
		val above = world.getBlock(x, y + 1, z)
		// PORT: metadata — свойства состояний: своя — permanent, у слоя снега выше — число слоёв без одного, как metadata
		// слоя снега 1.7.10 (layers − 1); у прочих блоков она здесь не нужна
		val meta = if (world.getBlockState(BlockPos(x, y, z)).getValue(PERMANENT)) 1 else 0
		val metaAbove = world.getBlockState(BlockPos(x, y + 1, z)).let { if (it.hasProperty(BlockStateProperties.LAYERS)) it.getValue(BlockStateProperties.LAYERS) - 1 else 0 }
//		val meta = world.getBlockMetadata(x, y, z)
//		val metaAbove = world.getBlockMetadata(x, y + 1, z)
		
		if (AlfheimCore.winter) {
			// PORT: слой снега — свойство layers (metadata + 1)
			if (above === Blocks.SNOW)
				world.setBlock(x, y + 1, z, AlfheimBlocks.snowLayer.defaultBlockState().setValue(BlockStateProperties.LAYERS, metaAbove + 1), 3)
//			if (above === Blocks.snow_layer)
//				world.setBlock(x, y + 1, z, AlfheimBlocks.snowLayer, metaAbove, 3)
			
			// from BlockGrass:
			if (world.getBlockLightValue(x, y + 1, z) < 4 && world.getBlockLightOpacity(x, y + 1, z) > 2) {
				world.setBlock(x, y, z, Blocks.DIRT)
//				world.setBlock(x, y, z, Blocks.dirt)
				return
			}
			
			if (!world.isRaining || world.getPrecipitationHeight(x, z) < y)
				return
			
			// PORT: воздух 1.20.1 — ещё и воздух пещер и пустоты; слой снега — свойство layers (metadata + 1)
			if (above.defaultBlockState().isAir) {
//			if (above === Blocks.air) {
				world.setBlock(x, y + 1, z, AlfheimBlocks.snowLayer)
			} else if (above === AlfheimBlocks.snowLayer) {
				val upMeta = WE_PerlinNoise.PerlinNoise2D(world.seed, x.D, z.D, 1.0, 1).times(15).I.and(7).div(2)
				
				if (metaAbove < upMeta) world.setBlock(x, y + 1, z, world.getBlockState(BlockPos(x, y + 1, z)).setValue(BlockStateProperties.LAYERS, metaAbove + 2), 1 or 2)
//				if (metaAbove < upMeta) world.setBlockMetadataWithNotify(x, y + 1, z, metaAbove + 1, 1 or 2)
			}
			
			repeat(4) {
				val i = x + random.nextInt(3) - 1
				val j = y + random.nextInt(4) - 3
				val k = z + random.nextInt(3) - 1
				val block = world.getBlock(i, j, k)
				
				// PORT: metadata 0 земли — обычная земля (каменистая земля и подзол в 1.20.1 — свои блоки), у травы — всегда 0
				if ((block === Blocks.DIRT || block === Blocks.GRASS_BLOCK) && world.getPrecipitationHeight(i, k) <= k) world.setBlock(i, j, k, this)
//				if ((block === Blocks.dirt || block === Blocks.grass) && world.getBlockMetadata(i, j, k) == 0 && world.getPrecipitationHeight(i, k) <= k) world.setBlock(i, j, k, this)
			}
		} else {
			if (meta == 1 || world.rand.nextInt(meltDelay) != 0) return
			
			if (above === AlfheimBlocks.snowLayer || above === Blocks.SNOW)
//			if (above === AlfheimBlocks.snowLayer || above === Blocks.snow_layer)
				world.setBlockToAir(x, y + 1, z)
			
			world.setBlock(x, y, z, Blocks.GRASS_BLOCK)
//			world.setBlock(x, y, z, Blocks.grass)
		}
	}
	
	// PORT: metadata — свойство permanent
	override fun canHornHarvest(world: World, x: Int, y: Int, z: Int, stack: ItemStack?, hornType: IHornHarvestable.EnumHornType) =
		hornType == IHornHarvestable.EnumHornType.COVERING && !world.getBlockState(BlockPos(x, y, z)).getValue(PERMANENT)
//		hornType == IHornHarvestable.EnumHornType.COVERING && world.getBlockMetadata(x, y, z) != 1
	
	override fun hasSpecialHornHarvest(world: World, x: Int, y: Int, z: Int, stack: ItemStack?, hornType: IHornHarvestable.EnumHornType) =
		canHornHarvest(world, x, y, z, stack, hornType)
	
	override fun harvestByHorn(world: World, x: Int, y: Int, z: Int, stack: ItemStack?, hornType: IHornHarvestable.EnumHornType) {
		if (!canHornHarvest(world, x, y, z, stack, hornType)) return
		
		world.setBlock(x, y, z, Blocks.GRASS_BLOCK)
//		world.setBlock(x, y, z, Blocks.grass)
	}
	
	companion object {
		// PORT: КТ-8 — RagnarokHandler: летом Рагнарёка снежная трава тает каждым случайным тиком
		val meltDelay get() = 20
//		val meltDelay get() = if (RagnarokHandler.summer) 1 else 20
		
		// PORT: metadata 1 — вечная снежная трава
		val PERMANENT: BooleanProperty = BooleanProperty.create("permanent")
	}
}