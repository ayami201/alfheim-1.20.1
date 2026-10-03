package alfheim.common.item

// PORT: импорты 1.20.1 (MAPPING.md); луч — alfheim.port.legacy.Hit1710; цвет с амулета (ColorOverrideHelper) — КТ-4
import alexsocol.asjlib.*
import alexsocol.asjlib.math.Vector3
import alfheim.client.render.world.VisualEffectHandlerClient
import alfheim.common.core.handler.VisualEffectHandler
import alfheim.port.legacy.*
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level as World
import vazkii.botania.api.mana.ManaItemHandler
import java.awt.Color
//import alfheim.api.item.ColorOverrideHelper

class ItemChalk: ItemMod("Chalk") {
	
	init {
		maxStackSize = 1
	}
	
	// PORT: onItemRightClick → use; setItemInUse → startUsingItem; getMaxItemUseDuration → getUseDuration; onUsingTick →
	// onUseTick (держит любое существо — мел у автора только у игрока)
	override fun use(world: World, player: EntityPlayer, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = player.getItemInHand(hand)
		player.startUsingItem(hand)
		return InteractionResultHolder.consume(stack)
	}
//	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack {
//		player.setItemInUse(stack, getMaxItemUseDuration(stack))
//		return stack
//	}
	
	override fun getUseDuration(stack: ItemStack) = 72000
	
	override fun onUseTick(world: World, entity: LivingEntity, stack: ItemStack, count: Int) {
		val player = entity as? EntityPlayer ?: return
		if (ASJUtilities.isClient) return
		
		if (!ManaItemHandler.instance().requestManaExact(stack, player, 1, true)) return
		
		val distance = 3.0
		val mop = ASJUtilities.getMouseOver(player, distance.D, true)
		
		val hit = if (mop?.hitVec == null)
			Vector3(player.lookAngle).normalize().mul(distance.D).add(player.x, player.y + player.eyeHeight, player.z)
//			Vector3(player.lookVec).normalize().mul(distance.D).add(player.posX, player.posY + player.eyeHeight, player.posZ)
		else {
			val v = Vector3(mop.hitVec)
			when (mop.sideHit) {
				0 -> v.sub(0, 0.00390625, 0)
				2 -> v.sub(0, 0, 0.00390625)
				4 -> v.sub(0.00390625, 0, 0)
			}
			v
		}
		
		val (x, y, z) = hit
		// PORT: КТ-4 — цвет с амулета жреца (ColorOverrideHelper, аксессуары Curios); до неё — цвет автора без амулета
		val (r, g, b) = Color(0xFFD400).getRGBColorComponents(null)
//		val (r, g, b) = Color(ColorOverrideHelper.getColor(player, 0xFFD400)).getRGBColorComponents(null)
		
		VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.CHALK, player.level().dimension(), x, y, z, r.D, g.D, b.D)
//		VisualEffectHandler.sendPacket(VisualEffectHandlerClient.VisualEffects.CHALK, player.worldObj.provider.dimensionId, x, y, z, r.D, g.D, b.D)
	}
}
