package alfheim.api.event

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.event.entity.player.PlayerEvent

class AttackEntityEventPost(player: EntityPlayer?, val target: Entity?): PlayerEvent(player)
 