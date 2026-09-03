package alfheim.common.block.tile

import alexsocol.asjlib.*
import cpw.mods.fml.common.registry.*
import net.minecraft.init.*
import net.minecraft.nbt.*
import net.minecraft.util.*
import net.minecraftforge.common.util.*
import vazkii.botania.common.block.tile.string.*

class TileRedStringWatcher: TileRedString() {
	
	var blockAt = Blocks.air!!
	var metaAt = 0
	var rsFromAss = false
	
	override fun acceptBlock(x: Int, y: Int, z: Int) = !worldObj.isAirBlock(x, y, z)
	
	override fun updateEntity() {
		if (!ASJUtilities.isServer) return
		
		val dir = getOrientation()
		var x = xCoord
		var y = yCoord
		var z = zCoord
		val range = getRange()
		val currBinding = binding
		
		for (i in 0..<range) {
			x += dir.offsetX
			y += dir.offsetY
			z += dir.offsetZ
			
			if (!acceptBlock(x, y, z)) continue
			
			binding = ChunkCoordinates(x, y, z)
			break
		}
		
		val newBlock = getBlockAtBinding()
		val newMeta = getMetaAtBinding()
		
		if (getBlockMetadata() and 8 != 0) {
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, blockMetadata and 7, 3)
			return
		}
		
		if (blockAt == newBlock && metaAt == newMeta && currBinding == binding) return
		
		blockAt = newBlock
		metaAt = newMeta
		
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, getBlockMetadata() or 8, 3)
	}
	
	fun getMetaAtBinding(): Int {
		val (x, y, z) = binding ?: return 0
		return worldObj.getBlockMetadata(x, y, z)
	}
	
	override fun getOrientation() = ForgeDirection.getOrientation(getBlockMetadata() and 7)!!
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		nbt.setString("blockAt", GameRegistry.findUniqueIdentifierFor(blockAt).toString())
		nbt.setInteger("metaAt", metaAt)
		nbt.setBoolean("rsFromAss", rsFromAss)
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		if (nbt.hasKey("blockAt")) {
			val (modid, name) = nbt.getString("blockAt").split(':')
			blockAt = GameRegistry.findBlock(modid, name)
		}
		
		metaAt = nbt.getInteger("metaAt")
		rsFromAss = nbt.getBoolean("rsFromAss")
	}
}
