package alfheim.common.entity.item

import alexsocol.asjlib.*
import alfheim.common.core.asm.hook.extender.RelicHooks
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
			val owner = worldObj.playerEntities.firstOrNull { (it as EntityPlayer).commandSenderName == owner } as EntityPlayer?
			
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
		if (player.capabilities.isCreativeMode || stack?.item !in RelicHooks.underControl)
			true
		else if (owner.let { it.isNullOrEmpty() || it == player.commandSenderName })
			bindAhievement.let { it == null || player.hasAchievement(it) }
		else
			true
	
	val owner get() = (stack?.item as? IRelic)?.getSoulbindUsername(stack)
	
	val bindAhievement get() = (stack?.item as? IRelic)?.bindAchievement
}