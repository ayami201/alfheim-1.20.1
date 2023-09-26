package alfheim.common.block

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.tile.TileComposite
import alfheim.common.floatingisland.FloatingIslandInteractionHandler.calculateIntersectionPoint
import alfheim.common.floatingisland.FloatingIslandInteractionHandler.intersectsWithLine
import alfheim.common.item.AlfheimItems
import alfheim.common.item.ItemCarver.Companion.CarverMode
import alfheim.common.item.ItemCarver.Companion.carverMode
import alfheim.common.network.NetworkService
import alfheim.common.network.packet.MessageFuckedUpServerPrecision
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import kotlin.math.*

class BlockComposite: BlockDoubleCamo() {
	
	init {
		setBlockName("Composite")
	}
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		val carver = player.heldItem
		
		if (carver?.item !== AlfheimItems.carver)
			return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ)
		
		if (ASJUtilities.isClient) NetworkService.sendToServer(MessageFuckedUpServerPrecision(x, y, z, side, hitX, hitY, hitZ))
		
		return true
	}
	
	fun wrapCarving(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, carver: ItemStack, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		val result = carveDatShit(carver.carverMode, world, x, y, z, player, side, hitX, hitY, hitZ)
		if (result) ASJUtilities.dispatchTEToNearbyPlayers(world, x, y, z)
		return result
	}
	
	fun carveDatShit(mode: CarverMode, world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		val tile = world.getTileEntity(x, y, z) as? TileComposite ?: return false
		if (tile.locked) return false
		
		var dir = ForgeDirection.getOrientation(side)
		
		if (mode == CarverMode.ROTATE) {
			tile.composition = rotate3DArray(tile.composition, rotations[if (player.isSneaking) dir.opposite else dir] ?: return false)
			return true
		}
		
		val step = 1.0 / tile.size
		
		for ((i, sub) in tile.composition.withIndex())
			for ((j, subber) in sub.withIndex())
				for ((k, flag) in subber.withIndex()) {
					val bb = getBoundingBox(i * step, j * step, k * step, (i + 1) * step, (j + 1) * step, (k + 1) * step).expand(0.001)
					
					val hit = Vector3(hitX, hitY, hitZ)
					if (mode == CarverMode.BIT && player.isSneaking) when (side) {
						0 -> hit.sub(0, step / 2, 0)
						1 -> hit.add(0, step / 2, 0)
						2 -> hit.sub(0, 0, step / 2)
						3 -> hit.add(0, 0, step / 2)
						4 -> hit.sub(step / 2, 0, 0)
						5 -> hit.add(step / 2, 0, 0)
					}
					
					if (!bb.isVecInside(hit.toVec3())) continue
					
					when (mode) {
						CarverMode.BIT           -> {
							if (!(flag xor player.isSneaking)) continue
							
							tile.composition[i][j][k] = !flag
						}
						
						CarverMode.LINE -> {
							var io = i
							var jo = j
							var ko = k
							
							if (player.isSneaking) {
								io += dir.offsetX
								jo += dir.offsetY
								ko += dir.offsetZ
							} else {
								dir = dir.opposite
							}
							
							line@ while (io in tile.composition.indices && jo in sub.indices && ko in subber.indices) {
								if (player.isSneaking == tile.composition[io][jo][ko]) break@line
								
								tile.composition[io][jo][ko] = player.isSneaking
								
								io += dir.offsetX
								jo += dir.offsetY
								ko += dir.offsetZ
							}
						}
						
						CarverMode.PLANE         -> {
							val oi = side != 4 && side != 5
							val oj = side != 0 && side != 1
							val ok = side != 2 && side != 3
							
							fun recursive(a: Int, b: Int, c: Int, flag: Boolean) {
								if (a !in tile.composition.indices || b !in sub.indices || c !in subber.indices) return
								if (!flag && !tile.composition[a - dir.offsetX][b - dir.offsetY][c - dir.offsetZ]) return
								
								tile.composition[a][b][c] = !flag
								
								if (oi) {
									if (tile.composition.getOrNull(a + 1)?.getOrNull(b)?.getOrNull(c) == flag) recursive(a + 1, b, c, flag)
									if (tile.composition.getOrNull(a - 1)?.getOrNull(b)?.getOrNull(c) == flag) recursive(a - 1, b, c, flag)
								}
								if (oj) {
									if (tile.composition.getOrNull(a)?.getOrNull(b + 1)?.getOrNull(c) == flag) recursive(a, b + 1, c, flag)
									if (tile.composition.getOrNull(a)?.getOrNull(b - 1)?.getOrNull(c) == flag) recursive(a, b - 1, c, flag)
								}
								if (ok) {
									if (tile.composition.getOrNull(a)?.getOrNull(b)?.getOrNull(c + 1) == flag) recursive(a, b, c + 1, flag)
									if (tile.composition.getOrNull(a)?.getOrNull(b)?.getOrNull(c - 1) == flag) recursive(a, b, c - 1, flag)
								}
							}
							
							val io = i + if (player.isSneaking) dir.offsetX else 0
							val jo = j + if (player.isSneaking) dir.offsetY else 0
							val ko = k + if (player.isSneaking) dir.offsetZ else 0
							
							recursive(io, jo, ko, !player.isSneaking)
						}
						
						else -> Unit
					}
					
					return true
				}
		
		return false
	}
	
	private val rotations = mapOf<ForgeDirection, (Int, Int, Int, Int, Int, Int) -> Triple<Int, Int, Int>>(
		ForgeDirection.DOWN  to { x, y, z, maxX, _, _ ->
			z to y with maxX - x
		},
		ForgeDirection.UP    to { x, y, z, _, _, maxZ ->
			maxZ - z to y with x
		},
		ForgeDirection.NORTH to { x, y, z, _, maxY, _ ->
			maxY - y to x with z
		},
		ForgeDirection.SOUTH to { x, y, z, maxX, _, _ ->
			y to maxX - x with z
		},
		ForgeDirection.WEST  to { x, y, z, _, _, maxZ ->
			x to maxZ - z with y
		},
		ForgeDirection.EAST  to { x, y, z, _, maxY, _ ->
			x to z with maxY - y
		},
	)
	
	private fun rotate3DArray(composite: Array<Array<Array<Boolean>>>, rotation: (Int, Int, Int, Int, Int, Int) -> Triple<Int, Int, Int>): Array<Array<Array<Boolean>>> {
		val rotatedArray = Array(composite.size) { Array(composite[0].size) { Array(composite[0][0].size) { true } } }
		
		val maxX = composite.size - 1
		val maxY = composite[0].size - 1
		val maxZ = composite[0][0].size - 1
		
		for (x in composite.indices)
			for (y in composite[x].indices)
				for (z in composite[x][y].indices) {
					val (newX, newY, newZ) = rotation(x, y, z, maxX, maxY, maxZ)
					rotatedArray[newX][newY][newZ] = composite[x][y][z]
				}
		
		return rotatedArray
	}
	
	override fun onBlockClicked(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?) {
		val tile = world?.getTileEntity(x, y, z) as? TileComposite ?: return
		if (player?.heldItem?.item !== AlfheimItems.carver) return
		tile.size = max(2, min(tile.size + if (player.isSneaking) -1 else 1, 16))
		tile.readCustomNBT(NBTTagCompound().also { tile.writeCustomNBT(it) })
		ASJUtilities.dispatchTEToNearbyPlayers(tile)
	}
	
	override fun collisionRayTrace(world: World?, x: Int, y: Int, z: Int, start: Vec3?, end: Vec3?): MovingObjectPosition? {
		val tile = world?.getTileEntity(x, y, z) as? TileComposite ?: return super.collisionRayTrace(world, x, y, z, start, end)
		val step = 1.0 / tile.size
		
		var closest: MovingObjectPosition? = null
		var minDistance = Double.POSITIVE_INFINITY
		val a = Vector3(start!!)
		val b = Vector3(end!!)
		
		for ((i, sub) in tile.composition.withIndex())
			for ((j, subber) in sub.withIndex())
				for ((k, flag) in subber.withIndex()) {
					if (!flag) continue
					
					val aabb = getBoundingBox(
						x + i * step,
						y + j * step,
						z + k * step,
						x + (i + 1) * step,
						y + (j + 1) * step,
						z + (k + 1) * step
					)
					
					if (!aabb.intersectsWithLine(a, b)) continue
					
					val intersectionPoint = aabb.calculateIntersectionPoint(a, b) ?: continue
					val distance = Vector3.vecDistance(a, intersectionPoint)
					
					if (distance >= minDistance) continue
					
					val side = when {
						intersectionPoint.y == aabb.minY -> 0
						intersectionPoint.y == aabb.maxY -> 1
						intersectionPoint.z == aabb.minZ -> 2
						intersectionPoint.z == aabb.maxZ -> 3
						intersectionPoint.x == aabb.minX -> 4
						intersectionPoint.x == aabb.maxX -> 5
						
						else -> 0 // should be impossible, but let it be
					}
					
					minDistance = distance
					closest = MovingObjectPosition(x, y, z, side, Vector3(aabb.minX, aabb.minY, aabb.minZ).add(step / 2).toVec3())
				}
		
		return closest
	}
	
	override fun getSelectedBoundingBoxFromPool(world: World, x: Int, y: Int, z: Int): AxisAlignedBB? {
		val mop = mc.objectMouseOver
		val hit = Vector3(mop.hitVec).sub(x, y, z).toVec3()
		val tile = world.getTileEntity(x, y, z) as? TileComposite ?: return null
		
		val step = 1.0 / tile.size

		for ((i, sub) in tile.composition.withIndex())
			for ((j, subber) in sub.withIndex())
				for ((k, _) in subber.withIndex()) {
					val bb = getBoundingBox(i * step, j * step, k * step, (i + 1) * step, (j + 1) * step, (k + 1) * step)
					if (!bb.isVecInside(hit)) continue

					return bb.getOffsetBoundingBox(x.D, y.D, z.D)
				}
		
		return null
	}
	
	override fun addCollisionBoxesToList(world: World, x: Int, y: Int, z: Int, mask: AxisAlignedBB?, list: MutableList<Any?>, entity: Entity?) {
		val tile = world.getTileEntity(x, y, z) as? TileComposite ?: return super.addCollisionBoxesToList(world, x, y, z, mask, list, entity)
		val step = 1.0 / tile.size
		
		for ((i, sub) in tile.composition.withIndex())
			for ((j, subber) in sub.withIndex())
				for ((k, flag) in subber.withIndex()) {
					if (!flag) continue
					
					val bb = getBoundingBox(x + i * step, y + j * step, z + k * step, x + (i + 1) * step, y + (j + 1) * step, z + (k + 1) * step)
					if (bb.intersectsWith(mask)) list += bb
				}
	}
	
	override fun topSide(meta: Int) = -1
	override fun getRenderType() = LibRenderIDs.idComposite
	override fun createNewTileEntity(world: World?, meta: Int) = TileComposite()
}
