package alfheim.common.core.handler

import alexsocol.asjlib.*
import alexsocol.patcher.event.ServerStoppedEvent
import alfheim.AlfheimCore
import alfheim.api.ModInfo
import cpw.mods.fml.common.FMLLog
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.TickEvent
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent
import net.minecraft.nbt.NBTTagIntArray
import net.minecraft.world.*
import net.minecraftforge.common.ForgeChunkManager.*

object ChunkLoadingHandler: LoadingCallback {
	
	val ticketsStore = HashMap<Int, LinkedHashSet<AlfheimTicket>>()
	
	const val TAG_CHUNKS = "chunks"
	
	init {
		eventFML().eventForge()
		setForcedChunkLoadingCallback(AlfheimCore, this)
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
			val fTicket = requestTicket(AlfheimCore, world, Type.NORMAL) ?: run {
				FMLLog.bigWarning("Unable to allocate new chunkloading ticket for [${ModInfo.MODID}]! Please, expand the limit in FML configs or remove extra")
				return false
			}
			
			ticket = AlfheimTicket(fTicket, linkedMapOf())
			newTicket = true
		}
		
		if (!getPersistentChunksFor(world).containsKey(chunk)) {
			forceChunk(ticket.ticket, chunk)
			val nbt = ticket.ticket.modData
			val tagList = nbt.getTagList(TAG_CHUNKS, 11)
			val list = tagList.tagList
			if (list.none { it is NBTTagIntArray && it.func_150302_c()[0] == chunk.chunkXPos && it.func_150302_c()[1] == chunk.chunkZPos })
				tagList.appendTag(NBTTagIntArray(intArrayOf(chunk.chunkXPos, chunk.chunkZPos)))
			
			nbt.setTag(TAG_CHUNKS, tagList)
		}
		
		ticket.requestedChunkTimers[chunk] = 100
		
		if (newTicket)
			ticketsForDim += ticket
		
		return true
	}
	
	@SubscribeEvent
	fun onServerTick(e: ServerTickEvent) {
		if (e.phase != TickEvent.Phase.END) return
		
		ticketsStore.forEach { (_, alfheimTicketsPerDim) ->
			alfheimTicketsPerDim.iterator().onEach { ticket ->
				val toRemove = mutableSetOf<ChunkCoordIntPair>()
				
				for (key in ticket.requestedChunkTimers.keys) {
					val timer = ticket.requestedChunkTimers[key]!! - 1
					ticket.requestedChunkTimers[key] = timer
					
					if (timer > 0) continue
					toRemove += key
					unforceChunk(ticket.ticket, key)
					
					val nbt = ticket.ticket.modData
					val tagList = nbt.getTagList(TAG_CHUNKS, 11)
					tagList.tagList.removeAll {
						if (it !is NBTTagIntArray) return@removeAll false
						
						val (x, z) = it.func_150302_c()
						x == key.chunkXPos && z == key.chunkZPos
					}
					
					nbt.setTag(TAG_CHUNKS, tagList)
				}
				
				toRemove.forEach(ticket.requestedChunkTimers::remove)
				
				if (ticket.ticket.chunkList.isEmpty()) {
					releaseTicket(ticket.ticket)
					remove()
				}
			}
		}
	}
	
	@SubscribeEvent
	fun onServerStopped(e: ServerStoppedEvent) {
		ticketsStore.clear()
	}
	
	override fun ticketsLoaded(tickets: MutableList<Ticket>, world: World) {
		val forDim = ticketsStore.computeIfAbsent(world.provider.dimensionId) { LinkedHashSet() }
		
		tickets.forEach { ticket ->
			forDim.add(AlfheimTicket(ticket, linkedMapOf()))
			
			ticket.modData.getTagList(TAG_CHUNKS, 11).tagList.forEach {
				if (it !is NBTTagIntArray) return@forEach
				
				val (x, z) = it.func_150302_c()
				requestChunkLoad(world, x, z)
			}
		}
	}
}

class AlfheimTicket(val ticket: Ticket, val requestedChunkTimers: LinkedHashMap<ChunkCoordIntPair, Int>)