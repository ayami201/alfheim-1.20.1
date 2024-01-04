package alfheim.common.block.tile

import alexsocol.asjlib.*
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

class TileComposite: TileDoubleCamo() {
	
	var size = 2
	var composition = initArray()
	
	fun initArray(data: Pair<Block, Int>? = blockBottom to blockBottomMeta) = Array(size) { Array(size) { Array(size) { data } } }
	
	override fun updateEntity() {
		super.updateEntity()
		
		if (worldObj.totalWorldTime % 20 != 0L || !composition.all { s -> s.all { ss -> ss.all { it == null } } }) return
		
		noDrop = true
		worldObj.setBlockToAir(xCoord, yCoord, zCoord)
		
		if (!worldObj.isRemote) EntityItem(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, ItemStack(blockBottom, 1, blockBottomMeta)).spawn()
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		super.writeCustomNBT(nbt)
		
		nbt.removeTag(TAG_BLOCK_TOP)
		nbt.removeTag(TAG_BLOCK_TOP_META)
		
		nbt.setInteger(TAG_SIZE, size)
		for ((i, sub) in composition.withIndex())
			for ((j, subber) in sub.withIndex())
				for ((k, pair) in subber.withIndex()) {
					if (pair == null) {
						nbt.setString("$i-$j-$k", "-")
						continue
					}
					val (block, meta) = pair
					nbt.setString("$i-$j-$k", "${GameRegistry.findUniqueIdentifierFor(block)}:$meta")
				}
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		super.readCustomNBT(nbt)
		
		size = nbt.getInteger(TAG_SIZE)
		composition = initArray(composition[0][0][0])
		
		for (i in 0 until size)
			for (j in 0 until size)
				for (k in 0 until size) {
					val key = "$i-$j-$k"
					if (!nbt.hasKey(key)) continue
					
					val data = nbt.getString(key)
					if (data == "-") {
						composition[i][j][k] = null
						continue
					}
					
					val (modid, name, meta) = data.split(':')
					val block = Block.getBlockFromName("$modid:$name")
					
					if (block == null) {
						composition[i][j][k] = null
						continue
					}
					
					composition[i][j][k] = block to meta.toInt()
				}
		
		if (ASJUtilities.isClient && worldObj?.getTileEntity(xCoord, yCoord, zCoord) === this) worldObj.markBlockForUpdate(xCoord, yCoord, zCoord)
	}
	
	companion object {
		const val TAG_SIZE = "size"
	}
}
