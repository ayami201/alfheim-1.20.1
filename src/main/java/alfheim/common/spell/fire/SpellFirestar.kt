package alfheim.common.spell.fire

import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.spawn
import alfheim.api.entity.EnumRace
import alfheim.api.spell.SpellBase
import alfheim.common.entity.spell.EntitySpellFirestar
import net.minecraft.entity.EntityLivingBase
import kotlin.math.min

object SpellFirestar: SpellBase("firestar", EnumRace.SALAMANDER, 6000, 2400, 30) {
	
	override var damage = 1f
	override var duration = 200
	override var efficiency = 0.025
	override var radius = 8.0
	
	override val usableParams
		get() = arrayOf(damage, duration, efficiency, radius)
	
	var canDelete = false
	
	@Suppress("UNCHECKED_CAST")
	override fun performCast(caster: EntityLivingBase): SpellCastResult {
		val result = checkCastOver(caster)
		if (result != SpellCastResult.OK) return result
		
		val myStars = caster.worldObj.loadedEntityList.filter {
			it is EntitySpellFirestar && it.caster === caster
		} as MutableList<EntitySpellFirestar>
		
		if (myStars.isEmpty()) {
			EntitySpellFirestar(caster.worldObj, caster).spawn()
			canDelete = true
			return result
		}
		
		myStars.removeAll { Vector3.entityDistance(caster, it) > radius }
		if (myStars.isEmpty()) return SpellCastResult.NOTARGET
		
		myStars.removeAll { it.powered || it.ticksExisted > duration / 2 }
		if (myStars.isEmpty()) return SpellCastResult.WRONGTGT
		
		myStars.firstOrNull()?.powered = true
		
		return SpellCastResult.OK
	}
	
	override fun getCooldown(): Int {
		val orig = super.getCooldown()
		
		if (canDelete) {
			canDelete = false
			return min(10, orig)
		}
		
		return orig
	}
}