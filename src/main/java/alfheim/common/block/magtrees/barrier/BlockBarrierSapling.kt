package alfheim.common.block.magtrees.barrier

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.colored.BlockColoredSapling
import alfheim.common.world.gen.HeartWoodTreeGen
import alfheim.port.legacy.*
import net.minecraft.world.level.block.Block

class BlockBarrierSapling: BlockColoredSapling("barrierSapling") {
	
	// PORT: варианты metadata — блоки массива (SPEC, Р-5): бревно — вариант 0, сердцевина — вариант 1, metadata у обоих — 0
	override fun getGenerator(meta: Int) = HeartWoodTreeGen(5, AlfheimBlocks.barrierWood[0], 0, AlfheimBlocks.barrierWood[1], 0, AlfheimBlocks.barrierLeaves, 0, AlfheimBlocks.barrierBerry)
//	override fun getGenerator(meta: Int) = HeartWoodTreeGen(5, AlfheimBlocks.barrierWood, 0, AlfheimBlocks.barrierWood, 1, AlfheimBlocks.barrierLeaves, 0, AlfheimBlocks.barrierBerry)
	
	override fun canGrowHere(block: Block) =
		block.material == Material.ground || block.material == Material.grass
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.barrierSapling
}