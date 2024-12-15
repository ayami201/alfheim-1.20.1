package alfheim.common.item.equipment.bauble

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.api.event.PlayerInteractAdequateEvent
import alfheim.client.core.helper.IconHelper
import alfheim.client.model.armor.ModelBelt
import alfheim.common.core.util.AlfheimTab
import baubles.api.BaubleType
import baubles.common.lib.PlayerHandler
import baubles.common.network.*
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.block.Block
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraftforge.client.*
import net.minecraftforge.client.event.*
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import org.lwjgl.opengl.GL11.*
import vazkii.botania.api.item.*
import vazkii.botania.client.core.handler.ClientTickHandler
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.item.ItemBaubleBox
import vazkii.botania.common.item.equipment.bauble.ItemBauble
import java.awt.Color
import kotlin.math.*

// ItemToolBelt will not become an IManaItem. That's... a bit excessively OP, imo. Store those in your Bauble Case, or keep them in your inventory.
class ItemToolBelt: ItemBauble("toolbelt"), IBaubleRender, IBlockProvider {
	
	companion object {
		
		val glowTexture = ResourceLocation("${ModInfo.MODID}:textures/misc/glow.png")
		val beltTexture = ResourceLocation("${ModInfo.MODID}:textures/model/armor/toolbelt.png")
		
		const val SEGMENTS = 12
		const val TAG_ITEM_PREFIX = "item"
		const val TAG_EQUIPPED = "equipped"
		const val TAG_ROTATION_BASE = "rotationBase"
		
		fun isEquipped(stack: ItemStack): Boolean = ItemNBTHelper.getBoolean(stack, TAG_EQUIPPED, false)
		
		fun setEquipped(stack: ItemStack, equipped: Boolean) = ItemNBTHelper.setBoolean(stack, TAG_EQUIPPED, equipped)
		
		fun getRotationBase(stack: ItemStack): Float = ItemNBTHelper.getFloat(stack, TAG_ROTATION_BASE, 0F)
		
		fun setRotationBase(stack: ItemStack, rotation: Float) = ItemNBTHelper.setFloat(stack, TAG_ROTATION_BASE, rotation)
		
		fun getSegmentLookedAt(stack: ItemStack, player: EntityLivingBase): Int {
			val yaw = getCheckingAngle(player, getRotationBase(stack))
			val angles = 360
			val segAngles = angles / SEGMENTS
			for (seg in 0 until SEGMENTS) {
				val calcAngle = seg.F * segAngles
				if (yaw >= calcAngle && yaw < calcAngle + segAngles)
					return seg
			}
			return 0
		}
		
		fun getCheckingAngle(player: EntityLivingBase): Float = getCheckingAngle(player, 0F)
		
		// Agreed, V, minecraft's rotation is shit. And no roll? Seriously?
		fun getCheckingAngle(player: EntityLivingBase, base: Float): Float {
			var yaw = MathHelper.wrapAngleTo180_float(player.rotationYaw) + 90F
			val angles = 360
			val segAngles = angles / SEGMENTS
			val shift = segAngles / 2
			
			if (yaw < 0)
				yaw = 180F + (180F + yaw)
			yaw -= 360F - base
			var angle = 360F - yaw + shift
			
			if (angle < 0)
				angle += 360F
			
			return angle
		}
		
		fun isLookingAtSegment(player: EntityLivingBase): Boolean {
			val pitch = player.rotationPitch
			
			return pitch > -33.75 && pitch < 45
		}
		
		fun getItemForSlot(stack: ItemStack, slot: Int): ItemStack? {
			if (slot >= SEGMENTS) return null
			val cmp = getStoredCompound(stack, slot) ?: return null
			return ItemStack.loadItemStackFromNBT(cmp)
		}
		
		fun getStoredCompound(stack: ItemStack, slot: Int): NBTTagCompound? = ItemNBTHelper.getCompound(stack, TAG_ITEM_PREFIX + slot, true)
		
		fun setItem(player: EntityPlayer?, beltStack: ItemStack, stack: ItemStack?, pos: Int) {
			if (stack == null) ItemNBTHelper.setCompound(beltStack, TAG_ITEM_PREFIX + pos, NBTTagCompound())
			else {
				val tag = NBTTagCompound()
				stack.writeToNBT(tag)
				ItemNBTHelper.setCompound(beltStack, TAG_ITEM_PREFIX + pos, tag)
			}
			
			if (player?.worldObj?.isRemote == false) PacketHandler.INSTANCE.sendToAll(PacketSyncBauble(player, 3))
		}
		
		fun getEquippedBelt(player: EntityPlayer): ItemStack? {
			val inv = PlayerHandler.getPlayerBaubles(player)
			val belt = inv[3]
			return if (belt?.item is ItemToolBelt) belt else null
		}
	}
	
