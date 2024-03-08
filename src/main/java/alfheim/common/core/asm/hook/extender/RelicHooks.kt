package alfheim.common.core.asm.hook.extender

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.entity.item.EntityItemImmortalRelic
import alfheim.common.item.AlfheimItems.akashicRecords
import alfheim.common.item.AlfheimItems.daolos
import alfheim.common.item.AlfheimItems.excaliber
import alfheim.common.item.AlfheimItems.flugelSoul
import alfheim.common.item.AlfheimItems.gjallarhorn
import alfheim.common.item.AlfheimItems.gleipnir
import alfheim.common.item.AlfheimItems.gungnir
import alfheim.common.item.AlfheimItems.mask
import alfheim.common.item.AlfheimItems.mjolnir
import alfheim.common.item.AlfheimItems.moonlightBow
import alfheim.common.item.AlfheimItems.priestRingHeimdall
import alfheim.common.item.AlfheimItems.priestRingNjord
import alfheim.common.item.AlfheimItems.priestRingSif
import alfheim.common.item.AlfheimItems.subspaceSpear
import alfheim.common.network.NetworkService
import alfheim.common.network.packet.MessageRelicNBTSync
import alfheim.common.world.data.CustomWorldData.Companion.customData
import baubles.api.*
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.network.NetworkRegistry
import cpw.mods.fml.common.registry.GameRegistry
import gloomyfolken.hooklib.asm.*
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.*
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.util.*
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent
import vazkii.botania.api.item.IRelic
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.item.ModItems.*
import vazkii.botania.common.item.relic.*
import alexsocol.asjlib.ItemNBTHelper as AItemNBTHelper

@Suppress("UNUSED_PARAMETER", "unused")
object RelicHooks {
	
	// things so relics won't hurt you or do other unwanted stuff
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun getSoulbindUsernameS(static: ItemRelic?, stack: ItemStack) = stack.tagCompound?.getString("soulbind") ?: ""
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun bindToUsernameS(static: ItemRelic?, username: String?, stack: ItemStack) {
		AItemNBTHelper.initNBT(stack)
		stack.tagCompound?.setString("soulbind", username)
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_TRUE)
	fun isRightPlayer(static: ItemRelic?, player: String, stack: ItemStack?) = stack?.item !is ItemDice
	
	private val underControl by lazy { arrayOf(infiniteFruit, kingKey, flugelEye, thorRing, odinRing, lokiRing, flugelSoul, mask, excaliber, subspaceSpear, moonlightBow, gleipnir, mjolnir, daolos, gungnir, gjallarhorn, priestRingHeimdall, priestRingNjord, priestRingSif, akashicRecords) }
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun updateRelic(static: ItemRelic?, stack: ItemStack?, player: EntityPlayer) {
		val item = stack?.item ?: return
		if (item !is IRelic) return
		
		AItemNBTHelper.initNBT(stack)
		
		if (ItemRelic.getSoulbindUsernameS(stack).isNotEmpty()) return
		
		// can't be sure that relics from other mods are correct + no tuner recipe, so should be OK
		if (item inl underControl) {
			if (item.bindAchievement != null && !player.hasAchievement(item.bindAchievement)) return
		} else {
			player.triggerAchievement(item.bindAchievement)
		}
		
		ItemRelic.bindToPlayer(player, stack)
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ALWAYS)
	fun addBindInfo(static: ItemRelic?, list: MutableList<Any?>, stack: ItemStack, player: EntityPlayer?) {
		if (GuiScreen.isShiftKeyDown()) {
			addStringToTooltip(list, "alfheimmisc.relic")
			
			if (stack.item === aesirRing)
				addStringToTooltip(list, "botaniamisc.dropIkea")
			
			val name = stack.unlocalizedName + ".poem"
			if (StatCollector.canTranslate("${name}0")) {
				addStringToTooltip(list, "")
				
				for (i in 0..3)
					list.add("${EnumChatFormatting.ITALIC}${StatCollector.translateToLocal(name + i)}")
			}
		} else addStringToTooltip(list, "botaniamisc.shiftinfo")
	}
	
	// thing to prevent cheats and dupes
	
