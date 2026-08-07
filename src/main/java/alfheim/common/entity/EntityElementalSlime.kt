package alfheim.common.entity

import alexsocol.asjlib.*
import alfheim.api.entity.*
import alfheim.common.core.helper.*
import alfheim.common.item.*
import alfheim.common.item.material.*
import net.minecraft.client.particle.*
import net.minecraft.entity.item.*
import net.minecraft.entity.monster.*
import net.minecraft.item.*
import net.minecraft.nbt.*
import net.minecraft.util.*
import net.minecraft.world.*
import vazkii.botania.common.block.subtile.generating.*
import java.awt.*
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
	
	override fun createInstance(): EntitySlime {
		val instance = EntityElementalSlime(worldObj)
		instance.elements = elements.clone()
		
		if (getEntityData().getBoolean(SubTileNarslimmus.TAG_WORLD_SPAWNED))
			instance.getEntityData().setBoolean(SubTileNarslimmus.TAG_WORLD_SPAWNED, true)
		
		return instance
	}
	
	override fun dropFewItems(resentlyHit: Boolean, looting: Int) {
		if (slimeSize != 1) return
		
		var count = rand.nextInt(3)
		
		if (looting > 0)
			count += rand.nextInt(looting + 1)
		
		super.entityDropItem(dropStack(count), 0f)
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
		
		if (nbt.hasKey(TAG_ELEMENT, 99))
			dataWatcher.updateObject(2, nbt.getInteger(TAG_ELEMENT))
	}
	
	override fun getPickedResult(target: MovingObjectPosition?) = super<IAlfheimMob>.getPickedResult(target)
	
	override fun entityDropItem(stack: ItemStack, height: Float): EntityItem? = super.entityDropItem(
		if (stack.item === AlfheimItems.elvenResource && stack.meta == ElvenResourcesMetas.ElementalSlimeBall.I)
			ItemElvenResource.ballForElement(elements.first(), stack.stackSize)
		else
			stack, 
		height)
	
	companion object {
		const val TAG_ELEMENT = "element"
		val allowedElements = EnumSet.copyOf(ElementalDamage.entries).apply { remove(ElementalDamage.COMMON) }!!
	}
}
