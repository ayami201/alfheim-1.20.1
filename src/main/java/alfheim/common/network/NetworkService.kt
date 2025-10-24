package alfheim.common.network

import alexsocol.asjlib.ASJUtilities
import alfheim.api.ModInfo
import alfheim.api.network.AlfheimPacket
import alfheim.common.network.packet.*
import cpw.mods.fml.common.network.NetworkRegistry
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.MinecraftServer
import kotlin.reflect.KClass

object NetworkService {
	
	private val network: SimpleNetworkWrapper = SimpleNetworkWrapper(ModInfo.MODID)
	
	private var nextPacketID = 0
	
	init {
		register()
	}
	
	private fun register() {
		registerPacket(Message0dC::class, Side.CLIENT)
		registerPacket(Message1d::class, Side.CLIENT)
		registerPacket(Message1l::class, Side.CLIENT)
		registerPacket(Message2d::class, Side.CLIENT)
		registerPacket(Message3d::class, Side.CLIENT)
		registerPacket(MessageNI::class, Side.CLIENT)
		
		registerPacket(MessageContributor::class, Side.CLIENT)
		registerPacket(MessageDisguise::class, Side.CLIENT)
		registerPacket(MessageEffect::class, Side.CLIENT)
		registerPacket(MessageFIBlock::class, Side.CLIENT)
		registerPacket(MessageGleipnirLeash::class, Side.CLIENT)
		registerPacket(MessageHotSpellC::class, Side.CLIENT)
		registerPacket(MessageOrgans::class, Side.CLIENT)
		registerPacket(MessageParty::class, Side.CLIENT)
		registerPacket(MessageRaceInfo::class, Side.CLIENT)
		registerPacket(MessageRedstoneSignalsSync::class, Side.CLIENT)
		registerPacket(MessageRelicNBTSync::class, Side.CLIENT)
		registerPacket(MessageSkinInfo::class, Side.CLIENT)
		registerPacket(MessageSpellParams::class, Side.CLIENT)
		registerPacket(MessageTileItem::class, Side.CLIENT)
		registerPacket(MessageTimeStop::class, Side.CLIENT)
		registerPacket(MessageVisualEffect::class, Side.CLIENT)
		
		registerPacket(Message0dS::class, Side.SERVER)
		registerPacket(MessageContributor::class, Side.SERVER)
		registerPacket(MessageCorporeaRequest::class, Side.SERVER)
		registerPacket(MessageHotSpellS::class, Side.SERVER)
		registerPacket(MessageFuckedUpServerPrecision::class, Side.SERVER)
		registerPacket(MessageDisguise::class, Side.SERVER)
		registerPacket(MessageKeyBindS::class, Side.SERVER)
		registerPacket(MessageNI::class, Side.SERVER)
		registerPacket(MessageRaceSelection::class, Side.SERVER)
		registerPacket(MessageUpdateGaiaButton::class, Side.SERVER)
	}

	private fun <T : AlfheimPacket<T>> registerPacket(clazz: KClass<out T>, side: Side) {
		val id = nextPacketID++
		
		try {
			network.registerMessage(clazz.java.newInstance(), clazz.java, id, side)
		} catch (e: Exception) {
			ASJUtilities.error("Can't register packet: Class: ${clazz.qualifiedName} ID: $id Side:${side.name}", e)
			throw RuntimeException(e)
		}
	}
	
	fun sendTo(packet: AlfheimPacket<*>, receiver: EntityPlayerMP) {
		try {
			network.sendTo(packet, receiver)
		} catch (e: Exception) {
			ASJUtilities.error("Error sending packet ${packet::class.java.name} to ${receiver.commandSenderName}: ", e)
		}
	}
	
	fun sendToAll(packet: AlfheimPacket<*>) {
		if (MinecraftServer.getServer()?.configurationManager?.playerEntityList?.isNotEmpty() != true) return
		
		try {
			network.sendToAll(packet)
		} catch (e: Exception) {
			ASJUtilities.error("Error sending packet ${packet::class.java.name} to all: ", e)
		}
	}
	
	fun sendToDim(packet: AlfheimPacket<*>, dimId: Int) {
		if (MinecraftServer.getServer()?.worldServerForDimension(dimId)?.playerEntities?.isNotEmpty() != true) return
		
		try {
			network.sendToDimension(packet, dimId)
		} catch (e: Exception) {
			ASJUtilities.error("Error sending packet ${packet::class.java.name} to dim $dimId: ", e)
		}
	}
	
	fun sendToAllAround(packet: AlfheimPacket<*>, tp: NetworkRegistry.TargetPoint) {
		if (MinecraftServer.getServer()?.worldServerForDimension(tp.dimension)?.playerEntities?.isNotEmpty() != true) return
		
		try {
			network.sendToAllAround(packet, tp)
		} catch (e: Exception) {
			ASJUtilities.error("Error sending packet ${packet::class.java.name} to point (${tp.x},${tp.y},${tp.z})x${tp.range} in dim ${tp.dimension}: ", e)
		}
	}
	
	@SideOnly(Side.CLIENT)
	fun sendToServer(packet: AlfheimPacket<*>) {
		try {
			network.sendToServer(packet)
		} catch (e: Exception) {
			ASJUtilities.error("Error sending packet ${packet::class.java.name} to server: ", e)
		}
	}
}