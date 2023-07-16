package alfheim.common.floatingisland

import alexsocol.asjlib.*
import net.minecraft.block.material.Material
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.pathfinding.*
import net.minecraft.util.IntHashMap

class FloatingIslandPathfinder(val island: EntityFloatingIsland, val canPassOpenWoodenDoors: Boolean, val canPassClosedWoodenDoors: Boolean, val avoidsWater: Boolean) {
	
	val worldMap = island.blockAccess
	
	/** The path being generated  */
	private val path = Path()
	
	/** The points in the path  */
	private val pointMap = IntHashMap()
	
	/** Selection of path points to add to the path  */
	private val pathOptions = arrayOfNulls<PathPoint>(32)
	
	/**
	 * Creates a path from one entity to another within a minimum distance
	 */
	fun createEntityPathTo(host: Entity, target: Entity, pathSearchRange: Float): PathEntity? {
		return createEntityPathTo(host, target.posX.x, target.boundingBox.minY.y, target.posZ.z, pathSearchRange)
	}
	
	/**
	 * Creates a path from an entity to a specified location within a minimum distance
	 */
	fun createEntityPathTo(host: Entity, x: Int, y: Int, z: Int, pathSearchRange: Float): PathEntity? {
		return createEntityPathTo(host, x.x + 0.5, y.y + 0.5, z.z + 0.5, pathSearchRange)
	}
	
	/**
	 * Internal implementation of creating a path from an entity to a point
	 */
	private fun createEntityPathTo(host: Entity, x: Double, y: Double, z: Double, pathSearchRange: Float): PathEntity? {
		path.clearPath()
		pointMap.clearMap()
		
		val i = (host.boundingBox.minY.y + 0.5).mfloor()
		
		val start = openPoint(host.boundingBox.minX.x.mfloor(), i, host.boundingBox.minZ.z.mfloor())
		val end = openPoint((x - host.width / 2.0).mfloor(), y.mfloor(), (z - host.width / 2.0).mfloor())
		val offset = PathPoint((host.width + 1.0).mfloor(), (host.height + 1.0).mfloor(), (host.width + 1.0).mfloor())
		
		return addToPath(host, start, end, offset, pathSearchRange)
	}
	
	/**
	 * Adds a path from start to end and returns the whole path
	 */
	private fun addToPath(host: Entity, start: PathPoint, end: PathPoint, offset: PathPoint, pathSearchRange: Float): PathEntity? {
		start.totalPathDistance = 0.0f
		start.distanceToNext = start.distanceToSquared(end)
		start.distanceToTarget = start.distanceToNext
		
		path.clearPath()
		path.addPoint(start)
		
		var newStart = start
		
		while (!path.isPathEmpty) {
			val currentPoint = path.dequeue()
			
			if (currentPoint == end) {
				return createEntityPath(end)
			}
			
			if (currentPoint.distanceToSquared(end) < newStart.distanceToSquared(end)) {
				newStart = currentPoint
			}
			
			currentPoint.isFirst = true
			
			val optionsCount = findPathOptions(host, currentPoint, offset, end, pathSearchRange)
			
			for (j in 0 until optionsCount) {
				val pathOption = pathOptions[j] ?: continue // null-safety, why not
				
				val newDistance = currentPoint.totalPathDistance + currentPoint.distanceToSquared(pathOption)
				
				if (!pathOption.isAssigned || newDistance < pathOption.totalPathDistance) {
					pathOption.previous = currentPoint
					pathOption.totalPathDistance = newDistance
					pathOption.distanceToNext = pathOption.distanceToSquared(end)
					
					if (pathOption.isAssigned) {
						path.changeDistance(pathOption, pathOption.totalPathDistance + pathOption.distanceToNext)
					} else {
						pathOption.distanceToTarget = pathOption.totalPathDistance + pathOption.distanceToNext
						path.addPoint(pathOption)
					}
				}
			}
		}
		
		return if (newStart === start) null else createEntityPath(newStart)
	}
	
	/**
	 * populates pathOptions with available points and returns the number of options found
	 */
	private fun findPathOptions(host: Entity, currentPoint: PathPoint, offset: PathPoint, targetPoint: PathPoint, pathSearchRange: Float): Int {
		var optionsCount = 0
		val collisionState = if (getEntityCollisionState(host, currentPoint.xCoord, currentPoint.yCoord + 1, currentPoint.zCoord, offset) == 1) 1 else 0
		
		val pathPosZ = getSafePoint(host, currentPoint.xCoord, currentPoint.yCoord, currentPoint.zCoord + 1, offset, collisionState)
		val pathNegX = getSafePoint(host, currentPoint.xCoord - 1, currentPoint.yCoord, currentPoint.zCoord, offset, collisionState)
		val pathPosX = getSafePoint(host, currentPoint.xCoord + 1, currentPoint.yCoord, currentPoint.zCoord, offset, collisionState)
		val pathNegZ = getSafePoint(host, currentPoint.xCoord, currentPoint.yCoord, currentPoint.zCoord - 1, offset, collisionState)
		
		if (pathPosZ != null && !pathPosZ.isFirst && pathPosZ.distanceTo(targetPoint) < pathSearchRange)
			pathOptions[optionsCount++] = pathPosZ
		
		if (pathNegX != null && !pathNegX.isFirst && pathNegX.distanceTo(targetPoint) < pathSearchRange)
			pathOptions[optionsCount++] = pathNegX
		
		if (pathPosX != null && !pathPosX.isFirst && pathPosX.distanceTo(targetPoint) < pathSearchRange)
			pathOptions[optionsCount++] = pathPosX
		
		if (pathNegZ != null && !pathNegZ.isFirst && pathNegZ.distanceTo(targetPoint) < pathSearchRange)
			pathOptions[optionsCount++] = pathNegZ
		
		return optionsCount
	}
	
