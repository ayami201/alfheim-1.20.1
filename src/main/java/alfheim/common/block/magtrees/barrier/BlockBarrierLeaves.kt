package alfheim.common.block.magtrees.barrier

// PORT: импорты 1.20.1
import alfheim.common.block.magtrees.BlockMagicLeaves
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.item.Item

// PORT: КТ-9 — лексикон (ILexiconable)
class BlockBarrierLeaves: BlockMagicLeaves("barrierLeaves")/*, ILexiconable*/ {
	
	override fun isInterpolated() = true
	
	// PORT: КТ-2 (партия 8в-2) — саженец; до него листва без ножниц ничего не роняет
	override fun getItemDropped(meta: Int, random: Random, fortune: Int): Item? = null
//	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = AlfheimBlocks.barrierSapling.toItem()
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.barrierSapling
}