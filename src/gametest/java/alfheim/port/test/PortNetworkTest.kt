package alfheim.port.test

import alexsocol.asjlib.network.ASJPacket
import alfheim.api.ModInfo.MODID
import alfheim.api.event.PlayerInteractAdequateEvent
import alfheim.common.network.*
import alfheim.common.network.packet.*
import io.netty.buffer.Unpooled
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate
import java.util.function.Consumer

/**
 * КТ-1: сеть. Пакеты автора сами пишут и читают свои поля (замена ASJPacketCompleter в ASJPacket),
 * события автора принимают слушателей шины Forge.
 */
@GameTestHolder(MODID)
@PrefixGameTestTemplate(false)
object PortNetworkTest {

	private inline fun <reified T: ASJPacket> roundTrip(packet: T): T {
		val buf = FriendlyByteBuf(Unpooled.buffer())
		packet.toBytes(buf)
		val read = ASJPacket.create(T::class.java).apply { fromBytes(buf) }
		check(!buf.isReadable) { "${T::class.simpleName}: ${buf.readableBytes()} bytes left unread" }
		return read
	}

	@JvmStatic
	@GameTest(template = "empty")
	fun packetFieldsSurviveRoundTrip(helper: GameTestHelper) {
		// NetworkService регистрирует канал и пакеты при загрузке мода; обращение проверяет, что это прошло без ошибок
		NetworkService

		val m1d = roundTrip(Message1d(M1d.DEATH_TIMER, 1234.5))
		helper.assertTrue(m1d.data1 == 1234.5 && m1d.type == M1d.DEATH_TIMER.ordinal, "Message1d: ${m1d.data1} ${m1d.type}")

		val m3d = roundTrip(Message3d(M3d.WEATHER, 1.0, -2.5, 300.0))
		helper.assertTrue(m3d.data1 == 1.0 && m3d.data2 == -2.5 && m3d.data3 == 300.0 && m3d.type == M3d.WEATHER.ordinal, "Message3d")

		val m1l = roundTrip(Message1l(M1l.SEED, Long.MIN_VALUE + 7))
		helper.assertTrue(m1l.data1 == Long.MIN_VALUE + 7, "Message1l: ${m1l.data1}")

		// поля, которые пакет пишет сам (toCustomBytes), идут перед полями, которые пишутся автоматически
		val ve = roundTrip(MessageVisualEffect(5, 0.25, -1.0, 8.0))
		helper.assertTrue(ve.type == 5 && ve.data.contentEquals(doubleArrayOf(0.25, -1.0, 8.0)), "MessageVisualEffect: ${ve.type} ${ve.data.toList()}")

		val ni = roundTrip(MessageNI(Mni.WINGS_BL, 1, -1, 7))
		helper.assertTrue(ni.type == Mni.WINGS_BL.ordinal && ni.intArray.contentEquals(intArrayOf(1, -1, 7)), "MessageNI: ${ni.type} ${ni.intArray.toList()}")

		val effect = roundTrip(MessageEffect(42, 7, 1200, 3, true, -1))
		helper.assertTrue(effect.entity == 42 && effect.id == 7 && effect.dur == 1200 && effect.amp == 3 && effect.readd && effect.state == (-1).toByte(), "MessageEffect")

		val contributor = roundTrip(MessageContributor("AlexSocol", "Альфхейм", true))
		helper.assertTrue(contributor.key == "AlexSocol" && contributor.value == "Альфхейм" && contributor.isRequest, "MessageContributor: ${contributor.key} ${contributor.value} ${contributor.isRequest}")

		helper.succeed()
	}

	@JvmStatic
	@GameTest(template = "empty")
	fun eventsAcceptListeners(helper: GameTestHelper) {
		// Шина Forge ищет слушателей события через его конструктор без аргументов; у событий автора его дописывает
		// сама шина, как FML в 1.7.10. Без него регистрация слушателя бросила бы исключение
		val left = Consumer<PlayerInteractAdequateEvent.LeftClick> {}
		val right = Consumer<PlayerInteractAdequateEvent.RightClick> {}
		val any = Consumer<PlayerInteractAdequateEvent> {}
		MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerInteractAdequateEvent.LeftClick::class.java, left)
		MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerInteractAdequateEvent.RightClick::class.java, right)
		MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerInteractAdequateEvent::class.java, any)
		MinecraftForge.EVENT_BUS.unregister(left)
		MinecraftForge.EVENT_BUS.unregister(right)
		MinecraftForge.EVENT_BUS.unregister(any)
		helper.succeed()
	}
}
