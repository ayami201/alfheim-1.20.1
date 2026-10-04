package alfheim.common.block.colored

// PORT: импорты 1.20.1; BlockDoublePlant 1.7.10 — alfheim.port.legacy.DoublePlant1710 (MAPPING.md, «Растения»); цвета
// шерсти 1.7.10 — Sheep1710
import alfheim.common.block.base.IDoublePlant
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.block.*
import alfheim.port.legacy.*
import alfheim.port.legacy.Sheep1710 as EntitySheep
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.Block
import net.minecraftforge.api.distmarker.*
import java.awt.Color

// PORT: вариант metadata (цвет) — отдельный блок (SPEC, Р-5): номер варианта — meta, создают массивом
// `Array(8) { BlockColoredDoubleGrass(colorSet, it) }`; верхняя половина (бит 8 metadata) — свойство half
// (DoublePlant1710). КТ-9 — лексикон (ILexiconable)
class BlockColoredDoubleGrass(var colorSet: Int, val meta: Int): DoublePlant1710(), IDoublePlant/*, ILexiconable*/ {
	
	val name = "irisDoubleGrass$colorSet"
	val TYPES: Int = 8
	// PORT: иконки половин → модели (alfheim.port.data.AlfheimBlockStates): низ — irisDoubleGrass, верх — irisDoubleGrassTop
//	lateinit var topIcon: IIcon
//	lateinit var bottomIcon: IIcon
	
	override val variant get() = meta
	
	init {
		setBlockNameSafe(name)
		setCreativeTab(AlfheimTab)
		setStepSound(soundTypeGrass)
	}
	
	override fun func_149851_a(world: World, x: Int, y: Int, z: Int, isRemote: Boolean) = false
	
	override fun func_149853_b(world: World, random: Random, x: Int, y: Int, z: Int) = Unit
	
	internal fun register(name: String) {
		when (colorSet) {
			1    -> GameRegistry.registerBlock(this, ItemIridescentTallGrassMod1::class.java, name)
			else -> GameRegistry.registerBlock(this, ItemIridescentTallGrassMod0::class.java, name)
		}
	}
	
	fun setBlockNameSafe(par1Str: String): Block {
		register(par1Str)
		return super.setBlockName(par1Str)
	}
	
	override fun setBlockName(par1Str: String) = this
	
	@OnlyIn(Dist.CLIENT)
	override fun getBlockColor() = 0xFFFFFF
	
	/**
	 * Returns the color this block should be rendered. Used by leaves.
	 */
	@OnlyIn(Dist.CLIENT)
	override fun getRenderColor(meta: Int): Int {
		val subtype = meta % TYPES
		val color = EntitySheep.fleeceColorTable[subtype + TYPES * colorSet]
		return Color(color[0], color[1], color[2]).rgb
	}
	
	@OnlyIn(Dist.CLIENT)
	override fun colorMultiplier(access: IBlockAccess, x: Int, y: Int, z: Int): Int {
		// PORT: верхняя половина брала цвет у нижней; номер варианта у них один — сам блок (SPEC, Р-5)
//		if ((access.getBlockMetadata(x, y, z) and 8) != 0 && y > 0)
//			return colorMultiplier(access, x, y - 1, z)
		
		val meta = this.meta
//		val meta = access.getBlockMetadata(x, y, z)
		return getRenderColor(meta)
	}
	
	/* PORT: варианты во вкладке — отдельные блоки (AlfheimTab); иконки → модели половин и предмета
	   (alfheim.port.data.AlfheimBlockStates)
	override fun getSubBlocks(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>?) {
		if (list != null && item != null)
			for (i in 0 until TYPES) {
				list.add(ItemStack(item, 1, i))
			}
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(iconRegister: IIconRegister) {
		topIcon = IconHelper.forName(iconRegister, "irisDoubleGrassTop")
		bottomIcon = IconHelper.forName(iconRegister, "irisDoubleGrass")
	}
	
	@SideOnly(Side.CLIENT)
	override fun getIcon(side: Int, meta: Int) = topIcon
	
	@SideOnly(Side.CLIENT)
	override fun func_149888_a(top: Boolean, index: Int) = if (top) topIcon else bottomIcon
	*/
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.pastoralSeeds
	
	/* PORT: поломку обеих половин ведёт DoublePlantBlock 1.20.1: вторая половина исчезает без лута, в творческом режиме
	   нижняя тоже ничего не роняет (onBlockHarvested ниже). Лут — таблица (alfheim.port.data.AlfheimBlockLoot): с
	   ножницами — две травы ириса цвета растения (onSheared), без них ничего (getItemDropped = null); шёлковое касание
	   двойное растение 1.7.10 не брало (canSilkHarvest: не обычный куб)
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
				val downMeta = world.getBlockMetadata(x, y - 1, z)
				val b0 = TYPES * colorSet + downMeta
				ret.add(ItemStack(AlfheimBlocks.irisGrass, 2, b0))
			}
		} else {
			val b0 = TYPES * colorSet + meta
			ret.add(ItemStack(AlfheimBlocks.irisGrass, 2, b0))
		}
		return ret
	}
	
	override fun getRenderType() = LibRenderIDs.idDoubleFlower
	
	override fun isShearable(item: ItemStack, world: IBlockAccess, x: Int, y: Int, z: Int) = true
	
	override fun getBottomIcon(lowerMeta: Int) = bottomIcon
	
	override fun getTopIcon(lowerMeta: Int) = topIcon
	*/
	
}
