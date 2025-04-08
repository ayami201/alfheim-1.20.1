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
			val iTicket = alfheimTicketsPerDim.iterator()
			
			while (iTicket.hasNext()) {
				val ticket = iTicket.next()
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
						it.func_150302_c()[0] == key.chunkXPos && it.func_150302_c()[1] == key.chunkZPos
					}
					nbt.setTag(TAG_CHUNKS, tagList)
				}
				
				toRemove.forEach(ticket.requestedChunkTimers::remove)
				
				if (ticket.ticket.chunkList.isEmpty()) {
					releaseTicket(ticket.ticket)
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
		tickets.forEach { ticket ->
			ticket.modData.getTagList(TAG_CHUNKS, 11).tagList.forEach {
				if (it is NBTTagIntArray)
					requestChunkLoad(world, it.func_150302_c()[0], it.func_150302_c()[1])
			}
		}
	}
}

class AlfheimTicket(val ticket: Ticket, val requestedChunkTimers: LinkedHashMap<ChunkCoordIntPair, Int>)