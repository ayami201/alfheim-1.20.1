package alfheim.common.potion

// PORT: импорты 1.20.1 (MAPPING.md); Botania.proxy — alfheim.port.legacy.botania.Botania, Vector3 Botania 1.7.10 —
// Vector3 ASJCore; амулет перекраски и плащ жреца (КТ-4), урон заклинаний (КТ-7) закомментированы вместе со своими строками
import alexsocol.asjlib.mc
import alexsocol.asjlib.math.Vector3
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.port.legacy.*
import alfheim.port.legacy.botania.Botania
import net.minecraft.client.CameraType
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import java.awt.Color
//import alfheim.api.item.ColorOverrideHelper
//import alfheim.common.core.util.DamageSourceSpell
//import alfheim.common.item.equipment.bauble.ItemPriestCloak
//import kotlin.math.min

object PotionLightningShield: PotionAlfheim(AlfheimConfigHandler.potionIDLightningShield, "lightningShield", false, 0x0079C4) {
	
	override fun isReady(time: Int, mod: Int): Boolean {
		return true
	}
	
	override fun performEffect(entity: EntityLivingBase, mod: Int) {
		if (!entity.worldObj.isRemote) return
		if (Math.random() > 0.25 || (mc.gameSettings.thirdPersonView == 0 && mc.thePlayer === entity)) return
		
		val start = Vector3.fromEntity(entity)
		val end = start.copy().add(Math.random() * 2 - 1, Math.random() * 2 - 1, Math.random() * 2 - 1)
		var color = 0x0079C4
		// PORT: КТ-4 — цвет с амулета перекраски (ColorOverrideHelper)
//		if (entity is EntityPlayer) color = ColorOverrideHelper.getColor(entity, color)
		val innerColor = Color(color).brighter().brighter().rgb
		Botania.proxy.lightningFX(entity.worldObj, start, end, 2f, color, innerColor)
	}
	
	@SubscribeEvent
	fun onPlayerAttacked(e: LivingAttackEvent) {
		if (e.source.damageType == "lightningShieldEffect") return // Stack overflow fix
		
		val attacker = e.source.entity as? EntityLivingBase ?: return
		val player = e.entityLiving as? EntityPlayer ?: return
		if (!player.isPotionActive(this.id)) return
		// PORT: КТ-4 — плащ жреца (ItemPriestCloak), КТ-7 — урон молнией (DamageSourceSpell)
//		if (ItemPriestCloak.getCloak(0, player) == null) return
//		
//		attacker.attackEntityFrom(DamageSourceSpell.lightningShield(player), min(e.ammount, 2f))
	}
	
	// PORT: имена полей 1.7.10; вид от первого лица — cameraType FIRST_PERSON
	private val EntityLivingBase.worldObj get() = level()
	private val net.minecraft.client.Options.thirdPersonView get() = if (cameraType == CameraType.FIRST_PERSON) 0 else 1
	private val net.minecraft.client.Minecraft.gameSettings get() = options
	private val net.minecraft.client.Minecraft.thePlayer get() = player
}
