package alfheim.common.block.base

// PORT: импорты 1.20.1; BlockLeaves 1.7.10 — alfheim.port.legacy.Leaves1710 (MAPPING.md, «Растения»)
import alexsocol.asjlib.*
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.block.ItemSubtypedBlockMod
import alfheim.port.legacy.*
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.Block
import net.minecraftforge.api.distmarker.*

// PORT: IShearable 1.7.10 — лут листвы с ножницами (генерация данных, alfheim.port.data.AlfheimBlockLoot); КТ-9 —
// лексикон (ILexiconable)
@Suppress("LeakingThis")
abstract class BlockLeavesMod: Leaves1710()/*, IShearable, ILexiconable*/ {
	
	internal var decayField: IntArray? = null
	// PORT: иконки → модели (alfheim.port.data): текстура — имя блока, при «быстрой» графике — с `_opaque`
	// (alfheim.port.client.AlfheimModels)
//	protected var icons: Array<IIcon?> = emptyArray()
	
	init {
		setCreativeTab(AlfheimTab)
		setHardness(0.2f)
		setLightOpacity(1)
		setStepSound(soundTypeGrass)
		// PORT: анимированные текстуры 1.20.1 рисует сама по .mcmeta; подписка на TextureStitchEvent не нужна
//		if (ASJUtilities.isClient && isInterpolated())
//			MinecraftForge.EVENT_BUS.register(this)
	}
	
	override fun setBlockName(par1Str: String): Block {
		register(par1Str)
		return super.setBlockName(par1Str)
	}
	
	abstract override fun quantityDropped(random: Random): Int
	
	open fun register(name: String) {
		GameRegistry.registerBlock(this, ItemSubtypedBlockMod::class.java, name)
	}
	
	@OnlyIn(Dist.CLIENT)
	override fun getBlockColor() = 0xFFFFFF
	
	@OnlyIn(Dist.CLIENT)
	override fun getRenderColor(meta: Int) = 0xFFFFFF
	
	@OnlyIn(Dist.CLIENT)
	override fun colorMultiplier(world: IBlockAccess, x: Int, y: Int, z: Int) = 0xFFFFFF
	
	// PORT: иконка по настройке графики → модель листвы и модель с `_opaque` для «быстрой» графики (alfheim.port.data,
	// alfheim.port.client.AlfheimModels)
	/*
	@SideOnly(Side.CLIENT)
	override fun getIcon(side: Int, meta: Int): IIcon? {
		setGraphicsLevel(!Blocks.leaves.isOpaqueCube)
		return icons[field_150127_b]
	}
	*/
	
	private fun removeLeaves(world: World, x: Int, y: Int, z: Int) {
		// PORT: dropBlockAsItem 1.7.10 → лут состояния листвы (таблица генерации данных)
		val pos = BlockPos(x, y, z)
		dropResources(world.getBlockState(pos), world, pos)
//		this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0)
		world.setBlockToAir(x, y, z)
	}
	
	/* PORT: иконки → модели (alfheim.port.data); анимация — .mcmeta
	override fun registerBlockIcons(reg: IIconRegister) {
		if (!isInterpolated())
			icons = Array(2) { i -> IconHelper.forBlock(reg, this, if (i == 0) "" else "_opaque") }
	}
	*/
	
	open fun isInterpolated() = false
	
	/*
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	open fun loadTextures(event: TextureStitchEvent.Pre) {
		if (event.map.textureType == 0 && isInterpolated())
			icons = Array(2) { i -> InterpolatedIconHelper.forBlock(event.map, this, if (i == 0) "" else "_opaque") }
	}
	*/
	
	override fun isOpaqueCube() = false
	
	/* PORT: лут с ножницами — таблица листвы (генерация данных): сама листва, бит опадания в предмет не попадает
	override fun isShearable(item: ItemStack?, world: IBlockAccess?, x: Int, y: Int, z: Int) = true
	
	override fun onSheared(item: ItemStack, world: IBlockAccess, x: Int, y: Int, z: Int, fortune: Int): ArrayList<ItemStack> {
		val ret = ArrayList<ItemStack>()
		val meta = world.getBlockMetadata(x, y, z) and decayBit().inv()
		ret.add(ItemStack(this, 1, meta))
		return ret
	}
	*/
	
