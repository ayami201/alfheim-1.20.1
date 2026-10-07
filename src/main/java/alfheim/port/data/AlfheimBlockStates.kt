package alfheim.port.data

import alexsocol.asjlib.extendables.block.*
import alfheim.api.ModInfo.MODID
import alfheim.api.lib.LibOreDict.ALT_TYPES
import alfheim.client.render.tile.RenderTileTreeBerry
import alfheim.common.block.*
import alfheim.common.block.alt.*
import alfheim.common.block.base.*
import alfheim.common.block.colored.*
import alfheim.common.block.colored.rainbow.*
import alfheim.port.legacy.*
import alfheim.port.legacy.botania.*
import alfheim.port.registry.LegacyRegistration
import net.minecraft.core.Direction
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.RotatedPillarBlock.AXIS
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import net.minecraftforge.client.model.generators.*
import net.minecraftforge.client.model.generators.loaders.ObjModelBuilder
import net.minecraftforge.common.data.ExistingFileHelper

/**
 * Модели и состояния блоков автора: иконки 1.7.10 (`registerBlockIcons` / `getIcon`) → модели 1.20.1
 * (MAPPING.md, «Блоки и предметы»). Модель предмета-блока — модель блока, как рисовал предмет 1.7.10.
 *
 * Текстура по умолчанию — та, что регистрировал базовый класс блока ([texture]). Блок со своим `getIcon` получает
 * модель здесь же, по своему `getIcon` (он закомментирован в классе блока рядом). Проход рендера 1 —
 * `render_type` translucent; не непрозрачный куб в проходе 0 — cutout: 1.7.10 рисовал его с отсечением прозрачных
 * пикселей.
 *
 * Лестница, плита и стена 1.7.10 рисовали каждую грань иконкой блока-источника для этой стороны (`getIcon(side, meta)`
 * источника): у них бок, верх и низ — как у источника ([faces]). Модели формы — шаблоны ванилы 1.20.1; у стены свои
 * шаблоны с разными текстурами граней ([wallTemplates]): в шаблоне ванилы у стены одна текстура.
 *
 * Блок, которого красил класс автора (`colorMultiplier`, `getRenderColor`; [LegacyBlock.isTinted]), — по окрашенной копии
 * шаблона ([TintedTemplates]), листва — по `block/leaves`, растение-крест — по `block/tinted_cross` ванилы.
 */
class AlfheimBlockStates(output: PackOutput, files: ExistingFileHelper): BlockStateProvider(output, MODID, files) {

	override fun registerStatesAndModels() {
		wallTemplates()

		for (block in LegacyRegistration.blocks.keys) when (block) {
			is BlockAlfStorage       -> alfStorage(block)
			is BlockElvenSandstone   -> elvenSandstone(block)
			is BlockLivingCobble     -> livingCobble(block)
			is BlockLivingMountain   -> livingMountain(block)
			is BlockYggDecor         -> yggDecor(block)
			is BlockDwarfLantern     -> dwarfLantern(block)
			is BlockColoredLamp      -> irisLamp(block)
			is BlockSadOakLeaves     -> sadOakLeaves(block)
			is BlockLeavesMod        -> leaves(block)
			is BlockColoredSapling   -> plant(block, legacyTexture(icon(block)))
			is BlockDreamSapling     -> plant(block, texture(block))
			is BlockColoredGrass     -> plant(block, legacyTexture(icon(block)))
			is BlockColoredDoubleGrass -> irisDoubleGrass(block)
			is BlockRainbowGrass       -> rainbowGrass(block)
			is BlockRainbowDoubleGrass -> rainbowDoubleGrass(block)
			is BlockRainbowDoubleFlower -> doublePlant(block, legacyTexture("$MODID:rainbowDoubleFlower"), legacyTexture("$MODID:rainbowDoubleFlowerTop"))
			is BlockRainbowMushroom  -> plant(block, legacyTexture(icon(block)))
			is BlockTreeBerry        -> treeBerry(block)
			is BlockShimmerQuartz    -> shimmerQuartz(block)
			is BlockModRotatedPillar -> pillar(block)
			is Stairs1710            -> stairs(block)
			is BlockLivingSlab       -> slab(block, block.source)
			is BlockSpecialQuartzSlab -> slab(block, block.source)
			is BlockSlabMod          -> slab(block, block.source)
			is Wall1710              -> wall(block)
			is Fence1710             -> fence(block)
			is FenceGate1710         -> fenceGate(block)
			is TrapDoor1710          -> trapDoor(block)
			is BlockPaneMeta         -> pane(block)
			else                     -> block(block, cubeAll(block, texture(block)))
		}
	}

