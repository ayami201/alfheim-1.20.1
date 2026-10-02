package alfheim.common.item.equipment.bauble

import alexsocol.asjlib.*
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.AlfheimCore
import alfheim.api.ModInfo
import alfheim.client.core.helper.IconHelper
import alfheim.common.core.util.AlfheimTab
import alfheim.common.integration.travellersgear.*
import baubles.api.BaubleType
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.common.Optional
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.client.Minecraft
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderPlayerEvent
import org.lwjgl.opengl.GL11
import vazkii.botania.client.lib.LibResources
import vazkii.botania.common.item.equipment.bauble.ItemBauble
import kotlin.math.*

@Optional.Interface(modid = "TravellersGear", iface = "alfheim.common.integration.travellersgear.ITravellersGearSynced", striprefs = true)
open class ItemBaubleGlove(val name: String): ItemBauble(name + if (AlfheimCore.TravellersGearLoaded) "s" else ""), ITravellersGearSynced {
	
	init {
		creativeTab = AlfheimTab
	}
	
	final override fun getBaubleType(stack: ItemStack) =
		if (AlfheimCore.TravellersGearLoaded) null else BaubleType.RING
	
	final override fun getSlot(stack: ItemStack?) = 2
	
	override fun onWornTick(stack: ItemStack, player: EntityLivingBase) {
		super.onWornTick(stack, player)
		ItemNBTHelper.setBoolean(stack, ITravellersGearSynced.TAG_EQUIPPED, true)
	}
	
	override fun onUpdate(stack: ItemStack, world: World, entity: Entity?, slot: Int, inHand: Boolean) {
		ItemNBTHelper.setBoolean(stack, ITravellersGearSynced.TAG_EQUIPPED, false)
	}
	
	override fun addHiddenTooltip(stack: ItemStack, player: EntityPlayer, tooltip: MutableList<Any?>, adv: Boolean) {
		TravellerBaubleTooltipHandler.addHiddenTooltip(this, stack, tooltip)
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		itemIcon = IconHelper.forItem(reg, this)
	}
	
	@SideOnly(Side.CLIENT)
	override fun getArmorTexture(stack: ItemStack?, entity: Entity?, slot: Int, type: String?): String {
		return if (hasPhantomInk(stack)) LibResources.MODEL_INVISIBLE_ARMOR else "${ModInfo.MODID}:textures/model/armor/$name.png"
	}
	
	@SideOnly(Side.CLIENT)
	override fun getArmorModel(entity: EntityLivingBase?, stack: ItemStack?, armorSlot: Int): ModelBiped {
		val mod = model ?: ModelBiped(0.01f, 0.0f, 64, 32).apply {
			model = this
			
			bipedHead.showModel = false
			bipedHeadwear.showModel = false
			bipedBody.showModel = false
			bipedLeftLeg.showModel = false
			bipedRightLeg.showModel = false
			bipedBody.showModel = false
		}
		
		if (entity is EntityPlayer) {
			val baubles = PlayerHandler.getPlayerBaubles(entity)
			mod.bipedRightArm.showModel = !hasPhantomInk(baubles[1]) && (baubles[1]?.item === this || AlfheimCore.TravellersGearLoaded)
			mod.bipedLeftArm.showModel = !hasPhantomInk(baubles[2]) && (baubles[2]?.item === this || AlfheimCore.TravellersGearLoaded)
		}
		
		return mod
	}
	
	companion object {
		
		@field:SideOnly(Side.CLIENT)
		var model: ModelBiped? = null
			@SideOnly(Side.CLIENT)
			get
			@SideOnly(Side.CLIENT)
			set
		
		init {
			if (ASJUtilities.isClient) eventForge()
		}
		
		@SubscribeEvent
		fun renderPlayerSpecialPre(event: RenderPlayerEvent.Specials.Pre) {
			if (AlfheimCore.TravellersGearLoaded) return
			
			val baubs = PlayerHandler.getPlayerBaubles(event.entityPlayer)
			if (!(baubs[1]?.item is ItemBaubleGlove || baubs[2]?.item is ItemBaubleGlove)) return
			
			if (baubs[1]?.item is ItemBaubleGlove) renderTravellersItem(baubs[1]!!, event.entityPlayer, event.renderer, event.partialRenderTick)
			if (baubs[2]?.item is ItemBaubleGlove) renderTravellersItem(baubs[2]!!, event.entityPlayer, event.renderer, event.partialRenderTick)
		}
		
		// copy of travellersgear.client.ClientProxy#renderTravellersItem
		fun renderTravellersItem(stack: ItemStack, player: EntityPlayer, renderer: RenderPlayer, partialRenderTick: Float) {
			GL11.glPushMatrix()
			GL11.glColor4f(1f, 1f, 1f, 1f)
			
			val model = stack.item.getArmorModel(player, stack, 0)
			Minecraft.getMinecraft().textureManager.bindTexture(ResourceLocation(stack.item.getArmorTexture(stack, player, 0, null)))
			
			model.aimedBow = renderer.modelBipedMain.aimedBow
			model.heldItemRight = renderer.modelBipedMain.heldItemRight
			model.heldItemLeft = renderer.modelBipedMain.heldItemLeft
			model.onGround = renderer.modelBipedMain.onGround
			model.isRiding = renderer.modelBipedMain.isRiding
			model.isChild = renderer.modelBipedMain.isChild
			model.isSneak = renderer.modelBipedMain.isSneak
			
			var f2 = ASJRenderHelper.interpolate(player.prevRenderYawOffset.D, player.renderYawOffset.D).F
			val f3 = ASJRenderHelper.interpolate(player.prevRotationYawHead.D, player.rotationYawHead.D).F
			
			var f4: Float
			if (player.isRiding && player.ridingEntity is EntityLivingBase) {
				val riding = player.ridingEntity as EntityLivingBase
				
				f2 = ASJRenderHelper.interpolate(riding.prevRenderYawOffset.D, riding.renderYawOffset.D).F
				f4 = min(85f, max(-85f, MathHelper.wrapAngleTo180_float(f3 - f2)))
				f2 = f3 - f4
				if (f4 * f4 > 2500f) f2 += f4 * 0.2f
			}
			
			val f13 = ASJRenderHelper.interpolate(player.prevRotationPitch.D, player.rotationPitch.D).F
			
			f4 = player.ticksExisted + partialRenderTick
			val f5 = 0.0625f
			val f6 = min(1f, ASJRenderHelper.interpolate(player.prevLimbSwingAmount.D, player.limbSwingAmount.D).F)
			val f7 = (if (player.isChild) 3 else 1) * (player.limbSwing - player.limbSwingAmount * (1f - partialRenderTick))
			model.setLivingAnimations(player, f7, f6, partialRenderTick)
			model.render(player, f7, f6, f4, f3 - f2, f13, f5)
			
			GL11.glPopMatrix()
		}
	}
}