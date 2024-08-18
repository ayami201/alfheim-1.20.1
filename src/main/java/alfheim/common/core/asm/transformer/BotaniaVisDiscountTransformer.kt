package alfheim.common.core.asm.transformer

import net.minecraft.launchwrapper.IClassTransformer
import org.objectweb.asm.*
import org.objectweb.asm.tree.ClassNode
import vazkii.botania.common.Botania

class BotaniaVisDiscountTransformer: IClassTransformer {
	
	val iface = "thaumcraft/api/IVisDiscountGear"
	
	val classes = listOf(
		"vazkii.botania.common.item.interaction.thaumcraft.ItemElementiumHelmRevealing",
		"vazkii.botania.common.item.interaction.thaumcraft.ItemManasteelHelmRevealing",
		"vazkii.botania.common.item.interaction.thaumcraft.ItemTerrasteelHelmRevealing"
	)
	
	override fun transform(name: String, transformedName: String, basicClass: ByteArray?): ByteArray? {
		if (basicClass == null || basicClass.isEmpty() || transformedName !in classes || !Botania.thaumcraftLoaded) return basicClass
		
		var resultClass = basicClass
		
		println("Appending interface(s) $iface to $transformedName")
		val cr = ClassReader(resultClass)
		val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
		
		val cn = ClassNode()
		cr.accept(cn, ClassReader.EXPAND_FRAMES)
		
		cn.interfaces.add(iface)
		
//		val interfaceListAnnotation = cn.visibleAnnotations.first { "Lcpw/mods/fml/common/Optional\$InterfaceList;" == it.desc }
//		val valueIndex = interfaceListAnnotation.values.indexOfFirst { it == "value" } + 1
//		val an = AnnotationNode(Opcodes.ASM5, "Lcpw/mods/fml/common/Optional\$Interface;")
//		an.values = listOf("modid", "Thaumcraft", "iface", "thaumcraft.api.IVisDiscountGear", "striprefs", java.lang.Boolean.TRUE)
//		(interfaceListAnnotation.values[valueIndex] as java.util.ArrayList<AnnotationNode>).add(an)
		
		cn.accept(cw)
		resultClass = cw.toByteArray()
		
		return resultClass
	}
}
