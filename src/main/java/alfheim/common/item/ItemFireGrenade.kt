package alfheim.common.item

// PORT: импорты 1.20.1 (MAPPING.md). Иконка с плавной анимацией (InterpolatedIconHelper, TextureStitchEvent) — в
// 1.20.1 модель предмета (генерация данных) и "interpolate" в .mcmeta текстуры, которое 1.20.1 понимает само
import alexsocol.asjlib.*
import alfheim.common.entity.EntityThrowableItem
import alfheim.port.legacy.*
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level as World
//import alfheim.client.core.helper.InterpolatedIconHelper

class ItemFireGrenade: ItemMod("fireGrenade") {
	
//	init {
//		if (ASJUtilities.isClient)
//			MinecraftForge.EVENT_BUS.register(this)
//	}
	
	// PORT: onItemRightClick → use
	override fun use(world: World, player: EntityPlayer, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = player.getItemInHand(hand)
		if (!world.isRemote) {
			EntityThrowableItem(player).spawn()
			stack.shrink(1)
//			stack.stackSize--
		}
		
		return InteractionResultHolder.consume(stack)
//		return stack
	}
	
//	override fun registerIcons(reg: IIconRegister) = Unit // NO-OP
//
//	@SubscribeEvent
//	@SideOnly(Side.CLIENT)
//	fun loadTextures(event: TextureStitchEvent.Pre) {
//		if (event.map.textureType == 1) {
//			itemIcon = InterpolatedIconHelper.forItem(event.map, this)
//		}
//	}
}