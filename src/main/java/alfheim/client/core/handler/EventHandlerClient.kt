package alfheim.client.core.handler

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.render.ASJRenderHelper
import alexsocol.patcher.event.EntityUpdateEvent
import alfheim.api.AlfheimAPI
import alfheim.api.entity.raceID
import alfheim.api.lib.LibResourceLocations
import alfheim.client.core.handler.CardinalSystemClient.PlayerSegmentClient
import alfheim.client.core.handler.CardinalSystemClient.SpellCastingSystemClient
import alfheim.client.core.handler.CardinalSystemClient.TimeStopSystemClient
import alfheim.client.gui.ItemsRemainingRenderHandler
import alfheim.client.render.entity.*
import alfheim.client.render.item.RenderItemFlugelHead
import alfheim.client.render.particle.*
import alfheim.client.render.world.*
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.helper.ContributorsPrivacyHelper
import alfheim.common.crafting.recipe.RecipeSaveIvy
import alfheim.common.item.equipment.bauble.ItemElvenDisguise
import alfheim.common.network.NetworkService
import alfheim.common.network.packet.MessageKeyBindS
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.common.gameevent.TickEvent.*
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.block.material.Material
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.renderer.*
import net.minecraft.entity.boss.IBossDisplayData
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraftforge.client.event.*
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent
import net.minecraftforge.event.entity.player.*
import org.lwjgl.opengl.GL11.*
import vazkii.botania.client.core.handler.ClientTickHandler
import vazkii.botania.common.Botania
import vazkii.botania.common.item.ModItems
import vazkii.botania.common.item.equipment.bauble.ItemMonocle

object EventHandlerClient {
	
