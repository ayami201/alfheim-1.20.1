package alfheim.port.legacy;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * {@code net.minecraft.block.BlockTallGrass} 1.7.10 (MAPPING.md, «Растения»): материал лиан — трава заменяемая (в неё
 * ставят блок) и горит; рамка 0,1–0,9 по сторонам и 0,8 в высоту. Костная мука: удобрить можно, срабатывает всегда;
 * что вырастает, решает класс автора. Лут — таблица (генерация данных): семена с шансом 1/8
 * ({@code ForgeHooks.getGrassSeed}), с ножницами — ещё и сама трава ({@code onSheared}).
 */
public class TallGrass1710 extends Bush1710 implements IGrowable {

	public TallGrass1710() {
		super(Material.vine);
		setBlockBounds(0.1F, 0F, 0.1F, 0.9F, 0.8F, 0.9F);
	}

	@Override
	public boolean func_149851_a(Level world, int x, int y, int z, boolean isRemote) {
		return true;
	}

	@Override
	public boolean func_149852_a(Level world, RandomSource random, int x, int y, int z) {
		return true;
	}

	@Override
	public void func_149853_b(Level world, RandomSource random, int x, int y, int z) {
	}
}
