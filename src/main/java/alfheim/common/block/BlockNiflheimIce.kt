package alfheim.common.block

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md); защита существ Нифльхейма (КТ-5), кулон (КТ-4),
// измерение и портал Нифльхейма (КТ-6), лексикон (КТ-9) закомментированы вместе со своими строками
import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.MaterialPublic
import alexsocol.asjlib.math.Vector3
//import alfheim.api.entity.INiflheimEntity
import alfheim.common.block.base.BlockMod
import alfheim.common.core.handler.AlfheimConfigHandler
//import alfheim.common.item.equipment.bauble.ItemPendant
//import alfheim.common.lexicon.AlfheimLexiconData
import alfheim.common.potion.PotionEternity
import alfheim.port.legacy.*
import alfheim.port.legacy.Potion1710 as Potion
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource as Random
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.shapes.*
import net.minecraftforge.common.ForgeHooks
import vazkii.botania.xplat.BotaniaConfig

// PORT: КТ-9 — лексикон (ILexiconable)
class BlockNiflheimIce: BlockMod(material)/*, ILexiconable*/ {
	
	init {
		setBlockName("NiflheimIce")
		setCreativeTab(null)
		setBlockUnbreakable()
		setHarvestLevel("pickaxe", 2)
		setLightOpacity(0)
		setResistance(6000000F)
		setStepSound(soundTypeGlass)
		tickRandomly = true
		slipperiness = 0.98f
		// PORT: metadata — свойство type
		registerDefaultState(stateDefinition.any().setValue(TYPE, IceType.NORMAL))
	}
	
	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(TYPE)
	}
	
	// PORT: рамка столкновений — форма столкновений; рамка автора — куб без 0,01 с каждой стороны (COLLISION_SHAPE)
	@Deprecated("Deprecated in Java")
	override fun getCollisionShape(state: BlockState, level: IBlockAccess, pos: BlockPos, context: CollisionContext) = COLLISION_SHAPE
//	override fun getCollisionBoundingBoxFromPool(world: World?, x: Int, y: Int, z: Int): AxisAlignedBB {
//		return super.getCollisionBoundingBoxFromPool(world, x, y, z).expand(-0.01)
//	}
	
	// PORT: рамка столкновений в 1.7.10 не мешала блоку быть нормальным кубом (материал непрозрачный, рисуется кубом): к
	// нему крепятся факелы, на нём появляются мобы, он проводит сигнал, в нём задыхаются (форма опоры —
	// alfheim.port.legacy.Material), он затеняет углы соседей (AO, 0,2). 1.20.1 судила бы по столкновениям — так же
	// сделан песок душ 1.20.1
	@Deprecated("Deprecated in Java")
	override fun getBlockSupportShape(state: BlockState, level: IBlockAccess, pos: BlockPos): VoxelShape = Shapes.block()
	
	@Deprecated("Deprecated in Java")
	override fun getShadeBrightness(state: BlockState, level: IBlockAccess, pos: BlockPos) = 0.2f
	
	// PORT: getPlayerRelativeBlockHardness → getDestroyProgress 1.20.1: твёрдость — состояния в точке, скорость добычи —
	// getDigSpeed, можно ли добыть — ForgeHooks.isCorrectToolForDrops (MAPPING.md)
	@Deprecated("Deprecated in Java")
	override fun getDestroyProgress(state: BlockState, player: EntityPlayer, world: IBlockAccess, pos: BlockPos): Float {
//	override fun getPlayerRelativeBlockHardness(player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Float {
//		val metadata = world.getBlockMetadata(x, y, z)
		var hardness = state.getDestroySpeed(world, pos)
//		var hardness = getBlockHardness(world, x, y, z)
		
		// PORT: КТ-4 — кулон (ItemPendant)
//		if (ItemPendant.canProtect(player, ItemPendant.Companion.EnumPrimalWorldType.NIFLHEIM, 5)) hardness = 2f
		
		if (hardness <= 0f) return 0f
		
		return player.getDigSpeed(state, pos) / hardness / if (!ForgeHooks.isCorrectToolForDrops(state, player)) 100f else 30f
//		return player.getBreakSpeed(this, true, metadata, x, y, z) / hardness / if (!ForgeHooks.canHarvestBlock(this, player, metadata)) 100f else 30f
	}
	
	override fun isOpaqueCube() = false
	
	// PORT: проход рендера читает и генерация моделей — не только клиент
//	@SideOnly(Side.CLIENT)
	override fun getRenderBlockPass() = 1
	
	// PORT: shouldSideBeRendered (рисовать грань, если сосед — не этот блок и не непрозрачный куб) → skipRendering (не
	// рисовать, если этот); грань за непрозрачным кубом 1.20.1 не рисует сама
	@Deprecated("Deprecated in Java")
	override fun skipRendering(state: BlockState, adjacent: BlockState, side: Direction) = adjacent.block === this
//	@SideOnly(Side.CLIENT)
//	override fun shouldSideBeRendered(world: IBlockAccess, x: Int, y: Int, z: Int, side: Int) =
//		world.getBlock(x, y, z) !== this && !world.getBlock(x, y, z).isOpaqueCube
	
	// PORT: лут — таблица (alfheim.port.data.AlfheimBlockLoot): ничего, и с шёлковым касанием — его вещь шла через
	// dropBlockAsItem, а тот ничего не ронял
	fun quantityDropped(r: Random?) = 0
//	override fun quantityDropped(r: Random?) = 0
	
