package alfheim.common.potion

import alexsocol.asjlib.F
import alexsocol.asjlib.math.Vector3
import alfheim.AlfheimCore
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.DamageSourceSpell
import net.minecraft.entity.EntityLivingBase
import kotlin.math.*

object PotionBleeding: PotionAlfheim(AlfheimConfigHandler.potionIDBleeding, "bleeding", true, 0xFF0000) {
	
	override fun isReady(time: Int, ampl: Int) = time % min(1, (20 / max(1, ampl))) == 0
	
	override fun performEffect(living: EntityLivingBase, ampl: Int) {
		living.attackEntityFrom(DamageSourceSpell.bleeding, (ampl + 1).F)
		val (x, y, z) = Vector3.fromEntity(living).add((Math.random() - 0.5) * living.width, living.height / 2f + Math.random() - 0.5, (Math.random() - 0.5) * living.width)
		AlfheimCore.proxy.bloodFX(living.worldObj, x, y, z, 200, (Math.random() * 2 + 1).F / 10, 0.5F)
	}
}
