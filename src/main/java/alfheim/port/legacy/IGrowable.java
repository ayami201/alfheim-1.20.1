package alfheim.port.legacy;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * {@code IGrowable} 1.7.10 — костная мука (MAPPING.md, «Растения»). Имена методов — 1.7.10, как у автора:
 * {@link #func_149851_a} — можно ли удобрить ({@code isValidBonemealTarget}), {@link #func_149852_a} — сработает ли
 * ({@code isBonemealSuccess}; иначе мука просто тратится), {@link #func_149853_b} — удобрить ({@code performBonemeal}).
 * Мука тратится так же, как в 1.7.10: если удобрить можно.
 * <p>
 * Интерфейс написан на Java, как {@code IGrowable} 1.7.10: переопределения автора объявляют параметры кто nullable,
 * кто нет.
 */
public interface IGrowable extends BonemealableBlock {

	boolean func_149851_a(Level world, int x, int y, int z, boolean isRemote);

	boolean func_149852_a(Level world, RandomSource random, int x, int y, int z);

	void func_149853_b(Level world, RandomSource random, int x, int y, int z);

	/** Мир генерации (не {@code Level}) в 1.7.10 костную муку не применял */
	@Override
	default boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
		return level instanceof Level world && func_149851_a(world, pos.getX(), pos.getY(), pos.getZ(), isClient);
	}

	@Override
	default boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
		return func_149852_a(level, random, pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	default void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
		func_149853_b(level, random, pos.getX(), pos.getY(), pos.getZ());
	}
}
