package alfheim.port.legacy;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * {@code net.minecraft.block.BlockSapling} 1.7.10 (MAPPING.md, «Растения»): рамка 0,1–0,9 по сторонам и 0,8 в высоту.
 * Саженец растёт в два шага: первый ставит бит 8 metadata («готов расти») — свойство {@link #STAGE}, второй сажает
 * дерево. Костная мука: удобрить можно всегда, срабатывает с шансом 0,45; шаг роста и само дерево задаёт класс автора.
 */
public class Sapling1710 extends Bush1710 implements IGrowable {

	/** Бит 8 metadata саженца 1.7.10 — «готов расти» */
	public static final IntegerProperty STAGE = BlockStateProperties.STAGE;

	public Sapling1710() {
		super(Material.plants);
		setBlockBounds(0.1F, 0F, 0.1F, 0.9F, 0.8F, 0.9F);
		registerDefaultState(stateDefinition.any().setValue(STAGE, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(STAGE);
	}

	@Override
	public boolean func_149851_a(Level world, int x, int y, int z, boolean isRemote) {
		return true;
	}

	@Override
	public boolean func_149852_a(Level world, RandomSource random, int x, int y, int z) {
		return random.nextFloat() < 0.45;
	}

	/** Шаг роста саженца ванилы ({@code func_149879_c}) — деревья ванилы; класс автора сажает свои */
	@Override
	public void func_149853_b(Level world, RandomSource random, int x, int y, int z) {
	}
}
