package alfheim.common.block

import alexsocol.asjlib.*
import alfheim.client.core.helper.IconHelper
import alfheim.common.block.tile.TileRedStringObserver
import alfheim.common.item.rod.RedstoneSignal.EnumRedstoneType
import alfheim.common.item.rod.RedstoneSignalHandler
import alfheim.common.lexicon.*
import cpw.mods.fml.relauncher.*
import net.minecraft.block.*
import net.minecraft.client.renderer.texture.*
import net.minecraft.entity.player.*
import net.minecraft.item.*
import net.minecraft.util.*
import net.minecraft.world.*
import vazkii.botania.api.lexicon.*
import vazkii.botania.common.block.string.*

class BlockRedStringObserver: BlockRedString("RedStringObserver"), ILexiconable {
	
	lateinit var frontUnlitIcon: IIcon
	
	override fun onNeighborBlockChange(world: World, x: Int, y: Int, z: Int, block: Block?) {
		val tile = world.getTileEntity(x, y, z) as? TileRedStringObserver ?: return
		val (tx, ty, tz) = tile.binding ?: return
		
		RedstoneSignalHandler.get().apply {	
			if (getPower(world, tx, ty, tz).second == EnumRedstoneType.NONE)
				addSignal(world, tx, ty, tz, 2, 15)
		}
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerSideIcon(register: IIconRegister) = IconHelper.forBlock(register, this)
	
	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.redStringObserver
	
	override fun createNewTileEntity(world: World?, meta: Int) = TileRedStringObserver()
}
