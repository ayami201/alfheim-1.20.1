package alfheim.common.potion

// PORT: импорты 1.20.1 (MAPPING.md); заклинание (КТ-7) закомментировано вместе со своей строкой
import alexsocol.asjlib.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.port.legacy.*
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.eventbus.api.*
//import alfheim.common.spell.nature.SpellBeastWithin

object PotionBeastWithin: PotionAlfheim(AlfheimConfigHandler.potionIDBeastWithin, "beast", false, 0xFF8000) {
	
	init {
		func_111184_a(SharedMonsterAttributes.movementSpeed, "4B91E728-A16B-4D77-A81E-16F1286F7B1B", 0.3, 2)
		eventForge()
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	fun onLivingHurt(e: LivingHurtEvent) {
		val attacker = e.source.entity as? EntityLivingBase ?: return
		if (!attacker.isPotionActive(this)) return
		// PORT: КТ-7 — кровотечение силой заклинания «Зверь внутри» (SpellBeastWithin)
//		e.entityLiving.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDBleeding, SpellBeastWithin.damage.I, SpellBeastWithin.efficiency.I))
	}
}