package alfheim.common.item

import alexsocol.asjlib.*
import alfheim.api.ModInfo
import alfheim.api.lib.LibResourceLocations
import alfheim.client.core.helper.IconHelper
import alfheim.client.render.entity.RenderEntityWarBanner
import alfheim.common.entity.EntityWarBanner
import baubles.api.BaubleType
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.boss.IBossDisplayData
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.util.IIcon
import net.minecraftforge.client.event.RenderPlayerEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import org.lwjgl.opengl.GL11.*
import vazkii.botania.api.item.*
import vazkii.botania.common.item.equipment.bauble.ItemBauble

class ItemWarBanner: ItemBauble("WarBanner"), ICosmeticBauble {
	
	lateinit var iconAlt: IIcon
	
	init {
		setHasSubtypes(true)
		setMaxStackSize(1)
	}
	
	override fun getSubItems(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>) {
		super.getSubItems(item, tab, list)
		list.add(ItemStack(item, 1, 1))
	}
	
	override fun getIconFromDamage(meta: Int) = (if (meta == 0) itemIcon else iconAlt)!!
	
	override fun registerIcons(reg: IIconRegister) {
		itemIcon = IconHelper.forItem(reg, this)
		iconAlt = IconHelper.forItem(reg, this, "Alt")
	}
	
	override fun getBaubleType(p0: ItemStack?) = BaubleType.AMULET
	
	override fun onPlayerBaubleRender(stack: ItemStack, event: RenderPlayerEvent, type: IBaubleRender.RenderType) {
		if (type != IBaubleRender.RenderType.BODY) return
		
		glRotatef(180f, 1f, 0f, 0f)
		glTranslatef(0.03125f, -0.75f, -0.1f)
		if (event.entityPlayer.isSneaking)
			glTranslatef(0f, 0.2f, -0.35f)
			
		glRotatef(-20f, 1f, 0f, 0f)
		IBaubleRender.Helper.rotateIfSneaking(event.entityPlayer)
		
		mc.renderEngine.bindTexture(if (stack.meta == 0) LibResourceLocations.warBanner else LibResourceLocations.warBannerAlt)
		RenderEntityWarBanner.model.renderAll()
	}
	
	companion object {
		
		init {
			eventForge()
		}
		
		@SubscribeEvent
		fun onEntityKill(e: LivingDeathEvent) {
			if (e.entity.worldObj.isRemote) return
			
			val victim = e.entity
			if (victim !is IBossDisplayData && victim !is EntityPlayer) return
			val killer = e.source.entity as? EntityPlayer ?: return
			
			if (victim === killer) return // no suicide celebration
			
			val meta: Int
			val slot = ASJUtilities.getSlotWithItem(AlfheimItems.warBanner, killer.inventory)
			
			if (slot != -1) {
				meta = killer.inventory[slot]?.meta ?: 0
			} else run baubs@ {
				val baubs = PlayerHandler.getPlayerBaubles(killer)
				
				for (i in 0 until baubs.sizeInventory) {
					val baub = baubs[i] ?: continue
					val item = baub.item
					
					if (item is ItemWarBanner) {
						meta = baub.meta
						return@baubs
					} else if (item is ICosmeticAttachable) {
						val cosm = item.getCosmeticItem(baub)
						if (cosm?.item !is ItemWarBanner) continue
						meta = cosm.meta
						return@baubs
					}
				}
				
				return
			}
			
			EntityWarBanner(victim.worldObj).apply {
				type = meta == 0
				setPosition(victim)
				air = (killer.rotationYaw * 10).I
				playSoundAtEntity("${ModInfo.MODID}:warbanner", 1f, 1f)
				spawn()
			}
		}
	}
}
