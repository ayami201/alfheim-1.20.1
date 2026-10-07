package alfheim.common.potion

// PORT: импорты 1.20.1 (MAPPING.md)
import alexsocol.asjlib.*
import alfheim.common.block.AlfheimBlocks
import alfheim.common.block.alt.BlockAltLeaves
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.network.NetworkService
import alfheim.common.network.packet.MessageEffect
import alfheim.port.legacy.*
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.ai.attributes.AttributeMap as BaseAttributeMap
import net.minecraft.world.entity.player.Abilities
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraftforge.api.distmarker.*
import net.minecraftforge.client.event.RenderBlockScreenEffectEvent as RenderBlockOverlayEvent
import net.minecraftforge.client.event.RenderHighlightEvent.Block as DrawBlockHighlightEvent
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
//import net.minecraft.util.DamageSource

object PotionNoclip: PotionAlfheim(AlfheimConfigHandler.potionIDNoclip, "noclip", false, 0xAAAAAA) {
	
	init {
		if (ASJUtilities.isClient)
			ClientEventHandler.eventForge()
		
		CommonEventHandler.eventForge()
	}
	
	override fun isReady(time: Int, amp: Int) = AlfheimConfigHandler.enableMMO
	
	override fun performEffect(target: EntityLivingBase, time: Int) {
		if (AlfheimConfigHandler.enableMMO) // hacky shit to forbid noclip through Yggdrasil
			target.noClip = if (target is EntityPlayer)
				if (target.capabilities.isCreativeMode)
					true
				else
					// PORT: вариант metadata — блок массива (SPEC, Р-5): листва Иггдрасиля — altLeaves[yggMeta], древесина —
					// altWood1[2]
					!(target.worldObj.getBlock(target) === AlfheimBlocks.altLeaves[BlockAltLeaves.yggMeta] ||
					 (target.worldObj.getBlock(target) === AlfheimBlocks.altWood1[2]))
//					!(target.worldObj.getBlock(target) === AlfheimBlocks.altLeaves && target.worldObj.getBlockMeta(target) % 8 == BlockAltLeaves.yggMeta ||
//					 (target.worldObj.getBlock(target) === AlfheimBlocks.altWood1  && target.worldObj.getBlockMeta(target) % 4 == 2))
			else
				true
	}
	
	override fun applyAttributesModifiersToEntity(target: EntityLivingBase, attributes: BaseAttributeMap, amp: Int) {
		if (!AlfheimConfigHandler.enableMMO) return
		target.noClip = true
		if (target !is EntityPlayer) return
		target.capabilities.allowFlying = true
		target.capabilities.isFlying = true
		target.onGround = false
		target.sendPlayerAbilities()
	}
	
	override fun removeAttributesModifiersFromEntity(target: EntityLivingBase, attributes: BaseAttributeMap, amp: Int) {
		if (!AlfheimConfigHandler.enableMMO) return
		target.noClip = false
		if (ASJUtilities.isServer) NetworkService.sendToAll(MessageEffect(target.entityId, this.id, 0, 0))
	}
	
	object CommonEventHandler {
		
		@SubscribeEvent
		fun onEntityAttacked(e: LivingAttackEvent) {
			// PORT: DamageSource.inWall, DamageSource.drown — типы урона 1.20.1 с теми же именами (msgId)
			if (!AlfheimConfigHandler.enableMMO || !((e.source.damageType.equals("inWall", true) || e.source.damageType.equals("drown", true)) && e.entityLiving.isPotionActive(AlfheimConfigHandler.potionIDNoclip))) return
//			if (!AlfheimConfigHandler.enableMMO || !((e.source.damageType.equals(DamageSource.inWall.damageType, true) || e.source.damageType.equals(DamageSource.drown.damageType, true)) && e.entityLiving.isPotionActive(AlfheimConfigHandler.potionIDNoclip))) return
			e.isCanceled = true
			return
		}
	}
	
	object ClientEventHandler {
		
		// PORT: RenderBlockOverlayEvent → RenderBlockScreenEffectEvent, DrawBlockHighlightEvent → RenderHighlightEvent.Block
		@SubscribeEvent
		@OnlyIn(Dist.CLIENT)
		fun onBlockOverlay(e: RenderBlockOverlayEvent) {
			if (AlfheimConfigHandler.enableMMO && e.overlayType != RenderBlockOverlayEvent.OverlayType.FIRE)
				e.isCanceled = e.player.isPotionActive(AlfheimConfigHandler.potionIDNoclip)
		}
		
		@SubscribeEvent
		@OnlyIn(Dist.CLIENT)
		fun onWireframeRender(e: DrawBlockHighlightEvent) {
			if (AlfheimConfigHandler.enableMMO && AlfheimConfigHandler.disableWireframe) e.isCanceled = e.player.isPotionActive(AlfheimConfigHandler.potionIDNoclip)
		}
		
		// PORT: у события 1.20.1 нет игрока — это игрок клиента, как в 1.7.10
		private val DrawBlockHighlightEvent.player get() = mc.player!!
	}
	
	// PORT: имена полей 1.7.10
	private var EntityLivingBase.noClip: Boolean
		get() = noPhysics
		set(value) {
			noPhysics = value
		}
	private val EntityPlayer.capabilities get() = abilities
	private var Abilities.allowFlying: Boolean
		get() = mayfly
		set(value) {
			mayfly = value
		}
	private var Abilities.isFlying: Boolean
		get() = flying
		set(value) {
			flying = value
		}
	private var Entity.onGround: Boolean
		get() = onGround()
		set(value) {
			setOnGround(value)
		}
	private fun EntityPlayer.sendPlayerAbilities() = onUpdateAbilities()
	private val Entity.entityId get() = id
}