	/** Блок и его предмет с одной моделью */
	private fun block(block: Block, model: ModelFile) {
		simpleBlock(block, model)
		simpleBlockItem(block, model)
	}

	private fun name(block: Block) = LegacyRegistration.blocks[block]!!.id.path

	/** Модель этого же генератора: файла ещё может не быть на диске */
	private fun generated(name: String) = ModelFile.UncheckedModelFile(modLoc("block/$name"))

	private fun cubeAll(block: Block, texture: ResourceLocation, name: String = name(block)) =
		(if (block.tinted) tinted(name, "cube_all").texture("all", texture) else models().cubeAll(name, texture)).renderType(block)

	/** Окрашенная копия шаблона ванилы [template] ([TintedTemplates]): её строит этот же запуск генерации */
	private fun tinted(name: String, template: String) = models().getBuilder(name).parent(generated(TintedTemplates.PREFIX + template))

	private val Block.tinted get() = (this as? LegacyBlock)?.isTinted == true

	/** Иконка блока по имени, как `IconHelper.forBlock(reg, block, suffix)` автора: `alfheim:<имя блока><suffix>` */
	private fun icon(block: Block, suffix: String = "") = MODID + ":" + (block as LegacyBlock).legacy.unlocalizedName.removePrefix("tile.") + suffix

	private fun <T: ModelBuilder<T>> T.renderType(block: Block): T {
		val legacy = block as? LegacyBlock ?: return this
		return when {
			legacy.getRenderBlockPass() == 1 -> renderType("translucent")
			!legacy.isOpaqueCube()           -> renderType("cutout")
			else                             -> this
		}
	}

	/** Бок, низ и верх блока 1.7.10 — `getIcon(side, meta)` для сторон 2–5, 0 и 1 */
	private class Faces(val side: ResourceLocation, val bottom: ResourceLocation, val top: ResourceLocation) {

		constructor(all: ResourceLocation): this(all, all, all)
	}

	/** Грани блока-источника лестницы, плиты, стены */
	private fun faces(block: Block): Faces = when (block) {
		is BlockElvenSandstone -> elvenSandstoneFaces(block)
		is BlockLivingMountain -> Faces(mountain(1))
		is LegacyBlock         -> Faces(texture(block))
		else                   -> Faces(Botania1710.side(block), Botania1710.bottom(block), Botania1710.top(block))
	}

	/**
	 * `BlockAlfStorage.registerBlockIcons`: у вариантов 1–3 текстура `New`, если включена опция newStorageTexture.
	 * Модель по умолчанию — с новой текстурой (опция включена по умолчанию); модель со старой текстурой подставляет
	 * клиент при сборке моделей, если опция выключена (`alfheim.port.client.AlfheimModels`)
	 */
	private fun alfStorage(block: BlockAlfStorage) {
		val meta = block.meta
		if (meta in 1..3) {
			cubeAll(block, legacyTexture("$MODID:alfStorage$meta"))
			block(block, cubeAll(block, legacyTexture("$MODID:alfStorageNew$meta"), "alf_storage_new$meta"))
		} else
			block(block, cubeAll(block, texture(block)))
	}

	/** `BlockElvenSandstone.getIcon`: грани по номеру варианта */
	private fun elvenSandstoneFaces(block: BlockElvenSandstone): Faces {
		val side = { name: String -> legacyTexture("$MODID:decor/ElvenSandstone$name") }
		val top = side("Top")
		val bottom = side("Bottom")
		return when (val meta = block.meta) {
			block.names.size     -> Faces(top)
			block.names.size + 1 -> Faces(bottom)
			0                    -> Faces(side(block.names[meta]), bottom, top)
			else                 -> Faces(side(block.names[meta]), top, top)
		}
	}

	private fun elvenSandstone(block: BlockElvenSandstone) {
		val faces = elvenSandstoneFaces(block)
		val model = when {
			faces.side == faces.top && faces.side == faces.bottom -> models().cubeAll(name(block), faces.side)
			faces.bottom == faces.top                             -> models().cubeColumn(name(block), faces.side, faces.top)
			else                                                  -> models().cubeBottomTop(name(block), faces.side, faces.bottom, faces.top)
		}
		block(block, model)
	}

