package alfheim.port.legacy;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
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
 * случайный тик), {@link #checkAndDropBlock}. Класс написан на Java, как {@code BlockBush} 1.7.10: переопределения
 * автора объявляют параметры кто nullable, кто нет.
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
