package alfheim.common.item.equipment.bauble

import alexsocol.asjlib.*
import alfheim.common.core.handler.EventHandler.isMagical
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.MathHelper
import net.minecraftforge.event.entity.living.LivingHurtEvent
import vazkii.botania.api.mana.*
import kotlin.math.max

class ItemCrescentMoonAmulet: ItemPendant("CrescentMoonAmulet"), IManaUsingItem {
	
	init {
		maxStackSize = 1
	}
	
	override fun onWornTick(stack: ItemStack, player: EntityLivingBase) {
		super.onWornTick(stack, player)
		if (stack.cooldown > 0) stack.cooldown--
	}
	
	override fun usesMana(stack: ItemStack) = true
	
	companion object {
		
		const val MANA_PER_DAMAGE = 100
		
		init {
			eventForge()
		}
		
		@SubscribeEvent
		fun onWearerHurt(e: LivingHurtEvent) {
			if (e.source.isDamageAbsolute) return
			
			val player = e.entityLiving as? EntityPlayer ?: return
			val amulet = PlayerHandler.getPlayerBaubles(player)[0]
			
			if (amulet?.item !is ItemCrescentMoonAmulet) return
			
			if (e.source.isMagical) {
				if (amulet.cooldown <= 0) {
					amulet.cooldown = 100
					e.ammount = max(0f, e.ammount - 10)
				}
			} else
				e.ammount -= ManaItemHandler.requestMana(amulet, player, MathHelper.ceiling_float_int(e.ammount * MANA_PER_DAMAGE), true) / (MANA_PER_DAMAGE * 10f)
		}
	}
}