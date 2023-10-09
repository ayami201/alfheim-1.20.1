package alfheim.common.floatingisland

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.patcher.event.ServerStoppedEvent
import alfheim.api.ModInfo
import alfheim.common.block.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.world.dim.alfheim.WorldProviderAlfheim
import alfheim.common.world.dim.alfheim.customgens.*
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.TickEvent
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.nbt.*
import net.minecraft.tileentity.TileEntity
import ru.vamig.worldengine.*
import vazkii.botania.common.block.ModBlocks
import kotlin.math.*
import kotlin.random.Random

object FloatingIslandGenerator {
	
	var genTimer = 0
	
	@SubscribeEvent
	fun onServerStop(e: ServerStoppedEvent) {
		genTimer = 0
	}
	
	@SubscribeEvent
	fun worldTickEvent(e: TickEvent.WorldTickEvent) {
		if (!ASJUtilities.isServer) return
		
		val world = e.world
		if (e.phase != TickEvent.Phase.END) return
		if (world.provider.dimensionId != AlfheimConfigHandler.dimensionIDAlfheim) return
		if (world.playerEntities.isEmpty()) return
		
		if (genTimer-- > 0) return
		
		val fis = world.loadedEntityList.filterIsInstance<EntityFloatingIsland>()
		if (fis.size >= AlfheimConfigHandler.floatingIslandCountMax) return
		if (fis.size >= AlfheimConfigHandler.floatingIslandCountPerPlayer * world.playerEntities.size) return
		
		val target = world.playerEntities.random(world.rand) as EntityPlayer
		val (x, _, z) = Vector3().rand().sub(0.5).mul(1, 0, 1).normalize().mul(world.rand.nextInt(200) + 300).add(target).I
		val j = WE_Biome.getBiomeAt((world.provider as WorldProviderAlfheim).chunkProvider, x, z).biomeSurfaceHeight
		val y = j + world.rand.nextInt(32) + 32
		
		if (Vector3.pointDistancePlane(x, z, 0, 0) < 1024) return // dist to Ygg
		if (fis.any { Vector3.pointDistancePlane(x, z, it.posX, it.posY) < 64 }) return // dist to other
		
		world.getBlock(x, y, z) // load chunk
		
		EntityFloatingIsland(world).apply {
			setPosition(x.D, y.D, z.D)
			val (mx, _, mz) = Vector3.fromEntity(target).sub(this).mul(1, 0, 1).normalize().mul(j / 10000.0).F
			setVelocity(mx, mz)
			generate(this)
			spawn()
			loadChunk()
		}
		
		genTimer = Short.MAX_VALUE - 1
	}
	
	fun generate(island: EntityFloatingIsland) {
		val world = island.blockAccess
		
		world.blockMap.clear()
		
		val seed = island.worldObj.rand.nextLong()
		val rand = Random(seed)
		
		val min = -4
		val max = 4
		
		val ix = min + rand.nextFloat() * (max - min)
		val iz = min + rand.nextFloat() * (max - min)
		
		generateSubstrate(world, ix, iz, rand)
		generateSurface(world, ix, iz, rand)
		
		island.readEntityFromNBT(NBTTagCompound().also { island.writeEntityToNBT(it) })
	}
	
	private fun hasBlock(x: Int, y: Int, z: Int, ix: Float, iz: Float) = YggdrasilGenerator.yobaFunction(x * 10, y - 1, z * 10, ix, 1f, iz)
	
	private fun generateSubstrate(world: FloatingIslandBlockAccess, ix: Float, iz: Float, rand: Random) {
		for (x in -64..64)
			for (z in -64..64)
				for (y in 15 downTo 0) {
					if (!hasBlock(x, y, z, ix, iz)) continue
					
					val (block, meta) = when (rand.nextInt(10)) {
						in 0..2 -> AlfheimFluffBlocks.livingMountain to 0
						in 3..5 -> AlfheimBlocks.livingcobble to 0
						in 6..7 -> AlfheimBlocks.livingcobble to 3
						else    -> ModBlocks.livingrock to 0
					}
					
					world.setBlock(x, y, z, block, meta)
				}
	}
	
