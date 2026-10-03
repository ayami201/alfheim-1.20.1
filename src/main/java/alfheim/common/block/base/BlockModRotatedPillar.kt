package alfheim.common.block.base

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alfheim.port.legacy.*
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.RotatedPillarBlock.AXIS
import net.minecraft.world.level.block.state.*

// PORT: КТ-9 — лексикон (ILexiconable)
abstract class BlockModRotatedPillar(mat: Material): BlockMod(mat)/*, ILexiconable*/ {
	
	// PORT: иконки → модель столба (alfheim.port.data.AlfheimBlockStates): торцы — iconTop, бока — iconSide
//	protected var iconTop: IIcon? = null
//	protected var iconSide: IIcon? = null
	
	// PORT: поворот в metadata (meta and 12: 0 — вверх, 4 — вдоль X, 8 — вдоль Z) → свойство состояния axis, как у
	// RotatedPillarBlock 1.20.1. Номер варианта (meta and 3) — отдельный блок (SPEC, Р-5)
	init {
		registerDefaultState(defaultBlockState().setValue(AXIS, Direction.Axis.Y))
	}
	
	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(AXIS)
	}
	
	@Deprecated("Deprecated in Java")
	override fun rotate(state: BlockState, rotation: Rotation): BlockState = RotatedPillarBlock.rotatePillar(state, rotation)
	
	override fun setBlockName(name: String): Block {
		register(name)
		return super.setBlockName(name)
	}
	
	open fun register(name: String) {
		// PORT: КТ-2 — ItemIridescentBlockMod переносится с цветными деревьями; блоки этой партии переопределяют register
//		GameRegistry.registerBlock(this, ItemIridescentBlockMod::class.java, name)
		throw IllegalStateException("ItemIridescentBlockMod is not ported yet: $name")
	}
	
	// PORT: лут (alfheim.port.data.AlfheimBlockLoot) — сам блок, один
	/*
	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = this.toItem()
	
	@SideOnly(Side.CLIENT)
	override fun getIcon(side: Int, meta: Int): IIcon? {
		val k = meta and 12
		return if (k == 0 && (side == 1 || side == 0)) getTopIcon(meta and 3) else (if (k == 4 && (side == 5 || side == 4)) getTopIcon(meta and 3) else (if (k == 8 && (side == 2 || side == 3)) getTopIcon(meta and 3) else getSideIcon(meta and 3)))
	}
	
	override fun quantityDropped(random: Random) = 1
	*/
	
	override fun shouldRegisterInNameSet() = false
	
	/* PORT: лут и модель столба — генерация данных (alfheim.port.data)
	override fun damageDropped(meta: Int) = meta and 3
	
	override fun getRenderType() = 31
	
	@SideOnly(Side.CLIENT)
	open fun getSideIcon(meta: Int) = iconSide
	
	@SideOnly(Side.CLIENT)
	open fun getTopIcon(meta: Int) = iconTop
	
	override fun createStackedBlock(meta: Int) = ItemStack(this, 1, meta and 3)
	*/
	
	// PORT: onBlockPlaced → getStateForPlacement: ось — по стороне, на которую ставят. Предмета с поворотом
	// (meta and 0b1100 == 0b1100) в 1.20.1 нет: поворот — состояние блока
	override fun getStateForPlacement(context: BlockPlaceContext): BlockState? = defaultBlockState().setValue(AXIS, context.clickedFace.axis)
	/*
	override fun onBlockPlaced(world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float, meta: Int): Int {
		if (meta and 0b1100 == 0b1100) return meta
		
		val base = meta and 3
		val rotation = when (side) {
			2, 3 -> 8
			4, 5 -> 4
			else -> 0
		}
		
		return base or rotation
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(reg: IIconRegister) {
		if (!isInterpolated()) {
			iconTop = IconHelper.forBlock(reg, this, "Top")
			iconSide = IconHelper.forBlock(reg, this, "Side")
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	override fun loadTextures(event: TextureStitchEvent.Pre) {
		if (event.map.textureType == 0 && isInterpolated()) {
			iconTop = InterpolatedIconHelper.forBlock(event.map, this, "Top")
			iconSide = InterpolatedIconHelper.forBlock(event.map, this, "Side")
		}
	}
	
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int, player: EntityPlayer): ItemStack {
		val meta = world.getBlockMetadata(x, y, z)
		return ItemStack(this, 1, meta and 3)
	}
	*/
}
