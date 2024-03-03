package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.ASJTile
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ChunkCoordinates
import vazkii.botania.api.mana.IManaReceiver
import vazkii.botania.api.wand.IWandBindable
import vazkii.botania.common.block.tile.mana.TilePool
import kotlin.math.min

class TileWorldTree: ASJTile(), IManaReceiver, IWandBindable {
	
	var name = ""
	var mana = 0
	var boundList = arrayOfNulls<ChunkCoordinates>(16)
	
	override fun updateEntity() {
		for ((id, it) in boundList.withIndex()) {
			val (x, y, z) = it ?: continue
			if (worldObj.getTileEntity(x, y, z) !is TileWorldTree) boundList[id] = null
		}
	}
	
	fun onActivated(player: EntityPlayer, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		val stack = player.heldItem
		
		if (stack?.item === Items.name_tag!!) {
			if (!stack.hasDisplayName()) return false
			
			name = stack.displayName
			ASJUtilities.dispatchTEToNearbyPlayers(this)
			return true
		}
		
		if (stack != null) return false
		
		val idTo = appleCoords.indexOfFirst { hitX * 16 in it.first && hitY * 16 in it.second && hitZ * 16 in it.third }
		if (idTo == -1) return false
		
		val (x, y, z) = boundList[idTo] ?: return false
		val other = worldObj.getTileEntity(x, y, z) as? TileWorldTree ?: return false
		
		if (player.isSneaking)
			ASJUtilities.say(player, other.name)
		else
			player.setPositionAndUpdate(x + 0.5, y + 0.5, z + 0.5)
		
		return true
	}
	
	override fun getCurrentMana() = mana
	
	override fun isFull() = mana >= MAX_MANA
	
	override fun recieveMana(add: Int) {
		mana = min(mana + add, 0)
	}
	
	override fun canRecieveManaFromBursts() = !isFull()
	
	override fun getBinding() = null
	
	override fun canSelect(player: EntityPlayer?, wand: ItemStack?, x: Int, y: Int, z: Int, side: Int) = true
	
	override fun bindTo(player: EntityPlayer, wand: ItemStack?, x: Int, y: Int, z: Int, side: Int): Boolean {
		val freeIndex = boundList.indexOfFirst { it == null }
		if (freeIndex == -1) {
			ASJUtilities.say(player, "alfheimmisc.treefull")
			return false
		}
		
//		val other = player.worldObj.getTileEntity(x, y, z) as? TileWorldTree ?: return false
//		val freeIndexOther = other.boundList.indexOfFirst { it == null }
//		if (freeIndexOther == -1) {
//			ASJUtilities.say(player, "alfheimmisc.treefull.other")
//			return false
//		}
		
		boundList[freeIndex] = ChunkCoordinates(x, y, z)
//		other.boundList[freeIndexOther] = ChunkCoordinates(xCoord, yCoord, zCoord)
		
		return true
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		nbt.setInteger(TAG_MANA, mana)
		nbt.setString(TAG_NAME, name)
		
		for ((id, it) in boundList.withIndex()) {
			if (it == null) continue
			nbt.setIntArray("$TAG_BIND$id", intArrayOf(it.posX, it.posY, it.posZ))
		}
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		mana = nbt.getInteger(TAG_MANA)
		name = nbt.getString(TAG_NAME)
		
		for (id in boundList.indices) {
			if (!nbt.hasKey("$TAG_BIND$id")) {
				boundList[id] = null
				continue
			}
			
			val (x, y, z) = nbt.getIntArray("$TAG_BIND$id")
			boundList[id] = ChunkCoordinates(x, y, z)
		}
	}
	
	companion object {
		
		const val TAG_BIND = "bind"
		const val TAG_MANA = "mana"
		const val TAG_NAME = "name"
		
		const val MAX_MANA = TilePool.MAX_MANA_DILLUTED
		
		val appleCoords = arrayOf(
			3f..5f   to 8.25f..10.25f  with 15f..15f,
			6f..8f   to 9.25f..11.25f  with 15f..15f,
			9f..11f  to 10.75f..12.75f with 15f..15f,
			10f..12f to 8.25f..10.25f  with 15f..15f,
			
			15f..15f to 8.25f..10.25f  with 11f..13f,
			15f..15f to 9.25f..11.25f  with 8f..10f,
			15f..15f to 10.75f..12.75f with 5f..7f,
			15f..15f to 8.25f..10.25f  with 3f..5f,
			
			11f..13f     to 9.25f..11.25f  with 1f..1f,
			8f..10f      to 8.25f..10.25f  with 1f..1f,
			5.75f..7.75f to 10.75f..12.75f with 1f..1f,
			3f..5f       to 8.25f..10.25f  with 1f..1f,
			
			1f..1f to 9.25f..11.25f with 3f..5f,
			1f..1f to 11f..13f      with 6f..8f,
			1f..1f to 8.25f..10.25f with 8f..10f,
			1f..1f to 9.25f..11.25f with 11f..13f,
		)
	}
}
