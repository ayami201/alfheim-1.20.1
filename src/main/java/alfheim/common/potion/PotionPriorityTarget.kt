package alfheim.common.potion

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.ModInfo
import alfheim.api.lib.LibResourceLocations
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.DamageSourceSpell
import alfheim.common.network.*
import alfheim.common.network.packet.Message2d
import alfheim.common.spell.sound.SpellPriorityTarget
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.entity.*
import net.minecraft.entity.ai.attributes.BaseAttributeMap
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.living.LivingHurtEvent
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12
import java.util.*

object PotionPriorityTarget: PotionAlfheim(AlfheimConfigHandler.potionIDPriorityTarget, "priorityTarget", false, 0x004DFF) {
	
	const val TAG_PT = "${ModInfo.MODID}.PriorityTarget"
	
	fun hasPriorityTarget(target: EntityLivingBase) = target.entityData.hasKey(TAG_PT)
	
	fun getPriorityTarget(target: EntityLivingBase) = target.entityData.getString(TAG_PT)!!
	
	fun applyTo(target: EntityLivingBase, pm: EntityLivingBase, duration: Int) {
		pm.addPotionEffect(PotionEffectU(id, duration))
		val uuid = target.uniqueID
		pm.entityData.setString(TAG_PT, uuid.toString())
		
		if (pm is EntityPlayerMP)
			NetworkService.sendTo(Message2d(M2d.PRIOTGT, Double.fromBits(uuid.mostSignificantBits), Double.fromBits(uuid.leastSignificantBits)), pm)
	}
	
	override fun removeAttributesModifiersFromEntity(target: EntityLivingBase, map: BaseAttributeMap?, mod: Int) {
		super.removeAttributesModifiersFromEntity(target, map, mod)
		target.entityData.removeTag(TAG_PT)
		
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
	}
	
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
}
