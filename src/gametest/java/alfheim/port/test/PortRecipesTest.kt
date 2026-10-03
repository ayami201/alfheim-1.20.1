package alfheim.port.test

import alfheim.api.ModInfo.MODID
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.AlfheimFluffBlocks
import alfheim.common.item.AlfheimItems
import alfheim.common.item.ItemSplashPotion
import alfheim.common.item.material.ElvenResourcesMetas.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.inventory.TransientCraftingContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.*
import net.minecraft.gametest.framework.*
import net.minecraftforge.gametest.GameTestHolder
import net.minecraftforge.gametest.PrefixGameTestTemplate
import vazkii.botania.common.block.BotaniaBlocks
import vazkii.botania.common.brew.BotaniaBrews
import vazkii.botania.common.item.BotaniaItems
import vazkii.botania.common.item.brew.BaseBrewItem

/**
 * КТ-2, партия 7а: рецепты автора (`AlfheimRecipes`) — данные 1.20.1 из генерации данных. Проверяется то, что
 * загрузила игра: каждый JSON стал рецептом, у каждого ингредиента есть предметы, раскладку на верстаке занимает один
 * рецепт, выборочные рецепты дают то же, что у автора
 */
@GameTestHolder(MODID)
@PrefixGameTestTemplate(false)
object PortRecipesTest {

	/**
	 * Раскладки, которые занимает и рецепт автора, и рецепт другого мода 1.20.1: в 1.7.10 второго рецепта не было
	 * (TASKS.md, «Вопросы к владельцу»)
	 */
	private val knownConflicts = mapOf(
		ResourceLocation(MODID, "livingrock1_wall") to setOf(ResourceLocation("botania", "livingrock_bricks_wall")),
	)

	/** Сетка верстака 3×3 без меню; [stacks] — слоты по строкам */
	private fun grid(vararg stacks: ItemStack?): CraftingContainer {
		val menu = object: AbstractContainerMenu(null, -1) {
			override fun quickMoveStack(player: Player, index: Int): ItemStack = ItemStack.EMPTY
			override fun stillValid(player: Player) = true
		}
		return TransientCraftingContainer(menu, 3, 3).apply { stacks.forEachIndexed { i, stack -> setItem(i, stack ?: ItemStack.EMPTY) } }
	}

	private fun recipes(helper: GameTestHelper) = helper.level.recipeManager.recipes.filter { it.id.namespace == MODID }

	private fun craft(helper: GameTestHelper, grid: CraftingContainer): ItemStack =
		helper.level.recipeManager.getRecipeFor(RecipeType.CRAFTING, grid, helper.level).map { it.assemble(grid, helper.level.registryAccess()) }.orElse(ItemStack.EMPTY)

	private fun assertCraft(helper: GameTestHelper, grid: CraftingContainer, result: ItemStack, what: String) {
		val crafted = craft(helper, grid)
		helper.assertTrue(ItemStack.isSameItemSameTags(crafted, result) && crafted.count == result.count, "$what: $crafted, expected $result")
	}

	/**
	 * Каждый JSON рецепта мода загружен (рецепт с ошибкой игра пропускает, только пишет в лог); у каждого ингредиента
	 * есть предметы: пустой тег Forge показывает барьером
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun recipesLoaded(helper: GameTestHelper) {
		val files = helper.level.server.resourceManager.listResources("recipes") { it.namespace == MODID && it.path.endsWith(".json") }
		val recipes = recipes(helper)
		helper.assertTrue(files.size == recipes.size && recipes.size >= 137, "recipe files: ${files.size}, loaded: ${recipes.size}")
		val problems = ArrayList<String>()
		for (recipe in recipes) {
			if (recipe is CustomRecipe) continue
			for (ingredient in recipe.ingredients) {
				if (ingredient.isEmpty) continue
				val items = ingredient.items
				if (items.isEmpty() || items.any { it.`is`(Items.BARRIER) }) problems += "${recipe.id}: ${ingredient.toJson()}"
			}
		}
		helper.assertTrue(problems.isEmpty(), "ingredients without items: $problems")
		helper.succeed()
	}

	/**
	 * Раскладку каждого рецепта верстака мода (первые предметы его ингредиентов) занимает он один: в 1.20.1 из двух
	 * подходящих рецептов верстак берёт любой. Известные пересечения с рецептами Botania 1.20.1 — [knownConflicts]
	 */
	@JvmStatic
	@GameTest(template = "empty")
	fun recipesDoNotOverlap(helper: GameTestHelper) {
		val problems = ArrayList<String>()
		for (recipe in recipes(helper)) {
			if (recipe.type != RecipeType.CRAFTING || recipe is CustomRecipe) continue
			val grid = grid()
			if (recipe is ShapedRecipe) {
				for (y in 0 until recipe.height) for (x in 0 until recipe.width)
					grid.setItem(x + y * 3, recipe.ingredients[x + y * recipe.width].items.firstOrNull()?.copy() ?: ItemStack.EMPTY)
			} else {
				recipe.ingredients.forEachIndexed { i, ingredient -> grid.setItem(i, ingredient.items.first().copy()) }
			}
			val matches = helper.level.recipeManager.getRecipesFor(RecipeType.CRAFTING, grid, helper.level).map { it.id }.toSet()
			if (recipe.id !in matches) problems += "${recipe.id} does not match its own layout"
			val others = matches - recipe.id
			if (others != (knownConflicts[recipe.id] ?: emptySet<ResourceLocation>())) problems += "${recipe.id} overlaps $others"
		}
		helper.assertTrue(problems.isEmpty(), problems.joinToString("; "))
		helper.succeed()
	}

