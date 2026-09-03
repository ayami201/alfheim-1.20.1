package alfheim.common.item.relic

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.ModInfo
import alfheim.client.gui.ItemsRemainingRenderHandler
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.tile.*
import alfheim.common.core.asm.hook.AlfheimHookHandler
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.item.AlfheimItems
import alfheim.common.network.*
import alfheim.common.network.packet.Message0dS
import alfheim.common.world.data.CustomWorldData.Companion.customData
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.PlayerEvent
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.*
import net.minecraft.server.MinecraftServer
import net.minecraft.util.EnumChatFormatting
import net.minecraft.world.World
import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.util.Constants
import net.minecraftforge.event.entity.item.ItemTossEvent
import org.lwjgl.input.Mouse
import vazkii.botania.api.item.IRelic
import vazkii.botania.common.core.helper.ItemNBTHelper.*
import vazkii.botania.common.item.relic.ItemRelic
import kotlin.math.max

class ItemAkashicRecords: ItemRelic("AkashicRecords") {
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		if (player.isSneaking) {
			setBoolean(stack, TAG_SWITCH, true)
			return stack
		}
		
		if (!isOpen(stack) || world.isRemote) return stack
		if (player.dimension == AlfheimConfigHandler.dimensionIDDomains) return stack
		
		val cd = MinecraftServer.getServer().worldServerForDimension(0).customData
		val akashas = cd.nbtData.tagMap.computeIfAbsent(TAG_AKASHAS) { NBTTagCompound() } as NBTTagCompound
		
		val nbt = akashas.tagMap.computeIfAbsent(player.commandSenderName) {
			val nbt = NBTTagCompound()
			val x = AlfheimConfigHandler.domainStartX - AlfheimConfigHandler.domainDistance
			var z = AlfheimConfigHandler.domainStartZ
			val dimWorld = MinecraftServer.getServer().worldServerForDimension(AlfheimConfigHandler.dimensionIDDomains)
			while (!dimWorld.isAirBlock(x, 0, z)) z += AlfheimConfigHandler.domainDistance
			
			nbt.setInteger("x", x)
			nbt.setInteger("z", z)
			
			dimWorld.setBlock(x, 0, z, AlfheimBlocks.barrier)
			
			for (i in 0.bidiRange(16))
				for (j in 16..48)
					for (k in 0.bidiRange(16))
						if (i == -16 || i == 16 || j == 16 || j == 48 || k == -16 || k == 16)
							dimWorld.setBlock(x + i, j, z + k, AlfheimBlocks.altPlanks, 6, 0)
			
			dimWorld.setBlock(x, 16, z, AlfheimBlocks.domainDoor, 1, 0)
			
			cd.markDirty()
			
			nbt
		} as NBTTagCompound
		
		val (i, j, k) = Vector3.fromEntity(player).mf()
		player.persistentData.setIntArray(TileDomainLobby.TAG_DOMAIN_ENTRANCE, intArrayOf(i, j, k, player.dimension))
		val x = nbt.getInteger("x") + 0.5
		val y = 17.0
		val z = nbt.getInteger("z") + 0.5
		
		AlfheimHookHandler.allowtp = true
		ASJUtilities.sendToDimensionWithoutPortal(player, AlfheimConfigHandler.dimensionIDDomains, x, y, z)
		
