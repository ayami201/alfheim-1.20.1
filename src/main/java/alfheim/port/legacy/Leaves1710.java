package alfheim.port.legacy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * {@code net.minecraft.block.BlockLeaves} 1.7.10 (MAPPING.md, «Растения»): материал листвы; свет задерживает, как задал
 * {@code setLightOpacity}, в дождь роняет капли, при «быстрой» графике рисуется сплошной — как листва 1.20.1. Бит
 * опадания metadata (у автора — {@code decayBit()}: листва, которую поставил игрок, не опадает) — свойство
 * {@code persistent}.
 * <p>
 * Опадать ли, решает метод 1.7.10 {@link #updateTick} класса автора — на случайном тике листвы, которая может опасть;
 * у ванилы 1.7.10 он свой, здесь — пустой. Расстояние до бревна ({@code distance}) листва считает, как листва 1.20.1:
 * по нему через неё находит бревно листва ванилы, поэтому запланированный тик ({@code tick}) остаётся тиком 1.20.1.
 * Лут — таблица (генерация данных): с ножницами или шёлковым касанием — сама листва; без шёлкового касания — что даёт
 * {@link #getItemDropped} (и с ножницами: Forge 1.7.10 после среза не отменял обычный сбор).
 * <p>
 * Класс написан на Java, как {@code BlockLeaves} 1.7.10: переопределения автора объявляют параметры кто nullable, кто
 * нет.
 */
public abstract class Leaves1710 extends LeavesBlock implements LegacyBlock, SoundTypes1710 {

	private final BlockProps legacy;

	public Leaves1710() {
		super(Material.leaves.properties().noOcclusion());
		legacy = new BlockProps(Material.leaves);
	}

	@Override
	public BlockProps getLegacy() {
		return legacy;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	/** Свет листва задерживает, как задала {@code setLightOpacity} (у листвы ванилы 1.7.10 и 1.20.1 — 1), как {@code Block1710} */
	@Override
	public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
		return Math.min(lightOpacity(), 15);
	}

	/** Свет неба 1.7.10 проходил блок с непрозрачностью 0 не ослабевая, как {@code Block1710} */
	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return lightOpacity() == 0;
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return !state.getValue(PERSISTENT);
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		updateTick(level, pos.getX(), pos.getY(), pos.getZ(), random);
	}

	/**
	 * {@code randomDisplayTick} 1.7.10 — частицы рядом с игроком на клиенте; у листвы — капли в дождь, как у листвы
	 * 1.20.1 ({@code animateTick})
	 */
	public void randomDisplayTick(Level world, int x, int y, int z, RandomSource random) {
		BlockPos pos = new BlockPos(x, y, z);
		super.animateTick(world.getBlockState(pos), world, pos, random);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		randomDisplayTick(level, pos.getX(), pos.getY(), pos.getZ(), random);
	}

	/**
	 * {@code isLeaves} Forge 1.7.10 — листва ли блок; у листвы — да. В 1.20.1 листву узнают по тегу
	 * {@code minecraft:leaves}: генерация данных кладёт в него листву порта, у которой этот метод отвечает «да» (мир —
	 * {@code null}), а {@code isLeaves} прослойки (World.kt) спрашивает тег
	 */
	public boolean isLeaves(BlockGetter world, int x, int y, int z) {
		return true;
	}

	// Методы 1.7.10 класса автора (LegacyBlockMethods) из методов 1.20.1 — как у Block1710, кроме тика

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

	/** Что роняет листва без ножниц ({@code getItemDropped} 1.7.10): у ванилы — саженец дуба */
	public Item getItemDropped(int meta, RandomSource random, int fortune) {
		return Items.OAK_SAPLING;
	}

	/** {@code quantityDropped} 1.7.10; лут листвы 1.7.10 ({@code getDrops}) его не спрашивал — у саженца свой шанс */
	public int quantityDropped(RandomSource random) {
		return random.nextInt(20) == 0 ? 1 : 0;
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
