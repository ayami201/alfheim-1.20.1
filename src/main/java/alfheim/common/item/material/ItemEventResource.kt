package alfheim.common.item.material

// PORT: импорты 1.20.1
import alexsocol.asjlib.*
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.ItemMod
import net.minecraft.world.item.*
import kotlin.math.*

// PORT: вариант metadata — отдельный предмет (SPEC, Р-5): номер варианта — meta, имя — из subItems
// (SnowRelic → alfheim:snow_relic), создают их массивом `Array(subItems.size) { ItemEventResource(it) }`
class ItemEventResource(val meta: Int): ItemMod("EventResource") {
	
	override val variant get() = meta
	
	override val variantName get() = subItems[meta]
	
	// PORT: иконки → модели предметов (alfheim.port.data)
//	val texture = arrayOfNulls<IIcon>(subItems.size)
	
	init {
		setHasSubtypes(true)
		creativeTab = AlfheimTab
		
		// PORT: анимированную текстуру 1.20.1 рисует сама по .mcmeta
//		if (ASJUtilities.isClient)
//			MinecraftForge.EVENT_BUS.register(this)
	}
	
	fun isInterpolated(meta: Int) = meta == EventResourcesMetas.LavaMelon
	
	// PORT: иконки → модели предметов (alfheim.port.data); арбуз-лава — анимированная текстура с interpolate
	/*
	override fun registerIcons(reg: IIconRegister) {
		for (i in subItems.indices)
			if (!isInterpolated(i))
				texture[i] = IconHelper.forName(reg, subItems[i], "materials")
	}
	
	override fun getIconFromDamage(meta: Int) = texture[max(0, min(meta, texture.size - 1))]
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun loadTextures(event: TextureStitchEvent.Pre) {
		if (event.map.textureType == 1)
			for (i in subItems.indices)
				if (isInterpolated(i))
					texture[i] = InterpolatedIconHelper.forName(event.map, subItems[i], "materials")
	}
	*/
	
	override fun getUnlocalizedName(stack: ItemStack) = "item.${subItems[max(0, min(stack.meta, subItems.size - 1))]}"
	
	// PORT: вариант — отдельный предмет: каждый выдаёт во вкладку только свою вещь
	override fun getSubItems(item: Item, tab: Any?, list: MutableList<Any?>) {
		for (i in subItems.indices) if (i == meta) list.add(ItemStack(item))
//		for (i in subItems.indices) list.add(ItemStack(item, 1, i))
	}
	
	fun addStringToTooltip(s: String, tooltip: MutableList<String?>) {
		tooltip.add(s.replace("&".toRegex(), "§"))
	}
	
	companion object {
		
		val subItems = arrayOf("SnowRelic", "VolcanoRelic", "LavaMelon")
	}
}

object EventResourcesMetas {
	
	val LavaMelon: Int
	val SnowRelic: Int
	val VolcanoRelic: Int
	
	init {
		val items = ItemEventResource.subItems
		LavaMelon = items.indexOf("LavaMelon")
		SnowRelic = items.indexOf("SnowRelic")
		VolcanoRelic = items.indexOf("VolcanoRelic")
	}
}