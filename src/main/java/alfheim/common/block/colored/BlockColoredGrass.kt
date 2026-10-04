package alfheim.common.block.colored

// PORT: импорты 1.20.1; BlockTallGrass 1.7.10 — alfheim.port.legacy.TallGrass1710 (MAPPING.md, «Растения»); цвета шерсти
// 1.7.10 — Sheep1710
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.block.ItemIridescentGrassMod
import alfheim.port.legacy.*
import alfheim.port.legacy.Sheep1710 as EntitySheep
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.*
import net.minecraftforge.api.distmarker.*
import java.awt.Color

// PORT: вариант metadata (цвет) — отдельный блок (SPEC, Р-5): номер варианта — meta, создают массивом
// `Array(16) { BlockColoredGrass(it) }`. КТ-9 — лексикон (ILexiconable)
class BlockColoredGrass(val meta: Int): TallGrass1710()/*, ILexiconable*/ {
	
	val TYPES: Int = 16
	
	override val variant get() = meta
	
	init {
		setBlockName("irisGrass")
		setCreativeTab(AlfheimTab)
		setStepSound(soundTypeGrass)
	}
	
	override fun func_149851_a(world: World, x: Int, y: Int, z: Int, remote: Boolean) = true
	
	override fun func_149853_b(world: World, random: Random, x: Int, y: Int, z: Int) {
		// PORT: metadata — номер варианта блока (SPEC, Р-5)
		val l = meta
//		val l = world.getBlockMetadata(x, y, z)
		val b0 = l % 8
		
		// PORT: двойная трава — массивы вариантов (SPEC, Р-5); обе половины (верхняя — metadata 8) ставит
		// DoublePlantBlock.placeAt 1.20.1, флаги те же
		if (AlfheimBlocks.irisTallGrass0[b0].canPlaceBlockAt(world, x, y, z)) {
			if (l < 8) {
				DoublePlantBlock.placeAt(world, AlfheimBlocks.irisTallGrass0[b0].defaultBlockState(), BlockPos(x, y, z), 2)
//				world.setBlock(x, y, z, AlfheimBlocks.irisTallGrass0, b0, 2)
//				world.setBlock(x, y + 1, z, AlfheimBlocks.irisTallGrass0, 8, 2)
			} else {
				DoublePlantBlock.placeAt(world, AlfheimBlocks.irisTallGrass1[b0].defaultBlockState(), BlockPos(x, y, z), 2)
//				world.setBlock(x, y, z, AlfheimBlocks.irisTallGrass1, b0, 2)
//				world.setBlock(x, y + 1, z, AlfheimBlocks.irisTallGrass1, 8, 2)
			}
		}
	}
	
	internal fun register(name: String) {
		GameRegistry.registerBlock(this, ItemIridescentGrassMod::class.java, name)
	}
	
	override fun setBlockName(par1Str: String): Block {
		register(par1Str)
		return super.setBlockName(par1Str)
	}
	
	@OnlyIn(Dist.CLIENT)
	override fun getBlockColor() = 0xFFFFFF
	
	// PORT: лут с ножницами — таблица травы (alfheim.port.data.AlfheimBlockLoot): сама трава, как здесь
	/*
	override fun onSheared(item: ItemStack, world: IBlockAccess, x: Int, y: Int, z: Int, fortune: Int): ArrayList<ItemStack> {
		val ret = ArrayList<ItemStack>()
		ret.add(ItemStack(this, 1, world.getBlockMetadata(x, y, z)))
		return ret
	}
	*/
	
	/**
	 * Returns the color this block should be rendered. Used by leaves.
	 */
	@OnlyIn(Dist.CLIENT)
	override fun getRenderColor(meta: Int): Int {
		if (meta >= EntitySheep.fleeceColorTable.size)
			return 0xFFFFFF
		
		val color = EntitySheep.fleeceColorTable[meta]
		return Color(color[0], color[1], color[2]).rgb
	}
	
	@OnlyIn(Dist.CLIENT)
	override fun colorMultiplier(access: IBlockAccess?, x: Int, y: Int, z: Int): Int {
		// PORT: metadata — номер варианта блока (SPEC, Р-5)
		val meta = this.meta
//		val meta = access!!.getBlockMetadata(x, y, z)
		return getRenderColor(meta)
	}
	
	/* PORT: варианты во вкладке — отдельные блоки (AlfheimTab); иконка → модель (alfheim.port.data.AlfheimBlockStates):
	   окрашенный крест с текстурой блока
	override fun getSubBlocks(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>?) {
		if (list != null && item != null)
			for (i in 0 until TYPES) {
				list.add(ItemStack(item, 1, i))
			}
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerBlockIcons(iconRegister: IIconRegister) {
		blockIcon = IconHelper.forBlock(iconRegister, this)
	}
	
	@SideOnly(Side.CLIENT)
	override fun getIcon(side: Int, meta: Int) = blockIcon!!
	*/
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.pastoralSeeds
}
