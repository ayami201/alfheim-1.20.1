package alfheim.common.block

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.client.render.tile.RenderTileItemFrame
import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.TileItemFrame
import alfheim.common.item.block.ItemBlockItemFrame
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.Entity
import net.minecraft.entity.player.*
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import vazkii.botania.common.item.ModItems
import java.util.*
import kotlin.math.*

// shameless CB Translocators placement code rip-off
class BlockItemFrame: BlockContainerMod(Material.wood) {
	
	init {
		val name = "ItemFrame"
		
		setBlockName(name)
		setHardness(1f)
		setStepSound(soundTypeWood)
		
		GameRegistry.registerBlock(this, ItemBlockItemFrame::class.java, name)
	}
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		if (world.isRemote) return true
		
		val tile = world.getTileEntity(x, y, z) as? TileItemFrame ?: return false
		val frameId = getSelectedFrame(tile, world, x, y, z, player) ?: return false
		val frame = tile.frames[frameId] ?: return false
		val heldItem: ItemStack? = player.heldItem
		
		if (frame.item != null) {
			if (heldItem?.item === ModItems.phantomInk) {
				frame.invis = true
				--heldItem.stackSize
			} else if (player.isSneaking) {
				if (!player.inventory.addItemStackToInventory(frame.item))
					player.dropPlayerItemWithRandomChoice(frame.item, false)
				frame.item = null
			} else if (heldItem == null)
				frame.rotation = (frame.rotation + 1) % 4
			else
				return false
		} else {
			if (heldItem == null)
				return false
			
			frame.item = heldItem.copy()
			player.setCurrentItemOrArmor(0, null)
		}
		
		ASJUtilities.dispatchTEToNearbyPlayers(tile)
		
