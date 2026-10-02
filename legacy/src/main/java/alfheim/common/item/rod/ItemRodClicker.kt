package alfheim.common.item.rod

import alexsocol.asjlib.*
import alexsocol.asjlib.ItemNBTHelper.getBoolean
import alexsocol.asjlib.ItemNBTHelper.getLong
import alexsocol.asjlib.ItemNBTHelper.setBoolean
import alexsocol.asjlib.ItemNBTHelper.setLong
import alexsocol.asjlib.math.Vector3
import alfheim.api.lib.LibResourceLocations
import alfheim.client.core.helper.IconHelper
import alfheim.common.entity.boss.EntityFlugel
import alfheim.common.item.ItemMod
import com.mojang.authlib.GameProfile
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.common.gameevent.TickEvent
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent
import cpw.mods.fml.relauncher.Side
import net.minecraft.block.Block
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.*
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.*
import net.minecraft.item.*
import net.minecraft.server.MinecraftServer
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraft.world.WorldSettings.GameType
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.event.ForgeEventFactory
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent
import net.minecraftforge.event.entity.player.*
import net.minecraftforge.event.world.BlockEvent.BreakEvent
import vazkii.botania.api.item.*
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.common.block.tile.TileAvatar
import vazkii.botania.common.core.helper.InventoryHelper
import java.util.*
import kotlin.math.max

class ItemRodClicker: ItemMod("RodClicker"), IAvatarWieldable {
	
	init {
		maxStackSize = 1
		setFull3D()
	}
	
	override fun registerIcons(reg: IIconRegister) {
		itemIcon = IconHelper.forItem(reg, this, "Right")
		iconLeft = IconHelper.forItem(reg, this, "Left")
	}
	
	override fun getIconIndex(stack: ItemStack) = if (stack.isLeftClick) iconLeft else itemIcon!!
	
	override fun getIcon(stack: ItemStack, pass: Int) = getIconIndex(stack)
	
	override fun addInformation(stack: ItemStack, player: EntityPlayer?, list: MutableList<Any?>, adv: Boolean) {
		list.add(StatCollector.translateToLocal("${unlocalizedName}.leftclick.${stack.isLeftClick}"))
	}
	
	override fun onItemRightClick(stack: ItemStack, world: World?, player: EntityPlayer?): ItemStack {
		stack.isLeftClick = !stack.isLeftClick
		return stack
	}
	
	override fun onUpdate(stack: ItemStack, world: World?, holder: Entity?, slot: Int, inHand: Boolean) {
		stack.prevTick = -1
	}
	
	override fun onEntityItemUpdate(entity: EntityItem): Boolean {
		entity.entityItem.prevTick = -1
		return false
	}
	
