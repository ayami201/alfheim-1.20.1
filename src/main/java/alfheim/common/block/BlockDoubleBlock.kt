package alfheim.common.block

import alexsocol.asjlib.extendables.block.*
import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.tile.TileDoubleBlock
import net.minecraft.block.*
import net.minecraft.block.material.Material
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.util.*
import net.minecraft.world.*
import net.minecraftforge.common.util.ForgeDirection

class BlockDoubleBlock: BlockDoubleCamo(Material.iron), IFenceConnectable, IFenceGate, IPaneConnectable, IWallConnectable {
	
	init {
		setBlockName("Double")
		setStepSound(soundTypeMetal)
	}
	
	override fun topSide(world: IBlockAccess, x: Int, y: Int, z: Int) = 1
	
	override fun addCollisionBoxesToList(world: World, x: Int, y: Int, z: Int, aabb: AxisAlignedBB?, list: MutableList<Any?>, entity: Entity?) {
		val tile = world.getTileEntity(x, y, z) as? TileDoubleBlock ?: return super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity)
		
		fun addCollisions(block: Block, meta: Int) {
			val wrapper = WorldWrapper(world)
			wrapper.setOverride(x, y, z, block, meta)
			
			when (block) {
				is BlockStairs -> {
					block.func_150147_e(wrapper, x, y, z)
					block.getCollisionBoundingBoxFromPool(world, x, y, z)?.let { if (it.intersectsWith(aabb)) list += it }
					val flag = block.func_150145_f(wrapper, x, y, z)
					block.getCollisionBoundingBoxFromPool(world, x, y, z)?.let { if (it.intersectsWith(aabb)) list += it }
					if (flag && block.func_150144_g(wrapper, x, y, z))
						block.getCollisionBoundingBoxFromPool(world, x, y, z)?.let { if (it.intersectsWith(aabb)) list += it }
					
					block.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f)
				}
				is BlockSlab   -> {
					block.setBlockBoundsBasedOnState(wrapper, x, y, z)
					block.getCollisionBoundingBoxFromPool(world, x, y, z)?.let { if (it.intersectsWith(aabb)) list += it }
				}
				else           -> {
					block.addCollisionBoxesToList(world, x, y, z, aabb, list, null)
				}
			}
		}
		
		addCollisions(tile.blockTop, tile.blockTopMeta)
		addCollisions(tile.blockBottom, tile.blockBottomMeta)
	}
	
	override fun getRenderType() = LibRenderIDs.idDoubleBlock
	override fun createNewTileEntity(world: World?, meta: Int) = TileDoubleBlock()
	
	override fun isGate(world: IBlockAccess, x: Int, y: Int, z: Int): Boolean {
		val tile = world.getTileEntity(x, y, z) as? TileDoubleBlock ?: return false
		return tile.blockTop.let {
			it is BlockFenceGate || it is IFenceGate && it.isGate(world, x, y, z)
		} || tile.blockBottom.let {
			it is BlockFenceGate || it is IFenceGate && it.isGate(world, x, y, z)
		}
	}
	
	override fun canConnectFenceTo(world: IBlockAccess, x: Int, y: Int, z: Int): Boolean {
		val tile = world.getTileEntity(x, y, z) as? TileDoubleBlock ?: return false
		return tile.blockTop.let {
			it is BlockFence || it is IFenceConnectable && it.canConnectFenceTo(world, x, y, z) || it is IFenceGate && it.isGate(world, x, y, z)
		} || tile.blockBottom.let {
			it is BlockFence || it is IFenceConnectable && it.canConnectFenceTo(world, x, y, z) || it is IFenceGate && it.isGate(world, x, y, z)
		}
	}
	
	override fun canPaneConnectTo(world: IBlockAccess, x: Int, y: Int, z: Int): Boolean {
		val tile = world.getTileEntity(x, y, z) as? TileDoubleBlock ?: return false
		return tile.blockTop.let {
			it is BlockPane || it is IPaneConnectable && it.canPaneConnectTo(world, x, y, z)
		} || tile.blockBottom.let {
			it is BlockPane || it is IPaneConnectable && it.canPaneConnectTo(world, x, y, z)
		}
	}
	
	override fun canConnectWallTo(world: IBlockAccess, x: Int, y: Int, z: Int): Boolean {
		val tile = world.getTileEntity(x, y, z) as? TileDoubleBlock ?: return false
		return tile.blockTop.let {
			it is BlockWall || it is IWallConnectable && it.canConnectWallTo(world, x, y, z) || it is IFenceGate && it.isGate(world, x, y, z)
		} || tile.blockBottom.let {
			it is BlockWall || it is IWallConnectable && it.canConnectWallTo(world, x, y, z) || it is IFenceGate && it.isGate(world, x, y, z)
		}
	}
}

class WorldWrapper(val world: World): IBlockAccess {
	
	var xOverride = 0
	var yOverride = -1
	var zOverride = 0
	var blockOverride = Blocks.air
	var blockOverrideMeta = 0
	
	fun setOverride(x: Int, y: Int, z: Int, block: Block, meta: Int) {
		xOverride = x
		yOverride = y
		zOverride = z
		blockOverride = block
		blockOverrideMeta = meta
	}
	
	override fun getBlock(x: Int, y: Int, z: Int): Block {
		if (ChunkCoordinates(x, y, z) == ChunkCoordinates(xOverride, yOverride, zOverride)) return blockOverride
		return world.getBlock(x, y, z)
	}
	
	override fun getBlockMetadata(x: Int, y: Int, z: Int): Int {
		if (ChunkCoordinates(x, y, z) == ChunkCoordinates(xOverride, yOverride, zOverride)) return blockOverrideMeta
		return world.getBlockMetadata(x, y, z)
	}
	
	override fun getTileEntity(x: Int, y: Int, z: Int) = world.getTileEntity(x, y, z)
	override fun getLightBrightnessForSkyBlocks(x: Int, y: Int, z: Int, lightValue: Int) = world.getLightBrightnessForSkyBlocks(x, y, z, lightValue)
	override fun isBlockProvidingPowerTo(x: Int, y: Int, z: Int, side: Int) = world.isBlockProvidingPowerTo(x, y, z, side)
	override fun isAirBlock(x: Int, y: Int, z: Int) = world.isAirBlock(x, y, z)
	override fun getBiomeGenForCoords(x: Int, z: Int) = world.getBiomeGenForCoords(x, z)
	override fun getHeight() = world.height
	override fun extendedLevelsInChunkCache() = world.extendedLevelsInChunkCache()
	override fun isSideSolid(x: Int, y: Int, z: Int, side: ForgeDirection?, default: Boolean) = world.isSideSolid(x, y, z, side, default)
}