	init {
		eventForge().eventFML()
		AstrolabePreviewHandler.eventForge()
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun onDrawScreenPre(event: RenderGameOverlayEvent.Pre) {
		if (event.type === ElementType.BOSSHEALTH && AlfheimConfigHandler.enableMMO)
			event.isCanceled = true
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun onDisconnect(e: ClientDisconnectionFromServerEvent) {
		TimeStopSystemClient.clear()
		PlayerSegmentClient.party = null
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun onEntityUpdate(e: EntityUpdateEvent) {
		if (ASJUtilities.isClient && TimeStopSystemClient.affected(e.entity)) e.isCanceled = true
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun onEntityUpdate(e: LivingUpdateEvent) {
		if (ASJUtilities.isClient && TimeStopSystemClient.affected(e.entity)) {
			e.isCanceled = true
			return
		}
		
		val entity = e.entityLiving
		val world = entity.worldObj
		if (world.provider.dimensionId != AlfheimConfigHandler.dimensionIDAlfheim || !entity.onGround || (entity.motionX == 0.0 && entity.motionZ == 0.0) || world.worldTime % 24000 !in 13333..22666 || world.getBlock(entity, y = if (entity.isSneaking) 0 else -1) !== Blocks.grass) return
		
		for (i in 0..when { entity.isSneaking -> 0; entity.isSprinting -> 2; else -> 1 }) {
			val (x, y, z) = Vector3().rand().mul(entity.width, 0.1, entity.width).add(Vector3.fromEntity(entity)).sub(entity.width / 2, 0, entity.width / 2)
			Botania.proxy.sparkleFX(world, x, y, z, Math.random().F * 0.1f, Math.random().F + 0.5f, Math.random().F * 0.25f, Math.random().F * 0.25F + 0.5F, 3)
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun onClientTick(e: ClientTickEvent) {
		if (e.phase != Phase.END) return
		
		if (mc.thePlayer == null) PlayerSegmentClient.target = null
		
		if (mc.isGamePaused) return
		
		if (PlayerSegmentClient.target !== mc.thePlayer && PlayerSegmentClient.target?.isInvisibleToPlayer(mc.thePlayer) == true) {
			PlayerSegmentClient.target = null
			NetworkService.sendToServer(MessageKeyBindS(KeyBindingHandlerClient.KeyBindingIDs.SEL.ordinal, false, -1))
		}
		
		RenderWings.spawnQueuedParticles()
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun onPlayerPreRender(e: RenderPlayerEvent.Pre) {
		if (AlfheimConfigHandler.enableMMO && e.entityPlayer.isPotionActive(AlfheimConfigHandler.potionIDLeftFlame)) {
			e.isCanceled = true
			return
		}
		
		RenderItemFlugelHead.render(e, e.entityPlayer)
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	@SideOnly(Side.CLIENT)
	fun onPlayerSpecialPreRender(e: RenderPlayerEvent.Specials.Pre) {
		val player = e.entityPlayer as AbstractClientPlayer
		bindCustomSkin(player)
		RenderEntityLeftHand.render(e)
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	@SideOnly(Side.CLIENT)
	fun onHandRender(e: RenderHandEvent) {
		bindCustomSkin(mc.thePlayer)
	}
	
	fun bindCustomSkin(player: AbstractClientPlayer) {
		val disguise = ItemElvenDisguise.getDisguise(player)
		
		if (disguise == null && ContributorsPrivacyHelper.isCorrect(player, "AlexSocol"))
			player.func_152121_a(Type.SKIN, LibResourceLocations.skin)
		
		if (!AlfheimConfigHandler.enableElvenStory && disguise == null) return
		
		val data = if (disguise != null)
			(ItemElvenDisguise.getGurl(player) ?: false) to true
		else
			CardinalSystemClient.playerSkinsData[player.commandSenderName]
			?: return
		
		val raceID = disguise?.ordinal ?: player.raceID
		if (raceID == 0 || raceID > 9) return
		
		if (data.second) {
			player.func_152121_a(Type.SKIN,
			                     (if (data.first)
									 LibResourceLocations.oldFemale
								 else
									 LibResourceLocations.oldMale)
				                 [raceID - 1]
			)
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun onPlayerSpecialPostRender(e: RenderPlayerEvent.Specials.Post) {
		RenderItemFlugelHead.render(e, e.entityPlayer)
		RenderBooba.render(e.entityPlayer)
		RenderWings.render(e.entityPlayer)
		RenderContributors.render(e, e.entityPlayer)
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun onPlayerTick(e: PlayerTickEvent) {
		val player = e.player
		if (player !== mc.thePlayer) return
		
		if (e.phase == Phase.START && e.side == Side.CLIENT && !mc.isGamePaused) {
			KeyBindingHandlerClient.parseKeybindings(player)
			SpellCastingSystemClient.tick()
			
			if (player != null) {
				val tg = PlayerSegmentClient.target
				if (tg != null) {
					if (!tg.isEntityAlive || Vector3.entityDistance(player, tg) > (if (tg is IBossDisplayData) 128 else 32)) PlayerSegmentClient.target = null
				} else if (PlayerSegmentClient.partyIndex > 0) run {
					val mr = PlayerSegmentClient.party?.get(PlayerSegmentClient.partyIndex) ?: return@run
					if (!mr.isEntityAlive || Vector3.entityDistance(player, mr) > (if (mr is IBossDisplayData) 128 else 32)) return@run
					PlayerSegmentClient.target = mr
				}
			}
		}
		if (e.phase == Phase.END) {
			ItemsRemainingRenderHandler.tick()
		}
	}
	
	private fun renderMMO() {
		run {
			val spell = AlfheimAPI.getSpellByIDs(KeyBindingHandlerClient.raceID, KeyBindingHandlerClient.spellID)
						?: return@run
			if (SpellCastingSystemClient.getCoolDown(spell) > 0) return@run
			
			glPushMatrix()
			glColor4f(1f, 1f, 1f, 1f)
			ASJRenderHelper.interpolatedTranslationReverse(mc.thePlayer)
			spell.render(mc.thePlayer)
			glPopMatrix()
		}
		
		run {
			val target = PlayerSegmentClient.target ?: return@run
			if (target == mc.thePlayer && mc.gameSettings.thirdPersonView == 0) return@run
			
			glPushMatrix()
			
			glAlphaFunc(GL_GREATER, 1 / 255f)
			ASJRenderHelper.setBlend()
			ASJRenderHelper.setGlow()
			ASJRenderHelper.setTwoside()
			
			if (target != mc.thePlayer) {
				ASJRenderHelper.interpolatedTranslationReverse(mc.thePlayer)
				ASJRenderHelper.interpolatedTranslation(target)
			} else {
				glTranslated(0.0, -(1.5 + mc.thePlayer.eyeHeight), 0.0)
			}
			
			glRotatef(ClientTickHandler.total, 0f, 1f, 0f)
			glScalef(target.width)
			
			ASJRenderHelper.glColor1u(if (PlayerSegmentClient.isParty) 0xFF00FF00U else 0xFFFF0000U)
			mc.renderEngine.bindTexture(LibResourceLocations.cross)
			Tessellator.instance.startDrawingQuads()
			Tessellator.instance.addVertexWithUV(-1.0, 0.1, -1.0, 0.0, 0.0)
			Tessellator.instance.addVertexWithUV(-1.0, 0.1, 1.0, 0.0, 1.0)
			Tessellator.instance.addVertexWithUV(1.0, 0.1, 1.0, 1.0, 1.0)
			Tessellator.instance.addVertexWithUV(1.0, 0.1, -1.0, 1.0, 0.0)
			Tessellator.instance.draw()
			
			glColor4f(1f, 1f, 1f, 1f)
			ASJRenderHelper.discard()
			glAlphaFunc(GL_GREATER, 0.1f)
			glPopMatrix()
		}
		
		TimeStopSystemClient.render()
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun onClonePlayer(e: PlayerEvent.Clone) {
		if (AlfheimConfigHandler.enableElvenStory) {
			e.entityPlayer.raceID = e.original.raceID
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun onFOV(e: FOVUpdateEvent) {
		if (AlfheimConfigHandler.enableMMO && e.entity.getActivePotionEffect(AlfheimConfigHandler.potionIDIceLens) != null) e.newfov = 0.1f
	}
	
	@SubscribeEvent(receiveCanceled = true)
	@SideOnly(Side.CLIENT)
	fun onFog(e: EntityViewRenderEvent.FogDensity) {
		val rve = mc.renderViewEntity
		if (rve is EntityPlayer && rve.capabilities.isCreativeMode || !AlfheimConfigHandler.enableMMO || !rve.isPotionActive(AlfheimConfigHandler.potionIDNoclip) || e.block.material !== Material.water && e.block.material !== Material.lava) return
		glFogi(GL_FOG_MODE, GL_EXP)
		e.density = 0.05f
		e.isCanceled = true
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun onRenderWorldLastEvent(e: RenderWorldLastEvent) {
		if (AlfheimConfigHandler.enableMMO) renderMMO()
		
		glAlphaFunc(GL_GREATER, 0f)
		
		FenrirVisualEffectsRenderer.renderAll(e.partialTicks)
		renderParticles(e.partialTicks.D)
		
		glAlphaFunc(GL_GREATER, 0.003921569f)
	}
	
	fun renderParticles(ticks: Double) {
		mc.entityRenderer.enableLightmap(ticks)
		RenderHelper.disableStandardItemLighting()
		glColor4f(1f, 1f, 1f, 1f)
		glDepthMask(false)
		glEnable(GL_BLEND)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
		mc.mcProfiler.startSection("wingParticles")
		EntityFeatherFx.renderQueue()
		mc.mcProfiler.endStartSection("bloodParticles")
		EntityBloodFx.renderQueue()
		mc.mcProfiler.endStartSection("voxelParticles")
		EntityVoxelFX.renderQueue()
		mc.mcProfiler.endSection()
		glDisable(GL_BLEND)
		glDepthMask(true)
		mc.entityRenderer.disableLightmap(ticks)
	}
	
	@Suppress("UNCHECKED_CAST")
	@SubscribeEvent
	fun drawTooltip(e: ItemTooltipEvent) {
		val stack = e.itemStack ?: return
		
		if (stack.item === ModItems.laputaShard && ItemMonocle.hasMonocle(mc.thePlayer))
			addStringToTooltip(e.toolTip as MutableList<Any?>, "misc.alfheim.customSize", (14 + stack.meta).toString())
		
		if (ItemNBTHelper.getBoolean(stack, RecipeSaveIvy.TAG_SAVE, false))
			addStringToTooltip(e.toolTip as MutableList<Any?>, "alfheimmisc.saveIvy", (14 + stack.meta).toString())
	}
}