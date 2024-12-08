package alfheim.common.spell.nature

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.core.handler.CardinalSystem.TargetingSystem
import net.minecraft.entity.*
import kotlin.math.max

object SpellAport: SpellBase("aport", EnumRace.CAITSITH, 1500, 500, 20) {
	
	override var radius = 32.0
	
	override val usableParams
		get() = arrayOf(radius)
	
	var timesApplied = 0
	
	@Suppress("UNCHECKED_CAST")
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val tg = TargetingSystem.getTarget(caster)
		val tgt = tg.target ?: return SpellCastResult.NOTARGET
		
		if (tgt === caster || tg.isParty) return SpellCastResult.WRONGTGT
		if (ASJUtilities.isNotInFieldOfVision(tgt, caster)) return SpellCastResult.NOTSEEING
		
		val pets = getEntitiesWithinAABB(caster.worldObj, IEntityOwnable::class.java, caster.boundingBox(radius)).filter {
			it.owner === caster && it is EntityLivingBase
		} as List<EntityLivingBase>
		
		timesApplied = pets.size
		
		if (pets.isEmpty())
			return SpellCastResult.NOTARGET
		
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		
		val (x, y, z) = Vector3.fromEntity(tgt)
		pets.forEach { 
			it.setPosition(x, y, z)
			it.setRevengeTarget(tgt)
			
			if (it is EntityLiving)
				it.attackTarget = tgt
		}
		
		return result
	}
	
	override fun getManaCost(): Int {
		try {
			return max(mana, timesApplied * mana)
		} finally {
			timesApplied = 1
		}
	}
}