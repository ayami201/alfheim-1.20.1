package alfheim.common.entity

import alexsocol.asjlib.*
import alfheim.api.entity.IAlfheimMob
import alfheim.common.core.handler.AlfheimConfigHandler
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.monster.EntityCreeper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.MovingObjectPosition
import net.minecraft.world.World
import vazkii.botania.common.item.ModItems

/**
 * All the mana is mine mahhhaahahha
 */
class EntityVoidCreeper(world: World): EntityCreeper(world), IAlfheimMob {
	
	override fun dropFewItems(par1: Boolean, par2: Int) {
		if (par1 && Math.random() < AlfheimConfigHandler.blackLotusDropRate)
			entityDropItem(ItemStack(ModItems.blackLotus), 1F)
	}
	
	override fun func_146077_cc() {
		if (worldObj.isRemote) return
		
		getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox(explosionRadius * if (powered) 2 else 1)).forEach {
			if (it !is EntityPlayer) return@forEach
			it.addPotionEffect(PotionEffectU(AlfheimConfigHandler.potionIDManaVoid, if (powered) 1200 else 120, 0))
		}
		
		worldObj.createExplosion(this, posX, posY, posZ, 1f, false)
		setDead()
	}
	
	override fun getPickedResult(target: MovingObjectPosition?) = super<IAlfheimMob>.getPickedResult(target)
}