	/** Выборочные рецепты верстака — числа и ингредиенты из `AlfheimRecipes` */
	@JvmStatic
	@GameTest(template = "empty")
	fun craftingRecipes(helper: GameTestHelper) {
		val sand = ItemStack(AlfheimBlocks.elvenSand)
		val sandstone = ItemStack(AlfheimFluffBlocks.elvenSandstone[0])
		// 4 эльфийских песка → эльфийский песчаник; квадрат 2×2 — в любом углу сетки
		assertCraft(helper, grid(sand, sand, null, sand, sand), sandstone, "elven sandstone")
		assertCraft(helper, grid(null, null, null, null, sand, sand, null, sand, sand), sandstone, "elven sandstone in the corner")
		// лестница — и зеркальная, как в 1.7.10
		val stairs = ItemStack(AlfheimFluffBlocks.elvenSandstoneStairs[0], 4)
		assertCraft(helper, grid(sandstone, null, null, sandstone, sandstone, null, sandstone, sandstone, sandstone), stairs, "stairs")
		assertCraft(helper, grid(null, null, sandstone, null, sandstone, sandstone, sandstone, sandstone, sandstone), stairs, "mirrored stairs")
		// самородки ↔ слиток, слитки ↔ блок
		val nugget = AlfheimItems.elvenResource[ElvoriumNugget.I]
		assertCraft(helper, grid(*Array(9) { ItemStack(nugget) }), ElvoriumIngot.stack, "ingot from nuggets")
		assertCraft(helper, grid(null, ElvoriumIngot.stack), ElvoriumNugget.stack(9), "nuggets from an ingot")
		assertCraft(helper, grid(*Array(9) { ItemStack(BotaniaItems.gaiaIngot) }), ItemStack(AlfheimBlocks.alfStorage[4]), "gaia ingot block")
		assertCraft(helper, grid(ItemStack(AlfheimBlocks.alfStorage[4])), ItemStack(BotaniaItems.gaiaIngot, 9), "gaia ingots from the block")
		// живой камень и любой уголь (coal: уголь и древесный уголь) → тёмный живой камень
		val dark = ItemStack(AlfheimFluffBlocks.livingrockDark[0])
		assertCraft(helper, grid(ItemStack(BotaniaBlocks.livingrock), ItemStack(Items.COAL)), dark, "dark livingrock with coal")
		assertCraft(helper, grid(ItemStack(Items.CHARCOAL), null, null, null, null, ItemStack(BotaniaBlocks.livingrock)), dark, "dark livingrock with charcoal")
		assertCraft(helper, grid(ItemStack(BotaniaBlocks.livingrockBrick), ItemStack(Items.COAL)), ItemStack(AlfheimFluffBlocks.livingrockDark[1]), "dark livingrock bricks")
		// камень святилища: живой камень вокруг красителя (Ore Dictionary dyeWhite → forge:dyes/white)
		val livingrock = ItemStack(BotaniaBlocks.livingrock)
		assertCraft(helper, grid(livingrock, livingrock, livingrock, livingrock, ItemStack(Items.WHITE_DYE), livingrock, livingrock, livingrock, livingrock), ItemStack(AlfheimFluffBlocks.shrineRock[0], 8), "shrine rock")
		// доски гномов: доски тёмного дуба и мана-порошок
		val planks = ItemStack(Items.DARK_OAK_PLANKS)
		assertCraft(helper, grid(null, planks, null, planks, ItemStack(BotaniaItems.manaPowder), planks, null, planks, null), ItemStack(AlfheimFluffBlocks.dwarfPlanks, 4), "dwarf planks")
		// «Золото Рейна»: магическая ткань с любым износом (WILDCARD_VALUE)
		val cloth = ItemStack(BotaniaItems.spellCloth).apply { damageValue = 10 }
		val essence = ItemStack(BotaniaItems.lifeEssence)
		assertCraft(helper, grid(essence, cloth, essence, cloth, ItemStack(Items.GOLD_INGOT), cloth, essence, cloth, essence), DasRheingold.stack, "Das Rheingold with a worn spell cloth")
		helper.succeed()
	}

