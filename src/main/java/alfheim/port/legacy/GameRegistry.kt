package alfheim.port.legacy

import alfheim.port.registry.LegacyRegistration
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block

/**
 * `GameRegistry` 1.7.10 (SPEC, Р-4): регистрация блока или предмета под именем автора. Блок и предмет попадают в
 * событие регистрации 1.20.1 через [LegacyRegistration]; id — имя автора в snake_case, у варианта metadata — с его
 * номером ([LegacyBlock.variant]).
 *
 * Класс предмета-блока 1.7.10 (`ItemBlock` и его наследники автора) в порту — наследник `BlockItem` с конструктором
 * `(Block)` или `(Block, аргументы)`, как в 1.7.10.
 */
object GameRegistry {

	@JvmStatic
	fun registerBlock(block: Block, name: String) = registerBlock(block, BlockItem::class.java, name)

	/** [itemClass] `null` — блок без предмета, как в 1.7.10 */
	@JvmStatic
	fun registerBlock(block: Block, itemClass: Class<out Item>?, name: String, vararg itemArgs: Any): Block {
		LegacyRegistration.block(block, name, (block as? LegacyBlock)?.variant, itemClass?.let { itemFactory(it, itemArgs) })
		return block
	}

	@JvmStatic
	fun registerItem(item: Item, name: String): Item {
		LegacyRegistration.item(item, name)
		return item
	}

	/** Имя в реестре: `modid:name`, как `toString` у `UniqueIdentifier` 1.7.10 */
	@JvmStatic
	fun findUniqueIdentifierFor(block: Block): ResourceLocation = BuiltInRegistries.BLOCK.getKey(block)

	@JvmStatic
	fun findUniqueIdentifierFor(item: Item): ResourceLocation = BuiltInRegistries.ITEM.getKey(item)

	/** Как 1.7.10 создавал предмет-блок: конструктор `(Block, аргументы…)`; у `BlockItem` 1.20.1 — ещё и свойства предмета */
	fun itemFactory(itemClass: Class<out Item>, itemArgs: Array<out Any> = emptyArray()): (Block) -> Item = { block ->
		if (itemClass == BlockItem::class.java) BlockItem(block, Item.Properties())
		else itemClass.constructors.first { it.parameterCount == itemArgs.size + 1 }.newInstance(block, *itemArgs) as Item
	}

	// Рецепты 1.7.10 записываются для генерации данных (Recipes1710.kt)

	@JvmStatic
	fun addRecipe(output: ItemStack, vararg params: Any?) {
		addShapedRecipe(output, *params)
	}

	@JvmStatic
	fun addShapedRecipe(output: ItemStack, vararg params: Any?) = CraftingManager.getInstance().addRecipe(output, *params)

	@JvmStatic
	fun addShapelessRecipe(output: ItemStack, vararg params: Any?) {
		CraftingManager.getInstance().addShapelessRecipe(output, *params)
	}

	@JvmStatic
	fun addRecipe(recipe: IRecipe) {
		CraftingManager.getInstance().recipeList.add(recipe)
	}

	/** Блок или предмет без стека — любая metadata, как в 1.7.10; блок с вариантами в порту — массив блоков */
	@JvmStatic
	fun addSmelting(input: Block, output: ItemStack, xp: Float) = FurnaceRecipes.smelting().addSmelting(input, output, xp)

	@JvmStatic
	fun addSmelting(input: Array<out Block>, output: ItemStack, xp: Float) = FurnaceRecipes.smelting().addSmelting(input, output, xp)

	@JvmStatic
	fun addSmelting(input: Item, output: ItemStack, xp: Float) = FurnaceRecipes.smelting().addSmelting(input, output, xp)

	@JvmStatic
	fun addSmelting(input: ItemStack, output: ItemStack, xp: Float) = FurnaceRecipes.smelting().addSmelting(input.copy(), output, xp)
}

// Вызовы из `import cpw.mods.fml.common.registry.GameRegistry.*`

fun addRecipe(output: ItemStack, vararg params: Any?) = GameRegistry.addRecipe(output, *params)

fun addShapedRecipe(output: ItemStack, vararg params: Any?) = GameRegistry.addShapedRecipe(output, *params)

fun addShapelessRecipe(output: ItemStack, vararg params: Any?) = GameRegistry.addShapelessRecipe(output, *params)

fun addRecipe(recipe: IRecipe) = GameRegistry.addRecipe(recipe)

fun addSmelting(input: Block, output: ItemStack, xp: Float) = GameRegistry.addSmelting(input, output, xp)

fun addSmelting(input: Array<out Block>, output: ItemStack, xp: Float) = GameRegistry.addSmelting(input, output, xp)

fun addSmelting(input: Item, output: ItemStack, xp: Float) = GameRegistry.addSmelting(input, output, xp)

fun addSmelting(input: ItemStack, output: ItemStack, xp: Float) = GameRegistry.addSmelting(input, output, xp)
