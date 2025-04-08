package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.entity.FakeLightning
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.TickEvent
import cpw.mods.fml.common.gameevent.TickEvent.*
import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

class TileLightningTreeTop: TileEntity() {
	
	companion object {
		
		init {
			eventFML()
		}
		
		@SubscribeEvent
		fun onClientTick(e: ClientTickEvent) {
			removeLightnings(e, mc.theWorld ?: return)
		}
		
		@SubscribeEvent
		fun onWorldTick(e: WorldTickEvent) {
			removeLightnings(e, e.world ?: return)
		}
		
		fun removeLightnings(e: TickEvent, world: World) {
			if (e.phase != Phase.START || world.weatherEffects.isEmpty()) return
			
			val rods = world.loadedTileEntityList.filterIsInstance<TileLightningTreeTop>()
			if (rods.isEmpty()) return
			
			val newLightnings = ArrayList<FakeLightning>()
			
			world.weatherEffects.iterator().onEach { l ->
				if (l !is EntityLightningBolt) return@onEach
				val rod = rods.firstOrNull { Vector3.entityTileDistance(l, it) < 64 } ?: return@onEach
				
				remove()
				l.lightningState = -1
				l.boltLivingTime = -1
				l.setDead()
				
				newLightnings += FakeLightning(world, rod.xCoord.D, rod.yCoord + 1.5, rod.zCoord.D)
			}
			
			newLightnings.forEach(world::addWeatherEffect)
		}
	}
}