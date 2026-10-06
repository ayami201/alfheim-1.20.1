package alfheim.common.block.magtrees.barrier

// PORT: импорты 1.20.1
import alexsocol.asjlib.toItem
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.magtrees.BlockMagicLeaves
import net.minecraft.util.RandomSource as Random

// PORT: КТ-9 — лексикон (ILexiconable)
class BlockBarrierLeaves: BlockMagicLeaves("barrierLeaves")/*, ILexiconable*/ {
	
	override fun isInterpolated() = true
	
	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = AlfheimBlocks.barrierSapling.toItem()
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.barrierSapling
}