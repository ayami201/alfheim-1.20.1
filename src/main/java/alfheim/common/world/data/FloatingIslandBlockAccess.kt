package alfheim.common.world.data

import alexsocol.asjlib.*
import alfheim.common.entity.EntityFloatingIsland
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.*
import net.minecraftforge.common.util.ForgeDirection
import kotlin.math.*

class BlockElementBlockAccess(blocks: List<BlockElement>, val host: EntityFloatingIsland): IBlockAccess {
	
	var startX = Int.MAX_VALUE
	var startY = Int.MAX_VALUE
	var startZ = Int.MAX_VALUE
	var endX = Int.MIN_VALUE
	var endY = Int.MIN_VALUE
	var endZ = Int.MIN_VALUE
	
	val blockMap = HashMap<Triple<Int, Int, Int>, Triple<Block, Int, TileEntity?>>()
	
	private operator fun <T> HashMap<Triple<Int, Int, Int>, T>.get(x: Int, y: Int, z: Int) = get(x to y with z)
	private operator fun <T> HashMap<Triple<Int, Int, Int>, T>.set(x: Int, y: Int, z: Int, t: T) = put(x to y with z, t)
	
	init {
		blocks.forEach { e ->
			val block = Block.getBlockFromName(e.block) ?: return@forEach
			
			e.location.forEach { l ->
				blockMap[l.x, l.y, l.z] = block to l.meta with null
				
				startX = min(startX, l.x)
				startY = min(startY, l.y)
				startZ = min(startZ, l.z)
				endX = max(endX, l.x)
				endY = max(endY, l.y)
				endZ = max(endZ, l.z)
			}
		}
	}
	
	override fun getBlock(x: Int, y: Int, z: Int): Block = blockMap[x, y, z]?.first ?: Blocks.air
	
	override fun getBlockMetadata(x: Int, y: Int, z: Int) = blockMap[x, y, z]?.second ?: 0
	
	override fun getTileEntity(x: Int, y: Int, z: Int) = blockMap[x, y, z]?.third
	
	override fun isAirBlock(x: Int, y: Int, z: Int) = getBlock(x, y, z).isAir(this, x, y, z)
	
	override fun getLightBrightnessForSkyBlocks(x: Int, y: Int, z: Int, blockMin: Int): Int {
		val i1 = EnumSkyBlock.Sky.defaultLightValue
		var j1 = EnumSkyBlock.Block.defaultLightValue
		
		if (j1 < blockMin) j1 = blockMin
		
		return i1 shl 20 or (j1 shl 4)
	}
	
	override fun isBlockProvidingPowerTo(x: Int, y: Int, z: Int, side: Int) = getBlock(x, y, z).isProvidingStrongPower(this, x, y, z, side)
	
	override fun getBiomeGenForCoords(x: Int, z: Int) = host.worldObj.getBiomeGenForCoords(x + host.posX.mfloor(), z + host.posZ.mfloor())
	
	override fun getHeight() = 256
	
	override fun extendedLevelsInChunkCache() = false
	
	override fun isSideSolid(x: Int, y: Int, z: Int, side: ForgeDirection, default: Boolean) = getBlock(x, y, z).isSideSolid(this, x, y, z, side)
}