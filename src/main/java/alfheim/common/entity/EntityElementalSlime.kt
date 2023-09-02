package alfheim.common.entity

import alexsocol.asjlib.*
import alfheim.api.entity.IAlfheimMob
import alfheim.common.core.helper.*
import alfheim.common.item.material.ItemElvenResource
import net.minecraft.client.particle.EntityBreakingFX
import net.minecraft.entity.monster.EntitySlime
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraft.world.World
import java.awt.Color
import java.util.*

class EntityElementalSlime(world: World): EntitySlime(world), IElementalEntity, IAlfheimMob {
	
	override var elements: EnumSet<ElementalDamage>
		get() = EnumSet.of(ElementalDamage.entries[dataWatcher.getWatchableObjectInt(2)])
		set(value) = dataWatcher.updateObject(2, value.first().ordinal)
	
	override fun entityInit() {
		super.entityInit()
		val element = allowedElements.random(rng)!!
		dataWatcher.addObject(2, element.ordinal)
	}
	
	override fun getCanSpawnHere() =
		worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox)
	
	override fun createInstance(): EntitySlime {
		val instance = EntityElementalSlime(worldObj)
		instance.elements = elements.clone()
		return instance
	}
	
	override fun dropFewItems(resentlyHit: Boolean, looting: Int) {
		var count = rand.nextInt(3)
		
		if (looting > 0)
			count += rand.nextInt(looting + 1)
		
		entityDropItem(dropStack(count), 0f)
	}
	
	fun dropStack(size: Int = 1) = ItemElvenResource.ballForElement(elements.first(), size)
	
	override fun getDropItem() = null
	
	override fun getSlimeParticle(): String {
		if (ASJUtilities.isServer) return ""
		
		val drop = dropStack()
		
		val f = rand.nextFloat() * Math.PI.F * 2.0f
		val f1 = rand.nextFloat() * 0.5f + 0.5f
		val f2 = MathHelper.sin(f) * slimeSize * 0.5 * f1
		val f3 = MathHelper.cos(f) * slimeSize * 0.5 * f1
		
		EntityBreakingFX(worldObj, posX + f2, boundingBox.minY, posZ + f3, drop.item, drop.meta).apply {
			setParticleIcon(drop.item.getIcon(drop, 0))
			
			val (r, g, b) = Color(drop.item.getColorFromItemStack(drop, 0)).getRGBColorComponents(null)
			setRBGColorF(r, g, b)
			
			mc.effectRenderer.addEffect(this)
		}
		
		return ""
	}
	
	override fun writeEntityToNBT(nbt: NBTTagCompound) {
		super.writeEntityToNBT(nbt)
		nbt.setInteger(TAG_ELEMENT, dataWatcher.getWatchableObjectInt(2))
	}
	
	override fun readEntityFromNBT(nbt: NBTTagCompound) {
		super.readEntityFromNBT(nbt)
		dataWatcher.updateObject(2, nbt.getInteger(TAG_ELEMENT))
	}
	
	override fun getPickedResult(target: MovingObjectPosition?) = super<IAlfheimMob>.getPickedResult(target)
	
	companion object {
		const val TAG_ELEMENT = "element"
		val allowedElements = EnumSet.copyOf(ElementalDamage.entries).apply { remove(ElementalDamage.COMMON) }!!
	}
}
