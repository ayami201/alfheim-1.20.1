package alfheim.port.legacy;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * {@code net.minecraft.block.BlockMushroom} 1.7.10 — гриб (MAPPING.md, «Растения»): растение с рамкой 0,3–0,7 по
 * сторонам и 0,4 в высоту. Держит его блок снизу, если это мицелий или подзол, иначе — при свете ниже 13 и блоке, который
 * держит растение ({@code canSustainPlant}). Сплошной блок держит гриб всегда, как в 1.7.10: Forge и 1.7.10, и 1.20.1
 * сначала спрашивает само растение ({@code canPlaceBlockOn} 1.7.10 — {@link #mayPlaceOn}). Костная мука тратится
 * всегда и с шансом 0,4 растит большой гриб — только из гриба ванилы; гриб мода ставится на место.
 * <p>
 * Растекание грибов по случайным тикам ({@code updateTick} 1.7.10: раз в 25 тиков, не больше 5 грибов в квадрате 9×9)
 * не повторено: единственный гриб автора ({@code BlockRainbowMushroom}) его отключает.
 */
public class Mushroom1710 extends Bush1710 implements IGrowable {

	public Mushroom1710() {
		super(Material.plants);
		float f = 0.2F;
		setBlockBounds(0.5F - f, 0F, 0.5F - f, 0.5F + f, f * 2F, 0.5F + f);
		setTickRandomly(true);
	}

	/** {@code isFullBlock} 1.7.10 блока снизу — непрозрачный полный куб */
	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
		return state.isSolidRender(level, pos);
	}

	/**
	 * {@code canBlockStay} 1.7.10 с правкой Forge: мицелий, подзол (земля с metadata 2) или свет ниже 13 и блок снизу
	 * держит растение. Высота мира 1.7.10 (0–255) — границы мира 1.20.1
	 */
	@Override
	public boolean canBlockStay(Level world, int x, int y, int z) {
		if (y < world.getMinBuildHeight() || y >= world.getMaxBuildHeight()) return false;
		Block block = WorldKt.getBlock(world, x, y - 1, z);
		return block == Blocks.MYCELIUM || block == Blocks.PODZOL || world.getRawBrightness(new BlockPos(x, y, z), 0) < 13 && WorldKt.canSustainPlant(block, world, x, y - 1, z, ForgeDirection.UP, this);
	}

	@Override
	public boolean func_149851_a(Level world, int x, int y, int z, boolean isRemote) {
		return true;
	}

	@Override
	public boolean func_149852_a(Level world, RandomSource random, int x, int y, int z) {
		return random.nextFloat() < 0.4;
	}

	@Override
	public void func_149853_b(Level world, RandomSource random, int x, int y, int z) {
		func_149884_c(world, x, y, z, random);
	}

	/**
	 * Рост большого гриба 1.7.10: гриб убирается, генератор большого гриба есть только у грибов ванилы
	 * ({@code WorldGenBigMushroom}), поэтому гриб мода ставится обратно, как был (флаги 3)
	 */
	public boolean func_149884_c(Level world, int x, int y, int z, RandomSource random) {
		BlockPos pos = new BlockPos(x, y, z);
		BlockState state = world.getBlockState(pos);
		world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
		world.setBlock(pos, state, 3);
		return false;
	}
}