	init {
		creativeTab = AlfheimTab
		ToolbeltEventHandler.eventForge()
		setHasSubtypes(true)
	}
	
	override fun registerIcons(reg: IIconRegister) {
		itemIcon = IconHelper.forItem(reg, this)
	}
	
	override fun getBlockCount(player: EntityPlayer?, requestor: ItemStack, stack: ItemStack, block: Block, meta: Int): Int {
		var total = 0
		for (segment in 0 until SEGMENTS) {
			val slotStack = getItemForSlot(stack, segment) ?: continue
			
			val slotItem = slotStack.item
			if (slotItem is IBlockProvider) {
				val count = slotItem.getBlockCount(player, requestor, slotStack, block, meta)
				setItem(player, stack, slotStack, segment)
				if (count == -1) return -1
				total += count
			} else if (slotStack.block == block && slotStack.meta == meta) {
				total += slotStack.stackSize
			}
		}
		
		return total
	}
	
	override fun provideBlock(player: EntityPlayer?, requestor: ItemStack, stack: ItemStack, block: Block, meta: Int, doit: Boolean): Boolean {
		for (segment in 0 until SEGMENTS) {
			val slotStack = getItemForSlot(stack, segment) ?: continue
			val slotItem = slotStack.item
			if (slotItem is IBlockProvider) {
				val provided = slotItem.provideBlock(player, requestor, slotStack, block, meta, doit)
				setItem(player, stack, slotStack, segment)
				if (provided) return true
			} else if (slotStack.block == block && slotStack.meta == meta) {
				if (doit) slotStack.stackSize--
				
				if (slotStack.stackSize == 0) setItem(player, stack, null, segment)
				else setItem(player, stack, slotStack, segment)
				return true
			}
		}
		
		return false
	}
	
	override fun addHiddenTooltip(stack: ItemStack, player: EntityPlayer?, list: MutableList<Any?>, adv: Boolean) {
		val map = HashMap<String, Int>()
		for (segment in 0 until SEGMENTS) {
			val slotStack = getItemForSlot(stack, segment) ?: continue
			
			var base = 0
			val name = slotStack.displayName
			val node = map[name]
			if (node != null) base = node
			map[name] = base + slotStack.stackSize
		}
		
		if (map.size > 0) list.add("${EnumChatFormatting.AQUA}" + StatCollector.translateToLocal("misc.${ModInfo.MODID}.contains"))
		else list.add("${EnumChatFormatting.AQUA}" + StatCollector.translateToLocal("misc.${ModInfo.MODID}.containsNothing"))
		
		val keys = ArrayList(map.keys)
		keys.sort()
		keys.mapTo(list) { "${map[it]}x ${EnumChatFormatting.WHITE}$it" }
		
		super.addHiddenTooltip(stack, player, list, adv)
	}
	
	override fun getBaubleType(stack: ItemStack) = BaubleType.BELT
	
	override fun onWornTick(stack: ItemStack, player: EntityLivingBase) {
		if (player !is EntityPlayer) return
		
		val eqLastTick = isEquipped(stack)
		val eq = player.isSneaking && isLookingAtSegment(player)
		if (eqLastTick != eq)
			setEquipped(stack, eq)
		
		if (!player.isSneaking) {
			val angles = 360
			val segAngles = angles / SEGMENTS
			val shift = segAngles / 2
			setRotationBase(stack, getCheckingAngle(player) - shift)
		}
	}
	
	override fun getUnlocalizedNameInefficiently(par1ItemStack: ItemStack) =
		super.getUnlocalizedNameInefficiently(par1ItemStack).replace("item\\.botania:".toRegex(), "item.${ModInfo.MODID}:")
	
	@SideOnly(Side.CLIENT)
	fun getGlowResource(): ResourceLocation = glowTexture
	
