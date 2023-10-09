package alfheim.common.block.tile

import alexsocol.asjlib.extendables.block.ASJTile
import net.minecraft.nbt.NBTTagCompound

class TileGaiaButton: ASJTile() {
	
	var delay = 10
	
	override fun canUpdate() = false
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		nbt.setInteger("delay", delay)
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		delay = nbt.getInteger("delay")
	}
}