	/** Брызгающее зелье: склянка с варевом и порох → зелье с тем же варевом (`RecipeThrowablePotion`) */
	@JvmStatic
	@GameTest(template = "empty")
	fun throwablePotionRecipe(helper: GameTestHelper) {
		val vial = ItemStack(BotaniaItems.brewVial).also { BaseBrewItem.setBrew(it, BotaniaBrews.healing) }
		val result = craft(helper, grid(vial, null, null, null, ItemStack(Items.GUNPOWDER)))
		val potion = AlfheimItems.splashPotion as ItemSplashPotion
		helper.assertTrue(result.`is`(potion) && potion.getBrew(result) === BotaniaBrews.healing, "splash potion of healing: $result")
		val recipe = helper.level.recipeManager.byKey(ResourceLocation(MODID, "throwpotion")).orElse(null) as? CustomRecipe
		helper.assertTrue(recipe != null, "alfheim:throwpotion is a special recipe")
		// лишний предмет ломает рецепт; склянка с запасным варевом даёт пустой результат, как null в 1.7.10
		helper.assertTrue(!recipe!!.matches(grid(vial, ItemStack(Items.GUNPOWDER), ItemStack(Items.DIRT)), helper.level), "an extra item breaks the recipe")
		val fallback = ItemStack(BotaniaItems.brewVial).also { BaseBrewItem.setBrew(it, BotaniaBrews.fallbackBrew) }
		helper.assertTrue(recipe.assemble(grid(fallback, ItemStack(Items.GUNPOWDER)), helper.level.registryAccess()).isEmpty, "no splash potion of the fallback brew")
		helper.succeed()
	}

	/** Печь: руда, песок, песчаник — результат и опыт из `registerSmeltingRecipes` */
	@JvmStatic
	@GameTest(template = "empty")
	fun smeltingRecipes(helper: GameTestHelper) {
		fun smelt(stack: ItemStack) = helper.level.recipeManager.getRecipeFor(RecipeType.SMELTING, SimpleContainer(stack), helper.level).orElse(null)
		val expected = listOf(
			ItemStack(AlfheimBlocks.elvenOre[0]) to (ItemStack(BotaniaItems.dragonstone) to 1f),
			ItemStack(AlfheimBlocks.elvenOre[1]) to (ItemStack(BotaniaItems.elementium) to 1f),
			ItemStack(AlfheimBlocks.elvenOre[2]) to (ItemStack(BotaniaItems.elfQuartz) to 1f),
			ItemStack(AlfheimBlocks.elvenOre[3]) to (ItemStack(Items.GOLD_INGOT) to 1f),
			ItemStack(AlfheimBlocks.elvenOre[4]) to (IffesalDust.stack to 1f),
			ItemStack(AlfheimBlocks.elvenOre[5]) to (ItemStack(Items.LAPIS_LAZULI) to 0.2f),
			ItemStack(AlfheimBlocks.elvenSand) to (ItemStack(BotaniaBlocks.elfGlass) to 1f),
			ItemStack(AlfheimFluffBlocks.elvenSandstone[1]) to (ItemStack(AlfheimFluffBlocks.elvenSandstone[4]) to 1f),
		)
		for ((input, out) in expected) {
			val recipe = smelt(input)
			helper.assertTrue(recipe != null, "no smelting for $input")
			val result = recipe.getResultItem(helper.level.registryAccess())
			helper.assertTrue(ItemStack.isSameItemSameTags(result, out.first) && result.count == out.first.count, "$input → $result, expected ${out.first}")
			helper.assertTrue(recipe.experience == out.second && recipe.cookingTime == 200, "$input: ${recipe.experience} xp, ${recipe.cookingTime} ticks")
		}
		helper.succeed()
	}
}
