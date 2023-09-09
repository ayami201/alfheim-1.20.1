package alfheim.common.block

import alexsocol.asjlib.ASJUtilities
import alexsocol.asjlib.extendables.MaterialPublic
import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.tile.TileCurtainPlacer
import net.minecraft.block.material.MapColor
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.*
import net.minecraftforge.common.util.ForgeDirection
import vazkii.botania.common.item.ModItems

class BlockCurtainPlacer: BlockDoubleCamo(MaterialPublic(MapColor.clothColor, opaque = false)) {
	
	init {
		setBlockName("CurtainPlacer")
		setLightOpacity(255)
		setStepSound(soundTypeCloth)
	}
	
	override fun onBlockClicked(world: World, x: Int, y: Int, z: Int, player: EntityPlayer?) {
		if (ASJUtilities.isClient || player?.heldItem?.item !== ModItems.twigWand) return
		
		(world.getTileEntity(x, y, z) as? TileCurtainPlacer)?.let {
			if (it.locked) return
			
			it.collidable = !it.collidable
			ASJUtilities.say(player, "alfheimmisc.curtainCollide.${it.collidable}")
		}
	}
	
	override fun getDrops(world: World, x: Int, y: Int, z: Int, metadata: Int, fortune: Int) =
		if (world.getBlockMetadata(x, y, z) != 0) ArrayList() else super.getDrops(world, x, y, z, metadata, fortune)
	
	override fun getCollisionBoundingBoxFromPool(world: World, x: Int, y: Int, z: Int) =
		if (world.getBlockMetadata(x, y, z) == 1) null else super.getCollisionBoundingBoxFromPool(world, x, y, z)
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float) =
		if (world.getBlockMetadata(x, y, z) != 0) false else super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ)
	
	override fun shouldSideBeRendered(world: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Boolean {
		val default = super.shouldSideBeRendered(world, x, y, z, side)
		if (y <= 0 || y >= world.height) return default
		
		val sideThis = ForgeDirection.getOrientation(side)
		val sideThat = sideThis.opposite
		
		val tileThis = world.getTileEntity(x + sideThat.offsetX, y + sideThat.offsetY, z + sideThat.offsetZ) as? TileCurtainPlacer ?: return default
		val tileThat = world.getTileEntity(x, y, z) as? TileCurtainPlacer ?: return default
		
		val block_src = if (sideThis.ordinal == topSide(world, x + sideThat.offsetX, y + sideThat.offsetY, z + sideThat.offsetZ)) tileThis.blockTop else tileThis.blockBottom
		val block_adj = if (sideThat.ordinal == topSide(world, x, y, z)) tileThat.blockTop else tileThat.blockBottom
		
		return !(block_src.isOpaqueCube == block_adj.isOpaqueCube && block_src.renderBlockPass == block_adj.renderBlockPass)
	}
	
	override fun topSide(world: IBlockAccess, x: Int, y: Int, z: Int): Int {
		val currentMeta = world.getBlockMetadata(x, y, z)
		if (currentMeta == 0 && world.getBlock(x, y - 1, z) == this) return -1
		return topSide(currentMeta)
	}
	
	override fun topSide(meta: Int) = if (meta == 0) 0 else -1
	override fun isSideSolid(world: IBlockAccess?, x: Int, y: Int, z: Int, side: ForgeDirection?) = true
	override fun getRenderType() = LibRenderIDs.idSimpleDoubleBlock
	override fun createNewTileEntity(world: World?, meta: Int) = TileCurtainPlacer(meta)
}
