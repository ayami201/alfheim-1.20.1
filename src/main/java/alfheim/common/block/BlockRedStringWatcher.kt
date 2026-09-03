package alfheim.common.block

import alexsocol.asjlib.ASJUtilities
import alfheim.client.core.helper.*
import alfheim.common.block.tile.*
import alfheim.common.lexicon.*
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.texture.*
import net.minecraft.entity.player.*
import net.minecraft.item.*
import net.minecraft.util.Facing
import net.minecraft.world.*
import vazkii.botania.api.wand.IWandable
import vazkii.botania.common.block.string.*

class BlockRedStringWatcher: BlockRedString("RedStringWatcher"), IWandable {
	
	override fun isProvidingWeakPower(world: IBlockAccess, x: Int, y: Int, z: Int, side: Int) = 
		isProvidingStrongPower(world, x, y, z, side)
	
	override fun isProvidingStrongPower(world: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Int {
		val meta = world.getBlockMetadata(x, y, z)
		val active = meta and 8 != 0
		val rotation = meta and 7
		
		if (Facing.oppositeSide[side] == rotation) return 0
		
		val tile = world.getTileEntity(x, y, z) as? TileRedStringWatcher
		if (tile?.rsFromAss == true) return if (active && rotation == side) 15 else 0
		
		return if (active) 15 else 0
	}
	
	override fun isNormalCube() = true
	override fun isNormalCube(world: IBlockAccess?, x: Int, y: Int, z: Int) = true
	override fun canProvidePower() = true
	
	@SideOnly(Side.CLIENT)
	override fun registerSideIcon(register: IIconRegister) = IconHelper.forBlock(register, this)
	
	override fun getIcon(side: Int, meta: Int) = super.getIcon(side, meta and 7)
	
	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.redStringWatcher
	
	override fun createNewTileEntity(world: World?, meta: Int) = TileRedStringWatcher()
	
	override fun onUsedByWand(player: EntityPlayer, stack: ItemStack?, world: World, x: Int, y: Int, z: Int, side: Int): Boolean {
		val tile = world.getTileEntity(x, y, z) as? TileRedStringWatcher ?: return false
		tile.rsFromAss = !tile.rsFromAss
		ASJUtilities.say(player, "alfheimmisc.")
		return true
	}
}