	override fun onAvatarUpdate(avatar: IAvatarTile, wand: ItemStack) {
		val tile = avatar as? TileAvatar ?: return
		val world = tile.worldObj ?: return
		
		val (x, y, z) = Vector3.fromTileEntity(tile).I
		if (world.isRemote || !world.isBlockIndirectlyGettingPowered(x, y, z)) return
		
		if (tile.currentMana < COST_AVATAR) return
		
		var xl = x
		var yl = y
		var zl = z
		var s = tile.getBlockMetadata()
		
		when (s - 2) {
			0    -> zl -= 2
			2    -> xl -= 2
			3    -> xl += 2
			else -> zl += 2
		}
		
		val leftClick = wand.isLeftClick
		
		val entities = getEntitiesWithinAABB(world, Entity::class.java, getBoundingBox(xl, yl, zl).offset(0.5).expand(0.5))
		
		if (world.isAirBlock(xl, yl, zl) && !leftClick) {
			yl -= 1
			s = 1
		} else {
			s = ForgeDirection.entries[s].opposite.ordinal
		}
		
		val player = getFake(world.provider.dimensionId)
		player.isSneaking = !world.isAirBlock(x, y + 1, z)
		val (xOff, zOff, yaw) = when (tile.blockMetadata) {
			3    ->  0.0  to  0.25 with  0f
			4    -> -0.25 to  0.0  with  90f
			5    ->  0.25 to  0.0  with -90f
			else ->  0.0  to -0.25 with  180f
		}
		
		player.setPositionAndRotation(tile.xCoord + 0.5 + xOff, tile.yCoord.D, tile.zCoord + 0.5 + zOff, yaw, 0f)
		
		val currTick = world.totalWorldTime
		val prevTick = wand.prevTick
		wand.prevTick = currTick
		val skipped = if (prevTick == -1L) 0 else (currTick - prevTick).I
		
		val inv = InventoryHelper.getInventory(world, x, y - 1, z)
		equipPlayer(player, inv, skipped)
		
		try_ { // fukkit
			val playerPostTickEvent = PlayerTickEvent(TickEvent.Phase.END, player)
			playerPostTickEvent.side = Side.SERVER
			FMLCommonHandler.instance().bus().post(playerPostTickEvent)
		}
		
		val manaForCharging = max(0, tile.currentMana - COST_AVATAR)
		if (manaForCharging > 0) {
			val sent = if (ManaItemHandler.dispatchManaExact(wand, player, manaForCharging, true))
				manaForCharging
			else
				ManaItemHandler.dispatchMana(wand, player, manaForCharging, true)
			
			tile.recieveMana(-sent)
		}
		
		try_ { // fukkit x2
			var done = false
			
			if (leftClick) {
				run {
					entities.removeAll { !it.canAttackWithItem() }
					if (entities.isEmpty()) return@run
					
					val mods = player.heldItem?.attributeModifiers
					
					if (mods != null)
						player.getAttributeMap().applyAttributeModifiers(mods)
					
					entities.minByOrNull { Vector3.entityDistancePlane(player, it) }?.let { player.attackTargetEntityWithCurrentItem(it) }
					
					if (mods != null)
						player.getAttributeMap().removeAttributeModifiers(mods)
					
					done = true
				}
				
				if (!done)
					done = player.theItemInWorldManager.onBlockClicked(xl, yl, zl, s)
			} else {
				val entity = if (entities.isEmpty()) null else entities.random()
				
				done = entity != null && player.interactWith(entity)
				if (!done) done = player.theItemInWorldManager.activateBlockOrUseItem(player, world, player.heldItem, xl, yl, zl, s, 0.5f, 0.5f, 0.5f)
				if (!done) run {
					player.heldItem ?: return@run
					val event = ForgeEventFactory.onPlayerInteract(player, PlayerInteractEvent.Action.RIGHT_CLICK_AIR, 0, 0, 0, -1, world)
					if (event.isCanceled || event.useItem == Event.Result.DENY) return@run
					done = player.theItemInWorldManager.tryUseItem(player, world, player.heldItem)
				}
			}
			
			if (done) tile.recieveMana(-COST_AVATAR)
		}
		
		unequipPlayer(player, inv)
	}
	
	fun equipPlayer(player: EntityPlayer, inv: IInventory?, ticksSkipped: Int) {
		if (inv == null) return
		
		val accessibleSlots = (inv as? ISidedInventory)?.getAccessibleSlotsFromSide(1)
		for (i in 0 until player.inventory.sizeInventory) {
			if (i >= inv.sizeInventory) break
			
			if (inv is ISidedInventory && i !in accessibleSlots!!) continue
			
			val stack = inv[i]?.copy() ?: continue
			if (inv is ISidedInventory && !inv.canExtractItem(i, stack, 1)) continue
			inv[i] = null
			
			if (stack.stackSize <= 0) continue
			
			player.inventory[i] = stack
		}
		
		for (i in 0 until player.inventory.sizeInventory) {
			val stack = player.inventory[i] ?: continue
			
			repeat(ticksSkipped) {
				stack.item.onUpdate(stack, player.worldObj, player, i, i == 0)
			}
		}
		
		return
	}
	
	fun unequipPlayer(player: EntityPlayer, inv: IInventory?) {
		val accessibleSlots = (inv as? ISidedInventory)?.getAccessibleSlotsFromSide(1)
		
		for (i in 0 until player.inventory.sizeInventory) {
			var stack = player.inventory[i]?.copy()
			player.inventory[i] = null
			
			if (stack != null && stack.stackSize <= 0) stack = null
			if (stack == null) continue
			
			if (inv == null || i >= inv.sizeInventory || (inv is ISidedInventory && (i !in accessibleSlots!! || !inv.canInsertItem(i, stack, 1)))) {
				player.dropPlayerItemWithRandomChoice(stack, true)
				continue
			}
			
			inv[i] = stack
		}
	}
	
	override fun getOverlayResource(tile: IAvatarTile?, stack: ItemStack?) = LibResourceLocations.avatarClicker
	
	private var ItemStack.isLeftClick
		get() = getBoolean(this, TAG_MODE, false)
		set(left) = setBoolean(this, TAG_MODE, left)
	
	private var ItemStack.prevTick
		get() = getLong(this, TAG_TICK, -1L)
		set(left) = setLong(this, TAG_TICK, left)
	
