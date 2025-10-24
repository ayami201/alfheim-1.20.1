package alfheim.common.block

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.*
import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.tile.TileDoubleBlock
import net.minecraft.block.*
import net.minecraft.block.material.Material
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.*
import net.minecraft.world.*
import net.minecraftforge.common.util.ForgeDirection

class BlockDoubleBlock: BlockDoubleCamo(Material.iron), IFenceConnectable, IFenceGate, IPaneConnectable, IWallConnectable {
	
	init {
		setBlockName("Double")
		setStepSound(soundTypeMetal)
	}
	
	override fun topSide(meta: Int) = 1
	
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
				is BlockSnow, is BlockSnowLayer -> {
					block.setBlockBoundsBasedOnState(wrapper, x, y, z)
					getBoundingBox(
						x + block.blockBoundsMinX,
						y + block.blockBoundsMinY,
						z + block.blockBoundsMinZ,
						x + block.blockBoundsMaxX,
						y + block.blockBoundsMaxY,
						z + block.blockBoundsMaxZ
					).let { if (it.intersectsWith(aabb)) list += it }
				}
				else           -> {
					block.addCollisionBoxesToList(world, x, y, z, aabb, list, null)
				}
			}
		}
		
		addCollisions(tile.blockTop, tile.blockTopMeta)
		addCollisions(tile.blockBottom, tile.blockBottomMeta)
	}
	
	override fun setBlockBoundsBasedOnState(world: IBlockAccess?, x: Int, y: Int, z: Int) {
		val bb = if (world is World) getBB(world, x, y, z) else getBoundingBox(x, y, z, x + 1, y + 1, z + 1)
		bb.offset(-x, -y, -z)
		setBlockBounds(bb.minX.clamp(0.0, 1.0).F, bb.minY.clamp(0.0, 1.0).F, bb.minZ.clamp(0.0, 1.0).F, bb.maxX.clamp(0.0, 1.0).F, bb.maxY.clamp(0.0, 1.0).F, bb.maxZ.clamp(0.0, 1.0).F)
	}
	
	override fun getSelectedBoundingBoxFromPool(world: World, x: Int, y: Int, z: Int): AxisAlignedBB {
		setBlockBoundsBasedOnState(world, x, y, z)
		return super.getSelectedBoundingBoxFromPool(world, x, y, z)
	}
	
	@Suppress("UNCHECKED_CAST")
	fun getBB(world: World, x: Int, y: Int, z: Int): AxisAlignedBB {
		val bbs = mutableListOf<AxisAlignedBB>()
		addCollisionBoxesToList(world, x, y, z, TileEntity.INFINITE_EXTENT_AABB, bbs as MutableList<Any?>, null)
		
		val tile = world.getTileEntity(x, y, z) as? TileDoubleBlock
		
		if (bbs.isEmpty()) {
			if (ASJUtilities.isClient) {
				tile?.blockTop?.getSelectedBoundingBoxFromPool(world, x, y, z)?.let { bbs += it }
				tile?.blockBottom?.getSelectedBoundingBoxFromPool(world, x, y, z)?.let { bbs += it }
			} else {
				tile?.blockTop?.apply { bbs += getBoundingBox(blockBoundsMinX, blockBoundsMinY, blockBoundsMinZ, blockBoundsMaxX, blockBoundsMaxY, blockBoundsMaxZ) }
				tile?.blockBottom?.apply { bbs += getBoundingBox(blockBoundsMinX, blockBoundsMinY, blockBoundsMinZ, blockBoundsMaxX, blockBoundsMaxY, blockBoundsMaxZ) }
			}
		}
		
		return getBoundingBox(bbs.minOf { it.minX }, bbs.minOf { it.minY }, bbs.minOf { it.minZ }, bbs.maxOf { it.maxX }, bbs.maxOf { it.maxY }, bbs.maxOf { it.maxZ })
	}
	
	override fun isSideSolid(world: IBlockAccess?, x: Int, y: Int, z: Int, side: ForgeDirection): Boolean {
		if (world !is World) return false
		
		val bb = getBB(world, x, y, z).offset(-x, -y, -z)
		
		return when (side) {
			ForgeDirection.DOWN    -> bb.minX == 0.0 && bb.minZ == 0.0 && bb.minY == 0.0 && bb.maxX == 1.0                   && bb.maxZ == 1.0
			ForgeDirection.UP      -> bb.minX == 0.0 && bb.minZ == 0.0 &&                   bb.maxX == 1.0 && bb.maxY == 1.0 && bb.maxZ == 1.0
			ForgeDirection.NORTH   -> bb.minX == 0.0 && bb.minZ == 0.0 && bb.minY == 0.0 && bb.maxX == 1.0 && bb.maxY == 1.0
			ForgeDirection.SOUTH   -> bb.minX == 0.0 &&                   bb.minY == 0.0 && bb.maxX == 1.0 && bb.maxY == 1.0 && bb.maxZ == 1.0
			ForgeDirection.WEST    -> bb.minX == 0.0 && bb.minZ == 0.0 && bb.minY == 0.0                   && bb.maxY == 1.0 && bb.maxZ == 1.0
			ForgeDirection.EAST    ->                   bb.minZ == 0.0 && bb.minY == 0.0 && bb.maxX == 1.0 && bb.maxY == 1.0 && bb.maxZ == 1.0
			ForgeDirection.UNKNOWN -> false
		}
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

class WorldWrapper(val original: IBlockAccess): IBlockAccess {
	
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
		return original.getBlock(x, y, z)
	}
	
	override fun getBlockMetadata(x: Int, y: Int, z: Int): Int {
		if (ChunkCoordinates(x, y, z) == ChunkCoordinates(xOverride, yOverride, zOverride)) return blockOverrideMeta
		return original.getBlockMetadata(x, y, z)
	}
	
	override fun getTileEntity(x: Int, y: Int, z: Int) = original.getTileEntity(x, y, z)
	override fun getLightBrightnessForSkyBlocks(x: Int, y: Int, z: Int, lightValue: Int) = original.getLightBrightnessForSkyBlocks(x, y, z, lightValue)
	override fun isBlockProvidingPowerTo(x: Int, y: Int, z: Int, side: Int) = original.isBlockProvidingPowerTo(x, y, z, side)
	override fun isAirBlock(x: Int, y: Int, z: Int) = original.isAirBlock(x, y, z)
	override fun getBiomeGenForCoords(x: Int, z: Int) = original.getBiomeGenForCoords(x, z)
	override fun getHeight() = original.height
	override fun extendedLevelsInChunkCache() = original.extendedLevelsInChunkCache()
	override fun isSideSolid(x: Int, y: Int, z: Int, side: ForgeDirection?, default: Boolean) = original.isSideSolid(x, y, z, side, default)
}