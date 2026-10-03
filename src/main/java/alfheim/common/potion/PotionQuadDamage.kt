package alfheim.common.potion

// PORT: импорты 1.20.1; заклинания и CardinalSystem (КТ-7) закомментированы вместе со своими строками
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.*
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.ai.attributes.AttributeMap as BaseAttributeMap
//import alexsocol.asjlib.*
//import alfheim.api.event.SpellCastEvent
//import cpw.mods.fml.common.eventhandler.SubscribeEvent
//import net.minecraft.entity.player.EntityPlayer
//import net.minecraftforge.event.entity.EntityStruckByLightningEvent

object PotionQuadDamage: PotionAlfheim(AlfheimConfigHandler.potionIDQuadDamage, "quadDamage", false, 0x22FFFF) {
	
	override fun applyAttributesModifiersToEntity(target: EntityLivingBase?, attributes: BaseAttributeMap, ampl: Int) {
		super.applyAttributesModifiersToEntity(target, attributes, ampl)
		if (AlfheimConfigHandler.enableMMO) VisualEffectHandler.sendPacket(VisualEffects.QUAD, target!!)
	}
	
	override fun isReady(dur: Int, amp: Int) = dur <= 100 && dur % 20 == 0
	
	override fun performEffect(target: EntityLivingBase, amp: Int) = VisualEffectHandler.sendPacket(VisualEffects.QUADH, target)
	
	// PORT: КТ-7 — четверной урон даёт цепочка заклинаний (SpellCastEvent, CardinalSystem.quadStage) и удар молнии
	/*
	@SubscribeEvent
	fun handleQuadDamageSequence(e: SpellCastEvent.Post) {
		if (e.caster !is EntityPlayer) return
		val player = e.caster
		val seg = CardinalSystem.forPlayer(player)
		
		when (seg.quadStage) {
			0    -> if (e.spell.name == "stoneskin") {
				++seg.quadStage
			} else {
				seg.quadStage = 0
			}
			
			1    -> if (e.spell.name == "uphealth" && player.isPotionActive(AlfheimConfigHandler.potionIDStoneSkin)) {
				++seg.quadStage
			} else {
				seg.quadStage = 0
			}
			
			2    -> if (e.spell.name == "icelens" && player.isPotionActive(AlfheimConfigHandler.potionIDStoneSkin) && player.getActivePotionEffect(field_76434_w.id)?.amplifier == 1) {
				++seg.quadStage
			} else {
				seg.quadStage = 0
			}
			
			3    -> if (e.spell.name == "battlehorn" && player.isPotionActive(AlfheimConfigHandler.potionIDStoneSkin) && player.getActivePotionEffect(field_76434_w.id)?.amplifier == 1 && player.isPotionActive(AlfheimConfigHandler.potionIDIceLens)) {
				++seg.quadStage
			} else {
				seg.quadStage = 0
			}
			
			4    -> if (e.spell.name == "thor" && player.isPotionActive(AlfheimConfigHandler.potionIDStoneSkin) && player.getActivePotionEffect(field_76434_w.id)?.amplifier == 1 && player.isPotionActive(AlfheimConfigHandler.potionIDIceLens)) {
				++seg.quadStage
			} else {
				seg.quadStage = 0
			}
			
			else -> {
				seg.quadStage = 0
			}
		}
	}
	
	@SubscribeEvent
	fun addQuadDamageEffect(e: EntityStruckByLightningEvent) {
		if (!AlfheimConfigHandler.enableMMO) return
		if (e.entity !is EntityPlayer) return
		val player = e.entity as EntityPlayer
		val seg = CardinalSystem.forPlayer(player)
		
		if (seg.quadStage < 5 || !player.isPotionActive(AlfheimConfigHandler.potionIDStoneSkin) || player.getActivePotionEffect(field_76434_w.id)?.amplifier != 1 || !player.isPotionActive(AlfheimConfigHandler.potionIDIceLens)) return
		seg.quadStage = 0
		player.removePotionEffect(AlfheimConfigHandler.potionIDStoneSkin)
		player.removePotionEffect(field_76434_w.id)
		player.removePotionEffect(AlfheimConfigHandler.potionIDIceLens)
		player.removePotionEffect(damageBoost.id)
		player.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDQuadDamage, 600, 24))
		e.isCanceled = true
	}
	*/
}