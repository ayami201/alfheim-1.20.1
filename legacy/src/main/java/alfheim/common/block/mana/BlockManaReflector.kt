package alfheim.common.block.mana

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.patcher.event.EntityUpdateEvent
import alfheim.api.lib.LibRenderIDs
import alfheim.common.block.base.BlockContainerMod
import alfheim.common.block.tile.TileManaReflector
import alfheim.common.item.block.ItemBlockManaReflector
import alfheim.common.lexicon.AlfheimLexiconData
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.item.ItemStack
import net.minecraft.util.ChunkCoordinates
import net.minecraft.world.World
import vazkii.botania.api.internal.IManaBurst
import vazkii.botania.api.lexicon.ILexiconable
import vazkii.botania.api.mana.IManaTrigger
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.entity.EntityManaBurst

class BlockManaReflector: BlockContainerMod(Material.wood), IManaTrigger, ILexiconable {
	
	init {
		setBlockName("ManaReflector")
		setHardness(1f)
		setHarvestLevel("axe", 0)
		setStepSound(soundTypeWood)
		
		GameRegistry.registerBlock(this, ItemBlockManaReflector::class.java, "ManaReflector")
	}
	
	override fun onBurstCollision(burst: IManaBurst, world: World, x: Int, y: Int, z: Int) {
		burst as EntityManaBurst
		
		if (burst.isInReflector(x, y, z)) return
		
		val v = Vector3(burst.motionX, burst.motionY, burst.motionZ)
		val n = normals[world.getBlockMetadata(x, y, z)].copy()
		
		if (!isRayValid(v, n)) return burst.setDead()
		
		val p = Vector3.fromEntity(burst)
		
		val i = intersectLineWithPlane(p, v, n.copy().negate(), Vector3(x, y, z).add(0.5)) ?: return
		
		burst.setPosition(i.x, i.y, i.z)
		burst.particles()
		
		burst.prevPosX = i.x
		burst.prevPosY = i.y
		burst.prevPosZ = i.z
		
		val (rX, rY, rZ) = reflect(v, n)
		burst.setMotion(rX, rY, rZ)
		
		burst.setInReflector(x, y, z)
	}
	
	override fun getEntry(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?, lexicon: ItemStack?) = AlfheimLexiconData.manaReflector
	override fun registerBlockIcons(reg: IIconRegister) = Unit
	override fun getIcon(side: Int, meta: Int) = ModBlocks.dreamwood.getIcon(0, 0)!!
	override fun createNewTileEntity(world: World?, meta: Int) = TileManaReflector() // fuck you Vazkii
	override fun shouldRegisterInNameSet() = false
	override fun isOpaqueCube() = false
	override fun renderAsNormalBlock() = false
	override fun getRenderType() = LibRenderIDs.idManaReflector
	
	companion object {
		
		private const val TAG_REFLECTOR = "reflector"
		
		val normals = arrayOf(
			Vector3(0, 1, 1).normalize().negate(),
			Vector3(-1, 1, 0).normalize().negate(),
			Vector3(0, 1, -1).normalize().negate(),
			Vector3(1, 1, 0).normalize().negate(),
			Vector3(0, -1, 1).normalize().negate(),
			Vector3(-1, -1, 0).normalize().negate(),
			Vector3(0, -1, -1).normalize().negate(),
			Vector3(1, -1, 0).normalize().negate(),
			Vector3(-1, 0, 1).normalize().negate(),
			Vector3(-1, 0, -1).normalize().negate(),
			Vector3(1, 0, -1).normalize().negate(),
			Vector3(1, 0, 1).normalize().negate(),
		)
		
		init {
			eventForge()
		}
		
		private fun intersectLineWithPlane(p: Vector3, v: Vector3, n: Vector3, a: Vector3): Vector3? {
			val denominator = n.dot(v)
			
			// Если знаменатель равен нулю, значит прямая параллельна плоскости и пересечения нет
			if (denominator == 0.0) return null
			
			// Вычисляем числитель с учетом точки a на плоскости
			val numerator = n.dot(a - p)
			
			// Находим параметр t
			val t = numerator / denominator
			
			// Возвращаем точку пересечения
			return p + (v * t)
		}
		
		private fun reflect(incident: Vector3, normal: Vector3): Vector3 {
			val dotProduct = incident dot normal
			return incident - (normal * (2 * dotProduct))
		}
		
		private infix fun Vector3.dot(other: Vector3): Double {
			return x * other.x + y * other.y + z * other.z
		}
		
		private operator fun Vector3.plus(other: Vector3): Vector3 =
			Vector3(x + other.x, y + other.y, z + other.z)
		
		private operator fun Vector3.minus(other: Vector3): Vector3 {
			return Vector3(x - other.x, y - other.y, z - other.z)
		}
		
		private operator fun Vector3.times(scalar: Double): Vector3 {
			return Vector3(x * scalar, y * scalar, z * scalar)
		}
		
		private fun isRayValid(incident: Vector3, normal: Vector3): Boolean {
			return incident dot normal > 0
		}
		
		private fun IManaBurst.setInReflector(x: Int, y: Int, z: Int) {
			this as EntityThrowable
			entityData.setChunkCoords(TAG_REFLECTOR, ChunkCoordinates(x, y, z))
		}
		
		private fun IManaBurst.isInReflector(x: Int, y: Int, z: Int): Boolean {
			this as EntityThrowable
			
			val mirror = entityData.getChunkCoords(TAG_REFLECTOR)
			return mirror == ChunkCoordinates(x, y, z)
		}
		
		@SubscribeEvent
		fun clearMirrorData(e: EntityUpdateEvent) {
			val burst = e.entity as? IManaBurst ?: return
			if (burst !is EntityThrowable) return
			
			if (!burst.entityData.hasKey(TAG_REFLECTOR)) return
			
			val (x, y, z) = Vector3.fromEntity(burst).mf()
			val pos = ChunkCoordinates(x, y, z)
			val mirror = burst.entityData.getChunkCoords(TAG_REFLECTOR)
			
			if (pos != mirror) burst.entityData.removeTag(TAG_REFLECTOR)
		}
	}
}
