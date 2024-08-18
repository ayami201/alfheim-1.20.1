package alfheim.common.core.asm.hook.integration

import alfheim.common.core.asm.superwrapper.SuperWrapperHandler
import gloomyfolken.hooklib.asm.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import thaumcraft.api.IVisDiscountGear
import thaumcraft.api.aspects.Aspect
import vazkii.botania.common.Botania
import vazkii.botania.common.item.equipment.armor.manasteel.ItemManasteelArmor
import vazkii.botania.common.item.interaction.thaumcraft.*

object BotaniaVisDiscountHooks {
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun getVisDiscount(item: ItemElementiumHelmRevealing, stack: ItemStack?, player: EntityPlayer?, aspect: Aspect?) = 5
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun getVisDiscount(item: ItemManasteelHelmRevealing, stack: ItemStack?, player: EntityPlayer?, aspect: Aspect?) = 5
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun getVisDiscount(item: ItemTerrasteelHelmRevealing, stack: ItemStack?, player: EntityPlayer?, aspect: Aspect?) = 5
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun addInformation(item: ItemElementiumHelmRevealing, stack: ItemStack?, player: EntityPlayer?, list: MutableList<Any?>, b: Boolean) =
		addVisDiscountTooltip(item, stack, player, list, b)
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun addInformation(item: ItemManasteelHelmRevealing, stack: ItemStack?, player: EntityPlayer?, list: MutableList<Any?>, b: Boolean) =
		addVisDiscountTooltip(item, stack, player, list, b)
	
	@JvmStatic
	@Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
	fun addInformation(item: ItemTerrasteelHelmRevealing, stack: ItemStack?, player: EntityPlayer?, list: MutableList<Any?>, b: Boolean) =
		addVisDiscountTooltip(item, stack, player, list, b)
	
	@JvmStatic
	fun addVisDiscountTooltip(item: ItemManasteelArmor, stack: ItemStack?, player: EntityPlayer?, list: MutableList<Any?>, b: Boolean) {
		SuperWrapperHandler.addInformation(item, stack, player, list, b)
		if (!Botania.thaumcraftLoaded) return
		
		item as IVisDiscountGear
		list.add(EnumChatFormatting.DARK_PURPLE.toString() + StatCollector.translateToLocal("tc.visdiscount") + ": " + item.getVisDiscount(stack, player, null) + "%")
	}
}