	/**
	 * `BlockLivingCobble.getIcon`: у варианта 3 вторая текстура `3Alt` на половине координат. Её модель — отдельная,
	 * выбирает клиент по координатам (`alfheim.port.client.AlfheimModels`)
	 */
	private fun livingCobble(block: BlockLivingCobble) {
		if (block.meta == 3) cubeAll(block, legacyTexture("$MODID:LivingCobble3Alt"), "living_cobble3_alt")
		block(block, cubeAll(block, texture(block)))
	}

	/** Иконка `BlockLivingMountain` номер 1–4 */
	private fun mountain(i: Int) = legacyTexture("$MODID:decor/LivingMountain$i")

	/**
	 * `BlockLivingMountain.getIcon`: 4 иконки, на каждой грани — своя по координатам. Модель с иконкой 1 — блока и
	 * предмета (`getIcon(side, meta)`), модели `_icon2`–`_icon4` подставляет клиент по координатам
	 * (`alfheim.port.client.AlfheimModels`)
	 */
	private fun livingMountain(block: BlockLivingMountain) {
		for (i in 2..4) cubeAll(block, mountain(i), "${name(block)}_icon$i")
		block(block, cubeAll(block, mountain(1)))
	}

	/** `BlockYggDecor.getIcon`: у варианта 1 верх и низ — `Wisdomwood1Top` */
	private fun yggDecor(block: BlockYggDecor) {
		if (block.meta == 1)
			block(block, models().cubeColumn(name(block), texture(block), legacyTexture("$MODID:decor/Wisdomwood1Top")))
		else
			block(block, cubeAll(block, texture(block)))
	}

	/** `BlockDwarfLantern.getIcon`: верх и низ — `DwarfLanternTop`, бока — `DwarfLantern` */
	private fun dwarfLantern(block: BlockDwarfLantern) =
		block(block, models().cubeColumn(name(block), legacyTexture("$MODID:decor/DwarfLantern"), legacyTexture("$MODID:decor/DwarfLanternTop")))

	/**
	 * `BlockModRotatedPillar.getIcon` (рендер 31 — столб, как бревно): торцы по оси столба. Иконки —
	 * `BlockModRotatedPillar.registerBlockIcons`: `<имя блока>Side` и `<имя блока>Top`
	 */
	private fun pillar(block: BlockModRotatedPillar) {
		val (side, end) = when (block) {
			// BlockShrinePillar.registerBlockIcons
			is BlockShrinePillar -> legacyTexture("$MODID:decor/ShrinePillar") to legacyTexture("$MODID:decor/ShrinePillarTop")
			// BlockAltWood.registerBlockIcons: altOak<вид>Side и altOak<вид>Top, вид — ALT_TYPES[набор * 4 + вариант]
			is BlockAltWood      -> legacyTexture("$MODID:altOak${ALT_TYPES[block.set * 4 + block.meta]}Side") to legacyTexture("$MODID:altOak${ALT_TYPES[block.set * 4 + block.meta]}Top")
			else                 -> legacyTexture(icon(block, "Side")) to legacyTexture(icon(block, "Top"))
		}
		pillar(block, side, end)
	}

	/** Столб со свойством `axis`: бока — [side], торцы по оси — [end]; предмет — стоячий столб */
	private fun pillar(block: Block, side: ResourceLocation, end: ResourceLocation) {
		val vertical = if (block.tinted) tinted(name(block), "cube_column").texture("side", side).texture("end", end) else models().cubeColumn(name(block), side, end)
		val horizontal = if (block.tinted) tinted(name(block) + "_horizontal", "cube_column_horizontal").texture("side", side).texture("end", end) else models().cubeColumnHorizontal(name(block) + "_horizontal", side, end)
		getVariantBuilder(block)
			.partialState().with(AXIS, Direction.Axis.Y).modelForState().modelFile(vertical).addModel()
			.partialState().with(AXIS, Direction.Axis.Z).modelForState().modelFile(horizontal).rotationX(90).addModel()
			.partialState().with(AXIS, Direction.Axis.X).modelForState().modelFile(horizontal).rotationX(90).rotationY(90).addModel()
		simpleBlockItem(block, vertical)
	}

