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
import alfheim.common.lexicon.AlfheimLexiconData
import alfheim.common.network.NetworkService
import alfheim.common.network.packet.MessageFuckedUpServerPrecision
import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import kotlin.math.*

class BlockComposite: BlockDoubleCamo(info = false) {
	
	init {
		setBlockName("Composite")
	}
	
	override fun onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		val stack = player.heldItem
		
		if (stack?.item !== AlfheimItems.carver && (stack?.block === Blocks.air || stack?.block is BlockDoubleCamo))
			return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ)
		
		if (ASJUtilities.isClient)
			NetworkService.sendToServer(MessageFuckedUpServerPrecision(x, y, z, side, hitX, hitY, hitZ))
		
		return true
	}
	
	fun wrapCarving(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, stack: ItemStack, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		val mode: CarverMode
		val data: Pair<Block, Int>?
		
		val block = stack.block
		if (stack.item === AlfheimItems.carver) {
			mode = stack.carverMode
			data = null
		} else if (block !== Blocks.air && block !is BlockDoubleCamo) {
			mode = CarverMode.BIT
			data = block to stack.meta
		} else
			return false
		
		val result = carveDatShit(mode, world, x, y, z, player, side, hitX, hitY, hitZ, data)
		if (result) ASJUtilities.dispatchTEToNearbyPlayers(world, x, y, z)
		
		return result
	}
	
	fun carveDatShit(mode: CarverMode, world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float, newData: Pair<Block, Int>?): Boolean {
		val tile = world.getTileEntity(x, y, z) as? TileComposite ?: return false
		if (tile.locked) return false
		
		var dir = ForgeDirection.getOrientation(side)
		
		when (mode) {
			CarverMode.ROTATE -> {
				tile.composition = transform3DArray(tile.composition, rotations[if (player.isSneaking) dir.opposite else dir] ?: return false)
				return true
			}
			CarverMode.FLIP   -> {
				tile.composition = transform3DArray(tile.composition, flips[if (player.isSneaking) dir.opposite else dir] ?: return false)
				return true
			}
			CarverMode.PUSH   -> {
				tile.composition = transform3DArray(tile.composition, pushes[if (player.isSneaking) dir else dir.opposite] ?: return false)
				return true
			}
			else              -> Unit
		}
		
		val step = 1.0 / tile.size
		
		for ((i, sub) in tile.composition.withIndex())
			for ((j, subber) in sub.withIndex())
				for ((k, data) in subber.withIndex()) {
					val bb = getBoundingBox(i * step, j * step, k * step, (i + 1) * step, (j + 1) * step, (k + 1) * step).expand(0.001)
					
					if (!bb.isVecInside(Vec3(hitX, hitY, hitZ))) continue
					
					when (mode) {
						CarverMode.BIT           -> {
							if (newData != null || player.isSneaking) {
								val array = tile.composition.getOrNull(i + dir.offsetX)?.getOrNull(j + dir.offsetY)
								if (array == null || k + dir.offsetZ !in array.indices)
									return false
								
								array[k + dir.offsetZ] = newData ?: data
							} else {
								tile.composition[i][j][k] = null
							}
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
								if (player.isSneaking == (tile.composition[io][jo][ko] != null)) break@line
								
								tile.composition[io][jo][ko] = if (player.isSneaking) tile.composition[io - dir.offsetX][jo - dir.offsetY][ko - dir.offsetZ] else null
								
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
								if (!flag && tile.composition[a - dir.offsetX][b - dir.offsetY][c - dir.offsetZ] == null) return
								
								tile.composition[a][b][c] = if (!flag) tile.composition[a - dir.offsetX][b - dir.offsetY][c - dir.offsetZ] else null
								
								if (oi) {
									if ((tile.composition.getOrNull(a + 1)?.getOrNull(b)?.getOrNull(c) != null) == flag) recursive(a + 1, b, c, flag)
									if ((tile.composition.getOrNull(a - 1)?.getOrNull(b)?.getOrNull(c) != null) == flag) recursive(a - 1, b, c, flag)
								}
								if (oj) {
									if ((tile.composition.getOrNull(a)?.getOrNull(b + 1)?.getOrNull(c) != null) == flag) recursive(a, b + 1, c, flag)
									if ((tile.composition.getOrNull(a)?.getOrNull(b - 1)?.getOrNull(c) != null) == flag) recursive(a, b - 1, c, flag)
								}
								if (ok) {
									if ((tile.composition.getOrNull(a)?.getOrNull(b)?.getOrNull(c + 1) != null) == flag) recursive(a, b, c + 1, flag)
									if ((tile.composition.getOrNull(a)?.getOrNull(b)?.getOrNull(c - 1) != null) == flag) recursive(a, b, c - 1, flag)
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
		ForgeDirection.DOWN  to { x, y, z, maxX, _, _ -> z to y with maxX - x },
		ForgeDirection.UP    to { x, y, z, _, _, maxZ -> maxZ - z to y with x },
		ForgeDirection.NORTH to { x, y, z, _, maxY, _ -> maxY - y to x with z },
		ForgeDirection.SOUTH to { x, y, z, maxX, _, _ -> y to maxX - x with z },
		ForgeDirection.WEST  to { x, y, z, _, _, maxZ -> x to maxZ - z with y },
		ForgeDirection.EAST  to { x, y, z, _, maxY, _ -> x to z with maxY - y },
	)
	
	private val flips = mapOf<ForgeDirection, (Int, Int, Int, Int, Int, Int) -> Triple<Int, Int, Int>>(
		ForgeDirection.DOWN  to { x, y, z, _, maxY, _ -> x to maxY - y with z },
		ForgeDirection.UP    to { x, y, z, _, maxY, _ -> x to maxY - y with z },
		ForgeDirection.NORTH to { x, y, z, _, _, maxZ -> x to y with maxZ - z },
		ForgeDirection.SOUTH to { x, y, z, _, _, maxZ -> x to y with maxZ - z },
		ForgeDirection.WEST  to { x, y, z, maxX, _, _ -> maxX - x to y with z },
		ForgeDirection.EAST  to { x, y, z, maxX, _, _ -> maxX - x to y with z },
	)
	
	private val pushes = ForgeDirection.VALID_DIRECTIONS.associateWith<ForgeDirection, (Int, Int, Int, Int, Int, Int) -> Triple<Int, Int, Int>> { { x, y, z, _, _, _ -> x + it.offsetX to y + it.offsetY with z + it.offsetZ } }
	
	private fun transform3DArray(original: Array<Array<Array<Pair<Block, Int>?>>>, transformation: (Int, Int, Int, Int, Int, Int) -> Triple<Int, Int, Int>): Array<Array<Array<Pair<Block, Int>?>>> {
		val transformedArray = Array(original.size) { Array(original[0].size) { Array(original[0][0].size) { original[0][0][0] } } }
		
		val maxX = original.size - 1
		val maxY = original[0].size - 1
		val maxZ = original[0][0].size - 1
		
		for (x in original.indices)
			for (y in original[x].indices)
				for (z in original[x][y].indices) {
					val (newX, newY, newZ) = transformation(x, y, z, maxX, maxY, maxZ)
					val array = transformedArray.getOrNull(newX)?.getOrNull(newY)
					if (array != null && newZ in array.indices) array[newZ] = original[x][y][z]
				}
		
		return transformedArray
	}
	
	override fun onBlockClicked(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?) {
		val tile = world?.getTileEntity(x, y, z) as? TileComposite ?: return
		val stack = player?.heldItem
		if (tile.locked || stack?.item !== AlfheimItems.carver) return
		
		stack.cooldown = 2
		tile.size = max(1, min(tile.size + if (player.isSneaking) -1 else 1, 16))
		
		if (tile.size == 1) {
			tile.noDrop = true
			world.setBlock(x, y, z, tile.blockBottom, tile.blockBottomMeta, 3)
			return
		}
		
		tile.readCustomNBT(NBTTagCompound().also { tile.writeCustomNBT(it) })
		ASJUtilities.dispatchTEToNearbyPlayers(tile)
	}
	
	@Suppress("OVERRIDE_DEPRECATION") // no render for WAILA
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int) = ItemStack(this)
	
	override fun getPickBlock(target: MovingObjectPosition?, world: World, x: Int, y: Int, z: Int, player: EntityPlayer?) = super.getPickBlock(target, world, x, y, z)
	
	override fun collisionRayTrace(world: World?, x: Int, y: Int, z: Int, start: Vec3?, end: Vec3?): MovingObjectPosition? {
		val tile = world?.getTileEntity(x, y, z) as? TileComposite ?: return super.collisionRayTrace(world, x, y, z, start, end)
		val step = 1.0 / tile.size
		
		var closest: MovingObjectPosition? = null
		var minDistance = Double.POSITIVE_INFINITY
		val a = Vector3(start!!)
		val b = Vector3(end!!)
		
		for ((i, sub) in tile.composition.withIndex())
			for ((j, subber) in sub.withIndex())
				for ((k, data) in subber.withIndex()) {
					if (data == null) continue
					
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
		val tile = world.getTileEntity(x, y, z) as? TileComposite ?: return getBoundingBox(x, y, z, x + 1, y + 1, z + 1)
		
		val step = 1.0 / tile.size

		for ((i, sub) in tile.composition.withIndex())
			for ((j, subber) in sub.withIndex())
				for ((k, _) in subber.withIndex()) {
					val bb = getBoundingBox(i * step, j * step, k * step, (i + 1) * step, (j + 1) * step, (k + 1) * step)
					if (!bb.isVecInside(hit)) continue

					return bb.getOffsetBoundingBox(x.D, y.D, z.D)
				}
		
		return getBoundingBox(x, y, z, x + 1, y + 1, z + 1)
	}
	
	override fun addCollisionBoxesToList(world: World, x: Int, y: Int, z: Int, mask: AxisAlignedBB?, list: MutableList<Any?>, entity: Entity?) {
		val tile = world.getTileEntity(x, y, z) as? TileComposite ?: return super.addCollisionBoxesToList(world, x, y, z, mask, list, entity)
		val step = 1.0 / tile.size
		
		for ((i, sub) in tile.composition.withIndex())
			for ((j, subber) in sub.withIndex())
				for ((k, data) in subber.withIndex()) {
					if (data == null) continue
					
					val bb = getBoundingBox(x + i * step, y + j * step, z + k * step, x + (i + 1) * step, y + (j + 1) * step, z + (k + 1) * step)
					if (bb.intersectsWith(mask)) list += bb
				}
	}
	
	override fun topSide(meta: Int) = -1
	override fun getRenderType() = LibRenderIDs.idComposite
	override fun createNewTileEntity(world: World?, meta: Int) = TileComposite()
	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.carver
}