	private fun generateSurface(world: FloatingIslandBlockAccess, ix: Float, iz: Float, rand: Random) {
		val seed = rand.nextLong()
		
		val scaleXZ = 10.0
		val scaleY = 8
		
		for (x in -64..64)
			for (z in -64..64) {
				val maxY = max(16, WE_PerlinNoise.PerlinNoise2D(seed, x / scaleXZ, z / scaleXZ, 1.0, 1).times(scaleY).I + 18)
				
				for (y in 16..maxY)
					if (hasBlock(x, y, z, ix, iz) && world.isAirBlock(x, y, z))
						world.setBlock(x, y, z, if (y == maxY) Blocks.grass else Blocks.dirt)
			}
		
		val isForest = rand.nextBoolean()
		val hasStructure = rand.nextInt(100) < if (isForest) 50 else 90
		val hasPool = hasStructure && rand.nextInt(100) < 40
		if (hasPool) genStructure(world, rand, pools.random(rand))
		
		for (x in -64..64)
			for (z in -64..64)
				for (y in 16..world.endY) {
					if (world.getBlock(x, y, z) != Blocks.grass) continue
					
					genTrees(world, x, y + 1, z, rand, isForest)
					break
				}
		
		if (!hasPool && hasStructure) {
			val id = ruins.indices.random(rand)
			val (x, y, z) = genStructure(world, rand, ruins[id]).I
			
			lootboxes[id]?.forEach { (i, j, k) ->
				val rarity = when(rand.nextInt(100) + 1) {
					in 1..10 -> 0 // scambox
					in 11..35 -> 2 // uncommon
					in 36..40 -> 3 // rare
					else -> 1 // common
				} * 3
				
				world.setBlock(x + i, y + j, z + k, AlfheimBlocks.lootbox, rarity + (lootboxTypes[id] ?: 0))
			}
		}
		
		for (x in -64..64)
			for (z in -64..64)
				for (y in 16..world.endY) {
					if (world.getBlock(x, y, z) != Blocks.grass) continue
					
					genGrassAndFlowers(world, x, y + 1, z, rand)
					break
				}
	}
	
