package alfheim.port.legacy.botania

import alfheim.port.legacy.*
import alfheim.port.registry.LegacyRegistration
import net.minecraft.core.Direction
import net.minecraft.world.item.*
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.RotatedPillarBlock.AXIS
import net.minecraft.world.level.block.state.*

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
abstract class BlockModSlab(full: Boolean, mat: Material, val name: String): Slab1710(full, mat) {

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

/**
 * `vazkii.botania.common.item.block.ItemBlockSpecialQuartz`: ключ перевода варианта — его имя из
 * [BlockSpecialQuartz.getNames] (`tile.botania:chiseledShimmerQuartz`); старый ключ читает генерация `legacy_ids.json`
 */
open class ItemBlockSpecialQuartz(block: Block): BlockItem(block, Item.Properties())

/**
 * `vazkii.botania.common.block.decor.quartz.BlockSpecialQuartz`: кварц вида [type], твёрдость 0,8, взрывоустойчивость 10;
 * поля открыты, как их открывал автор (`alfheim_at.cfg`). Вариант [meta] — отдельный блок (SPEC, Р-5): 0 — блок,
 * 1 — резной, 2 — колонна. Колонна 1.7.10 хранила поворот в metadata (2 — вдоль Y, 3 — вдоль X, 4 — вдоль Z), в 1.20.1
 * это свойство `axis`, как у колонны кварца ванилы; у блока и резного кварца его нет
 */
open class BlockSpecialQuartz(val type: String, val meta: Int): Block1710(material(meta)) {

	/** Иконки вариантов 1.7.10 (`registerBlockIcons`): по ним генерация моделей выбирает текстуры */
	var iconNames = arrayOf("block${type}Quartz0", "chiseled${type}Quartz0", "pillar${type}Quartz0", null, null)

	override val variant get() = meta

	init {
		if (meta == PILLAR) registerDefaultState(defaultBlockState().setValue(AXIS, Direction.Axis.Y))
		setHardness(0.8f)
		setResistance(10f)
		setBlockName("quartzType$type")
	}

	override fun setBlockName(name: String): Block {
		GameRegistry.registerBlock(this, ItemBlockSpecialQuartz::class.java, name)
		return super.setBlockName(name)
	}

	/** Ключи перевода вариантов 0–2 (`ItemBlockSpecialQuartz.getUnlocalizedName`) */
	open fun getNames() = arrayOf(
		"tile.botania:block${type}Quartz",
		"tile.botania:chiseled${type}Quartz",
		"tile.botania:pillar${type}Quartz",
	)

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		if (creatingPillar) builder.add(AXIS)
	}

	/** `onBlockPlaced`: колонна ложится вдоль оси стороны, на которую её ставят */
	override fun getStateForPlacement(context: BlockPlaceContext): BlockState? =
		if (meta == PILLAR) defaultBlockState().setValue(AXIS, context.clickedFace.axis) else defaultBlockState()

	@Deprecated("Deprecated in Java")
	override fun rotate(state: BlockState, rotation: Rotation): BlockState = if (meta == PILLAR) RotatedPillarBlock.rotatePillar(state, rotation) else state

	// getIcon, getRenderType (39 — колонна по metadata) и registerBlockIcons — модели генерации данных; damageDropped и
	// createStackedBlock (повёрнутая колонна роняет колонну) — лут; getSubBlocks — варианты во вкладке; getEntry — лексикон, КТ-9

	companion object {

		const val PILLAR = 2

		/**
		 * Колонна ли блок, который сейчас создаётся. Свойства состояний блока 1.20.1 задаёт конструктор `Block` — раньше,
		 * чем у наследника появляется номер варианта; блоки создаются по одному, в событии регистрации
		 */
		private var creatingPillar = false

		private fun material(meta: Int): Material {
			creatingPillar = meta == PILLAR
			return Material.rock
		}
	}
}

/**
 * `vazkii.botania.common.block.decor.quartz.BlockSpecialQuartzSlab`: плита кварца [source], имя `quartzSlab<вид>Half`
 * (двойной — `…Full`), твёрдость 0,8, взрывоустойчивость 10. Двойная и одинарная — одна плита ([BlockModSlab]); плиты
 * своих кварцев Botania выбирала сама, у прочих — плита. Выбор колёсиком, лут и шёлковое касание — одинарная плита
 * (`getPickBlock`, `getItemDropped`, `createStackedBlock`), как у плиты 1.20.1; грани — блока-источника (модель)
 */
open class BlockSpecialQuartzSlab(val source: Block, full: Boolean): BlockModSlab(full, Material.rock, "quartzSlab" + (source as BlockSpecialQuartz).type + if (full) "Full" else "Half") {

	init {
		setHardness(0.8f)
		setResistance(10f)
	}

	override fun getFullBlock(): SlabBlock = this

	override fun getSingleBlock(): SlabBlock = this

	/** Регистрация под именем Botania; имя двойной плиты 1.7.10 (`quartzSlab<вид>Full`) — её состояние `type=double` */
	override fun register() {
		GameRegistry.registerBlock(this, ItemBlockModSlab::class.java, name)
		LegacyRegistration.alias("quartzSlab${(source as BlockSpecialQuartz).type}Full", this, "type=double")
	}

	// getEntry — лексикон, КТ-9
}

/** `vazkii.botania.common.block.decor.quartz.BlockSpecialQuartzStairs`: лестница кварца [source], имя `quartzStairs<вид>` */
open class BlockSpecialQuartzStairs(source: Block): BlockModStairs(source, 0, "quartzStairs" + (source as BlockSpecialQuartz).type)
