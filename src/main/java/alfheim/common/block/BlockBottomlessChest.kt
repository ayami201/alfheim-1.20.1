package alfheim.common.block

import alexsocol.asjlib.get
import alfheim.client.core.helper.IconHelper
import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.TileBottomlessChest
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.World
import vazkii.botania.api.lexicon.ILexiconable
import vazkii.botania.common.block.tile.TileSimpleInventory
import java.util.*

class BlockBottomlessChest: BlockContainerMod(Material.wood), ILexiconable {
	
	lateinit var iconBottom: IIcon
	
	var random: Random
	
	init {
		setBlockName("BottomlessChest")
		setHardness(2f)
		setStepSound(soundTypeWood)
		
		random = Random()
	}
	
	override fun breakBlock(world: World, x: Int, y: Int, z: Int, block: Block, meta: Int) {
		val inv = world.getTileEntity(x, y, z) as? TileSimpleInventory
		
		if (inv != null) {
			for (i in 0 until inv.sizeInventory) {
				val stack = inv[i] ?: continue
				EntityItem(world, x + 0.5, y + 0.5, z + 0.5, stack.copy())
			}
			
			world.func_147453_f(x, y, z, block)
		}
		
		super.breakBlock(world, x, y, z, block, meta)
	}
	
	override fun registerBlockIcons(reg: IIconRegister) {
		super.registerBlockIcons(reg)
		iconBottom = IconHelper.forBlock(reg, this, 1)
	}
	
	override fun getIcon(side: Int, meta: Int) = if (side == 0) iconBottom else blockIcon!!
	
	override fun createNewTileEntity(world: World, meta: Int) = TileBottomlessChest()
	
	override fun getEntry(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, lexicon: ItemStack) = AlfheimLexiconData.openChest
}