	/**
	 * `BlockSpecialQuartz.getIcon` с иконками `BlockShimmerQuartz.registerIcons` (папка decor): бока варианта —
	 * `iconNames`; верх и низ блока — его же иконка, резного — `chiseled<вид>Quartz1`, колонны (рендер 39 — по оси) —
	 * `pillar<вид>Quartz1`
	 */
	private fun shimmerQuartz(block: BlockShimmerQuartz) {
		val side = texture(block)
		when (block.meta) {
			BlockSpecialQuartz.PILLAR -> pillar(block, side, legacyTexture("$MODID:decor/pillar${block.type}Quartz1"))
			1                         -> block(block, models().cubeColumn(name(block), side, legacyTexture("$MODID:decor/chiseled${block.type}Quartz1")))
			else                      -> block(block, cubeAll(block, side))
		}
	}

	private fun stairs(block: Stairs1710) {
		val name = name(block)
		val source = block.legacySource
		val faces = faces(source)
		fun model(suffix: String, template: String, vanilla: (String) -> BlockModelBuilder) =
			(if (block.tinted) tinted(name + suffix, template).texture("side", faces.side).texture("bottom", faces.bottom).texture("top", faces.top) else vanilla(name + suffix)).renderType(source)
		val stairs = model("", "stairs") { models().stairs(it, faces.side, faces.bottom, faces.top) }
		val inner = model("_inner", "inner_stairs") { models().stairsInner(it, faces.side, faces.bottom, faces.top) }
		val outer = model("_outer", "outer_stairs") { models().stairsOuter(it, faces.side, faces.bottom, faces.top) }
		stairsBlock(block, stairs, inner, outer)
		simpleBlockItem(block, stairs)
	}

	/**
	 * Плита Botania 1.7.10 (`BlockLivingSlab`) и автора (`BlockSlabMod`): грани — блока-источника; двойная — как сам
	 * блок-источник
	 */
	private fun slab(block: Slab1710, source: Block) {
		val name = name(block)
		val faces = faces(source)

		when (block) {
			// BlockLivingCobbleSlab2.getIcon: бока — LivingCobble2Slab, у двойной тоже
			is BlockLivingCobbleSlab2 -> {
				val side = legacyTexture("$MODID:LivingCobble2Slab")
				val bottom = models().slab(name, side, faces.bottom, faces.top)
				slabBlock(block, bottom, models().slabTop(name + "_top", side, faces.bottom, faces.top), models().cubeBottomTop(name + "_double", side, faces.bottom, faces.top))
				simpleBlockItem(block, bottom)
				return
			}
			// BlockLivingMountainSlab.getIcon — иконка BlockLivingMountain по координатам: модели _icon2–_icon4
			is BlockLivingMountainSlab -> for (i in 2..4) {
				models().slab("${name}_icon$i", mountain(i), mountain(i), mountain(i))
				models().slabTop("${name}_top_icon$i", mountain(i), mountain(i), mountain(i))
			}
		}

		fun model(suffix: String, template: String, vanilla: (String) -> BlockModelBuilder) =
			(if (block.tinted) tinted(name + suffix, template).texture("side", faces.side).texture("bottom", faces.bottom).texture("top", faces.top) else vanilla(name + suffix)).renderType(source)
		val bottom = model("", "slab") { models().slab(it, faces.side, faces.bottom, faces.top) }
		val top = model("_top", "slab_top") { models().slabTop(it, faces.side, faces.bottom, faces.top) }
		slabBlock(block, bottom, top, generated(name(source)))
		simpleBlockItem(block, bottom)
	}

	/**
	 * `BlockColoredLamp.getIcon`: при силе сигнала 15 (`meta > 14`) — переливающаяся иконка `irisLampRB`, иначе —
	 * иконка блока; предмет — лампа без сигнала
	 */
	private fun irisLamp(block: BlockColoredLamp) {
		val normal = cubeAll(block, texture(block))
		val rainbow = cubeAll(block, legacyTexture(icon(block, "RB")), name(block) + "_rb")
		getVariantBuilder(block).forAllStates { ConfiguredModel.builder().modelFile(if (it.getValue(BlockColoredLamp.POWER) > 14) rainbow else normal).build() }
		simpleBlockItem(block, normal)
	}

