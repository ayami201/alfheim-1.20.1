package alfheim.common.block.colored.rainbow

// PORT: импорты 1.20.1; BlockDoublePlant 1.7.10 — alfheim.port.legacy.DoublePlant1710 (MAPPING.md, «Растения»)
import alfheim.common.block.base.IDoublePlant
import alfheim.common.block.colored.BlockAuroraDirt
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.block.ItemRainbowDoubleGrassMod
import alfheim.port.legacy.*
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import net.minecraftforge.api.distmarker.*

// PORT: вариант metadata (GRASS, AURORA) — отдельный блок (SPEC, Р-5): номер варианта — meta, создают массивом
// `Array(2) { BlockRainbowDoubleGrass(it) }`; верхняя половина (бит 8 metadata) — свойство half (DoublePlant1710). КТ-9 —
// лексикон (ILexiconable)
class BlockRainbowDoubleGrass(val meta: Int): DoublePlant1710(), IDoublePlant/*, ILexiconable*/ {
	
	val name = "rainbowDoubleGrass"
	// PORT: иконки половин → модели (alfheim.port.data.AlfheimBlockStates): низ — rainbowDoubleGrass, верх —
	// rainbowDoubleGrassTop, у авроровой — модели двойной травы ириса
//	lateinit var topIcon: IIcon
//	lateinit var bottomIcon: IIcon
	
	val GRASS = 0
	val AURORA = 1
	
	override val variant get() = meta
	
	init {
		setBlockNameSafe(name)
		setCreativeTab(AlfheimTab)
		setStepSound(soundTypeGrass)
		// PORT: анимированные текстуры 1.20.1 рисует сама по .mcmeta; подписка на TextureStitchEvent не нужна
//		if (ASJUtilities.isClient)
//			MinecraftForge.EVENT_BUS.register(this)
	}
	
	/* PORT: иконки → модели (alfheim.port.data.AlfheimBlockStates); анимация — .mcmeta
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun loadTextures(event: TextureStitchEvent.Pre) {
		if (event.map.textureType == 0) {
			topIcon = InterpolatedIconHelper.forBlock(event.map, this, "Top")
			bottomIcon = InterpolatedIconHelper.forBlock(event.map, this)
		}
	}
	*/
	
	override fun func_149851_a(world: World, x: Int, y: Int, z: Int, isRemote: Boolean) = false
	
	override fun func_149853_b(world: World, random: Random, x: Int, y: Int, z: Int) = Unit
	
	internal fun register(name: String) {
		GameRegistry.registerBlock(this, ItemRainbowDoubleGrassMod::class.java, name)
	}
	
	fun setBlockNameSafe(par1Str: String): Block {
		register(par1Str)
		return super.setBlockName(par1Str)
	}
	
	// PORT: setBlockName прослойки возвращает блок — null не вернуть; имя ставит setBlockNameSafe, как у автора
	override fun setBlockName(par1Str: String) = this
//	override fun setBlockName(par1Str: String) = null
	
	@OnlyIn(Dist.CLIENT)
	override fun getBlockColor() = 0xFFFFFF
	
	@OnlyIn(Dist.CLIENT)
	override fun getRenderColor(meta: Int) = 0xFFFFFF
	
	// PORT: номер варианта — сам блок, верхняя половина (бит 8) — свойство half. Верхнюю половину рендер автора
	// (RenderBlockColoredDoubleGrass) красил цветом нижней: colorMultiplier в точке под ней
	@OnlyIn(Dist.CLIENT)
	override fun colorMultiplier(world: IBlockAccess, x: Int, y: Int, z: Int) = run {
		val top = world.getBlockState(BlockPos(x, y, z)).let { it.block === this && it.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER }
		when (meta) {
			AURORA -> BlockAuroraDirt.getBlockColor(x, if (top) y - 1 else y, z)
			else   -> 0xFFFFFF
		}
	}
//	override fun colorMultiplier(world: IBlockAccess, x: Int, y: Int, z: Int) = run {
//		when (world.getBlockMetadata(x, y, z)) {
//			AURORA     -> BlockAuroraDirt.getBlockColor(x, y, z)
//			AURORA + 8 -> BlockAuroraDirt.getBlockColor(x, y + 1, z)
//			else       -> 0xFFFFFF
//		}
//	}
	
	/* PORT: варианты во вкладке — отдельные блоки (AlfheimTab); иконки → модели половин и предмета
	   (alfheim.port.data.AlfheimBlockStates)
	override fun getSubBlocks(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>) {
		list.add(ItemStack(item, 1, GRASS))
		list.add(ItemStack(item, 1, AURORA))
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(iconRegister: IIconRegister) = Unit
	
	@SideOnly(Side.CLIENT)
	override fun getIcon(side: Int, meta: Int) = getTopIcon(meta)
	
	@SideOnly(Side.CLIENT)
	override fun func_149888_a(top: Boolean, index: Int) = if (top) getTopIcon(index) else getBottomIcon(index)
	*/
	
