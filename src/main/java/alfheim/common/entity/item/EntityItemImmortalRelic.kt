package alfheim.common.entity.item

import alexsocol.asjlib.*
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import vazkii.botania.api.item.IRelic

// oh fuck you forge and your entity recreation algorithms
class EntityItemImmortalRelic: EntityItemImmortal {
	
	constructor(world: World): super(world)
	
	constructor(original: EntityItem): super(original.worldObj, original, original.entityItem)
	
	override fun onUpdate() {
		super.onUpdate()
		
		if (posY < 0) {
			val owner = worldObj.playerEntities.firstOrNull { (it as EntityPlayer).commandSenderName == getOwner() } as EntityPlayer?
			
			setMotion(0.0)
			
			if (owner == null)
				setPosition(0.0, 256.0, 0.0)
			else {
				setPosition(owner)
				delayBeforeCanPickup = 0
			}
		}
	}
	
	override fun canBePickedByPlayer(player: EntityPlayer) =
		player.capabilities.isCreativeMode || getOwner().let { it.isNullOrEmpty() || it == player.commandSenderName } && getAchievement().let { it == null || player.hasAchievement(it) }
	
	fun getOwner() = (stack?.item as? IRelic)?.getSoulbindUsername(stack)
	
	fun getAchievement() = (stack?.item as? IRelic)?.bindAchievement
}