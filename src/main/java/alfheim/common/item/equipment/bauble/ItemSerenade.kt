package alfheim.common.item.equipment.bauble

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.ModInfo
import alfheim.client.core.helper.IconHelper
import alfheim.client.sound.EntityBoundMovingSound
import alfheim.common.block.magtrees.sealing.EventHandlerSealingOak
import alfheim.common.core.util.AlfheimTab
import baubles.api.BaubleType
import baubles.common.lib.PlayerHandler
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.texture.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderPlayerEvent
import org.lwjgl.opengl.GL11
import vazkii.botania.api.item.IBaubleRender
import vazkii.botania.client.core.handler.ClientTickHandler
import vazkii.botania.common.item.equipment.bauble.ItemBauble
import kotlin.math.sin

class ItemSerenade: ItemBauble("Serenade"), IBaubleRender {
	
	init {
		creativeTab = AlfheimTab
	}
	
	override fun onItemRightClick(stack: ItemStack, world: World?, player: EntityPlayer): ItemStack {
		return if (player.isSneaking) {
			stack.doMusic = !stack.doMusic
			return stack
		} else super.onItemRightClick(stack, world, player)
	}
	
	override fun onWornTick(stack: ItemStack, player: EntityLivingBase?) {
		if (player !is EntityPlayer || player.ticksExisted % 50 != 0) return
		
		if (ASJUtilities.isServer) {
			val world = player.worldObj
			val (x, y, z) = Vector3.fromEntity(player).mf()
			val mult = EventHandlerSealingOak.calculateMultiplier(world, x, y, z)
			if (mult < 1f) return player.heal(0.5f)
			
			getEntitiesWithinAABB(world, EntityLivingBase::class.java, player.boundingBox(16)).forEach {
				it.heal(0.5f)
			}
			
			return
		}
		
		if (!stack.doMusic) return
		
		if (player.commandSenderName in wearers) return
		
		wearers[player.commandSenderName] = EntityBoundMovingSound(player, "${ModInfo.MODID}:calm") {
			if (host.isDead || PlayerHandler.getPlayerBaubles(host)[0]?.item !is ItemSerenade) {
				wearers.remove(host.commandSenderName)
				isDonePlaying = true
				volume = 0f
			}
		}.apply {
			mc.soundHandler.playSound(this)
		}
	}
	
	override fun registerIcons(reg: IIconRegister) {
		itemIcon = IconHelper.forItem(reg, this)
	}
	
	override fun getBaubleType(p0: ItemStack?) = BaubleType.AMULET
	
	override fun onPlayerBaubleRender(stack: ItemStack, event: RenderPlayerEvent, type: IBaubleRender.RenderType) {
		if (type != IBaubleRender.RenderType.BODY) return
		
		mc.renderEngine.bindTexture(TextureMap.locationItemsTexture)
		IBaubleRender.Helper.rotateIfSneaking(event.entityPlayer)
		chestTranslate()
		GL11.glTranslatef(0.2F, -0.2F, -0.45F + sin(ClientTickHandler.total / 20f) * 0.1f)
		GL11.glRotatef(10F, 0F, 0F, 1F)
		val icon = itemIcon
		ItemRenderer.renderItemIn2D(Tessellator.instance, icon.maxU, icon.minV, icon.minU, icon.maxV, icon.iconWidth, icon.iconHeight, 1F / 16F)
	}
	
	fun chestTranslate() {
		GL11.glRotatef(180F, 1F, 0F, 0F)
		GL11.glTranslatef(-0.5F, -0.7F, 0.15F)
	}
	
	companion object {
		
		val wearers = HashMap<String, EntityBoundMovingSound<EntityPlayer>>()
		
		const val TAG_DO_MUSIC = "doMusic"
		
		var ItemStack.doMusic
			get() = ItemNBTHelper.getBoolean(this, TAG_DO_MUSIC, true)
			set(value) = ItemNBTHelper.setBoolean(this, TAG_DO_MUSIC, value)
	}
}
