package alfheim.common.core.handler

import alexsocol.asjlib.*
import alfheim.common.block.tile.TileTradePortal
import alfheim.common.world.data.CustomWorldData.Companion.customData
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.TickEvent
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.server.MinecraftServer
import vazkii.botania.common.block.tile.TileAlfPortal
import java.util.Random

object TradingGiftsHandler {
	
	init {
		eventForge().eventFML()
	}
	
	fun addAlfheimGift(stack: ItemStack, random: Random) {
		addGift(stack, random, TAG_ALFHEIM)
	}
	
	fun addMidgardGift(stack: ItemStack, random: Random) {
		addGift(stack, random, TAG_MIDGARD)
	}
	
	private fun addGift(stack: ItemStack, random: Random, tag: String) {
		applyToPerWorldGiftsList(tag) {
			val giftTag = NBTTagCompound()
			Gift(stack, ASJUtilities.randInBounds(6000, 36000, random)).writeToNBT(giftTag)
			appendTag(giftTag)
		}
	}
	
	private fun applyToPerWorldGiftsList(tag: String, doYourShit: NBTTagList.() -> Unit) {
		val nbtData = MinecraftServer.getServer().worldServerForDimension(0).customData.nbtData
		val gifts = nbtData.getCompoundTag(TAG_GIFTS) as NBTTagCompound
		val listOfGiftsInWorld = gifts.getTagList(tag, 10)
		
		listOfGiftsInWorld.doYourShit()
		
		gifts.setTag(tag, listOfGiftsInWorld)
		nbtData.setTag(TAG_GIFTS, gifts)
	}
	
	@SubscribeEvent
	fun onServerTick(event: TickEvent.ServerTickEvent) {
		if (event.phase != TickEvent.Phase.END) return
		
		arrayOf(TAG_ALFHEIM, TAG_MIDGARD).forEach { tag ->
			applyToPerWorldGiftsList(tag) {
				repeat(tagCount()) {
					val giftTag = tagList[it] as NBTTagCompound
					val gift = Gift.readFromNBT(giftTag)
					--gift.lifespan
					gift.writeToNBT(giftTag)
				}
			}
		}
	}
	
	@SubscribeEvent
	fun onWorldTick(event: TickEvent.WorldTickEvent) {
		if (event.phase != TickEvent.Phase.START) return
		
		val world = event.world
		
		when (world.provider.dimensionId) {
			AlfheimConfigHandler.dimensionIDAlfheim -> {
				val portals = world.loadedTileEntityList.filterIsInstance<TileTradePortal>().filter { it.ticksOpen > 60 }
				if (portals.isEmpty()) return
				
				applyToPerWorldGiftsList(TAG_ALFHEIM) {
					val toRemove = ArrayList<Int>()
					
					repeat(tagCount()) {
						val giftTag = tagList[it] as NBTTagCompound
						val gift = Gift.readFromNBT(giftTag)
						if (gift.lifespan > 0) return@repeat
						
						portals.random(world.rand)?.spawnItem(gift.stack)
						toRemove.add(it)
					}
					
					toRemove.forEach(tagList::removeAt)
				}
			}
			
			0                                       -> {
				val portals = world.loadedTileEntityList.filterIsInstance<TileAlfPortal>().filter { it.ticksOpen > 60 }
				if (portals.isEmpty()) return
				
				applyToPerWorldGiftsList(TAG_MIDGARD) {
					val toRemove = ArrayList<Int>()
					
					repeat(tagCount()) {
						val giftTag = tagList[it] as NBTTagCompound
						val gift = Gift.readFromNBT(giftTag)
						if (gift.lifespan > 0) return@repeat
						
						portals.random(world.rand)?.spawnItem(gift.stack)
						toRemove.add(it)
					}
					
					toRemove.forEach(tagList::removeAt)
				}
			}
			
			else                                    -> return
		}
	}
	
	const val TAG_GIFTS = "gifts"
	const val TAG_ALFHEIM = "alfheim"
	const val TAG_MIDGARD = "midgard"
	
	data class Gift(val stack: ItemStack, var lifespan: Int) {
		
		fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
			val stackNBT = NBTTagCompound()
			stack.writeToNBT(stackNBT)
			nbt.setTag(TAG_STACK, stackNBT)
			nbt.setInteger(TAG_LIFESPAN, lifespan)
			return nbt
		}
		
		companion object {
			const val TAG_LIFESPAN = "lifespan"
			const val TAG_STACK = "stack"
			
			fun readFromNBT(nbt: NBTTagCompound) = Gift(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag(TAG_STACK)), nbt.getInteger(TAG_LIFESPAN))
		}
	}
}