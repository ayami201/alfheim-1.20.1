package alfheim.port.legacy;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Методы блока 1.7.10, которые переопределяет автор и которые 1.20.1 зовёт через порт (MAPPING.md, «Блоки и
 * предметы»): цвет блока, инструмент добычи, тики, установка и снятие блока, взрыв, сигнал красного камня, вечный огонь,
 * замена блока.
 * Их наследует {@link LegacyBlock}. Методы 1.20.1, из которых они вызываются, переопределяют базовые классы порта:
 * {@code Block1710}, {@code Slab1710}, {@code Stairs1710}, {@code Leaves1710}, {@code Bush1710}.
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

	/**
	 * {@code updateTick} 1.7.10 — тик блока на сервере: запланированный ({@code tick} 1.20.1) и случайный
	 * ({@code randomTick}), если блоку нужны случайные тики ({@code setTickRandomly})
	 */
	default void updateTick(Level world, int x, int y, int z, RandomSource random) {
	}

	/**
	 * {@code onBlockAdded} 1.7.10: блок поставлен или сменил metadata. 1.20.1 зовёт {@code onPlace} так же — на сервере, при
	 * каждой смене состояния в точке
	 */
	default void onBlockAdded(Level world, int x, int y, int z) {
	}

	/**
	 * {@code breakBlock} 1.7.10: блок убран или сменил metadata ({@code onRemove} 1.20.1, на сервере). {@code meta} — номер
	 * варианта блока (SPEC, Р-5); состояние, которое в 1.7.10 тоже было metadata (поворот, бит опадания), сюда не входит
	 */
	default void breakBlock(Level world, int x, int y, int z, Block block, int meta) {
	}

	/**
	 * {@code onBlockExploded} Forge 1.7.10: взрыв убирает блок, после того как выпал лут взрыва. Переопределение без
	 * вызова этого метода оставляет блок на месте
	 */
	default void onBlockExploded(Level world, int x, int y, int z, Explosion explosion) {
		BlockPos pos = new BlockPos(x, y, z);
		world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
		((Block) this).wasExploded(world, pos, explosion);
	}

	/** {@code canProvidePower} 1.7.10 — блок даёт сигнал красного камня ({@code isSignalSource} 1.20.1) */
	default boolean canProvidePower() {
		return false;
	}

	/**
	 * {@code isProvidingWeakPower} 1.7.10 — слабый сигнал блока ({@code getSignal} 1.20.1); номер стороны {@code side}
	 * 1.7.10 — тот же, что у {@code Direction} 1.20.1 ({@code get3DDataValue})
	 */
	default int isProvidingWeakPower(BlockGetter world, int x, int y, int z, int side) {
		return 0;
	}

	/**
	 * {@code isFireSource} Forge 1.7.10: огонь на блоке горит, не угасая. У ванилы 1.7.10 это адский камень (и бедрок в
	 * Энде) — в 1.20.1 тег измерения, его порт проверяет тоже
	 */
	default boolean isFireSource(Level world, int x, int y, int z, ForgeDirection side) {
		return false;
	}

	/**
	 * {@code isReplaceable} Forge 1.7.10: блок в точке можно заменить — поставить в его место другой блок, вырастить
	 * сквозь него дерево; по умолчанию — заменяемый материал ({@code canBeReplaced} состояния 1.20.1)
	 */
	default boolean isReplaceable(BlockGetter world, int x, int y, int z) {
		return world.getBlockState(new BlockPos(x, y, z)).canBeReplaced();
	}
}
