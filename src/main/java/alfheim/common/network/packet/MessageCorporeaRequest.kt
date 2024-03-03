package alfheim.common.network.packet

import alfheim.api.network.AlfheimPacket
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraftforge.event.ServerChatEvent
import vazkii.botania.api.corporea.CorporeaHelper
import vazkii.botania.common.block.tile.corporea.TileCorporeaIndex

class MessageCorporeaRequest(var stack: ItemStack, var count: Int): AlfheimPacket<MessageCorporeaRequest>() {
	
	override fun handleServer(player: EntityPlayerMP) {
		TileCorporeaIndex.getInputHandler().onChatMessage(ServerChatEvent(player, "$count ${CorporeaHelper.stripControlCodes(stack.getDisplayName())}", null))
	}
}
