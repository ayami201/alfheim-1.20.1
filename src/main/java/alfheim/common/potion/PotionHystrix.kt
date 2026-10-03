package alfheim.common.potion

// PORT: импорты 1.20.1; заклинание (КТ-7) закомментировано вместе со своей строкой
import alexsocol.asjlib.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.port.legacy.*
import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
//import alfheim.common.spell.nature.SpellHystrix
//import net.minecraft.entity.EntityLivingBase
//import net.minecraft.util.DamageSource

object PotionHystrix: PotionAlfheim(AlfheimConfigHandler.potionIDHystrix, "hystrix", false, 0xE5E2DA) {
	
	var antiStackOverflow = false
	
	@SubscribeEvent
	fun onTakenDamage(e: LivingHurtEvent) {
		val target = e.entityLiving
		if (!target.isPotionActive(this)) return
		if (antiStackOverflow) return
		
		antiStackOverflow = true
		
		// PORT: КТ-7 — шипы радиусом и уроном заклинания «Дикобраз» (SpellHystrix)
//		getEntitiesWithinAABB(target.worldObj, EntityLivingBase::class.java, target.boundingBox(SpellHystrix.radius)).forEach { 
//			if (it !== target && it.isEntityAlive)
//				it.attackEntityFrom(DamageSource.causeThornsDamage(target), SpellHystrix.damage)
//		}
		
		antiStackOverflow = false
	}
}