	// PORT: бревно 1.20.1 не сообщает листве о своей поломке: листва ванилы сама пересчитывает расстояние до бревна
	// (Leaves1710), а листва автора и в 1.7.10 ничего не делала
//	override fun beginLeavesDecay(world: World?, x: Int, y: Int, z: Int) = Unit
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, random: Random) {
		if (world.isRemote) return
		// PORT: metadata листвы 1.7.10 — номер варианта и бит опадания; бит — свойство persistent (Leaves1710)
		val meta = (variant ?: 0) or if (world.getBlockState(BlockPos(x, y, z)).getValue(PERSISTENT)) decayBit() else 0
//		val meta = world.getBlockMetadata(x, y, z)
		if (!canDecay(meta)) return
		
		val range = getDecayRange(meta)
		val extraRange = range + 1
		val bufferSize = extraRange * 2 + 1
		val squareBufferSize = bufferSize * bufferSize
		val halfBufferSize = bufferSize / 2
		val arraySize = bufferSize * bufferSize * bufferSize
		
		if (decayField?.run { size == arraySize } != true) {
			decayField = IntArray(arraySize)
		}
		val decayField = decayField!!
		
		if (world.checkChunksExist(x - extraRange, y - extraRange, z - extraRange, x + extraRange, y + extraRange, z + extraRange)) {
			for (i in 0.bidiRange(range)) {
				for (j in 0.bidiRange(range)) {
					for (k in 0.bidiRange(range)) {
						val block = world.getBlock(x + i, y + j, z + k)
						
						if (!block.canSustainLeaves(world, x + i, y + j, z + k)) {
							if (block.isLeaves(world, x + i, y + j, z + k)) {
								decayField[(i + halfBufferSize) * squareBufferSize + (j + halfBufferSize) * bufferSize + k + halfBufferSize] = -2
							} else {
								decayField[(i + halfBufferSize) * squareBufferSize + (j + halfBufferSize) * bufferSize + k + halfBufferSize] = -1
							}
						} else {
							decayField[(i + halfBufferSize) * squareBufferSize + (j + halfBufferSize) * bufferSize + k + halfBufferSize] = 0
						}
					}
				}
			}
			
			for (state in 1..range) {
				for (i in 0.bidiRange(range)) {
					for (j in 0.bidiRange(range)) {
						for (k in 0.bidiRange(range)) {
							if (decayField[(i + halfBufferSize) * squareBufferSize + (j + halfBufferSize) * bufferSize + k + halfBufferSize] == state - 1) {
								if (decayField[(i + halfBufferSize - 1) * squareBufferSize + (j + halfBufferSize) * bufferSize + k + halfBufferSize] == -2) {
									decayField[(i + halfBufferSize - 1) * squareBufferSize + (j + halfBufferSize) * bufferSize + k + halfBufferSize] = state
								}
								
								if (decayField[(i + halfBufferSize + 1) * squareBufferSize + (j + halfBufferSize) * bufferSize + k + halfBufferSize] == -2) {
									decayField[(i + halfBufferSize + 1) * squareBufferSize + (j + halfBufferSize) * bufferSize + k + halfBufferSize] = state
								}
								
								if (decayField[(i + halfBufferSize) * squareBufferSize + (j + halfBufferSize - 1) * bufferSize + k + halfBufferSize] == -2) {
									decayField[(i + halfBufferSize) * squareBufferSize + (j + halfBufferSize - 1) * bufferSize + k + halfBufferSize] = state
								}
								
								if (decayField[(i + halfBufferSize) * squareBufferSize + (j + halfBufferSize + 1) * bufferSize + k + halfBufferSize] == -2) {
									decayField[(i + halfBufferSize) * squareBufferSize + (j + halfBufferSize + 1) * bufferSize + k + halfBufferSize] = state
								}
								
								if (decayField[(i + halfBufferSize) * squareBufferSize + (j + halfBufferSize) * bufferSize + (k + halfBufferSize - 1)] == -2) {
									decayField[(i + halfBufferSize) * squareBufferSize + (j + halfBufferSize) * bufferSize + (k + halfBufferSize - 1)] = state
								}
								
								if (decayField[(i + halfBufferSize) * squareBufferSize + (j + halfBufferSize) * bufferSize + k + halfBufferSize + 1] == -2) {
									decayField[(i + halfBufferSize) * squareBufferSize + (j + halfBufferSize) * bufferSize + k + halfBufferSize + 1] = state
								}
							}
						}
					}
				}
			}
		}
		val state = decayField[halfBufferSize * squareBufferSize + halfBufferSize * bufferSize + halfBufferSize]
		
		if (state < 0) removeLeaves(world, x, y, z)
	}
	
	// PORT: выбор колёсиком — предмет блока (getCloneItemStack 1.20.1): вариант — сам блок, бита опадания у предмета нет
	/*
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int, player: EntityPlayer?) =
		ItemStack(this, 1, world.getBlockMetadata(x, y, z) and decayBit().inv())
	*/
	
	abstract fun decayBit(): Int
	
	open fun getDecayRange(meta: Int) = 4
	
	open fun canDecay(meta: Int) = meta and decayBit() == 0
	
	/* PORT: лут — таблица листвы (генерация данных, alfheim.port.data.AlfheimBlockLoot): с ножницами или шёлковым
	   касанием — сама листва, иначе — getItemDropped с шансом 1/20, с удачей I, II, III — 1/16, 1/12, 1/10, как здесь;
	   плоды наследников (func_150124_c) — там же. Взрыв лут не уменьшает: dropBlockAsItemWithChance бросал вещи с
	   шансом 1. getDamageValue (metadata для выбора колёсиком) не нужен: вариант — сам блок
	override fun getDamageValue(world: World, x: Int, y: Int, z: Int) = world.getBlockMetadata(x, y, z)
	
	override fun dropBlockAsItemWithChance(world: World, x: Int, y: Int, z: Int, metadata: Int, chance: Float, fortune: Int) {
		super.dropBlockAsItemWithChance(world, x, y, z, metadata, 1f, fortune)
	}
	
	override fun getDrops(world: World, x: Int, y: Int, z: Int, metadata: Int, fortune: Int): ArrayList<ItemStack> {
		val ret: ArrayList<ItemStack> = ArrayList()
		
		var chance = 20
		
		if (fortune > 0) {
			chance -= 2 shl fortune
			if (chance < 10) chance = 10
		}
		
		if (world.rand.nextInt(chance) == 0)
			ret.add(ItemStack(getItemDropped(metadata, world.rand, fortune), 1, 0))
		
		chance = 200
		if (fortune > 0) {
			chance -= 10 shl fortune
			if (chance < 40) chance = 40
		}
		
		this.captureDrops(true)
		func_150124_c(world, x, y, z, metadata, chance)
		ret.addAll(this.captureDrops(false))
		return ret
	}
	*/
}
