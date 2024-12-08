package alfheim.common.item.rod

import alexsocol.asjlib.*
import alexsocol.asjlib.ItemNBTHelper.getInt
import alexsocol.asjlib.ItemNBTHelper.getIntArray
import alexsocol.asjlib.ItemNBTHelper.getList
import alexsocol.asjlib.ItemNBTHelper.getNBT
import alexsocol.asjlib.ItemNBTHelper.setInt
import alexsocol.asjlib.ItemNBTHelper.setIntArray
import alexsocol.asjlib.ItemNBTHelper.setList
import alfheim.common.item.ItemMod
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.*
import net.minecraft.util.ChunkCoordinates
import net.minecraft.world.World
import net.minecraftforge.common.util.*
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import vazkii.botania.api.item.*
import vazkii.botania.api.mana.*
import vazkii.botania.client.core.handler.ItemsRemainingRenderHandler
import vazkii.botania.common.block.BlockCamo

class ItemRodSuperExchange: ItemMod("RodSuperExchange"), IManaUsingItem, IWireframeCoordinateListProvider {
	
	init {
		eventForge()
		maxStackSize = 1
		setFull3D()
	}
	
	override fun onItemRightClick(stack: ItemStack, world: World?, player: EntityPlayer): ItemStack {
		if (player.isSneaking) clear(stack)
		return stack
	}
	
	override fun onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		val block = world.getBlock(x, y, z)
		var meta = world.getBlockMetadata(x, y, z)
		
		if (player.isSneaking) {
			val tile = world.getTileEntity(x, y, z)
			if (tile != null) return false
			
			if (!BlockCamo.isValidBlock(block)) return false
			
			if (block.toItem()?.hasSubtypes == false) meta = 0
			
			val set = addBlock(stack, block, meta)
			player.setCurrentItemOrArmor(0, stack)
			displayRemainderCounter(player, stack, block, meta)
			return set
		}
		
		if (getSavedSwaps(stack).isNotEmpty()) return false
		
		val blocks = getBlocks(stack)
		val metas = getBlockMetas(stack)
		
		if (blocks.isEmpty()) return false
		
		val swaps = getBlocksToSwap(world, stack, blocks, metas, x, y, z, block, meta)
		if (swaps.isEmpty()) return false
		
		saveSwaps(stack, swaps)
		
		if (world.isRemote) player.swingItem()
		