	val aesirRingComponents by lazy { arrayOf(thorRing, lokiRing, odinRing, priestRingSif, priestRingNjord, priestRingHeimdall) }
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_TRUE, booleanReturnConstant = false)
	fun canEquip(relic: ItemRelicBauble, stack: ItemStack?, player: EntityLivingBase?): Boolean {
		if (player !is EntityPlayer) return false
		val item = stack?.item ?: return false
		
		// only rings have two slots, so...
		if (item !is IBauble || item.getBaubleType(stack) != BaubleType.RING) return false
		
		val baubs = PlayerHandler.getPlayerBaubles(player)
		if (baubs[1]?.item === item || baubs[2]?.item === item) return true // no copies
		if (item is ItemAesirRing && (baubs[1]?.item inl aesirRingComponents || baubs[2]?.item inl aesirRingComponents)) return true // no components 1
		if (item inl aesirRingComponents && (baubs[1]?.item is ItemAesirRing || baubs[2]?.item is ItemAesirRing)) return true // no components 2
		
		return false
	}
	
	// things to preserve NBT
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_NOT_NULL)
	fun getNBT(static: ItemNBTHelper?, stack: ItemStack?) = getNBT(AItemNBTHelper, stack)
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_NOT_NULL)
	fun getNBT(static: AItemNBTHelper?, stack: ItemStack?): NBTTagCompound? {
		val item = stack?.item as? IRelic ?: return null
		val hostName = stack.tagCompound?.getString("soulbind")
		val statId = item.bindAchievement?.statId ?: GameRegistry.findUniqueIdentifierFor(stack.item).toString()
		
		if (hostName.isNullOrEmpty()) {
			if (stack.tagCompound == null) stack.tagCompound = NBTTagCompound()
			return stack.tagCompound
		}
		
		if (ASJUtilities.isClient)
			return relicNBTClient.computeIfAbsent("$hostName>>>$statId") { NBTTagCompound() }

		val nbtData = MinecraftServer.getServer().worldServerForDimension(0).customData.nbtData
		val relicNBT = nbtData.tagMap.computeIfAbsent(TAG_RELIC_NBT) { NBTTagCompound() } as NBTTagCompound
		val userData = relicNBT.tagMap.computeIfAbsent(hostName) { NBTTagCompound() } as NBTTagCompound
		return userData.tagMap.computeIfAbsent(statId) { NBTTagCompound() } as NBTTagCompound
	}
	
	@JvmStatic
	@Hook(returnCondition = ReturnCondition.ON_TRUE)
	fun verifyExistance(static: AItemNBTHelper?, stack: ItemStack?, tag: String) = getNBT(AItemNBTHelper, stack)?.hasKey(tag) == true
	
	val relicNBTClient = HashMap<String, NBTTagCompound>()
	const val TAG_RELIC_NBT = "relicNBT"
}

object RelicNBTSyncHandler {
	
	@SubscribeEvent
	fun onPlayerUpdate(e: LivingUpdateEvent) {
		if (ASJUtilities.isClient) return
		val player = e.entityLiving as? EntityPlayerMP ?: return
		
		run sync@ {
			val nbtData = MinecraftServer.getServer().worldServerForDimension(0).customData.nbtData
			val relicNBT = nbtData.tagMap.computeIfAbsent(RelicHooks.TAG_RELIC_NBT) { NBTTagCompound() } as NBTTagCompound
			val userData = relicNBT.tagMap.computeIfAbsent(player.commandSenderName) { NBTTagCompound() } as NBTTagCompound
			val syncMap = HashMap<String, NBTTagCompound>()
			userData.tagMap.forEach { (statId, nbt) -> syncMap["${player.commandSenderName}>>>$statId"] = nbt as NBTTagCompound }
			val (x, y, z) = Vector3.fromEntity(player)
			NetworkService.sendToAllAround(MessageRelicNBTSync(syncMap), NetworkRegistry.TargetPoint(player.dimension, x, y, z, 64.0))
		}
		
		if (player.capabilities.isCreativeMode) return
		
		run removeNotGot@ {
			fun iterateInventory(inv: IInventory) {
				for (i in 0 until inv.sizeInventory) {
					val stack = inv[i] ?: continue
					val ach = (stack.item as? IRelic)?.bindAchievement
					if (ach == null || player.hasAchievement(ach)) continue
					
					inv[i] = null
					if (stack.tagCompound?.getString("soulbind") == player.commandSenderName)
						stack.tagCompound?.removeTag("soulbind")
					
					EntityItemImmortalRelic(EntityItem(player.worldObj, player.posX, player.posY, player.posZ, stack)).spawn()
					
					ASJUtilities.say(player, "alfheimmisc.relic.fail", stack.displayName)
				}
			}
			
			iterateInventory(player.inventory)
			iterateInventory(PlayerHandler.getPlayerBaubles(player))
		}
	}
}

