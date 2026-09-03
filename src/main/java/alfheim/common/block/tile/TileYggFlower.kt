package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.TileImmobile
import alexsocol.asjlib.math.Vector3
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.item.material.ElvenResourcesMetas
import net.minecraft.entity.item.EntityItem
import net.minecraft.nbt.NBTTagCompound

class TileYggFlower: TileImmobile() {
	
	val hasFruit
		get() = xCoord == 2 && yCoord == 226 && zCoord == -45 && worldObj?.provider?.dimensionId == -105 && worldObj.totalWorldTime >= fruitRespawnTime
	
	var fruitRespawnTime = 0L
	
	override fun updateEntity() {
		super.updateEntity()
		
		if (!getBlockType().canBlockStay(worldObj, xCoord, yCoord, zCoord))
			worldObj.setBlockToAir(xCoord, yCoord, zCoord)
	}
	
	fun harvestFruit(): Boolean {
		if (!hasFruit || worldObj.isRemote) return false

		fruitRespawnTime = worldObj.totalWorldTime + ASJUtilities.randInBounds(AlfheimConfigHandler.yggdrasilFruitMinSpawnDelay, AlfheimConfigHandler.yggdrasilFruitMinSpawnDelay * 3, worldObj.rand)
		
		val (x, y, z) = Vector3.fromTileEntityCenter(this)
		val fruit = EntityItem(worldObj, x, y, z, ElvenResourcesMetas.YggFruit.stack)
		fruit.setMotion(0.0)
		fruit.spawn()
		
		return true
	}
	
	fun setup() {
		lock(xCoord, yCoord, zCoord, worldObj.provider.dimensionId)
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		super.writeCustomNBT(nbt)
		nbt.setLong(TAG_TIME, fruitRespawnTime)
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		super.readCustomNBT(nbt)
		fruitRespawnTime = nbt.getLong(TAG_TIME)
	}
	
	companion object {
		const val TAG_TIME = "fruitRespawnTime"
	}
}
