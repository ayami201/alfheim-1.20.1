package alfheim.common.block.magtrees.sealing

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alfheim.common.block.magtrees.BlockMagicLeaves
import alfheim.port.legacy.*
import net.minecraft.util.RandomSource as Random
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level as World

class BlockSealingLeaves: BlockMagicLeaves("sealingLeaves"), ISoundSilencer {
	
	init {
		setStepSound(soundTypeCloth)
	}
	
	// PORT: КТ-2 (партия 8в-2) — саженец; до него листва без ножниц ничего не роняет
	override fun getItemDropped(meta: Int, random: Random, fortune: Int): Item? = null
//	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = AlfheimBlocks.sealingSapling.toItem()
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.silencer
	
	// ####
	
	override fun canSilence(world: World, x: Int, y: Int, z: Int, dist: Double) = dist <= 8
	
	override fun getVolumeMultiplier(world: World, x: Int, y: Int, z: Int, dist: Double) = 0.5f
}
