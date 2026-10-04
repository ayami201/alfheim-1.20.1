package alfheim.port.legacy;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/**
 * {@code net.minecraft.block.BlockDoublePlant} 1.7.10 (MAPPING.md, «Растения»): растение в два блока высотой,
 * верхняя половина (бит 8 metadata) — свойство {@code half}. Ставит и ломает обе половины {@code DoublePlantBlock}
 * 1.20.1, как 1.7.10. Материал растений, твёрдость 0, звук травы, рамка — весь блок.
 * <p>
 * Заменяемы ({@code isReplaceable} 1.7.10) только варианты 2 и 3: 1.7.10 считал по номеру варианта, это трава и
 * папоротник ванилы. У блока автора с вариантами-цветами это его варианты 2 и 3. Костная мука ({@link IGrowable}) — как
 * у цветов 1.7.10: варианты, кроме 2 и 3, роняют копию себя; классы автора её отключают.
 * <p>
 * Рисуется смещённым по X и Z, как двойное растение 1.7.10 ({@code RenderBlocks.renderBlockDoublePlant},
 * {@code RenderBlockColoredDoubleGrass} автора): на ±0,15 блока по хэшу координат ({@link #offset}).
 */
public class DoublePlant1710 extends DoublePlantBlock implements LegacyBlock, SoundTypes1710, IGrowable {

	private final BlockProps legacy;

	public DoublePlant1710() {
		super(Material.plants.properties().noCollission());
		legacy = new BlockProps(Material.plants);
		setHardness(0F);
		setStepSound(SoundType.GRASS);
		for (BlockState state : stateDefinition.getPossibleStates()) state.offsetFunction = Optional.of(DoublePlant1710::offset);
	}

	/**
	 * Смещение двойного растения 1.7.10: {@code ((h >> 16 & 15) / 15 - 0,5) * 0,3} по X и {@code (h >> 24 & 15)} так же по
	 * Z, где h — хэш x и z. {@code Mth.getSeed} 1.20.1 — тот же хэш, сдвинутый на 16; растения ванилы 1.20.1 смещаются им
	 * на ±0,25, а не на ±0,15
	 */
	private static Vec3 offset(BlockState state, BlockGetter level, BlockPos pos) {
		long hash = Mth.getSeed(pos.getX(), 0, pos.getZ());
		return new Vec3(((hash & 15L) / 15.0F - 0.5) * 0.3, 0, ((hash >> 8 & 15L) / 15.0F - 0.5) * 0.3);
	}

	@Override
	public BlockProps getLegacy() {
		return legacy;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
		Integer variant = getVariant();
		if (variant == null || (variant != 2 && variant != 3)) return false;
		return context.getItemInHand().isEmpty() || !context.getItemInHand().is(asItem());
	}

	/**
	 * {@code canPlaceBlockAt} 1.7.10: место можно занять (заменяемый материал), нижний блок держит растение
	 * ({@code canSustainPlant}), сверху — воздух
	 */
	public boolean canPlaceBlockAt(Level world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		return world.getBlockState(pos).canBeReplaced()
			&& WorldKt.canSustainPlant(WorldKt.getBlock(world, x, y - 1, z), world, x, y - 1, z, ForgeDirection.UP, this)
			&& world.isEmptyBlock(pos.above());
	}

	/** Удобрить можно все варианты, кроме 2 и 3 (трава и папоротник ванилы 1.7.10) */
	@Override
	public boolean func_149851_a(Level world, int x, int y, int z, boolean isRemote) {
		Integer variant = getVariant();
		return variant == null || (variant != 2 && variant != 3);
	}

	@Override
	public boolean func_149852_a(Level world, RandomSource random, int x, int y, int z) {
		return true;
	}

	/** Цветок 1.7.10 от костной муки ронял копию себя */
	@Override
	public void func_149853_b(Level world, RandomSource random, int x, int y, int z) {
		Block.popResource(world, new BlockPos(x, y, z), new ItemStack(this));
	}

	@Override
	public float getExplosionResistance() {
		return legacy.getBlockResistance() / 5f;
	}

	@Override
	public float getFriction() {
		return legacy.getSlipperiness();
	}

	@Override
	public SoundType getSoundType(BlockState state) {
		SoundType sound = legacy.getStepSound();
		return sound != null ? sound : SoundType.STONE;
	}
}
