package alfheim.common.block.alt

// PORT: импорты 1.20.1; BlockLeaves 1.7.10 — alfheim.port.legacy.Leaves1710 (MAPPING.md, «Растения»)
import alexsocol.asjlib.*
import alfheim.api.lib.LibOreDict.ALT_TYPES
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.base.BlockLeavesMod
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.item.block.ItemUniqueSubtypedBlockMod
import alfheim.port.legacy.*
import alfheim.port.legacy.botania.Botania
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.state.BlockState

// PORT: вариант metadata (вид, meta % 8) — отдельный блок (SPEC, Р-5): номер варианта — meta, создают массивом
// `Array(ALT_TYPES.size) { BlockAltLeaves(it) }`; бит опадания — свойство persistent (Leaves1710). Светящийся слой листвы
// мечтаний (IGlowingLayerBlock, RenderGlowingLayerBlock ASJCore) — второй слой её модели (alfheim.port.data). КТ-9 —
// лексикон
class BlockAltLeaves(val meta: Int): BlockLeavesMod()/*, IGlowingLayerBlock*/ {
	
	override val variant get() = meta
	
	init {
		setBlockName("altLeaves")
		setLightOpacity(0)
		// PORT: твёрдость в мире (getBlockHardness ниже) 1.20.1 хранит в состояниях блока: листву Иггдрасиля не сломать
		if (meta % 8 == yggMeta) destroySpeed = -1f
	}
	
	// PORT: сигнатура 1.20.1 (IForgeBlock); metadata — номер варианта блока (SPEC, Р-5)
	override fun getExplosionResistance(state: BlockState, world: IBlockAccess, pos: BlockPos, explosion: Explosion) =
		if (meta % 8 == yggMeta)
			Float.MAX_VALUE
		else
			super.getExplosionResistance(state, world, pos, explosion)
//	override fun getExplosionResistance(entity: Entity?, world: World, x: Int, y: Int, z: Int, explosionX: Double, explosionY: Double, explosionZ: Double) =
//		if (world.getBlockMetadata(x, y, z) % 8 == yggMeta)
//			Float.MAX_VALUE
//		else
//			super.getExplosionResistance(entity, world, x, y, z, explosionX, explosionY, explosionZ)
	
	// PORT: твёрдость в мире — в состояниях блока (init выше)
//	override fun getBlockHardness(world: World, x: Int, y: Int, z: Int) =
//		if (world.getBlockMetadata(x, y, z) % 8 == yggMeta)
//			-1f
//		else
//			super.getBlockHardness(world, x, y, z)
	
	// PORT: сигнатуры 1.20.1 (IForgeBlock); metadata — номер варианта блока (SPEC, Р-5)
	override fun getFlammability(state: BlockState, world: IBlockAccess, pos: BlockPos, face: Direction) =
		if (meta % 8 == yggMeta) 0 else super.getFlammability(state, world, pos, face)
	
	override fun getFireSpreadSpeed(state: BlockState, world: IBlockAccess, pos: BlockPos, face: Direction) =
		if (meta % 8 == yggMeta) 0 else super.getFireSpreadSpeed(state, world, pos, face)
//	override fun getFlammability(world: IBlockAccess, x: Int, y: Int, z: Int, face: ForgeDirection?) =
//		if (world.getBlockMetadata(x, y, z) % 8 == yggMeta) 0 else super.getFlammability(world, x, y, z, face)
//
//	override fun getFireSpreadSpeed(world: IBlockAccess, x: Int, y: Int, z: Int, face: ForgeDirection?) =
//		if (world.getBlockMetadata(x, y, z) % 8 == yggMeta) 0 else super.getFireSpreadSpeed(world, x, y, z, face)
	
	override fun register(name: String) {
		GameRegistry.registerBlock(this, ItemUniqueSubtypedBlockMod::class.java, name, ALT_TYPES.size)
	}
	
	/* PORT: иконки → модели (alfheim.port.data.AlfheimBlockStates): altLeaves<вид>, при «быстрой» графике — с `_opaque`
	   (alfheim.port.client.AlfheimModels); светящийся слой листвы мечтаний — altLeavesDreamwoodGlow
	override fun registerBlockIcons(reg: IIconRegister) {
		textures = arrayOf(
			Array(ALT_TYPES.size) { i -> IconHelper.forBlock(reg, this, ALT_TYPES[i]) },
			Array(ALT_TYPES.size) { i -> IconHelper.forBlock(reg, this, "${ALT_TYPES[i]}_opaque") }
		)
		
		glowIcon = IconHelper.forBlock(reg, this, "DreamwoodGlow")
	}
	
	override fun getIcon(side: Int, meta: Int): IIcon {
		setGraphicsLevel(!Blocks.leaves.isOpaqueCube)
		return textures[field_150127_b].safeGet(meta and decayBit().inv())
	}
	*/
	
	// PORT: КТ-2 (партия 8г-2) — саженец древа мечтаний (dreamSapling): пока листва мечтаний без ножниц его не роняет
	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = if (meta % 8 == yggMeta) null else if (meta % 8 == yggMeta + 1) null else AlfheimBlocks.irisSapling.toItem()
//	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = if (meta % 8 == yggMeta) null else if (meta % 8 == yggMeta + 1) AlfheimBlocks.dreamSapling.toItem() else AlfheimBlocks.irisSapling.toItem()
	
