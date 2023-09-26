package alfheim.common.network.packet

import alfheim.api.network.AlfheimPacket
import alfheim.common.block.BlockComposite
import net.minecraft.entity.player.EntityPlayerMP

class MessageFuckedUpServerPrecision(var x: Int, var y: Int, var z: Int, var side: Int, var hitX: Float, var hitY: Float, var hitZ: Float): AlfheimPacket<MessageFuckedUpServerPrecision>() {
	
	override fun handleServer(player: EntityPlayerMP) {
		val world = player.worldObj
		val block = world.getBlock(x, y, z) as? BlockComposite ?: return
		
		val carver = player.heldItem ?: return
		val result = block.wrapCarving(world, x, y, z, player, carver, side, hitX, hitY, hitZ)
		
		if (result) player.swingItem()
	}
}
