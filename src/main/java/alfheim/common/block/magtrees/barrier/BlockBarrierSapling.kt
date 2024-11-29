package alfheim.common.block.magtrees.barrier

import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.colored.BlockColoredSapling
import alfheim.common.lexicon.AlfheimLexiconData
import alfheim.common.world.gen.HeartWoodTreeGen
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class BlockBarrierSapling: BlockColoredSapling("barrierSapling") {
	
	override fun getGenerator(meta: Int) = HeartWoodTreeGen(5, AlfheimBlocks.barrierWood, 0, AlfheimBlocks.barrierWood, 1, AlfheimBlocks.barrierLeaves, 0, AlfheimBlocks.barrierBerry)
	
	override fun canGrowHere(block: Block) =
		block.material == Material.ground || block.material == Material.grass
	
	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.barrierSapling
}