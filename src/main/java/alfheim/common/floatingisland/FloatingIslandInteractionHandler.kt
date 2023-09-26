package alfheim.common.floatingisland

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.event.PlayerInteractAdequateEvent
import alfheim.api.event.PlayerInteractAdequateEvent.RightClick.Action.*
import alfheim.common.block.*
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Blocks
import net.minecraft.util.AxisAlignedBB

object FloatingIslandInteractionHandler {
	
	@SubscribeEvent
	fun onPlayerInteract(e: PlayerInteractAdequateEvent.RightClick) {
		val player = e.player as? EntityPlayerMP ?: return
		
		val island = when (e.action) {
			RIGHT_CLICK_AIR    -> player.worldObj.loadedEntityList.filterIsInstance<EntityFloatingIsland>().firstOrNull { it.boundingBox(it.collisionBorderSize).intersectsWith(player.boundingBox(player.collisionBorderSize)) } ?: return
			RIGHT_CLICK_ENTITY -> if (e.entity is EntityFloatingIsland) e.entity else return
			RIGHT_CLICK_BLOCK,
			RIGHT_CLICK_LIQUID -> return
		}
		
		val a = Vector3.fromEntity(player).add(0, player.eyeHeight, 0)
		val b = Vector3(player.lookVec).extend(player.theItemInWorldManager.blockReachDistance).add(a)
		val access = island.blockAccess
		
		var closest: Map.Entry<Triple<Int, Int, Int>, FloatingIslandBlockAccess.BlockData>? = null
		var minDistance = Double.POSITIVE_INFINITY
		
		access.blockMap.forEach {
			if (it.value.block === Blocks.air) return@forEach
			
			val (x, y, z) = it.key
			val aabb = it.value.block.getCollisionBoundingBoxFromPool(island.worldObj, x + island.posX.mfloor(), y + island.posY.mfloor(), z + island.posZ.mfloor())
				?.offset(island.posX % 1 + if (island.posX < 0) 0.5 else -0.5, island.posY % 1, island.posZ % 1 + if (island.posZ < 0) 0.5 else -0.5)
			
			if (aabb?.intersectsWithLine(a, b) != true) return@forEach
			
			val intersectionPoint = aabb.calculateIntersectionPoint(a, b) ?: return@forEach
			val distance = Vector3.vecDistance(a, intersectionPoint)
			
			if (distance >= minDistance) return@forEach
			
			minDistance = distance
			closest = it
		}
		
		val (coords, data) = closest ?: return
		if (data.block !== AlfheimBlocks.lootbox) return
		
		val (x, y, z) = coords
		val scam = BlockLootbox.isScambox(access, x, y, z)
		if (scam) {
			BlockLootbox.getScammed(player)
		} else
			BlockLootbox.getRewarded(player, BlockLootbox.getRarity(access, x, y, z))
		
		val (px, py, pz) = Vector3.fromEntity(island)
		BlockLootbox.hooray(player.worldObj, px + x, py + y + 0.5, pz + z, scam)
		
		access.setBlock(x, y, z, Blocks.air, sync = true)
	}
	
	fun AxisAlignedBB.intersectsWithLine(a: Vector3, b: Vector3): Boolean {
		return (a.x <= maxX || b.x <= maxX) && (a.x >= minX || b.x >= minX) && (a.y <= maxY || b.y <= maxY) && (a.y >= minY || b.y >= minY) && (a.z <= maxZ || b.z <= maxZ) && (a.z >= minZ || b.z >= minZ)
	}
	
	/**
	 * In this implementation, we use the slabs method to calculate the intersection of the line segment AB with each of the AABB's sides.
	 * We keep track of the tNear and tFar values to determine if there is a valid intersection.
	 * Finally, we compute the actual intersection point using the tNear value and return it.
	 *
	 * @return intersection point closest to [a], if any
	 * @author ChatGPT
	 */
	fun AxisAlignedBB.calculateIntersectionPoint(a: Vector3, b: Vector3): Vector3? {
		val intersectionPoint = Vector3()
		var tNear = Double.NEGATIVE_INFINITY
		var tFar = Double.POSITIVE_INFINITY
		var t1: Double
		var t2: Double
		
		// Check intersection with X planes
		if (a.x != b.x) {
			t1 = (minX - a.x) / (b.x - a.x)
			t2 = (maxX - a.x) / (b.x - a.x)
			if (t1 > t2) {
				val temp = t1
				t1 = t2
				t2 = temp
			}
			if (t1 > tNear) tNear = t1
			if (t2 < tFar) tFar = t2
			if (tNear > tFar) return null
			if (tFar < 0) return null
		} else {
			if (a.x < minX || a.x > maxX) return null
		}
		
		// Check intersection with Y planes
		if (a.y != b.y) {
			t1 = (minY - a.y) / (b.y - a.y)
			t2 = (maxY - a.y) / (b.y - a.y)
			if (t1 > t2) {
				val temp = t1
				t1 = t2
				t2 = temp
			}
			if (t1 > tNear) tNear = t1
			if (t2 < tFar) tFar = t2
			if (tNear > tFar) return null
			if (tFar < 0) return null
		} else {
			if (a.y < minY || a.y > maxY) return null
		}
		
		// Check intersection with Z planes
		if (a.z != b.z) {
			t1 = (minZ - a.z) / (b.z - a.z)
			t2 = (maxZ - a.z) / (b.z - a.z)
			if (t1 > t2) {
				val temp = t1
				t1 = t2
				t2 = temp
			}
			if (t1 > tNear) tNear = t1
			if (t2 < tFar) tFar = t2
			if (tNear > tFar) return null
			if (tFar < 0) return null
		} else {
			if (a.z < minZ || a.z > maxZ) return null
		}
		
		// Compute the intersection point
		intersectionPoint.x = a.x + tNear * (b.x - a.x)
		intersectionPoint.y = a.y + tNear * (b.y - a.y)
		intersectionPoint.z = a.z + tNear * (b.z - a.z)
		
		return intersectionPoint
	}
}