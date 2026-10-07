package alfheim.common.block

// PORT: импорты 1.20.1; BlockLeaves 1.7.10 — alfheim.port.legacy.Leaves1710 (MAPPING.md, «Растения»)
import alfheim.common.block.base.BlockLeavesMod
import alfheim.common.item.block.ItemUniqueSubtypedBlockMod
import alfheim.port.legacy.*
import net.minecraft.client.renderer.BiomeColors
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.FoliageColor as ColorizerFoliage
import net.minecraftforge.api.distmarker.*

// PORT: КТ-9 — лексикон (getEntry); иконка листвы ванилы — модель дубовой листвы 1.20.1 (alfheim.port.data)
class BlockSadOakLeaves: BlockLeavesMod() {
	
	val SUBTYPES = 1
	
	init {
		setBlockName("leaves")
	}
	
	override fun register(name: String) {
		GameRegistry.registerBlock(this, ItemUniqueSubtypedBlockMod::class.java, name, SUBTYPES)
	}
	
	// PORT: ColorizerFoliage 1.7.10 — FoliageColor 1.20.1: getFoliageColor → get, getFoliageColorBasic → getDefaultColor
	@OnlyIn(Dist.CLIENT)
	override fun getBlockColor() = ColorizerFoliage.get(0.5, 1.0)
	
	// PORT: metadata — номер варианта блока (SPEC, Р-5)
	@OnlyIn(Dist.CLIENT)
	override fun getRenderColor(meta: Int) = if (meta and 7 == 0) ColorizerFoliage.getDefaultColor() else 0
	
	// PORT: среднее цвета листвы биомов вокруг (у автора — 3 × 3, как у листвы ванилы 1.7.10) — смешивание цвета биомов
	// 1.20.1, как у дубовой листвы ванилы рядом: по настройке «Смешивание биомов» (при 3 × 3 — то же самое); metadata —
	// номер варианта блока (SPEC, Р-5)
	@OnlyIn(Dist.CLIENT)
	override fun colorMultiplier(world: IBlockAccess, x: Int, y: Int, z: Int): Int {
		if ((variant ?: 0) and 7 != 0) return 0
//		if (world.getBlockMetadata(x, y, z) and 7 != 0) return 0
		
		return BiomeColors.getAverageFoliageColor(world as BlockAndTintGetter, BlockPos(x, y, z))
		/*
		var r = 0
		var g = 0
		var b = 0
		
		for (k in -1..1) {
			for (j in -1..1) {
				val c = world.getBiomeGenForCoords(x + j, z + k).getBiomeFoliageColor(x + j, y, z + k)
				r += c and 0xFF0000 shr 16
				g += c and 0x00FF00 shr 8
				b += c and 0x0000FF
			}
		}
		
		return r / 9 and 0xFF shl 16 or (g / 9 and 0xFF shl 8) or (b / 9 and 0xFF)
		*/
	}
	
	/* PORT: лут — таблица листвы (alfheim.port.data.AlfheimBlockLoot): яблоко с шансом 1 / chance BlockLeavesMod.getDrops
	override fun func_150124_c(world: World, x: Int, y: Int, z: Int, meta: Int, chance: Int) {
		if (meta and 7 == 0 && world.rand.nextInt(chance) == 0) dropBlockAsItem(world, x, y, z, ItemStack(Items.apple, 1, 0))
	}
	*/
	
	/* PORT: иконка дубовой листвы ванилы → модель с текстурой дубовой листвы 1.20.1 (alfheim.port.data.AlfheimBlockStates)
	override fun registerBlockIcons(reg: IIconRegister) = Unit
	
	@SideOnly(Side.CLIENT)
	override fun getIcon(side: Int, meta: Int) = if (meta and 7 == 0) Blocks.leaves.getIcon(side, 0) else null
	*/
	
	// PORT: выбор колёсиком в 1.7.10 шёл через getPickBlock BlockLeavesMod (сама листва), getItem он не вызывал; в 1.20.1 —
	// getCloneItemStack: сама листва (BUGS.md)
//	override fun getItem(world: World, x: Int, y: Int, z: Int) = if (world.getBlockMetadata(x, y, z) and 7 == 0) Blocks.leaves.toItem() else toItem()
	
	// drop chance
	// PORT: func_150123_b (шанс саженца 1/n) BlockLeaves 1.7.10 — без override: в Leaves1710 его нет
	fun func_150123_b(meta: Int) = 20
	override fun quantityDropped(random: Random) = if (random.nextInt(func_150123_b(0)) == 0) 1 else 0
	override fun decayBit() = 0x8
	override fun getDecayRange(meta: Int) = if (meta and 7 == 0) 8 else 4
	// PORT: имена видов листвы BlockLeaves 1.7.10 нужны были его иконкам; в 1.20.1 иконки — модели. КТ-9 — лексикон
//	override fun func_150125_e() = arrayOf("oak")
//	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = null
}
