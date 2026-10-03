package alfheim.common.entity

// PORT: импорты 1.20.1 (MAPPING.md); EntityThrowable 1.7.10 — alfheim.port.legacy.EntityThrowable
import alexsocol.asjlib.getEntitiesWithinAABB
import alexsocol.asjlib.expand
import alexsocol.asjlib.math.Vector3
import alfheim.port.legacy.*
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.block.Blocks

class EntityThrowableItem: EntityThrowable {
	
	// PORT: тип существа 1.20.1 — первым аргументом (legacyType)
	constructor(world: World): super(legacyType<EntityThrowableItem>(), world)
	
	constructor(player: EntityPlayer): super(legacyType<EntityThrowableItem>(), player.worldObj, player)
	
	override fun onImpact(movingObject: MovingObjectPosition?) {
		if (thrower == null) return
		if (worldObj.isRemote) return
		if (movingObject == null) return
		
		getEntitiesWithinAABB(worldObj, EntityLivingBase::class.java, boundingBox.expand(8.0, 2.0, 8.0)).forEach { living ->
			if (getDistanceSqToEntity(living) >= 16.0) return@forEach
			// PORT: EntityDamageSourceIndirect("fireball", …).setFireDamage() — урон типа «огненный шар» (огненный, как в 1.7.10)
			living.attackEntityFrom(DamageSource(worldObj.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.FIREBALL), this, thrower), 3f)
//			living.attackEntityFrom(EntityDamageSourceIndirect("fireball", this, thrower).setFireDamage(), 3f)
			living.setFire(10)
		}
		
		val v = Vector3.fromEntity(this)
		val (xo, yo, zo) = v.mf()
		
		// PORT: событие 2002 — брызги зелья; в 1.7.10 его число — metadata зелья (16451 — брызгающее огнестойкости), в
		// 1.20.1 — цвет брызг: цвет огнестойкости
		worldObj.levelEvent(2002, BlockPos(xo, yo, zo), MobEffects.FIRE_RESISTANCE.color)
//		worldObj.playAuxSFX(2002, xo, yo, zo, 16451) // fire resistance meta
		setDead()
		
		tryToSetFire(xo, yo, zo)
		
		for (n in 0..36) {
			val (x, y, z) = v.rand().mul(6).sub(3).add(this).mf()
			
			tryToSetFire(x, y, z)
		}
	}
	
	// PORT: canPlaceBlockAt огня 1.7.10 → canSurvive его состояния (твёрдый блок снизу или горючий сосед)
	private fun tryToSetFire(x: Int, y: Int, z: Int) {
		if (!worldObj.isAirBlock(x, y, z) || !Blocks.FIRE.defaultBlockState().canSurvive(worldObj, BlockPos(x, y, z))) return
		worldObj.setBlock(x, y, z, Blocks.FIRE)
//		if (!worldObj.isAirBlock(x, y, z) || !Blocks.fire.canPlaceBlockAt(worldObj, x, y, z)) return
//		worldObj.setBlock(x, y, z, Blocks.fire)
	}
	
	public override fun func_70183_g() = -10f
	
	public override fun func_70182_d() = 1f
}
