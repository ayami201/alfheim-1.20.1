package alfheim.common.potion

// PORT: импорты 1.20.1 (MAPPING.md); заклинание, его урон и метка цели на экране (КТ-7) закомментированы вместе со
// своими строками
import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.network.*
import alfheim.common.network.packet.Message2d
import alfheim.port.legacy.*
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.ai.attributes.AttributeMap as BaseAttributeMap
import net.minecraftforge.event.entity.EntityJoinLevelEvent as EntityJoinWorldEvent
import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.eventbus.api.*
import java.util.*
//import alexsocol.asjlib.render.ASJRenderHelper
//import alfheim.api.lib.LibResourceLocations
//import alfheim.common.core.util.DamageSourceSpell
//import alfheim.common.spell.sound.SpellPriorityTarget
//import cpw.mods.fml.relauncher.*
//import net.minecraft.client.renderer.Tessellator
//import net.minecraft.client.renderer.entity.RenderManager
//import net.minecraft.entity.*
//import net.minecraftforge.client.event.RenderWorldLastEvent
//import org.lwjgl.opengl.GL11.*
//import org.lwjgl.opengl.GL12

object PotionPriorityTarget: PotionAlfheim(AlfheimConfigHandler.potionIDPriorityTarget, "priorityTarget", false, 0x004DFF) {
	
	const val TAG_PT = "${ModInfo.MODID}.PriorityTarget"
	
	// PORT: entityData (NBT существа Forge 1.7.10) → persistentData, uniqueID → uuid
	fun hasPriorityTarget(target: EntityLivingBase) = target.persistentData.hasKey(TAG_PT)
	
	fun getPriorityTarget(target: EntityLivingBase) = target.persistentData.getString(TAG_PT)!!
	
	fun applyTo(target: EntityLivingBase, pm: EntityLivingBase, duration: Int) {
		pm.addPotionEffect(PotionEffectU(id, duration))
		val uuid = target.uuid
		pm.persistentData.setString(TAG_PT, uuid.toString())
		
		if (pm is EntityPlayerMP)
			NetworkService.sendTo(Message2d(M2d.PRIOTGT, Double.fromBits(uuid.mostSignificantBits), Double.fromBits(uuid.leastSignificantBits)), pm)
	}
	
	override fun removeAttributesModifiersFromEntity(target: EntityLivingBase, map: BaseAttributeMap?, mod: Int) {
		super.removeAttributesModifiersFromEntity(target, map, mod)
		target.persistentData.removeTag(TAG_PT)
		
		if (target is EntityPlayerMP)
			NetworkService.sendTo(Message2d(M2d.PRIOTGT, 0.0, 0.0), target)
	}
	
	@SubscribeEvent
	fun transferTarget(e: EntityJoinWorldEvent) {
		val player = e.entity as? EntityPlayerMP ?: return
		if (!hasPriorityTarget(player)) return
		
		val uuid = UUID.fromString(getPriorityTarget(player))
		NetworkService.sendTo(Message2d(M2d.PRIOTGT, Double.fromBits(uuid.mostSignificantBits), Double.fromBits(uuid.leastSignificantBits)), player)
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	fun onPriorityTarget(e: LivingHurtEvent) {
		val attacker = e.source.entity as? EntityLivingBase ?: return
		if (!hasPriorityTarget(attacker)) return
		
		// PORT: КТ-7 — урон по цели и мимо неё силой заклинания «Приоритетная цель» (SpellPriorityTarget, DamageSourceSpell)
		/*
		val target = getPriorityTarget(attacker)
		val victim = e.entityLiving.uniqueID.toString()
		
		if (target == victim) {
			e.ammount *= 1 + SpellPriorityTarget.efficiency.F
			attacker.heal(e.ammount * SpellPriorityTarget.efficiency.F)
		} else {
			e.ammount *= 1 - SpellPriorityTarget.efficiency.F
			
			val prev = attacker.hurtResistantTime
			attacker.hurtResistantTime = 0
			attacker.attackEntityFrom(DamageSourceSpell.notPriorityTarget, e.ammount * SpellPriorityTarget.efficiency.F)
			attacker.hurtResistantTime = prev
		}
		*/
	}
	
	// PORT: КТ-7 — метка цели на экране (рендер 1.20.1: RenderLevelStageEvent, PoseStack)
	/*
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	fun renderTargetMarker(e: RenderWorldLastEvent) {
		if (!hasPriorityTarget(mc.thePlayer)) return
		
		val target = getPriorityTarget(mc.thePlayer)
		val entity = mc.theWorld.loadedEntityList.find {
			(it as Entity).uniqueID.toString() == target
		} as? Entity ?: return
		
		glPushMatrix()
		
		ASJRenderHelper.interpolatedTranslationReverse(mc.thePlayer)
		ASJRenderHelper.interpolatedTranslation(entity)
		glTranslatef(0f, entity.height / 2, 0f)
		
		glDisable(GL_DEPTH_TEST)
		glEnable(GL12.GL_RESCALE_NORMAL)
		ASJRenderHelper.setBlend()
		ASJRenderHelper.setGlow()
		
		mc.renderEngine.bindTexture(LibResourceLocations.spell("priorityTarget"))
		
		val tes = Tessellator.instance
		glRotatef(180f - RenderManager.instance.playerViewY, 0f, 1f, 0f)
		glRotatef(-RenderManager.instance.playerViewX, 1f, 0f, 0f)
		tes.startDrawingQuads()
		tes.setNormal(0f, 1f, 0f)
		val s = 0.25
		tes.addVertexWithUV(-s, -s, 0.0, 0.0, 1.0)
		tes.addVertexWithUV(s, -s, 0.0, 1.0, 1.0)
		tes.addVertexWithUV(s, s, 0.0, 1.0, 0.0)
		tes.addVertexWithUV(-s, s, 0.0, 0.0, 0.0)
		tes.draw()
		
		ASJRenderHelper.discard()
		
		glEnable(GL_DEPTH_TEST)
		glPopMatrix()
	}
	*/
}
