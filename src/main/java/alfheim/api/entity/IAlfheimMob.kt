package alfheim.api.entity

import alfheim.common.item.ItemSpawnEgg
import net.minecraft.entity.Entity
import net.minecraft.util.MovingObjectPosition

interface IAlfheimMob {
	fun getPickedResult(target: MovingObjectPosition?) = ItemSpawnEgg.forEntity(this as Entity)
}
