package alfheim.common.block.colored

// PORT: импорты 1.20.1; цвета шерсти 1.7.10 — Sheep1710
import alexsocol.asjlib.toItem
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.base.BlockLeavesMod
import alfheim.common.item.block.ItemIridescentLeavesMod
import alfheim.port.legacy.*
import alfheim.port.legacy.Sheep1710 as EntitySheep
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraftforge.api.distmarker.*
import java.awt.Color

// PORT: вариант metadata (цвет) — отдельный блок (SPEC, Р-5): номер варианта — meta, создают массивом
// `Array(8) { BlockColoredLeaves(colorSet, it) }`; бит опадания — свойство persistent (Leaves1710). КТ-9 — лексикон
class BlockColoredLeaves(val colorSet: Int, val meta: Int): BlockLeavesMod() {
	
	val TYPES: Int = 8
	
	override val variant get() = meta
	
	init {
		setBlockName("irisLeaves$colorSet")
	}
	
	override fun quantityDropped(random: Random) = if (random.nextInt(20) == 0) 1 else 0
	
	override fun register(name: String) {
		GameRegistry.registerBlock(this, ItemIridescentLeavesMod::class.java, name)
	}
	
	@OnlyIn(Dist.CLIENT)
	override fun getRenderColor(meta: Int): Int {
		val shiftedMeta = meta % TYPES + colorSet * TYPES
		if (shiftedMeta >= EntitySheep.fleeceColorTable.size)
			return 0xFFFFFF
		
		val color = EntitySheep.fleeceColorTable[shiftedMeta]
		return Color(color[0], color[1], color[2]).rgb
	}
	
	@OnlyIn(Dist.CLIENT)
	override fun colorMultiplier(world: IBlockAccess, x: Int, y: Int, z: Int): Int {
		// PORT: metadata — номер варианта блока (SPEC, Р-5)
		val meta = this.meta
//		val meta = world.getBlockMetadata(x, y, z)
		return getRenderColor(meta)
	}
	
	// PORT: варианты во вкладке — отдельные блоки (AlfheimTab)
	/*
	override fun getSubBlocks(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>?) {
		if (list != null && item != null)
			for (i in 0 until TYPES) {
				list.add(ItemStack(item, 1, i))
			}
	}
	*/
	
	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = AlfheimBlocks.irisSapling.toItem()
	
	// PORT: шёлковое касание — сама листва (таблица листвы, alfheim.port.data.AlfheimBlockLoot)
//	override fun createStackedBlock(meta: Int) = ItemStack(this, 1, meta % 8)
	
	// PORT: имена видов листвы BlockLeaves 1.7.10 (func_150125_e) нужны были его иконкам; в 1.20.1 иконки — модели
//	override fun func_150125_e() = LEAVES.sliceArray(colorSet * 8 until (colorSet + 1) * 8)
	
	override fun decayBit(): Int = 0x8
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.irisSapling
}
