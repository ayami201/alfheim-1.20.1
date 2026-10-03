package alfheim.port.legacy

import net.minecraft.world.effect.*
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.*
import java.util.UUID

/*
 * Зелья на существе 1.7.10 (SPEC, Р-4; MAPPING.md, «Предметы, сущности, эффекты»): `PotionEffect` 1.7.10 —
 * `MobEffectInstance` 1.20.1, зелье ищется по id 1.7.10 ([Potion1710.potionTypes]). Длительность и силу наложенного
 * эффекта автор меняет прямо (`pe.duration = 0`) — поля открыты `META-INF/accesstransformer.cfg`, как в ASJCore 1.7.10.
 */

/** `PotionEffect(id, duration, amplifier, ambient)` 1.7.10; частицы видны, как у любого эффекта 1.7.10 */
fun PotionEffect(id: Int, duration: Int, amplifier: Int = 0, ambient: Boolean = false) = PotionEffect(Potion1710.byId(id)!!, duration, amplifier, ambient)

/** `PotionEffect(potion.id, duration, amplifier, ambient)` 1.7.10 */
fun PotionEffect(potion: MobEffect, duration: Int, amplifier: Int = 0, ambient: Boolean = false) = MobEffectInstance(potion, duration, amplifier, ambient, true)

/** id 1.7.10 зелья (`potion.id`); у [Potion1710] — его поле `id` */
val MobEffect.id get() = Potion1710.idOf(this)

/** `PotionEffect.getPotionID()` 1.7.10 */
val MobEffectInstance.potionID get() = Potion1710.idOf(effect)

fun LivingEntity.isPotionActive(id: Int) = Potion1710.byId(id)?.let { hasEffect(it) } ?: false

fun LivingEntity.isPotionActive(potion: MobEffect) = hasEffect(potion)

fun LivingEntity.getActivePotionEffect(potion: MobEffect): MobEffectInstance? = getEffect(potion)

fun LivingEntity.addPotionEffect(effect: MobEffectInstance) {
	addEffect(effect)
}

fun LivingEntity.removePotionEffect(id: Int) {
	Potion1710.byId(id)?.let { removeEffect(it) }
}

/** `getActivePotionEffects()` 1.7.10 — эффекты на существе */
val LivingEntity.activePotionEffects: Collection<MobEffectInstance> get() = activeEffects

/** `SharedMonsterAttributes` 1.7.10 */
object SharedMonsterAttributes {
	
	@JvmField val maxHealth: Attribute = Attributes.MAX_HEALTH
	@JvmField val followRange: Attribute = Attributes.FOLLOW_RANGE
	@JvmField val knockbackResistance: Attribute = Attributes.KNOCKBACK_RESISTANCE
	@JvmField val movementSpeed: Attribute = Attributes.MOVEMENT_SPEED
	@JvmField val attackDamage: Attribute = Attributes.ATTACK_DAMAGE
}

/** `getEntityAttribute(attribute)` 1.7.10; у существа без атрибута 1.7.10 тоже падал на следующем вызове */
fun LivingEntity.getEntityAttribute(attribute: Attribute): AttributeInstance = getAttribute(attribute)!!

/** `applyModifier` 1.7.10: модификатор сохраняется вместе с существом, как в 1.7.10 */
fun AttributeInstance.applyModifier(modifier: AttributeModifier) = addPermanentModifier(modifier)

/** `AttributeModifier(uuid, name, amount, operation)` 1.7.10: операция — 0, 1, 2 */
fun AttributeModifier(uuid: UUID, name: String, amount: Double, operation: Int) = AttributeModifier(uuid, name, amount, AttributeModifier.Operation.fromValue(operation))
