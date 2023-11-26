@file:Suppress("UNUSED_PARAMETER", "UNCHECKED_CAST")

package alfheim.common.core.asm

import alfheim.common.core.superwrapper.SuperWrapperHandler
import gloomyfolken.hooklib.asm.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.launchwrapper.IClassTransformer
import net.minecraft.util.*
import org.objectweb.asm.*
import org.objectweb.asm.tree.*
import thaumcraft.api.IVisDiscountGear
import thaumcraft.api.aspects.Aspect
import vazkii.botania.common.Botania
import vazkii.botania.common.item.equipment.armor.manasteel.ItemManasteelArmor
import vazkii.botania.common.item.interaction.thaumcraft.*

class BotaniaVisDiscountHooks: IClassTransformer {
	
	val iface = "thaumcraft/api/IVisDiscountGear"
	
	val classes = listOf(
		"vazkii.botania.common.item.interaction.thaumcraft.ItemElementiumHelmRevealing",
		"vazkii.botania.common.item.interaction.thaumcraft.ItemManasteelHelmRevealing",
		"vazkii.botania.common.item.interaction.thaumcraft.ItemTerrasteelHelmRevealing"
	)
	
	override fun transform(name: String, transformedName: String, basicClass: ByteArray?): ByteArray? {
		if (basicClass == null || basicClass.isEmpty() || transformedName !in classes) return basicClass
		
		var resultClass = basicClass
		
		println("Appending interface(s) $iface to $transformedName")
		val cr = ClassReader(resultClass)
		val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
		
		val cn = ClassNode()
		cr.accept(cn, ClassReader.EXPAND_FRAMES)
		
		cn.interfaces.add(iface)
		
		val interfaceListAnnotation = cn.visibleAnnotations.first { "Lcpw/mods/fml/common/Optional\$InterfaceList;" == it.desc }
		val valueIndex = interfaceListAnnotation.values.indexOfFirst { it == "value" } + 1
		val an = AnnotationNode(Opcodes.ASM5, "Lcpw/mods/fml/common/Optional\$Interface;")
		an.values = listOf("modid", "Thaumcraft", "iface", "thaumcraft.api.IVisDiscountGear", "striprefs", java.lang.Boolean.TRUE)
		(interfaceListAnnotation.values[valueIndex] as java.util.ArrayList<AnnotationNode>).add(an)
		
		cn.accept(cw)
		resultClass = cw.toByteArray()
		
		return resultClass
	}
	
	companion object {
		
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
}