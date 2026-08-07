package alfheim.common.block.tile

import alexsocol.asjlib.*
import alexsocol.asjlib.extendables.block.*
import alexsocol.asjlib.math.*
import alfheim.api.*
import alfheim.api.crafting.recipe.*
import net.minecraft.entity.item.*
import net.minecraft.entity.player.*
import net.minecraft.item.*
import net.minecraft.nbt.*
import net.minecraftforge.common.util.*

class TileBarrel: ASJTile() {
	
	var amountLevel = 0
	var closed = true
	var data = NBTTagCompound()
	var name = ""
	var recipeId = -1
	var timer = 0
	
	// no save
	var redstoneLastTick = true
	
	val recipe: RecipeBarrel?
		get() = AlfheimAPI.barrelRecipes.getOrNull(recipeId)
	
	override fun updateEntity() {
		if (worldObj.isRemote) return
		
		var redstone = false
		for (dir in ForgeDirection.VALID_DIRECTIONS) {
			val (x, y, z) = Vector3.fromTileEntity(this).add(dir.offsetX, dir.offsetY, dir.offsetZ).mf()
			if (worldObj.getIndirectPowerLevelTo(x, y, z, dir.ordinal) <= 0) continue
			redstone = true
			break
		}
		
		if (redstone && !redstoneLastTick) {
			closed = !closed
			sync()
		}
		redstoneLastTick = redstone
		
		if (!closed) for (item in getEntitiesWithinAABB(worldObj, EntityItem::class.java, boundingBox(-0.125))) {
			if (item.isDead) continue
			
			val stack = item.entityItem ?: continue
			if (stack.stackSize <= 0) continue
			
			if (recipe == null) {
				if (!selectRecipeMatchingFirstInput(null, stack)) continue
			} else {
				if (!recipe!!.onInteractedWith(this, null, stack)) continue
			}
			
			sync()
			
			if (stack.stackSize > 0) break
			
			item.setEntityItemStack(null)
			item.setDead()
			
			break
		}
		
		if (timer > 0) --timer
		recipe?.serverTick(this)
	}
	
	fun selectRecipeMatchingFirstInput(player: EntityPlayer?, stack: ItemStack): Boolean {
		AlfheimAPI.barrelRecipes.forEachIndexed { index, variant -> 
			if (!variant.isInitStack(stack)) return@forEachIndexed
			
			recipeId = index
			recipe!!.onInteractedWith(this, player, stack)
			
			return true
		}
		
		return false
	}
	
	fun sync() {
		if (!ASJUtilities.isServer) return
		
		ASJUtilities.dispatchTEToNearbyPlayers(this)
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType())
	}
	
	fun reset() {
		amountLevel = 0
		data.tagMap.clear()
		recipeId = -1
		timer = 0
		sync()
	}
	
	override fun readCustomNBT(nbt: NBTTagCompound) {
		super.readCustomNBT(nbt)
		
		amountLevel = nbt.getInteger(TAG_AMOUNT_LEVEL)
		closed = nbt.getBoolean(TAG_CLOSED)
		data = nbt.getCompoundTag(TAG_DATA)
		name = nbt.getString(TAG_NAME)
		recipeId = nbt.getInteger(TAG_RECIPE)
		timer = nbt.getInteger(TAG_TIMER)
	}
	
	override fun writeCustomNBT(nbt: NBTTagCompound) {
		super.writeCustomNBT(nbt)
		
		nbt.setInteger(TAG_AMOUNT_LEVEL, amountLevel)
		nbt.setBoolean(TAG_CLOSED, closed)
		nbt.setTag(TAG_DATA, data)
		nbt.setString(TAG_NAME, name)
		nbt.setInteger(TAG_RECIPE, recipeId)
		nbt.setInteger(TAG_TIMER, timer)
	}
	
	override fun getRenderBoundingBox() = boundingBox(1)
	
	companion object {
		const val TAG_AMOUNT_LEVEL = "amountLevel"
		const val TAG_CLOSED = "closed"
		const val TAG_DATA = "data"
		const val TAG_NAME = "name"
		const val TAG_RECIPE = "recipe"
		const val TAG_TIMER = "timer"
	}
}