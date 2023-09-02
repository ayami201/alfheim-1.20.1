package alfheim.common.block

import alexsocol.asjlib.*
import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.TileDoubleCamo
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.world.*
import vazkii.botania.api.wand.IWandable
import kotlin.math.max

abstract class BlockDoubleCamo(material: Material = Material.wood): BlockContainerMod(material), IWandable {
	
	init {
		setStepSound(soundTypeWood)
	}
	
	override fun getBlockHardness(world: World, x: Int, y: Int, z: Int): Float {
		val tile = world.getTileEntity(x, y, z) as? TileDoubleCamo ?: return 1f
		return max(tile.blockTop.getBlockHardness(world, x, y, z), tile.blockBottom.getBlockHardness(world, x, y, z))
	}
	
	override fun onUsedByWand(player: EntityPlayer, stack: ItemStack?, world: World, x: Int, y: Int, z: Int, side: Int): Boolean {
		val tile = world.getTileEntity(x, y, z) as? TileDoubleCamo ?: return false
		
		if (player.isSneaking) {
			tile.locked = !tile.locked
		} else {
			if (tile.locked) return false
			
			if (side == topSide(world, x, y, z)) tile.blockTopMeta = (tile.blockTopMeta + 1) % 16
			else tile.blockBottomMeta = (tile.blockBottomMeta + 1) % 16
		}
		
		ASJUtilities.dispatchTEToNearbyPlayers(tile)
		
		return true
	}
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		val tile = world.getTileEntity(x, y, z) as? TileDoubleCamo ?: return false
		
		val stack = player.heldItem ?: return false
		if (tile.locked) return false
		
		val block = stack.block
		if (block === Blocks.air) return false
		if (block is BlockDoubleCamo) return false
		
		val meta = stack.meta
		
		if (side == topSide(world, x, y, z)) {
			tile.blockTop = block
			tile.blockTopMeta = meta
		} else {
			tile.blockBottom = block
			tile.blockBottomMeta = meta
		}
		
		ASJUtilities.dispatchTEToNearbyPlayers(tile)
		
		return true
	}
	
	abstract fun topSide(world: IBlockAccess, x: Int, y: Int, z: Int): Int
	
	abstract override fun getRenderType(): Int
	
	override fun registerBlockIcons(reg: IIconRegister) = Unit
	override fun isOpaqueCube() = false
	override fun renderAsNormalBlock() = false
	
	override fun getIcon(side: Int, meta: Int) = iconOverride.getIcon(side, metaOverride)
	override fun getIcon(world: IBlockAccess?, x: Int, y: Int, z: Int, side: Int) = getIcon(side, metaOverride) ?: iconOverride.getIcon(world, x, y, z, side)
	override fun colorMultiplier(world: IBlockAccess?, x: Int, y: Int, z: Int) = iconOverride.getRenderColor(metaOverride)
	override fun getRenderColor(meta: Int) = iconOverride.getRenderColor(meta)
	
	companion object {
		var iconOverride = Blocks.log!!
		var metaOverride = 0
	}
}