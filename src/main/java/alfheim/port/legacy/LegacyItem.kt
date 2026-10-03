package alfheim.port.legacy

import alfheim.port.registry.LegacyIds
import net.minecraft.world.item.*

/*
 * Предмет с сеттерами 1.7.10 (SPEC, Р-4; MAPPING.md, «Блоки и предметы»).
 *
 * В 1.7.10 предмет создавался пустым и настраивался потом: `setUnlocalizedName`, `setMaxStackSize`,
 * `setHasSubtypes`… — в `init` предмета или цепочкой. В 1.20.1 это свойства, которые передаются в конструктор.
 * Чтобы код автора остался как был, базовый класс порта [Item1710] создаёт предмет без свойств, а сеттеры 1.7.10
 * меняют его потом; 1.20.1 читает их методами предмета (`getMaxStackSize(stack)`, `getMaxDamage(stack)` и др.).
 *
 * Имя предмета — ключ перевода автора: `getUnlocalizedNameInefficiently(stack) + ".name"`, как
 * `getItemStackDisplayName` 1.7.10, через раздел `lang` файла `legacy_ids.json` (ключ, переименованный в ключ
 * 1.20.1, — новый). Поэтому имена, которые автор собирал в коде (по NBT, по празднику), работают как у него.
 */

/** Значения сеттеров 1.7.10 */
class ItemProps {

	/** `getUnlocalizedName()` 1.7.10: `item.` + имя из `setUnlocalizedName` */
	var unlocalizedName = "item.null"
	/** `setTextureName` 1.7.10; читает генерация моделей */
	var iconString: String? = null
	var maxStackSize = 64
	var maxDamage = 0
	var hasSubtypes = false
	var full3D = false
	var containerItem: Item? = null
	var canRepair = true
}

/** Предмет порта со свойствами 1.7.10; реализует [Item1710] */
interface LegacyItem {

	val legacy: ItemProps

	/** Номер варианта, если в 1.7.10 предмет был одной из metadata (SPEC, Р-5); иначе `null` */
	val variant: Int? get() = null

	/**
	 * Имя варианта, если у автора оно не «имя предмета + номер», а своё: вариант 3 предмета `ElvenItems` —
	 * `ElvoriumIngot` (так его звали текстура и ключ перевода). По нему id 1.20.1: `elvorium_ingot`
	 */
	val variantName: String? get() = null
}

/** `net.minecraft.item.Item` 1.7.10 */
open class Item1710: Item(Properties()), LegacyItem {

	final override val legacy = ItemProps()

	open fun setUnlocalizedName(name: String): Item {
		legacy.unlocalizedName = "item.$name"
		return this
	}

	fun setTextureName(name: String): Item {
		legacy.iconString = name
		return this
	}

	fun setMaxStackSize(size: Int): Item {
		legacy.maxStackSize = size
		return this
	}

	fun setMaxDamage(damage: Int): Item {
		legacy.maxDamage = damage
		return this
	}

	fun setHasSubtypes(subtypes: Boolean): Item {
		legacy.hasSubtypes = subtypes
		return this
	}

	/** Вкладка 1.20.1 сама перечисляет свои вещи (`AlfheimTab`) */
	open fun setCreativeTab(@Suppress("UNUSED_PARAMETER") tab: Any?): Item = this

	fun setFull3D(): Item {
		legacy.full3D = true
		return this
	}

	fun setContainerItem(item: Item?): Item {
		legacy.containerItem = item
		return this
	}

	fun setNoRepair(): Item {
		legacy.canRepair = false
		return this
	}

	// Свойства-сеттеры, как их видел Kotlin в 1.7.10 (`unlocalizedName = name`, `creativeTab = tab`)

	var unlocalizedName: String
		@JvmName("getUnlocalizedName1710") get() = legacy.unlocalizedName
		@JvmName("setUnlocalizedName1710") set(value) {
			setUnlocalizedName(value)
		}

	var creativeTab: Any?
		@JvmName("getCreativeTab1710") get() = null
		@JvmName("setCreativeTab1710") set(value) {
			setCreativeTab(value)
		}

	var maxStackSize: Int
		@JvmName("getMaxStackSize1710") get() = legacy.maxStackSize
		@JvmName("setMaxStackSize1710") set(value) {
			setMaxStackSize(value)
		}

	val hasSubtypes get() = legacy.hasSubtypes

	// Имя: ключ перевода 1.7.10

	/** `getUnlocalizedName(stack)` 1.7.10 */
	open fun getUnlocalizedName(stack: ItemStack): String = legacy.unlocalizedName

	/** `getUnlocalizedNameInefficiently(stack)` 1.7.10: из него и `.name` — ключ имени */
	open fun getUnlocalizedNameInefficiently(stack: ItemStack): String = getUnlocalizedName(stack)

	/** Ключ имени 1.7.10 для этого стака → ключ 1.20.1 по `legacy_ids.json` */
	override fun getDescriptionId(stack: ItemStack): String {
		val key = getUnlocalizedNameInefficiently(stack) + ".name"
		return LegacyIds.lang[key] ?: key
	}

	override fun getDescriptionId(): String = getDescriptionId(ItemStack(this))

	// Свойства, которые 1.20.1 читает методами предмета

	/** `getItemStackLimit(stack)` 1.7.10 */
	open fun getItemStackLimit(stack: ItemStack) = legacy.maxStackSize

	final override fun getMaxStackSize(stack: ItemStack) = getItemStackLimit(stack)

	override fun getMaxDamage(stack: ItemStack) = legacy.maxDamage

	/** `isDamageable` 1.7.10: есть прочность и нет вариантов */
	override fun isDamageable(stack: ItemStack) = legacy.maxDamage > 0 && !legacy.hasSubtypes

	override fun isRepairable(stack: ItemStack) = legacy.canRepair && isDamageable(stack)

	/** `hasContainerItem(stack)` 1.7.10 */
	open fun hasContainerItem(stack: ItemStack) = legacy.containerItem != null

	/** `getContainerItem(stack)` 1.7.10; `null` — ничего не остаётся */
	open fun getContainerItem(stack: ItemStack): ItemStack? = legacy.containerItem?.let { ItemStack(it) }

	final override fun hasCraftingRemainingItem(stack: ItemStack) = hasContainerItem(stack)

	final override fun getCraftingRemainingItem(stack: ItemStack): ItemStack = (if (hasContainerItem(stack)) getContainerItem(stack) else null) ?: ItemStack.EMPTY

	/** `hasEffect(stack, pass)` 1.7.10 — блеск зачарования; 1.20.1 рисует его на всём предмете, как на проходе 0 */
	open fun hasEffect(stack: ItemStack, pass: Int) = pass == 0 && stack.isEnchanted

	override fun isFoil(stack: ItemStack) = hasEffect(stack, 0)

	/** `getColorFromItemStack(stack, pass)` 1.7.10 — цвет прохода рендера; клиент красит им слой модели с тем же номером */
	open fun getColorFromItemStack(stack: ItemStack, pass: Int) = 0xFFFFFF
}
