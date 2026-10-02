package alfheim.common.block.magtrees.sealing

import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.colored.BlockColoredSapling
import alfheim.common.lexicon.AlfheimLexiconData
import alfheim.common.world.gen.HeartWoodTreeGen
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class BlockSealingSapling: BlockColoredSapling("sealingSapling"), ISoundSilencer {
	
	init {
		setStepSound(soundTypeCloth)
	}
	
	override fun getGenerator(meta: Int) = HeartWoodTreeGen(5, AlfheimBlocks.sealingWood, 0, AlfheimBlocks.sealingWood, 0, AlfheimBlocks.sealingLeaves, 0, AlfheimBlocks.sealingBerry)
	
	override fun canSilence(world: World, x: Int, y: Int, z: Int, dist: Double) = dist <= 8
	
	override fun getVolumeMultiplier(world: World, x: Int, y: Int, z: Int, dist: Double) = 0.5f
	
	override fun canGrowHere(block: Block) = block.material == Material.ground || block.material == Material.grass
	
	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.silencer
}
