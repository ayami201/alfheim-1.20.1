package alfheim.common.block.tile

import alexsocol.asjlib.extendables.block.ASJTile
import net.minecraft.nbt.NBTTagCompound

class TileGaiaButton: ASJTile() {
	
	var color = 0x0
	var delay = 10
	var name = ""
	
	override fun canUpdate() = false
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		nbt.setInteger("color", color)
		nbt.setInteger("delay", delay)
		nbt.setString("name", name)
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		color = nbt.getInteger("color")
		delay = nbt.getInteger("delay")
		name = nbt.getString("name")
	}
}
