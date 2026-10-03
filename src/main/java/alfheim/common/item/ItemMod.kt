package alfheim.common.item

// PORT: импорты 1.20.1; Item 1.7.10 — alfheim.port.legacy.Item1710 (MAPPING.md, «Блоки и предметы»)
import alfheim.api.ModInfo
import alfheim.common.core.util.AlfheimTab
import alfheim.port.legacy.*
import net.minecraft.network.chat.Component
import net.minecraft.world.item.*

open class ItemMod(name: String): Item1710() {
	
	init {
		creativeTab = AlfheimTab
		unlocalizedName = name
	}
	
	override fun setUnlocalizedName(name: String): Item {
		GameRegistry.registerItem(this, name)
		return super.setUnlocalizedName(name)
	}
	
	// PORT: getItemStackDisplayName → getName: имя — перевод ключа автора на этой стороне (StatCollector), коды «&» →
	// «§», как в 1.7.10
	override fun getName(stack: ItemStack): Component = Component.literal(
		StatCollector.translateToLocal(getDescriptionId(stack)).trim().replace("&".toRegex(), "\u00a7"))
//	override fun getItemStackDisplayName(stack: ItemStack) =
//		super.getItemStackDisplayName(stack).replace("&".toRegex(), "\u00a7")
	
	override fun getUnlocalizedNameInefficiently(stack: ItemStack) =
		getUnlocalizedName(stack).replace("item\\.".toRegex(), "item.${ModInfo.MODID}:")
	
	// PORT: иконка → модель предмета (alfheim.port.data); текстура — по имени предмета
	/*
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		itemIcon = IconHelper.forItem(reg, this)
	}
	*/
}
