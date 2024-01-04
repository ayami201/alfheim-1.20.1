package alfheim.common.block.tile.sub.flower

import alexsocol.asjlib.*
import alfheim.common.block.tile.sub.flower.AlfheimSignature.Companion.isOnSpecialSoil
import alfheim.common.entity.item.*
import alfheim.common.item.rod.ItemRodClicker
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.entity.IMerchant
import net.minecraft.entity.item.*
import net.minecraft.item.*
import net.minecraft.tileentity.TileEntityChest
import net.minecraft.util.*
import net.minecraft.village.MerchantRecipe
import net.minecraftforge.common.util.ForgeDirection
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.subtile.*
import vazkii.botania.common.block.decor.IFloatingFlower
import vazkii.botania.common.core.helper.InventoryHelper
import vazkii.botania.common.lib.LibMisc
import kotlin.math.min

class SubTileTradescantia: SubTileFunctional() {
	
	override fun onUpdate() {
		super.onUpdate()
		
		if (supertile.worldObj.isRemote) return
		if (redstoneSignal > 0 || mana < COST) return
		if (ticksExisted % (if (isOnSpecialSoil) 10 else 20) != 0) return
		
		val x = supertile.xCoord
		val y = supertile.yCoord
		val z = supertile.zCoord
		
		val buyer = ItemRodClicker.getFake(supertile.worldObj.provider.dimensionId)
		
		val merchants = getEntitiesWithinAABB(supertile.worldObj, IMerchant::class.java, supertile.boundingBox().expand(RANGE, 1, RANGE)).apply { shuffle() }
		if (merchants.isEmpty()) return
		
		val cashs = collectCash()
		if (cashs.isEmpty()) return
		
		var complete = false
		val cantTrade = { mana < COST || complete }
		
		for (dir in ForgeDirection.VALID_DIRECTIONS) if (cantTrade()) break else {
			val i = x + dir.offsetX
			val j = y + dir.offsetY
			val k = z + dir.offsetZ
			
			val inv = InventoryHelper.getInventory(supertile.worldObj, i, j, k) ?: continue
			
			val filters = getFilterForInventory(i, j, k)
			val boughts = ArrayList<ItemStack>()
			
			for (merchant in merchants) if (cantTrade()) break else {
				for (recipe in merchant.getRecipes(buyer).apply { shuffle() }) if (cantTrade()) break else {
					recipe as MerchantRecipe
					
					if (recipe.isRecipeDisabled) continue
					if (filters.isNotEmpty() && filters.none { ASJUtilities.isItemStackEqualData(it, recipe.itemToSell) }) continue
					
					val buy1 = recipe.itemToBuy.copy()
					if (buy1.hasTagCompound())
						ItemNBTHelper.setBoolean(buy1, ASJUtilities.TAG_ASJONLYNBT, true)
					else
						ItemNBTHelper.setBoolean(buy1, ASJUtilities.TAG_ASJIGNORENBT, true)
					
					val cash1i = cashs.indexOfFirst {
						val copy = it.copy()
						ItemNBTHelper.initNBT(copy)
						copy.stackSize >= buy1.stackSize && ASJUtilities.isItemStackEqualCrafting(buy1, copy)
					}
					
					if (cash1i == -1) continue
					
					var cash2i = -1
					
					if (recipe.hasSecondItemToBuy()) {
						val buy2 = recipe.secondItemToBuy.copy()
						if (buy2.hasTagCompound())
							ItemNBTHelper.setBoolean(buy2, ASJUtilities.TAG_ASJONLYNBT, true)
						else
							ItemNBTHelper.setBoolean(buy2, ASJUtilities.TAG_ASJIGNORENBT, true)
						
						cash2i = cashs.indexOfFirst {
							val copy = it.copy()
							ItemNBTHelper.initNBT(copy)
							copy.stackSize >= buy2.stackSize && ASJUtilities.isItemStackEqualCrafting(buy2, copy)
						}
						
						if (cash2i == -1) continue
					}
					
					if (InventoryHelper.testInventoryInsertion(inv, recipe.itemToSell, dir.opposite) != recipe.itemToSell.stackSize) continue
					
					boughts += recipe.itemToSell.copy()
					
					cashs[cash1i].stackSize -= recipe.itemToBuy.stackSize
					
					if (cash2i != -1)
						cashs[cash2i].stackSize -= recipe.secondItemToBuy.stackSize
					
					merchant.useRecipe(recipe)
					
					mana -= COST
					complete = true
				}
			}
			
			for (bought in boughts) {
				InventoryHelper.insertItemIntoInventory(inv, bought, dir.opposite, -1)
				
				if (bought.stackSize < 1) continue
				
				EntityItem(supertile.worldObj, x + dir.offsetX * 2 + 0.5, y + dir.offsetY * 2 + 0.5, z + dir.offsetZ * 2 + 0.5, bought).apply {
					setMotion(0.0, 0.0, 0.0)
					spawn()
				}
			}
		}
		
		val floating = supertile is IFloatingFlower
		for (cash in cashs) {
			if (cash.stackSize < 1) continue
			
			EntityItemImmortal(supertile.worldObj, x + 0.5, y + if (floating) 1.115 else 0.125, z + 0.5, cash).apply {
				setMotion(0.0, 0.0, 0.0)
				spawn()
			}
		}
	}
	
