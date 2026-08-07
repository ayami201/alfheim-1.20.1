package alfheim.common.item

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.patcher.handler.PlayerReachDistanceHandler
import alfheim.client.gui.ItemsRemainingRenderHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import vazkii.botania.api.item.IBlockProvider
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.common.core.handler.ConfigHandler
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.item.rod.ItemExchangeRod
import kotlin.math.floor

class ItemAstrolabe: ItemMod("Astrolabe") {
	
	init {
		maxStackSize = 1
		eventForge()
	}
	
	@SubscribeEvent
	fun selectBlock(e: PlayerInteractEvent) {
		val player = e.entityPlayer
		if (e.action != PlayerInteractEvent.Action.LEFT_CLICK_BLOCK || !player.isSneaking) return
		
		val stack = player.heldItem ?: return
		if (stack.item !== this) return
		
		val block = e.world.getBlock(e.x, e.y, e.z)
		val meta = e.world.getBlockMetadata(e.x, e.y, e.z)
		
		if (block.hasTileEntity(meta))
			return
		
		if (setBlock(stack, block, meta))
			displayRemainderCounter(player, stack)
	}
	
	override fun onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		if (world.isRemote) return false
		
		val did = placeAllBlocks(stack, player, x, y, z, side, hitX, hitY, hitZ)
		
		if (did) {
			displayRemainderCounter(player, stack)
			if (!world.isRemote)
				player.swingItem()
		}
		
