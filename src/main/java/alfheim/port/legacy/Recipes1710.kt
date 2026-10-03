package alfheim.port.legacy

import alfheim.api.ModInfo.MODID
import alfheim.port.data.OreDictTags
import net.minecraft.core.NonNullList
import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.world.Container
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.*
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraftforge.common.crafting.PartialNBTIngredient
import vazkii.botania.common.block.BotaniaBlocks
import vazkii.botania.common.item.BotaniaItems

/*
 * Рецепты 1.7.10 (SPEC, Р-9; MAPPING.md, «Рецепты»). В 1.20.1 рецепты — данные мода. Код автора (`AlfheimRecipes`)
 * остаётся как был: его вызовы 1.7.10 записывают рецепты сюда — в `CraftingManager` и `FurnaceRecipes`, — а генерация
 * данных (`alfheim.port.data.AlfheimRecipeProvider`) пишет из записей JSON рецептов 1.20.1. В игре код автора не
 * выполняется: рецепты приходят из JSON.
 */

/** `InventoryCrafting` 1.7.10 — сетка верстака */
typealias InventoryCrafting = CraftingContainer

/** `IInventory.sizeInventory` 1.7.10 */
val Container.sizeInventory get() = containerSize

/** Метка стека «любая metadata» ([ItemStack] с `WILDCARD_VALUE`) */
private const val TAG_WILDCARD = "$MODID:wildcard1710"

/**
 * `ItemStack(item, size, OreDictionary.WILDCARD_VALUE)` 1.7.10 — ингредиент «с любой metadata»: у инструмента — с
 * любым повреждением. Вариант metadata в 1.20.1 — отдельный предмет (`ItemStack(items[meta])`, MAPPING.md, «Имена и
 * metadata»), поэтому другая metadata здесь — ошибка переноса
 */
fun ItemStack(item: ItemLike, size: Int, meta: Int): ItemStack {
	require(meta == OreDictionary.WILDCARD_VALUE) { "1.7.10 metadata $meta of $item is a variant: write ItemStack(items[meta])" }
	return ItemStack(item, size).also { it.orCreateTag.putBoolean(TAG_WILDCARD, true) }
}

/**
 * Ингредиент рецепта 1.7.10 → `Ingredient` 1.20.1. Как в 1.7.10, вещь сравнивается по предмету и metadata, без NBT и
 * числа в стеке:
 * - имя Ore Dictionary — тег или предмет по таблице [ore];
 * - `ItemStack` — его предмет; повреждаемый предмет (инструмент) — только целый, как metadata 0 в 1.7.10, а
 *   `ItemStack(x, n, WILDCARD_VALUE)` — с любым повреждением;
 * - `Item` и `Block` — как у рецепта этого вида в 1.7.10 ([Mode]): metadata 0 или любая. Блок или предмет с
 *   вариантами metadata в порту — массив (`alfStorage`, `livingcobble`): «любая» — все варианты, 0 — первый.
 */
object Ingredients1710 {

	/**
	 * Вид рецепта 1.7.10: что значат переданные без стека `Item` и `Block`. Рецепт по шаблону (`ShapedOreRecipe`,
	 * `ShapedRecipes`): предмет — metadata 0, блок — любая; рецепт без формы — оба metadata 0; печь
	 * (`GameRegistry.addSmelting`) — оба любая
	 */
	enum class Mode(val anyItem: Boolean, val anyBlock: Boolean) { SHAPED(false, true), SHAPELESS(false, false), SMELTING(true, true) }

	fun of(input: Any?, mode: Mode): Ingredient = when (input) {
		null         -> Ingredient.EMPTY
		is String    -> ore(input)
		is ItemStack -> stack(input.item, input.tag?.getBoolean(TAG_WILDCARD) == true)
		is Item      -> stack(input, mode.anyItem)
		is Block     -> stack(input.asItem(), mode.anyBlock)
		is Array<*>  -> variants(input, mode)
		else         -> throw IllegalArgumentException("Unknown 1.7.10 recipe ingredient $input")
	}

	private fun variants(variants: Array<*>, mode: Mode): Ingredient {
		val any = if (variants.all { it is Block }) mode.anyBlock else if (variants.all { it is Item }) mode.anyItem else throw IllegalArgumentException("Not a 1.7.10 metadata variant array: ${variants.contentToString()}")
		return if (any) Ingredient.of(*variants.map { (it as ItemLike).asItem() }.toTypedArray()) else of(variants[0], mode)
	}

