package alfheim.common.block.magtrees.sealing

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.colored.BlockColoredSapling
import alfheim.common.world.gen.HeartWoodTreeGen
import alfheim.port.legacy.*
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.Block

class BlockSealingSapling: BlockColoredSapling("sealingSapling"), ISoundSilencer {
	
	init {
		setStepSound(soundTypeCloth)
	}
	
	override fun getGenerator(meta: Int) = HeartWoodTreeGen(5, AlfheimBlocks.sealingWood, 0, AlfheimBlocks.sealingWood, 0, AlfheimBlocks.sealingLeaves, 0, AlfheimBlocks.sealingBerry)
	
	override fun canSilence(world: World, x: Int, y: Int, z: Int, dist: Double) = dist <= 8
	
	override fun getVolumeMultiplier(world: World, x: Int, y: Int, z: Int, dist: Double) = 0.5f
	
	override fun canGrowHere(block: Block) = block.material == Material.ground || block.material == Material.grass
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.silencer
}