		return true
	}
	
	override fun onNeighborBlockChange(world: World, x: Int, y: Int, z: Int, block: Block) {
		val tile = world.getTileEntity(x, y, z) as? TileItemFrame ?: return
		
		for (i in 0..5) if (tile.frames[i] != null && !canExist(world, x, y, z, i)) if (tile.removeFrame(i, true)) break
	}
	
	override fun removedByPlayer(world: World, player: EntityPlayer, x: Int, y: Int, z: Int, willharvest: Boolean): Boolean {
		val tile = world.getTileEntity(x, y, z) as? TileItemFrame ?: return false
		val frameId = getSelectedFrame(tile, world, x, y, z, player) ?: return false
		
		tile.removeFrame(frameId, !player.capabilities.isCreativeMode)
		
		return false
	}
	
	override fun getDrops(world: World, x: Int, y: Int, z: Int, md: Int, fortune: Int): ArrayList<ItemStack> {
		val drops = ArrayList<ItemStack>()
		if (world.isRemote) return drops
		val tile = world.getTileEntity(x, y, z) as? TileItemFrame ?: return drops
		
		for (frame in tile.frames)
			if (frame != null) {
				drops.add(ItemStack(this))
				frame.item?.let { drops += it }
			}
		
		return drops
	}
	
	override fun collisionRayTrace(world: World, x: Int, y: Int, z: Int, start: Vec3, end: Vec3): MovingObjectPosition? {
		val tile = world.getTileEntity(x, y, z) as? TileItemFrame ?: return null
		
		var side: Int = -1
		var closestPoint: Vec3? = null
		var minDistance = Double.MAX_VALUE
		
		for ((id, aabb) in aabbs(x, y, z).withIndex()) {
			if (tile.frames[id] == null) continue
			
			val intersection = lineIntersectsAABB3D(start, end, aabb) ?: continue
			
			val distance = sqrt(
				(intersection.xCoord - start.xCoord).pow(2.0) +
				(intersection.yCoord - start.yCoord).pow(2.0) +
				(intersection.zCoord - start.zCoord).pow(2.0)
			)
			
			if (distance < minDistance) {
				minDistance = distance
				closestPoint = intersection
				side = id
			}
		}
		
		return MovingObjectPosition(x, y, z, side, closestPoint ?: return null)
	}
	
	fun lineIntersectsAABB3D(start: Vec3, end: Vec3, aabb: AxisAlignedBB): Vec3? {
		var tmin = (aabb.minX - start.xCoord) / (end.xCoord - start.xCoord)
		var tmax = (aabb.maxX - start.xCoord) / (end.xCoord - start.xCoord)
		
		if (tmin > tmax) {
			val temp = tmin
			tmin = tmax
			tmax = temp
		}
		
		var tymin = (aabb.minY - start.yCoord) / (end.yCoord - start.yCoord)
		var tymax = (aabb.maxY - start.yCoord) / (end.yCoord - start.yCoord)
		
		if (tymin > tymax) {
			val temp = tymin
			tymin = tymax
			tymax = temp
		}
		
		if ((tmin > tymax) || (tymin > tmax)) {
			return null // No intersection
		}
		
		if (tymin > tmin) tmin = tymin
		if (tymax < tmax) tmax = tymax
		
		var tzmin = (aabb.minZ - start.zCoord) / (end.zCoord - start.zCoord)
		var tzmax = (aabb.maxZ - start.zCoord) / (end.zCoord - start.zCoord)
		
		if (tzmin > tzmax) {
			val temp = tzmin
			tzmin = tzmax
			tzmax = temp
		}
		
		if ((tmin > tzmax) || (tzmin > tmax)) {
			return null // No intersection
		}
		
		if (tzmin > tmin) tmin = tzmin
//		if (tzmax < tmax) tmax = tzmax
		
		// Calculate intersection point closest to the `start` point
		val t = tmin
		return Vec3(
			start.xCoord + t * (end.xCoord - start.xCoord),
			start.yCoord + t * (end.yCoord - start.yCoord),
			start.zCoord + t * (end.zCoord - start.zCoord)
		)
	}
	
	override fun getSelectedBoundingBoxFromPool(world: World, x: Int, y: Int, z: Int): AxisAlignedBB {
		val default = getBoundingBox(x, y, z, x + 1, y + 1, z + 1)
		
		val tile = world.getTileEntity(x, y, z) as? TileItemFrame ?: return default
		val id = getSelectedFrame(tile, world, x, y, z, m = mc.objectMouseOver) ?: return default
		
		return aabbs(x, y, z)[id]
	}
	
	override fun addCollisionBoxesToList(world: World, x: Int, y: Int, z: Int, aabb: AxisAlignedBB?, list: MutableList<Any?>, entity: Entity?) {
		aabb ?: return
		val tile = world.getTileEntity(x, y, z) as? TileItemFrame ?: return
		
		for ((id, const) in aabbs(x, y, z).withIndex()) {
			if (tile.frames[id] == null) continue
			
			if (const.intersectsWith(aabb))
				list += const
		}
	}
	
	override fun getPickBlock(target: MovingObjectPosition, world: World, x: Int, y: Int, z: Int, player: EntityPlayer?): ItemStack {
		val empty = ItemStack(this)
		
		val tile = world.getTileEntity(x, y, z) as? TileItemFrame ?: return empty
		val frameId = getSelectedFrame(tile, world, x, y, z, player) ?: return empty
		val frame = tile.frames[frameId] ?: return empty
		
		return frame.item ?: empty
	}
	
	override fun registerBlockIcons(reg: IIconRegister) = Unit
	override fun getIcon(side: Int, meta: Int) = RenderTileItemFrame.icon
	override fun quantityDropped(meta: Int, fortune: Int, random: Random?) = 0
	override fun getRenderType() = -1
	override fun isOpaqueCube() = false
	override fun renderAsNormalBlock() = false
	override fun shouldRegisterInNameSet() = false
	override fun createNewTileEntity(world: World?, meta: Int) = TileItemFrame()
	
	companion object {
		
		fun aabbs(x: Int, y: Int, z: Int) = arrayOf(
			getBoundingBox(0.0 + 0.125, 0.0,         0.0 + 0.125, 1.0 - 0.125, 0.0625,      1.0 - 0.125).offset(x, y, z), // DOWN
			getBoundingBox(0.0 + 0.125, 0.9375,      0.0 + 0.125, 1.0 - 0.125, 1.0,         1.0 - 0.125).offset(x, y, z), // UP
			getBoundingBox(0.0 + 0.125, 0.0 + 0.125, 0.0,         1.0 - 0.125, 1.0 - 0.125, 0.0625     ).offset(x, y, z), // NORTH
			getBoundingBox(0.0 + 0.125, 0.0 + 0.125, 0.9375,      1.0 - 0.125, 1.0 - 0.125, 1.0        ).offset(x, y, z), // SOUTH
			getBoundingBox(0.0,         0.0 + 0.125, 0.0 + 0.125, 0.0625,      1.0 - 0.125, 1.0 - 0.125).offset(x, y, z), // WEST
			getBoundingBox(0.9375,      0.0 + 0.125, 0.0 + 0.125, 1.0,         1.0 - 0.125, 1.0 - 0.125).offset(x, y, z), // EAST
		)
		
		fun canExist(world: World, x: Int, y: Int, z: Int, side: Int): Boolean {
			val d = ForgeDirection.getOrientation(side)
			val (i, j, k) = Vector3(x, y, z).add(d.offsetX, d.offsetY, d.offsetZ).I
			return world.getBlock(i, j, k).material.isSolid
		}
		
		// CCC code start
		fun retraceBlock(world: World, player: EntityPlayer, x: Int, y: Int, z: Int): MovingObjectPosition? {
			val headVec = getCorrectedHeadVec(player)
			val lookVec = player.getLook(1.0f)
			val reach = getBlockReachDistance(player)
			val endVec = headVec.addVector(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach)
			return world.getBlock(x, y, z).collisionRayTrace(world, x, y, z, headVec, endVec)
		}
		
		fun getCorrectedHeadVec(player: EntityPlayer): Vec3 {
			val v = Vec3.createVectorHelper(player.posX, player.posY, player.posZ)
			if (player.worldObj.isRemote) {
				v.yCoord += (player.getEyeHeight() - player.defaultEyeHeight).toDouble()
			} else {
				v.yCoord += player.getEyeHeight().toDouble()
				if (player is EntityPlayerMP && player.isSneaking) {
					v.yCoord -= 0.08
				}
			}
			
			return v
		}
		
		fun getBlockReachDistance(player: EntityPlayer) = if (player.worldObj.isRemote) mc.playerController.blockReachDistance.D else if (player is EntityPlayerMP) player.theItemInWorldManager.blockReachDistance else 5.0
		// CCC code end
		
		fun AxisAlignedBB.isVecInsideOrTouch(vec: Vec3): Boolean {
			return if (vec.xCoord in minX..maxX) (if (vec.yCoord in minY..maxY) vec.zCoord in minZ..maxZ else false) else false
		}
		
		fun getSelectedFrame(tile: TileItemFrame, world: World, x: Int, y: Int, z: Int, player: EntityPlayer? = null, m: MovingObjectPosition? = null): Int? {
			val mop = m ?: retraceBlock(world, player!!, x, y, z) ?: return null
			
			val aabbs = aabbs(x, y, z)
			for ((id, frame) in tile.frames.withIndex()) {
				frame ?: continue
				if (aabbs[id].isVecInsideOrTouch(mop.hitVec))
					return id
			}
			
			return null
		}
	}
}
