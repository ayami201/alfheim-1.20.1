package alfheim.common.block.tile.corporea

import alexsocol.asjlib.ASJUtilities
import alexsocol.asjlib.I
import alexsocol.asjlib.math.Vector3
import alfheim.common.block.AlfheimBlocks
import alfheim.common.core.helper.CorporeaAdvancedHelper
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.server.MinecraftServer
import vazkii.botania.api.corporea.*
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.common.block.tile.corporea.TileCorporeaBase
import kotlin.math.max

class TileCorporeaRat: TileCorporeaBase(), ICorporeaRequestor {
	
	val requestersNames = HashMap<Pair<String, Int>, MutableList<String>>()

	fun saveRequesterName(request: Any?, count: Int, commandSenderName: String) {
		if (request !is String) return
		requestersNames.computeIfAbsent(request to count) { ArrayList() }.add(commandSenderName)
	}
	
	override fun doCorporeaRequest(request: Any?, count: Int, spark: ICorporeaSpark) {
		if (request !is String) return
		
		val stacks = CorporeaHelper.requestItem(request, count, spark, true)
		if (stacks.isEmpty()) return
		
		spark.onItemsRequested(stacks)
		
		val missing = count - stacks.sumOf { it?.stackSize ?: 0 }
		
		val name = requestersNames[request to count]?.removeFirstOrNull() ?: ""
		val requestor = MinecraftServer.getServer()?.configurationManager?.func_152612_a(name)
		
		if (missing > 0)
			saveRequesterName(request, missing, name)
		
		for (stack in stacks) {
			if (stack == null) continue
			
			if (requestor == null) {
				CorporeaAdvancedHelper.putOrDrop(this, spark, stack)
				continue
			}
			
			var cost = manaRequired(Vector3.entityTileDistance(requestor, this), count)
			
			if (requestor.dimension != this.worldObj.provider.dimensionId)
				cost *= CROSSDIM_COST
			
			if (!ManaItemHandler.requestManaExact(ItemStack(Blocks.stone), requestor, cost, true)) {
				ASJUtilities.say(requestor, "alfheimmisc.quandex.res.nomana")
				CorporeaAdvancedHelper.putOrDrop(this, spark, stack)
				continue
			}
			
			if (!requestor.inventory.addItemStackToInventory(stack))
				requestor.dropPlayerItemWithRandomChoice(stack, true)
		}
	}
	
	fun manaRequired(distance: Double, count: Int) = (distance.I + BLOCK_COST) * MANA_BALANCE * max(1, count + SIZE_COST)
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		super.readCustomNBT(nbt)
		
		nbt.setInteger(TAG_REQUESTS_COUNT, requestersNames.size)
		requestersNames.entries.forEachIndexed { index, entry ->
			val (request, names) = entry
			val (text, count) = request
			nbt.setString(TAG_REQUEST_N_TEXT + index, text)
			nbt.setInteger(TAG_REQUEST_N_COUNT + index, count)
			
			nbt.setInteger(TAG_REQUEST_N_NAMES_COUNT + index, names.size)
			names.forEachIndexed { ni, name ->
				nbt.setString(TAG_REQUEST_N_NAME_N + index + "_" + ni, name)
			}
		}
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		super.writeCustomNBT(nbt)
		
		requestersNames.clear()
		repeat(nbt.getInteger(TAG_REQUESTS_COUNT)) {
			val text = nbt.getString(TAG_REQUEST_N_TEXT + it)
			val count = nbt.getInteger(TAG_REQUEST_N_COUNT + it)
			
			repeat(nbt.getInteger(TAG_REQUEST_N_NAMES_COUNT + it)) { ni ->
				saveRequesterName(text, count, nbt.getString(TAG_REQUEST_N_NAME_N + it + "_" + ni))
			}
		}
	}
	
	// UNUSED
	override fun getSizeInventory() = 0
	override fun getInventoryName() = AlfheimBlocks.corporeaRatBase.localizedName!!
	
	companion object {
		const val BLOCK_COST = 1
		const val MANA_BALANCE = 1
		const val SIZE_COST = 1
		const val CROSSDIM_COST = 10
		
		const val TAG_REQUESTS_COUNT = "requestsCount"
		const val TAG_REQUEST_N_TEXT = "requestText_"
		const val TAG_REQUEST_N_COUNT = "requestCount_"
		const val TAG_REQUEST_N_NAMES_COUNT = "requestNamesCount_"
		const val TAG_REQUEST_N_NAME_N = "requestName_"
	}
}

