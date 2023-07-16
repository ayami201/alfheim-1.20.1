package alfheim.common.network.packet

import alexsocol.asjlib.mc
import alexsocol.asjlib.network.ASJPacket
import alfheim.api.network.AlfheimPacket
import alfheim.common.floatingisland.EntityFloatingIsland
import net.minecraft.block.Block
import net.minecraft.nbt.JsonToNBT
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity

class MessageFIBlock(var fiId: Int, var x: Int, var y: Int, var z: Int, var blockName: String, var meta: Int, var nbt: String): ASJPacket(), AlfheimPacket<MessageFIBlock> {
	
	override fun handleClient() {
		val island = mc.theWorld.getEntityByID(fiId) as? EntityFloatingIsland ?: return
		val block = Block.getBlockFromName(blockName) ?: return
		
		val tile = TileEntity.createAndLoadEntity(JsonToNBT.func_150315_a(nbt) as NBTTagCompound)
		
		island.blockAccess.setBlock(x, y, z, block, meta, tile)
		island.glCallList = -1
	}
}