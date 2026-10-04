package alfheim.port.legacy.botania

import alfheim.port.legacy.*
import net.minecraft.world.item.*
import net.minecraft.world.level.block.Block
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.IEventBus
import vazkii.botania.api.BotaniaRegistries
import vazkii.botania.common.block.BotaniaBlocks

/*
 * Блоки Botania r1.8-249 (1.7.10), которых нет в Botania 1.20.1, а Альфхейму они нужны: из черепицы
 * (`ModBlocks.customBrick`, вариант 3) автор делает свою цветную черепицу (`AlfheimFluffBlocks.roofTile`). Порт
 * возвращает такой блок, каким он был в Botania 1.7.10 (решение автора, TASKS.md, журнал решений): свойства, имя,
 * рецепт и место во вкладке Botania — из кода Botania r1.8-249, текстура — её же (автор Vazkii, Botania License;
 * README.md). Блок регистрируется под именем Botania 1.7.10 (`Botania:customBrick`, вариант 3), id — по правилу порта
 * (SPEC, Р-5): `alfheim:custom_brick3`.
 */

/** `vazkii.botania.common.item.block.ItemBlockWithMetadataAndName`: ключ перевода — `tile.botania:`, имя и номер варианта */
class ItemBlockWithMetadataAndName(block: Block): BlockItem(block, Item.Properties())

/** `vazkii.botania.common.block.decor.BlockCustomBrick`, вариант [meta]: камень, твёрдость 2, взрывоустойчивость 5 */
class BlockCustomBrick(val meta: Int): Block1710(Material.rock) {

	override val variant get() = meta

	init {
		setHardness(2.0f)
		setResistance(5.0f)
		setStepSound(soundTypeStone)
		setBlockName("customBrick")
		// IconHelper.forBlock — текстура botania:customBrick<номер>; в порту она лежит в alfheim (README.md)
		setBlockTextureName("alfheim:botania/customBrick$meta")
	}

	override fun setBlockName(name: String): Block {
		GameRegistry.registerBlock(this, ItemBlockWithMetadataAndName::class.java, "Botania:$name")
		return super.setBlockName(name)
	}

	// damageDropped — сам вариант: лут (alfheim.port.data.AlfheimBlockLoot); getEntry (ILexiconable) — лексикон, КТ-9
}

object BotaniaBlocks1710 {

	/** `ModBlocks.customBrick`, 3 — черепица */
	lateinit var roofTile: Block
		private set

	/** Создаёт блоки; вызывается в событии регистрации блоков (`LegacyRegistration.onBlocks`) */
	fun init() {
		roofTile = BlockCustomBrick(3)
	}

	/** Рецепт Botania r1.8-249 (`ModCraftingRecipes`, Roof Tile Recipe): 6 кирпичей → 4 черепицы */
	fun registerRecipes() {
		GameRegistry.addRecipe(ShapedOreRecipe(ItemStack(roofTile, 4),
				"BB", "BB", "BB",
				'B', "ingotBrick"))
	}

	/** Вкладка Botania 1.7.10 перечисляла варианты `customBrick` подряд: черепица (3) — перед первым азулежу (4) */
	fun register(bus: IEventBus) {
		bus.addListener(EventPriority.NORMAL, false, BuildCreativeModeTabContentsEvent::class.java) { e ->
			if (e.tabKey == BotaniaRegistries.BOTANIA_TAB_KEY)
				e.entries.putBefore(ItemStack(BotaniaBlocks.azulejo0), ItemStack(roofTile), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS)
		}
	}
}
