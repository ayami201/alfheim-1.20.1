package alfheim.common.core.helper

import alexsocol.asjlib.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.network.NetworkService
import alfheim.common.network.packet.MessageContributor
// PORT: события FML 1.7.10 → события Forge; EntityPlayer → Player, EntityPlayerMP → ServerPlayer;
// MinecraftServer.getServer() → ServerLifecycleHooks; kickPlayerFromServer → disconnect
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.server.ServerLifecycleHooks
import java.net.URL
import java.nio.charset.Charset
import java.security.*
import java.util.*

object ContributorsPrivacyHelper {
	
	//  contributor - username alias
	val contributors = HashMap<String, String>()
	private val authCredits = HashMap<String, String>()
	
	val auras = HashMap<String, String>()
	val shields = HashMap<String, Int>()
	val wings = HashMap<String, String>()
	
	init {
		eventFML()
		download()
	}
	
	private fun download() {
		try {
			connect("hashes.txt") { paired().forEach { (k, v) -> register(k, v) } }
		} catch (e: Throwable) {
			ASJUtilities.error("Failed to register contributors, using default parameters")
			// default username:password pairs just in case
			register("AlexSocol", "3A2DC92A7ACA6F8E94B834F6B0CF85AA0E73A01B3061EADEF061810BB1A96BD2")
			register("GedeonGrays", "B2612EA4C009B2C3FDDCAA7D6C1FFB8DD6C9C7ECFFD785DCD1A08BB41CAD47C0")
			register("KAIIIAK", "D761FAABD0C7F4042189C0CE308FDAD79566B198416BFDE23361EBA8DCB0BB96")
		}
		
		try {
			connect("auras.txt") { forEach { it.split(":").also { (k, v) -> auras[k] = v } } }
		} catch (e: Throwable) {
			ASJUtilities.error("Failed to register custom auras")
		}
		
		try {
			connect("patrons.txt") { forEach { it.split(":").also { (k, v) -> shields[k] = v.toIntOrNull() ?: 0 } } }
		} catch (e: Throwable) {
			ASJUtilities.error("Failed to register patrons")
		}
		
		try {
			connect("wings.txt") { forEach { it.split(":").also { (k, v) -> wings[k] = v } } }
		} catch (e: Throwable) {
			ASJUtilities.error("Failed to register custom wings")
		}
	}
	
	fun connect(file: String, action: List<String>.() -> Unit) {
		URL("https://bitbucket.org/AlexSocol/alfheim/raw/master/$file").openConnection().also { it.connectTimeout = 5000; it.readTimeout = 5000 }.getInputStream().bufferedReader().readLines().also { action(it) }
	}
	
	private fun register(contributor: String, passwordHash: String) {
		authCredits[contributor] = passwordHash
		
		if (ServerLifecycleHooks.getCurrentServer()?.isMultiPlayer != true)
			contributors[contributor] = contributor // no power on server if no response
	}
	
	fun isRegistered(login: String) = authCredits.contains(login)
	
	fun getPassHash(login: String) = authCredits[login]
	
	fun isCorrect(user: EntityPlayer, contributor: String) = isCorrect(user.gameProfile.name, contributor)
	
	fun isCorrect(user: String, contributor: String) = contributors[contributor] == user
	
	val authTimeout = WeakHashMap<EntityPlayerMP, Int>()
	
	@SubscribeEvent
	fun onPlayerTick(e: TickEvent.PlayerTickEvent) {
		if (ASJUtilities.isClient || e.phase != TickEvent.Phase.START) return
		
		val player = e.player as? EntityPlayerMP ?: return
		authTimeout[player]?.let {
			val time = it - 1
			
			if (time < 0)
				player.connection.disconnect(Component.literal("Authentication request timed out"))
			else
				authTimeout[player] = time
		}
	}
	
	@SubscribeEvent
	fun onPlayerLogin(e: PlayerEvent.PlayerLoggedInEvent) {
		val player = e.entity as? EntityPlayerMP ?: return
		
		if (ServerLifecycleHooks.getCurrentServer()?.isMultiPlayer == false) return

		NetworkService.sendTo(MessageContributor(isRequest = true), player)
		
		if (isRegistered(player.gameProfile.name))
			authTimeout[player] = AlfheimConfigHandler.authTimeout
	}
	
	@SubscribeEvent
	fun onPlayerLogout(e: PlayerEvent.PlayerLoggedOutEvent) {
		if (ServerLifecycleHooks.getCurrentServer()?.isMultiPlayer == false) return

		contributors.values.removeAll { it == e.entity.gameProfile.name }
	}
}

object HashHelper {
	
	fun hash(str: String?, salt: String = "soyeahthatsjustarandomuselesssecuritysaltthingsoyeah"): String {
		if (str != null)
			try {
				val md = MessageDigest.getInstance("SHA-256")
				// PORT: JAXB (HexBinaryAdapter) убран из Java 11; HexFormat даёт ту же запись заглавными буквами
				return HexFormat.of().withUpperCase().formatHex(md.digest(salt(str, salt).toByteArray(Charset.forName("UTF-8"))))
			} catch (e: NoSuchAlgorithmException) {
				ASJUtilities.error("Hashing error:", e)
			}
		
		return ""
	}
	
	// Might as well be called sugar given it's not secure at all :D
	fun salt(str: String, salt: String): String {
		val salted = str + salt
		val rand = Random(salted.length.toLong())
		val l = salted.length
		val steps = rand.nextInt(l)
		val chars = salted.toCharArray()
		for (i in 0 until steps) {
			val indA = rand.nextInt(l)
			var indB: Int
			do {
				indB = rand.nextInt(l)
			} while (indB == indA)
			val c = (chars[indA].code xor chars[indB].code).toChar()
			chars[indA] = c
		}
		
		return String(chars)
	}
}