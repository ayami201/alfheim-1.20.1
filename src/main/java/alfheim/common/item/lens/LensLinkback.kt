package alfheim.common.item.lens

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.item.ItemStack
import vazkii.botania.api.internal.IManaBurst
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.common.block.tile.mana.TileSpreader
import vazkii.botania.common.item.lens.Lens
import kotlin.math.min

class LensLinkback: Lens() {
	
	override fun updateBurst(burst: IManaBurst, entity: EntityThrowable, stack: ItemStack) {
		val need = burst.startingMana - burst.mana
		if (need <= 0) return
		
		val (x, y, z) = burst.burstSourceChunkCoordinates
		if (y != -1) run {
			val tile = entity.worldObj?.getTileEntity(x, y, z) as? TileSpreader ?: return@run
			
			if (Vector3.entityTileDistance(entity, tile) > when (tile.getBlockMetadata()) {
				2 -> 34
				3 -> 60
				4 -> 152
				else -> 20
			}) return
			
			val requested = min(tile.currentMana, need)
			if (requested <= 0) return
			
			tile.recieveMana(-requested)
			burst.mana += requested
			
			return
		}
		
		val shooter = entity.thrower as? EntityPlayer ?: return
		if (Vector3.entityDistance(entity, shooter) > 90) return
		
		burst.mana += ManaItemHandler.requestMana(stack, shooter, need, true)
	}
}