	/** Целый инструмент: у повреждаемого стека 1.20.1 всегда есть `Damage` (0 — целый) */
	private fun stack(item: Item, any: Boolean): Ingredient {
		if (item === Items.AIR) throw IllegalArgumentException("1.7.10 recipe ingredient has no item")
		return if (any || !item.canBeDepleted()) Ingredient.of(item) else PartialNBTIngredient.of(item, CompoundTag().apply { putInt("Damage", 0) })
	}

	/**
	 * Имя Ore Dictionary 1.7.10 → ингредиент 1.20.1 (MAPPING.md, «Ore Dictionary»). Имена мода — его теги
	 * ([OreDictTags.names], их строит генерация данных). Имена Forge 1.7.10 — общий тег Forge или ванилы 1.20.1, если
	 * он есть, иначе предмет. Имена Botania 1.7.10 — то, что ставит на их место в свои рецепты Botania 1.20.1: её тег
	 * или предмет. Имя без строки — ошибка генерации данных: строку добавляет КТ, рецептам которой имя понадобилось
	 */
	fun ore(name: String): Ingredient {
		OreDictTags.names[name]?.let { return Ingredient.of(ItemTags.create(it)) }
		return when (val value = ores[name] ?: throw IllegalArgumentException("No 1.20.1 ingredient for Ore Dictionary name $name: add it to Ingredients1710.ores")) {
			is ResourceLocation -> Ingredient.of(ItemTags.create(value))
			is ItemLike         -> Ingredient.of(value)
			else                -> throw IllegalStateException("$name → $value")
		}
	}

	private val ores: Map<String, Any> by lazy {
		val colors = DyeColor.entries // порядок 1.7.10: White … Black
		mapOf(
			// Forge 1.7.10 (OreDictionary.initVanillaEntries)
			"cobblestone" to forge("cobblestone"),
			"dustGlowstone" to forge("dusts/glowstone"),
			"dustRedstone" to forge("dusts/redstone"),
			"gemEmerald" to forge("gems/emerald"),
			"gemQuartz" to forge("gems/quartz"),
			"glowstone" to Blocks.GLOWSTONE,
			"ingotGold" to forge("ingots/gold"),
			"ingotBrickNether" to forge("ingots/nether_brick"),
			"nuggetGold" to forge("nuggets/gold"),
			"stickWood" to forge("rods/wooden"),
			"treeSapling" to ResourceLocation("minecraft", "saplings"),
			"blockRedstone" to forge("storage_blocks/redstone"),
			*colors.map { "dye" + camel(it) to forge("dyes/${it.getName()}") }.toTypedArray(),
			*colors.map { "blockGlass" + camel(it) to forge("glass/${it.getName()}") }.toTypedArray(),
			// Botania r1.8-249 (ModBlocks, ModItems: registerOre)
			"livingrock" to BotaniaBlocks.livingrock,
			"livingwood" to botania("livingwood_logs"),
			"dreamwood" to botania("dreamwood_logs"),
			"ingotManasteel" to botania("manasteel_ingots"),
			"manaPearl" to BotaniaItems.manaPearl,
			"manaDiamond" to botania("mana_diamond_gems"),
			"livingwoodTwig" to BotaniaItems.livingwoodTwig,
			"ingotTerrasteel" to botania("terrasteel_ingots"),
			"eternalLifeEssence" to BotaniaItems.lifeEssence,
			"redstoneRoot" to BotaniaItems.redstoneRoot,
			"ingotElvenElementium" to botania("elementium_ingots"),
			"elvenPixieDust" to BotaniaItems.pixieDust,
			"elvenDragonstone" to botania("dragonstone_gems"),
			"bPlaceholder" to BotaniaItems.placeholder,
			"bRedString" to BotaniaItems.redString,
			"dreamwoodTwig" to BotaniaItems.dreamwoodTwig,
			"gaiaIngot" to BotaniaItems.gaiaIngot,
			"bEnderAirBottle" to BotaniaItems.enderAirBottle,
			"manaString" to BotaniaItems.manaString,
			"nuggetManasteel" to botania("manasteel_nuggets"),
			"nuggetTerrasteel" to botania("terrasteel_nuggets"),
			"nuggetElvenElementium" to botania("elementium_nuggets"),
			"livingRoot" to BotaniaItems.livingroot,
			"pebble" to BotaniaItems.pebble,
			"clothManaweave" to BotaniaItems.manaweaveCloth,
			"powderMana" to botania("mana_dusts"),
			"bVial" to BotaniaItems.vial,
			"bFlask" to BotaniaItems.flask,
			"lexicaBotania" to BotaniaItems.lexicon,
			"twigWand" to BotaniaItems.twigWand,
			"blockBlaze" to BotaniaBlocks.blazeBlock,
			*colors.map { "petal" + camel(it) to botania("petals/${it.getName()}") }.toTypedArray(),
			*colors.map { "mysticFlower" + camel(it) to BotaniaBlocks.getFlower(it) }.toTypedArray(),
			*colors.map { "mysticFlower" + camel(it) + "Double" to BotaniaBlocks.getDoubleFlower(it) }.toTypedArray(),
			*listOf("Water", "Fire", "Earth", "Air", "Spring", "Summer", "Autumn", "Winter", "Mana", "Lust", "Gluttony", "Greed", "Sloth", "Wrath", "Envy", "Pride")
				.zip(listOf(BotaniaItems.runeWater, BotaniaItems.runeFire, BotaniaItems.runeEarth, BotaniaItems.runeAir, BotaniaItems.runeSpring, BotaniaItems.runeSummer, BotaniaItems.runeAutumn, BotaniaItems.runeWinter, BotaniaItems.runeMana, BotaniaItems.runeLust, BotaniaItems.runeGluttony, BotaniaItems.runeGreed, BotaniaItems.runeSloth, BotaniaItems.runeWrath, BotaniaItems.runeEnvy, BotaniaItems.runePride))
				.map { (name, rune) -> "rune${name}B" to rune }.toTypedArray(),
			*listOf("Dark", "Mana", "Blaze", "Lavender", "Red", "Elven", "Sunny")
				.zip(listOf(BotaniaItems.darkQuartz, BotaniaItems.manaQuartz, BotaniaItems.blazeQuartz, BotaniaItems.lavenderQuartz, BotaniaItems.redQuartz, BotaniaItems.elfQuartz, BotaniaItems.sunnyQuartz))
				.map { (name, quartz) -> "quartz$name" to quartz }.toTypedArray(),
			"dirt" to Blocks.DIRT,
			"slabCobblestone" to Blocks.COBBLESTONE_SLAB,
			"powderBlaze" to Items.BLAZE_POWDER,
			"rodBlaze" to forge("rods/blaze"),
		)
	}

