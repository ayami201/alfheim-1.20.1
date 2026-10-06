package alfheim.port.legacy.botania;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * {@code vazkii.botania.api.item.IHornHarvestable} r1.8-249: блок сам решает, что с ним делает рог Botania (рог дикой
 * природы ломает растения, рог кроны — листву, рог покрова — снег). Botania 1.20.1 спрашивает об этом не блок, а
 * capability {@code HORN_HARVEST}: блоки порта с этим интерфейсом передаёт ей {@link HornHarvest1710}.
 * <p>
 * Интерфейс написан на Java, как в Botania 1.7.10: переопределения автора объявляют параметры кто nullable, кто нет.
 */
public interface IHornHarvestable {

	/** Ломает ли рог блок в точке */
	boolean canHornHarvest(Level world, int x, int y, int z, ItemStack stack, EnumHornType hornType);

	/** {@code true} — рог не ломает блок, а зовёт {@link #harvestByHorn} */
	boolean hasSpecialHornHarvest(Level world, int x, int y, int z, ItemStack stack, EnumHornType hornType);

	void harvestByHorn(Level world, int x, int y, int z, ItemStack stack, EnumHornType hornType);

	/** Рога Botania — те же в 1.20.1 */
	enum EnumHornType {
		WILD,
		CANOPY,
		COVERING
	}
}