		return false
	}
	
	@SubscribeEvent
	fun onLeftClick(e: PlayerInteractEvent) {
		if (e.action != PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) return
		
		val player = e.entityPlayer
		val stack = player.currentEquippedItem
		if (stack == null || stack.item !== this || !ManaItemHandler.requestManaExactForTool(stack, player, COST, false)) return
		
		val blocks = getBlocks(stack)
		val metas = getBlockMetas(stack)
		if (blocks.isEmpty()) return
		
		val i = ASJUtilities.randInBounds(0, blocks.size - 1, player.rng)
		
		if (!exchangeOneBlock(e.world, player, e.x, e.y, e.z, stack, blocks[i], metas[i])) return
		ManaItemHandler.requestManaExactForTool(stack, player, COST, true)
	}
	
	override fun onUpdate(stack: ItemStack, world: World, entity: Entity?, something: Int, somethingelse: Boolean) {
		if (entity !is EntityPlayer) return
		
		val extraRange = getInt(stack, TAG_EXTRA_RANGE, 1)
		val extraRangeNew = if (IManaProficiencyArmor.Helper.hasProficiency(entity)) 3 else 1
		if (extraRange != extraRangeNew) setInt(stack, TAG_EXTRA_RANGE, extraRangeNew)
		
		repeat(5) {
			val swaps = getSavedSwaps(stack)
			if (swaps.isEmpty()) return
			
			if (!ManaItemHandler.requestManaExactForTool(stack, entity, COST, false))
				return saveSwaps(stack, null)
			
			val blocks = getBlocks(stack)
			val metas = getBlockMetas(stack)
			if (blocks.isEmpty()) return
			
			val coords = swaps.removeAt(entity.rng.nextInt(swaps.size))
			val i = ASJUtilities.randInBounds(0, blocks.size - 1, entity.rng)
			val exchange = exchangeOneBlock(world, entity, coords.posX, coords.posY, coords.posZ, stack, blocks[i], metas[i])
			
			if (exchange) ManaItemHandler.requestManaExactForTool(stack, entity, COST, true)
			
			saveSwaps(stack, swaps)
		}
	}
	
	fun getBlocksToSwap(world: World, stack: ItemStack?, blockToSwaps: List<Block>, metasToSwap: IntArray, xc: Int, yc: Int, zc: Int, tb: Block?, tm: Int): MutableList<ChunkCoordinates> {
		// If we have no target block passed in, infer it to be
		// the block which the swapping is centered on (presumably the block
		// which the player is looking at)
		var targetBlock = tb
		var targetMeta = tm
		
		if (targetBlock == null) {
			targetBlock = world.getBlock(xc, yc, zc)
			targetMeta = world.getBlockMetadata(xc, yc, zc)
		}
		
		// Our result list
		val coordsList = ArrayList<ChunkCoordinates>()
		
		// We subtract 1 from the effective range as the center tile is included
		// So, with a range of 3, we are visiting tiles at -2, -1, 0, 1, 2
		val effRange = RANGE + getInt(stack, TAG_EXTRA_RANGE, 1) - 1
		
		// Iterate in all 3 dimensions through our possible positions.
		for (offsetX in -effRange..effRange)
			for (offsetY in -effRange..effRange)
				for (offsetZ in -effRange..effRange) {
					val x = xc + offsetX
					val y = yc + offsetY
					val z = zc + offsetZ
					
					val currentBlock = world.getBlock(x, y, z)
					val currentMeta = world.getBlockMetadata(x, y, z)
					
					// If this block is not our target, ignore it, as we don't need
					// to consider replacing it
					if (currentBlock !== targetBlock || currentMeta != targetMeta) continue
					
					// If this block is already the block we're swapping to,
					// we don't need to swap again
					if (blockToSwaps.size == 1 && currentBlock === blockToSwaps[0] && currentMeta == metasToSwap[0]) continue
					
					// Check to see if the block is visible on any side:
					for (dir in ForgeDirection.VALID_DIRECTIONS) {
						val adjX = x + dir.offsetX
						val adjY = y + dir.offsetY
						val adjZ = z + dir.offsetZ
						
						val adjBlock = world.getBlock(adjX, adjY, adjZ)
						
						// If the side of the adjacent block facing this block is
						// _not_ solid, then this block is considered "visible"
						// and should be replaced.
						// If there is a rendering-specific way to check for this,
						// that should be placed in preference to this.
						if (!adjBlock.isSideSolid(world, adjX, adjY, adjZ, dir.opposite)) {
							coordsList.add(ChunkCoordinates(x, y, z))
							break
						}
					}
				}
		
		return coordsList
	}
	
	fun exchangeOneBlock(world: World, player: EntityPlayer, x: Int, y: Int, z: Int, stack: ItemStack, blockToSet: Block, metaToSet: Int): Boolean {
		if (world.getTileEntity(x, y, z) != null) return false
		
		val placeStack = removeFromInventory(player, stack, blockToSet, metaToSet, false) ?: return false
		
		val blockAt = world.getBlock(x, y, z)
		val meta = world.getBlockMetadata(x, y, z)
		
		if (blockAt.isAir(world, x, y, z) ||
		    blockAt.getPlayerRelativeBlockHardness(player, world, x, y, z) <= 0 ||
		    !(blockAt !== blockToSet || meta != metaToSet))
			return false
		
		if (!world.isRemote) {
			if (!player.capabilities.isCreativeMode) {
				val drops = blockAt.getDrops(world, x, y, z, meta, 0)
				for (drop in drops) EntityItem(world, x + 0.5, y + 0.5, z + 0.5, drop).spawn()
				removeFromInventory(player, stack, blockToSet, metaToSet, true)
			}
			
			world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(blockAt) + (meta shl 12))
			world.setBlock(x, y, z, blockToSet, metaToSet, 3)
			blockToSet.onBlockPlacedBy(world, x, y, z, player, placeStack)
		}
		
		displayRemainderCounter(player, stack, blockToSet, metaToSet)
		return true
	}
	
	fun removeFromInventory(player: EntityPlayer, inv: IInventory, stack: ItemStack, block: Block, meta: Int, doit: Boolean): ItemStack? {
		val providers = ArrayList<ItemStack>()
		val blockItem = block.toItem()
		
		for (i in inv.sizeInventory - 1 downTo 0) {
			val invStack = inv[i] ?: continue
			val item = invStack.item
			if (item === blockItem && invStack.getItemDamage() == meta) {
				val retStack = invStack.copy()
				
				if (doit) {
					invStack.stackSize--
					if (invStack.stackSize == 0) inv.setInventorySlotContents(i, null)
				}
				
				return retStack
			}
			
			if (item is IBlockProvider) providers.add(invStack)
		}
		
		for (provStack in providers) {
			val prov = provStack.item as IBlockProvider
			if (prov.provideBlock(player, stack, provStack, block, meta, doit))
				return ItemStack(block, 1, meta)
		}
		
		return null
	}
	
	fun removeFromInventory(player: EntityPlayer, stack: ItemStack, block: Block, meta: Int, doit: Boolean): ItemStack? {
		if (player.capabilities.isCreativeMode) return ItemStack(block, 1, meta)
		return removeFromInventory(player, PlayerHandler.getPlayerBaubles(player), stack, block, meta, doit) ?: removeFromInventory(player, player.inventory, stack, block, meta, doit)
	}
	
	fun getInventoryItemCount(player: EntityPlayer, stack: ItemStack, block: Block, meta: Int): Int {
		if (player.capabilities.isCreativeMode) return -1
		
		val baubleCount = getInventoryItemCount(player, PlayerHandler.getPlayerBaubles(player), stack, block, meta)
		if (baubleCount == -1) return -1
		val count = getInventoryItemCount(player, player.inventory, stack, block, meta)
		return if (count == -1) -1 else count + baubleCount
	}
	
	fun getInventoryItemCount(player: EntityPlayer, inv: IInventory, stack: ItemStack, block: Block, meta: Int): Int {
		if (player.capabilities.isCreativeMode) return -1
		var count = 0
		for (i in 0 until inv.sizeInventory) {
			val invStack = inv[i] ?: continue
			val item = invStack.item
			if (item === getItemFromBlock(block) && invStack.getItemDamage() == meta) count += invStack.stackSize
			if (item !is IBlockProvider) continue
			val prov = item as IBlockProvider
			val provCount = prov.getBlockCount(player, stack, invStack, block, meta)
			if (provCount == -1) return -1
			count += provCount
		}
		return count
	}
	
	fun displayRemainderCounter(player: EntityPlayer, stack: ItemStack, block: Block, meta: Int) {
		val count = getInventoryItemCount(player, stack, block, meta)
		if (!player.worldObj.isRemote) ItemsRemainingRenderHandler.set(ItemStack(block, 1, meta), count)
	}
	
	override fun addInformation(stack: ItemStack, player: EntityPlayer?, list: MutableList<Any?>, adv: Boolean) {
		val map = LinkedHashMap<Pair<Block, Int>, Int>()
		
		val blocks = getBlocks(stack)
		val metas = getBlockMetas(stack)
		
		blocks.forEachIndexed { id, it ->
			val key = it to metas[id]
			map[key] = map.computeIfAbsent(key) { 0 } + 1
		}
		
		map.forEach { (e, count) ->
			addStringToTooltip(list, "&a${ItemStack(e.first, 1, e.second).displayName}${if (count > 1) " &7x$count" else ""}")
		}
	}
	
	// NBT
	
	fun saveSwaps(stack: ItemStack, swaps: List<ChunkCoordinates>?) {
		if (swaps.isNullOrEmpty())
			return getNBT(stack).removeTag(TAG_SWAPS)
		
		val list = NBTTagList()
		swaps.forEach {
			val (x, y, z) = it
			list.appendTag(NBTTagIntArray(intArrayOf(x, y, z)))
		}
		setList(stack, TAG_SWAPS, list)
	}
	
	fun getSavedSwaps(stack: ItemStack): MutableList<ChunkCoordinates> {
		return getList(stack, TAG_SWAPS, Constants.NBT.TAG_INT_ARRAY).tagList.mapTo(mutableListOf()) {
			val (x, y, z) = (it as NBTTagIntArray).func_150302_c()
			ChunkCoordinates(x, y, z)
		}
	}
	
	fun clear(stack: ItemStack) {
		getNBT(stack).apply {
			removeTag(TAG_BLOCK_NAMES)
			removeTag(TAG_BLOCK_METAS)
		}
	}
	
	fun addBlock(stack: ItemStack, block: Block, meta: Int): Boolean {
		val list = getRawBlocks(stack)
		list.appendTag(NBTTagString(Block.blockRegistry.getNameForObject(block)))
		
		setList(stack, TAG_BLOCK_NAMES, list)
		setIntArray(stack, TAG_BLOCK_METAS, getIntArray(stack, TAG_BLOCK_METAS) + meta)
		
		return true
	}
	
	fun getRawBlocks(stack: ItemStack): NBTTagList {
		return getList(stack, TAG_BLOCK_NAMES, Constants.NBT.TAG_STRING)
	}
	
	fun getBlocks(stack: ItemStack): List<Block> {
		return getRawBlocks(stack).tagList.mapNotNull { Block.getBlockFromName((it as NBTTagString).func_150285_a_()) }
	}
	
	fun getBlockMetas(stack: ItemStack): IntArray {
		return getIntArray(stack, TAG_BLOCK_METAS)
	}
	
	// RENDER
	
	@SideOnly(Side.CLIENT)
	override fun getWireframesToDraw(player: EntityPlayer, stack: ItemStack): List<ChunkCoordinates>? {
		val holding = player.currentEquippedItem
		if (holding != stack) return null
		
		val blocks = getBlocks(stack)
		val metas = getBlockMetas(stack)
		if (blocks.isEmpty()) return null
		
		val pos = Minecraft.getMinecraft().objectMouseOver ?: return null
		val x = pos.blockX
		val y = pos.blockY
		val z = pos.blockZ
		
		var swaps = getSavedSwaps(stack)
		if (swaps.isEmpty()) {
			if (player.worldObj.isAirBlock(x, y, z))
				return null
			
			swaps = getBlocksToSwap(player.worldObj, stack, blocks, metas, x, y, z, null, 0)
		}
		
		return swaps
	}
	
	override fun usesMana(stack: ItemStack?) = true
	
	companion object {
		private const val RANGE = 5
		private const val COST = 40
		private const val TAG_BLOCK_NAMES = "blockNames"
		private const val TAG_BLOCK_METAS = "blockMetas"
		private const val TAG_SWAPS = "swaps"
		private const val TAG_EXTRA_RANGE = "extraRange"
	}
}