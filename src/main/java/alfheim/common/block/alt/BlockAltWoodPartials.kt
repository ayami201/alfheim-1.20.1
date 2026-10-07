package alfheim.common.block.alt

// PORT: импорты 1.20.1 (BlockSlab, BlockStairs 1.7.10 — Slab1710, Stairs1710, MAPPING.md)
import alexsocol.asjlib.*
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.base.*
import alfheim.common.item.block.*
import alfheim.port.legacy.*
import alfheim.port.registry.LegacyRegistration
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SlabBlock as BlockSlab
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Half

// PORT: вариант metadata (вид, meta % 8) — отдельная плита (SPEC, Р-5): номер варианта — meta, создают массивом
// `Array(ALT_TYPES.size - 1) { BlockAltWoodSlab(false, it) }`; доски-источник — блок варианта (altPlanks[meta]); двойная
// плита — состояние type=double одинарной (BlockSlabMod). КТ-9 — лексикон
class BlockAltWoodSlab(full: Boolean, meta: Int, source: Block = AlfheimBlocks.altPlanks[meta]):
	BlockSlabMod(full, meta, source, source.unlocalizedName.replace("tile.".toRegex(), "") + "Slab" + (if (full) "Full" else "")), IFuelHandler {
	
	override val variant get() = meta
	
	init {
		GameRegistry.registerFuelHandler(this)
		// PORT: твёрдость в мире (getBlockHardness ниже) 1.20.1 хранит в состояниях блока
		if (meta % 8 == 6) destroySpeed = 100f
	}
	
	// PORT: сигнатура 1.20.1 (IForgeBlock); metadata — номер варианта блока (SPEC, Р-5)
	override fun getExplosionResistance(state: BlockState, world: IBlockAccess, pos: BlockPos, explosion: Explosion) =
		if (meta % 8 == 6)
			1000f
		else
			super.getExplosionResistance(state, world, pos, explosion)
//	override fun getExplosionResistance(entity: Entity?, world: World, x: Int, y: Int, z: Int, explosionX: Double, explosionY: Double, explosionZ: Double) =
//		if (world.getBlockMetadata(x, y, z) % 8 == 6)
//			1000f
//		else
//			super.getExplosionResistance(entity, world, x, y, z, explosionX, explosionY, explosionZ)
	
	// PORT: твёрдость в мире — в состояниях блока (init выше)
//	override fun getBlockHardness(world: World, x: Int, y: Int, z: Int) =
//		if (world.getBlockMetadata(x, y, z) % 8 == 6)
//			100f
//		else
//			super.getBlockHardness(world, x, y, z)
	
	// PORT: сигнатуры 1.20.1 (IForgeBlock); metadata — номер варианта блока (SPEC, Р-5)
	override fun getFlammability(state: BlockState, world: IBlockAccess, pos: BlockPos, face: Direction) =
		if (meta % 8 == 6) 0 else super.getFlammability(state, world, pos, face)
	
	override fun getFireSpreadSpeed(state: BlockState, world: IBlockAccess, pos: BlockPos, face: Direction) =
		if (meta % 8 == 6) 0 else super.getFireSpreadSpeed(state, world, pos, face)
//	override fun getFlammability(world: IBlockAccess, x: Int, y: Int, z: Int, face: ForgeDirection?) =
//		if (world.getBlockMetadata(x, y, z) % 8 == 6) 0 else super.getFlammability(world, x, y, z, face)
//
//	override fun getFireSpreadSpeed(world: IBlockAccess, x: Int, y: Int, z: Int, face: ForgeDirection?) =
//		if (world.getBlockMetadata(x, y, z) % 8 == 6) 0 else super.getFireSpreadSpeed(world, x, y, z, face)
	
	// PORT: варианты во вкладке — отдельные блоки (AlfheimTab)
	/*
	override fun getSubBlocks(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>) {
		for (i in 0 until LibOreDict.ALT_TYPES.size - 1)
			list.add(ItemStack(item, 1, i))
	}
	*/
	
	override fun getFullBlock() = AlfheimBlocks.altSlabsFull[meta] as BlockSlab
	
	// PORT: модель — по текстурам блока-источника (alfheim.port.data.AlfheimBlockStates)
	/*
	override fun getIcon(side: Int, meta: Int): IIcon {
		return source.getIcon(side, meta % 8)!!
	}
	*/
	
	override fun register() {
		GameRegistry.registerBlock(this, ItemMetaSlabMod::class.java, name)
		// PORT: двойная плита 1.7.10 — состояние type=double этой плиты (BlockSlabMod); её старое имя — altPlanksSlabFull
		// с metadata варианта
		LegacyRegistration.alias(name.replaceFirst("Slab", "SlabFull"), this, "type=double", meta)
	}
	
	override fun getSingleBlock() = AlfheimBlocks.altSlabs[meta] as BlockSlab
	
	// PORT: field_150004_a (двойная ли плита) — full. Печь 1.7.10 сжигала деревянный блок 300 тиков раньше обработчиков
	// модов: ни Int.MAX_VALUE / 13 / 8 у плиты Иггдрасиля, ни 150 у одинарной плиты не срабатывали (Fuel1710; BUGS.md)
	override fun getBurnTime(fuel: ItemStack) = if (fuel.item === this.toItem()) if (fuel.meta == BlockAltLeaves.yggMeta) Int.MAX_VALUE / 13 / 8 else if (full) 300 else 150 else 0
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = if (world.getBlockMetadata(x, y, z) == BlockAltLeaves.yggMeta) null else AlfheimLexiconData.irisSapling
}

