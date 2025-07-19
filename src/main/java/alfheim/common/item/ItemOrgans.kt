package alfheim.common.item

import alexsocol.asjlib.*
import alexsocol.asjlib.ItemNBTHelper.getInt
import alexsocol.asjlib.ItemNBTHelper.setInt
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.ModInfo
import alfheim.client.core.helper.IconHelper
import alfheim.client.gui.*
import alfheim.common.achievement.AlfheimAchievements
import alfheim.common.core.helper.*
import alfheim.common.network.NetworkService
import alfheim.common.network.packet.MessageOrgans
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.relauncher.*
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.*
import net.minecraft.item.*
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.client.event.*
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent
import org.lwjgl.opengl.GL11.*
import kotlin.math.max

class ItemOrgans: ItemMod("Organs") {
	
	lateinit var icons: Array<IIcon>
	
	init {
		setHasSubtypes(true)
	}
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		if (stack.item !== this) return stack // WTF ???
		
		if (player.itemInUse != null)
			return player.itemInUse
		
		if (!world.isRemote) {
			val start = player.rng.nextInt(MAX_PROGRESS - PROGRESS_RANGE + 1)
			setInt(stack, TAG_MINIGAME_START, start)
		}

		player.setItemInUse(stack, getMaxItemUseDuration(stack))
		
