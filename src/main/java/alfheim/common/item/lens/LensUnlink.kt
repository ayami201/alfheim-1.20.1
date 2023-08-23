package alfheim.common.item.lens

import alexsocol.asjlib.*
import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.item.ItemStack
import vazkii.botania.api.internal.IManaBurst
import vazkii.botania.common.block.tile.mana.TileSpreader
import vazkii.botania.common.item.lens.Lens
import java.util.*

class LensUnlink: Lens() {
	
	val zeroUUID = UUID(0L, 0L)
	
	override fun updateBurst(burst: IManaBurst, entity: EntityThrowable, stack: ItemStack?) {
		burst.setShooterUUID(zeroUUID)
		
		val (x, y, z) = burst.burstSourceChunkCoordinates ?: return
		if (y == -1) return
		
		val spreader = entity.worldObj.getTileEntity(x, y, z) as? TileSpreader ?: return
		spreader.canShootBurst = true
	}
}