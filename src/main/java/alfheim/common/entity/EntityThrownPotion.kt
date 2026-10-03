package alfheim.common.entity

// PORT: импорты 1.20.1 (MAPPING.md); EntityThrowable 1.7.10 — alfheim.port.legacy.EntityThrowable; зелья Botania 1.7.10
// (ModPotions) — BotaniaMobEffects
import alexsocol.asjlib.*
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.VisualEffectHandler
import alfheim.common.item.*
import alfheim.port.legacy.*
import alfheim.port.legacy.Potion1710 as Potion
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level as World
import vazkii.botania.common.brew.BotaniaMobEffects as ModPotions
import kotlin.math.sqrt

class EntityThrownPotion: EntityThrowable {
	
	// PORT: тип существа 1.20.1 — первым аргументом (legacyType)
	constructor(world: World): super(legacyType<EntityThrownPotion>(), world) {
		stack = ItemStack(AlfheimItems.splashPotion)
		effects = emptyList()
		
		color = 0xFFFFFF
	}
	
	constructor(world: World, st: ItemStack): super(legacyType<EntityThrownPotion>(), world) {
		stack = st
		val brew = stack.item as ItemSplashPotion
		effects = brew.getBrew(stack).getPotionEffects(stack)
		color = brew.getColor(stack)
	}
	
	constructor(player: EntityPlayer, st: ItemStack): super(legacyType<EntityThrownPotion>(), player.worldObj, player) {
		stack = st
		val brew = stack.item as ItemSplashPotion
		effects = brew.getBrew(stack).getPotionEffects(stack)
		color = brew.getColor(stack)
	}
	
	val effects: List<PotionEffect>
	val stack: ItemStack
	
	// PORT: DataWatcher 1.7.10 (номера 30, 31) → SynchedEntityData 1.20.1 (ключи GRAVITY, COLOR)
	var color
		get() = entityData.get(COLOR)
		set(value) {
			entityData.set(COLOR, value)
		}
//		get() = dataWatcher.getWatchableObjectInt(31)
//		set(value) {
//			dataWatcher.updateObject(31, value)
//		}
	
	// PORT: entityInit → defineSynchedData
	override fun defineSynchedData() {
		super.defineSynchedData()
		entityData.define(GRAVITY, 0.1f)
		entityData.define(COLOR, 0)
//		dataWatcher.addObject(30, 0.1f)
//		dataWatcher.setObjectWatched(30)
//
//		dataWatcher.addObject(31, 0)
//		dataWatcher.setObjectWatched(31)
	}
	
	val peClear = PotionEffect(ModPotions.clear.id, 0, 0)
	
	override fun onImpact(movingObject: MovingObjectPosition?) {
		if (worldObj.isRemote || movingObject == null || effects.isEmpty()) return setDead()
		
		VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.POTION, worldObj.dimension(), x, y, z, color.D, if (effects.contains(peClear)) 1.0 else 0.0)
//		VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.POTION, dimension, posX, posY, posZ, color.D, if (effects.contains(peClear)) 1.0 else 0.0)
		
		val list = getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox.expand(5.0, 2.5, 5.0))
		if (list.isEmpty()) return setDead()
		list.forEach { living ->
			val d0 = getDistanceSqToEntity(living)
			
			if (d0 >= 16.0) return@forEach
			var d1 = 1.0 - sqrt(d0) / 4.0
			
			if (living === movingObject.entityHit) d1 = 1.0
			
			for (e: PotionEffect in effects) {
				if (!Potion.potionTypes[e.potionID].isInstant) {
					val j = (d1 * e.duration.D + 0.5).I
					
					if (j <= 20) continue
					living.addPotionEffect(PotionEffect(e.potionID, j, e.amplifier))
					continue
				}
				
				Potion.potionTypes[e.potionID].affectEntity(thrower, living, e.amplifier, d1)
			}
		}
		
		setDead()
	}
	
	override fun getGravityVelocity() = entityData.get(GRAVITY)
//	override fun getGravityVelocity() = dataWatcher.getWatchableObjectFloat(30)
	
	public override fun func_70183_g() = -10f
	
	public override fun func_70182_d() = 1f
	
	companion object {
		
		// PORT: ключи данных существа 1.20.1 — вместо номеров DataWatcher 30 и 31
		private val GRAVITY = SynchedEntityData.defineId(EntityThrownPotion::class.java, EntityDataSerializers.FLOAT)
		private val COLOR = SynchedEntityData.defineId(EntityThrownPotion::class.java, EntityDataSerializers.INT)
	}
}
