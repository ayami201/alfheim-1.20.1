package alfheim.common.block

import alfheim.common.block.base.*
import alfheim.common.core.handler.WorkInProgressItemsHandler.WIP
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.block.material.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import vazkii.botania.api.lexicon.ILexiconable

class BlockRedstoneAttractor: BlockMod(Material.rock), ILexiconable {
	
	init {
		setBlockName("RedstoneAttractor")
		setHardness(2f)
		setResistance(10f)
		setStepSound(soundTypeStone)
		
		WIP()
	}
	
	override fun canProvidePower() = true
	override fun isNormalCube() = true
	override fun isNormalCube(world: IBlockAccess?, x: Int, y: Int, z: Int) = true
	override fun canConnectRedstone(world: IBlockAccess?, x: Int, y: Int, z: Int, side: Int) = true
	
	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.redstoneAttractor
}