	@SideOnly(Side.CLIENT)
	override fun onPlayerBaubleRender(stack: ItemStack, event: RenderPlayerEvent, type: IBaubleRender.RenderType) {
		if (type != IBaubleRender.RenderType.BODY) return
		
		mc.renderEngine.bindTexture(beltTexture)
		IBaubleRender.Helper.rotateIfSneaking(event.entityPlayer)
		glTranslatef(0F, 0.2F, 0F)
		val s = 1.05F / 16F
		glScalef(s, s, s)
		ModelBelt.bipedBody.render(1F)
	}
}

/**
 * @author WireSegal
 * Created at 2:59 PM on 1/23/16.
 */
object ToolbeltEventHandler {
	
	@SubscribeEvent
	fun cancelRightclicks(event: PlayerInteractEvent) {
		if (event.action !== PlayerInteractEvent.Action.RIGHT_CLICK_AIR) return
		
		val player = event.entityPlayer
		val beltStack = ItemToolBelt.getEquippedBelt(player)
		if (beltStack == null || !ItemToolBelt.isEquipped(beltStack)) return
		
		val toolStack = ItemToolBelt.getItemForSlot(beltStack, ItemToolBelt.getSegmentLookedAt(beltStack, player))
		val heldItem = player.heldItem
		
		if (toolStack != null || (heldItem != null && heldItem.item !is ItemToolBelt && heldItem.item !is ItemBaubleBox))
			event.isCanceled = true
	}
	
