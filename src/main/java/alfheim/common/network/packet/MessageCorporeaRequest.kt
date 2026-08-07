package alfheim.common.network.packet

import alfheim.api.network.AlfheimPacket
import alfheim.common.item.ItemCorporeaRat.RatInputHandler
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraftforge.event.ServerChatEvent
import vazkii.botania.api.corporea.CorporeaHelper
import vazkii.botania.common.block.tile.corporea.TileCorporeaIndex

class MessageCorporeaRequest(var stack: ItemStack, var count: Int, var viaQuandex: Boolean): AlfheimPacket<MessageCorporeaRequest>() {
	
	override fun handleServer(player: EntityPlayerMP) {
		val event = ServerChatEvent(player, "$count ${CorporeaHelper.stripControlCodes(stack.getDisplayName())}", null)
		
		if (viaQuandex)
			RatInputHandler.onChatMessage(event)
		else {
			TileCorporeaIndex.getInputHandler().onChatMessage(event)
		}
	}
}
