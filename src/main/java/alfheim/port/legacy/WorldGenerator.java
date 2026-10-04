package alfheim.port.legacy;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * {@code net.minecraft.world.gen.feature.WorldGenerator} 1.7.10 (MAPPING.md, «Растения»): постройка по координатам —
 * дерево из саженца. Генератор автора ставит блоки так же, как в 1.7.10: с оповещением соседей (флаги 3, если
 * генератор создан с {@code notify}) или без (флаги 2).
 * <p>
 * Класс написан на Java, как {@code WorldGenerator} 1.7.10: переопределения {@link #generate} у автора объявляют
 * параметры кто nullable, кто нет.
 */
public abstract class WorldGenerator {

	private final boolean doBlockNotify;

	public WorldGenerator() {
		this(false);
	}

	public WorldGenerator(boolean notify) {
		doBlockNotify = notify;
	}

	public abstract boolean generate(Level world, RandomSource random, int x, int y, int z);

	/**
	 * {@code setBlockAndNotifyAdequately} 1.7.10. Вариант metadata в 1.20.1 — отдельный блок (SPEC, Р-5): генератор
	 * ставит сам блок варианта, metadata здесь — 0
	 */
	protected void setBlockAndNotifyAdequately(Level world, int x, int y, int z, Block block, int meta) {
		if (meta != 0) throw new IllegalArgumentException("1.7.10 metadata " + meta + " of " + block + ": place the variant block itself");
		world.setBlock(new BlockPos(x, y, z), block.defaultBlockState(), doBlockNotify ? 3 : 2);
	}
}
