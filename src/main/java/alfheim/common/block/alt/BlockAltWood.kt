package alfheim.common.block.alt

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alexsocol.asjlib.*
import alfheim.api.lib.LibOreDict.ALT_TYPES
import alfheim.common.block.base.BlockModRotatedPillar
import alfheim.common.item.block.ItemUniqueSubtypedBlockMod
import alfheim.port.legacy.*
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.block.state.BlockState

// PORT: вариант metadata (meta and 3) — отдельный блок (SPEC, Р-5): номер варианта — meta, создают массивом
// `Array(4) { BlockAltWood(set, it) }`; поворот (meta and 12) — свойство axis (BlockModRotatedPillar). Бревно Иггдрасиля —
// набор 1, вариант 2. КТ-9 — лексикон
class BlockAltWood(val set: Int, val meta: Int): BlockModRotatedPillar(Material.wood), IFuelHandler {
	
	override val variant get() = meta
	
	// PORT: иконки → модели (alfheim.port.data.AlfheimBlockStates): altOak<вид>Top и altOak<вид>Side; кадры опалённого
	// бревна сглаживает .mcmeta
//	lateinit var icons: Array<Array<IIcon?>>
	
	init {
		setBlockName("altWood$set")
		// PORT: флаг isBlockContainer Forge 1.7.10 не читал (блок-сущность бывает только у ITileEntityProvider), в 1.20.1
		// его нет
//		isBlockContainer = true
		blockHardness = 2f
		// PORT: твёрдость в мире (getBlockHardness ниже) 1.20.1 хранит в состояниях блока: бревно Иггдрасиля не сломать
		if (set == 1 && meta % 4 == 2) destroySpeed = -1f
		// PORT: анимированные текстуры 1.20.1 рисует сама по .mcmeta; подписка на TextureStitchEvent не нужна
//		if (ASJUtilities.isClient)
//			MinecraftForge.EVENT_BUS.register(this)
		
		GameRegistry.registerFuelHandler(this)
	}
	
	// PORT: сигнатура 1.20.1 (IForgeBlock); metadata — номер варианта блока (SPEC, Р-5)
	override fun getExplosionResistance(state: BlockState, world: IBlockAccess, pos: BlockPos, explosion: Explosion) =
		if (set == 1 && meta % 4 == 2)
			Float.MAX_VALUE
		else
			super.getExplosionResistance(state, world, pos, explosion)
//	override fun getExplosionResistance(entity: Entity?, world: World, x: Int, y: Int, z: Int, explosionX: Double, explosionY: Double, explosionZ: Double) =
//		if (set == 1 && world.getBlockMetadata(x, y, z) % 4 == 2)
//			Float.MAX_VALUE
//		else
//			super.getExplosionResistance(entity, world, x, y, z, explosionX, explosionY, explosionZ)
	
	// PORT: твёрдость в мире — в состояниях блока (init выше)
//	override fun getBlockHardness(world: World, x: Int, y: Int, z: Int) =
//		if (set == 1 && world.getBlockMetadata(x, y, z) % 4 == 2)
//			-1f
//		else
//			super.getBlockHardness(world, x, y, z)
	
	/* PORT: КТ-2 (партия 9) — клык Нидхёгга (nidhoggTooth): им бревно Иггдрасиля ломается (getDestroyProgress 1.20.1)
	override fun getPlayerRelativeBlockHardness(player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Float {
		val meta = world.getBlockMetadata(x, y, z)
		
		return if (set == 1 &&
		           meta != 14 && meta % 4 == 2 &&
		           player.heldItem?.let { it.item === AlfheimBlocks.nidhoggTooth.toItem() } == true)
			player.getBreakSpeed(this, false, meta, x, y, z) / blockHardness / 100f
		else
			super.getPlayerRelativeBlockHardness(player, world, x, y, z)
	}
	*/
	
	// PORT: бревно, которое держит листву и считается деревом (canSustainLeaves, isWood), — тег minecraft:logs: в нём
	// брёвна по Ore Dictionary (logWood, alfheim.port.data.OreDictTags) — все, кроме бревна Иггдрасиля
//	override fun canSustainLeaves(world: IBlockAccess, x: Int, y: Int, z: Int) = !(set == 1 && world.getBlockMetadata(x, y, z) % 4 == 2)
//
//	override fun isWood(world: IBlockAccess, x: Int, y: Int, z: Int) = !(set == 1 && world.getBlockMetadata(x, y, z) % 4 == 2)
	
	// PORT: сигнатуры 1.20.1 (IForgeBlock); metadata — номер варианта блока (SPEC, Р-5)
	override fun getFlammability(state: BlockState, world: IBlockAccess, pos: BlockPos, face: Direction) =
		if (set == 1 && meta % 4 == 2) 0 else super.getFlammability(state, world, pos, face)
	