		return stack
	}
	
	override fun onUpdate(stack: ItemStack, world: World, entity: Entity, slot: Int, inHand: Boolean) {
		if (world.provider.dimensionId == AlfheimConfigHandler.dimensionIDDomains && !world.isRemote) {
			val list = NBTTagList()
			
			TileItemDisplay.displaysInDomainsList.forEach { te ->
				val relic = te[0] ?: return@forEach
				if (relic.item !is IRelic) return@forEach
				if (getSoulbindUsernameS(relic) != entity.commandSenderName) return@forEach
				if (relic.tagCompound?.hasKey(TAG_AKASHIC_STACK) == true) return@forEach
				
				val data = NBTTagCompound()
				data.setIntArray(TAG_RELIC_COORDS, intArrayOf(te.xCoord, te.yCoord, te.zCoord))
				data.setTag(TAG_RELIC_STACK, NBTTagCompound().also { relic.writeToNBT(it) })
				
				list.appendTag(data)
			}
			
			ItemNBTHelper.setList(stack, TAG_PEDESTALS, list)
		}
		
		super.onUpdate(stack, world, entity, slot, inHand)
		
		if (!inHand) {
			setInt(stack, TAG_MULT, -1)
			setInt(stack, TAG_FRAME, 0)
			return
		}
		
		var frame = (getInt(stack, TAG_FRAME, 0) + getInt(stack, TAG_MULT, -1))
		
		if (frame == 160) frame = 60
		
		if ((frame % 100 == 60 || frame == -1) && getBoolean(stack, TAG_SWITCH, false)) {
			setInt(stack, TAG_MULT, getInt(stack, TAG_MULT, -1) * -1)
			setBoolean(stack, TAG_SWITCH, false)
			setInt(stack, TAG_FRAME, if (frame != -1) 60 else 1)
		} else {
			setInt(stack, TAG_FRAME, max(0, frame))
		}
	}
	
	override fun onEntitySwing(entity: EntityLivingBase, stack: ItemStack): Boolean {
		if (entity.worldObj.isRemote) return false
		
		val list = ItemNBTHelper.getList(stack, TAG_PEDESTALS, Constants.NBT.TAG_COMPOUND, true) ?: return false
		if (list.tagCount() == 0) return false
		
		if (!ItemNBTHelper.getNBT(stack).hasKey(TAG_SCROLL)) return false
		val scroll = ItemNBTHelper.getInt(stack, TAG_SCROLL, 0)
		
		var index = scroll % list.tagCount()
		if (index < 0) index += list.tagCount()
		val data = list.getCompoundTagAt(index)
		
		if (data.getBoolean(TAG_TAKEN)) return false
		
		val dimWorld = MinecraftServer.getServer().worldServerForDimension(AlfheimConfigHandler.dimensionIDDomains) ?: return false
		val (x, y, z) = data.getIntArray(TAG_RELIC_COORDS)
		
		val tile = dimWorld.getTileEntity(x, y, z) as? TileItemDisplay ?: return false
		val relic = tile[0]?.copy() ?: return false
		tile[0] = null
		
		data.setBoolean(TAG_TAKEN, true)
		
		ItemNBTHelper.initNBT(relic)
		relic.tagCompound.setTag(TAG_AKASHIC_STACK, stack.writeToNBT(NBTTagCompound()))
		
		entity.setCurrentItemOrArmor(0, relic)
		
		MinecraftServer.getServer().worldServerForDimension(0).customData.markDirty()
		
		return true
	}
	
	override fun addInformation(stack: ItemStack?, player: EntityPlayer?, list: MutableList<Any?>, adv: Boolean) {
		super.addInformation(stack, player, list, adv)
		
		if (GuiScreen.isShiftKeyDown()) run {
			val relics = ItemNBTHelper.getList(stack, TAG_PEDESTALS, Constants.NBT.TAG_COMPOUND, true) ?: return@run
			if (relics.tagCount() == 0) return@run
			
			var index = ItemNBTHelper.getInt(stack, TAG_SCROLL, 0) % relics.tagCount()
			if (index < 0) index += relics.tagCount()
			
			list.add("")
			for (i in 0 until relics.tagCount()) {
				val data = relics.getCompoundTagAt(i)
				val relic = ItemStack.loadItemStackFromNBT(data.getCompoundTag(TAG_RELIC_STACK))
				
				list.add("${if (index == i) EnumChatFormatting.GREEN else ""}${if (data.getBoolean("taken")) EnumChatFormatting.STRIKETHROUGH else ""}${i + 1}. ${relic.displayName}")
			}
			list.add("")
		}
	}
	
	companion object {
		
		const val TAG_SCROLL = "scroll"
		const val TAG_PEDESTALS = "pedestals"
		const val TAG_RELIC_COORDS = "relic_coords"
		const val TAG_RELIC_STACK = "relic_stack"
		const val TAG_AKASHIC_STACK = "${ModInfo.MODID}_akashic_stack"
		const val TAG_TAKEN = "taken"
		
		const val TAG_FRAME = "frame"
		const val TAG_MULT = "mult"
		const val TAG_SWITCH = "switch"
		
		const val TAG_AKASHAS = "player_akashas"
		
		init {
			MinecraftForge.EVENT_BUS.register(this)
		}
		
		fun isOpen(stack: ItemStack) = getInt(stack, TAG_FRAME, 0) > 60 && getInt(stack, TAG_MULT, -1) == 1 && !getBoolean(stack, TAG_SWITCH, false)
		
		fun unpack(player: EntityPlayer) {
			val stack = player.heldItem ?: return
			if (stack.tagCompound?.hasKey(TAG_AKASHIC_STACK) != true) return
			
			val akashic = ItemStack.loadItemStackFromNBT(stack.tagCompound.getCompoundTag(TAG_AKASHIC_STACK)) ?: return
			
			fun giveBack() {
				stack.tagCompound.removeTag(TAG_AKASHIC_STACK)
				if (!player.inventory.addItemStackToInventory(akashic))
					player.entityDropItem(akashic, 0f)
			}
			
			val list = ItemNBTHelper.getList(akashic, TAG_PEDESTALS, Constants.NBT.TAG_COMPOUND, true)
			if (list == null || list.tagCount() == 0) return giveBack()
			
			val index = (0 until list.tagCount()).firstOrNull {
				val data = list.getCompoundTagAt(it)
				val copy = ItemStack.loadItemStackFromNBT(data.getCompoundTag(TAG_RELIC_STACK)) ?: return@firstOrNull false
				return@firstOrNull stack.isItemEqual(copy)
			} ?: return giveBack()
			
			val data = list.getCompoundTagAt(index)
			
			val dimWorld = MinecraftServer.getServer().worldServerForDimension(AlfheimConfigHandler.dimensionIDDomains)
			val (x, y, z) = data.getIntArray(TAG_RELIC_COORDS)
			val tile = dimWorld.getTileEntity(x, y, z) as? TileItemDisplay ?: return giveBack()
			
			data.removeTag(TAG_TAKEN)
			stack.tagCompound.removeTag(TAG_AKASHIC_STACK)
			tile[0] = stack
			
			player.setCurrentItemOrArmor(0, akashic)
			
			MinecraftServer.getServer().worldServerForDimension(0).customData.markDirty()
		}
		
		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		fun onWheel(e: MouseEvent) {
			val i = Mouse.getEventDWheel()
			if (i == 0) return
			
			val player = mc.thePlayer ?: return
			if (!player.isSneaking) return
			
			val stack = player.heldItem ?: return
			if (stack.item !== AlfheimItems.akashicRecords) return
			
			NetworkService.sendToServer(Message0dS(if (i > 0) M0ds.AKASHIK_SCROLL_UP else M0ds.AKASHIK_SCROLL_DOWN))
			
			run {
				val relics = ItemNBTHelper.getList(stack, TAG_PEDESTALS, Constants.NBT.TAG_COMPOUND, true) ?: return@run
				if (relics.tagCount() == 0) return@run
				
				var index = (ItemNBTHelper.getInt(stack, TAG_SCROLL, 0) + if (i > 0) -1 else 1) % relics.tagCount()
				if (index < 0) index += relics.tagCount()
				
				val data = relics.getCompoundTagAt(index)
				val relic = ItemStack.loadItemStackFromNBT(data.getCompoundTag(TAG_RELIC_STACK))
				
				ItemsRemainingRenderHandler.set(relic, "${if (data.getBoolean("taken")) EnumChatFormatting.STRIKETHROUGH else ""}${relic.displayName}")
			}
			
			e.isCanceled = true
		}
		
		@SubscribeEvent
		fun onDropped(e: ItemTossEvent) {
			val stack = e.entityItem?.entityItem ?: return
			
			if (stack.item === AlfheimItems.akashicRecords) {
				setInt(stack, TAG_MULT, -1)
				setInt(stack, TAG_FRAME, 0)
			}
		}
		
		@SubscribeEvent
		fun onPickup(e: PlayerEvent.ItemPickupEvent) {
			val stack = e.pickedUp?.entityItem ?: return
			
			if (stack.item === AlfheimItems.akashicRecords) {
				setInt(stack, TAG_MULT, -1)
				setInt(stack, TAG_FRAME, 0)
			}
		}
	}
}