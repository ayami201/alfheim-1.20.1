package alfheim.port.legacy.botania

import alfheim.port.legacy.*
import alfheim.port.registry.LegacyRegistration
import net.minecraft.world.item.*
import net.minecraft.world.level.block.*

/*
 * Базовые классы декоративных блоков Botania r1.8-249 (1.7.10), от которых автор наследует свои лестницы, плиты и
 * стены. В Botania 1.20.1 их нет: её лестницы и плиты — блоки ванилы 1.20.1. Здесь они повторены поверх базовых классов
 * порта (`Stairs1710`, `Slab1710`, `Wall1710`) с той же регистрацией и теми же именами, что в Botania r1.8-249.
 * Предмет-блок Botania (`ItemBlockMod`, `ItemBlockModSlab`) переименовывал ключ перевода: `tile.` → `tile.botania:` —
 * его читает генерация `legacy_ids.json`. Вкладка Botania (`BotaniaCreativeTab`) и `useNeighborBrightness` не
 * нужны: блоки автора перечисляет `AlfheimTab`, свет неполных блоков 1.20.1 считает сама.
 */

/** `vazkii.botania.common.item.block.ItemBlockMod` */
open class ItemBlockMod(block: Block): BlockItem(block, Item.Properties())

/** `vazkii.botania.common.item.block.ItemBlockModSlab`: вторую плиту на первую кладёт `SlabBlock` 1.20.1 */
open class ItemBlockModSlab(block: Block): BlockItem(block, Item.Properties())

/**
 * `vazkii.botania.common.block.decor.slabs.BlockModSlab`. Двойная плита (`getFullBlock`) в 1.20.1 — состояние
 * `type=double` той же плиты: роняет две плиты, как `quantityDropped` 1.7.10, при выборе колёсиком мыши — одинарная
 */
abstract class BlockModSlab(val full: Boolean, mat: Material, val name: String): Slab1710(full, mat) {

	init {
		setBlockName(name)
	}

	abstract fun getFullBlock(): SlabBlock

	abstract fun getSingleBlock(): SlabBlock

	/** Регистрация под именем Botania; имя двойной плиты 1.7.10 (`…SlabFull`) — её состояние `type=double` */
	open fun register() {
		GameRegistry.registerBlock(this, ItemBlockModSlab::class.java, name)
		LegacyRegistration.alias("${name}Full", this, "type=double")
	}

	// getEntry (ILexiconable) — LexiconData.decorativeBlocks; лексикон — КТ-9
}

/** `vazkii.botania.common.block.decor.slabs.BlockLivingSlab`: имя, материал, звук и текстура — от блока-источника */
abstract class BlockLivingSlab(full: Boolean, val source: Block, meta: Int): BlockModSlab(full, legacyProps(source).material, legacyProps(source).unlocalizedName.replace(Regex("tile."), "") + meta + "Slab" + if (full) "Full" else "") {

	init {
		setStepSound(legacyProps(source).stepSound)
	}
}

/** `vazkii.botania.common.block.decor.stairs.BlockModStairs`: регистрируется в `setBlockName` */
open class BlockModStairs(source: Block, meta: Int, name: String): Stairs1710(source, meta) {

	init {
		setBlockName(name)
	}

	override fun setBlockName(name: String): Block {
		GameRegistry.registerBlock(this, ItemBlockMod::class.java, name)
		return super.setBlockName(name)
	}

	// getEntry (ILexiconable) — LexiconData.decorativeBlocks; лексикон — КТ-9
}

/** `vazkii.botania.common.block.decor.walls.BlockModWall`: имя и текстура — от блока-источника и его варианта [meta] */
open class BlockModWall(block: Block, meta: Int): Wall1710(block) {

	init {
		setBlockName(legacyProps(block).unlocalizedName.replace(Regex("tile."), "") + meta + "Wall")
	}

	// canPlaceTorchOnTop = true: факел на стену 1.20.1 ставится и так

	override fun setBlockName(name: String): Block {
		register(name)
		return super.setBlockName(name)
	}

	open fun register(name: String) {
		GameRegistry.registerBlock(this, ItemBlockMod::class.java, name)
	}

	// getSubBlocks — один предмет, getIcon — иконка блока-источника (генерация моделей), getEntry — лексикон, КТ-9
}