	override fun getFireSpreadSpeed(state: BlockState, world: IBlockAccess, pos: BlockPos, face: Direction) =
		if (set == 1 && meta % 4 == 2) 0 else super.getFireSpreadSpeed(state, world, pos, face)
//	override fun getFlammability(world: IBlockAccess, x: Int, y: Int, z: Int, face: ForgeDirection?) =
//		if (set == 1 && world.getBlockMetadata(x, y, z) % 4 == 2) 0 else super.getFlammability(world, x, y, z, face)
//
//	override fun getFireSpreadSpeed(world: IBlockAccess, x: Int, y: Int, z: Int, face: ForgeDirection?) =
//		if (set == 1 && world.getBlockMetadata(x, y, z) % 4 == 2) 0 else super.getFireSpreadSpeed(world, x, y, z, face)
	
	// PORT: листва 1.20.1 сама пересчитывает расстояние до бревна, когда его убрали (updateShape); листва автора опадает
	// и без этого сигнала (beginLeavesDecay у неё пустой)
	/*
	override fun breakBlock(world: World, x: Int, y: Int, z: Int, block: Block, fortune: Int) {
		val meta = world.getBlockMetadata(x, y, z)
		if (set == 1 && meta % 4 == 2)
			return super.breakBlock(world, x, y, z, block, fortune)
		
		val range = if (set == 1 && meta % 4 == 3) 12 else 4
		val chunkRange = range + 1
		
		if (world.checkChunksExist(x - chunkRange, y - chunkRange, z - chunkRange, x + chunkRange, y + chunkRange, z + chunkRange)) {
			for (j1 in -range..range) for (k1 in -range..range)
				for (l1 in -range..range) {
					val blockInWorld: Block = world.getBlock(x + j1, y + k1, z + l1)
					if (blockInWorld.isLeaves(world, x + j1, y + k1, z + l1)) {
						blockInWorld.beginLeavesDecay(world, x + j1, y + k1, z + l1)
					}
				}
		}
		super.breakBlock(world, x, y, z, block, fortune)
	}
	*/
	
	// PORT: лут — сам блок, один (alfheim.port.data.AlfheimBlockLoot): вариант damageDropped — сам блок
//	override fun damageDropped(meta: Int) = meta and 0b0011
//
//	override fun quantityDropped(random: Random) = 1
	
	override fun register(name: String) {
		GameRegistry.registerBlock(this, ItemUniqueSubtypedBlockMod::class.java, name, ALT_TYPES.size / 2)
	}
	
	/* PORT: иконки → модели (alfheim.port.data.AlfheimBlockStates); варианты во вкладке — отдельные блоки (AlfheimTab)
	override fun getTopIcon(meta: Int) = icons[0].safeGet(meta % 4)
	
	override fun getSideIcon(meta: Int) = icons[1].safeGet(meta % 4)
	
	override fun getSubBlocks(item: Item, tab: CreativeTabs?, list: MutableList<Any?>) {
		for (i in 0..3)
			list.add(ItemStack(this, 1, i))
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(reg: IIconRegister) {
		icons = arrayOf(
			Array(4) { i -> if (set == 0 && i == 3) null else IconHelper.forName(reg, "altOak${ALT_TYPES[(set * 4) + i]}Top") },
			Array(4) { i -> if (set == 0 && i == 3) null else IconHelper.forName(reg, "altOak${ALT_TYPES[(set * 4) + i]}Side") }
		)
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	override fun loadTextures(event: TextureStitchEvent.Pre) {
		if (event.map.textureType == 0 && set == 0) {
			icons[0][3] = InterpolatedIconHelper.forName(event.map, "altOak${ALT_TYPES[3]}Top")
			icons[1][3] = InterpolatedIconHelper.forName(event.map, "altOak${ALT_TYPES[3]}Side")
		}
	}
	*/
	
	// PORT: КТ-9 — лексикон
	/*
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?): LexiconEntry? {
		val meta = world.getBlockMetadata(x, y, z)
		return when (set) {
			1 if meta % 8 == BlockAltLeaves.yggMeta - 4 + 1 -> AlfheimLexiconData.worldgen
			1 if meta % 8 == BlockAltLeaves.yggMeta - 4     -> null
			else                                            -> AlfheimLexiconData.irisSapling
		}
	}
	*/
	
	// PORT: деревянный блок печь 1.7.10 жгла 300 тиков раньше обработчиков модов: Int.MAX_VALUE / 13 у бревна Иггдрасиля
	// не срабатывал (Fuel1710; BUGS.md)
	override fun getBurnTime(fuel: ItemStack) = if (fuel.item === this.toItem()) if (set == 1 && fuel.meta % 8 == BlockAltLeaves.yggMeta - 4) Int.MAX_VALUE / 13 else 300 else 0
}
