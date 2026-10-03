package alfheim.port.data

import alfheim.api.ModInfo.MODID
import alfheim.common.item.ItemMod
import alfheim.common.item.ItemSplashPotion
import alfheim.common.item.material.*
import alfheim.common.item.material.ElvenResourcesMetas.*
import alfheim.port.legacy.*
import alfheim.port.registry.LegacyRegistration
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.*
import net.minecraftforge.client.model.generators.ItemModelBuilder
import net.minecraftforge.client.model.generators.ItemModelProvider
import net.minecraftforge.common.data.ExistingFileHelper

/**
 * Модели предметов автора: иконки 1.7.10 (`registerIcons` / `getIcon(stack, pass)`) → модели 1.20.1
 * (MAPPING.md, «Блоки и предметы»). Модели предметов-блоков строит [AlfheimBlockStates].
 *
 * Проход рендера 1.7.10 с номером n — слой `layer<n>` модели `item/generated`: 1.20.1 ставит ему цвет с тем же
 * номером, а клиент красит его цветом `getColorFromItemStack(stack, n)` (alfheim.port.client.AlfheimItemColors).
 * Предмет, который `setFull3D` держал в руке как инструмент, — `item/handheld`.
 *
 * Текстура по умолчанию — та, что регистрировал `ItemMod` (`IconHelper.forItem`: имя предмета). Предмет со своим
 * `registerIcons` получает модель здесь же, по своему `registerIcons` (он закомментирован в классе предмета рядом).
 */
class AlfheimItemModels(output: PackOutput, files: ExistingFileHelper): ItemModelProvider(output, MODID, files) {

	override fun registerModels() {
		for (item in LegacyRegistration.items.keys) when (item) {
			is BlockItem         -> Unit
			is ItemElvenResource -> elvenResource(item)
			is ItemElvenFood     -> elvenFood(item)
			is ItemEventResource -> item(item, "materials/${ItemEventResource.subItems[item.meta]}")
			is ItemSplashPotion  -> splashPotion(item)
			is ItemMod           -> item(item, item.legacy.unlocalizedName.removePrefix("item."))
			else                 -> throw IllegalStateException("No 1.7.10 icon rule for ${item.javaClass.name}")
		}

		// ItemElvenResource.getIcon: на праздник (AlfheimCore.jingleTheBells) прутик рисуется конфетой; модель
		// подставляет клиент (alfheim.port.client.AlfheimModels)
		withExistingParent(INFUSED_CANDY, mcLoc("item/generated")).texture("layer0", legacyTexture("$MODID:materials/CandyCane"))
	}

	/**
	 * ItemElvenResource.registerIcons и getIcon: иконка варианта — `materials/<имя>`, у осколков разлома — иконка
	 * пустого осколка; второй проход (getRenderPasses) у ElvenWeed и RiftDrive — `materials/<имя>1`
	 */
	private fun elvenResource(item: ItemElvenResource) {
		val type = ElvenResourcesMetas.of(item.meta)!!
		val icons = mutableListOf("materials/${if (item.meta in item.riftIcons) RiftShardEmpty else type}")
		if (type == ElvenWeed || type == RiftDrive) icons += "materials/${type}1"
		item(item, *icons.toTypedArray())
	}

	/**
	 * ItemElvenFood.registerIcons и getIcon: иконка варианта — `materials/food/<имя>`; пиво с именем «Cerveza Cristal»
	 * (`ItemElvenFood.isCC`) — `materials/food/cc`: модель [CC] по свойству [CC_PROPERTY], которое ставит клиент
	 * (alfheim.port.client.AlfheimModels)
	 */
	private fun elvenFood(item: ItemElvenFood) {
		val model = item(item, "materials/food/${ElvenFoodMetas.entries[item.meta]}")
		if (item.meta != ElvenFoodMetas.Beer.I) return
		val cc = withExistingParent(CC, mcLoc("item/generated")).texture("layer0", legacyTexture("$MODID:materials/food/cc"))
		model.override().predicate(CC_PROPERTY, 1f).model(cc).end()
	}
	
	/**
	 * ItemSplashPotion.registerIcons и getIcon: склянка `vial0` и жидкость `vial1_0` Botania (проходы 0 и 1) — те же
	 * иконки Botania 1.20.1: `item/vial` и `item/brew_vial_0` (полная склянка с варевом)
	 */
	private fun splashPotion(item: ItemSplashPotion) {
		withExistingParent(LegacyRegistration.items[item]!!.id.path, mcLoc("item/generated"))
			.texture("layer0", ResourceLocation("botania", "item/vial"))
			.texture("layer1", ResourceLocation("botania", "item/brew_vial_0"))
	}
	
	/** Модель предмета с иконкой на каждый проход рендера, по порядку */
	private fun item(item: Item, vararg icons: String): ItemModelBuilder {
		val full3D = (item as LegacyItem).legacy.full3D
		val model = withExistingParent(LegacyRegistration.items[item]!!.id.path, mcLoc(if (full3D) "item/handheld" else "item/generated"))
		icons.forEachIndexed { pass, icon -> model.texture("layer$pass", legacyTexture("$MODID:$icon")) }
		return model
	}

	companion object {

		/** Модель прутика на праздник */
		const val INFUSED_CANDY = "infused_candy"
		
		/** Модель пива Cerveza Cristal */
		const val CC = "cc"
		
		/** Свойство модели пива: 1 — Cerveza Cristal */
		val CC_PROPERTY = ResourceLocation(MODID, "cc")

		/** Имя иконки предмета 1.7.10 `modid:путь` → спрайт 1.20.1 `modid:items/путь` (MAPPING.md, «Ресурсы») */
		fun legacyTexture(icon: String): ResourceLocation {
			val modid = icon.substringBefore(':', "minecraft")
			return ResourceLocation(modid, "items/" + legacyPath(icon.substringAfter(':')))
		}
	}
}
