package alfheim.common.block.tile

import alexsocol.asjlib.extendables.block.ASJTile
import net.minecraft.entity.EntityLivingBase

class TileFloodLight: ASJTile() {
	
	var redstone = false
	var target: EntityLivingBase? = null
	
	override fun updateEntity() {
		redstone = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)
	}
	
	override fun getMaxRenderDistanceSquared() = 65536.0
	override fun getRenderBoundingBox() = INFINITE_EXTENT_AABB!!
}
