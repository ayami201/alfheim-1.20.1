package alfheim.common.block.magtrees.lightning

// PORT: импорты 1.20.1
import alexsocol.asjlib.toItem
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.magtrees.BlockMagicLeaves
import net.minecraft.util.RandomSource as Random

class BlockLightningLeaves: BlockMagicLeaves("lightningLeaves") {
	
	override fun isInterpolated() = true
	
	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = AlfheimBlocks.lightningSapling.toItem()
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.lightningSapling
}
