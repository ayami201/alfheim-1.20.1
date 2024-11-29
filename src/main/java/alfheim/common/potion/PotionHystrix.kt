package alfheim.common.potion

import alexsocol.asjlib.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.spell.nature.SpellHystrix
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.DamageSource
import net.minecraftforge.event.entity.living.LivingHurtEvent

object PotionHystrix: PotionAlfheim(AlfheimConfigHandler.potionIDHystrix, "hystrix", false, 0xE5E2DA) {
	
	@SubscribeEvent
	fun onTakenDamage(e: LivingHurtEvent) {
		val target = e.entityLiving
		if (!target.isPotionActive(this)) return
		
		getEntitiesWithinAABB(target.worldObj, EntityLivingBase::class.java, target.boundingBox(SpellHystrix.radius)).forEach { 
			if (it === target) return@forEach
			it.attackEntityFrom(DamageSource.causeThornsDamage(target), SpellHystrix.damage)
		}
	}
}
