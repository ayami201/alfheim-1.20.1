package alfheim.common.block.tile

import alexsocol.asjlib.ASJUtilities
import net.minecraft.nbt.NBTTagCompound

class TileComposite: TileDoubleCamo() {
	
	var size = 2
	var composition = initArray()
	
	private fun initArray() = Array(size) { Array(size) { Array(size) { true } } }
	
	override fun updateEntity() {
		super.updateEntity()
		
		if (worldObj.totalWorldTime % 20 == 0L && composition.all { s -> s.all { ss -> ss.none { it } } })
			worldObj.setBlockToAir(xCoord, yCoord, zCoord)
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		super.writeCustomNBT(nbt)
		
		nbt.removeTag(TAG_BLOCK_TOP)
		nbt.removeTag(TAG_BLOCK_TOP_META)
		
		nbt.setInteger(TAG_SIZE, size)
		for ((i, sub) in composition.withIndex())
			for ((j, subber) in sub.withIndex())
				for ((k, flag) in subber.withIndex())
					nbt.setBoolean("$i-$j-$k", flag)
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		super.readCustomNBT(nbt)
		
		size = nbt.getInteger(TAG_SIZE)
		composition = initArray()
		
		for (i in 0 until size)
			for (j in 0 until size)
				for (k in 0 until size) {
					val key = "$i-$j-$k"
					if (nbt.hasKey(key)) composition[i][j][k] = nbt.getBoolean(key)
				}
		
		if (ASJUtilities.isClient && worldObj?.getTileEntity(xCoord, yCoord, zCoord) === this) worldObj.markBlockForUpdate(xCoord, yCoord, zCoord)
	}
	
	companion object {
		const val TAG_SIZE = "size"
	}
}
