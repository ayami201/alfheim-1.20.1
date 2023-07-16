package alfheim.common.network.packet

import alexsocol.asjlib.ASJUtilities
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.network.ASJPacket
import alfheim.api.network.AlfheimPacket
import alfheim.common.block.tile.TileRaceSelector
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.MinecraftServer

class MessageRaceSelection(var doMeta: Boolean, var custom: Boolean, var female: Boolean, var give: Boolean, var meta: Int, var rot: Int, var arot: Int, var timer: Int, var x: Int, var y: Int, var z: Int, var dim: Int): ASJPacket(), AlfheimPacket<MessageRaceSelection> {
	override fun handleServer(player: EntityPlayerMP) {
		if (Vector3.vecEntityDistance(Vector3(x, y, z), player) > 5) return

		val world = MinecraftServer.getServer().worldServerForDimension(dim) ?: return
		val tile = world.getTileEntity(x, y, z) as? TileRaceSelector ?: return

		if (doMeta) {
			world.setBlockMetadataWithNotify(x, y, z, meta, 3)

			tile.custom = custom
			tile.female = female
		}

		tile.activeRotation = arot
		tile.rotation = rot
		tile.timer = timer

		if (give) tile.giveRaceAndReset(player)

		ASJUtilities.dispatchTEToNearbyPlayers(tile)
	}
}