	/* PORT: поломку обеих половин ведёт DoublePlantBlock 1.20.1: вторая половина исчезает без лута, в творческом режиме
	   нижняя тоже ничего не роняет (onBlockHarvested ниже). Лут — таблица (alfheim.port.data.AlfheimBlockLoot): с
	   ножницами — две радужные травы того же варианта (onSheared), без них ничего (getItemDropped = null); шёлковое
	   касание двойное растение 1.7.10 не брало (canSilkHarvest: не обычный куб). Рисует модель (getRenderType)
	override fun harvestBlock(world: World, player: EntityPlayer, x: Int, y: Int, z: Int, meta: Int) {
		if (world.isRemote || player.currentEquippedItem == null || player.currentEquippedItem.item !== Items.shears || func_149887_c(meta)/*|| !dropBlock(world, x, y, z, meta, player)*/) {
			superSuperHarvestBlock(world, player, x, y, z, meta)
		}
	}
	
	// stupid private methods
	fun superSuperHarvestBlock(world: World, player: EntityPlayer, x: Int, y: Int, z: Int, meta: Int) {
		player.addStat(StatList.mineBlockStatArray[id], 1)
		player.addExhaustion(0.025f)
		
		if (this.canSilkHarvest(world, player, x, y, z, meta) && EnchantmentHelper.getSilkTouchModifier(player)) {
			val items = ArrayList<ItemStack>()
			val itemstack = createStackedBlock(meta)
			
			if (itemstack != null)
				items.add(itemstack)
			
			ForgeEventFactory.fireBlockHarvesting(items, world, this, x, y, z, meta, 0, 1f, true, player)
			for (item in items)
				this.dropBlockAsItem(world, x, y, z, item)
		} else {
			harvesters.set(player)
			val i1 = EnchantmentHelper.getFortuneModifier(player)
			this.dropBlockAsItem(world, x, y, z, meta, i1)
			harvesters.set(null)
		}
	}
	
	override fun onBlockHarvested(world: World, x: Int, y: Int, z: Int, meta: Int, player: EntityPlayer) {
		if (func_149887_c(meta)) {
			if (world.getBlock(x, y - 1, z) === this) {
				if (!player.capabilities.isCreativeMode) {
					val i1 = world.getBlockMetadata(x, y - 1, z)
					val j1 = func_149890_d(i1)
					
					if (j1 == 3 || j1 == 2) {
						world.setBlockToAir(x, y - 1, z)
					}
				} else {
					world.setBlockToAir(x, y - 1, z)
				}
			}
		} else if (player.capabilities.isCreativeMode && world.getBlock(x, y + 1, z) === this) {
			world.setBlock(x, y + 1, z, Blocks.air, 0, 2)
		}
	}
	
	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = null
	
	override fun onSheared(item: ItemStack, world: IBlockAccess, x: Int, y: Int, z: Int, fortune: Int): ArrayList<ItemStack> {
		val ret = ArrayList<ItemStack>()
		val meta = world.getBlockMetadata(x, y, z)
		if (func_149887_c(meta)) {
			if (y > 0 && world.getBlock(x, y - 1, z) == this) {
				ret.add(ItemStack(AlfheimBlocks.rainbowGrass, 2, world.getBlockMetadata(x, y - 1, z)))
			}
		} else {
			ret.add(ItemStack(AlfheimBlocks.rainbowGrass, 2, meta))
		}
		return ret
	}
	
	override fun getRenderType() = LibRenderIDs.idDoubleFlower
	
	override fun isShearable(item: ItemStack, world: IBlockAccess, x: Int, y: Int, z: Int) = true
	*/
	
	/* PORT: КТ-9 — лексикон
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer?, stack: ItemStack?) =
		when (world.getBlockMetadata(x, y, z)) {
			GRASS, AURORA -> AlfheimLexiconData.pastoralSeeds
			else          -> null
		}
	*/
	
	/* PORT: иконки половин → модели (alfheim.port.data.AlfheimBlockStates)
	override fun getBottomIcon(lowerMeta: Int) =
		when (lowerMeta) {
			AURORA -> (AlfheimBlocks.irisTallGrass0 as IDoublePlant).getBottomIcon(0)
			else   -> bottomIcon
		}
	
	override fun getTopIcon(lowerMeta: Int) =
		when (lowerMeta) {
			AURORA -> (AlfheimBlocks.irisTallGrass0 as IDoublePlant).getTopIcon(0)
			else   -> topIcon
		}
	*/
}
