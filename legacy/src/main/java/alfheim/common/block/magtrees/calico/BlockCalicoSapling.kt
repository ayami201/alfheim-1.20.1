package alfheim.common.block.magtrees.calico

import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.colored.BlockColoredSapling
import alfheim.common.lexicon.AlfheimLexiconData
import alfheim.common.world.gen.HeartWoodTreeGen
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.*

class BlockCalicoSapling: BlockColoredSapling("calicoSapling"), IExplosionDampener {
	
	override fun getGenerator(meta: Int) = HeartWoodTreeGen(5, AlfheimBlocks.calicoWood, 0, AlfheimBlocks.calicoWood, 0, AlfheimBlocks.calicoLeaves, 0, AlfheimBlocks.calicoBerry)
	
	override fun canGrowHere(block: Block) =
		block.material == Material.ground || block.material == Material.grass
	
	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.calicoSapling
	
	// ####
	
	override fun onBlockExploded(world: World, x: Int, y: Int, z: Int, explosion: Explosion) = Unit //NO-OP
}