package alfheim.common.block.magtrees.calico

// PORT: импорты 1.20.1
import alexsocol.asjlib.toItem
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.magtrees.BlockMagicLeaves
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level as World

// PORT: КТ-9 — лексикон (ILexiconable)
class BlockCalicoLeaves: BlockMagicLeaves("calicoLeaves"), IExplosionDampener/*, ILexiconable*/ {
	
	override fun isInterpolated() = true
	
	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = AlfheimBlocks.calicoSapling.toItem()
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.calicoSapling
	
	// ####
	
	override fun onBlockExploded(world: World, x: Int, y: Int, z: Int, explosion: Explosion) = Unit //NO-OP
}