	/* PORT: лут — таблица листвы (alfheim.port.data.AlfheimBlockLoot): листва мечтаний роняет вишню мечтаний с шансом
	   1 / (шанс плода BlockLeavesMod.getDrops / 2); с шёлковым касанием — сама листва без бита опадания
	override fun func_150124_c(world: World, x: Int, y: Int, z: Int, meta: Int, chance: Int) {
		if (meta % 8 != yggMeta + 1 || world.rand.nextInt(chance / 2) != 0) return
		
		dropBlockAsItem(world, x, y, z, ElvenFoodMetas.DreamCherry.stack)
	}
	
	override fun createStackedBlock(meta: Int) = ItemStack(this, 1, meta % 8)
	*/
	
	// PORT: func_150123_b (шанс саженца 1/n) BlockLeaves 1.7.10 — без override: в Leaves1710 его нет. Лут листвы автора
	// (BlockLeavesMod.getDrops) его не спрашивал — саженец выпадает с шансом 1/20 (BUGS.md)
	fun func_150123_b(meta: Int) = if (meta == yggMeta) 0 else if (meta == yggMeta + 1) 100 else 60
	
	override fun quantityDropped(random: Random) = if (random.nextInt(func_150123_b(0)) == 0) 1 else 0
	
	/* PORT: имена видов листвы BlockLeaves 1.7.10 (func_150125_e) нужны были его иконкам; в 1.20.1 иконки — модели.
	   Варианты во вкладке — отдельные блоки (AlfheimTab)
	override fun func_150125_e() = ALT_TYPES
	
	override fun getSubBlocks(item: Item, tab: CreativeTabs?, list: MutableList<Any?>) {
		for (i in ALT_TYPES.indices)
			list.add(ItemStack(item, 1, i))
	}
	*/
	
	override fun decayBit() = 0x8
	
	override fun canDecay(meta: Int) = if (meta % 8 == yggMeta) false else super.canDecay(meta)
	
	override fun getDecayRange(meta: Int) = if (meta % 8 == 7) 8 else 4
	
	// PORT: metadata — номер варианта блока (SPEC, Р-5); мир — null у генерации данных: листва Иггдрасиля не в теге
	// minecraft:leaves (Leaves1710.isLeaves)
	override fun isLeaves(world: IBlockAccess?, x: Int, y: Int, z: Int) = if (meta % 8 == yggMeta) false else super.isLeaves(world, x, y, z)
//	override fun isLeaves(world: IBlockAccess, x: Int, y: Int, z: Int) = if (world.getBlockMetadata(x, y, z) % 8 == yggMeta) false else super.isLeaves(world, x, y, z)
	
	// PORT: КТ-9 — лексикон
	/*
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?): LexiconEntry {
		val meta = world.getBlockMetadata(x, y, z)
		return when {
			meta % 8 == yggMeta + 1 -> AlfheimLexiconData.worldgen
			meta % 8 == yggMeta     -> AlfheimLexiconData.legends
			else                    -> AlfheimLexiconData.irisSapling
		}
	}
	*/
	
	// PORT: рендер со светящимся слоем (RenderGlowingLayerBlock) — модель листвы мечтаний со вторым слоем
	// (alfheim.port.data.AlfheimBlockStates): грани поверх листвы, полная яркость, без затенения
//	override fun getRenderType() = if (ASJUtilities.isClient) RenderGlowingLayerBlock.glowBlockID else -1
//
//	override fun getGlowIcon(side: Int, meta: Int) = if (meta % 8 == 7) glowIcon else null
	
	// PORT: metadata — номер варианта блока (SPEC, Р-5)
	override fun randomDisplayTick(world: World, x: Int, y: Int, z: Int, rand: Random) {
		super.randomDisplayTick(world, x, y, z, rand)
		
		if (!AlfheimConfigHandler.increasedSpiritsRange && meta % 8 == 7)
//		if (!AlfheimConfigHandler.increasedSpiritsRange && world.getBlockMetadata(x, y, z) % 8 == 7)
			spawnRandomSpirit(world, x, y, z, rand, 0f, rand.nextFloat() * 0.25f + 0.5f, 1f)
	}
	
	companion object {
		
		// PORT: иконки → модели (alfheim.port.data.AlfheimBlockStates)
//		lateinit var textures: Array<Array<IIcon>>
//		lateinit var glowIcon: IIcon
		
		val yggMeta = ALT_TYPES.indexOf("Wisdom")
		
		fun spawnRandomSpirit(world: World, x: Int, y: Int, z: Int, rand: Random, r: Float, g: Float, b: Float) {
			if (world.worldTime % 24000 !in 13333..22666 || rand.nextInt(512) != 0) return
			
			val i = Math.random()
			val j = Math.random()
			val k = Math.random()
			val s = Math.random()
			val m = Math.random()
			val n = Math.random()
			val o = Math.random()
			val l = Math.random()
			
			Botania.proxy.setWispFXDistanceLimit(false)
			for (q in 0..4)
				Botania.proxy.wispFX(world, x + i, y + j * 5 + 1, z + k, r, g, b, s.F * 0.25f + 0.1f, m.F * 0.1f - 0.05f, n.F * 0.01F, o.F * 0.1f - 0.05f, l.F * 20f + 5f)
			
			if (AlfheimConfigHandler.increasedSpiritsRange) // not so good in close range
				Botania.proxy.wispFX(world, x + i, y + j * 5 + 1, z + k, r / 2, g / 2, b / 2, s.F * 0.25f + 1.5f, m.F * 0.1f - 0.05f, n.F * 0.01F, o.F * 0.1f - 0.05f, l.F * 20f + 5f)
			
			Botania.proxy.setWispFXDistanceLimit(true)
		}
	}
}
