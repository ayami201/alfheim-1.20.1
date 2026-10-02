package alfheim.api.event

// PORT: EntityPlayer → Player
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraftforge.event.entity.player.PlayerEvent

class AttackEntityEventPost(player: EntityPlayer?, val target: Entity?): PlayerEvent(player)
 