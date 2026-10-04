package alfheim.common.block.colored.rainbow

// PORT: импорты 1.20.1
import alexsocol.asjlib.toItem
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.base.BlockLeavesMod
import alfheim.common.item.block.ItemIridescentBlockMod
import alfheim.port.legacy.*
import net.minecraft.util.RandomSource as Random

// PORT: бит опадания — свойство persistent (Leaves1710); переливающаяся текстура — .mcmeta. КТ-9 — лексикон
class BlockRainbowLeaves: BlockLeavesMod() {
	
	init {
		setBlockName("rainbowLeaves")
	}
	
	override fun register(name: String) {
		GameRegistry.registerBlock(this, ItemIridescentBlockMod::class.java, name)
	}
	
	override fun quantityDropped(random: Random) = if (random.nextInt(20) == 0) 1 else 0
	
	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = AlfheimBlocks.irisSapling.toItem()
	
	// PORT: имена видов листвы BlockLeaves 1.7.10 (func_150125_e) нужны были его иконкам; в 1.20.1 иконки — модели
//	override fun func_150125_e() = arrayOf("rainbowLeaves")
	
	override fun isInterpolated() = true
	
	override fun decayBit() = 0x1
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.irisSapling
}
