package alfheim.port.legacy

import net.minecraft.core.BlockSource
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior
import net.minecraft.core.dispenser.DispenseItemBehavior
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.entity.BlockEntity

/*
 * Раздатчик 1.7.10 (MAPPING.md, «Предметы, сущности, эффекты»). Поведение регистрируется `DispenserBlock.registerBehavior`;
 * сторона раздатчика 1.20.1 — в состоянии блока (`DispenserBlock.FACING`), а не в metadata — переписывается на месте.
 */

/** `IBlockSource` 1.7.10 */
typealias IBlockSource = BlockSource

/** `IBehaviorDispenseItem` 1.7.10 */
typealias IBehaviorDispenseItem = DispenseItemBehavior

/** `BehaviorDefaultDispenseItem` 1.7.10; `dispenseStack` — `execute` 1.20.1 */
typealias BehaviorDefaultDispenseItem = DefaultDispenseItemBehavior

/** `getXInt()`, `getYInt()`, `getZInt()` 1.7.10 — блок раздатчика */
val BlockSource.xInt get() = pos.x
val BlockSource.yInt get() = pos.y
val BlockSource.zInt get() = pos.z

/** `getWorld()` 1.7.10 */
val BlockSource.world: ServerLevel get() = level

/** `getBlockTileEntity()` 1.7.10 */
val BlockSource.blockTileEntity: BlockEntity get() = getEntity()