	/**
	 * `BlockLeavesMod.registerBlockIcons` и `getIcon`: иконка по имени блока, при «быстрой» графике — `_opaque` (её модель
	 * подставляет клиент, alfheim.port.client.AlfheimModels). Модель — листва ванилы `block/leaves`: окрашенный куб,
	 * сплошным или с отсечением его рисует 1.20.1 по настройке графики (LeavesBlock). `BlockAltLeaves.registerBlockIcons`:
	 * иконка — имя блока и вид (`altLeavesDry`), у листвы мечтаний — светящийся слой `altLeavesDreamwoodGlow`
	 * (`getGlowIcon`, [glowLeaves])
	 */
	private fun leaves(block: BlockLeavesMod) {
		val type = if (block is BlockAltLeaves) ALT_TYPES[block.meta] else ""
		if (block is BlockAltLeaves && block.meta == 7) return glowLeaves(block, type, legacyTexture(icon(block, "DreamwoodGlow")))
		models().withExistingParent(name(block) + "_opaque", mcLoc("block/leaves")).texture("all", legacyTexture(icon(block, type + "_opaque")))
		block(block, models().withExistingParent(name(block), mcLoc("block/leaves")).texture("all", legacyTexture(icon(block, type))))
	}

	/**
	 * Листва со светящимся слоем (`RenderGlowingLayerBlock` ASJCore): поверх граней листвы — те же грани с иконкой [glow],
	 * белые, со светом блока 15 (яркость 240) и без затенения граней. Прозрачные точки слоя 1.7.10 отсекал при любой
	 * графике, поэтому модель — с отсечением прозрачного (`cutout_mipped`, как листва при «красивой» графике) и при
	 * «быстрой» графике: непрозрачная текстура `_opaque` выглядит так же, как сплошная
	 */
	private fun glowLeaves(block: BlockLeavesMod, type: String, glow: ResourceLocation) {
		fun model(name: String, leaves: ResourceLocation): BlockModelBuilder {
			val model = models().withExistingParent(name, mcLoc("block/block")).texture("particle", leaves).texture("all", leaves).texture("glow", glow).renderType("cutout_mipped")
			model.element().allFaces { side, face -> face.texture("#all").cullface(side).tintindex(0) }.end()
			model.element().shade(false).ao(false).emissivity(15, 0).allFaces { side, face -> face.texture("#glow").cullface(side) }.end()
			return model
		}
		model(name(block) + "_opaque", legacyTexture(icon(block, type + "_opaque")))
		block(block, model(name(block), legacyTexture(icon(block, type))))
	}

	/**
	 * `BlockSadOakLeaves.getIcon`: иконка дубовой листвы ванилы (`Blocks.leaves.getIcon(side, 0)`) — модель листвы ванилы
	 * с текстурой дубовой листвы 1.20.1. Отдельной текстуры для «быстрой» графики у неё нет: сплошной её рисует 1.20.1,
	 * поэтому модель `_opaque` — с той же текстурой
	 */
	private fun sadOakLeaves(block: BlockSadOakLeaves) {
		models().withExistingParent(name(block) + "_opaque", mcLoc("block/leaves")).texture("all", mcLoc("block/oak_leaves"))
		block(block, models().withExistingParent(name(block), mcLoc("block/leaves")).texture("all", mcLoc("block/oak_leaves")))
	}

	/** Растение 1.7.10 с рендером 1 — крест (окрашенный у блока, которого красит класс автора), с отсечением прозрачного */
	private fun cross(block: Block, texture: ResourceLocation, name: String = name(block)) =
		models().withExistingParent(name, mcLoc(if (block.tinted) "block/tinted_cross" else "block/cross")).texture("cross", texture).renderType("cutout")

	/** Растение-крест: предмет 1.7.10 рисовал его иконку плоской (`item/generated`) */
	private fun plant(block: Block, texture: ResourceLocation) {
		simpleBlock(block, cross(block, texture))
		itemModels().withExistingParent(name(block), mcLoc("item/generated")).texture("layer0", texture)
	}

