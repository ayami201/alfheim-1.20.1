package alfheim.common.block.magtrees

import alfheim.common.block.base.BlockLeavesMod
import alfheim.common.item.block.ItemBlockLeavesMod
import cpw.mods.fml.common.registry.GameRegistry
import java.util.*

abstract class BlockMagicLeaves(val name: String): BlockLeavesMod() {
	
	init {
		setBlockName(name)
	}
	
	override fun register(name: String) {
		GameRegistry.registerBlock(this, ItemBlockLeavesMod::class.java, name)
	}
	
	override fun quantityDropped(random: Random) = if (random.nextInt(400) == 0) 1 else 0
	
	override fun decayBit() = 0x1
	
	override fun func_150125_e() = arrayOf(name)
	
}