package alfheim.common.item.rod

import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.api.item.ColorOverrideHelper
import alfheim.client.core.helper.InterpolatedIconHelper
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.VisualEffectHandler
import alfheim.common.core.handler.ragnarok.RagnarokHandler
import alfheim.common.item.ItemMod
import alfheim.common.item.equipment.bauble.ItemPriestEmblem
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.*
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.*
import net.minecraft.util.DamageSource
import net.minecraft.world.World
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge
import vazkii.botania.api.item.IManaProficiencyArmor
import vazkii.botania.api.mana.*
import java.awt.Color
import kotlin.math.*

/**
 * @author WireSegal
 * Created at 9:32 PM on 1/27/16.
 */
class ItemRodFlameStar(name: String = "rodFlameStar"): ItemMod(name), IManaUsingItem {
	
	init {
		maxStackSize = 1
		if (ASJUtilities.isClient)
			MinecraftForge.EVENT_BUS.register(this)
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) = Unit
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun loadTextures(event: TextureStitchEvent.Pre) {
		if (event.map.textureType == 1) {
			itemIcon = InterpolatedIconHelper.forItem(event.map, this)
		}
	}
	
	override fun getItemUseAction(par1ItemStack: ItemStack?) = EnumAction.bow
	
	override fun getMaxItemUseDuration(par1ItemStack: ItemStack?) = 72000
	
	override fun isFull3D() = true
	
	override fun usesMana(stack: ItemStack) = true
	
	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
		player.setItemInUse(stack, getMaxItemUseDuration(stack))
		return stack
	}
	
	override fun onUsingTick(stack: ItemStack, player: EntityPlayer, count: Int) {
		val world = player.worldObj
		
		val priest = (!RagnarokHandler.blockedPowers[3] && ItemPriestEmblem.getEmblem(3, player) != null)
		val prowess = IManaProficiencyArmor.Helper.hasProficiency(player)
		
		val cost = getCost(prowess, priest)
		if (!ManaItemHandler.requestManaExactForTool(stack, player, cost, false)) return
		
		var maxDistance = 8.0
		if (priest) maxDistance += 6.0
		if (prowess) maxDistance += 2.0
		
		val distance = max(min(maxDistance, (getMaxItemUseDuration(stack) - count) * 0.2), 0.5)
		
		val mop = ASJUtilities.getMouseOver(player, distance.D, true)
		
		val hit = if (mop?.hitVec == null)
			Vector3(player.lookVec).normalize().mul(distance.D).add(player.posX, player.posY + player.eyeHeight, player.posZ)
		else {
			val v = Vector3(mop.hitVec)
			when (mop.sideHit) {
				1 -> v.add(0, 0.125, 0)
				2 -> v.sub(0, 0, 0.00390625)
				4 -> v.sub(0.00390625, 0, 0)
			}
			v
		}
		
		val (x, y, z) = hit
		
		val color = Color(ColorOverrideHelper.getColor(player, 0xF94407))
		val (r, g, b) = color.getRGBColorComponents(null)
		
		VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.SPARKLE, world.provider.dimensionId, x, y, z, r.D, g.D, b.D, 1.0)
		
		val power = getDamage(prowess, priest)
//		if (count % 20 != 0) return
		getEntitiesWithinAABB(world, EntityLivingBase::class.java, getBoundingBox(x, y, z).expand(0.5)).forEach { entity ->
			if (entity == player) return@forEach
			entity.knockback(player, 0.5f)
			if (entity.health <= 0) return@forEach
			if (!entity.attackEntityFrom(DamageSource.causePlayerDamage(player).setFireDamage(), power.F)) return@forEach
			ManaItemHandler.requestManaExactForTool(stack, player, cost, true)
			entity.setFire(power * 20)
		}
	}
	
	val COST = 10
	val PROWESS_COST = -2
	val PRIEST_COST = 3
	
	val DAMAGE = 2
	val PROWESS_DAMAGE = 1
	val PRIEST_DAMAGE = 5
	
	fun getCost(prowess: Boolean, priest: Boolean): Int {
		var d = COST
		if (prowess) d += PROWESS_COST
		if (priest) d += PRIEST_COST
		return d
	}
	
	fun getDamage(prowess: Boolean, priest: Boolean): Int {
		var d = DAMAGE
		if (prowess) d += PROWESS_DAMAGE
		if (priest) d += PRIEST_DAMAGE
		return d
	}
}
