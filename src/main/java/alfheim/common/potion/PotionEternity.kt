package alfheim.common.potion

// PORT: импорты 1.20.1 (MAPPING.md); защита существ Нифльхейма (КТ-5), заклинания и их урон (КТ-7) закомментированы
// вместе со своими строками
import alexsocol.asjlib.getActivePotionEffect
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.port.legacy.*
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.ai.attributes.*
import net.minecraft.world.entity.ai.attributes.AttributeMap as BaseAttributeMap
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.phys.Vec3
import net.minecraftforge.event.entity.living.*
import net.minecraftforge.eventbus.api.*
import java.util.*
//import alfheim.api.entity.INiflheimEntity
//import alfheim.api.event.SpellCastEvent
//import alfheim.common.core.util.DamageSourceSpell

/**
 * @author ExtraMeteorP, CKATEPTb
 */
object PotionEternity: PotionAlfheim(AlfheimConfigHandler.potionIDEternity, "eternity", false, 0xDAA520) {
	
	val uuid = UUID.fromString("0B02BC22-17AE-484C-8FD8-BA9BF3472D5C")!!
	
	const val STUN        = 0b0001
	const val ATTACK      = 0b0010
	const val IRREMOVABLE = 0b0100
	const val DISABLE     = 0b1000
	
	override fun applyAttributesModifiersToEntity(target: EntityLivingBase, map: BaseAttributeMap, amp: Int) {
		super.applyAttributesModifiersToEntity(target, map, amp)
		if (amp != 0 && amp and STUN == 0) return
		
		val m = AttributeModifier(uuid, name, -1.0, 2)
		target.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(m)
		target.getEntityAttribute(SharedMonsterAttributes.movementSpeed).applyModifier(m)
	}
	
	override fun removeAttributesModifiersFromEntity(target: EntityLivingBase, map: BaseAttributeMap, amp: Int) {
		super.removeAttributesModifiersFromEntity(target, map, amp)
		if (amp != 0 && amp and STUN == 0) return
		
		target.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(target.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getModifier(uuid) ?: return)
	}
	
	var time = 0
	
	override fun isReady(dur: Int, amp: Int): Boolean {
		time = dur
		return true
	}
	
	override fun performEffect(target: EntityLivingBase, amp: Int) {
		if (amp == 0) {
			if (target.isSneaking) target.getActivePotionEffect(id)?.duration = 0
			
			if (time >= 115) return
		}
		// PORT: КТ-5 — защита существ Нифльхейма (INiflheimEntity)
		/* else if (amp and IRREMOVABLE == 0 && INiflheimEntity.checkProtection(target, time)) {
			target.getActivePotionEffect(id)?.duration = 0
			return
		}*/
		
		val stun = amp == 0 || amp and STUN != 0
		val attack = amp and ATTACK != 0 && time % 20 == 0
		
		if (stun) {
			if (target is EntityPlayer && target.capabilities.isFlying) target.capabilities.isFlying = false

			if (!target.onGround) {
				target.motionX = 0.0
				target.motionY = 0.0
				target.motionZ = 0.0
				target.fallDistance = 0f
			}
		}
		
		// PORT: КТ-5 — защита существ Нифльхейма (INiflheimEntity), КТ-7 — урон льдом (DamageSourceSpell.nifleice)
//		if (attack && !INiflheimEntity.checkProtection(target, 300))
//			target.attackEntityFrom(DamageSourceSpell.nifleice, 1f)
	}
	
	@SubscribeEvent
	fun cancelDamageForSubspace(event: LivingHurtEvent) {
		val player = event.entityLiving as? EntityPlayer ?: return
		val eff = player.getActivePotionEffect(this.id) ?: return
		if (eff.amplifier == 0) event.ammount = 0f
	}
	
	@SubscribeEvent
	fun cancelHealingInStun(e: LivingHealEvent) {
		val pe = e.entityLiving.getActivePotionEffect(this) ?: return
		if (pe.amplifier and STUN != 0) e.isCanceled = true
	}
	
	// PORT: КТ-7 — запрет заклинаний (SpellCastEvent)
	/*
	@SubscribeEvent
	fun disableCast(e: SpellCastEvent.Pre) {
		val pe = e.caster.getActivePotionEffect(this) ?: return
		if (pe.amplifier and DISABLE != 0) e.isCanceled = true
	}
	*/
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	fun disableAttacks(e: LivingAttackEvent) {
		val attacker = e.source.entity as? EntityLivingBase ?: return
		val pe = attacker.getActivePotionEffect(this) ?: return
		if (pe.amplifier and DISABLE != 0) e.isCanceled = true
	}
	
	// PORT: имена полей 1.7.10; скорость существа 1.20.1 — вектор deltaMovement
	private val Entity.isSneaking get() = isShiftKeyDown
	private val EntityPlayer.capabilities get() = abilities
	private var net.minecraft.world.entity.player.Abilities.isFlying: Boolean
		get() = flying
		set(value) {
			flying = value
		}
	private val Entity.onGround get() = onGround()
	private var Entity.motionX: Double
		get() = deltaMovement.x
		set(value) {
			deltaMovement = Vec3(value, deltaMovement.y, deltaMovement.z)
		}
	private var Entity.motionY: Double
		get() = deltaMovement.y
		set(value) {
			deltaMovement = Vec3(deltaMovement.x, value, deltaMovement.z)
		}
	private var Entity.motionZ: Double
		get() = deltaMovement.z
		set(value) {
			deltaMovement = Vec3(deltaMovement.x, deltaMovement.y, value)
		}
}