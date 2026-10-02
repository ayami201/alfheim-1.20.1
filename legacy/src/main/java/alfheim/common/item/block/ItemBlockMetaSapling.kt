package alfheim.common.item.block

import alexsocol.asjlib.meta
import net.minecraft.block.Block
import net.minecraft.item.*

class ItemBlockMetaSapling(block: Block): ItemBlockWithMetadata(block, block) {
	
	override fun getUnlocalizedName(stack: ItemStack) = "${super.getUnlocalizedName(stack)}${getMetadata(stack.meta)}"
	
	override fun getMetadata(meta: Int) = meta and 7
}