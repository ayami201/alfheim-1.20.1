package alfheim.common.block.tile

import alexsocol.asjlib.extendables.block.ASJTile
import net.minecraft.entity.EntityLivingBase

class TileFloodLight: ASJTile() {
	
	var redstone = false
	var target: EntityLivingBase? = null
	
	override fun updateEntity() {
		redstone = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)
		
		val newMeta = if (redstone) 1 else 0
		if (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) != newMeta) worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, newMeta, 3)
	}
	
	override fun getMaxRenderDistanceSquared() = 65536.0
	override fun getRenderBoundingBox() = INFINITE_EXTENT_AABB
}
