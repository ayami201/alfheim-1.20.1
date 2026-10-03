package alfheim.common.potion

// PORT: импорты 1.20.1; урон заклинаний (КТ-7) закомментирован вместе со своей строкой
import alexsocol.asjlib.F
import alexsocol.asjlib.math.Vector3
import alfheim.AlfheimCore
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.port.legacy.*
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import kotlin.math.max
//import alfheim.common.core.util.DamageSourceSpell

object PotionBleeding: PotionAlfheim(AlfheimConfigHandler.potionIDBleeding, "bleeding", true, 0xFF0000) {
	
	override fun isReady(time: Int, ampl: Int) = time % max(1, 20 / (ampl + 1)) == 0
	
	override fun performEffect(living: EntityLivingBase, ampl: Int) {
		val prev = living.hurtResistantTime
		living.hurtResistantTime = 0
		// PORT: КТ-7 — урон кровотечением (DamageSourceSpell.bleeding)
//		living.attackEntityFrom(DamageSourceSpell.bleeding, (ampl + 1).F)
		living.hurtResistantTime = prev
		
		val (x, y, z) = Vector3.fromEntity(living).add((Math.random() - 0.5) * living.width, living.height / 2f + Math.random() - 0.5, (Math.random() - 0.5) * living.width)
		AlfheimCore.proxy.bloodFX(living.worldObj, x, y, z, 200, (Math.random() * 2 + 1).F / 10, 0.5F)
	}
	
	// PORT: имена полей 1.7.10
	private val EntityLivingBase.width get() = bbWidth
	private val EntityLivingBase.height get() = bbHeight
	private val EntityLivingBase.worldObj get() = level()
}
