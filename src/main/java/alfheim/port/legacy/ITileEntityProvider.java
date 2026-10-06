package alfheim.port.legacy;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * {@code net.minecraft.block.ITileEntityProvider} 1.7.10 — блок с блок-сущностью: {@code EntityBlock} 1.20.1 (MAPPING.md,
 * «Мир, блоки, блок-сущности»). {@code createNewTileEntity(world, meta)} автора становится {@code newBlockEntity(pos,
 * state)}: блок-сущность 1.20.1 создаётся сразу в своей точке. Блок без блок-сущности в этом состоянии (в 1.7.10 —
 * {@code hasTileEntity(meta) = false}) возвращает {@code null}.
 * <p>
 * Блок-сущности 1.7.10 мир тикал сам — и на сервере, и на клиенте, если {@code canUpdate()}; в 1.20.1 тик даёт блок
 * ({@link #getTicker}).
 */
public interface ITileEntityProvider extends EntityBlock {

	@Override
	default <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return (tickLevel, pos, tickState, be) -> {
			if (be instanceof TileEntity tile && tile.canUpdate()) tile.updateEntity();
		};
	}
}
