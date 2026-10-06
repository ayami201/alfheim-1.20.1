package alfheim.common.block.magtrees.lightning

import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.colored.BlockColoredSapling
import alfheim.common.lexicon.AlfheimLexiconData
import alfheim.common.world.gen.HeartWoodTreeGen
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class BlockLightningSapling: BlockColoredSapling("lightningSapling") {
	
	override fun getGenerator(meta: Int) = HeartWoodTreeGen(5, AlfheimBlocks.lightningWood, 0, AlfheimBlocks.lightningWood, 1, AlfheimBlocks.lightningLeaves, 0, AlfheimBlocks.lightningBerry)
	
	override fun canGrowHere(block: Block) = block.material == Material.ground || block.material == Material.grass
	
	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.lightningSapling
}
