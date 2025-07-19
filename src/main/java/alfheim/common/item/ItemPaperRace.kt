package alfheim.common.item

import alexsocol.asjlib.*
import alfheim.api.entity.*
import alfheim.client.core.helper.InterpolatedIconHelper
import alfheim.common.achievement.AlfheimAchievements
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.handler.CardinalSystem.ElvenStoryModeSystem
import alfheim.common.network.NetworkService
import alfheim.common.network.packet.*
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.Entity
import net.minecraft.entity.player.*
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.server.MinecraftServer
import net.minecraft.world.World
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge

class ItemPaperRace: ItemMod("PaperRace") {
	
	init {
		maxDamage = 1200
		setMaxStackSize(1)
		setNoRepair()
		
		if (ASJUtilities.isClient)
			MinecraftForge.EVENT_BUS.register(this)
	}
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		if (world.isRemote) return stack
		
		val result = ItemStack(Items.paper, 0)
		
		if (!AlfheimConfigHandler.enableElvenStory) {
			ASJUtilities.say(player, "alfheimmisc.panel.esmDisabled")
			return result
		}
		
		if (check(stack, player, world)) {
			ASJUtilities.say(player, "item.alfheim:PaperRace.fail")
			return result
		}
		
		player.triggerAchievement(AlfheimAchievements.fuckup)
		
		player.race = EnumRace.HUMAN
		ElvenStoryModeSystem.setGender(player, false)
		ElvenStoryModeSystem.setCustomSkin(player, false)
		
		NetworkService.sendToAll(MessageRaceInfo(player.commandSenderName, 0))
		NetworkService.sendToAll(MessageSkinInfo(player.commandSenderName, false, false))
		
		val (x, y, z) = MinecraftServer.getServer().worldServerForDimension(AlfheimConfigHandler.dimensionIDAlfheim).provider.spawnPoint
		ASJUtilities.sendToDimensionWithoutPortal(player, AlfheimConfigHandler.dimensionIDAlfheim, x + 0.5, y + 0.5, z + 0.5)
		
		return result
	}
	
	fun check(stack: ItemStack, player: EntityPlayer, world: World): Boolean {
		if (ItemNBTHelper.getString(stack, TAG_BOUND, "") != player.commandSenderName)
			return true
		
		val timer = ItemNBTHelper.getLong(stack, TAG_TIMER, 0)
		return world.totalWorldTime !in (timer - MAX_TIME)..timer
	}
	
	override fun onUpdate(stack: ItemStack, world: World, entity: Entity, slot: Int, inHand: Boolean) {
		if (entity is EntityPlayer && !check(stack, entity, world)) return
		
		stack.stackSize = 0
		if (entity is EntityPlayer)
			entity.inventory[slot] = null
		
		if (entity is EntityPlayerMP)
			entity.sendContainerToPlayer(entity.inventoryContainer)
	}
	
	override fun addInformation(stack: ItemStack?, player: EntityPlayer, list: MutableList<Any?>, adv: Boolean) {
		addStringToTooltip(list, if (AlfheimConfigHandler.enableElvenStory) "item.alfheim:PaperRace.desc" else "alfheimmisc.panel.esmDisabled")
	}
	
	override fun registerIcons(reg: IIconRegister) = Unit
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun loadTextures(event: TextureStitchEvent.Pre) {
		if (event.map.textureType == 1)
			itemIcon = InterpolatedIconHelper.forItem(event.map, this)
	}
	
	override fun showDurabilityBar(stack: ItemStack) = stack.tagCompound?.hasKey(TAG_TIMER) == true
	
	override fun getDurabilityForDisplay(stack: ItemStack?): Double {
		val time = (if (ASJUtilities.isClient) mc.theWorld else MinecraftServer.getServer().entityWorld).totalWorldTime
		val timer = ItemNBTHelper.getLong(stack, TAG_TIMER, 0)
		// Forge fucked things up (again) and durability values are swapped, hence the (1.0 -).
		return 1.0 - (timer - time) / MAX_TIME.D
	}
	
	companion object {
		
		const val MAX_TIME = 1200
		const val TAG_BOUND = "bound"
		const val TAG_TIMER = "timer"
		
		fun give(player: EntityPlayer, currentTime: Long) {
			val stack = ItemStack(AlfheimItems.paperRace)
			
			ItemNBTHelper.setString(stack, TAG_BOUND, player.commandSenderName)
			ItemNBTHelper.setLong(stack, TAG_TIMER, currentTime + MAX_TIME)
			
			if (!player.inventory.addItemStackToInventory(stack))
				player.dropPlayerItemWithRandomChoice(stack, false)
			else if (player is EntityPlayerMP)
				player.sendContainerToPlayer(player.inventoryContainer)
		}
	}
}
