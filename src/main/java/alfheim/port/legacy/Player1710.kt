package alfheim.port.legacy

import net.minecraft.network.protocol.Packet
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.entity.player.Abilities
import net.minecraft.world.entity.player.Player

/*
 * `PlayerCapabilities` 1.7.10 — `Abilities` 1.20.1 (MAPPING.md, «Прослойка `alfheim.port.legacy`»): те же флаги игрока
 * под другими именами.
 */

/** `player.capabilities` 1.7.10 */
val Player.capabilities: Abilities get() = abilities

/** Творческий режим: блоки ломаются сразу, предметы не тратятся */
var Abilities.isCreativeMode
	get() = instabuild
	set(value) { instabuild = value }

/** Игрок летит */
var Abilities.isFlying
	get() = flying
	set(value) { flying = value }

/** Игроку можно летать */
var Abilities.allowFlying
	get() = mayfly
	set(value) { mayfly = value }

/** Игрок не получает урона */
var Abilities.disableDamage
	get() = invulnerable
	set(value) { invulnerable = value }

/** `playerNetServerHandler` 1.7.10 — соединение игрока с сервером */
val ServerPlayer.playerNetServerHandler: ServerGamePacketListenerImpl get() = connection

/** `sendPacket(packet)` 1.7.10 — пакет этому игроку */
fun ServerGamePacketListenerImpl.sendPacket(packet: Packet<*>) = send(packet)
