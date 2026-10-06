package alfheim.common.block.tile

// PORT: импорты 1.20.1 (блок-сущность и погодные эффекты 1.7.10 — alfheim.port.legacy, MAPPING.md); события тика FML
// 1.7.10 — на шине Forge
import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.entity.FakeLightning
import alfheim.port.legacy.*
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.LightningBolt as EntityLightningBolt
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.TickEvent.*
import net.minecraftforge.event.TickEvent.LevelTickEvent as WorldTickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.LogicalSide

// PORT: блок-сущность 1.20.1 создаётся сразу в своей точке и со своим типом (alfheim.port.legacy.TileEntity)
class TileLightningTreeTop(pos: BlockPos, state: BlockState): TileEntity(legacyTileType<TileLightningTreeTop>(), pos, state) {
//class TileLightningTreeTop: TileEntity() {
	
	companion object {
		
		init {
			eventFML()
		}
		
		// PORT: молнию заменяет сервер, а замену (FakeLightning) клиент получает пакетом существа, как молнию 1.20.1. В
		// 1.7.10 сервер замену клиентам не слал (погодный эффект, не молния), и клиент заменял молнию сам. Молния 1.20.1
		// звучит на клиенте, на своём первом тике: клиент, который убирал бы её до тика, не услышал бы гром в месте
		// удара, а в 1.7.10 его было слышно
//		@SubscribeEvent
//		fun onClientTick(e: ClientTickEvent) {
//			removeLightnings(e, mc.theWorld ?: return)
//		}
		
		@SubscribeEvent
		fun onWorldTick(e: WorldTickEvent) {
			// PORT: WorldTickEvent 1.7.10 шёл только на сервере, LevelTickEvent 1.20.1 — и на клиенте
			if (e.side != LogicalSide.SERVER) return
			removeLightnings(e, e.level)
//			removeLightnings(e, e.world ?: return)
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
				// PORT: молния 1.20.1 уходит из мира сразу (setDead) и больше не тикает; сбрасывать её состояние (поля
				// закрыты) не нужно
//				l.lightningState = -1
//				l.boltLivingTime = -1
				l.setDead()
				
				newLightnings += FakeLightning(world, rod.xCoord.D, rod.yCoord + 1.5, rod.zCoord.D)
			}
			
			newLightnings.forEach(world::addWeatherEffect)
		}
	}
}