	fun genTrees(world: FloatingIslandBlockAccess, x: Int, y: Int, z: Int, rand: Random, isForest: Boolean) {
		if (rand.nextFloat() > if (isForest) 0.015 else 0.001) return
		
		if (isForest && rand.nextFloat() < 0.15) {
			(if (rand.nextFloat() < 0.33) dreamTreeSchema else sadOakSchema).forEach { e ->
				val block = Block.getBlockFromName(e.block)
				e.location.forEach l@ { l ->
					val Y =  y + l.y - 1
					if (block.isLeaves(world, x + l.x, Y, z + l.z) && !world.getBlock(x + l.x, Y, z + l.z).let { it.isAir(world, x + l.x, Y, z + l.z) || it.isLeaves(world, x + l.x, Y, z + l.z) })
						return@l
					
					world.setBlock(x + l.x, Y, z + l.z, block, l.meta)
				}
			}
			
			return
		}
		
		val height = rand.nextInt(5, 7)
		
		val type = if (isForest) -1 else rand.nextInt(24)
		
		val log = when (type) {
			in 0..3 -> AlfheimBlocks.irisWood0
			in 4..7 -> AlfheimBlocks.irisWood1
			in 8..11 -> AlfheimBlocks.irisWood2
			in 12..15 -> AlfheimBlocks.irisWood3
			16 -> AlfheimBlocks.rainbowWood
			17 -> AlfheimBlocks.auroraWood
			in 18..21 -> AlfheimBlocks.altWood0
			in 22..23 -> AlfheimBlocks.altWood1
			else -> Blocks.log
		}
		
		val logMeta = when (type) {
			in 0..15 -> type % 4
			in 18..21 -> type - 18
			in 22..23 -> type - 22
			else -> 0
		}
		
		val leaves = when (type) {
			in 0..7 -> AlfheimBlocks.irisLeaves0
			in 8..15 -> AlfheimBlocks.irisLeaves1
			16 -> AlfheimBlocks.rainbowLeaves
			17 -> AlfheimBlocks.auroraLeaves
			in 18..23 -> AlfheimBlocks.altLeaves
			else -> Blocks.leaves
		}
		
		val leavesMeta = when (type) {
			in 0..15 -> type % 8
			in 18..23 -> type - 18
			else -> 0
		}
		
		for (j in height downTo height - 3) {
			val range = if (j > height - 2) -1..1 else -2..2
			
			for (i in range)
				for (k in range) {
					if (abs(i) == range.last && abs(k) == range.last) continue
					
					if (!world.getBlock(x + i, y + j, z + k).let { it.isAir(world, x + i, y + j, z + k) || it.isLeaves(world, x + i, y + j, z + k) }) continue
					
					world.setBlock(x + i, y + j, z + k, leaves, leavesMeta)
				}
		}
		
		run {
			dynamicLeaves.forEach outer@ { (coords, rangeY) ->
				coords.forEach { (i, k) ->
					if (rand.nextBoolean()) return@forEach
					
					for (j in rangeY) {
						if (rand.nextBoolean()) return@forEach
						
						if (!world.getBlock(x + i, y + j + height, z + k).let { it.isAir(world, x + i, y + j + height, z + k) || it.isLeaves(world, x + i, y + j + height, z + k) }) return@forEach
						
						world.setBlock(x + i, y + j + height, z + k, leaves, leavesMeta)
					}
				}
			}
		}
		
		for (j in 0 until height)
			world.setBlock(x, y + j, z, log, logMeta)
		
		if (isForest) return
		
		val grass = when (type) {
			in 0..15 -> AlfheimBlocks.irisDirt
			16 -> AlfheimBlocks.rainbowDirt
			17 -> AlfheimBlocks.auroraDirt
			in 18..23 -> ModBlocks.altGrass
			else -> Blocks.grass
		}
		
		val grassMeta = when (type) {
			in 0..15 -> type
			in 18..23 -> type - 18
			else -> 0
		}
		
		for (i in -2..2)
			for (k in -2..2) {
				if (abs(i) == 2 && abs(k) == 2) continue
				
				for (j in 0.bidiRange(height)) {
					if (world.getBlock(x + i, y + j, z + k) !== Blocks.grass) continue
					
					world.setBlock(x + i, y + j, z + k, grass, grassMeta)
					break
				}
			}
		
		return
	}
	
	fun genGrassAndFlowers(world: FloatingIslandBlockAccess, x: Int, y: Int, z: Int, rand: Random) {
		if (rand.nextFloat() > 0.4) return
		if (!world.isAirBlock(x, y, z)) return
		if (world.getBlock(x, y - 1, z) !== Blocks.grass) return
		
		val type = rand.nextInt(16)
		
		run {
			if (type > 12) return@run
			
			val metas = byteArrayOf(0, 0, 0, 0, -1, 0, 2, 1, 1, 1, 1, 2, -1)
			
			when (type) {
				4  -> metas[4] = (rand.nextInt(8) + 1).toByte()
				11 -> {
					if (!world.getBlock(x, y + 1, z).let { it.isAir(world, x, y + 1, z) || it.isLeaves(world, x, y + 1, z) }) return@run
					world.setBlock(x, y + 1, z, WorldGenGrass.types[11], metas[11] + 8)
				}
				12 -> {
					metas[12] = rand.nextInt(6).toByte()
					if (metas[12].I == 2) return@run
					if (!world.getBlock(x, y + 1, z).let { it.isAir(world, x, y + 1, z) || it.isLeaves(world, x, y + 1, z) }) return@run
					world.setBlock(x, y + 1, z, WorldGenGrass.types[12], metas[12] + 8)
				}
			}
			
			world.setBlock(x, y, z, WorldGenGrass.types[type], metas[type].I)
			
			return
		}
		
		val (block, meta) = run {
			val meta = rand.nextInt(17)
			
			if (type == 16) {
				val result = if (meta == 16) AlfheimBlocks.rainbowTallFlower to 0 else (if (meta > 7) ModBlocks.doubleFlower2 else ModBlocks.doubleFlower1) to meta % 8
				if (!world.getBlock(x, y + 1, z).let { it.isAir(world, x, y + 1, z) || it.isLeaves(world, x, y + 1, z) }) return
				world.setBlock(x, y + 1, z, result.first, result.second)
				result
			} else
				if (meta == 16) AlfheimBlocks.rainbowGrass to 2 else ModBlocks.flower to meta
		}
		
		world.setBlock(x, y, z, block, meta)
		
		return
	}
	
