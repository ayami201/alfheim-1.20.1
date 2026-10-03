package alfheim.common.potion

// PORT: импорты 1.20.1; заклинание (КТ-7) закомментировано вместе со своей строкой
import alexsocol.asjlib.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.port.legacy.*
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed
import net.minecraftforge.eventbus.api.SubscribeEvent
//import alfheim.common.spell.earth.SpellGoldRush

object PotionGoldRush: PotionAlfheim(AlfheimConfigHandler.potionIDGoldRush, "goldRush", false, 0x55FF00) {
	
	init {
		eventForge()
	}
	
	@SubscribeEvent
	fun onBreakSpeed(e: BreakSpeed) {
		// PORT: КТ-7 — скорость добычи по силе заклинания «Золотая лихорадка» (SpellGoldRush)
//		if (AlfheimConfigHandler.enableMMO && e.entityLiving.isPotionActive(this.id)) e.newSpeed *= SpellGoldRush.efficiency.F
	}
}