	/** `light_blue` → `LightBlue`: цвет в именах 1.7.10 */
	private fun camel(color: DyeColor) = color.getName().split('_').joinToString("") { it.replaceFirstChar(Char::uppercase) }

	private fun forge(path: String) = ResourceLocation("forge", path)

	private fun botania(path: String) = ResourceLocation("botania", path)
}

/**
 * `ShapedOreRecipe` (Forge 1.7.10): рецепт по шаблону — строки шаблона, затем пары «символ — ингредиент»; в начале
 * может стоять `true` (зеркальный шаблон, как и без него) или массив строк шаблона. Символ без пары — пустая клетка,
 * как в 1.7.10
 */
open class ShapedOreRecipe(result: ItemStack?, vararg recipe: Any?): IRecipe {

	val output: ItemStack? = result?.copy()

	/** Строки шаблона, все одной ширины */
	val pattern: List<String>

	/** Ингредиенты 1.7.10 по символам шаблона, как их передал автор */
	val inputs: Map<Char, Any?>

	init {
		var i = 0
		if (recipe[i] is Boolean) require(recipe[i++] == true) { "Non-mirrored shaped recipes are not supported in 1.20.1" }
		val rows = ArrayList<String>()
		if (recipe[i] is Array<*>) (recipe[i++] as Array<*>).mapTo(rows) { it as String }
		else while (recipe[i] is String) rows += recipe[i++] as String
		require(rows.isNotEmpty() && rows.size <= 3 && rows.all { it.length == rows[0].length && it.length <= 3 }) { "Invalid 1.7.10 shaped recipe pattern $rows for $result" }
		pattern = rows
		val map = LinkedHashMap<Char, Any?>()
		while (i < recipe.size) map[recipe[i++] as Char] = recipe[i++]
		inputs = map
	}

	/** Ингредиенты 1.20.1 по символам шаблона; символ без пары — пустая клетка */
	fun key(): Map<Char, Ingredient> = inputs.mapValues { (_, input) -> Ingredients1710.of(input, Ingredients1710.Mode.SHAPED) }