//	override fun dropBlockAsItem(w: World, x: Int, y: Int, z: Int, s: ItemStack) = Unit
	
	override fun onEntityWalking(w: World, x: Int, y: Int, z: Int, e: Entity) {
		// PORT: КТ-5 — защита существ Нифльхейма, игрока в творчестве или с кулоном (КТ-4), мобов из mobBlacklistCold
		// (INiflheimEntity.checkProtection); до неё лёд держит всех
//		if (INiflheimEntity.checkProtection(e, 50)) return
		
		e.setInWeb()
		if (w.isRemote || e !is EntityLivingBase) return
		
		e.addPotionEffect(PotionEffectU(Potion.moveSlowdown.id, 25, 2))
		e.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDEternity, 100, PotionEternity.ATTACK))
	}
	
	override fun onEntityCollidedWithBlock(w: World, x: Int, y: Int, z: Int, e: Entity) {
		onEntityWalking(w, x, y, z, e)
	}
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, rand: Random) {
		// PORT: metadata — свойство type
		if (world.getBlockState(BlockPos(x, y, z)).getValue(TYPE) == IceType.FREEZING) replaceNearestWater(world, x, y, z)
//		if (world.getBlockMetadata(x, y, z) == 1) replaceNearestWater(world, x, y, z)
		
		// PORT: КТ-6 — измерение Нифльхейм (AlfheimConfigHandler.dimensionIDNiflheim): в нём лёд не тает; до КТ-6 его нет
//		if (world.provider.dimensionId == AlfheimConfigHandler.dimensionIDNiflheim) return
		if (!world.gameRules.getBoolean(GameRules.RULE_DOFIRETICK)) return
//		if (!world.gameRules.getGameRuleBooleanValue("doFireTick")) return
		// PORT: раскол (replaceNearestWater выше) оставляет на месте льда воздух: metadata воздуха в 1.7.10 — 0, свойства type у
		// него нет
		if (world.getBlockState(BlockPos(x, y, z)).getOptionalValue(TYPE).orElse(null) == IceType.PERMANENT) return
//		if (world.getBlockMetadata(x, y, z) == 2) return
		
		val below = world.getBlock(x, y - 1, z)
		if (below != Blocks.PACKED_ICE && below != this && world.rand.nextInt(100) == 0)
			world.setBlockToAir(x, y, z)
//		if (below != Blocks.packed_ice && below != this && world.rand.nextInt(100) == 0)
//			world.setBlockToAir(x, y, z)
	}
	
	fun replaceNearestWater(world: World, x: Int, y: Int, z: Int) {
		for (d in ForgeDirection.VALID_DIRECTIONS) {
			val (i, j, k) = Vector3(x, y, z).add(d.offsetX, d.offsetY, d.offsetZ).I
			// PORT: стоячая и текучая вода 1.20.1 — один блок (MAPPING.md)
			if (world.getBlock(i, j, k) !== Blocks.WATER) continue
//			if (world.getBlock(i, j, k) !== Blocks.water) continue
			
			// PORT: metadata 1 — type=freezing
			world.setBlock(i, j, k, defaultBlockState().setValue(TYPE, IceType.FREEZING), 2)
//			world.setBlock(i, j, k, this, 1, 2)
			world.scheduleBlockUpdate(i, j, k, this, 1)
			destroyNextTick = false
		}
		
		if (destroyNextTick) {
			// PORT: настройка Botania 1.20.1 — BotaniaConfig; playAuxSFX 2001 (звук и частицы поломки) → levelEvent 2001 с
			// состоянием блока: metadata 0 — состояние по умолчанию
			if (BotaniaConfig.common().blockBreakParticles()) world.levelEvent(2001, BlockPos(x, y, z), Block.getId(defaultBlockState()))
//			if (ConfigHandler.blockBreakParticles) world.playAuxSFX(2001, x, y, z, getIdFromBlock(this) + (0 shl 12))
			
			// PORT: КТ-6 — портал Нифльхейма (BlockNiflheimPortal): лёд в единственной точке портала становится порталом
			world.setBlockToAir(x, y, z)
//			if (ChunkCoordinates(x, y, z) != BlockNiflheimPortal.onlyPortalPosition(world))
//				world.setBlockToAir(x, y, z)
//			else
//				world.setBlock(x, y, z, AlfheimBlocks.niflheimPortal)
			
			return
		}
		
		world.scheduleBlockUpdate(x, y, z, this, 5)
	}
	
	// PORT: частоту тиков (tickRate) 1.7.10 у этого блока не спрашивал: задержку дают вызовы scheduleBlockUpdate выше
//	override fun tickRate(world: World?) = 1
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, lexicon: ItemStack) = AlfheimLexiconData.ruling
	
	companion object {
		// PORT: КТ-6 — флаг поднимает в конце тика каждого мира портал Нифльхейма (BlockNiflheimPortal.updateDestroyFrost);
		// до неё замораживающий лёд, однажды найдя воду, больше не исчезает
		var destroyNextTick = true
		
		private val material = MaterialPublic(MapColor.ICE, requiresNoTool = false)
//		private val material = MaterialPublic(MapColor.iceColor, requiresNoTool = false)
		
		// PORT: metadata 0–2 — свойство type: 0 — обычный лёд, 1 — замораживает воду вокруг (его ставит портал Нифльхейма,
		// КТ-6), 2 — не тает
		val TYPE: EnumProperty<IceType> = EnumProperty.create("type", IceType::class.java)
		
		/** Рамка столкновений (getCollisionBoundingBoxFromPool): куб без 0,01 с каждой стороны */
		private val COLLISION_SHAPE = Shapes.box(0.01, 0.01, 0.01, 0.99, 0.99, 0.99)
	}
	
	/** Вид льда — metadata 1.7.10 по порядку */
	enum class IceType: StringRepresentable {
		NORMAL, FREEZING, PERMANENT;
		
		override fun getSerializedName() = name.lowercase()
	}
}