package alfheim.port.data

import alexsocol.asjlib.extendables.block.BlockModMeta
import alfheim.api.ModInfo.MODID
import alfheim.common.block.*
import alfheim.common.block.base.BlockMod
import alfheim.port.legacy.*
import alfheim.port.registry.LegacyRegistration
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraftforge.client.model.generators.*
import net.minecraftforge.common.data.ExistingFileHelper

/**
 * Модели и состояния блоков автора: иконки 1.7.10 (`registerBlockIcons` / `getIcon`) → модели 1.20.1
 * (MAPPING.md, «Блоки и предметы»). Модель предмета-блока — модель блока, как рисовал предмет 1.7.10.
 *
 * Текстура по умолчанию — та, что регистрировал базовый класс блока ([texture]). Блок со своим `getIcon` получает
 * модель здесь же, по своему `getIcon` (он закомментирован в классе блока рядом). Проход рендера 1 —
 * `render_type` translucent; не непрозрачный куб в проходе 0 — cutout: 1.7.10 рисовал его с отсечением прозрачных
 * пикселей.
 */
class AlfheimBlockStates(output: PackOutput, files: ExistingFileHelper): BlockStateProvider(output, MODID, files) {

	override fun registerStatesAndModels() {
		for (block in LegacyRegistration.blocks.keys) when (block) {
			is BlockAlfStorage     -> alfStorage(block)
			is BlockElvenSandstone -> elvenSandstone(block)
			is BlockLivingCobble   -> livingCobble(block)
			else                   -> block(block, cubeAll(block, texture(block)))
		}
	}

	/** Блок и его предмет с одной моделью */
	private fun block(block: Block, model: ModelFile) {
		simpleBlock(block, model)
		simpleBlockItem(block, model)
	}

	private fun name(block: Block) = LegacyRegistration.blocks[block]!!.id.path

	private fun cubeAll(block: Block, texture: ResourceLocation, name: String = name(block)) = models().cubeAll(name, texture).renderType(block)

	private fun <T: ModelBuilder<T>> T.renderType(block: Block): T {
		val legacy = block as LegacyBlock
		return when {
			legacy.getRenderBlockPass() == 1 -> renderType("translucent")
			!legacy.isOpaqueCube()           -> renderType("cutout")
			else                             -> this
		}
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
	private fun elvenSandstone(block: BlockElvenSandstone) {
		val side = { name: String -> legacyTexture("$MODID:decor/ElvenSandstone$name") }
		val top = side("Top")
		val bottom = side("Bottom")
		val model = when (val meta = block.meta) {
			block.names.size     -> models().cubeAll(name(block), top)
			block.names.size + 1 -> models().cubeAll(name(block), bottom)
			0                    -> models().cubeBottomTop(name(block), side(block.names[meta]), bottom, top)
			else                 -> models().cubeColumn(name(block), side(block.names[meta]), top)
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

	companion object {

		/** Текстура блока, которую регистрировал его базовый класс в 1.7.10 */
		fun texture(block: Block): ResourceLocation = legacyTexture(when {
			block is BlockModMeta                         -> "${block.modid}:${block.folder}${block.name}${block.variant ?: ""}"
			(block as LegacyBlock).legacy.textureName != null -> block.legacy.textureName!!
			block is BlockMod                             -> MODID + ":" + block.legacy.unlocalizedName.removePrefix("tile.")
			else                                          -> throw IllegalStateException("No 1.7.10 texture rule for ${block.javaClass.name}")
		})

		/** Имя иконки 1.7.10 `modid:путь` → спрайт 1.20.1 `modid:blocks/путь` (MAPPING.md, «Ресурсы») */
		fun legacyTexture(icon: String): ResourceLocation {
			val modid = icon.substringBefore(':', "minecraft")
			return ResourceLocation(modid, "blocks/" + legacyPath(icon.substringAfter(':')))
		}
	}
}
