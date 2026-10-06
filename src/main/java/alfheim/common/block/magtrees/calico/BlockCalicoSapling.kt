package alfheim.common.block.magtrees.calico

// PORT: импорты 1.20.1 (блок 1.7.10 — alfheim.port.legacy, MAPPING.md)
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.colored.BlockColoredSapling
import alfheim.common.world.gen.HeartWoodTreeGen
import alfheim.port.legacy.*
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.Block

class BlockCalicoSapling: BlockColoredSapling("calicoSapling"), IExplosionDampener {
	
	override fun getGenerator(meta: Int) = HeartWoodTreeGen(5, AlfheimBlocks.calicoWood, 0, AlfheimBlocks.calicoWood, 0, AlfheimBlocks.calicoLeaves, 0, AlfheimBlocks.calicoBerry)
	
	override fun canGrowHere(block: Block) =
		block.material == Material.ground || block.material == Material.grass
	
	// PORT: КТ-9 — лексикон
//	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.calicoSapling
	
	// ####
	
	override fun onBlockExploded(world: World, x: Int, y: Int, z: Int, explosion: Explosion) = Unit //NO-OP
}