package alfheim.common.core.asm.hook.replacer

import alfheim.common.core.util.DamageSourceSpell
import com.KAIIIAK.classManipulators.HookReplacer
import com.KAIIIAK.classManipulators.HookReplacer.Replacer.*
import net.minecraft.util.DamageSource
import vazkii.botania.common.block.tile.TileCocoon
import vazkii.botania.common.entity.EntityDoppleganger

@Suppress("UNUSED_PARAMETER", "unused")
object HookReplacerHandler {
	
	@JvmStatic
	@HookReplacer(targetMethod = "hatch")
	fun replaceSpecialChance(thiz: TileCocoon) {
		startFROM()
		POPLine();POP(0.05f)
		POPLine();startTO()
		POPLine();POP(getCocoonSpecialChance(thiz))
		POPLine();stop()
	}
	
	@JvmStatic
	fun getCocoonSpecialChance(thiz: TileCocoon): Float {
		return thiz.alfheim_synthetic_essenceGiven * 0.05f
	}
	
	@JvmStatic
	@HookReplacer(targetMethod = "hatch")
	fun replaceMooshroomChance(thiz: TileCocoon) {
		startFROM()
		POPLine();POP(0.01)
		POPLine();startTO()
		POPLine();POP(getCocoonMooshroomChance(thiz))
		POPLine();stop()
	}
	
	@JvmStatic
	fun getCocoonMooshroomChance(thiz: TileCocoon): Double {
		return thiz.alfheim_synthetic_essenceGiven * 0.01
	}
	
	@JvmStatic
	@HookReplacer(targetMethod = "attackEntityFrom")
	fun allowSpellDamage(thiz: EntityDoppleganger, src: DamageSource, dmg: Float): Boolean {
		startFROM()
		POPLine();POP(src.damageType.equals("player"))
		POPLine();startTO()
		POPLine();POP(checkDamage(src))
		POPLine();stop()
		
		return false
	}

	@JvmStatic
	fun checkDamage(src: DamageSource): Boolean {
		return src.damageType == "player" || src is DamageSourceSpell
	}
}