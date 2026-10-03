package alfheim.common.potion

// PORT: импорты 1.20.1; группа, стихии и заклинание (КТ-4, КТ-7) закомментированы вместе со своими строками
import alexsocol.asjlib.math.Vector3
import alfheim.common.core.handler.AlfheimConfigHandler
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.phys.Vec3
//import alexsocol.asjlib.*
//import alfheim.common.core.handler.CardinalSystem.PartySystem
//import alfheim.common.core.handler.CardinalSystem.PartySystem.Party
//import alfheim.common.core.helper.*
//import alfheim.common.spell.wind.SpellThrow
//import net.minecraft.entity.player.EntityPlayer
//import net.minecraft.util.DamageSource

object PotionThrow: PotionAlfheim(AlfheimConfigHandler.potionIDThrow, "throw", false, 0xAAFFFF) {
	
	override fun isReady(time: Int, mod: Int) = AlfheimConfigHandler.enableMMO
	
	override fun performEffect(target: EntityLivingBase, mod: Int) {
		if (!AlfheimConfigHandler.enableMMO) return
		
		val v = Vector3(target.lookVec).mul(mod + 1)
		target.motionX = v.x
		target.motionY = v.y
		target.motionZ = v.z
		
		// PORT: КТ-7 — урон воздухом всем вокруг, кроме группы (CardinalSystem.PartySystem, SpellThrow), КТ-4 — стихия (ElementalDamage)
		/*
		var pt = PartySystem.getMobParty(target)
		if (pt == null) pt = Party()
		
		val l = getEntitiesWithinAABB(target.worldObj, EntityLivingBase::class.java, target.boundingBox.copy().expand(SpellThrow.radius))
		l.remove(target)
		for (e in l) if (!pt.isMember(e)) e.attackEntityFrom((if (target is EntityPlayer) DamageSource.causePlayerDamage(target) else DamageSource.causeMobDamage(target)).setTo(ElementalDamage.AIR), SpellThrow.damage)
		*/
	}
	
	// PORT: имена полей 1.7.10; скорость существа 1.20.1 — вектор deltaMovement
	private val EntityLivingBase.lookVec get() = lookAngle
	private var EntityLivingBase.motionX: Double
		get() = deltaMovement.x
		set(value) {
			deltaMovement = Vec3(value, deltaMovement.y, deltaMovement.z)
		}
	private var EntityLivingBase.motionY: Double
		get() = deltaMovement.y
		set(value) {
			deltaMovement = Vec3(deltaMovement.x, value, deltaMovement.z)
		}
	private var EntityLivingBase.motionZ: Double
		get() = deltaMovement.z
		set(value) {
			deltaMovement = Vec3(deltaMovement.x, deltaMovement.y, value)
		}
}
