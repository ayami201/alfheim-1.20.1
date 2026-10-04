package alfheim.port.legacy;

import net.minecraft.world.level.BlockGetter;

/**
 * Методы блока 1.7.10, которые переопределяет автор и которые 1.20.1 зовёт через порт (MAPPING.md, «Блоки и
 * предметы»): цвет блока и инструмент добычи. Их наследует {@link LegacyBlock}.
 * <p>
 * Интерфейс написан на Java, как {@code Block} 1.7.10: переопределения автора объявляют параметры кто nullable, кто
 * нет ({@code IBlockAccess} и {@code IBlockAccess?}), а Kotlin принимает оба варианта только у метода Java.
 * <p>
 * Цвет: блок в мире красит {@link #colorMultiplier}, вещь в инвентаре — {@link #getRenderColor} (у предмета-блока —
 * по его metadata, номеру варианта); {@code 0xFFFFFF} — без цвета. Клиент ({@code alfheim.port.client.
 * AlfheimBlockColors}) красит ими грани модели с {@code tintindex} — их ставит генерация моделей.
 */
public interface LegacyBlockMethods {

	/** {@code getBlockColor()} 1.7.10 — цвет блока без координат; в 1.20.1 цвет вещи берётся из {@link #getRenderColor} */
	default int getBlockColor() {
		return 0xFFFFFF;
	}

	default int getRenderColor(int meta) {
		return 0xFFFFFF;
	}

	default int colorMultiplier(BlockGetter world, int x, int y, int z) {
		return 0xFFFFFF;
	}

	/**
	 * {@code getHarvestTool(metadata)} Forge 1.7.10 — инструмент добычи из {@code setHarvestLevel}; по нему генерация
	 * данных кладёт блок в теги {@code mineable} ({@code alfheim.port.data.HarvestTags}). Номер варианта блока порта —
	 * {@link LegacyBlock#getVariant()}
	 */
	default String getHarvestTool(int metadata) {
		return ((LegacyBlock) this).getLegacy().getHarvestTool();
	}

	/** {@code isToolEffective(type, metadata)} Forge 1.7.10: инструмент — тот, что из {@link #getHarvestTool} */
	default boolean isToolEffective(String type, int metadata) {
		return type != null && type.equals(getHarvestTool(metadata));
	}
}
