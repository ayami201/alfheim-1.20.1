package alfheim.common.crafting.recipe

import alexsocol.asjlib.*
import alfheim.api.*
import alfheim.api.AlfheimAPI.set
import alfheim.api.crafting.recipe.TunerIncantation
import alfheim.api.item.equipment.IElementalItem
import alfheim.common.core.helper.ElementalDamage
import alfheim.common.item.material.ItemElvenResource
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.enchantment.EnumEnchantmentType
import net.minecraft.inventory.IInventory
import net.minecraft.item.*
import net.minecraft.util.*
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import vazkii.botania.common.lib.LibOreDict

class IncantationEquipmentElementalTuning(val element: String, incantation: String): TunerIncantation<ItemStack>(ItemStack::class.java, incantation, arrayOf(), {
	ItemNBTHelper.setInt(it, TAG_ELEMENT_LEVEL, ItemNBTHelper.getInt(it, TAG_ELEMENT_LEVEL, 0) + 1)
	ItemNBTHelper.setString(it, TAG_ELEMENT, element)
	true
}) {
	
	val index = counter++
	
	init {
		AlfheimAPI.tunerIncantations[incantation.lowercase()] = this
	}
	
	override fun matches(inv: IInventory, target: ItemStack): Boolean {
		if (target.stackSize != 1) return false
		
		val item = target.item
		if (item is IElementalItem ||
		    !EnumEnchantmentType.weapon.canEnchantItem(item) && item !is ItemArmor ||
			ItemNBTHelper.getString(target, TAG_ELEMENT, element) != element ||
			ItemNBTHelper.getInt(target, TAG_ELEMENT_LEVEL, 0) >= 4)
			return false
		
		return super.matches(inv, target)
	}
	
	override fun getInputs(target: ItemStack, renderTick: Int): List<Any> {
		val level = ItemNBTHelper.getInt(target, TAG_ELEMENT_LEVEL, 0)
		val er = Array(level + 1) { ItemElvenResource.ballForElement(ElementalDamage.valueOf(element)) }
		val manaWeave = Array(level + 7) { LibOreDict.MANAWEAVE_CLOTH }
		return listOf(*er, *manaWeave)
	}
	
	companion object {
		
		var counter = 0
		
		const val TAG_ELEMENT = "${ModInfo.MODID}:element"
		const val TAG_ELEMENT_LEVEL = "${ModInfo.MODID}:element_level"
		
		init {
			if (ASJUtilities.isClient) eventForge()
		}
		
		@SubscribeEvent
		fun onTooltip(e: ItemTooltipEvent) {
			val stack = e.itemStack ?: return
			val item = stack.item
			
			val element: String
			var level: Int
			
			if (item is IElementalItem) {
				element = item.getElement(stack).name
				if (element == "COMMON") return
				
				level = item.getElementLevel(stack)
				if (level == 0) return
			} else {
				element = ItemNBTHelper.getString(stack, TAG_ELEMENT, "")
				if (element.isEmpty()) return
				
				level = ItemNBTHelper.getInt(stack, TAG_ELEMENT_LEVEL, 0)
				if (level !in 1..4) return
				
				if (item !is ItemArmor && level == 4)
					level = 5
			}
			
			val color = when (element) {
				"FIRE" -> EnumChatFormatting.RED
				"WATER" -> EnumChatFormatting.DARK_AQUA
				"AIR" -> EnumChatFormatting.YELLOW
				"EARTH" -> EnumChatFormatting.GREEN
				"ICE" -> EnumChatFormatting.AQUA
				"ELECTRIC" -> EnumChatFormatting.LIGHT_PURPLE
				"NATURE" -> EnumChatFormatting.DARK_GREEN
				"LIGHTNESS" -> EnumChatFormatting.GOLD
				"DARKNESS" -> EnumChatFormatting.DARK_GRAY
				"PSYCHIC" -> EnumChatFormatting.DARK_PURPLE
				else -> EnumChatFormatting.RESET
			}
			
			val textColor = EnumChatFormatting.BLUE
			val type = if (item is ItemArmor) "armor" else "weapon"
			
			val sting = textColor + StatCollector.translateToLocalFormatted(
				"alfheimmisc.attunement.$type",
				level * 25,
				color + StatCollector.translateToLocal("alfheimmisc.attunement.$element") + textColor
			)
			
			val u = StatCollector.translateToLocal("item.unbreakable")
			val posU = e.toolTip.indexOfLast { it.endsWith(u) }
			if (posU != -1) {
				e.toolTip.add(posU, sting)
				if (item is ItemArmor) e.toolTip.add(posU, "")
				return
			}
			
			val posD = e.toolTip.indexOfLast { it.startsWith("Durability: ") }
			if (posD != -1) {
				e.toolTip.add(posD, sting)
				if (item is ItemArmor) e.toolTip.add(posD, "")
				return
			}
			
			if (item is ItemArmor) e.toolTip.add("")
			e.toolTip.add(sting)
		}
	}
}