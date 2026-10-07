package alfheim.common.block

// PORT: импорты 1.20.1; BlockBush 1.7.10 — alfheim.port.legacy.Bush1710, IGrowable — alfheim.port.legacy.IGrowable
// (MAPPING.md, «Растения»)
import alexsocol.asjlib.toItem
import alfheim.api.ModInfo
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.block.ItemBlockLeavesMod
import alfheim.common.world.dim.alfheim.biome.BiomeAlfheim
import alfheim.port.legacy.*
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.*
import net.minecraft.world.level.block.state.properties.BlockStateProperties.STAGE

// PORT: КТ-9 — лексикон (ILexiconable)
class BlockDreamSapling: Bush1710(), IGrowable/*, ILexiconable*/, IFuelHandler {
	
	init {
		setBlockBounds(0.1f, 0f, 0.1f, 0.9f, 0.8f, 0.9f)
		setBlockName("DreamSapling")
		setBlockTextureName(ModInfo.MODID + ":DreamSapling")
		setCreativeTab(AlfheimTab)
		setLightLevel(9f / 15f)
		setLightOpacity(0)
		stepSound = soundTypeGrass
		tickRandomly = true
		// PORT: бит 8 metadata («готов расти») — свойство stage, как у саженца (Sapling1710)
		registerDefaultState(stateDefinition.any().setValue(STAGE, 0))
		
		GameRegistry.registerFuelHandler(this)
	}
	
	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(STAGE)
	}
	
	override fun setBlockName(name: String): Block {
		GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
		return super.setBlockName(name)
	}
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, rand: Random?) {
		if (!world.isRemote) {
			super.updateTick(world, x, y, z, rand)
			
			if (world.getBlockLightValue(x, y + 1, z) >= 9 && rand!!.nextInt(7) == 0) {
				grow(world, x, y, z, rand)
			}
		}
	}
	
	fun grow(world: World, x: Int, y: Int, z: Int, rand: Random) {
		// PORT: бит 8 metadata — свойство stage; саженец мог убрать super.updateTick (нет опоры), а стадии у воздуха
		// 1.20.1 нет (в 1.7.10 metadata воздуха менялась впустую)
		val pos = BlockPos(x, y, z)
		val state = world.getBlockState(pos)
		if (!state.`is`(this)) return
//		val l = world.getBlockMetadata(x, y, z)
		
		if (state.getValue(STAGE) == 0) {
//		if (l and 8 == 0) {
			world.setBlock(pos, state.setValue(STAGE, 1), 4)
//			world.setBlockMetadataWithNotify(x, y, z, l or 8, 4)
		} else {
			growTree(world, x, y, z, rand)
		}
	}
	
	fun growTree(world: World, x: Int, y: Int, z: Int, rand: Random) {
		if (!TerrainGen.saplingGrowTree(world, rand, x, y, z)) return
		// PORT: metadata без бита 8 — саженец со стадией 0 (вариантов у саженца нет)
//		val l = world.getBlockMetadata(x, y, z) and 7
		world.setBlock(x, y, z, Blocks.AIR.defaultBlockState(), 4)
//		world.setBlock(x, y, z, Blocks.air, 0, 4)
		if (!BiomeAlfheim.dreamTree.generate(world, rand, x, y, z, null)) world.setBlock(x, y, z, defaultBlockState(), 4)
//		if (!BiomeAlfheim.dreamTree.generate(world, rand, x, y, z, null)) world.setBlock(x, y, z, this, l, 4)
	}
	
	/** Can the block grow
	 * @param b == world.isRemote
	 */
	override fun func_149851_a(world: World, x: Int, y: Int, z: Int, b: Boolean) = true
	
	/** Applying chance to grow
	 * @return true to grow tree
	 */
	override fun func_149852_a(world: World, rand: Random, x: Int, y: Int, z: Int) = world.rand.nextDouble() < 0.45
	
	/** Grow block */
	override fun func_149853_b(world: World, rand: Random, x: Int, y: Int, z: Int) {
		grow(world, x, y, z, rand)
	}
	
	// PORT: лут — сам саженец (alfheim.port.data.AlfheimBlockLoot): бит стадии в предмет не попадает
//	override fun damageDropped(meta: Int) = 0
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, lexicon: ItemStack) = AlfheimLexiconData.worldgen
	
	override fun getBurnTime(fuel: ItemStack) = if (fuel.item === this.toItem()) 100 else 0
}
