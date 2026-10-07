package alfheim.port.legacy

import net.minecraft.network.protocol.Packet
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Abilities
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

/*
 * `PlayerCapabilities` 1.7.10 — `Abilities` 1.20.1 (MAPPING.md, «Прослойка `alfheim.port.legacy`»): те же флаги игрока
 * под другими именами. Ещё — вещь в руке и инвентарь игрока по именам 1.7.10.
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

/** `getCurrentEquippedItem()` 1.7.10 — вещь в руке (основной: второй руки в 1.7.10 не было); пустая рука — `null` */
val Player.currentEquippedItem: ItemStack? get() = mainHandItem.takeUnless { it.isEmpty }

/** `inventory.addItemStackToInventory(stack)` 1.7.10 — положить в инвентарь; `false` — не поместилось */
fun Inventory.addItemStackToInventory(stack: ItemStack) = add(stack)

/**
 * `dropPlayerItemWithRandomChoice(stack, flag)` 1.7.10 — бросить вещь из игрока вперёд, по взгляду: 1.7.10 флаг не
 * читал (`func_146097_a(stack, false, false)`) — вещь не разлетается во все стороны, не помечается именем игрока, событие
 * выброса (`ItemTossEvent`) не приходит. В мир вещь попадает только на сервере, как в 1.7.10
 */
@Suppress("UNUSED_PARAMETER")
fun Player.dropPlayerItemWithRandomChoice(stack: ItemStack, flag: Boolean): ItemEntity? = drop(stack, false, false)
