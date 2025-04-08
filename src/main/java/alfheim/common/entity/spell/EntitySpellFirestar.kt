package alfheim.common.entity.spell

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.AlfheimAPI
import alfheim.api.spell.*
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.*
import alfheim.common.network.*
import alfheim.common.network.packet.Message2d
import alfheim.common.spell.fire.SpellFirestar
import alfheim.common.spell.illusion.SpellDarkness
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayerMP
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
			
			if (caster is EntityPlayerMP) {
				val cd = SpellFirestar.getCooldown()
				CardinalSystem.SpellCastingSystem.setCoolDown(caster, SpellFirestar, cd)
				NetworkService.sendTo(Message2d(M2d.COOLDOWN, (SpellFirestar.race.ordinal and 0xF shl 28 or (AlfheimAPI.getSpellID(SpellFirestar) and 0xFFFFFFF)).D, cd.D), caster)
			}
			
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
				
				if ((ticksExisted % if (powered) 10 else 50) != 0) return@forEach
				
				val bleeding = it.getActivePotionEffect(AlfheimConfigHandler.potionIDBleeding) ?: return@forEach
				
				if (bleeding.amplifier > 0)
					bleeding.amplifier -= 1
				else
					it.removePotionEffect(AlfheimConfigHandler.potionIDBleeding)
			} else {
				it.attackEntityFrom(DamageSource.inFire, SpellBase.over(caster, SpellDarkness.damage * if (powered) 2 else 1))
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
