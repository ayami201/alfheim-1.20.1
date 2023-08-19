package alfheim.common.block

import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.TileRedstoneRelay
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import vazkii.botania.api.lexicon.*
import vazkii.botania.api.wand.IWandable

class BlockRedstoneRelay: BlockContainerMod(Material.iron), ILexiconable, IWandable {
	
	init {
		setBlockName("RedstoneRelay")
		setHardness(3f)
	}
	
	override fun createNewTileEntity(world: World?, meta: Int) = TileRedstoneRelay()
	
	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.redstoneRelay
	
	override fun onUsedByWand(player: EntityPlayer?, stack: ItemStack?, world: World?, x: Int, y: Int, z: Int, side: Int) = false
}
