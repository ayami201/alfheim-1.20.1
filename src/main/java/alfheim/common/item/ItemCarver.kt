package alfheim.common.item

import alexsocol.asjlib.*
import alfheim.api.event.PlayerInteractAdequateEvent
import alfheim.client.gui.ItemsRemainingRenderHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.StatCollector
import net.minecraft.world.World

class ItemCarver: ItemMod("Carver") {
	
	init {
		setFull3D()
		setMaxStackSize(1)
		
		eventForge()
	}
	
	override fun addInformation(stack: ItemStack, player: EntityPlayer?, list: MutableList<Any?>, adv: Boolean) {
		addStringToTooltip(list, "${getUnlocalizedNameInefficiently(stack)}.mode.${stack.carverMode}")
	}
	
	override fun doesSneakBypassUse(world: World?, x: Int, y: Int, z: Int, player: EntityPlayer?) = true
	
	@SubscribeEvent
	fun onItemRightClick(e: PlayerInteractAdequateEvent.RightClick) {
		if (e.action != PlayerInteractAdequateEvent.RightClick.Action.RIGHT_CLICK_AIR) return
		
		val stack = e.player.heldItem ?: return
		if (stack.item !== this) return
		
		stack.carverMode = CarverMode.entries[(stack.carverMode.ordinal + 1) % CarverMode.entries.size]
		
		ItemsRemainingRenderHandler.set(stack, StatCollector.translateToLocal("${getUnlocalizedNameInefficiently(stack)}.mode.${stack.carverMode}"))
	}
	
	companion object {
		
		const val TAG_MODE = "mode"
		
		var ItemStack.carverMode: CarverMode
			get() = CarverMode.entries[ItemNBTHelper.getInt(this, TAG_MODE, 0)]
			set(value) = ItemNBTHelper.setInt(this, TAG_MODE, value.ordinal)
		
		enum class CarverMode {
			BIT, LINE, PLANE, ROTATE
		}
	}
}
