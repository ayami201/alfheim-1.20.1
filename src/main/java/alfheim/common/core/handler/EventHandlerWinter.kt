package alfheim.common.core.handler

import alexsocol.patcher.handler.GameRulesHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.TickEvent

object EventHandlerWinter {
	
	var nextRain = 0
	
	@SubscribeEvent
	fun onWorldTick(e: TickEvent.WorldTickEvent) {
		if (!WRATH_OF_THE_WINTER || e.world.worldInfo.isRaining || --nextRain > 0) return
		if (!e.world.gameRules.getGameRuleBooleanValue(GameRulesHandler.GR_DO_WEATHER_CYCLE)) return
		
		e.world.rainingStrength = 1f
		e.world.worldInfo.isRaining = true
		e.world.worldInfo.rainTime = e.world.rand.nextInt(12000) + 24000
		
		nextRain = e.world.rand.nextInt(12000) + 18000
	}
}