	/**
	 * `BlockTreeBerry.getIcon`: иконка зрелости `TreeBerry<вид><зрелость>`, рендер 1 — крест; предмет — иконка зрелости 0.
	 * Ягоды видов 2–4 без опции minimalGraphics рисует OBJ-моделью `RenderTileTreeBerry`: модель загрузчика OBJ Forge с
	 * той же текстурой (материал `model/port/tree_berry.mtl`), координаты текстуры перевёрнуты по вертикали, как у
	 * загрузчика OBJ Forge 1.7.10
	 */
	private fun treeBerry(block: BlockTreeBerry) {
		val textures = Array(3) { legacyTexture(icon(block, it.toString())) }
		val states = getVariantBuilder(block)
		for (age in 0..2)
			states.partialState().with(BlockTreeBerry.AGE, age).modelForState().modelFile(cross(block, textures[age], "tree_berry${block.type}$age")).addModel()
		itemModels().withExistingParent(name(block), mcLoc("item/generated")).texture("layer0", textures[0])
		if (block.type !in RenderTileTreeBerry.hasModels) return
		for (age in 0..2)
			models().getBuilder("tree_berry${block.type}${age}_obj").customLoader { builder, helper -> ObjModelBuilder.begin(builder, helper) }
				.modelLocation(modLoc("model/tree_berry${block.type}$age.obj")).flipV(true).overrideMaterialLibrary(modLoc("model/port/tree_berry.mtl")).end()
				.texture("texture", textures[age]).texture("particle", textures[age])
	}

	/**
	 * `BlockColoredDoubleGrass.registerBlockIcons`: низ — `irisDoubleGrass`, верх — `irisDoubleGrassTop`
	 * (`func_149888_a`); предмет — верх (`ItemIridescentTallGrassMod0.getIcon`)
	 */
	private fun irisDoubleGrass(block: BlockColoredDoubleGrass) =
		doublePlant(block, legacyTexture("$MODID:irisDoubleGrass"), legacyTexture("$MODID:irisDoubleGrassTop"))

	/** Двойное растение 1.7.10: у половин свои кресты (`func_149888_a`), предмет — верхняя половина */
	private fun doublePlant(block: Block, bottom: ResourceLocation, top: ResourceLocation) {
		val lower = cross(block, bottom, name(block) + "_bottom")
		val upper = cross(block, top, name(block) + "_top")
		getVariantBuilder(block)
			.partialState().with(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER).modelForState().modelFile(lower).addModel()
			.partialState().with(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER).modelForState().modelFile(upper).addModel()
		itemModels().withExistingParent(name(block), mcLoc("item/generated")).texture("layer0", top)
	}

	/**
	 * `BlockRainbowGrass.getIcon` по варианту: трава — `rainbowGrass`, авроровая трава — иконка травы ириса, цветок —
	 * `rainbowGrassFlower`, мерцающий цветок — `rainbowGrassFlowerGlimmer` (анимации — .mcmeta). Закопанные лепестки —
	 * иконка закопанных лепестков Botania 1.7.10, а она полностью прозрачная: у модели нет граней, частицы поломки и
	 * предмет прозрачные (`blocks/port/transparent`)
	 */
	private fun rainbowGrass(block: BlockRainbowGrass) {
		if (block.meta == BlockRainbowGrass.BURIED) {
			val transparent = modLoc("blocks/port/transparent")
			simpleBlock(block, models().getBuilder(name(block)).texture("particle", transparent))
			itemModels().withExistingParent(name(block), mcLoc("item/generated")).texture("layer0", transparent)
			return
		}
		plant(block, legacyTexture(when (block.meta) {
			BlockRainbowGrass.AURORA  -> "$MODID:irisGrass"
			BlockRainbowGrass.FLOWER  -> "$MODID:rainbowGrassFlower"
			BlockRainbowGrass.GLIMMER -> "$MODID:rainbowGrassFlowerGlimmer"
			else                      -> "$MODID:rainbowGrass"
		}))
	}

	/** `BlockRainbowDoubleGrass.getBottomIcon` / `getTopIcon`: радужная — `rainbowDoubleGrass`, авроровая — иконки двойной травы ириса */
	private fun rainbowDoubleGrass(block: BlockRainbowDoubleGrass) {
		val name = if (block.meta == block.AURORA) "irisDoubleGrass" else "rainbowDoubleGrass"
		doublePlant(block, legacyTexture("$MODID:$name"), legacyTexture("$MODID:${name}Top"))
	}