	companion object {
		
		const val TAG_MODE = "mode"
		const val TAG_TICK = "tick"
		const val COST_AVATAR = 10
		
		lateinit var iconLeft: IIcon
		
		val profileMap = HashMap<Int, GameProfile>()
		
		init {
			eventForge()
		}
		
		fun getFake(dim: Int) = NoNetFakePlayer[MinecraftServer.getServer().worldServerForDimension(dim), profileMap.computeIfAbsent(dim) { GameProfile(UUID(dim.toLong(), dim.toLong()), "[Avatar-Clicker_$dim]") }]
		
		fun isTruePlayerOrAvatar(player: EntityPlayer) = player.commandSenderName.startsWith("[Avatar-Clicker_") || EntityFlugel.isTruePlayer(player)
		
		@SubscribeEvent
		fun unagre(e: LivingSetAttackTargetEvent) {
			val target = e.target
			if (target !is EntityPlayer || EntityFlugel.isTruePlayer(target)) return
			
			val living = e.entityLiving
			living.entityLivingToAttack = null
			
			if (living is EntityLiving)
				living.attackTarget = null
		}
	}
}

// No network - no network problems
class NoNetFakePlayer(world: World, profile: GameProfile): EntityPlayer(world, profile) {
	
	var theItemInWorldManager = NoNetItemInWorldManager(world, this)
	
	init {
		eyeHeight = 12/16f
	}
	
	override fun openGui(mod: Any?, modGuiId: Int, world: World?, x: Int, y: Int, z: Int) = Unit
	override fun addChatMessage(message: IChatComponent?) = Unit
	override fun canCommandSenderUseCommand(level: Int, name: String?) = false
	
	override fun getPlayerCoordinates(): ChunkCoordinates {
		val (x, y, z) = Vector3.fromEntity(this).mf()
		return ChunkCoordinates(x, y, z)
	}
	
	companion object {
		val cache = HashMap<GameProfile, NoNetFakePlayer>()
		operator fun get(world: World, profile: GameProfile) = cache.computeIfAbsent(profile) { NoNetFakePlayer(world, profile) }
	}
}

class NoNetItemInWorldManager(var theWorld: World, var thisPlayerMP: NoNetFakePlayer) {
	
	/** Forge reach distance  */
	var blockReachDistance: Double = 5.0
	
	/** The EntityPlayerMP object that this object is connected to.  */
	
	private var gameType: GameType = GameType.NOT_SET
		set(value) {
			field = value
			value.configurePlayerCapabilities(thisPlayerMP.capabilities)
		}
	
	fun onBlockClicked(x: Int, y: Int, z: Int, side: Int): Boolean {
		if (gameType.isAdventure && !thisPlayerMP.isCurrentToolAdventureModeExempt(x, y, z)) return false
		
		var event: PlayerInteractEvent? = null
		
		try_ {  // fukkit x3
			event = ForgeEventFactory.onPlayerInteract(thisPlayerMP, PlayerInteractEvent.Action.LEFT_CLICK_BLOCK, x, y, z, side, theWorld)
		}
		
		if (event?.isCanceled == true)
			return false
		
		var hardness = 1f
		val block = theWorld.getBlock(x, y, z)
		
		var clicked = false
		
		if (!block.isAir(theWorld, x, y, z)) {
			if (event?.useBlock != Event.Result.DENY) {
				block.onBlockClicked(theWorld, x, y, z, thisPlayerMP)
				theWorld.extinguishFire(thisPlayerMP, x, y, z, side)
				clicked = true
			}
			
			hardness = block.getPlayerRelativeBlockHardness(thisPlayerMP, thisPlayerMP.worldObj, x, y, z)
		}
		
		if (event?.useItem == Event.Result.DENY) {
			return clicked
		}
		
		if (!block.isAir(theWorld, x, y, z) && hardness >= 1f)
			return tryHarvestBlock(x, y, z) || clicked
		
		return clicked
	}
	
	private fun removeBlock(x: Int, y: Int, z: Int, canHarvest: Boolean = false): Boolean {
		val block = theWorld.getBlock(x, y, z)
		val l = theWorld.getBlockMetadata(x, y, z)
		block.onBlockHarvested(theWorld, x, y, z, l, thisPlayerMP)
		val flag = block.removedByPlayer(theWorld, thisPlayerMP, x, y, z, canHarvest)
		
		if (flag) {
			block.onBlockDestroyedByPlayer(theWorld, x, y, z, l)
		}
		
		return flag
	}
	
