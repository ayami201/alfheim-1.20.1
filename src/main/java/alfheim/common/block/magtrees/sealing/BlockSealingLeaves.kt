package alfheim.common.block.magtrees.sealing

import alexsocol.asjlib.toItem
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.magtrees.BlockMagicLeaves
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import java.util.*

class BlockSealingLeaves: BlockMagicLeaves("sealingLeaves"), ISoundSilencer {
	
	init {
		setStepSound(Block.soundTypeCloth)
	}
	
	override fun getItemDropped(meta: Int, random: Random, fortune: Int) = AlfheimBlocks.sealingSapling.toItem()
	
	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.silencer
	
	// ####
	
	override fun canSilence(world: World, x: Int, y: Int, z: Int, dist: Double) = dist <= 8
	
	override fun getVolumeMultiplier(world: World, x: Int, y: Int, z: Int, dist: Double) = 0.5f
}