	@SubscribeEvent
	fun onPlayerInteract(event: PlayerInteractAdequateEvent.RightClick) {
		if (event.action !== PlayerInteractAdequateEvent.RightClick.Action.RIGHT_CLICK_AIR) return
		if (ASJUtilities.isClient) return
		
		val player = event.player
		val beltStack = ItemToolBelt.getEquippedBelt(player)
		if (beltStack == null || !ItemToolBelt.isEquipped(beltStack)) return
		
		val segment = ItemToolBelt.getSegmentLookedAt(beltStack, player)
		val toolStack = ItemToolBelt.getItemForSlot(beltStack, segment)
		val heldItem = player.heldItem
		
		if (toolStack == null) {
			if (heldItem != null && heldItem.item !is ItemToolBelt && heldItem.item !is ItemBaubleBox) {
				val item = heldItem.copy()
				
				player.inventory[player.inventory.currentItem] = null
				player.inventory.markDirty()
				
				ItemToolBelt.setItem(player, beltStack, item, segment)
			}
		} else {
			ItemToolBelt.setItem(player, beltStack, null, segment)
			
			if (heldItem == null)
				player.setCurrentItemOrArmor(0, toolStack)
			else if (!player.inventory.addItemStackToInventory(toolStack))
				player.dropPlayerItemWithRandomChoice(toolStack, false)
			
			player.inventory.markDirty()
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	fun onRenderWorldLast(event: RenderWorldLastEvent) {
		val player = mc.thePlayer
		val beltStack = ItemToolBelt.getEquippedBelt(player) ?: return
		
		if (ItemToolBelt.isEquipped(beltStack))
			render(beltStack, player, event.partialTicks)
	}
	
	@SideOnly(Side.CLIENT)
	fun render(stack: ItemStack, player: EntityPlayer, partialTicks: Float) {
		val mc = mc
		val tess = Tessellator.instance
		Tessellator.renderingWorldRenderer = false
		
		glPushMatrix()
		glEnable(GL_BLEND)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
		val alpha = (sin((ClientTickHandler.ticksInGame + partialTicks).D * 0.2) * 0.5F + 0.5F) * 0.4F + 0.3F
		val posX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks
		val posY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks
		val posZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks
		
		glTranslated(posX - RenderManager.renderPosX, posY - RenderManager.renderPosY, posZ - RenderManager.renderPosZ)
		val base = ItemToolBelt.getRotationBase(stack)
		val angles = 360
		val segAngles = angles / ItemToolBelt.SEGMENTS
		val shift = base - segAngles / 2
		val u = 1F
		val v = 0.25F
		val s = 3F
		val m = 0.8F
		val y = v * s * 2
		var y0 = 0.0
		val segmentLookedAt = ItemToolBelt.getSegmentLookedAt(stack, player)
		
		for (seg in 0 until ItemToolBelt.SEGMENTS) {
			var inside = false
			val rotationAngle = (seg + 0.5F) * segAngles + shift
			glPushMatrix()
			glRotatef(rotationAngle, 0F, 1F, 0F)
			glTranslatef(s * m, -0.75F, 0F)
			
			if (segmentLookedAt == seg)
				inside = true
			val slotStack = ItemToolBelt.getItemForSlot(stack, seg)
			if (slotStack != null) {
				mc.renderEngine.bindTexture(if (slotStack.item is ItemBlock) TextureMap.locationBlocksTexture else TextureMap.locationItemsTexture)
				
				if (slotStack.item is ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(slotStack.item).renderType)) {
					glScalef(0.6F, 0.6F, 0.6F)
					glRotatef(180F, 0F, 1F, 0F)
					glTranslatef(0F, 0.6F, 0F)
					
					RenderBlocks.getInstance().renderBlockAsItem(Block.getBlockFromItem(slotStack.item), slotStack.meta, 1F)
				} else if (slotStack.item is ItemBlock || MinecraftForgeClient.getItemRenderer(slotStack, IItemRenderer.ItemRenderType.ENTITY) != null) {
					var entityitem: EntityItem?
					glPushMatrix()
					
					if (slotStack.item is ItemBlock) {
						glScalef(1F, 1F, 1F)
					} else {
						glScalef(1.5F, 1.5F, 1.5F)
						glTranslatef(0F, -0.125F, 0F)
					}
					glRotatef(90F, 0F, 1F, 0F)
					val `is` = slotStack.copy()
					`is`.stackSize = 1
					entityitem = EntityItem(player.worldObj, 0.0, 0.0, 0.0, `is`)
					entityitem.hoverStart = 0f
					
					glEnable(GL_BLEND)
					glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
					RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0, 0.0, 0.0, 0f, 0f)
					glDisable(GL_BLEND)
					
					glPopMatrix()
				} else {
					glScalef(0.75F, 0.75F, 0.75F)
					glTranslatef(0F, 0F, 0.5F)
					glRotatef(90F, 0F, 1F, 0F)
					var renderPass = 0
					
					do {
						val icon = slotStack.item.getIcon(slotStack, renderPass)
						if (icon != null) {
							val color = Color(slotStack.item.getColorFromItemStack(slotStack, renderPass))
							glColor3ub(color.red.toByte(), color.green.toByte(), color.blue.toByte())
							val f = icon.minU
							val f1 = icon.maxU
							val f2 = icon.minV
							val f3 = icon.maxV
							
							ItemRenderer.renderItemIn2D(Tessellator.instance, f1, f2, f, f3, icon.iconWidth, icon.iconHeight, 0.0625f)
							glColor3f(1f, 1f, 1f)
						}
						
						++renderPass
					} while (renderPass < slotStack.item.getRenderPasses(slotStack.meta))
				}
			}
			glPopMatrix()
			
			glPushMatrix()
			glRotatef(180F, 1F, 0F, 0F)
			var a = alpha.F
			if (inside) {
				a += 0.3F
				y0 = -y.D
			}
			
			if (seg % 2 == 0)
				glColor4f(0.6F, 0.6F, 0.6F, a)
			else {
				val (r, g, b) = Color(0x9F7F62).getColorComponents(null)
				glColor4f(r, g, b, a)
			}
			
			glDisable(GL_CULL_FACE)
			val item = stack.item as ItemToolBelt
			glDisable(GL_LIGHTING)
			glEnable(GL_BLEND)
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
			mc.renderEngine.bindTexture(item.getGlowResource())
			tess.startDrawingQuads()
			for (i in 0 until segAngles) {
				val ang = i + seg * segAngles + shift
				var xp = cos(ang * Math.PI / 180F) * s
				var zp = sin(ang * Math.PI / 180F) * s
				
				tess.addVertexWithUV(xp * m, y.D, zp * m, u.D, v.D)
				tess.addVertexWithUV(xp, y0, zp, u.D, 0.0)
				
				xp = cos((ang + 1) * Math.PI / 180F) * s
				zp = sin((ang + 1) * Math.PI / 180F) * s
				
				tess.addVertexWithUV(xp, y0, zp, 0.0, 0.0)
				tess.addVertexWithUV(xp * m, y.D, zp * m, 0.0, v.D)
			}
			y0 = 0.0
			tess.draw()
			
			glColor4f(1f, 1f, 1f, 1f)
			glDisable(GL_BLEND)
			glEnable(GL_CULL_FACE)
			
			glPopMatrix()
		}
		glPopMatrix()
	}
}