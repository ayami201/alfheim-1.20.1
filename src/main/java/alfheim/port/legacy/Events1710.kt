package alfheim.port.legacy

import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraftforge.event.entity.living.*
import net.minecraftforge.event.entity.player.PlayerEvent

/*
 * Имена полей событий Forge и существ 1.7.10 (SPEC, Р-4; MAPPING.md, «Прослойка `alfheim.port.legacy`»): смысл тот же,
 * в 1.20.1 поле только переименовано. Код автора с ними остаётся как был.
 */

/** `LivingEvent.entityLiving` 1.7.10 */
val LivingEvent.entityLiving: LivingEntity get() = entity

/** `LivingHurtEvent.ammount` 1.7.10 (поле Forge 1.7.10 так и называлось) */
var LivingHurtEvent.ammount: Float
	get() = amount
	set(value) {
		amount = value
	}

/** `LivingAttackEvent.ammount` 1.7.10 */
val LivingAttackEvent.ammount: Float get() = amount

/** `PlayerEvent.entityPlayer` 1.7.10 */
val PlayerEvent.entityPlayer: Player get() = entity

/** `PlayerEvent.HarvestCheck.success` 1.7.10 */
var PlayerEvent.HarvestCheck.success: Boolean
	get() = canHarvest()
	set(value) {
		setCanHarvest(value)
	}

/** `DamageSource.damageType` 1.7.10 — имя источника урона; у источников ванилы имена те же (`inFire`, `lava`, `drown`…) */
val DamageSource.damageType: String get() = msgId

/** `hurtResistantTime` 1.7.10 — время неуязвимости после удара */
var LivingEntity.hurtResistantTime: Int
	get() = invulnerableTime
	set(value) {
		invulnerableTime = value
	}

/** `maxHurtResistantTime` 1.7.10 */
val LivingEntity.maxHurtResistantTime: Int get() = invulnerableDuration

/** `lastDamage` 1.7.10 — урон последнего удара (поле открыто `META-INF/accesstransformer.cfg`) */
var LivingEntity.lastDamage: Float
	get() = lastHurt
	set(value) {
		lastHurt = value
	}
