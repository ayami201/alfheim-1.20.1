package alfheim.common.network.packet

import alfheim.api.network.AlfheimPacket
import alfheim.common.core.helper.*
import alfheim.common.network.NetworkService
// PORT: EntityPlayerMP → ServerPlayer; MinecraftServer.getServer() → ServerLifecycleHooks; kickPlayerFromServer → disconnect
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraftforge.server.ServerLifecycleHooks
import org.apache.commons.io.FileUtils
import java.io.File

class MessageContributor(var key: String = "", var value: String = key, var isRequest: Boolean = false): AlfheimPacket<MessageContributor>() {
	
	override fun handleClient() {
		if (isRequest) {
			with(File("contributor.info")) {
				if (exists()) {
					val lines = FileUtils.readLines(this)
					NetworkService.sendToServer(MessageContributor(lines.getOrElse(0) { "login" }, HashHelper.hash(lines.getOrElse(1) { "password" })))
				}
			}
		} else {
			ContributorsPrivacyHelper.contributors[key] = value
		}
	}

	override fun handleServer(player: EntityPlayerMP) {
		// we are on server
		val username = player.gameProfile.name
		val passMatch = ContributorsPrivacyHelper.getPassHash(key)?.let { if (it.isBlank()) true else it == HashHelper.hash(value) } ?: false

		// are you the person you are saying you are ?
		if (ContributorsPrivacyHelper.isRegistered(username)) {
			// auth packet received - no hacking (probably)
			ContributorsPrivacyHelper.authTimeout.remove(player)

			if (key != username) {
				player.connection.disconnect(Component.literal("Invalid login provided, it must be equal to your username"))
				return
			}

			// yes, you are. Welcome!
			if (passMatch) {
				// --> proceed
			} else {
				// no, you not. Get out!
				player.connection.disconnect(Component.literal("Incorrect Alfhiem credentials for your contributor username"))
				return
			}
		} else {
			// so you are noname...

			// do you want to stay nobody ?
			if (key == "login" || value == HashHelper.hash("password"))
				return

			// ok, your new identity will be set
			if (passMatch) {
				// --> proceed
			} else {
				// incorrect password for identity
				player.connection.disconnect(Component.literal("Incorrect Alfheim contributor credentials"))
				return
			}
		}

		// --> proceeding here:

		// set contributor name alias to current username
		ContributorsPrivacyHelper.contributors[key] = username

		// tell everyone about new alias
		ServerLifecycleHooks.getCurrentServer()?.playerList?.players?.forEach {
			if (it is EntityPlayerMP)
				NetworkService.sendTo(MessageContributor(key, username), it)
		}

		// send all aliases to new player
		ContributorsPrivacyHelper.contributors.forEach { (k, v) -> NetworkService.sendTo(MessageContributor(k, v), player) }
	}
}