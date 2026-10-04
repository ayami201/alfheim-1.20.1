package alfheim.port.legacy;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * {@code net.minecraft.block.BlockDoublePlant} 1.7.10 (MAPPING.md, «Растения»): растение в два блока высотой,
 * верхняя половина (бит 8 metadata) — свойство {@code half}. Ставит и ломает обе половины {@code DoublePlantBlock}
 * 1.20.1, как 1.7.10. Материал растений, твёрдость 0, звук травы, рамка — весь блок.
 * <p>
 * Заменяемы ({@code isReplaceable} 1.7.10) только варианты 2 и 3: 1.7.10 считал по номеру варианта, это трава и
 * папоротник ванилы. У блока автора с вариантами-цветами это его варианты 2 и 3.
 */
public class DoublePlant1710 extends DoublePlantBlock implements LegacyBlock, SoundTypes1710 {

	private final BlockProps legacy;

	public DoublePlant1710() {
		super(Material.plants.properties().noCollission());
		legacy = new BlockProps(Material.plants);
		setHardness(0F);
		setStepSound(SoundType.GRASS);
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
