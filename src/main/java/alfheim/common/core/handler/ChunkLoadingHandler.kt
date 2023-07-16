package alfheim.common.core.handler

import alexsocol.asjlib.*
import alexsocol.patcher.event.ServerStoppedEvent
import alfheim.AlfheimCore
import alfheim.api.ModInfo
import com.google.common.collect.ImmutableSetMultimap
import cpw.mods.fml.common.FMLLog
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.TickEvent
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent
import net.minecraft.world.*
import net.minecraftforge.common.ForgeChunkManager
import net.minecraftforge.common.ForgeChunkManager.Ticket
import net.minecraftforge.common.ForgeChunkManager.Type

object ChunkLoadingHandler: ForgeChunkManager.LoadingCallback {
	
	val ticketsStore = HashMap<Int, LinkedHashSet<AlfheimTicket>>()
	
	init {
		eventFML().eventForge()
		ForgeChunkManager.setForcedChunkLoadingCallback(AlfheimCore, this)
	}
	
	fun requestChunkLoad(world: World, chunkX: Int, chunkZ: Int): Boolean {
		if (ASJUtilities.isClient) return false
		
		val chunk = ChunkCoordIntPair(chunkX, chunkZ)
		
		val ticketsForDim = ticketsStore.computeIfAbsent(world.provider.dimensionId) { LinkedHashSet() }
		
		var ticket = ticketsForDim.firstOrNull {
			chunk in it.ticket.chunkList
		} ?: ticketsForDim.firstOrNull {
			it.ticket.chunkListDepth <= 0 || it.ticket.chunkList.size < it.ticket.chunkListDepth
		}
		
		var newTicket = false
		
		if (ticket == null) {
			val fTicket = ForgeChunkManager.requestTicket(AlfheimCore, world, Type.NORMAL) ?: run {
				FMLLog.bigWarning("Unable to allocate new chunkloading ticket for [${ModInfo.MODID}]! Please, expand the limit in FML configs or remove extra")
				return false
			}
			
			ticket = AlfheimTicket(fTicket, linkedMapOf())
			newTicket = true
		}
		
		if (!ForgeChunkManager.getPersistentChunksFor(world).containsKey(chunk))
			ForgeChunkManager.forceChunk(ticket.ticket, chunk)
		
		ticket.requestedChunkTimers[chunk] = 100
		
		if (newTicket)
			ticketsForDim += ticket
		
		return true
	}
	
	@SubscribeEvent
	fun onServerTick(e: ServerTickEvent) {
		if (e.phase != TickEvent.Phase.END) return
		
		ticketsStore.forEach { (_, alfheimTicketsPerDim) ->
			val iTicket = alfheimTicketsPerDim.iterator()
			
			while (iTicket.hasNext()) {
				val ticket = iTicket.next()
				val toRemove = mutableSetOf<ChunkCoordIntPair>()
				
				for (key in ticket.requestedChunkTimers.keys) {
					val timer = ticket.requestedChunkTimers[key]!! - 1
					ticket.requestedChunkTimers[key] = timer
					
					if (timer > 0) continue
					toRemove += key
					ForgeChunkManager.unforceChunk(ticket.ticket, key)
				}
				
				toRemove.forEach(ticket.requestedChunkTimers::remove)
				
				if (ticket.ticket.chunkList.isEmpty()) {
					ForgeChunkManager.releaseTicket(ticket.ticket)
					iTicket.remove()
				}
			}
		}
	}
	
	@SubscribeEvent
	fun onServerStopped(e: ServerStoppedEvent) {
		ticketsStore.clear()
	}
	
	override fun ticketsLoaded(tickets: MutableList<Ticket>, world: World) {
		tickets.mapTo(ticketsStore.computeIfAbsent(world.provider.dimensionId) { LinkedHashSet() }) {
			AlfheimTicket(it, it.chunkList.associateWithTo(LinkedHashMap()) { 100 })
		}
		ASJUtilities.log("Loaded ${tickets.size} [${ModInfo.MODID}] chunkloading ticket(s) for dim ${world.provider.dimensionId}")
	}
}

class AlfheimTicket(val ticket: Ticket, val requestedChunkTimers: LinkedHashMap<ChunkCoordIntPair, Int>)