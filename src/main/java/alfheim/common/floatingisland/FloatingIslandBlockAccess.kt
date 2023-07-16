package alfheim.common.floatingisland

import alexsocol.asjlib.*
import alfheim.common.network.NetworkService
import alfheim.common.network.packet.*
import alfheim.common.world.dim.alfheim.biome.BiomeField
import com.google.gson.*
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.nbt.*
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.*
import net.minecraft.world.biome.BiomeGenBase
import net.minecraftforge.common.util.ForgeDirection
import java.util.concurrent.*
import kotlin.math.*

class FloatingIslandBlockAccess(blocks: List<BlockElement>, val host: EntityFloatingIsland): IBlockAccess {
	
	var startX = Int.MAX_VALUE
	var startY = Int.MAX_VALUE
	var startZ = Int.MAX_VALUE
	var endX = Int.MIN_VALUE
	var endY = Int.MIN_VALUE
	var endZ = Int.MIN_VALUE
	
	val blockMap: MutableMap<Triple<Int, Int, Int>, BlockData> = ConcurrentHashMap()
	val tileList: MutableMap<Triple<Int, Int, Int>, TileEntity> = HashMap()
	
	private operator fun <T> Map<Triple<Int, Int, Int>, T>.get(x: Int, y: Int, z: Int) = get(x to y with z)
	private operator fun <T> MutableMap<Triple<Int, Int, Int>, T>.set(x: Int, y: Int, z: Int, bd: T) = put(x to y with z, bd)
	
	private val executor: ExecutorService?
	private val future: Future<*>?
	
	init {
		blocks.forEach { e ->
			val block = Block.getBlockFromName(e.block) ?: return@forEach
			
			if (block === Blocks.air) return@forEach
			
			e.location.forEach { l ->
				val tile = l.nbt?.let { TileEntity.createAndLoadEntity(JsonToNBT.func_150315_a(it) as NBTTagCompound)?.apply {
					xCoord = l.x
					yCoord = l.y
					zCoord = l.z
					blockType = block
					blockMetadata = l.meta
					tileList[l.x, l.y, l.z] = this
				} }
				
				blockMap[l.x, l.y, l.z] = BlockData(block, l.meta, tile, -1, block.lightValue)
				
				startX = min(startX, l.x)
				startY = min(startY, l.y)
				startZ = min(startZ, l.z)
				endX = max(endX, l.x)
				endY = max(endY, l.y)
				endZ = max(endZ, l.z)
			}
		}
		
		if (ASJUtilities.isClient) {
			executor = Executors.newSingleThreadExecutor()
			future = executor.submit { recalculateLight() }
		} else {
			executor = null
			future = null
		}
	}
	
	private fun recalculateLight() {
		val glowingBlocks = HashMap<Triple<Int, Int, Int>, Int>()
		
		blockMap.forEach { (coords, data) ->
			val (x, y, z) = coords
			data.preLightValue = data.block.getLightValue(this, x, y, z)
			if (data.preLightValue > 0) glowingBlocks[coords] = data.preLightValue
		}
		
//		val startTime = System.currentTimeMillis()
		
		glowingBlocks.forEach { (coords, light) ->
			val (x, y, z) = coords
			spreadLight(x, y, z, light)
		}
		
//		val endTime = System.currentTimeMillis() - startTime
//		ASJUtilities.chatLog("Finished in $endTime ms")
	}
	
	private fun spreadLight(x: Int, y: Int, z: Int, lightValue: Int) {
		if (lightValue <= 0) return
		
		val data = blockMap[x, y, z] ?: BlockData(Blocks.air, 0, null, -1, 0)

		val newLight = lightValue - data.block.getLightOpacity(this, x, y, z) / 17
		if (data.lightValue > newLight) return

		data.lightValue = max(data.preLightValue, newLight)
		blockMap[x, y, z] = data

		spreadLight(x - 1, y, z, data.lightValue - 1)
		spreadLight(x + 1, y, z, data.lightValue - 1)
		spreadLight(x, y - 1, z, data.lightValue - 1)
		spreadLight(x, y + 1, z, data.lightValue - 1)
		spreadLight(x, y, z - 1, data.lightValue - 1)
		spreadLight(x, y, z + 1, data.lightValue - 1)
	}
	
	fun tickLight() {
		if (executor?.isTerminated == true) return
		if (future?.isDone != true) return
		host.glCallList = -1
		executor?.shutdown()
	}
	
