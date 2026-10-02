package alfheim.common.network

import alexsocol.asjlib.ASJUtilities
import alexsocol.asjlib.network.ASJPacket
import alfheim.api.ModInfo
import alfheim.api.network.AlfheimPacket
import alfheim.common.network.packet.*
// PORT: SimpleNetworkWrapper → SimpleChannel; Side → LogicalSide; EntityPlayerMP → ServerPlayer; номер измерения →
// ResourceKey<Level>; NetworkRegistry.TargetPoint → alfheim.port.legacy.TargetPoint
import alfheim.port.legacy.TargetPoint
import net.minecraft.resources.*
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.level.Level
import net.minecraftforge.api.distmarker.*
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.fml.ModList
import net.minecraftforge.network.*
import net.minecraftforge.network.simple.SimpleChannel
import net.minecraftforge.server.ServerLifecycleHooks
import java.util.*
import kotlin.reflect.KClass

object NetworkService {
	
	// PORT: версия протокола — версия мода: клиент и сервер разных версий порта не соединятся, как в 1.7.10,
	// где FML при входе сверял версии модов
	private val PROTOCOL = ModList.get().getModContainerById(ModInfo.MODID).get().modInfo.version.toString()
	
	private val network: SimpleChannel = NetworkRegistry.newSimpleChannel(ResourceLocation(ModInfo.MODID, "main"), { PROTOCOL }, PROTOCOL::equals, PROTOCOL::equals)
	
	private var nextPacketID = 0
	
	// PORT: в 1.7.10 класс регистрировался отдельно для каждой стороны, которая его принимает. В SimpleChannel класс —
	// ключ кодека, поэтому пакет, который ходит в обе стороны, второй раз регистрируется без привязки к стороне
	private val registered = HashSet<KClass<*>>()
	
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
		
		// PORT: пакеты механик регистрируются вместе с механикой, КТ — в конце строки (INVENTORY.md)
		registerPacket(MessageContributor::class, Side.CLIENT)
//		registerPacket(MessageDisguise::class, Side.CLIENT) // КТ-4
//		registerPacket(MessageEffect::class, Side.CLIENT) // КТ-2
//		registerPacket(MessageFIBlock::class, Side.CLIENT) // КТ-6
//		registerPacket(MessageGleipnirLeash::class, Side.CLIENT) // КТ-4
//		registerPacket(MessageHotSpellC::class, Side.CLIENT) // КТ-7
//		registerPacket(MessageOrgans::class, Side.CLIENT) // КТ-8
//		registerPacket(MessageParty::class, Side.CLIENT) // КТ-7
//		registerPacket(MessageRaceInfo::class, Side.CLIENT) // КТ-7
//		registerPacket(MessageRedstoneSignalsSync::class, Side.CLIENT) // КТ-4
//		registerPacket(MessageRelicNBTSync::class, Side.CLIENT) // КТ-4
//		registerPacket(MessageSkinInfo::class, Side.CLIENT) // КТ-7
//		registerPacket(MessageSpellParams::class, Side.CLIENT) // КТ-7
//		registerPacket(MessageTileItem::class, Side.CLIENT) // КТ-3
//		registerPacket(MessageTimeStop::class, Side.CLIENT) // КТ-7
		registerPacket(MessageVisualEffect::class, Side.CLIENT)
		
		registerPacket(Message0dS::class, Side.SERVER)
		registerPacket(MessageContributor::class, Side.SERVER)
//		registerPacket(MessageCorporeaRequest::class, Side.SERVER) // КТ-3
//		registerPacket(MessageHotSpellS::class, Side.SERVER) // КТ-7
//		registerPacket(MessageFuckedUpServerPrecision::class, Side.SERVER) // КТ-3
//		registerPacket(MessageDisguise::class, Side.SERVER) // КТ-4
//		registerPacket(MessageKeyBindS::class, Side.SERVER) // КТ-7
		registerPacket(MessageNI::class, Side.SERVER)
//		registerPacket(MessageRaceSelection::class, Side.SERVER) // КТ-7
//		registerPacket(MessageUpdateGaiaButton::class, Side.SERVER) // КТ-3
	}

	private fun <T : AlfheimPacket<T>> registerPacket(clazz: KClass<out T>, side: Side) {
		val id = nextPacketID++
		
		try {
			@Suppress("UNCHECKED_CAST") val java = clazz.java as Class<T>
			val direction = if (registered.add(clazz)) Optional.of(if (side == Side.CLIENT) NetworkDirection.PLAY_TO_CLIENT else NetworkDirection.PLAY_TO_SERVER) else Optional.empty()
			network.registerMessage(id, java, { packet, buf -> packet.toBytes(buf) }, { buf -> ASJPacket.create(java).apply { fromBytes(buf) } }, { packet, ctx -> packet.onMessage(packet, ctx) }, direction)
		} catch (e: Exception) {
			ASJUtilities.error("Can't register packet: Class: ${clazz.qualifiedName} ID: $id Side:${side.name}", e)
			throw RuntimeException(e)
		}
	}
	
	fun sendTo(packet: AlfheimPacket<*>, receiver: EntityPlayerMP) {
		try {
			network.send(PacketDistributor.PLAYER.with { receiver }, packet)
		} catch (e: Exception) {
			ASJUtilities.error("Error sending packet ${packet::class.java.name} to ${receiver.gameProfile.name}: ", e)
		}
	}
	
	fun sendToAll(packet: AlfheimPacket<*>) {
		if (ServerLifecycleHooks.getCurrentServer()?.playerList?.players?.isNotEmpty() != true) return
		
		try {
			network.send(PacketDistributor.ALL.noArg(), packet)
		} catch (e: Exception) {
			ASJUtilities.error("Error sending packet ${packet::class.java.name} to all: ", e)
		}
	}
	
	fun sendToDim(packet: AlfheimPacket<*>, dimId: ResourceKey<Level>) {
		if (ServerLifecycleHooks.getCurrentServer()?.getLevel(dimId)?.players()?.isNotEmpty() != true) return
		
		try {
			network.send(PacketDistributor.DIMENSION.with { dimId }, packet)
		} catch (e: Exception) {
			ASJUtilities.error("Error sending packet ${packet::class.java.name} to dim ${dimId.location()}: ", e)
		}
	}
	
	fun sendToAllAround(packet: AlfheimPacket<*>, tp: TargetPoint) {
		if (ServerLifecycleHooks.getCurrentServer()?.getLevel(tp.dimension)?.players()?.isNotEmpty() != true) return
		
		try {
			network.send(PacketDistributor.NEAR.with { tp.toForge() }, packet)
		} catch (e: Exception) {
			ASJUtilities.error("Error sending packet ${packet::class.java.name} to point (${tp.x},${tp.y},${tp.z})x${tp.range} in dim ${tp.dimension.location()}: ", e)
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	fun sendToServer(packet: AlfheimPacket<*>) {
		try {
			network.sendToServer(packet)
		} catch (e: Exception) {
			ASJUtilities.error("Error sending packet ${packet::class.java.name} to server: ", e)
		}
	}
}