	private fun genStructure(world: FloatingIslandBlockAccess, rand: Random, structure: List<BlockElement>): Vector3 {
		val (x, _, z) = Vector3(rand.nextInt(16), 0, rand.nextInt(16)).sub(8).I
		val y = ((world.endY downTo 16).firstOrNull {
			val block = world.getBlock(x, it, z)
			!ASJUtilities.isBlockReplaceable(block) && !block.isWood(world, x, it, z)
		} ?: 16) + 1
		
		structure.forEach { e ->
			val block = Block.getBlockFromName(e.block)
			e.location.forEach { l ->
				val Y = y + l.y
				val tile = if (l.nbt != null) TileEntity.createAndLoadEntity(JsonToNBT.func_150315_a(l.nbt) as NBTTagCompound).apply {
					xCoord = x + l.x
					yCoord = Y
					zCoord = z + l.z
				} else null
				
				world.setBlock(x + l.x, Y, z + l.z, block, l.meta, tile)
			}
		}
		
		return Vector3(x, y, z)
	}
	
	private val pools = Array(7) { SchemaUtils.parse(SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/fi/pool$it")) }.toList()
	private val ruins = Array(12) { SchemaUtils.parse(SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/fi/ruin$it")) }.toList()
	
	private val lootboxes = hashMapOf(
		0 to listOf(0 to 0 with 0),
		2 to listOf(0 to 3 with 0),
		3 to listOf(
			0 to 0 with 0,
			0 to 7 with 0,
		),
		8 to listOf(
			-2 to 0 with 1,
			1 to 3 with -1,
		),
		9 to listOf(1 to 0 with 0),
		10 to listOf(
			-5 to 1 with 2,
			1 to 1 with 5,
			3 to 1 with -4,
		),
		11 to listOf(
			-3 to 3 with 0,
			0 to 3 with -3,
			0 to 3 with 3,
			3 to 3 with 0,
		)
	)
	
	private val lootboxTypes = mapOf(0 to 0, 2 to 0, 3 to 0, 8 to 0, 9 to 0, 10 to 0, 11 to 1)
	
	private val dreamTreeSchema = SchemaUtils.parse(SchemaUtils.loadStructure("${ModInfo.MODID}/schemas/fi/DreamTree"))
	
	private val sadOakSchema = dreamTreeSchema.map { old ->
		val leaves = old.block.endsWith("Leaves")
		BlockElement(if (leaves) "minecraft:leaves" else "minecraft:log", old.location.map {
			LocationElement(it.x, it.y, it.z, if (leaves) 0 else it.meta - 3, null)
		})
	}
	
	private val dynamicLeaves = arrayOf(arrayOf(-2 to -2, 2 to -2, -2 to 2, 2 to 2) to -3..-2, arrayOf(-1 to -1, 1 to -1, -1 to 1, 1 to 1) to -1..0)
}