	/** Тот же рецепт 1.20.1: по нему [matches] */
	private val converted by lazy {
		val key = key()
		val ingredients = NonNullList.withSize(pattern[0].length * pattern.size, Ingredient.EMPTY)
		pattern.joinToString("").forEachIndexed { i, c -> ingredients[i] = key[c] ?: Ingredient.EMPTY }
		ShapedRecipe(ResourceLocation(MODID, "legacy"), "", CraftingBookCategory.MISC, pattern[0].length, pattern.size, ingredients, output ?: ItemStack.EMPTY)
	}

	override fun matches(inv: CraftingContainer, world: Level?) = converted.matches(inv, world)

	override fun getCraftingResult(inv: CraftingContainer): ItemStack? = output?.copy()

	override fun getRecipeSize() = pattern[0].length * pattern.size

	override fun getRecipeOutput() = output
}

/** `ShapelessOreRecipe` (Forge 1.7.10): рецепт без формы — ингредиенты в любом порядке */
open class ShapelessOreRecipe(result: ItemStack?, vararg recipe: Any?): IRecipe {

	val output: ItemStack? = result?.copy()

	/** Ингредиенты 1.7.10, как их передал автор */
	val inputs: List<Any?> = recipe.toList()

	init {
		require(inputs.size in 1..9) { "Invalid 1.7.10 shapeless recipe $inputs for $result" }
	}

	fun ingredients(): List<Ingredient> = inputs.map { Ingredients1710.of(it, Ingredients1710.Mode.SHAPELESS) }

	/** Тот же рецепт 1.20.1: по нему [matches] */
	private val converted by lazy { ShapelessRecipe(ResourceLocation(MODID, "legacy"), "", CraftingBookCategory.MISC, output ?: ItemStack.EMPTY, NonNullList.of(Ingredient.EMPTY, *ingredients().toTypedArray())) }

	override fun matches(inv: CraftingContainer, world: Level?) = converted.matches(inv, world)

	override fun getCraftingResult(inv: CraftingContainer): ItemStack? = output?.copy()

	override fun getRecipeSize() = inputs.size

	override fun getRecipeOutput() = output
}

/** `ShapedRecipes` 1.7.10 (`CraftingManager.addRecipe`): те же правила, что у [ShapedOreRecipe], без имён Ore Dictionary */
class ShapedRecipes(result: ItemStack?, vararg recipe: Any?): ShapedOreRecipe(result, *recipe)

/** `ShapelessRecipes` 1.7.10 (`CraftingManager.addShapelessRecipe`): те же правила, что у [ShapelessOreRecipe] */
class ShapelessRecipes(result: ItemStack?, vararg recipe: Any?): ShapelessOreRecipe(result, *recipe)

/** `CraftingManager` 1.7.10: рецепты верстака в порядке добавления */
object CraftingManager {

	@JvmStatic
	fun getInstance() = this

	val recipeList: MutableList<IRecipe> = ArrayList()

	fun addRecipe(output: ItemStack, vararg params: Any?): IRecipe = ShapedRecipes(output, *params).also { recipeList += it }

	fun addShapelessRecipe(output: ItemStack, vararg params: Any?): IRecipe = ShapelessRecipes(output, *params).also { recipeList += it }
}

/** Переплавка 1.7.10: [input] — ингредиент 1.7.10 (`ItemStack`, `Item`, `Block` или массив вариантов) */
class Smelting1710(val input: Any, val output: ItemStack, val experience: Float) {

	fun ingredient() = Ingredients1710.of(input, Ingredients1710.Mode.SMELTING)
}

/** `FurnaceRecipes` 1.7.10: переплавка в печи в порядке добавления */
object FurnaceRecipes {

	@JvmStatic
	fun smelting() = this

	val smeltingList = ArrayList<Smelting1710>()

	fun addSmelting(input: Any, output: ItemStack, experience: Float) {
		smeltingList += Smelting1710(input, output.copy(), experience)
	}
}

/**
 * `RecipeSorter` (Forge 1.7.10): имя особого рецепта — класса автора с [IRecipe]. В 1.20.1 имя в snake_case — id
 * особого рецепта и его сериализатора (`alfheim.port.registry.LegacySpecialRecipes`)
 */
object RecipeSorter {

	enum class Category { UNKNOWN, SHAPELESS, SHAPED }

	val names = LinkedHashMap<Class<*>, String>()

	@JvmStatic
	fun register(name: String, recipe: Class<*>, category: Category, dependencies: String) {
		names[recipe] = name
	}
}
