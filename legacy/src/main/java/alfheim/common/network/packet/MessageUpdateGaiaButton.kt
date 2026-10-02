package alfheim.common.network.packet

import alexsocol.asjlib.ASJUtilities
import alfheim.api.network.AlfheimPacket
import alfheim.common.block.tile.TileGaiaButton
import net.minecraft.entity.player.EntityPlayerMP

class MessageUpdateGaiaButton(var x: Int, var y: Int, var z: Int, var name: String): AlfheimPacket<MessageUpdateGaiaButton>() {
	
	override fun handleServer(player: EntityPlayerMP) {
		val button = player.worldObj.getTileEntity(x, y, z) as? TileGaiaButton ?: return
		button.name = name
		ASJUtilities.dispatchTEToNearbyPlayers(button)
	}
}