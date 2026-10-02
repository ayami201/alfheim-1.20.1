package alfheim.common.block.magtrees.circuit

import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.colored.BlockColoredSapling
import alfheim.common.lexicon.AlfheimLexiconData
import alfheim.common.world.gen.HeartWoodTreeGen
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.*
import java.util.*

class BlockCircuitSapling: BlockColoredSapling("circuitSapling"), ICircuitBlock {
	
	override fun getGenerator(meta: Int) = HeartWoodTreeGen(5, AlfheimBlocks.circuitWood, 0, AlfheimBlocks.circuitWood, 0, AlfheimBlocks.circuitLeaves, 0, AlfheimBlocks.circuitBerry)
	
	override fun canGrowHere(block: Block) =
		block.material == Material.ground || block.material == Material.grass
	
	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.circuitSapling
	
	// ####
	
	override fun updateTick(world: World, x: Int, y: Int, z: Int, random: Random) {
		super.updateTick(world, x, y, z, random)
		world.notifyBlocksOfNeighborChange(x, y, z, this)
	}
	
	override fun getLightValue(world: IBlockAccess?, x: Int, y: Int, z: Int) = 8
	
	override fun canProvidePower() = true
	
	override fun tickRate(world: World) = 1
	
	override fun isProvidingWeakPower(blockAccess: IBlockAccess, x: Int, y: Int, z: Int, meta: Int) = ICircuitBlock.getPower(blockAccess, x, y, z)
}