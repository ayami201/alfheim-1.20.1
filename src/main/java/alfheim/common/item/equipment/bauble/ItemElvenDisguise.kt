package alfheim.common.item.equipment.bauble

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alexsocol.asjlib.render.ASJRenderHelper
import alfheim.api.entity.EnumRace
import alfheim.api.lib.LibResourceLocations
import alfheim.client.core.helper.IconHelper
import alfheim.client.gui.ItemsRemainingRenderHandler
import alfheim.client.model.armor.ModelBelt
import alfheim.common.network.NetworkService
import alfheim.common.network.packet.MessageDisguise
import baubles.api.BaubleType
import baubles.common.lib.PlayerHandler
import cpw.mods.fml.common.network.NetworkRegistry
import cpw.mods.fml.relauncher.*
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.*
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderPlayerEvent
import org.lwjgl.opengl.GL11
import vazkii.botania.api.item.IBaubleRender
import vazkii.botania.api.mana.*
import vazkii.botania.common.item.equipment.bauble.ItemBauble

class ItemElvenDisguise: ItemBauble("DisguiseBelt"), IManaUsingItem, IBaubleRender {
	
	lateinit var iconGem: IIcon
	
	override fun canEquip(stack: ItemStack, player: EntityLivingBase): Boolean {
		if (player !is EntityPlayer) return false
		
		val cost = if (stack.race === EnumRace.ALV) COST_ALV else COST_OTHER
		return ManaItemHandler.requestManaExactForTool(stack, player, cost, false)
	}
	
	override fun onEquipped(stack: ItemStack, player: EntityLivingBase) {
		if (player is EntityPlayerMP) {
			val (x, y, z) = Vector3.fromEntity(player)
			NetworkService.sendToAllAround(MessageDisguise("", stack.booba, stack.raceId, true, player.commandSenderName), NetworkRegistry.TargetPoint(player.dimension, x, y, z, 64.0))
		}
		
		super.onEquipped(stack, player)
	}
	
	override fun onEquippedOrLoadedIntoWorld(stack: ItemStack, player: EntityLivingBase) {
		if (player is EntityPlayerMP) {
			val (x, y, z) = Vector3.fromEntity(player)
			NetworkService.sendToAllAround(MessageDisguise("", stack.booba, stack.raceId, false, player.commandSenderName), NetworkRegistry.TargetPoint(player.dimension, x, y, z, 64.0))
		}
	}
	
	override fun onUnequipped(stack: ItemStack, player: EntityLivingBase) {
		if (player is EntityPlayerMP) {
			val (x, y, z) = Vector3.fromEntity(player)
			NetworkService.sendToAllAround(MessageDisguise(ItemNBTHelper.getString(stack, TAG_SKIN, "~"), stack.booba, -1, false, player.commandSenderName), NetworkRegistry.TargetPoint(player.dimension, x, y, z, 64.0))
		}
	}
	
	override fun onWornTick(stack: ItemStack, player: EntityLivingBase) {
		super.onWornTick(stack, player)
		if (player !is EntityPlayer || !ASJUtilities.isServer) return
		
		val cost = if (stack.race === EnumRace.ALV) COST_ALV else COST_OTHER
		if (ManaItemHandler.requestManaExactForTool(stack, player, cost, true)) return
		
		onUnequipped(stack, player)
		PlayerHandler.getPlayerBaubles(player)[3] = null
		
		if (!player.inventory.addItemStackToInventory(stack))
			player.dropPlayerItemWithRandomChoice(stack, false)
	}
	
	override fun onItemRightClick(stack: ItemStack, world: World?, player: EntityPlayer): ItemStack {
		if (player.isSneaking) {
			stack.booba = !stack.booba
			ItemsRemainingRenderHandler.set(stack, StatCollector.translateToLocal("gender.${!stack.booba}"))
		} else {
			stack.raceId = (stack.raceId + 1) % EnumRace.entries.size
			ItemsRemainingRenderHandler.set(stack, StatCollector.translateToLocal("race.${stack.race.name}.name"))
		}
		
		return stack
	}
	
	override fun registerIcons(reg: IIconRegister) {
		itemIcon = IconHelper.forItem(reg, this)
		iconGem = IconHelper.forItem(reg, this, "Gem")
	}
	
	override fun getIcon(stack: ItemStack, pass: Int) = if (pass > 0) iconGem else itemIcon!!
	
	override fun requiresMultipleRenderPasses() = true
	
	override fun getRenderPasses(metadata: Int) = 2
	
	override fun getColorFromItemStack(stack: ItemStack, pass: Int) = if (pass > 0) stack.race.rgbColor else 0xFFFFFF
	
	override fun getBaubleType(stack: ItemStack?) = BaubleType.BELT
	
	override fun usesMana(stack: ItemStack) = true
	
	@SideOnly(Side.CLIENT)
	override fun onPlayerBaubleRender(stack: ItemStack, event: RenderPlayerEvent, type: IBaubleRender.RenderType) {
		if (type != IBaubleRender.RenderType.BODY) return
		
		IBaubleRender.Helper.rotateIfSneaking(event.entityPlayer)
		
		if (!event.entityPlayer.isSneaking)
			GL11.glTranslatef(0F, 0.2F, 0F)
		val s = 1.05F / 16F
		glScalef(s)
		
		Minecraft.getMinecraft().renderEngine.bindTexture(LibResourceLocations.disguiseBelt)
		ModelBelt.bipedBody.render(1F)
		
		Minecraft.getMinecraft().renderEngine.bindTexture(LibResourceLocations.disguiseBeltGem)
		stack.race.glColor(1.0)
		ModelBelt.bipedBody.render(1F)
		ASJRenderHelper.glColor1u(-1)
	}
	
	companion object {
		
		const val COST_ALV = 1000
		const val COST_OTHER = 10
		const val TAG_BOOBA = "booba"
		const val TAG_RACE = "race"
		const val TAG_SKIN = "skin"
		
		private var ItemStack.booba: Boolean
			get() = ItemNBTHelper.getBoolean(this, TAG_BOOBA, false)
			set(value) = ItemNBTHelper.setBoolean(this, TAG_BOOBA, value)
		
		private var ItemStack.race: EnumRace
			get() = EnumRace[raceId]
			set(value) {
				raceId = value.ordinal
			}
		
		private var ItemStack.raceId
			get() = ItemNBTHelper.getInt(this, TAG_RACE, EnumRace.HUMAN.ordinal)
			set(value) = ItemNBTHelper.setInt(this, TAG_RACE, value)
		
		fun getDisguise(player: EntityPlayer): EnumRace? = PlayerHandler.getPlayerBaubles(player)[3]?.race
		
		fun getGurl(player: EntityPlayer): Boolean? = PlayerHandler.getPlayerBaubles(player)[3]?.booba
	}
}
