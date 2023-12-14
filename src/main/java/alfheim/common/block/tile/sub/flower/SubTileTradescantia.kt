package alfheim.common.block.tile.sub.flower

import alexsocol.asjlib.*
import alfheim.common.entity.item.EntityItemImmortal
import alfheim.common.item.rod.ItemRodClicker
import alfheim.common.lexicon.AlfheimLexiconData
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.item.EntityItemFrame
import net.minecraft.entity.passive.EntityVillager
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.*
import net.minecraft.util.*
import net.minecraft.village.MerchantRecipe
import net.minecraftforge.common.util.ForgeDirection
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.subtile.*
import vazkii.botania.common.core.helper.InventoryHelper
import vazkii.botania.common.lib.LibMisc

class SubTileTradescantia: SubTileFunctional() {
	
	override fun onUpdate() {
		super.onUpdate()
		
		if (supertile.worldObj.isRemote) return
		if (redstoneSignal > 0 || mana < COST) return
		
		val x = supertile.xCoord
		val y = supertile.yCoord
		val z = supertile.zCoord
		
		val buyer = ItemRodClicker.getFake(supertile.worldObj.provider.dimensionId)
		
		val cashs = collectCash()
		if (cashs.isEmpty()) return
		
		val villagers = getEntitiesWithinAABB(supertile.worldObj, EntityVillager::class.java, supertile.boundingBox().expand(RANGE, 1, RANGE))
		if (villagers.isEmpty()) return
		
		for (dir in ForgeDirection.VALID_DIRECTIONS) if (mana < COST) break else {
			val i = x + dir.offsetX
			val j = y + dir.offsetY
			val k = z + dir.offsetZ
			
			val inv = InventoryHelper.getInventory(supertile.worldObj, i, j, k) ?: continue
			
			val filters = getFilterForInventory(i, j, k)
			val boughts = ArrayList<ItemStack>()
			
			for (villager in villagers) if (mana < COST) break else {
				for (recipe in villager.getRecipes(buyer)) if (mana < COST) break else {
					recipe as MerchantRecipe
					
					if (recipe.isRecipeDisabled) continue
					if (filters.isNotEmpty() && filters.none { ASJUtilities.isItemStackEqualData(it, recipe.itemToSell) }) continue
					
					val buy1 = recipe.itemToBuy.copy()
					if (buy1.hasTagCompound())
						ItemNBTHelper.setBoolean(buy1, ASJUtilities.TAG_ASJONLYNBT, true)
					else
						ItemNBTHelper.setBoolean(buy1, ASJUtilities.TAG_ASJIGNORENBT, true)
					
					val cash1i = cashs.indexOfFirst {
						if (buy1.hasTagCompound()) ItemNBTHelper.initNBT(it)
						it.stackSize >= buy1.stackSize && ASJUtilities.isItemStackEqualCrafting(buy1, it)
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
							if (buy2.hasTagCompound()) ItemNBTHelper.initNBT(it)
							it.stackSize >= buy2.stackSize && ASJUtilities.isItemStackEqualCrafting(buy2, it)
						}
						
						if (cash2i == -1) continue
					}
					
					boughts += recipe.itemToSell.copy()
					
					cashs[cash1i].stackSize -= recipe.itemToBuy.stackSize
					
					if (cash2i != -1)
						cashs[cash2i].stackSize -= recipe.secondItemToBuy.stackSize
					
					mana -= COST
					villager.useRecipe(recipe)
				}
			}
			
			for (bought in boughts) {
				InventoryHelper.insertItemIntoInventory(inv, bought, dir.opposite, -1)
				
				if (bought.stackSize < 1) continue
				
				EntityItem(supertile.worldObj, x + dir.offsetX * 2 + 0.5, y + dir.offsetY * 2 + 0.5, z + dir.offsetZ * 2 + 0.5, bought).apply {
					setMotion(0.0,0.0,0.0)
					spawn()
				}
			}
		}
		
		for (cash in cashs) {
			if (cash.stackSize < 1) continue
			
			EntityItemImmortal(supertile.worldObj, x + 0.5, y + 0.5, z + 0.5, cash).apply {
				setMotion(0.0,0.0,0.0)
				spawn()
			}
		}
	}
	
	fun getFilterForInventory(x: Int, y: Int, z: Int): List<ItemStack> {
		val filters = ArrayList<ItemStack>()
		val tileEntity = supertile.worldObj.getTileEntity(x, y, z)
		val chest = supertile.worldObj.getBlock(x, y, z)
		
		if (tileEntity is TileEntityChest)
			for (dir in LibMisc.CARDINAL_DIRECTIONS) if (supertile.worldObj.getBlock(x + dir.offsetX, y, z + dir.offsetZ) === chest) {
				filters.addAll(getFilterForInventory(x + dir.offsetX, y, z + dir.offsetZ))
				break
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
		val cash = ArrayList<ItemStack>()
		
		getEntitiesWithinAABB(supertile.worldObj, EntityItem::class.java, supertile.boundingBox()).forEach {
			if (it.isDead || it.entityItem == null || it.entityItem.stackSize < 1)
				return@forEach it.setDead()
			
			cash += it.entityItem.copy()
			
			it.setEntityItemStack(null)
			it.setDead()
		}
		
		getEntitiesWithinAABB(supertile.worldObj, EntityItemImmortal::class.java, supertile.boundingBox()).forEach {
			val stack = it.stack
			
			if (it.isDead || stack == null || stack.stackSize < 1)
				return@forEach it.setDead()
			
			cash += stack.copy()
			
			it.stack = null
			it.setDead()
		}
		
		return cash
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