		return stack
	}
	
	override fun getMaxItemUseDuration(stack: ItemStack?) = 72000
	
	override fun getItemUseAction(stack: ItemStack?) = EnumAction.bow
	
	override fun onPlayerStoppedUsing(stack: ItemStack, world: World, player: EntityPlayer, timeLeft: Int) {
		if (stack.item !== this) return // WTF ???
		
		val start = getInt(stack, TAG_MINIGAME_START, -1)
		val end = start + PROGRESS_RANGE

		stack.tagCompound?.removeTag(TAG_MINIGAME_START)

		val left = if (world.isRemote) timeLeft else timeLeft + 4
		
		var progress = (getMaxItemUseDuration(stack) - left) % (MAX_PROGRESS * 2)
		if (progress > MAX_PROGRESS)
			progress = MAX_PROGRESS * 2 - progress
		
		if (progress !in start..end) {
			val old = when (stack.meta) {
				0 -> player.heart
				1 -> player.lungs
				2 -> player.liver
				3 -> player.nerves
				else -> throw IllegalArgumentException("No, u")
			}
			
			if (old > 0) {
				when (stack.meta) {
					0 -> player.heart = max(0, old - 10)
					1 -> player.lungs = max(0, old - 10)
					2 -> player.liver = max(0, old - 10)
					3 -> player.nerves = max(0, old - 10)
				}
				
				return
			}
			
			if (stack.meta == 3) run {
				if (ASJUtilities.chance(10))
					if (!reEjectOrgan(player, 0, { player.heart }, { player.heart = MAX_DECAY_TIME }))
						if (!reEjectOrgan(player, 2, { player.liver }, { player.liver = MAX_DECAY_TIME }))
							if (!reEjectOrgan(player, 1, { player.lungs }, { player.lungs = MAX_DECAY_TIME }))
								return@run 
						
				return
			}
			
			val prev = player.hurtResistantTime
			player.hurtResistantTime = 0
			player.attackEntityFrom(when (stack.meta) {
				0 -> heartDamage
				1 -> lungsDamage
				2 -> liverDamage
				3 -> nervesDamage
				else -> throw IllegalArgumentException("No, u")
			}, 4f)
			player.hurtResistantTime = prev
			
			return
		}
		
		when (stack.meta) {
			0 -> player.heart = -1
			1 -> player.lungs = -1
			2 -> player.liver = -1
			3 -> player.nerves = -1
		}
		
		player.playSoundAtEntity("${ModInfo.MODID}:organ.${
			when (stack.meta) {
				0 -> "heart"
				1 -> "lungs"
				2 -> "liver"
				3 -> "nerves"
				else -> throw IllegalArgumentException("No, u")
			}
		}", 1f, 1f)
		
		player.inventory[player.inventory.currentItem] = null
		
		if (player is EntityPlayerMP) {
			NetworkService.sendTo(MessageOrgans(player.heart, player.lungs, player.liver, player.nerves), player)
			player.sendContainerToPlayer(player.inventoryContainer)
		}
	}
	
	override fun onEntityItemUpdate(item: EntityItem): Boolean {
		item.setDead()
		return true
	}
	
	@SideOnly(Side.CLIENT)
	override fun getColorFromItemStack(stack: ItemStack, pass: Int): Int {
		if (mc.thePlayer.nerves == 0) return 0
		
		when (stack.meta) {
			0 -> if (mc.thePlayer.heart == 0) return 0x999999
			1 -> if (mc.thePlayer.lungs == 0) return 0x999999
			2 -> if (mc.thePlayer.liver == 0) return 0x999999
		}
		
		return 0xFFFFFF
	}
	
	override fun registerIcons(reg: IIconRegister) {
		icons = arrayOf(
			IconHelper.forName(reg, "misc/heart"),
			IconHelper.forName(reg, "misc/lungs"),
			IconHelper.forName(reg, "misc/liver"),
			IconHelper.forName(reg, "misc/nerves")
		)
	}
	
	override fun getIconFromDamage(meta: Int) = icons.safeGet(meta)
	
	companion object {
		
		const val EJECT_FREQUENCY = 300
		const val MAX_DECAY_TIME = 100
		const val MAX_PROGRESS = 8
		const val PROGRESS_RANGE = 4
		const val TAG_MINIGAME_START = "minigameStart"
		
		val heartDamage = DamageSource("organs.heart").setDamageBypassesArmor().setDamageIsAbsolute().setTo(ElementalDamage.PSYCHIC)
		val liverDamage = DamageSource("organs.liver").setDamageBypassesArmor().setTo(ElementalDamage.NATURE)
		val lungsDamage = DamageSource("organs.lungs").setDamageBypassesArmor().setTo(ElementalDamage.AIR)
		val nervesDamage = DamageSource("organs.nerves").setDamageBypassesArmor().setDamageIsAbsolute().setTo(ElementalDamage.PSYCHIC)
		
		var EntityPlayer.heart: Int
			get() = entityData.getIntegerDef("${ModInfo.MODID}:organs.heart")
			set(value) = entityData.setInteger("${ModInfo.MODID}:organs.heart", value)
		
		var EntityPlayer.lungs: Int
			get() = entityData.getIntegerDef("${ModInfo.MODID}:organs.lungs")
			set(value) = entityData.setInteger("${ModInfo.MODID}:organs.lungs", value)
		
		var EntityPlayer.liver: Int
			get() = entityData.getIntegerDef("${ModInfo.MODID}:organs.liver")
			set(value) = entityData.setInteger("${ModInfo.MODID}:organs.liver", value)
		
		var EntityPlayer.nerves: Int
			get() = entityData.getIntegerDef("${ModInfo.MODID}:organs.nerves")
			set(value) = entityData.setInteger("${ModInfo.MODID}:organs.nerves", value)
		
		private fun NBTTagCompound.getIntegerDef(key: String) = if (hasKey(key)) getInteger(key) else -1
		
		init {
			eventForge()
		}
		
		fun ejectOrgans(player: EntityPlayer) {
			val has = arrayOf(player.heart, player.lungs, player.liver, player.nerves).map { it != -1 }
			if (has.all { it }) return
			
			val ids = (0..8).toMutableList().apply { repeat(5 + has.count { it }) { removeRandom() } }
			val olds = ArrayList<ItemStack>()
			
			var changed = false
			
			ids.forEachIndexed { id, it ->
				if (has[id]) return@forEachIndexed
				
				val old = player.inventory[it]
				if (old != null) olds += old.copy()
				
				player.inventory[it] = ItemStack(AlfheimItems.organs, 1, id)
				changed = true
			}
			
			olds.forEach { if (!player.inventory.addItemStackToInventory(it)) player.dropPlayerItemWithRandomChoice(it, false) }
			
			if (!has[0]) player.heart = MAX_DECAY_TIME
			if (!has[1]) player.lungs = MAX_DECAY_TIME
			if (!has[2]) player.liver = MAX_DECAY_TIME
			if (!has[3]) player.nerves = MAX_DECAY_TIME
			
			if (changed && player is EntityPlayerMP) {
				NetworkService.sendTo(MessageOrgans(player.heart, player.lungs, player.liver, player.nerves), player)
				player.sendContainerToPlayer(player.inventoryContainer)
				
				if (!player.hasAchievement(AlfheimAchievements.organs))
					player.triggerAchievement(AlfheimAchievements.organs)
			}
		}
		
		fun addItem(player: EntityPlayer, meta: Int) {
			val i = (0..8).toMutableList().apply { removeAll { player.inventory[it]?.item === AlfheimItems.organs } }.random() ?: 1
			val old = player.inventory[i]?.copy()
			player.inventory[i] = ItemStack(AlfheimItems.organs, 1, meta)
			if (old != null) if (!player.inventory.addItemStackToInventory(old)) player.dropPlayerItemWithRandomChoice(old, false)
		}
		
		fun reEjectOrgan(player: EntityPlayer, meta: Int, get: () -> Int, set: () -> Unit): Boolean {
			if (get() != -1) return false
			
			set()
			
			if ((0 until player.inventory.sizeInventory).any {
					player.inventory[it]?.item === AlfheimItems.organs && player.inventory[it]?.meta == meta
				})
				return true
			
			addItem(player, meta)
			
			if (player is EntityPlayerMP)
				player.sendContainerToPlayer(player.inventoryContainer)
			
			return true
		}
		
		@SubscribeEvent
		fun organsWatcher(e: LivingUpdateEvent) {
			val player = e.entityLiving as? EntityPlayer ?: return
			if (player.worldObj.isRemote) return
			
			run heart@ {
				val heart = player.heart
				
				if (heart == -1) return@heart
				
				if (heart > 0)
					player.heart = heart - 1
				
				if (heart != 0) return@heart
				
				if (ASJUtilities.chance(5)) reEjectOrgan(player, 1, { player.lungs }, { player.lungs = MAX_DECAY_TIME })
				if (ASJUtilities.chance(5)) reEjectOrgan(player, 2, { player.liver }, { player.liver = MAX_DECAY_TIME })
				if (ASJUtilities.chance(5)) reEjectOrgan(player, 3, { player.nerves }, { player.nerves = MAX_DECAY_TIME })
				
				if (player.ticksExisted % 20 != 0)
					return@heart
				
				val old = player.hurtResistantTime
				player.hurtResistantTime = 0
				player.attackEntityFrom(heartDamage, 2f)
				player.hurtResistantTime = old
			}
			
			run lungs@ {
				val lungs = player.lungs
				
				if (lungs == -1) return@lungs
				
				if (lungs > 0)
					player.lungs = lungs - 1
				
				if (lungs != 0) return@lungs
				
				if (ASJUtilities.chance(5)) {
					val nerves = player.nerves
					
					if (nerves > 0) {
						player.nerves = max(nerves - 10, 0)
					} else if (nerves == -1)
						reEjectOrgan(player, 3, { player.nerves }, { player.nerves = MAX_DECAY_TIME })
				}
				
				if (player.ticksExisted % 20 != 0)
					return@lungs
				
				val old = player.hurtResistantTime
				player.hurtResistantTime = 0
				player.attackEntityFrom(lungsDamage, 2f)
				player.hurtResistantTime = old
			}
			
			run liver@ {
				val liver = player.liver
				
				if (liver == -1) return@liver
				
				if (liver > 0)
					player.liver = liver - 1
				
				if (liver != 0) return@liver
				
				if (ASJUtilities.chance(10))
					if (player.lungs == -1)
						reEjectOrgan(player, 1, { player.lungs }, { player.lungs = MAX_DECAY_TIME })
				
				if (player.ticksExisted % 20 != 0)
					return@liver
				
				val old = player.hurtResistantTime
				player.hurtResistantTime = 0
				player.attackEntityFrom(liverDamage, 2f)
				player.hurtResistantTime = old
			}
			
			run nerves@ {
				val nerves = player.nerves
				
				if (nerves <= 0) return@nerves
				
				player.nerves = nerves - 1
			}
			
			fun ensureHasOrgans(meta: Int, get: () -> Int): Boolean {
				var invChanged = false
				
				if (get() == -1) {
					(0 until player.inventory.sizeInventory).forEach { 
						val stack = player.inventory[it] ?: return@forEach
						if (stack.item !== AlfheimItems.organs || stack.meta != meta)
							return@forEach
						
						player.inventory[it] = null
						invChanged = true
					}
				} else {
					(0 until player.inventory.sizeInventory).forEach {
						val stack = player.inventory[it] ?: return@forEach
						if (stack.item === AlfheimItems.organs && stack.meta == meta)
							return false
					}
					
					addItem(player, meta)
					invChanged = true
				}
				
				return invChanged
			}
			
			val invChanged = ensureHasOrgans(0) { player.heart } ||
							 ensureHasOrgans(1) { player.lungs } ||
							 ensureHasOrgans(2) { player.liver } ||
							 ensureHasOrgans(3) { player.nerves }
			
			if (player is EntityPlayerMP && invChanged)
				player.sendContainerToPlayer(player.inventoryContainer)
			
			if (player is EntityPlayerMP)
				NetworkService.sendTo(MessageOrgans(player.heart, player.lungs, player.liver, player.nerves), player)
		}
		
		@SideOnly(Side.CLIENT)
		@SubscribeEvent(priority = EventPriority.LOWEST)
		fun removeVision(e: RenderWorldLastEvent) {
			if (mc.thePlayer.nerves != 0) return
			
			glClearColor(0f, 0f, 0f, 0f)
			glClear(GL_COLOR_BUFFER_BIT)
		}
		
		@SideOnly(Side.CLIENT)
		@SubscribeEvent(priority = EventPriority.HIGHEST)
		fun removeHand(e: RenderHandEvent) {
			if (mc.thePlayer.nerves == 0)
				e.isCanceled = true
		}
		
		@SideOnly(Side.CLIENT)
		@SubscribeEvent(priority = EventPriority.HIGHEST)
		fun removeHealthHunger(e: RenderGameOverlayEvent) {
			renderMinigame(e)
			
			if (mc.thePlayer.nerves != 0) return
			
			if (e.type == RenderGameOverlayEvent.ElementType.HEALTH || e.type == RenderGameOverlayEvent.ElementType.FOOD)
				e.isCanceled = true
		}
		
		@SideOnly(Side.CLIENT)
		fun renderMinigame(e: RenderGameOverlayEvent) {
			if (e.type != RenderGameOverlayEvent.ElementType.TEXT) return
			
			val b = 2
			val w = 128
			val h = 16
			
			val stack = mc.thePlayer.itemInUse ?: return
			if (stack.item !== AlfheimItems.organs) return
			
			val start = getInt(stack, TAG_MINIGAME_START, -1)
			if (start == -1) return
			
			val time = AlfheimItems.organs.getMaxItemUseDuration(stack) - mc.thePlayer.itemInUseCount
			
			var progress = time % (MAX_PROGRESS * 2)
			if (progress > MAX_PROGRESS)
				progress = MAX_PROGRESS * 2 - progress
			
			var prev = (time - 1) % (MAX_PROGRESS * 2)
			if (prev > MAX_PROGRESS)
				prev = MAX_PROGRESS * 2 - prev
			
			Tessellator.instance.addTranslation(e.resolution.scaledWidth / 2f - w / 2, e.resolution.scaledHeight.F - 64 - h, 0f)
			
			val end = start + PROGRESS_RANGE
			
			Gui.drawRect(-b, -b, w + b, h + b, 0x44888888u.toInt())
			Gui.drawRect(0, 0, w, h, 0x88880000u.toInt())
			
			Gui.drawRect((start.F / MAX_PROGRESS * w).I, 0, (end.F / MAX_PROGRESS * w).I, h, 0xFF008800u.toInt())
			
			val pos = ASJRenderHelper.interpolate(prev.D, progress.D).F
			Tessellator.instance.addTranslation(pos * (w / MAX_PROGRESS.F), 0f, 0f)
			Gui.drawRect(-1, 0, 1, h, 0xFFFFFFFFu.toInt())
			
			Tessellator.instance.setTranslation(0.0, 0.0, 0.0)
		}
		
		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		fun hideMMOUI(e: PartyGuiRenderEvent) {
			if (mc.thePlayer.nerves == 0)
				e.isCanceled = true
		}
	}
}