	fun toSchema(): String {
		if (blockMap.isEmpty()) return ""
		
		val elements = HashMap<String, MutableList<LocationElement>>()
		
		blockMap.forEach { (coords, blockdata) ->
			val (x, y, z) = coords
			val (block, meta, tile) = blockdata
			if (block === Blocks.air) return@forEach
			val nbt = if (tile != null) NBTTagCompound().also { tile.writeToNBT(it) }.toString() else null
			elements.computeIfAbsent(GameRegistry.findUniqueIdentifierFor(block).toString()) { mutableListOf() }.add(LocationElement(x, y, z, meta, nbt))
		}
		
		return JsonArray().apply {
			for (block in elements.keys) {
				add(JsonObject().apply {
					addProperty("block", block)
					add("location", JsonArray().apply {
						for (v in elements[block].orEmpty())
							add(v.getJson())
					})
				})
			}
		}.toString()
	}
	
	private fun LocationElement.getJson(): JsonObject = JsonObject().apply {
		if (x != 0) addProperty("x", x)
		if (y != 0) addProperty("y", y)
		if (z != 0) addProperty("z", z)
		if (meta != 0) addProperty("meta", meta)
		if (nbt != null) addProperty("nbt", nbt.toString())
	}
	
	fun setBlock(x: Int, y: Int, z: Int, block: Block, meta: Int = 0, tile: TileEntity? = null, sync: Boolean = false) {
		if (sync && ASJUtilities.isServer)
			NetworkService.sendToDim(
				MessageFIBlock(
					host.entityId,
					x, y, z,
					GameRegistry.findUniqueIdentifierFor(block).toString(),
					meta,
					NBTTagCompound().apply { tile?.writeToNBT(this) }.toString()
				),
				host.worldObj.provider.dimensionId
			)
		
		if (block === Blocks.air) {
			blockMap.remove(x to y with z)
			return
		}
		
		val oldLight = blockMap[x, y, z]?.lightValue ?: 0
		blockMap[x, y, z] = BlockData(block, meta, tile, oldLight, oldLight)
		
		if (tile == null)
			tileList.remove(x to y with z)
		else
			tileList[x, y, z] = tile
		
		startX = min(startX, x)
		startY = min(startY, y)
		startZ = min(startZ, z)
		endX = max(endX, x)
		endY = max(endY, y)
		endZ = max(endZ, z)
	}
	
	override fun getBlock(x: Int, y: Int, z: Int): Block = blockMap[x, y, z]?.block ?: Blocks.air
	
	override fun getBlockMetadata(x: Int, y: Int, z: Int) = blockMap[x, y, z]?.meta ?: 0
	
	override fun getTileEntity(x: Int, y: Int, z: Int) = blockMap[x, y, z]?.tile
	
	override fun isAirBlock(x: Int, y: Int, z: Int) = getBlock(x, y, z).isAir(this, x, y, z)
	
	override fun getLightBrightnessForSkyBlocks(x: Int, y: Int, z: Int, blockMin: Int): Int {
		val i1 = EnumSkyBlock.Sky.defaultLightValue
		var j1 = blockMap[x, y, z]?.lightValue ?: 0
		
		if (blockMap[x, y, z]?.block?.useNeighborBrightness == true) {
			val nb = arrayOf(
				blockMap[x - 1, y, z]?.lightValue ?: 0,
				blockMap[x + 1, y, z]?.lightValue ?: 0,
				blockMap[x, y - 1, z]?.lightValue ?: 0,
				blockMap[x, y + 1, z]?.lightValue ?: 0,
				blockMap[x, y, z - 1]?.lightValue ?: 0,
				blockMap[x, y, z + 1]?.lightValue ?: 0,
			).max() - 1
			
			j1 = max(j1, nb)
		}
		
		j1 = max(blockMin, j1)
		
		return i1 shl 20 or (j1 shl 4)
	}
	
	override fun isBlockProvidingPowerTo(x: Int, y: Int, z: Int, side: Int) = getBlock(x, y, z).isProvidingStrongPower(this, x, y, z, side)
	
	override fun getBiomeGenForCoords(x: Int, z: Int): BiomeGenBase = BiomeField // host.worldObj.getBiomeGenForCoords(x + host.posX.mfloor(), z + host.posZ.mfloor())
	
	override fun getHeight() = endY - startY
	
	override fun extendedLevelsInChunkCache() = false
	
	override fun isSideSolid(x: Int, y: Int, z: Int, side: ForgeDirection, default: Boolean) = getBlock(x, y, z).isSideSolid(this, x, y, z, side)
	
	protected fun finalize() {
		executor?.shutdownNow()
	}
	
	data class BlockData(var block: Block, var meta: Int, var tile: TileEntity?, var lightValue: Int, var preLightValue: Int)
}