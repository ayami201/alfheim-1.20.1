package alfheim.common.block.alt

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alexsocol.asjlib.*
import alfheim.api.lib.LibOreDict.ALT_TYPES
import alfheim.common.block.base.BlockMod
import alfheim.common.item.block.ItemUniqueSubtypedBlockMod
import alfheim.port.legacy.*
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

// PORT: вариант metadata — отдельный блок (SPEC, Р-5): номер варианта — meta, создают массивом
// `Array(ALT_TYPES.size - 1) { BlockAltPlanks(it) }`; доски Иггдрасиля — вариант BlockAltLeaves.yggMeta. КТ-9 — лексикон
// (ILexiconable)
class BlockAltPlanks(val meta: Int): BlockMod(Material.wood)/*, ILexiconable*/, IFuelHandler {
	
	val name = "altPlanks"
	// PORT: иконки → модели (alfheim.port.data.AlfheimBlockStates): altPlanks<вид>; кадры опалённых досок сглаживает .mcmeta
//	var icons: Array<IIcon?> = emptyArray()
	
	override val variant get() = meta
	
	init {
		blockHardness = 2F
		// PORT: твёрдость в мире (getBlockHardness ниже) 1.20.1 хранит в состояниях блока
		if (meta == BlockAltLeaves.yggMeta) destroySpeed = 100f
		setLightLevel(0f)
		stepSound = soundTypeWood
		
		setBlockName(name)
		GameRegistry.registerFuelHandler(this)
		// PORT: анимированные текстуры 1.20.1 рисует сама по .mcmeta; подписка на TextureStitchEvent не нужна
//		if (ASJUtilities.isClient)
//			MinecraftForge.EVENT_BUS.register(this)
	}
	
	// PORT: сигнатура 1.20.1 (IForgeBlock); metadata — номер варианта блока (SPEC, Р-5)
	override fun getExplosionResistance(state: BlockState, world: IBlockAccess, pos: BlockPos, explosion: Explosion) =
		if (meta == BlockAltLeaves.yggMeta)
			1000f
		else
			super.getExplosionResistance(state, world, pos, explosion)
//	override fun getExplosionResistance(entity: Entity?, world: World, x: Int, y: Int, z: Int, explosionX: Double, explosionY: Double, explosionZ: Double) =
//		if (world.getBlockMetadata(x, y, z) == BlockAltLeaves.yggMeta)
//			1000f
//		else
//			super.getExplosionResistance(entity, world, x, y, z, explosionX, explosionY, explosionZ)
	
	// PORT: твёрдость в мире — в состояниях блока (init выше)
//	override fun getBlockHardness(world: World, x: Int, y: Int, z: Int) =
//		if (world.getBlockMetadata(x, y, z) == BlockAltLeaves.yggMeta)
//			100f
//		else
//			super.getBlockHardness(world, x, y, z)
	
	// PORT: сигнатуры 1.20.1 (IForgeBlock); metadata — номер варианта блока (SPEC, Р-5)
	override fun getFlammability(state: BlockState, world: IBlockAccess, pos: BlockPos, face: Direction) =
		if (meta == BlockAltLeaves.yggMeta) 0 else super.getFlammability(state, world, pos, face)
	
	override fun getFireSpreadSpeed(state: BlockState, world: IBlockAccess, pos: BlockPos, face: Direction) =
		if (meta == BlockAltLeaves.yggMeta) 0 else super.getFireSpreadSpeed(state, world, pos, face)
//	override fun getFlammability(world: IBlockAccess, x: Int, y: Int, z: Int, face: ForgeDirection?) =
//		if (world.getBlockMetadata(x, y, z) == BlockAltLeaves.yggMeta) 0 else super.getFlammability(world, x, y, z, face)
//
//	override fun getFireSpreadSpeed(world: IBlockAccess, x: Int, y: Int, z: Int, face: ForgeDirection?) =
//		if (world.getBlockMetadata(x, y, z) == BlockAltLeaves.yggMeta) 0 else super.getFireSpreadSpeed(world, x, y, z, face)
	
	/* PORT: иконки → модели (alfheim.port.data.AlfheimBlockStates); анимация — .mcmeta
	override fun registerBlockIcons(reg: IIconRegister) {
		icons = Array(ALT_TYPES.size - 1) { i ->
			if (i == ALT_TYPES.indexOf("Scorched")) null else IconHelper.forBlock(reg, this, ALT_TYPES[i])
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	override fun loadTextures(event: TextureStitchEvent.Pre) {
		if (event.map.textureType == 0) {
			icons[ALT_TYPES.indexOf("Scorched")] = InterpolatedIconHelper.forBlock(event.map, this, "Scorched")
		}
	}
	
	override fun getIcon(side: Int, meta: Int) = icons.safeGet(meta % (ALT_TYPES.size - 1))
	*/
	
	override fun isToolEffective(type: String?, metadata: Int) = (type != null && type == "axe")
	
	override fun getHarvestTool(metadata: Int) = "axe"
	
	override fun shouldRegisterInNameSet() = false
	
	// PORT: лут — сам блок (alfheim.port.data.AlfheimBlockLoot): вариант — сам блок
//	override fun damageDropped(par1: Int) = par1
	
	override fun setBlockName(name: String): Block {
		register(name)
		return super.setBlockName(name)
	}
	
	// PORT: лут — сам блок, один (alfheim.port.data.AlfheimBlockLoot)
//	override fun quantityDropped(random: Random) = 1
//
//	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = this.toItem()
	
	internal fun register(name: String) {
		GameRegistry.registerBlock(this, ItemUniqueSubtypedBlockMod::class.java, name, ALT_TYPES.size - 1)
	}
	
	/* PORT: варианты во вкладке — отдельные блоки (AlfheimTab); выбор колёсиком — предмет блока (getCloneItemStack 1.20.1)
	override fun getSubBlocks(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>?) {
		if (list != null && item != null)
			for (i in 0 until ALT_TYPES.size - 1) {
				list.add(ItemStack(item, 1, i))
			}
	}
	
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int, player: EntityPlayer): ItemStack {
		val meta = world.getBlockMetadata(x, y, z)
		return ItemStack(this, 1, meta)
	}
	*/
	
	// PORT: КТ-9 — лексикон
	/*
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?): LexiconEntry? {
		return when (world.getBlockMetadata(x, y, z)) {
			BlockAltLeaves.yggMeta + 1 -> AlfheimLexiconData.worldgen
			BlockAltLeaves.yggMeta     -> null
			else                       -> AlfheimLexiconData.irisSapling
		}
	}
	*/
	
	// PORT: деревянный блок печь 1.7.10 жгла 300 тиков раньше обработчиков модов: Int.MAX_VALUE / 13 / 4 у досок
	// Иггдрасиля не срабатывал (Fuel1710; BUGS.md)
	override fun getBurnTime(fuel: ItemStack) = if (fuel.item === this.toItem()) if (fuel.meta == BlockAltLeaves.yggMeta) Int.MAX_VALUE / 13 / 4 else 300 else 0
}
