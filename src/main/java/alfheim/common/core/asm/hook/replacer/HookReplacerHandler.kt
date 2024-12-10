package alfheim.common.core.asm.hook.replacer

import alfheim.common.core.util.DamageSourceSpell
import com.KAIIIAK.classManipulators.HookReplacer
import com.KAIIIAK.classManipulators.HookReplacer.Replacer.*
import net.minecraft.entity.Entity
import net.minecraft.entity.player.*
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.util.DamageSource
import vazkii.botania.common.block.tile.TileCocoon
import vazkii.botania.common.core.handler.SheddingHandler
import vazkii.botania.common.core.handler.SheddingHandler.ShedPattern
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.entity.EntityDoppleganger
import vazkii.botania.common.item.rod.ItemGravityRod

@HookReplacer(targetMethod = "hatch")
fun replaceSpecialChance(thiz: TileCocoon) {
	startFROM()
	POPLine();POP(0.05f)
	POPLine();startTO()
	POPLine();POP(getCocoonSpecialChance(thiz))
	POPLine();stop()
}

fun getCocoonSpecialChance(thiz: TileCocoon): Float {
	return thiz.alfheim_synthetic_essenceGiven * 0.05f
}

@HookReplacer(targetMethod = "hatch")
fun replaceMooshroomChance(thiz: TileCocoon) {
	startFROM()
	POPLine();POP(0.01)
	POPLine();startTO()
	POPLine();POP(getCocoonMooshroomChance(thiz))
	POPLine();stop()
}

fun getCocoonMooshroomChance(thiz: TileCocoon): Double {
	return thiz.alfheim_synthetic_essenceGiven * 0.01
}

@HookReplacer(targetMethod = "attackEntityFrom")
fun allowSpellDamage(thiz: EntityDoppleganger, src: DamageSource, dmg: Float): Boolean {
	startFROM()
	POPLine();POP(src.damageType.equals("player"))
	POPLine();startTO()
	POPLine();POP(checkDamage(src))
	POPLine();stop()
	
	return false
}

fun checkDamage(src: DamageSource): Boolean {
	return src.damageType == "player" || src is DamageSourceSpell
}

@HookReplacer(correctStaticIndexes = true)
fun leftClick(static: ItemGravityRod, player: EntityPlayer) {
	startFROM()
	POPLine();ItemNBTHelper.setInt(ALOAD("1"), "ticksCooldown", 10)
	POPLine();startTO()
	POPLine();ItemNBTHelper.setInt(ALOAD("1"), "ticksCooldown", 10)
	POPLine();sendVelocityPacket(ALOAD("3"))
	POPLine();stop()
}

fun sendVelocityPacket(item: Entity?) {
	if (item is EntityPlayerMP) item.playerNetServerHandler.sendPacket(S12PacketEntityVelocity(item))
}

@HookReplacer
fun getShedPattern(static: SheddingHandler, entity: Entity): ShedPattern? {
	startFROM()
	POPLine();POP(ALOAD<ShedPattern>("3").EntityClass.isInstance(entity))
	POPLine();startTO()
	POPLine();POP(ALOAD<ShedPattern>("3").EntityClass == entity::class.java)
	POPLine();stop()
	
	return null
}