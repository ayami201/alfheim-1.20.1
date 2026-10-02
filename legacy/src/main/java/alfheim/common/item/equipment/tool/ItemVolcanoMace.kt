package alfheim.common.item.equipment.tool

import alexsocol.asjlib.*
import alfheim.client.core.helper.IconHelper
import alfheim.common.core.handler.AlfheimConfigHandler
import alfheim.common.core.util.AlfheimTab
import alfheim.common.item.AlfheimItems
import alfheim.common.item.material.ElvenResourcesMetas
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.*
import net.minecraft.item.ItemStack
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.potion.Potion
import net.minecraftforge.common.util.EnumHelper
import net.minecraftforge.event.entity.living.*
import vazkii.botania.common.item.equipment.tool.ToolCommons
import vazkii.botania.common.item.equipment.tool.manasteel.ItemManasteelSword
import kotlin.math.abs
import kotlin.math.max

class ItemVolcanoMace: ItemManasteelSword(volcano, "VolcanoMace") {
	
	init {
		creativeTab = AlfheimTab
		
		eventForge()
	}
	
	@SideOnly(Side.CLIENT)
	override fun registerIcons(reg: IIconRegister) {
		itemIcon = IconHelper.forItem(reg, this)
	}
	
	override fun hitEntity(stack: ItemStack, target: EntityLivingBase, attacker: EntityLivingBase): Boolean {
		target.setFire(10)
		return super.hitEntity(stack, target, attacker)
	}
	
	override fun getIsRepairable(par1ItemStack: ItemStack?, stack: ItemStack) =
		stack.item === AlfheimItems.elvenResource && stack.meta == ElvenResourcesMetas.MuspelheimEssence.I
	
	@SubscribeEvent
	fun increaseDamage(e: LivingHurtEvent) {
		if (e.source.damageType != "player") return
		
		val attacker = e.source.entity as? EntityLivingBase ?: return
		if (attacker.onGround || (attacker is EntityPlayer && attacker.capabilities.isFlying)) return
		
		val stack = attacker.heldItem ?: return
		if (stack.item !== this) return
		
		val crit = attacker.fallDistance > 0f && !attacker.onGround && !attacker.isOnLadder && !attacker.isInWater && !attacker.isPotionActive(Potion.blindness) && attacker.ridingEntity == null
		e.ammount += attacker.fallDistance * AlfheimConfigHandler.maceModifier * if (crit) 1.5f else 1f
		attacker.fallDistance = 0f
		attacker.motionY = max(0.8, abs(attacker.motionY))
		if (attacker is EntityPlayerMP)
			attacker.playerNetServerHandler.sendPacket(S12PacketEntityVelocity(attacker))
	}
	
	@SubscribeEvent
	fun increaseDamage(e: LivingFallEvent) {
		val player = e.entityLiving as? EntityPlayer ?: return
		val stack = player.itemInUse ?: return
		if (stack.item !== this) return
		
		ToolCommons.damageItem(stack, 1, player, manaPerDamage)
		e.isCanceled = true
	}
	
	companion object {
		val volcano = EnumHelper.addToolMaterial("Volcano", 0, 1200, 6f, 4f, 6)!!
	}
}