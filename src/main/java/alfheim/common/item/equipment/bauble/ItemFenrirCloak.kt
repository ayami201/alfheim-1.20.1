package alfheim.common.item.equipment.bauble

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.AlfheimCore
import alfheim.api.item.ISpeedUpItem
import alfheim.api.lib.LibResourceLocations
import alfheim.common.core.handler.SheerColdHandler
import alfheim.common.item.AlfheimItems
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.common.eventhandler.*
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.potion.*
import net.minecraft.util.IIcon
import net.minecraftforge.event.entity.living.LivingAttackEvent
import travellersgear.api.TravellersGearAPI
import vazkii.botania.client.core.helper.IconHelper
import kotlin.math.min

class ItemFenrirCloak: ItemBaubleCloak("FenrirCloak"), ISpeedUpItem {
	
	lateinit var overlay: IIcon
	
	override fun getSpeedUp(wearer: EntityLivingBase, stack: ItemStack): Float {
		val world = wearer.worldObj
		
		if (!world.isRaining) return 0f
		
		val (x, y, z) = Vector3.fromEntity(wearer).mf()
		val precipitationHeight = world.getPrecipitationHeight(x, z)
		
		if (y < precipitationHeight) return 0f
		
		if (world.worldChunkManager.getTemperatureAtHeight(world.getBiomeGenForCoords(x, z).getFloatTemperature(x, y, z), precipitationHeight) >= 0.15f) return 0f
		
		return 0.05f
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		super.registerIcons(reg)
		overlay = IconHelper.forItem(reg, this, "1")
	}
	
	override fun requiresMultipleRenderPasses() = true
	
	override fun getRenderPasses(metadata: Int) = 3
	
	override fun getIcon(stack: ItemStack?, pass: Int): IIcon? {
		return when (pass) {
			0    -> super.getIcon(stack, pass)
			
			1    -> {
				ASJRenderHelper.setGlow()
				overlay
			}
			
			else -> { // without that part armor will glow :(
				ASJRenderHelper.discard()
				
				// crutch because RenderItem#renderIcon has no null check :(
				// and some other places maybe too...
				// why not just use @Nullable ?
				super.getIcon(stack, pass)
			}
		}
	}
	
	override fun getCloakTexture(stack: ItemStack) = LibResourceLocations.cloakFenrir
	
	override fun getCloakGlowTexture(stack: ItemStack) = LibResourceLocations.cloakFenrirGlow
	
	companion object {
		
		init {
			eventForge()
		}
		
		fun getCloak(player: EntityPlayer): ItemStack? {
			val stack = if (AlfheimCore.TravellersGearLoaded) {
				TravellersGearAPI.getExtendedInventory(player)[0]
			} else {
				PlayerHandler.getPlayerBaubles(player)[3]
			}
			
			return if (stack?.item === AlfheimItems.fenrirCloak) stack else null
		}
		
		@SubscribeEvent(priority = EventPriority.LOW)
		fun slowdownFreezing(e: SheerColdHandler.SheerColdTickEvent) {
			if (e.entityLiving !is EntityPlayer || getCloak(e.entityLiving as EntityPlayer) == null) return
			if (e.delta == null) return
			e.delta = min(e.delta!!, e.delta!! / 16f) // minimal so that if other source heats - it won't override
		}
		
		@SubscribeEvent
		fun dodgeAttack(e: LivingAttackEvent) {
			val player = e.entityLiving as? EntityPlayer ?: return
			
			if (e.source.entity == null || e.source.entity == player || getCloak(player) == null || !ASJUtilities.chance(30)) return
			
			e.isCanceled = true
			player.hurtResistantTime = player.maxHurtResistantTime
		}
	}
}