	/**
	 * Attempts to harvest a block at the given coordinate
	 */
	fun tryHarvestBlock(x: Int, y: Int, z: Int): Boolean {
		val event = onBlockBreakEvent(theWorld, gameType, thisPlayerMP, x, y, z)
		if (event.isCanceled) {
			return false
		} else {
			val stack = thisPlayerMP.currentEquippedItem
			if (stack != null && stack.item.onBlockStartBreak(stack, x, y, z, thisPlayerMP)) {
				return false
			}
			val block = theWorld.getBlock(x, y, z)
			val l = theWorld.getBlockMetadata(x, y, z)
			theWorld.playAuxSFXAtEntity(thisPlayerMP, 2001, x, y, z, Block.getIdFromBlock(block) + (theWorld.getBlockMetadata(x, y, z) shl 12))
			val flag: Boolean
			val itemstack = thisPlayerMP.currentEquippedItem
			val flag1 = block.canHarvestBlock(thisPlayerMP, l)
			
			if (itemstack != null) {
				itemstack.func_150999_a(theWorld, block, x, y, z, thisPlayerMP)
				
				if (itemstack.stackSize == 0) {
					thisPlayerMP.destroyCurrentEquippedItem()
				}
			}
			
			flag = removeBlock(x, y, z, flag1)
			if (flag && flag1) {
				block.harvestBlock(theWorld, thisPlayerMP, x, y, z, l)
			}
			// Drop experience
			if (flag) {
				block.dropXpOnBlockBreak(theWorld, x, y, z, event.expToDrop)
			}
			return flag
		}
	}
	
	fun onBlockBreakEvent(world: World, gameType: GameType, entityPlayer: NoNetFakePlayer, x: Int, y: Int, z: Int): BreakEvent {
		// Logic from tryHarvestBlock for pre-canceling the event
		var preCancelEvent = false
		if (gameType.isAdventure && !entityPlayer.isCurrentToolAdventureModeExempt(x, y, z)) {
			preCancelEvent = true
		} else if (gameType.isCreative && entityPlayer.heldItem != null && entityPlayer.heldItem.item is ItemSword) {
			preCancelEvent = true
		}
		
		// Post the block break event
		val block = world.getBlock(x, y, z)
		val blockMetadata = world.getBlockMetadata(x, y, z)
		val event = BreakEvent(x, y, z, world, block, blockMetadata, entityPlayer)
		event.isCanceled = preCancelEvent
		MinecraftForge.EVENT_BUS.post(event)
		
		return event
	}
	
	/**
	 * Attempts to right-click use an item by the given EntityPlayer in the given World
	 */
	fun tryUseItem(player: EntityPlayer, world: World, stack: ItemStack): Boolean {
		val size = stack.stackSize
		val damage = stack.getItemDamage()
		val result = stack.useItemRightClick(world, player)
		
		if (result == stack && (result.stackSize == size && result.maxItemUseDuration <= 0 && result.getItemDamage() == damage)) {
			return false
		} else {
			player.inventory.mainInventory[player.inventory.currentItem] = result
			
			if (result.stackSize == 0) {
				player.inventory.mainInventory[player.inventory.currentItem] = null
				MinecraftForge.EVENT_BUS.post(PlayerDestroyItemEvent(thisPlayerMP, result))
			}
			
			return true
		}
	}
	
	/**
	 * Activate the clicked on block, otherwise use the held item. Args: player, world, itemStack, x, y, z, side,
	 * xOffset, yOffset, zOffset
	 */
	fun activateBlockOrUseItem(player: EntityPlayer, world: World, stack: ItemStack?, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		val event = try {
			ForgeEventFactory.onPlayerInteract(player, PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, x, y, z, side, world)
		} catch (e: Throwable) {
			PlayerInteractEvent(player, PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, x, y, z, side, world)
		}
		
		if (event.isCanceled)
			return false
		
		if (stack != null && stack.item.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ)) {
			if (stack.stackSize <= 0) ForgeEventFactory.onPlayerDestroyItem(thisPlayerMP, stack)
			return true
		}
		
		val block = world.getBlock(x, y, z)
		var useBlock = !player.isSneaking || player.heldItem == null
		if (!useBlock) useBlock = player.heldItem.item.doesSneakBypassUse(world, x, y, z, player)
		var result = false
		
		if (useBlock) {
			result = if (event.useBlock != Event.Result.DENY) {
				block.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ)
			} else {
				event.useItem != Event.Result.ALLOW
			}
		}
		
		if (stack != null && !result && event.useItem != Event.Result.DENY) {
			result = stack.tryPlaceItemIntoWorld(player, world, x, y, z, side, hitX, hitY, hitZ)
			if (stack.stackSize <= 0) ForgeEventFactory.onPlayerDestroyItem(thisPlayerMP, stack)
		}
		
		return result
	}
}