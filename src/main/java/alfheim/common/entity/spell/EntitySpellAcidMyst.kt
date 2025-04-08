package alfheim.common.entity.spell

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.spell.*
import alfheim.client.render.world.VisualEffectHandlerClient.VisualEffects
import alfheim.common.core.handler.*
import alfheim.common.core.handler.CardinalSystem.PartySystem
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.spell.darkness.SpellAcidMyst
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import java.util.*

class EntitySpellAcidMyst(world: World, val caster: EntityLivingBase?): Entity(world), ITimeStopSpecific {
	
	override val isImmune = false
	
	init {
		setSize(1f, 1f)
		if (caster != null) setPosition(caster.posX, caster.posY, caster.posZ)
		VisualEffectHandler.sendPacket(VisualEffects.ACID, this)
	}
	
	constructor(world: World): this(world, null)
	
	override fun onEntityUpdate() {
		if (!AlfheimConfigHandler.enableMMO || caster == null || caster.isDead || ticksExisted > SpellAcidMyst.duration) {
			setDead()
			return
		}
		if (isDead || ASJUtilities.isClient) return
		
		if (ticksExisted % 20 == 0) VisualEffectHandler.sendPacket(VisualEffects.ACID, this)
		
		val l = getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, getBoundingBox(posX, posY, posZ).expand(SpellAcidMyst.radius))
		
		l.remove(caster)
		
		l.forEach {
			if (PartySystem.mobsSameParty(caster, it) || Vector3.entityDistance(caster, it) > SpellAcidMyst.radius)
				return@forEach
			
			if (!it.attackEntityFrom(DamageSourceSpell.poisonMagic, SpellBase.over(caster, SpellAcidMyst.damage)))
				return@forEach
			
			val equipment: MutableList<ItemStack?> = it.lastActiveItems.toMutableList()
			if (it is EntityPlayer) equipment += it.heldItem
			
			equipment.forEach { stack ->
				if (stack != null && stack.item.isDamageable)
					stack.damageItem(20, it)
			}
		}
	}
	
	public override fun entityInit() = Unit
	public override fun readEntityFromNBT(nbt: NBTTagCompound) = Unit
	public override fun writeEntityToNBT(nbt: NBTTagCompound) = Unit
	override fun affectedBy(uuid: UUID) = caster?.uniqueID != uuid
}