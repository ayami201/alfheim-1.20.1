package alfheim.common.block.tile

import alexsocol.asjlib.ASJUtilities
import alfheim.common.block.AlfheimFluffBlocks
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.ForgeDirection.*

class TileCurtainPlacer @JvmOverloads constructor(meta: Int = 0): TileDoubleCamo() {
	
	var collidable = false
	var doing = false
	var down = false
	var prevRedstone = false
	var timer = 2
	
	init {
		blockBottom = Blocks.wool
		blockBottomMeta = if (meta != 0) 14 else 15
		blockTop = Blocks.wool
		blockTopMeta = 14
		if (meta != 0) locked = true
	}
	
	override fun updateEntity() {
		super.updateEntity()
		
		if (ASJUtilities.isClient || getBlockMetadata() != 0) return
		
		val rs = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)
		
		if (rs != prevRedstone && rs) {
			if (!doing) {
				down = !down
				doing = true
				timer = 2
			}
		}
		
		prevRedstone = rs
		
		if (doing && --timer == 0) {
			timer = 2
			
			if (down) {
				var y = yCoord
				
				while (y >= 0) {
					val at = worldObj.getBlock(xCoord, --y, zCoord)
					if (at == AlfheimFluffBlocks.curtainPlacer) continue
					if (y < 0 || !at.isAir(worldObj, xCoord, y, zCoord)) {
						doing = false
						break
					}
					
					worldObj.setBlock(xCoord, y, zCoord, AlfheimFluffBlocks.curtainPlacer, if (collidable) 2 else 1, 3)
					(worldObj.getTileEntity(xCoord, y, zCoord) as? TileCurtainPlacer)?.let {
						it.blockBottom = blockTop
						it.blockBottomMeta = blockTopMeta
						it.blockTop = blockTop
						it.blockTopMeta = blockTopMeta
						it.locked = true
						it.noDrop = true
					}
					
					if (y == yCoord - 1)
						for (d in arrayOf(NORTH, SOUTH, EAST, WEST))
							for (j in 0..1)
								(worldObj.getTileEntity(xCoord + d.offsetX, yCoord - j, zCoord + d.offsetZ) as? TileCurtainPlacer)?.let {
									it.doing = true
									it.down = down
									it.timer = 2
								}
					
					break
				}
			} else {
				var y = yCoord
				
				while (y >= 0) {
					val at = worldObj.getBlock(xCoord, --y, zCoord)
					if (at == AlfheimFluffBlocks.curtainPlacer) continue
					
					if ((y + 1) == yCoord) {
						doing = false
						break
					}
					
					worldObj.setBlockToAir(xCoord, y + 1, zCoord)
					
					for (d in arrayOf(NORTH, SOUTH, EAST, WEST))
						for (j in 0..1)
							(worldObj.getTileEntity(xCoord + d.offsetX, yCoord - j, zCoord + d.offsetZ) as? TileCurtainPlacer)?.let {
								it.doing = true
								it.down = false
								it.timer = 2
							}
					
					break
				}
			}
		}
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		super.writeCustomNBT(nbt)
		
		if (worldObj != null && getBlockMetadata() != 0) return
		
		nbt.setBoolean(TAG_COLLIDABLE, collidable)
		nbt.setBoolean(TAG_DOING, doing)
		nbt.setBoolean(TAG_DOWN, down)
		nbt.setBoolean(TAG_PREV_REDSTONE, prevRedstone)
		nbt.setInteger(TAG_TIMER, timer)
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		super.readCustomNBT(nbt)
		
		if (worldObj != null && getBlockMetadata() != 0) return
		
		collidable = nbt.getBoolean(TAG_COLLIDABLE)
		doing = nbt.getBoolean(TAG_DOING)
		down = nbt.getBoolean(TAG_DOWN)
		prevRedstone = nbt.getBoolean(TAG_PREV_REDSTONE)
		timer = nbt.getInteger(TAG_TIMER)
	}
	
	companion object {
		const val TAG_COLLIDABLE = "collidable"
		const val TAG_DOING = "doing"
		const val TAG_DOWN = "down"
		const val TAG_TIMER = "timer"
		const val TAG_PREV_REDSTONE = "prevRedstone"
	}
}