	/** Шаблоны стены ванилы (`template_wall_post`, `_side`, `_side_tall`, `wall_inventory`) с текстурами side, top, bottom */
	private fun wallTemplates() {
		models().getBuilder(WALL_POST).texture("particle", "#side")
			.element().from(4f, 0f, 4f).to(12f, 16f, 12f)
			.face(Direction.DOWN).texture("#bottom").cullface(Direction.DOWN).end()
			.face(Direction.UP).texture("#top").cullface(Direction.UP).end()
			.face(Direction.NORTH).texture("#side").end()
			.face(Direction.SOUTH).texture("#side").end()
			.face(Direction.WEST).texture("#side").end()
			.face(Direction.EAST).texture("#side").end()
			.end()

		for ((template, height) in listOf(WALL_SIDE to 14f, WALL_SIDE_TALL to 16f))
			models().getBuilder(template).texture("particle", "#side")
				.element().from(5f, 0f, 0f).to(11f, height, 8f)
				.face(Direction.DOWN).texture("#bottom").cullface(Direction.DOWN).end()
				.face(Direction.UP).texture("#top").apply { if (height == 16f) cullface(Direction.UP) }.end()
				.face(Direction.NORTH).texture("#side").cullface(Direction.NORTH).end()
				.face(Direction.WEST).texture("#side").end()
				.face(Direction.EAST).texture("#side").end()
				.end()

		models().getBuilder(WALL_INVENTORY).parent(models().getExistingFile(mcLoc("block/block")))
			.transforms()
			.transform(ItemDisplayContext.GUI).rotation(30f, 135f, 0f).translation(0f, 0f, 0f).scale(0.625f).end()
			.transform(ItemDisplayContext.FIXED).rotation(0f, 90f, 0f).translation(0f, 0f, 0f).scale(0.5f).end()
			.end()
			.texture("particle", "#side")
			.element().from(4f, 0f, 4f).to(12f, 16f, 12f)
			.face(Direction.DOWN).uvs(4f, 4f, 12f, 12f).texture("#bottom").cullface(Direction.DOWN).end()
			.face(Direction.UP).uvs(4f, 4f, 12f, 12f).texture("#top").end()
			.face(Direction.NORTH).uvs(4f, 0f, 12f, 16f).texture("#side").end()
			.face(Direction.SOUTH).uvs(4f, 0f, 12f, 16f).texture("#side").end()
			.face(Direction.WEST).uvs(4f, 0f, 12f, 16f).texture("#side").end()
			.face(Direction.EAST).uvs(4f, 0f, 12f, 16f).texture("#side").end()
			.end()
			.element().from(5f, 0f, 0f).to(11f, 13f, 16f)
			.face(Direction.DOWN).uvs(5f, 0f, 11f, 16f).texture("#bottom").cullface(Direction.DOWN).end()
			.face(Direction.UP).uvs(5f, 0f, 11f, 16f).texture("#top").end()
			.face(Direction.NORTH).uvs(5f, 3f, 11f, 16f).texture("#side").cullface(Direction.NORTH).end()
			.face(Direction.SOUTH).uvs(5f, 3f, 11f, 16f).texture("#side").cullface(Direction.SOUTH).end()
			.face(Direction.WEST).uvs(0f, 3f, 16f, 16f).texture("#side").end()
			.face(Direction.EAST).uvs(0f, 3f, 16f, 16f).texture("#side").end()
			.end()
	}

	/** `BlockModWall.getIcon` Botania 1.7.10 — иконка блока-источника для каждой стороны */
	private fun wall(block: Wall1710) {
		val name = name(block)
		val source = block.legacySource
		val faces = faces(source)
		fun model(suffix: String, template: String) = models().getBuilder(name + suffix).parent(generated(template))
			.texture("side", faces.side).texture("top", faces.top).texture("bottom", faces.bottom).renderType(source)
		wallBlock(block, model("_post", WALL_POST), model("_side", WALL_SIDE), model("_side_tall", WALL_SIDE_TALL))
		simpleBlockItem(block, model("_inventory", WALL_INVENTORY))
	}

	/** `BlockFence` 1.7.10: одна иконка по имени для всех граней; иконки Botania — текстуры Botania 1.20.1 */
	private fun fence(block: Fence1710) {
		val icon = block.legacy.textureName!!
		val texture = Botania1710.icon(icon) ?: legacyTexture(icon)
		fenceBlock(block, texture)
		simpleBlockItem(block, models().fenceInventory(name(block) + "_inventory", texture))
	}

