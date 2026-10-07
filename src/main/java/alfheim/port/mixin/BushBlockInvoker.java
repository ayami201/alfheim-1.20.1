package alfheim.port.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Обёртка автора {@code SuperWrapperHandler.canPlaceBlockOn(bush, block)} (S-03, HOOKS.md): спросить у куста, растёт ли
 * он на блоке, — его защищённым методом {@code canPlaceBlockOn} 1.7.10, в 1.20.1 — {@code mayPlaceOn} (своё правило
 * куста, если класс его переопределил)
 */
@Mixin(BushBlock.class)
public interface BushBlockInvoker {

	@Invoker("mayPlaceOn")
	boolean alfheim$mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos);
}
