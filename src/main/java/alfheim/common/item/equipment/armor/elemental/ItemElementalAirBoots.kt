package alfheim.common.item.equipment.armor.elemental

import alexsocol.asjlib.*
import alfheim.api.item.equipment.IElementalItem
import alfheim.common.core.helper.ElementalDamage
import alfheim.common.item.AlfheimItems
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.StatCollector
import net.minecraftforge.event.entity.living.*
import vazkii.botania.api.mana.ManaItemHandler

class ItemElementalAirBoots: ElementalArmor(3, "ElementalAirBoots"), IElementalItem {
	
	override fun getPixieChance(stack: ItemStack): Float {
		return 0.09f
	}
	
	override fun getElement(stack: ItemStack) = ElementalDamage.AIR
	
	override fun getElementLevel(stack: ItemStack) = 4
	
	@SideOnly(Side.CLIENT)
	override fun addInformation(stack: ItemStack?, player: EntityPlayer?, list: MutableList<Any?>, b: Boolean) {
		list.add(StatCollector.translateToLocal("item.ElementalArmor.desc1"))
		super.addInformation(stack, player, list, b)
	}
	
	companion object {
		
		const val ONEBLOCKCOST = 10
		
		init {
			eventForge()
		}
		
		@SubscribeEvent
		fun onEntityJump(event: LivingEvent.LivingJumpEvent) {
			val player = event.entityLiving as? EntityPlayer ?: return
			
			val boots = player.getCurrentArmor(0) ?: return
			if (boots.item !== AlfheimItems.elementalBoots || !ManaItemHandler.requestManaExact(boots, player, ONEBLOCKCOST * 10, !player.worldObj.isRemote)) return
			
			event.entityLiving.motionY += 0.5
		}
		
		@SubscribeEvent
		fun onEntityFall(event: LivingFallEvent) {
			val player = event.entityLiving as? EntityPlayer ?: return
			
			val boots = player.getCurrentArmor(0) ?: return
			if (boots.item !== AlfheimItems.elementalBoots) return
			
			if (event.distance < 4.5) {
				event.distance = 0f
				return
			}
			
			event.distance -= ManaItemHandler.requestMana(boots, event.entityLiving as EntityPlayer, (event.distance * ONEBLOCKCOST).I, true)
		}
	}
}
