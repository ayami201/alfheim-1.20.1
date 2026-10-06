package alfheim.port.legacy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * {@code net.minecraft.block.BlockBush} 1.7.10 — растение (MAPPING.md, «Растения»). Стоит на блоке, который его держит
 * ({@link #canBlockStay}: блок снизу {@code canSustainPlant}), без него ломается и роняет свой лут; столкновений нет;
 * рамка — 0,3–0,7 по сторонам и 0,6 в высоту; тики случайные. Держит ли блок снизу растение, решает Forge 1.20.1 так
 * же, по типу растения ({@code getPlantType}).
 * <p>
 * Методы 1.7.10, которые переопределяет автор: {@link #canBlockStay}, {@link #updateTick} (и запланированный, и
 * случайный тик), {@link #checkAndDropBlock}, {@link #randomDisplayTick}; установка и снятие, взрыв, сигнал и вечный
 * огонь — как у {@code Block1710} ({@link LegacyBlockMethods}). Класс написан на Java, как {@code BlockBush} 1.7.10:
 * переопределения автора объявляют параметры кто nullable, кто нет.
 */
public class Bush1710 extends BushBlock implements LegacyBlock, SoundTypes1710 {

	private final BlockProps legacy;

	private VoxelShape shape = Shapes.box(0.3, 0, 0.3, 0.7, 0.6, 0.7);

	public Bush1710(Material material) {
		super(material.properties().noCollission());
		legacy = new BlockProps(material);
		setTickRandomly(true);
	}

	@Override
	public BlockProps getLegacy() {
		return legacy;
	}

	/** {@code setBlockBounds} 1.7.10: рамка в долях блока */
	public void setBlockBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		shape = Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return shape;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	/** {@code canBlockStay} 1.7.10: блок снизу держит растение */
	public boolean canBlockStay(Level world, int x, int y, int z) {
		return WorldKt.canSustainPlant(WorldKt.getBlock(world, x, y - 1, z), world, x, y - 1, z, ForgeDirection.UP, this);
	}

	/**
	 * И при установке ({@code canPlaceBlockAt} 1.7.10), и потом: {@link #canBlockStay} смотрит только на блок снизу. Мир
	 * генерации (не {@code Level}) в 1.7.10 {@code canBlockStay} не спрашивал — для него правило растения 1.20.1
	 */
	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		if (level instanceof Level world) return canBlockStay(world, pos.getX(), pos.getY(), pos.getZ());
		return super.canSurvive(state, level, pos);
	}

	/** {@code updateTick} 1.7.10 — и запланированный, и случайный тик: у куста — проверка опоры */
	public void updateTick(Level world, int x, int y, int z, RandomSource random) {
		checkAndDropBlock(world, x, y, z);
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		updateTick(level, pos.getX(), pos.getY(), pos.getZ(), random);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		updateTick(level, pos.getX(), pos.getY(), pos.getZ(), random);
	}

	// Методы 1.7.10 класса автора (LegacyBlockMethods) из методов 1.20.1 — как у Block1710

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onPlace(state, level, pos, oldState, isMoving);
		onBlockAdded(level, pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		Integer variant = getVariant();
		breakBlock(level, pos.getX(), pos.getY(), pos.getZ(), this, variant != null ? variant : 0);
		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
		onBlockExploded(level, pos.getX(), pos.getY(), pos.getZ(), explosion);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return canProvidePower();
	}

	@Override
	public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return isProvidingWeakPower(level, pos.getX(), pos.getY(), pos.getZ(), direction.get3DDataValue());
	}

	@Override
	public boolean isFireSource(BlockState state, LevelReader level, BlockPos pos, Direction direction) {
		return level instanceof Level world && isFireSource(world, pos.getX(), pos.getY(), pos.getZ(), ForgeDirection.getOrientation(direction.get3DDataValue()))
			|| super.isFireSource(state, level, pos, direction);
	}

	/** {@code randomDisplayTick} 1.7.10 — частицы и звуки рядом с игроком на клиенте; у куста их нет */
	public void randomDisplayTick(Level world, int x, int y, int z, RandomSource random) {
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		randomDisplayTick(level, pos.getX(), pos.getY(), pos.getZ(), random);
	}

	/** Без опоры — лут блока и воздух (флаг 2: без частиц и звука), как в 1.7.10 */
	public void checkAndDropBlock(Level world, int x, int y, int z) {
		if (canBlockStay(world, x, y, z)) return;
		BlockPos pos = new BlockPos(x, y, z);
		Block.dropResources(world.getBlockState(pos), world, pos);
		world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
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

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return legacy.getNeedsRandomTick();
	}
}
