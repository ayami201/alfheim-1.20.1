package alfheim.common.entity.spell

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.spell.*
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.*
import alfheim.common.spell.fire.SpellFirestar
import alfheim.common.spell.illusion.SpellDarkness
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.*
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.potion.Potion
import net.minecraft.util.DamageSource
import net.minecraft.world.World
import net.minecraftforge.event.entity.living.LivingHurtEvent
import java.util.*
import kotlin.math.max

class EntitySpellFirestar(world: World, val caster: EntityLivingBase?): Entity(world), ITimeStopSpecific {
	
	var powered
		get() = getFlag(2)
		set(value) = setFlag(2, value)
	
	override val isImmune = true
	
	init {
		setSize(0f, 0f)
		if (caster != null) setPosition(caster.posX, caster.posY + 1.5, caster.posZ)
	}
	
	constructor(world: World): this(world, null)
	
	override fun onEntityUpdate() {
		if (!AlfheimConfigHandler.enableMMO || caster == null || caster.isDead || ticksExisted > (SpellFirestar.duration / if (powered) 2 else 1)) {
			setDead()
			return
		}
		
		if (isDead || ASJUtilities.isClient) return
		
		VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.FIRESTAR, dimension, posX, posY, posZ, SpellFirestar.radius, if (powered) 1.0 else 0.0)
		
		val l = getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, getBoundingBox(posX, posY, posZ).expand(SpellFirestar.radius))
		l.removeAll { Vector3.entityDistance(caster, it) > SpellFirestar.radius }
		
		l.forEach {
			if (it === caster || CardinalSystem.PartySystem.mobsSameParty(caster, it)) {
				it.addPotionEffect(PotionEffectU(Potion.fireResistance.id, 10))
				it.heal(SpellFirestar.efficiency.F)
			} else {
				it.attackEntityFrom(DamageSource.inFire, SpellBase.over(caster, SpellDarkness.damage))
			}
		}
	}
	
	override fun entityInit() = Unit
	override fun readEntityFromNBT(nbt: NBTTagCompound?) = Unit
	override fun writeEntityToNBT(nbt: NBTTagCompound?) = Unit
	override fun affectedBy(uuid: UUID) = false
	
	companion object {
		
		init {
			eventForge()
		}
		
		@SubscribeEvent
		fun lowerDamage(e: LivingHurtEvent) {
			val target = e.entityLiving
			
			val decrease = getEntitiesWithinAABB(target.worldObj, EntitySpellFirestar::class.java, target.boundingBox(SpellFirestar.radius)).filter { 
				Vector3.entityDistance(target, it) <= SpellFirestar.radius && CardinalSystem.PartySystem.mobsSameParty(it.caster, target)
			}.maxOfOrNull { SpellFirestar.damage * if (it.powered) 5 else 1 } ?: return
			
			e.ammount = max(0f, e.ammount - decrease)
		}
	}
}
