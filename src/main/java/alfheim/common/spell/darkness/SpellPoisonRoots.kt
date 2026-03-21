package alfheim.common.spell.darkness

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.core.handler.CardinalSystem.PartySystem
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.potion.*

object SpellPoisonRoots: SpellBase("poisonroots", EnumRace.IMP, 60000, 6000, 30) {
	
	override var duration = 300
	override var efficiency = 4.0
	
	override val usableParams
		get() = arrayOf<Number>(duration, efficiency, radius)
	
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val pt = (if (caster is EntityPlayer) PartySystem.getParty(caster) else PartySystem.getMobParty(caster)) ?: return SpellCastResult.NOTARGET
		var partyHasDebuffs = false
		var member: EntityLivingBase?
		
		scanpt@ for (i in 0 until pt.count) {
			member = pt[i]
			if (member == null || Vector3.entityDistance(caster, member) > 32) continue
			
			for (o in member.activePotionEffects) {
				if (Potion.potionTypes[(o as PotionEffect).potionID].isBadEffect) {
					partyHasDebuffs = true
					break@scanpt
				}
			}
		}
		
		if (!partyHasDebuffs) return SpellCastResult.WRONGTGT
		
		val targets = getEntitiesWithinAABB(caster.worldObj, EntityLivingBase::class.java, caster.boundingBox.expand(radius))
		targets.removeAll { pt.isMember(it) }
		
		if (targets.isEmpty()) return SpellCastResult.NOTARGET
		
		val result = checkCast(caster)
		if (result != SpellCastResult.OK) return result
		
		val removed = ArrayList<PotionEffect>()
		
		for (i in 0 until pt.count) {
			member = pt[i] ?: continue
			val toRemove = ArrayList<PotionEffect>()
			for (o in member.activePotionEffects) {
				val pe = o as PotionEffect
				
				if (Potion.potionTypes[pe.getPotionID()].isBadEffect)
					toRemove.add(pe)
			}
			
			for (pe in toRemove) {
				removed += PotionEffect(pe.potionID, pe.duration / targets.size, pe.amplifier / targets.size, pe.isAmbient)
				member.removePotionEffect(pe.potionID)
			}
		}
		
		for (pe in removed)
			for (target in targets)
				target.addPotionEffect(pe)
		
		for (e in targets)
			e.addPotionEffect(PotionEffectU(Potion.moveSlowdown.id, duration, efficiency.I))
		
		return result
	}
}