	/** `BlockModFenceGate.getIcon` — иконка блока-источника */
	private fun fenceGate(block: FenceGate1710) {
		val name = name(block)
		val src = (block as BlockModFenceGate).src
		val texture = if (src is LegacyBlock) texture(src) else Botania1710.side(src)
		val gate = models().fenceGate(name, texture)
		fenceGateBlock(block, gate, models().fenceGateOpen(name + "_open", texture), models().fenceGateWall(name + "_wall", texture), models().fenceGateWallOpen(name + "_wall_open", texture))
		simpleBlockItem(block, gate)
	}

	/**
	 * `BlockModTrapDoor.registerBlockIcons` — иконка по имени блока; у люка гномов `registerBlockIcons` переопределён в
	 * `AlfheimFluffBlocks`: иконка из папки `decor`. Текстура не поворачивается вместе с люком, как в 1.7.10
	 */
	private fun trapDoor(block: TrapDoor1710) {
		val name = name(block)
		val texture = if (block === AlfheimFluffBlocks.dwarfTrapDoor) legacyTexture("$MODID:decor/DwarfTrapDoor") else legacyTexture(MODID + ":" + block.legacy.unlocalizedName.removePrefix("tile."))
		val bottom = models().trapdoorBottom(name + "_bottom", texture).renderType(block)
		trapdoorBlock(block, bottom, models().trapdoorTop(name + "_top", texture).renderType(block), models().trapdoorOpen(name + "_open", texture).renderType(block), false)
		simpleBlockItem(block, bottom)
	}

	/**
	 * `BlockPaneMeta.registerBlockIcons`: плоскость — `$folder$texName$meta`, торец — `…Top`. Рендер 1.7.10
	 * (`RenderBlockShrinePanel`) — рендер стеклянной панели ванилы, предмет — плоская иконка плоскости
	 * (`shouldRender3DInInventory = false`)
	 */
	private fun pane(block: BlockPaneMeta) {
		val name = name(block)
		val pane = legacyTexture("$MODID:${block.folder}${block.texName}${block.meta}")
		val edge = legacyTexture("$MODID:${block.folder}${block.texName}${block.meta}Top")
		paneBlock(block,
			models().panePost(name + "_post", pane, edge).renderType(block),
			models().paneSide(name + "_side", pane, edge).renderType(block),
			models().paneSideAlt(name + "_side_alt", pane, edge).renderType(block),
			models().paneNoSide(name + "_noside", pane).renderType(block),
			models().paneNoSideAlt(name + "_noside_alt", pane).renderType(block))
		itemModels().withExistingParent(name, mcLoc("item/generated")).texture("layer0", pane).renderType(block)
	}

	companion object {

		private const val WALL_POST = "template_wall_post_faces"
		private const val WALL_SIDE = "template_wall_side_faces"
		private const val WALL_SIDE_TALL = "template_wall_side_tall_faces"
		private const val WALL_INVENTORY = "wall_inventory_faces"

		/** Текстура блока, которую регистрировал его базовый класс в 1.7.10 */
		fun texture(block: Block): ResourceLocation = legacyTexture(when {
			block is BlockModMeta                             -> "${block.modid}:${block.folder}${block.name}${block.variant ?: ""}"
			// BlockShimmerQuartz.registerIcons: иконка варианта из iconNames, папка decor
			block is BlockShimmerQuartz                       -> "$MODID:decor/" + block.iconNames[block.meta]!!.replace("decor/", "")
			// BlockAltPlanks.registerBlockIcons: иконка — имя блока и вид (altPlanksDry)
			block is BlockAltPlanks                           -> "$MODID:altPlanks${ALT_TYPES[block.meta]}"
			(block as LegacyBlock).legacy.textureName != null -> block.legacy.textureName!!
			block is BlockMod                                 -> MODID + ":" + block.legacy.unlocalizedName.removePrefix("tile.")
			else                                              -> throw IllegalStateException("No 1.7.10 texture rule for ${block.javaClass.name}")
		})

		/** Имя иконки 1.7.10 `modid:путь` → спрайт 1.20.1 `modid:blocks/путь` (MAPPING.md, «Ресурсы») */
		fun legacyTexture(icon: String): ResourceLocation {
			val modid = icon.substringBefore(':', "minecraft")
			return ResourceLocation(modid, "blocks/" + legacyPath(icon.substringAfter(':')))
		}
	}
}