	fun getFilterForInventory(x: Int, y: Int, z: Int, recursiveForDoubleChest: Boolean = true): List<ItemStack> {
		val filters = ArrayList<ItemStack>()
		
		if (recursiveForDoubleChest) {
			val tileEntity = supertile.worldObj.getTileEntity(x, y, z)
			val chest = supertile.worldObj.getBlock(x, y, z)
		
			if (tileEntity is TileEntityChest)
				for (dir in LibMisc.CARDINAL_DIRECTIONS) if (supertile.worldObj.getBlock(x + dir.offsetX, y, z + dir.offsetZ) === chest) {
					filters.addAll(getFilterForInventory(x + dir.offsetX, y, z + dir.offsetZ, false))
					break
				}
		}
		
		val orientationToDir = intArrayOf(
			3, 4, 2, 5
		)
		
		for (dir in LibMisc.CARDINAL_DIRECTIONS) {
			val aabb = AxisAlignedBB.getBoundingBox((x + dir.offsetX).toDouble(), (y + dir.offsetY).toDouble(), (z + dir.offsetZ).toDouble(), (x + dir.offsetX + 1).toDouble(), (y + dir.offsetY + 1).toDouble(), (z + dir.offsetZ + 1).toDouble())
			val frames = getEntitiesWithinAABB(supertile.worldObj, EntityItemFrame::class.java, aabb)
			
			for (frame in frames) {
				if (frame.displayedItem == null) continue
				
				val orientation = frame.hangingDirection
				if (orientationToDir[orientation] == dir.ordinal) filters.add(frame.displayedItem)
			}
		}
		
		return filters
	}
	
	fun collectCash(): List<ItemStack> {
		val cashs = ArrayList<ItemStack>()
		
		val slowdown = slowdownFactor
		
		getEntitiesWithinAABB(supertile.worldObj, EntityItem::class.java, supertile.boundingBox()).forEach {
			if (it.isDead || it.entityItem == null || it.entityItem.stackSize < 1)
				return@forEach it.setDead()
			
			if (it.age >= slowdown)
				return@forEach
			
			cashs += it.entityItem.copy()
			
			it.entityItem.stackSize = 0
			it.setEntityItemStack(ItemStack(null as Item?))
			it.setDead()
		}
		
		getEntitiesWithinAABB(supertile.worldObj, EntityItemImmortal::class.java, supertile.boundingBox()).forEach {
			if (it is EntityItemImmortalRelic) return@forEach
			
			val stack = it.stack
			
			if (it.isDead || stack == null || stack.stackSize < 1)
				return@forEach it.setDead()
			
			cashs += stack.copy()
			
			it.stack = null
			it.setDead()
		}
		
		val sortedCash = ArrayList<ItemStack>()
		
		outer@ for (cash in cashs) {
			if (sortedCash.isEmpty()) {
				sortedCash.add(cash)
				continue
			}
			
			for (sort in sortedCash) {
				if (cash.stackSize <= 0) break
				
				val canAdd = sort.maxStackSize - sort.stackSize
				
				if (canAdd > 0 && cash.isItemEqual(sort) && ItemStack.areItemStackTagsEqual(cash, sort)) {
					val toAdd = min(canAdd, cash.stackSize)
					sort.stackSize += toAdd
					cash.stackSize -= toAdd
				}
			}
			
			if (cash.stackSize > 0) sortedCash.add(cash)
		}
		
		return sortedCash
	}
	
	override fun getRadius() = RadiusDescriptor.Square(toChunkCoordinates(), RANGE)
	
	override fun acceptsRedstone() = true
	
	override fun getColor() = 0xF444FF
	
	override fun getMaxMana() = 1000
	
	override fun getEntry() = AlfheimLexiconData.flowerTradescantia
	
	override fun getIcon(): IIcon? = BotaniaAPI.getSignatureForName("tradescantia").getIconForStack(null)
	
	companion object {
		const val COST = 50
		const val RANGE = 7
	}
}
