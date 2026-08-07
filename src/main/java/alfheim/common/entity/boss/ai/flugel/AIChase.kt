package alfheim.common.entity.boss.ai.flugel

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.common.entity.boss.EntityFlugel
import alfheim.common.item.AlfheimItems

class AIChase(flugel: EntityFlugel, task: AITask): AIBase(flugel, task) {
	
	var lowest = false
	
	override fun startExecuting() {
		flugel.noClip = true
		val i = when (flugel.stage) {
			1    -> 200
			2    -> 100
			else -> 50
		}
		
		flugel.aiTaskTimer = flugel.rng.nextInt(i) + i
		
		lowest = flugel.rng.nextInt(10) == 0
		
		if (flugel.rng.nextInt(4) != 0) return
		// scramble items:
		
		val player = flugel.worldObj.getPlayerEntityByName(flugel.playersDamage.keys.random(flugel.rng) ?: return) ?: return
		if (player.capabilities.isCreativeMode) return
		
		val inv = player.inventory
		val slots = inv.mainInventory.indices.toMutableList()
		val items = slots.toList().mapNotNull {
			val stack = inv[it] ?: return@mapNotNull null
			
			if (stack.item === AlfheimItems.organs) {
				slots.remove(it)
				return@mapNotNull null
			}
			
			inv[it] = null
			stack.copy()
		}
		
		items.forEach {
			inv[slots.removeRandom()!!] = it
		}
	}
	
	override fun continueExecuting(): Boolean {
		flugel.checkCollision()
		if (flugel.aiTaskTimer % 10 == 0) {
			val name = if (lowest)
				flugel.playersDamage.minByOrNull { it.value }?.key ?: ""
			else
				flugel.playersDamage.maxByOrNull { it.value }?.key ?: ""
			
			val target = flugel.worldObj.getPlayerEntityByName(name)
			
			if (target != null) {
				val mot = Vector3(target.posX - flugel.posX, target.posY - flugel.posY, target.posZ - flugel.posZ).normalize()
				flugel.motionX = mot.x
				flugel.motionY = mot.y
				flugel.motionZ = mot.z
			} else {
				flugel.playersDamage.remove(name)
			}
		}
		return canContinue()
	}
}