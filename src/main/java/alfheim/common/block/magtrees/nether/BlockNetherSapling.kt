package alfheim.common.block.magtrees.nether

import alexsocol.asjlib.toItem
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.colored.BlockColoredSapling
import alfheim.common.lexicon.AlfheimLexiconData
import alfheim.common.world.gen.HeartWoodTreeGen
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.*
import net.minecraftforge.common.util.ForgeDirection

class BlockNetherSapling: BlockColoredSapling("netherSapling") {
	
	init {
		setLightLevel(0.5f)
	}
	
	override fun getGenerator(meta: Int) = HeartWoodTreeGen(5, AlfheimBlocks.netherWood, 0, AlfheimBlocks.netherWood, 1, AlfheimBlocks.netherLeaves, 0, AlfheimBlocks.netherBerry)
	
	override fun canGrowHere(block: Block) =
		block.material == Material.ground || block.material == Material.grass
	
	override fun getEntry(p0: World?, p1: Int, p2: Int, p3: Int, p4: EntityPlayer?, p5: ItemStack?) = AlfheimLexiconData.netherSapling
	
	override fun isFlammable(world: IBlockAccess?, x: Int, y: Int, z: Int, face: ForgeDirection?) = false
	
	override fun getFireSpreadSpeed(world: IBlockAccess?, x: Int, y: Int, z: Int, face: ForgeDirection?) = 0
	
	override fun getBurnTime(fuel: ItemStack) = if (fuel.item === this.toItem()) 800 else 0
}
