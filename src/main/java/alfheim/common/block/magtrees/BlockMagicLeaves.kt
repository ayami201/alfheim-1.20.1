package alfheim.common.block.magtrees

// PORT: импорты 1.20.1
import alfheim.common.block.base.BlockLeavesMod
import alfheim.common.item.block.ItemBlockLeavesMod
import alfheim.port.legacy.*
import net.minecraft.util.RandomSource as Random

abstract class BlockMagicLeaves(val name: String): BlockLeavesMod() {
	
	init {
		setBlockName(name)
	}
	
	override fun register(name: String) {
		GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
	}
	
	// PORT: лут листвы 1.7.10 (getDrops BlockLeavesMod) этот шанс не спрашивал: саженец выпадает с шансом 1/20, как у
	// любой листвы автора (BUGS.md, B-026)
	override fun quantityDropped(random: Random) = if (random.nextInt(400) == 0) 1 else 0
	
	override fun decayBit() = 0x1
	
	// PORT: имена видов листвы BlockLeaves 1.7.10 (func_150125_e) нужны были его иконкам; в 1.20.1 иконки — модели
//	override fun func_150125_e() = arrayOf(name)
	
}