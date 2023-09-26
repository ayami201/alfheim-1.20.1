package alfheim.common.network.packet

import alexsocol.asjlib.extendables.block.TileItemContainer
import alexsocol.asjlib.mc
import alfheim.api.network.AlfheimPacket
import net.minecraft.item.ItemStack

class MessageTileItem(var x: Int, var y: Int, var z: Int, var s: ItemStack): AlfheimPacket<MessageTileItem>() {
	
	override fun handleClient() {
		val world = mc.theWorld
		val te = world.getTileEntity(x, y, z)
		if (te is TileItemContainer) te.item = s
	}
}