		return did
	}
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		if (!player.isSneaking || ASJUtilities.getSelectedBlock(player, PlayerReachDistanceHandler.getReachDistance(player), false) != null) return stack
		
		val size = getSize(stack)
		val newSize = if (size == 11) 3 else size + 2
		setSize(stack, newSize)
		
		if (world.isRemote && player === mc.thePlayer) {
			ItemsRemainingRenderHandler.set(stack, "${newSize}x$newSize")
			player.playSoundAtEntity("random.orb", 0.1f, 0.5f * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7f + 1.8f))
		}
		
		return stack
	}
	
	fun placeAllBlocks(stack: ItemStack, player: EntityPlayer, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		val blocksToPlace = getBlocksToPlace(stack, player, x, y, z, side)
		
		if (!hasBlocks(stack, player, blocksToPlace.size))
			return false
		
		val stackToPlace = ItemStack(getBlock(stack), 1, getBlockMeta(stack))
		for (v in blocksToPlace)
			placeBlockAndConsume(player, stack, stackToPlace, v.x.I, v.y.I, v.z.I, side, hitX, hitY, hitZ)
		
		return true
	}
	
	private fun placeBlockAndConsume(player: EntityPlayer, requestor: ItemStack, blockToPlace: ItemStack, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float) {
		if (blockToPlace.item == null) return
		
		if (!ManaItemHandler.requestManaExact(requestor, player, 320, true)) return
		
		val world = player.worldObj
		
		var (stackToPlace, slot) = findStack(player, blockToPlace, requestor)
		if (stackToPlace == null) {
			if (!player.capabilities.isCreativeMode) return
			if (blockToPlace.block.hasTileEntity(blockToPlace.meta)) return
		}
		
		stackToPlace = stackToPlace ?: blockToPlace.copy()!!
		
		val oldSize = stackToPlace.stackSize
		if (!stackToPlace.tryPlaceItemIntoWorld(player, world, x, y, z, side, hitX, hitY, hitZ)) return
		
		if (!player.capabilities.isCreativeMode) {
			if (stackToPlace.stackSize <= 0 && slot != -1)
				player.inventory[slot] = null
			
			player.inventoryContainer.detectAndSendChanges()
		} else if (oldSize != stackToPlace.stackSize)
			stackToPlace.stackSize = oldSize
		
		if (!world.isRemote && ConfigHandler.blockBreakParticles && ConfigHandler.blockBreakParticlesTool)
			world.playAuxSFX(2001, x, y, z, stackToPlace.block.id + (stackToPlace.meta shl 12))
	}
	
	fun findStack(player: EntityPlayer, blockToPlace: ItemStack, requestor: ItemStack): Pair<ItemStack?, Int> {
		val blockProviders = ArrayList<ItemStack>()
		
		for (i in 0 until player.inventory.sizeInventory) {
			val stackInSlot = player.inventory[i]
			if (stackInSlot != null && stackInSlot.stackSize > 0 && stackInSlot.item === blockToPlace.item && stackInSlot.meta == blockToPlace.meta)
				return stackInSlot to i
			
			if (stackInSlot != null && stackInSlot.stackSize > 0 && stackInSlot.item is IBlockProvider)
				blockProviders.add(stackInSlot)
		}
		
		for (providerStack in blockProviders) {
			val prov = providerStack.item as IBlockProvider
			
			if (prov.provideBlock(player, requestor, providerStack, blockToPlace.block, blockToPlace.meta, false)) {
				prov.provideBlock(player, requestor, providerStack, blockToPlace.block, blockToPlace.meta, true)
				return blockToPlace.copy() to -1
			}
		}
		
		return null to -1
	}
	
	fun displayRemainderCounter(player: EntityPlayer, stack: ItemStack) {
		val block = getBlock(stack)
		val meta = getBlockMeta(stack)
		val count = ItemExchangeRod.getInventoryItemCount(player, stack, block, meta)
		
		if (player.worldObj.isRemote && player === mc.thePlayer)
			ItemsRemainingRenderHandler.set(ItemStack(block, 1, meta), count)
	}
	
	private fun setBlock(stack: ItemStack?, block: Block, meta: Int): Boolean {
		if (block !== Blocks.air) {
			ItemNBTHelper.setString(stack!!, TAG_BLOCK_NAME, Block.blockRegistry.getNameForObject(block))
			ItemNBTHelper.setInt(stack, TAG_BLOCK_META, meta)
			return true
		}
		return false
	}
	
	@SideOnly(Side.CLIENT)
	override fun addInformation(stack: ItemStack?, player: EntityPlayer?, tooltip: MutableList<Any?>, adv: Boolean) {
		val block = getBlock(stack)
		val size = getSize(stack)
		
		tooltip.add("$size x $size")
		if (block != null && block !== Blocks.air) tooltip.add(ItemStack(block, 1, getBlockMeta(stack)).displayName)
	}
	
	companion object {
		
		private const val TAG_BLOCK_NAME = "blockName"
		private const val TAG_BLOCK_META = "blockMeta"
		private const val TAG_SIZE = "size"
		
		fun hasBlocks(stack: ItemStack?, player: EntityPlayer, required: Int): Boolean {
			if (player.capabilities.isCreativeMode)
				return true
			
			val block = getBlock(stack)
			val meta = getBlockMeta(stack)
			val stackToPlace = ItemStack(block, 1, meta)
			
			var current = 0
			val blockProviders = ArrayList<ItemStack>()
			repeat(player.inventory.sizeInventory) {
				val stackInSlot = player.inventory[it] ?: return@repeat
				
				if (stackInSlot.stackSize > 0 && stackInSlot.item === stackToPlace.item && stackInSlot.meta == stackToPlace.meta) {
					current += stackInSlot.stackSize
					if (current >= required)
						return true
				}
				
				if (stackInSlot.stackSize > 0 && stackInSlot.item is IBlockProvider)
					blockProviders.add(stackInSlot)
			}
			
			for (providerStack in blockProviders) {
				val provider = providerStack.item as IBlockProvider
				val count = provider.getBlockCount(player, stack, providerStack, block, meta)
				if (count == -1)
					return true
				
				current += count
				
				if (current >= required)
					return true
			}
			
			return false
		}
		
		fun getBlocksToPlace(stack: ItemStack?, player: EntityPlayer, x: Int, y: Int, z: Int, side: Int): Set<Vector3> {
			val coords = HashSet<Vector3>()
			
			val p = Vector3(x, y, z)
			val range = (getSize(stack) xor 1) / 2
			
			val dir = ForgeDirection.getOrientation(side)
			val rot = floor(player.rotationYaw / 90.0 + 0.5).I and 3
			val rotationDir = if (rot == 0) ForgeDirection.SOUTH else if (rot == 1) ForgeDirection.WEST else if (rot == 2) ForgeDirection.NORTH else ForgeDirection.EAST
			
			val pitchedVertically = player.rotationPitch > 60 || player.rotationPitch < -60
			
			val axisX = rotationDir == ForgeDirection.WEST || rotationDir == ForgeDirection.EAST
			val axisZ = rotationDir == ForgeDirection.NORTH || rotationDir == ForgeDirection.SOUTH
			
			val xOff = if (axisZ || pitchedVertically) range else 0
			val yOff = if (pitchedVertically) 0 else range
			val zOff = if (axisX || pitchedVertically) range else 0
			
			for (x in -xOff until xOff + 1) {
				for (y in 0 until yOff * 2 + 1) {
					for (z in -zOff until zOff + 1) {
						val xp = (p.x + x.D + dir.offsetX.D).I
						val yp = (p.y + y.D + dir.offsetY.D).I
						val zp = (p.z + z.D + dir.offsetZ.D).I
						
						val newPos = Vector3(xp.D, yp.D, zp.D)
						val block1 = player.worldObj.getBlock(xp, yp, zp)
						
						if (player.worldObj.isAirBlock(xp, yp, zp) || block1.isReplaceable(player.worldObj, xp, yp, zp))
							coords.add(newPos)
					}
				}
			}
			
			return coords
		}
		
		private fun setSize(stack: ItemStack, size: Int) {
			ItemNBTHelper.setInt(stack, TAG_SIZE, size or 1)
		}
		
		fun getSize(stack: ItemStack?): Int {
			return ItemNBTHelper.getInt(stack, TAG_SIZE, 3) or 1
		}
		
		fun getBlock(stack: ItemStack?): Block? {
			return Block.getBlockFromName(getBlockName(stack)) ?: return Blocks.air
		}
		
		fun getBlockName(stack: ItemStack?): String {
			return ItemNBTHelper.getString(stack, TAG_BLOCK_NAME, "")
		}
		
		fun getBlockMeta(stack: ItemStack?): Int {
			return ItemNBTHelper.getInt(stack, TAG_BLOCK_META, 0)
		}
	}
}