// PORT: доски-источник — блок варианта (SPEC, Р-5): altPlanks[meta] вместо altPlanks с metadata. КТ-9 — лексикон
open class BlockAltWoodStairs(meta: Int, source: Block = AlfheimBlocks.altPlanks[meta]):
	BlockStairsMod(source, meta, source.unlocalizedName.replace("tile.".toRegex(), "") + "Stairs" + meta), IFuelHandler {
	
	init {
		GameRegistry.registerFuelHandler(this)
		// PORT: твёрдость ступенек в мире BlockStairsMod 1.7.10 спрашивал у досок в той же точке (getBlockHardness), а доски
		// отвечали по metadata точки — у ступенек это их положение: при 6 (перевёрнутые, к югу) доски видели в ней свой
		// вариант Иггдрасиля и отвечали 100 (BUGS.md). В 1.20.1 твёрдость — у состояния: у ступенек в этом положении — 100
		for (state in stateDefinition.possibleStates)
			if (state.getValue(StairBlock.HALF) == Half.TOP && state.getValue(StairBlock.FACING) == Direction.SOUTH) state.destroySpeed = 100f
	}
	
	override fun register() {
		GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
	}
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = if (world.getBlockMetadata(x, y, z) == BlockAltLeaves.yggMeta) null else AlfheimLexiconData.irisSapling
	
	override fun getBurnTime(fuel: ItemStack) = if (fuel.item === this.toItem()) 300 else 0
}

class BlockYggStairs: BlockAltWoodStairs(BlockAltLeaves.yggMeta), IFuelHandler {
	
	// PORT: твёрдость в мире (getBlockHardness ниже) 1.20.1 хранит в состояниях блока
	init {
		destroySpeed = 100f
	}
	
	// PORT: сигнатура 1.20.1 (IForgeBlock)
	override fun getExplosionResistance(state: BlockState, world: IBlockAccess, pos: BlockPos, explosion: Explosion) = 1000f
//	override fun getExplosionResistance(entity: Entity?, world: World, x: Int, y: Int, z: Int, explosionX: Double, explosionY: Double, explosionZ: Double) = 1000f
	
	// PORT: твёрдость в мире — в состояниях блока (init выше)
//	override fun getBlockHardness(world: World, x: Int, y: Int, z: Int) = 100f
	
	// PORT: сигнатуры 1.20.1 (IForgeBlock)
	override fun getFlammability(state: BlockState, world: IBlockAccess, pos: BlockPos, face: Direction) = 0
	
	override fun getFireSpreadSpeed(state: BlockState, world: IBlockAccess, pos: BlockPos, face: Direction) = 0
//	override fun getFlammability(world: IBlockAccess, x: Int, y: Int, z: Int, face: ForgeDirection?) = 0
//
//	override fun getFireSpreadSpeed(world: IBlockAccess, x: Int, y: Int, z: Int, face: ForgeDirection?) = 0
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = null
	
	// PORT: деревянный блок печь 1.7.10 жгла 300 тиков раньше обработчиков модов: Int.MAX_VALUE / 13 * 3 / 8 у ступенек
	// Иггдрасиля не срабатывал (Fuel1710; BUGS.md)
	override fun getBurnTime(fuel: ItemStack) = if (fuel.item === this.toItem()) Int.MAX_VALUE / 13 * 3 / 8 else 0
}