	/**
	 * Returns a point that the entity can safely move to
	 */
	private fun getSafePoint(host: Entity, x: Int, _y: Int, z: Int, offset: PathPoint, yOffset: Int): PathPoint? {
		var y = _y
		
		var nextPoint: PathPoint? = null
		
		val collisionState = getEntityCollisionState(host, x, y, z, offset)
		
		if (collisionState == 2) return openPoint(x, y, z)
		
		if (collisionState == 1) nextPoint = openPoint(x, y, z)
		
		if (nextPoint == null && yOffset > 0 && collisionState != -3 && collisionState != -4 && getEntityCollisionState(host, x, y + yOffset, z, offset) == 1) {
			nextPoint = openPoint(x, y + yOffset, z)
			y += yOffset
		}
		
		if (nextPoint == null) return null
		
		var j1 = 0
		var newCollisionState = 0
		
		while (y > 0) {
			newCollisionState = getEntityCollisionState(host, x, y - 1, z, offset)
			
			if (avoidsWater && newCollisionState == -1) return null
			if (newCollisionState != 1) break
			if (j1++ >= host.maxSafePointTries) return null
			
			--y
			if (y > 0) nextPoint = openPoint(x, y, z)
		}
		
		if (newCollisionState == -2) return null
		
		return nextPoint
	}
	
	/**
	 * Returns a mapped point or creates and adds one
	 */
	private fun openPoint(x: Int, y: Int, z: Int): PathPoint {
		val l = PathPoint.makeHash(x, y, z)
		var pathpoint = pointMap.lookup(l) as? PathPoint
		
		if (pathpoint == null) {
			pathpoint = PathPoint(x, y, z)
			pointMap.addKey(l, pathpoint)
		}
		
		return pathpoint
	}
	
	/**
	 * Checks if an entity collides with blocks at a position. Returns 1 if clear, 0 for colliding with any solid block,
	 * -1 for water(if avoiding water) but otherwise clear, -2 for lava, -3 for fence, -4 for closed trapdoor, 2 if
	 * otherwise clear except for open trapdoor or water(if not avoiding)
	 */
	private fun getEntityCollisionState(host: Entity, x: Int, y: Int, z: Int, offset: PathPoint): Int {
		var foundSomeSemiBlockingShit = false
		
		for (i in x until x + offset.xCoord) {
			for (j in y until y + offset.yCoord) {
				for (k in z until z + offset.zCoord) {
					val block = worldMap.getBlock(i, j, k)
					
					if (block.material === Material.air) continue
					
					when {
						block === Blocks.trapdoor                                -> foundSomeSemiBlockingShit = true
						block !== Blocks.flowing_water && block !== Blocks.water -> if (!canPassOpenWoodenDoors && block === Blocks.wooden_door) return 0
						else                                                     -> {
							if (avoidsWater) return -1
							foundSomeSemiBlockingShit = true
						}
					}
					
					val blockRenderType = block.renderType
					
					if (blockRenderType == 9) {
						val startX = host.posX.x.mfloor()
						val startY = host.posY.y.mfloor()
						val startZ = host.posZ.z.mfloor()
						
						if (worldMap.getBlock(startX, startY, startZ).renderType != 9 && worldMap.getBlock(startX, startY - 1, startZ).renderType != 9) return -3
					} else if (!block.getBlocksMovement(worldMap, i, j, k) && (!canPassClosedWoodenDoors || block !== Blocks.wooden_door)) {
						when {
							blockRenderType == 11 ||
							block === Blocks.fence_gate ||
							blockRenderType == 32            -> return -3
							block === Blocks.trapdoor        -> return -4
							block.material !== Material.lava -> return 0
							!host.handleLavaMovement()       -> return -2
						}
					}
				}
			}
		}
		
		return if (foundSomeSemiBlockingShit) 2 else 1
	}
	
	/**
	 * Returns a new PathEntity for a given point
	 */
	private fun createEntityPath(end: PathPoint): PathEntity {
		var i = 1
		var start = end
		
		while (start.previous != null) {
			++i
			start = start.previous
		}
		
		val path = arrayOfNulls<PathPoint>(i)
		
		start = end
		path[--i] = end
		
		while (start.previous != null) {
			start = start.previous
			path[--i] = start
		}
		
		return PathEntity(path.map {
			if (it == null) return@map null
			
			PathPoint(it.xCoord + (island.posX + 0.5).mfloor(), it.yCoord + island.posY.mfloor(), it.zCoord + (island.posZ + 0.5).mfloor()).apply {
				index = it.index
				totalPathDistance = it.totalPathDistance
				distanceToNext = it.distanceToNext
				distanceToTarget = it.distanceToTarget
				previous = it.previous
				isFirst = it.isFirst
			}
		}.toTypedArray())
	}
	
	private val Number.x get() = this.D - (island.posX - 0.5)
	private val Number.y get() = this.D - island.posY
	private val Number.z get() = this.D - (island.posZ - 0.5)
}